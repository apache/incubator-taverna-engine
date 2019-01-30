/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.provenance.lineageservice;

import static java.util.Collections.synchronizedList;
import static org.apache.taverna.provenance.lineageservice.utils.ProvenanceProcessor.DATAFLOW_ACTIVITY;
import static org.apache.taverna.provenance.lineageservice.utils.ProvenanceUtils.getDataItemAsXML;
import static org.apache.taverna.provenance.lineageservice.utils.ProvenanceUtils.iterationToString;
import static org.apache.taverna.provenance.lineageservice.utils.ProvenanceUtils.parentProcess;
import static net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary.ACTIVITY_EVENT_TYPE;
import static net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary.END_WORKFLOW_EVENT_TYPE;
import static net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary.INVOCATION_STARTED_EVENT_TYPE;
import static net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary.ITERATION_EVENT_TYPE;
import static net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary.PROCESSOR_EVENT_TYPE;
import static net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary.PROCESS_EVENT_TYPE;
import static net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary.WORKFLOW_DATA_EVENT_TYPE;

import java.beans.ExceptionListener;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.rowset.serial.SerialBlob;

import net.sf.taverna.t2.provenance.item.DataProvenanceItem;
import net.sf.taverna.t2.provenance.item.DataflowRunComplete;
import net.sf.taverna.t2.provenance.item.InputDataProvenanceItem;
import net.sf.taverna.t2.provenance.item.InvocationStartedProvenanceItem;
import net.sf.taverna.t2.provenance.item.IterationProvenanceItem;
import net.sf.taverna.t2.provenance.item.OutputDataProvenanceItem;
import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.item.WorkflowProvenanceItem;
import org.apache.taverna.provenance.lineageservice.utils.DataBinding;
import org.apache.taverna.provenance.lineageservice.utils.DataLink;
import org.apache.taverna.provenance.lineageservice.utils.NestedListNode;
import org.apache.taverna.provenance.lineageservice.utils.Port;
import org.apache.taverna.provenance.lineageservice.utils.PortBinding;
import org.apache.taverna.provenance.lineageservice.utils.ProcessorBinding;
import org.apache.taverna.provenance.lineageservice.utils.ProcessorEnactment;
import org.apache.taverna.provenance.lineageservice.utils.ProvenanceProcessor;
import org.apache.taverna.provenance.lineageservice.utils.ProvenanceUtils;
import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.InputPort;
import org.apache.taverna.workflowmodel.MergeInputPort;
import org.apache.taverna.workflowmodel.MergeOutputPort;
import org.apache.taverna.workflowmodel.OutputPort;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.ProcessorInputPort;
import org.apache.taverna.workflowmodel.ProcessorOutputPort;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.activity.NestedDataflow;


















//import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;

/**
 * @author Paolo Missier
 */
public class EventProcessor {
	/**
	 * A map of UUIDs of the originating processor to the ProcBinding object
	 * that contains its parameters
	 */
	private Map<String, ProcessorBinding> procBindingMap = new ConcurrentHashMap<>();

	/** A map of child ids to their parents in the hierarchy of events:
	 *  workflow -> process -> processor -> activity -> iteration
	 */
	private Map<String, String> parentChildMap= new ConcurrentHashMap<>();

	private static Logger logger = Logger.getLogger(EventProcessor.class);

	private static final String OUTPUT_CONTAINER_PROCESSOR = "_OUTPUT_";
	private static final String INPUT_CONTAINER_PROCESSOR = "_INPUT_";

	private volatile boolean workflowStructureDone = false; // used to inhibit processing of multiple workflow events -- we only need the first
	private volatile String workflowRunId = null; // unique run ID. set when we see the first event of type "process"

	String topLevelDataflowName = null;
	String topLevelDataflowID   = null;

	Map<String, String> wfNestingMap = new ConcurrentHashMap<>();

	// all input bindings are accumulated here so they can be "backpatched" (see backpatching() )
	List<PortBinding> allInputVarBindings = synchronizedList(new ArrayList<PortBinding>());

	// dedicated class for processing WorkflowData events which carry workflow output info
	private WorkflowDataProcessor  wfdp;
	private ProvenanceWriter pw = null;
	private ProvenanceQuery  pq = null;

	private HashMap<String, Port> mapping;

	private Map<String, ProcessorEnactment> processorEnactmentMap = new ConcurrentHashMap<>();

	private Map<String, ProvenanceProcessor> processorMapById = new ConcurrentHashMap<>();

	private WorkflowBundleIO io;

	// Backpatching temporarily disabled
	private static final boolean backpatching = false;

	public EventProcessor(WorkflowBundleIO io) {
		this.io = io;
	}

	/**
	 * @param pw
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 *
	 */
	public EventProcessor(ProvenanceWriter pw, ProvenanceQuery pq,
			WorkflowDataProcessor wfdp,WorkflowBundleIO io) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		this.pw = pw;
		this.pq = pq;
		this.wfdp = wfdp;
		this.io = io;

		//logger.setLevel((Level) Level.INFO);
	}

	/**
	 * this is the new version that makes use of the T2 deserializer
	 * populate static portion of the DB<br/>
	 * the static structure may already be in the DB -- this is detected as a duplicate top-level workflow ID.
	 * In this case, we skip this processing altogether
	 * @param content
	 *            is a serialized dataflow (XML) -- this is parsed using the T2
	 *            Deserializer
	 * @return the workflowRunId for this workflow structure
	 */
	public String processWorkflowStructure(ProvenanceItem provenanceItem) {
		/*
		 * this flag is set to prevent processing of separate
		 * workflowProvenanceItems that describe nested workflows. the
		 * processing of all nested workflows is done as part of the very first
		 * workflowProvenanceItem that we receive, which is self-consistent. so
		 * we ignore all others
		 */
		if (workflowStructureDone)
			return null;
		WorkflowProvenanceItem wpi = (WorkflowProvenanceItem) provenanceItem;
		setWorkflowRunId(wpi.getIdentifier());
		workflowStructureDone = true;
		return processWorkflowStructure(wpi.getDataflow());
	}

	public String processWorkflowStructure(Dataflow df) {
		topLevelDataflowName = df.getLocalName();
		topLevelDataflowID   = df.getIdentifier();

		// check whether we already have this WF in the DB
		List<String> workflowIds = null;
		try {
			workflowIds = pq.getAllworkflowIds();
		} catch (SQLException e) {
			logger.warn("Problem processing workflow structure", e);
		}

		if (workflowIds == null || workflowIds.contains(topLevelDataflowID)) {
			// not already in the DB
			logger.info("new workflow structure with ID " + topLevelDataflowID);
			ProvenanceProcessor provProc = new ProvenanceProcessor();
			provProc.setIdentifier(UUID.randomUUID().toString());
			provProc.setProcessorName(topLevelDataflowName);
			provProc.setFirstActivityClassName(DATAFLOW_ACTIVITY);
			provProc.setWorkflowId(topLevelDataflowID);
			provProc.setTopLevelProcessor(true);
			// record the top level dataflow as a processor in the DB
			try {
				pw.addProcessor(provProc);
				// pw.addProcessor(topLevelDataflowName, DATAFLOW_PROCESSOR_TYPE, topLevelDataflowID, true);  // true -> is top level
			} catch (SQLException e) {
				logger.warn("Can't add processor " + topLevelDataflowID, e);
			}
		}

		return processDataflowStructure(df, topLevelDataflowID, df.getLocalName());  // null: no external name given to top level dataflow
	}

	private Blob serialize(Dataflow df) {
		Element serializeDataflow = null;xmlSerializer.serializeDataflow(df);//FIXME
		String dataflowString = null;
		try {
		    XMLOutputter outputter = new XMLOutputter();
		    StringWriter stringWriter = new StringWriter();
		    outputter.output(serializeDataflow, stringWriter);
		    dataflowString = stringWriter.toString();
		} catch (java.io.IOException e) {
		    logger.error("Could not serialise dataflow", e);
		    // FIXME Bad Exception handling!
		}
		return new SerialBlob(dataflowString.getBytes("UTF-8"));
	}

	/**
	 * note: this method can be called as part of a recursion on sub-workflows
	 *
	 * @param df
	 * @param dataflowID
	 *            the UUID for the entire dataflow (may be a sub-dataflow)
	 * @param localName
	 *            the external name of the dataflow. Null if this is top level,
	 *            not null if a sub-dataflow
	 * @return the workflowRunId for this workflow structure
	 */
	private String processDataflowStructure(Dataflow df, String dataflowID, String externalName) {
		String localWorkflowRunID = getWorkflowRunId();

		//dataflowDepth++;

		try {
			// check whether we already have this WF in the DB
			boolean alreadyInDb;
			try {
				List<String> workflowIds = pq.getAllworkflowIds();
				alreadyInDb = workflowIds != null && workflowIds.contains(dataflowID);
			} catch (SQLException e) {
				logger.warn("Problem processing dataflow structure for " + dataflowID, e);
				alreadyInDb = false;
			}

			// add workflow ID -- this is NOT THE SAME AS the workflowRunId

			/*
			 * this could be a nested workflow -- in this case, override its
			 * workflowRunId with that of its parent
			 */
			if (!alreadyInDb) {
				String parentDataflow = wfNestingMap.get(dataflowID);
				Blob blob = serialize(df);
				if (parentDataflow == null) {
					// this is a top level dataflow description
					pw.addWFId(dataflowID, null, externalName, blob); // set its dataflowID with no parent

				} else {
					// we are processing a nested workflow structure
					logger.debug("dataflow "+dataflowID+" with external name "+externalName+" is nested within "+parentDataflow);

					pw.addWFId(dataflowID, parentDataflow, externalName, blob); // set its dataflowID along with its parent

					// override workflowRunId to point to top level -- UNCOMMENTED PM 9/09  CHECK
					localWorkflowRunID = pq.getRuns(parentDataflow, null).get(0).getWorkflowRunId();
				}
			}
			// Log the run itself
			pw.addWorkflowRun(dataflowID, localWorkflowRunID);

			// add processors along with their variables
			List<Port> vars = new ArrayList<Port>();
			for (Processor p : df.getProcessors()) {
				String pName = p.getLocalName();

				//CHECK get type of first activity and set this as the type of the processor itself
				List<? extends Activity<?>> activities = p.getActivityList();

				if (! alreadyInDb) {
					ProvenanceProcessor provProc;
					String pType = null;
					if (activities != null && !activities.isEmpty())
						pType = activities.get(0).getClass().getCanonicalName();
					provProc = new ProvenanceProcessor();
					provProc.setIdentifier(UUID.randomUUID().toString());
					provProc.setProcessorName(pName);
					provProc.setFirstActivityClassName(pType);
					provProc.setWorkflowId(dataflowID);
					provProc.setTopLevelProcessor(false);

					pw.addProcessor(provProc);

					//pw.addProcessor(pName, pType, dataflowID, false);  // false: not a top level processor

					/*
					 * add all input ports for this processor as input variables
					 */
					for (ProcessorInputPort ip : p.getInputPorts()) {
						Port inputVar = new Port();
						inputVar.setIdentifier(UUID.randomUUID().toString());
						inputVar.setProcessorId(provProc.getIdentifier());
						inputVar.setProcessorName(pName);
						inputVar.setWorkflowId(dataflowID);
						inputVar.setPortName(ip.getName());
						inputVar.setDepth(ip.getDepth());
						inputVar.setInputPort(true);
					 	vars.add(inputVar);
					}

					/*
					 * add all output ports for this processor as output
					 * variables
					 */
					for (ProcessorOutputPort op : p.getOutputPorts()) {
						Port outputVar = new Port();
						outputVar.setIdentifier(UUID.randomUUID().toString());
						outputVar.setProcessorName(pName);
						outputVar.setProcessorId(provProc.getIdentifier());
						outputVar.setWorkflowId(dataflowID);
						outputVar.setPortName(op.getName());
						outputVar.setDepth(op.getDepth());
						outputVar.setInputPort(false);

						vars.add(outputVar);
					}
				}

				/*
				 * check for nested structures: if the activity is
				 * DataflowActivity then this processor is a nested workflow;
				 * make an entry into wfNesting map with its ID and recurse on
				 * the nested workflow
				 */

				if (activities != null)
					for (Activity<?> a : activities) {
						if (!(a instanceof NestedDataflow))
							continue;

						Dataflow nested = ((NestedDataflow) a)
								.getNestedDataflow();
						wfNestingMap.put(nested.getIdentifier(), dataflowID); // child -> parent

						// RECURSIVE CALL
						processDataflowStructure(nested,
								nested.getIdentifier(), p.getLocalName());
					}
			} // end for each processor

			// add inputs to entire dataflow
			String pName = INPUT_CONTAINER_PROCESSOR;  // overridden -- see below

			/*
			 * check whether we are processing a nested workflow. in this case
			 * the input vars are not assigned to the INPUT processor but to the
			 * containing dataflow
			 */
			if (! alreadyInDb) {
				if (externalName != null) // override the default if we are nested or someone external name is provided
					pName = externalName;

				for (DataflowInputPort ip : df.getInputPorts()) {
					Port inputVar = new Port();
					inputVar.setIdentifier(UUID.randomUUID().toString());
					inputVar.setProcessorId(null); // meaning workflow port
					inputVar.setProcessorName(pName);
					inputVar.setWorkflowId(dataflowID);
					inputVar.setPortName(ip.getName());
					inputVar.setDepth(ip.getDepth());
					inputVar.setInputPort(true);  // CHECK PM modified 11/08 -- input vars are actually outputs of input processors...

					vars.add(inputVar);
				}

				// add outputs of entire dataflow
				pName = OUTPUT_CONTAINER_PROCESSOR;  // overridden -- see below

				/*
				 * check whether we are processing a nested workflow. in this
				 * case the output vars are not assigned to the OUTPUT processor
				 * but to the containing dataflow
				 */
				if (externalName != null) // we are nested
					pName = externalName;

				for (DataflowOutputPort op : df.getOutputPorts()) {
					Port outputVar = new Port();
					outputVar.setIdentifier(UUID.randomUUID().toString());
					outputVar.setProcessorId(null); // meaning workflow port
					outputVar.setProcessorName(pName);
					outputVar.setWorkflowId(dataflowID);
					outputVar.setPortName(op.getName());
					outputVar.setDepth(op.getDepth());
					outputVar.setInputPort(false);  // CHECK PM modified 11/08 -- output vars are actually outputs of output processors...
					vars.add(outputVar);
				}

				pw.addPorts(vars, dataflowID);
				makePortMapping(vars);

				/*
				 * add datalink records using the dataflow links retrieving the
				 * processor names requires navigating from links to source/sink
				 * and from there to the processors
				 */
				for (Datalink l : df.getLinks()) {
					// TODO cover the case of datalinks from an input and to an output to the entire dataflow

					Port sourcePort = null;
					Port destinationPort = null;

					OutputPort source = l.getSource();
					if (source instanceof ProcessorOutputPort) {
						String sourcePname = ((ProcessorOutputPort) source)
								.getProcessor().getLocalName();
						sourcePort = lookupPort(sourcePname, source.getName(), false);
					} else if (source instanceof MergeOutputPort) {
						// TODO: Handle merge output ports
					} else
						// Assume it is internal port from DataflowInputPort
						sourcePort = lookupPort(externalName, source.getName(), true);

					InputPort sink = l.getSink();
					if (sink instanceof ProcessorInputPort) {
						String sinkPname = ((ProcessorInputPort) sink)
								.getProcessor().getLocalName();
						destinationPort = lookupPort(sinkPname, sink.getName(), true);
					} else if (sink instanceof MergeInputPort) {
						// TODO: Handle merge input ports
					} else
						// Assume it is internal port from DataflowOutputPort
						destinationPort = lookupPort(externalName, sink.getName(), false);

					if (sourcePort != null && destinationPort != null)
						pw.addDataLink(sourcePort, destinationPort, dataflowID);
					else
						logger.info("Can't record datalink " + l);
				}
			}
		} catch (Exception e) {
			logger.error("Problem processing provenance for dataflow", e);
		}

		return dataflowID;
	}

	private void makePortMapping(List<Port> ports) {
		mapping = new HashMap<>();
		for (Port port: ports) {
			String key = port.getProcessorName()
					+ (port.isInputPort() ? "/i:" : "/o:") + port.getPortName();
			mapping.put(key, port);
		}
	}

	private Port lookupPort(String processorName, String portName, boolean isInputPort) {
		String key = processorName + (isInputPort ? "/i:" : "/o:") + portName;
		return mapping.get(key);
	}

	/**
	 * processes an elementary process execution event from T2. Collects info
	 * from events as they happen and sends them to the writer for processing
	 * when the iteration event is received. Uses the map of procBindings to
	 * process event id and the map of child ids to parent ids to ensure that
	 * the correct proc binding is used
	 * @param currentWorkflowID
	 *
	 * @param d
	 * @param context
	 */
	public void processProcessEvent(ProvenanceItem provenanceItem, String currentWorkflowID) {
		switch (provenanceItem.getEventType()) {
		case PROCESS_EVENT_TYPE: {
			String parentId = provenanceItem.getParentId();  // this is the workflowID
			String identifier = provenanceItem.getIdentifier();  // use this as workflowRunId if this is the top-level process

			parentChildMap.put(identifier, parentId);
			ProcessorBinding pb = new ProcessorBinding();
			pb.setWorkflowRunId(getWorkflowRunId());
			pb.setWorkflowId(currentWorkflowID);
			procBindingMap.put(identifier, pb);
			return;
		}
		case PROCESSOR_EVENT_TYPE: {
			String identifier = provenanceItem.getIdentifier();
			String parentId = provenanceItem.getParentId();
			String processID = provenanceItem.getProcessId(); // this is the external process ID

			// this has the weird form facade0:dataflowname:pname  need to extract pname from here
			String[] processName = processID.split(":");
			procBindingMap.get(parentId).setProcessorName(
					processName[processName.length - 1]);
			// 3rd component of composite name

			parentChildMap.put(identifier, parentId);
			return;
		}
		case ACTIVITY_EVENT_TYPE: {
			String identifier = provenanceItem.getIdentifier();
			String parentId = provenanceItem.getParentId();
			procBindingMap.get(parentChildMap.get(parentId))
					.setFirstActivityClassName(identifier);
			parentChildMap.put(identifier, parentId);
			return;
		}
		case ITERATION_EVENT_TYPE: {
			IterationProvenanceItem iterationProvenanceItem = (IterationProvenanceItem)provenanceItem;
			if (iterationProvenanceItem.getParentIterationItem() != null)
				// Skipping pipelined outputs, we'll process the parent output later instead
				return;

			// traverse up to root to retrieve ProcBinding that was created when we saw the process event
			String activityID = provenanceItem.getParentId();
			String processorID = parentChildMap.get(activityID);
			String processID = parentChildMap.get(processorID);
			String iterationID = provenanceItem.getIdentifier();
			parentChildMap.put(iterationID, activityID);

			ProcessorEnactment processorEnactment = processorEnactmentMap
					.get(iterationID);
			if (processorEnactment == null)
				processorEnactment = new ProcessorEnactment();

			ProcessorBinding procBinding = procBindingMap.get(processID);

			String itVector = extractIterationVector(iterationToString(iterationProvenanceItem
					.getIteration()));
			procBinding.setIterationVector(itVector);

			processorEnactment.setEnactmentStarted(iterationProvenanceItem
					.getEnactmentStarted());
			processorEnactment.setEnactmentEnded(iterationProvenanceItem
					.getEnactmentEnded());
			processorEnactment.setWorkflowRunId(workflowRunId);
			processorEnactment.setIteration(itVector);

			String processId = iterationProvenanceItem.getProcessId();
			String parentProcessId = parentProcess(processId, 3);
			if (parentProcessId != null) {
				ProcessorEnactment parentProcEnact = getWfdp().invocationProcessToProcessEnactment
						.get(parentProcessId);
				if (parentProcEnact != null)
					processorEnactment
							.setParentProcessorEnactmentId(parentProcEnact
									.getProcessEnactmentId());
			}
			processorEnactment.setProcessEnactmentId(iterationProvenanceItem
					.getIdentifier());
			processorEnactment.setProcessIdentifier(processId);

			ProvenanceProcessor provenanceProcessor;
			if (processorEnactment.getProcessorId() == null) {
				provenanceProcessor = pq.getProvenanceProcessorByName(
						currentWorkflowID, procBinding.getProcessorName());
				if (provenanceProcessor == null)
					// already logged warning
					return;
				processorMapById.put(provenanceProcessor.getIdentifier(),
						provenanceProcessor);
				processorEnactment.setProcessorId(provenanceProcessor
						.getIdentifier());
			} else {
				provenanceProcessor = processorMapById.get(processorEnactment
						.getProcessorId());
				if (provenanceProcessor == null) {
					provenanceProcessor = pq
							.getProvenanceProcessorById(processorEnactment
									.getProcessorId());
					processorMapById.put(provenanceProcessor.getIdentifier(),
							provenanceProcessor);
				}
			}

			InputDataProvenanceItem inputDataEl = iterationProvenanceItem.getInputDataItem();
			OutputDataProvenanceItem outputDataEl = iterationProvenanceItem.getOutputDataItem();

			if (inputDataEl != null
					&& processorEnactment.getInitialInputsDataBindingId() == null) {
				processorEnactment
						.setInitialInputsDataBindingId(processDataBindings(
								inputDataEl, provenanceProcessor));
				processInput(inputDataEl, procBinding, currentWorkflowID);
			}

			if (outputDataEl != null
					&& processorEnactment.getFinalOutputsDataBindingId() == null) {
				processorEnactment
						.setFinalOutputsDataBindingId(processDataBindings(
								outputDataEl, provenanceProcessor));
				processOutput(outputDataEl, procBinding, currentWorkflowID);
			}

			try {
				if (processorEnactmentMap.containsKey(iterationID)) {
					getPw().updateProcessorEnactment(processorEnactment);
				} else {
					getPw().addProcessorEnactment(processorEnactment);
					processorEnactmentMap.put(iterationID, processorEnactment);
				}
			} catch (SQLException e) {
				logger.warn("Could not store processor enactment", e);
			}
			return;
		}
		case END_WORKFLOW_EVENT_TYPE: {
			DataflowRunComplete completeEvent = (DataflowRunComplete) provenanceItem;
			// use this event to do housekeeping on the input/output varbindings

			// process the input and output values accumulated by WorkflowDataProcessor
			getWfdp().processTrees(completeEvent, getWorkflowRunId());

			reconcileLocalOutputs(provenanceItem.getWorkflowId());

			if (! provenanceItem.getProcessId().contains(":")) {
				// Top-level workflow finished
				// No longer needed, done by processTrees()
//				patchTopLevelnputs();

				workflowStructureDone = false; // CHECK reset for next run...
//				reconcileTopLevelOutputs(); // Done by reconcileLocalOutputs
				getPw().closeCurrentModel();  // only real impl is for RDF
			}
			return;
		}
		case WORKFLOW_DATA_EVENT_TYPE: {
			// give this event to a WorkflowDataProcessor object for pre-processing
			//			try {
			// TODO may generate an exception when the data is an error CHECK
			getWfdp().addWorkflowDataItem(provenanceItem);
			//			} catch (NumberFormatException e) {
			//			logger.error(e);
			//			}
			//			logger.info("Received workflow data - not processing");
			//FIXME not sure  - needs to be stored somehow
			return;
		}
		case INVOCATION_STARTED_EVENT_TYPE: {
			InvocationStartedProvenanceItem startedItem = (InvocationStartedProvenanceItem) provenanceItem;
			ProcessorEnactment processorEnactment = processorEnactmentMap
					.get(startedItem.getParentId());
			if (processorEnactment == null) {
				logger.error("Could not find ProcessorEnactment for invocation "
						+ startedItem);
				return;
			}
			getWfdp().invocationProcessToProcessEnactment.put(
					startedItem.getInvocationProcessId(), processorEnactment);
			return;
		}
		case ERROR_EVENT_TYPE:
			//TODO process the error
			return;
		default:
			// TODO broken, should we throw something here?
			return;
		}
	}

	private String processDataBindings(
			DataProvenanceItem provenanceItem, ProvenanceProcessor provenanceProcessor) {
		// TODO: Cache known provenaneItems and avoid registering again
		String dataBindingId = UUID.randomUUID().toString();
		boolean isInput = provenanceItem instanceof InputDataProvenanceItem;

		for (Entry<String, T2Reference> entry : provenanceItem.getDataMap().entrySet()) {
			DataBinding dataBinding = new DataBinding();
			dataBinding.setDataBindingId(dataBindingId);
			Port port = findPort(provenanceProcessor, entry.getKey(), isInput); // findPort
			if (port == null) {
				logger.warn("Could not find port for " + entry.getKey());
				continue;
			}
			dataBinding.setPort(port);
			dataBinding.setT2Reference(entry.getValue().toUri().toASCIIString());
			dataBinding.setWorkflowRunId(workflowRunId);
			try {
				getPw().addDataBinding(dataBinding);
			} catch (SQLException e) {
				logger.warn("Could not register data binding for " + port, e);
			}
		}
		return dataBindingId;
	}

	private Port findPort(ProvenanceProcessor provenanceProcessor,
			String portName, boolean isInput) {
		// TODO: Query pr dataflow and cache
		Map<String, String> queryConstraints = new HashMap<>();
		queryConstraints.put("V.workflowId",
				provenanceProcessor.getWorkflowId());
		String processorName = provenanceProcessor.getProcessorName();
		queryConstraints.put("processorName", processorName);
		queryConstraints.put("portName", portName);
		queryConstraints.put("isInputPort", isInput ? "1" : "0");
		try {
			List<Port> vars = pq.getPorts(queryConstraints);
			if (vars.isEmpty()) {
				logger.warn("Can't find port " + portName + " in "
						+ processorName);
			} else if (vars.size() > 1) {
				logger.warn("Multiple matches for port " + portName + " in "
						+ processorName + ", got:" + vars);
			} else
				return vars.get(0);
		} catch (SQLException e) {
			logger.error(
					"Problem getting ports for processor: " + processorName
							+ " worflow: "
							+ provenanceProcessor.getWorkflowId(), e);
		}
		return null;
	}


	/**
	 * fills in the VBs for the global inputs -- this removes the need for explicit events
	 * that account for these value bindings...
	 */
	public void patchTopLevelnputs() {

		// for each input I to topLevelDataflow:
		// pick first outgoing datalink with sink P:X
		// copy value X to I -- this can be a collection, so copy everything

		// get all global input vars

		//		logger.info("\n\n BACKPATCHING GLOBAL INPUTS with dataflowDepth = "+dataflowDepth+"*******\n");

		List<Port> inputs=null;
		try {
			inputs = getPq().getInputPorts(topLevelDataflowName, topLevelDataflowID);

			for (Port input:inputs)  {

				//				logger.info("global input: "+input.getVName());

				Map<String,String> queryConstraints = new HashMap<String,String>();

//				queryConstraints.put("sourcePortName", input.getVName());
//				queryConstraints.put("sourceProcessorName", input.getPName());
				queryConstraints.put("sourcePortId", input.getIdentifier());
				queryConstraints.put("workflowId", input.getWorkflowId());
				List<DataLink> outgoingDataLinks = getPq().getDataLinks(queryConstraints);

				// any datalink will do, use the first
				String targetPname = outgoingDataLinks.get(0).getDestinationProcessorName();
				String targetVname = outgoingDataLinks.get(0).getDestinationPortName();

//				logger.info("copying values from ["+targetPname+":"+targetVname+"] for instance ID: ["+workflowRunId+"]");

				queryConstraints.clear();
				queryConstraints.put("V.portName", targetVname);
				queryConstraints.put("V.processorName", targetPname);
				queryConstraints.put("VB.workflowRunId", getWorkflowRunId());
				queryConstraints.put("V.workflowId", topLevelDataflowID);

				for (PortBinding vb : getPq().getPortBindings(queryConstraints)) {
					PortBinding inputPortBinding = new PortBinding(vb);

					// insert PortBinding back into VB with the global input portName
					inputPortBinding.setProcessorName(input.getProcessorName());
					inputPortBinding.setPortName(input.getPortName());
					try {
						getPw().addPortBinding(inputPortBinding);
					} catch (SQLException ex) {
						logger.info("Already logged port binding", ex);
					}
				}
			}
		} catch (SQLException e) {
			logger.warn("Patch top level inputs problem for provenance", e);
		} catch (IndexOutOfBoundsException e) {
			logger.error("Could not patch top level", e);
		}
	}

	public void reconcileTopLevelOutputs() {
		reconcileLocalOutputs(topLevelDataflowID);
	}

	// PM added 23/4/09
	/**
	 * reconcile the top level outputs with the results from its immediate precedessors in the graph.<br/>
	 * various cases have to be considered: predecessors may include records that are not in the output,
	 * while the output may include nested list structures that are not in the precedessors. This method accounts
	 * for a 2-way reconciliation that considers all possible cases.<br/>
	 * at the end, outputs and their predecessors contain the same data.<p/>
	 * NOTE: if we assume that data values (URIs) are <em>always</em> unique then this is greatly simplified by just
	 * comparing two sets of value records by their URIs and reconciling them. But this is not the way it is done here
	 */
	public void reconcileLocalOutputs(String dataflowID) {
		/*
	for each output O

	for each variable V in predecessors(O)

	fetch all VB records for O into list OValues
	fetch all VB records for V  into list Yalues

	compare OValues and VValues:
	it SHOULD be the case that OValues is a subset of YValues. Under this assumption:

	for each vb in YValues:
	- if there is a matching o in OValues then (vb may be missing collection information)
	    copy o to vb
	  else
	    if vb has no collection info && there is a matching tree node tn  in OTree (use iteration index for the match) then
	       set vb to be in collection tb
	       copy vb to o

     finally copy all Collection records for O in OTree -- catch duplicate errors
		 */

		Map<String, String> queryConstraints = new HashMap<>();

		try {
			// for each output O
			for (Port output:pq.getOutputPorts(topLevelDataflowName, topLevelDataflowID))  {
				// collect all VBs for O
//				String oPName = output.getPName();
//				String oVName = output.getVName();
//				queryConstraints.put("V.portName", oVName);
//				queryConstraints.put("V.processorName", oPName);
//				queryConstraints.put("VB.workflowRunId", workflowRunId);
//				queryConstraints.put("V.workflowId", topLevelDataflowID);

//				List<PortBinding> OValues = pq.getPortBindings(queryConstraints);

				// find all records for the immediate precedessor Y of O
				queryConstraints.clear();
//				queryConstraints.put("destinationPortName", output.getVName());
//				queryConstraints.put("destinationProcessorName", output.getPName());
				queryConstraints.put("destinationPortId", output.getIdentifier());
				queryConstraints.put("workflowId", output.getWorkflowId());
				List<DataLink> incomingDataLinks = pq.getDataLinks(queryConstraints);

				// there can be only one -- but check that there is one!
				if (incomingDataLinks.isEmpty())
					continue;

				String sourcePname = incomingDataLinks.get(0).getSourceProcessorName();
				String sourceVname = incomingDataLinks.get(0).getSourcePortName();

				queryConstraints.clear();
				queryConstraints.put("V.portName", sourceVname);
				queryConstraints.put("V.processorName", sourcePname);
				queryConstraints.put("VB.workflowRunId", getWorkflowRunId());
				queryConstraints.put("V.workflowId", topLevelDataflowID);

				List<PortBinding> YValues = pq.getPortBindings(queryConstraints);

				// for each YValue look for a match in OValues
				// (assume the YValues values are a superset of OValues)!)

				for (PortBinding yValue:YValues) {
					// look for a matching record in PortBinding for output O
					queryConstraints.clear();
					queryConstraints.put("V.portName", output.getPortName());
					queryConstraints.put("V.processorName", output.getProcessorName());
					queryConstraints.put("VB.workflowRunId", getWorkflowRunId());
					queryConstraints.put("V.workflowid", topLevelDataflowID);
					queryConstraints.put("VB.iteration", yValue.getIteration());
					if (yValue.getCollIDRef()!= null) {
						queryConstraints.put("VB.collIDRef", yValue.getCollIDRef());
						queryConstraints.put("VB.positionInColl", Integer.toString(yValue.getPositionInColl()));
					}
					List<PortBinding> matchingOValues = pq.getPortBindings(queryConstraints);

					// result at most size 1
					if (!matchingOValues.isEmpty()) {
						PortBinding oValue = matchingOValues.get(0);

						// copy collection info from oValue to yValue
						yValue.setCollIDRef(oValue.getCollIDRef());
						yValue.setPositionInColl(oValue.getPositionInColl());

						pw.updatePortBinding(yValue);
					} else {
						// copy the yValue to O
						// insert PortBinding back into VB with the global output portName
						yValue.setProcessorName(output.getProcessorName());
						yValue.setPortName(output.getPortName());
						pw.addPortBinding(yValue);
					}

				} // for each yValue in YValues

				// copy all Collection records for O to Y

				// get all collections refs for O
				queryConstraints.clear();
				queryConstraints.put("workflowRunId", getWorkflowRunId());
				queryConstraints.put("processorNameRef", output.getProcessorName());
				queryConstraints.put("portName", output.getPortName());

				List<NestedListNode> oCollections = pq.getNestedListNodes(queryConstraints);

				// insert back as collection refs for Y -- catch duplicates
				for (NestedListNode nln:oCollections) {
					nln.setProcessorName(sourcePname);
					nln.setProcessorName(sourceVname);

					getPw().replaceCollectionRecord(nln, sourcePname, sourceVname);
				}

			} // for each output var

		} catch (SQLException e) {
			logger.warn("Problem reconciling top level outputs", e);
		}

	}

	@SuppressWarnings("unchecked")
	private void processOutput(OutputDataProvenanceItem provenanceItem,
			ProcessorBinding procBinding, String currentWorkflowID) {
		Element dataItemAsXML = getDataItemAsXML(provenanceItem);
		List<Element> outputPorts = dataItemAsXML.getChildren("port");
		for (Element outputport : outputPorts) {
			String portName = outputport.getAttributeValue("name");

			// value type may vary
			List<Element> valueElements = outputport.getChildren();
			if (valueElements != null && !valueElements.isEmpty()) {
				Element valueEl = valueElements.get(0); // only really 1 child

				processPortBinding(valueEl, procBinding.getProcessorName(),
						portName, procBinding.getIterationVector(),
						getWorkflowRunId(), currentWorkflowID);
			}
		}
	}

	/**
	 * this method reconciles values in varBindings across an datalink: Firstly,
	 * if vb's value is within a collection, _and_ it is copied from a value
	 * generated during a previous iteration, then this method propagates the
	 * list reference to that iteration value, which wouldn't have it.
	 * Conversely, if vb is going to be input to an iteration, then it's lost
	 * its containing list node, and we put it back in by looking at the
	 * corresponding predecessor
	 *
	 * @param vb
	 * @throws SQLException
	 */
	private void backpatchIterationResults(List<PortBinding> newBindings) throws SQLException {
		logger.debug("backpatchIterationResults: start");
		for (PortBinding vb : newBindings) {
			logger.debug("backpatchIterationResults: processing vb "
					+ vb.getProcessorName() + "/" + vb.getPortName() + "="
					+ vb.getValue());

			if (vb.getCollIDRef()!= null) // this is a member of a collection
				logger.debug("...which is inside a collection ");

			// look for its antecedent
			Map<String,String> queryConstraints = new HashMap<>();
			queryConstraints.put("destinationPortName", vb.getPortName());
			queryConstraints.put("destinationProcessorName", vb.getProcessorName());
			queryConstraints.put("workflowId", pq.getWorkflowIdsForRun(vb.getWorkflowRunId()).get(0));  // CHECK picking first element in list...
			List<DataLink> incomingDataLinks = pq.getDataLinks(queryConstraints);

			// there can be only one -- but check that there is one!
			if (incomingDataLinks.isEmpty())
				return;

			String sourcePname = incomingDataLinks.get(0).getSourceProcessorName();
			String sourceVname = incomingDataLinks.get(0).getSourcePortName();

			logger.debug("antecedent: "+sourcePname+":"+sourceVname);

			// get the varbindings for this port and select the one with the same iteration vector as its successor
			queryConstraints.clear();
			queryConstraints.put("VB.portName", sourceVname);
			queryConstraints.put("V.processorName", sourcePname);
			queryConstraints.put("VB.value", vb.getValue());
			queryConstraints.put("VB.workflowRunId", vb.getWorkflowRunId());

			// reconcile
			for (PortBinding b : pq.getPortBindings(queryConstraints)) {
				logger.debug("backpatching " + sourceVname + " " + sourcePname);

				if (vb.getCollIDRef() != null && b.getCollIDRef() == null) {
					logger.debug("successor " + vb.getPortName()
							+ " is in collection " + vb.getCollIDRef()
							+ " but pred " + b.getPortName() + " is not");
					logger.debug("putting " + b.getPortName()
							+ " in collection " + vb.getCollIDRef()
							+ " at pos " + vb.getPositionInColl());
					b.setCollIDRef(vb.getCollIDRef());
					b.setPositionInColl(vb.getPositionInColl());
					getPw().updatePortBinding(b);

				} else if (vb.getCollIDRef() == null && b.getCollIDRef() != null) {
					logger.debug("successor " + vb.getPortName()
							+ " is NOT in collection but pred "
							+ b.getPortName() + " IS");
					logger.debug("putting " + vb.getPortName()
							+ " in collection " + b.getCollIDRef() + " at pos "
							+ b.getPositionInColl());
					vb.setCollIDRef(b.getCollIDRef());
					vb.setPositionInColl(b.getPositionInColl());
					getPw().updatePortBinding(vb);
				}
			}
		}
	}


	/**
	 * create one new PortBinding record for each input port binding
	 * @param currentWorkflowID
	 */
	@SuppressWarnings("unchecked")
	private void processInput(InputDataProvenanceItem provenanceItem,
			ProcessorBinding procBinding, String currentWorkflowID) {
		Element dataItemAsXML = getDataItemAsXML(provenanceItem);
		int order = 0;
		for (Element inputport : (List<Element>) dataItemAsXML.getChildren("port")) {
			String portName = inputport.getAttributeValue("name");

			try {
				// add process order sequence to Port for this portName
				Map<String, String> queryConstraints = new HashMap<>();
				queryConstraints.put("V.workflowId", currentWorkflowID);
				queryConstraints.put("processorName", procBinding.getProcessorName());
				queryConstraints.put("portName", portName);
				queryConstraints.put("isInputPort", "1");

				Port v = getPq().getPorts(queryConstraints).get(0);
				v.setIterationStrategyOrder(order++);
				getPw().updatePort(v);
			} catch (IndexOutOfBoundsException e) {
				logger.error("Could not process input " + portName, e);
			} catch (SQLException e1) {
				logger.error("Could not process input " + portName, e1);
			}

			// value type may vary
			List<Element> valueElements = inputport.getChildren(); // hopefully
			// in the right order...
			if (valueElements != null && valueElements.size() > 0) {
				Element valueEl = valueElements.get(0); // expect only 1 child
				//				processVarBinding(valueEl, processor, portName, iterationVector,
				//				dataflow);

				List<PortBinding> newBindings = processPortBinding(valueEl,
						procBinding.getProcessorName(), portName,
						procBinding.getIterationVector(), getWorkflowRunId(),
						currentWorkflowID);
				// this is a list whenever valueEl is of type list: in this case processVarBinding recursively
				// processes all values within the collection, and generates one PortBinding record for each of them

				allInputVarBindings.addAll(newBindings);

				//				// if the new binding involves list values, then check to see if they need to be propagated back to
//				// results of iterations

				// Backpatching disabled as it is very inefficient and not needed
				// for current Taverna usage

				try {
					if (backpatching)
						backpatchIterationResults(newBindings);
				} catch (SQLException e) {
					logger.warn("Problem with back patching iteration results", e);
				}
			} else {
				if (valueElements != null)
					logger.debug("port name " + portName + "  "
							+ valueElements.size());
				else
					logger.debug("valueElements is null for port name "
							+ portName);
			}
		}
	}

	/**
	 * capture the default case where the value is not a list
	 *
	 * @param valueEl
	 * @param processorId
	 * @param portName
	 * @param iterationId
	 * @param workflowRunId
	 * @param currentWorkflowID
	 */
	private List<PortBinding> processPortBinding(Element valueEl,
			String processorId, String portName, String iterationId,
			String workflowRunId, String currentWorkflowID) {
		// uses the defaults:
		// collIdRef = null
		// parentcollectionRef = null
		// positionInCollection = 1
		return processPortBinding(valueEl, processorId, portName, null, 1, null,
				iterationId, workflowRunId, null, currentWorkflowID);
	}

	/**
	 * general case where value can be a list
	 * @param valueEl
	 * @param processorId
	 * @param portName
	 * @param collIdRef
	 * @param positionInCollection
	 * @param parentCollectionRef
	 * @param iterationId
	 * @param workflowRunId
	 * @param currentWorkflowID
	 */
	@SuppressWarnings("unchecked")
	private List<PortBinding> processPortBinding(Element valueEl,
			String processorId, String portName, String collIdRef,
			int positionInCollection, String parentCollectionRef,
			String iterationId, String workflowRunId, String itVector,
			String currentWorkflowID) {
		List<PortBinding> newBindings = new ArrayList<>();

		String valueType = valueEl.getName();
		//		logger.info("value element for " + processorId + ": "
		//		+ valueType);

		String iterationVector = (itVector == null ? extractIterationVector(iterationId)
				: itVector);

		PortBinding vb = new PortBinding();

		vb.setWorkflowId(currentWorkflowID);
		vb.setWorkflowRunId(workflowRunId);
		vb.setProcessorName(processorId);
		vb.setValueType(valueType);
		vb.setPortName(portName);
		vb.setCollIDRef(collIdRef);
		vb.setPositionInColl(positionInCollection);

		newBindings.add(vb);

		if (valueType.equals("literal")) {
			try {
				vb.setIteration(iterationVector);
				vb.setValue(valueEl.getAttributeValue("id"));
				logger.debug("new input VB with workflowId="+currentWorkflowID+" processorId="+processorId+
						" valueType="+valueType+" portName="+portName+" collIdRef="+collIdRef+
						" position="+positionInCollection+" itvector="+iterationVector+
						" value="+vb.getValue());
				getPw().addPortBinding(vb);
			} catch (SQLException e) {
				logger.warn("Process Port Binding problem with provenance", e);
			}

		} else if (valueType.equals("referenceSet")) {
			vb.setIteration(iterationVector);
			vb.setValue(valueEl.getAttributeValue("id"));
			vb.setReference(valueEl.getChildText("reference"));

			logger.debug("new input VB with workflowId=" + currentWorkflowID
					+ " processorId=" + processorId + " valueType=" + valueType
					+ " portName=" + portName + " collIdRef=" + collIdRef
					+ " position=" + positionInCollection + " itvector="
					+ iterationVector + " value=" + vb.getValue());

			try {
				getPw().addPortBinding(vb);
			} catch (SQLException e) {
				logger.debug("Problem processing var binding -- performing update instead of insert", e); //, e);
				// try to update the existing record instead using the current collection info

				getPw().updatePortBinding(vb);
			}

		} else if (valueType.equals("list")) {
			logger.debug("input of type list");

			// add entries to the Collection and to the PortBinding tables
			// list id --> Collection.collId

			String collId = valueEl.getAttributeValue("id");
			try {
				parentCollectionRef = getPw().addCollection(processorId, collId,
						parentCollectionRef, iterationVector, portName,
						workflowRunId);

				// iterate over each list element
				List<Element> listElements = valueEl.getChildren();

				positionInCollection = 1;  // also use this as a suffix to extend the iteration vector

				// extend iteration vector to account for additional levels within the list

				String originalIterationVector = iterationVector;

				// children can be any base type, including list itself -- so
				// use recursion
				for (Element el : listElements) {
					if (originalIterationVector.length() > 2) // vector is not empty
						iterationVector = originalIterationVector.substring(0,
								originalIterationVector.length()-1) + ","+
								Integer.toString(positionInCollection-1) + "]";
					else
						iterationVector = "["+ (positionInCollection-1) + "]";

					newBindings.addAll(processPortBinding(el, processorId,
							portName, collId, positionInCollection,
							parentCollectionRef, iterationId, workflowRunId,
							iterationVector, currentWorkflowID));

					positionInCollection++;
				}
			} catch (SQLException e) {
				logger.warn("Problem processing var binding", e);
			}
		} else if (valueType.equals("error")) {
			vb.setIteration(iterationVector);
			vb.setValue(valueEl.getAttributeValue("id"));
			vb.setReference(valueEl.getChildText("reference"));
			try {
				getPw().addPortBinding(vb);
			} catch (SQLException e) {
				logger.debug("Problem processing var binding -- performing update instead of insert", e); //, e);
				// try to update the existing record instead using the current collection info

				getPw().updatePortBinding(vb);
			}
		} else {
			logger.warn("unrecognized value type element for "
					+ processorId + ": " + valueType);
		}

		return newBindings;
	}


	/**
	 * OBSOLETE: returns the iteration vector x,y,z,... from [x,y,z,...]
	 * <p/>
	 * now returns the vector itself -- this is still experimental
	 *
	 * @param iteration
	 * @return
	 */
	@Deprecated
	String extractIterationVector(String iteration) {
		return iteration;
	}

	/**
	 * silly class to hold pairs of strings. any better way??
	 * @author paolo
	 *
	 */
	class Pair {
		String v1, v2;

		public Pair(String current, String workflowId) {
			v1=current; v2=workflowId;
		}

		/**
		 * @return the v1
		 */
		public String getV1() {
			return v1;
		}

		/**
		 * @param v1 the v1 to set
		 */
		public void setV1(String v1) {
			this.v1 = v1;
		}

		/**
		 * @return the v2
		 */
		public String getV2() {
			return v2;
		}

		/**
		 * @param v2 the v2 to set
		 */
		public void setV2(String v2) {
			this.v2 = v2;
		}
	}

	@SuppressWarnings("deprecation")
	public List<Pair> toposort(String dataflowName, String workflowRunId) throws SQLException  {

//		String workflowId = pq.getworkflowIdForDataflow(dataflowName, workflowRunId);
		String workflowId = pq.getWorkflowIdForExternalName(dataflowName);

		// fetch processors along with the count of their predecessors
		Map<String, Integer> predecessorsCount = getPq().getPredecessorsCount(workflowRunId);
		Map<String, List<String>> successorsOf = new HashMap<String, List<String>>();
//		List<String> procList = pq.getContainedProcessors(dataflowName, workflowRunId);
		List<String> procList = pq.getContainedProcessors(dataflowName);

		for (String s:procList) {
			List<String> successors = getPq().getSuccProcessors(s, workflowId, workflowRunId);
			successorsOf.put(s, successors);
		}

		List<Pair>  sorted = tsort(procList, dataflowName, predecessorsCount, successorsOf, workflowId, workflowRunId);

		for (int i=0; i< sorted.size(); i++) {
			String procName = sorted.get(i).getV1();

			if (pq.isDataflow(procName) && !procName.equals(dataflowName)) {  // handle weirdness: a dataflow is contained within itself..
				// recurse on procName
				List<Pair> sortedSublist = toposort(procName, workflowRunId);

				// replace procName with sortedSublist in sorted
				sorted.remove(i);
				sorted.addAll(i, sortedSublist);
			}
		}
		return sorted;
	}



	/**
	 * @param procList
	 * @param predecessorsCount
	 * @param successorsOf
	 * @param workflowRunId
	 * @return
	 * @throws SQLException
	 */
	public List<Pair> tsort(List<String> procList, String dataflowName,
			Map<String, Integer> predecessorsCount,
			Map<String, List<String>> successorsOf, String workflowId,
			String workflowRunId) throws SQLException {
		List<Pair> l = new ArrayList<>();		// holds sorted elements
		List<String> q = new ArrayList<>(); 	// temp queue

		// init queue with procList processors that have no predecessors
		for (String proc:procList)
			if (predecessorsCount.get(proc) == null || predecessorsCount.get(proc) == 0 &&
					!proc.equals(dataflowName))
				q.add(proc);

		while (!q.isEmpty()) {
			String current = q.remove(0);
			l.add(new Pair(current, workflowId));

			List<String> successors = successorsOf.get(current);

			if (successors == null)
				continue;

			// reduce the number of predecessors to each of the successors by one
			// NB we must traverse an additional datalink through a nested workflow input if the successor is a dataflow!!
			for (String succ : successors) {
				// decrease edge count for each successor processor
				predecessorsCount.put(succ, predecessorsCount.get(succ) - 1);

				if (predecessorsCount.get(succ) == 0 && !succ.equals(dataflowName))
					q.add(succ);
			}
		} // end loop on q
		return l;
	}

	@SuppressWarnings("deprecation")
	public void propagateANL(String workflowRunId) throws SQLException {
		String top = pq.getTopLevelDataflowName(workflowRunId);

		// //////////////////////
		// PHASE I: toposort the processors in the whole graph
		// //////////////////////
		List<Pair> sorted = toposort(top, workflowRunId);

		List<String> sortedProcessors = new ArrayList<>();

		for (Pair p : sorted)
			sortedProcessors.add(p.getV1());

		logger.debug("final sorted list of processors");
		for (Pair p : sorted)
			logger.debug(p.getV1() + " in workflowId " + p.getV2());

		// //////////////////////
		// PHASE II: traverse and set anl on each port
		// //////////////////////

		//		// sorted processor names in L at this point
		//		// process them in order
		for (Pair pnameInContext : sorted) {
			//			// process pname's inputs -- set ANL to be the DNL if not set in prior steps
			String pname     = pnameInContext.getV1();
			String workflowId = pnameInContext.getV2();

			List<Port> inputs = getPq().getInputPorts(pname, workflowId); // null -> do not use instance (??) CHECK

			int totalANL = 0;
			for (Port iv : inputs) {

				if (! iv.isResolvedDepthSet()) {
					iv.setResolvedDepth(iv.getDepth());
					getPw().updatePort(iv);
				}

				int delta_nl = iv.getResolvedDepth() - iv.getDepth();

				// if delta_nl < 0 then Taverna wraps the value into a list --> use dnl(X) in this case
				if (delta_nl < 0 ) delta_nl = 0;// CHECK iv.getTypedepth();

				totalANL += delta_nl;

				// this should take care of the special case of the top level dataflow with inputs that have successors in the graph
				// propagate this through all the links from this var
//				List<Port> successors = getPq().getSuccVars(pname, iv.getVName(), workflowRunId);

//				for (Port v : successors) {
//				v.setresolvedDepth(iv.getresolvedDepth());
//				getPw().updateVar(v);
//				}
			}

			// process pname's outputs -- set ANL based on the sum formula (see
			// paper)
			for (Port ov : getPq().getOutputPorts(pname, workflowId)) {

				ov.setResolvedDepth(ov.getDepth() + totalANL);

				logger.debug("anl for "+pname+":"+ov.getPortName()+" = "+(ov.getDepth() + totalANL));
				getPw().updatePort(ov);

				// propagate this through all the links from this var
				for (Port v : getPq().getSuccPorts(pname, ov.getPortName(), workflowId)) {
					List<Port> toBeProcessed = new ArrayList<>();
					toBeProcessed.add(v);

					if (v.getProcessorId() == null && v.isInputPort()) {  // this is the input to a nested workflow
//						String tempWorkflowId = pq.getworkflowIdForDataflow(v.getPName(), workflowRunId);
						String tempWorkflowId = pq
								.getWorkflowIdForExternalName(v
										.getProcessorName());
						List<Port> realSuccessors = getPq().getSuccPorts(
								v.getProcessorName(), v.getPortName(),
								tempWorkflowId);

						toBeProcessed.remove(0);
						toBeProcessed.addAll(realSuccessors);

					} else if (v.getProcessorId() == null && !v.isInputPort()) {  // this is the output to a nested workflow
//						String tempworkflowId = pq.getworkflowIdForDataflow(v.getPName(), workflowRunId);
						List<Port> realSuccessors = getPq().getSuccPorts(
								v.getProcessorName(), v.getPortName(), null);

						toBeProcessed.remove(0);
						toBeProcessed.addAll(realSuccessors);
					}

					for (Port v1 : toBeProcessed) {
						v1.setResolvedDepth(ov.getResolvedDepth());
						logger.debug("anl for " + v1.getProcessorName() + ":"
								+ v1.getPortName() + " = "
								+ ov.getResolvedDepth());
						getPw().updatePort(v1);
					}
				}
			}
		}
	}

	public void setPw(ProvenanceWriter pw) {
		this.pw = pw;
	}

	public ProvenanceWriter getPw() {
		return pw;
	}

	public void setPq(ProvenanceQuery pq) {
		this.pq = pq;
	}

	public ProvenanceQuery getPq() {
		return pq;
	}

	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}

	public String getWorkflowRunId() {
		return workflowRunId;
	}

	public void setWfdp(WorkflowDataProcessor wfdp) {
		this.wfdp = wfdp;
	}

	public WorkflowDataProcessor getWfdp() {
		return wfdp;
	}
}
