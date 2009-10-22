/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.provenance.lineageservice;

import java.beans.ExceptionListener;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.provenance.item.InputDataProvenanceItem;
import net.sf.taverna.t2.provenance.item.IterationProvenanceItem;
import net.sf.taverna.t2.provenance.item.OutputDataProvenanceItem;
import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.item.WorkflowProvenanceItem;
import net.sf.taverna.t2.provenance.lineageservice.utils.Arc;
import net.sf.taverna.t2.provenance.lineageservice.utils.NestedListNode;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProcBinding;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceUtils;
import net.sf.taverna.t2.provenance.lineageservice.utils.Var;
import net.sf.taverna.t2.provenance.lineageservice.utils.VarBinding;
import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 * @author paolo
 */
public class EventProcessor {
	/**
	 * A map of UUIDs of the originating processor to the ProcBinding object
	 * that contains its parameters
	 */
	private Map<String, ProcBinding> procBindingMap = new HashMap<String, ProcBinding>();;

	/** A map of child ids to their parents in the hierarchy of events:
	 *  workflow -> process -> processor -> activity -> iteration
	 */
	private Map<String, String> parentChildMap= new HashMap<String, String>();

	private static Logger logger = Logger.getLogger(EventProcessor.class);

	private static final String OUTPUT_CONTAINER_PROCESSOR = "_OUTPUT_";
	private static final String INPUT_CONTAINER_PROCESSOR = "_INPUT_";
	private static final String TEST_EVENTS_FOLDER = "/tmp/TEST-EVENTS";

	private static final String DATAFLOW_PROCESSOR_TYPE = "net.sf.taverna.t2.activities.dataflow.DataflowActivity";

	static int eventCnt = 0; // for events logging
	static boolean workflowStructureDone = false; // used to inhibit processing of multiple workflow events -- we only need the first
	static int dataflowDepth = 0; // incremented when we recurse on a subflow, decremented on exit

	private String wfInstanceID = null; // unique run ID. set when we see the first event of type "process"

	String topLevelDataflowName = null;
	String topLevelDataflowID   = null;

	Map<String, String> wfNestingMap = new HashMap<String, String>(); 
	
	// all input bindings are accumulated here so they can be "backpatched" (see backpatching() )
	List<VarBinding> allInputVarBindings = new ArrayList<VarBinding>(); 

	// dedicated class for processing WorkflowData events which carry workflow output info 
	private WorkflowDataProcessor  wfdp;
	private ProvenanceWriter pw = null;
	private ProvenanceQuery  pq = null;


	public EventProcessor() { }

	/**
	 * @param pw
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 */
	public EventProcessor(ProvenanceWriter pw, ProvenanceQuery pq, WorkflowDataProcessor wfdp)
	throws InstantiationException, IllegalAccessException,
	ClassNotFoundException, SQLException {
		this.pw = pw;
		this.pq = pq;
		this.wfdp = wfdp;

		logger.setLevel((Level)Level.INFO);
	}


	/**
	 * this is the new version that makes use of the T2 deserializer
	 * populate static portion of the DB<br/>
	 * the static structure may already be in the DB -- this is detected as a duplicate top-level workflow ID.
	 * In this case, we skip this processing altogether 
	 * @param content
	 *            is a serialized dataflow (XML) -- this is parsed using the T2
	 *            Deserializer
	 * @return the wfInstanceRef for this workflow structure
	 */
	public String processWorkflowStructure(ProvenanceItem provenanceItem) {

		// this flag is set to prevent processing of separate workflowProvenanceItems that describe nested workflows.
		// the processing of all nested workflows is done as part of the very first workflowProvenanceItem that we receive,
		// which is self-consistent. so we ignore all others
		if (workflowStructureDone)  { return null; }

		setWfInstanceID(((WorkflowProvenanceItem)provenanceItem).getIdentifier());
//		logger.debug("Workflow instance is: " + getWfInstanceID());
		Dataflow df = null;

		df = ((WorkflowProvenanceItem)provenanceItem).getDataflow();

		workflowStructureDone = true;

		topLevelDataflowName = df.getLocalName();
		topLevelDataflowID   = df.getInternalIdentier();

		// check whether we already have this WF in the DB
		List<String> wfNames = null;
		try {
			wfNames = pq.getAllWFnames();
		} catch (SQLException e) { 
			logger.warn("Problem processing workflow structure:" + e);
		}

		if (wfNames != null && wfNames.contains(topLevelDataflowID)) {  // already in the DB
//			logger.info("workflow structure with ID "+topLevelDataflowID+" is in the DB -- clearing static portion");

			// clearing the portion of the static DB that pertains to this specific WF.
			// it is going to be rewritten right away in the rest of this method
			// this is simpler to implement than selectively avoiding duplicate writes to the DB
			try {
				pw.clearDBStatic(topLevelDataflowID);
			} catch (SQLException e) {
				logger.warn(e);
			}

		} else {
//			logger.info("new workflow structure with ID "+topLevelDataflowID);
		}

		// record the top level dataflow as a processor in the DB
		try {
			pw.addProcessor(topLevelDataflowName, DATAFLOW_PROCESSOR_TYPE, topLevelDataflowID, true);  // true -> is top level
		} catch (SQLException e) {
			logger.warn(e);
		}

//		logger.info("top level wf name: "+topLevelDataflowName);
		return processDataflowStructure(df, topLevelDataflowID, df.getLocalName());  // null: no external name given to top level dataflow
	}		


	/**
	 * note: this method can be called as part of a recursion on sub-workflows
	 * @param df 
	 * @param dataflowID the UUID for the entire dataflow (may be a sub-dataflow)
	 * @param localName the external name of the dataflow. Null if this is top level, not null if a sub-dataflow
	 *  @return the wfInstanceRef for this workflow structure
	 */
	@SuppressWarnings("unchecked")
	public String processDataflowStructure(Dataflow df, String dataflowID, String externalName) {

		String localWfInstanceID = getWfInstanceID();

		dataflowDepth++;

		try {

			List<Var> vars = new ArrayList<Var>();

			// check whether we already have this WF in the DB
			List<String> wfNames = null;
			try {
				wfNames = pq.getAllWFnames();
			} catch (SQLException e) {
				logger.warn("Problem processing dataflow structure for " + dataflowID + " :" + e);
			}

			if (wfNames != null && wfNames.contains(dataflowID)) {  // already in the DB
//				logger.info("workflow structure with ID "+dataflowID+" is in the DB -- clearing static portion");

				// clearing the portion of the static DB that pertains to this specific WF.
				// it is going to be rewritten right away in the rest of this method
				// this is simpler to implement than selectively avoiding duplicate writes to the DB
				pw.clearDBStatic(dataflowID);
			} else {
//				logger.warn("new workflow structure with ID "+dataflowID);
			}

			// //////
			// add workflow ID -- this is NOT THE SAME AS the wfInstanceID
			// /////		

			// this could be a nested workflow -- in this case, override its wfInstanceID with that of its parent
			String parentDataflow;
			if ((parentDataflow = wfNestingMap.get(dataflowID)) == null) {

				// this is a top level dataflow description
				pw.addWFId(dataflowID, null, externalName); // set its dataflowID with no parent

			} else {

				// we are processing a nested workflow structure
				logger.debug("dataflow "+dataflowID+" with external name "+externalName+" is nested within "+parentDataflow);

				pw.addWFId(dataflowID, parentDataflow, externalName); // set its dataflowID along with its parent

				// override wfInstanceID to point to top level -- UNCOMMENTED PM 9/09  CHECK
				localWfInstanceID = pq.getRuns(parentDataflow, null).get(0).getInstanceID();
//				logger.debug("overriding nested WFRef "+getWfInstanceID()+" with parent WFRef "+localWfInstanceID);


			}
			pw.addWFInstanceId(dataflowID, localWfInstanceID);  // wfInstanceID stripped by stripWfInstanceHeader() above

			// //////
			// add processors along with their variables
			// /////
			List<? extends Processor> processors = df.getProcessors();

			for (Processor p : processors) {

//				logger.info("adding processor "+p.getLocalName());

				String pName = p.getLocalName();

				//CHECK get type of first activity and set this as the type of the processor itself
				List<? extends Activity<?>> activities = p.getActivityList();

				String pType = null;
				if (activities != null && !activities.isEmpty()) {
					pType = activities.get(0).getClass().getCanonicalName();
				}
				pw.addProcessor(pName, pType, dataflowID, false);  // false: not a top level processor

				// ///
				// add all input ports for this processor as input variables
				// ///
				List<? extends ProcessorInputPort> inputs = p.getInputPorts();

				for (ProcessorInputPort ip : inputs) {

					Var inputVar = new Var();

					inputVar.setPName(pName);
					inputVar.setWfInstanceRef(dataflowID);
					inputVar.setVName(ip.getName());
					inputVar.setTypeNestingLevel(ip.getDepth());
					inputVar.setInput(true);

//					logger.info("processDataflowStructure: adding input var "+pName+":"+ip.getName());

					vars.add(inputVar);
				}

				// ///
				// add all output ports for this processor as output variables
				// ///
				List<? extends ProcessorOutputPort> outputs = p
				.getOutputPorts();

				for (ProcessorOutputPort op : outputs) {

					Var outputVar = new Var();

					outputVar.setPName(pName);
					outputVar.setWfInstanceRef(dataflowID);
					outputVar.setVName(op.getName());
					outputVar.setTypeNestingLevel(op.getDepth());
					outputVar.setInput(false);

					vars.add(outputVar);
				}


				// check for nested structures: if the activity is DataflowActivity
				// then this processor is a nested workflow
				// make an entry into wfNesting map with its ID and recurse on the nested workflow 

				for (Activity a:activities) {

					if (a.getClass().getCanonicalName().contains("DataflowActivity" )) {

						Dataflow nested = (Dataflow) a.getConfiguration();
//						logger.info("RECURSION ON nested workflow: "+p.getLocalName()+" with id: "+nested.getInternalIdentier());

						wfNestingMap.put(nested.getInternalIdentier(), dataflowID); // child -> parent

						//////////////
						/// RECURSIVE CALL 
						//////////////
						processDataflowStructure(nested, nested.getInternalIdentier(), p.getLocalName());

						//List<? extends Processor> procs = nested.getProcessors();						
//						for (Processor nestedP:procs) {
//						System.out.println("recursion on nested processor: "+nestedP.getLocalName());
//						}

					}
				}

			} // end for each processor

			// ////
			// add inputs to entire dataflow
			// ////

			String pName = INPUT_CONTAINER_PROCESSOR;  // overridden -- see below

			// check whether we are processing a nested workflow. in this case
			// the input vars are not assigned to the INPUT processor but to the containing dataflow

			if (externalName != null) { // override the default if we are nested or someone external name is provided
				pName = externalName;
			}

			List<? extends DataflowInputPort> inputPorts = df.getInputPorts();

			for (DataflowInputPort ip : inputPorts) {

				Var inputVar = new Var();

				inputVar.setPName(pName);
				inputVar.setWfInstanceRef(dataflowID);
				inputVar.setVName(ip.getName());
				inputVar.setTypeNestingLevel(ip.getDepth());
				inputVar.setInput(true);  // CHECK PM modified 11/08 -- input vars are actually outputs of input processors...

				vars.add(inputVar);
			}

			// ////
			// add outputs of entire dataflow
			// ////
			pName = OUTPUT_CONTAINER_PROCESSOR;  // overridden -- see below

			// check whether we are processing a nested workflow. in this case
			// the output vars are not assigned to the OUTPUT processor but to the containing dataflow

			if (externalName != null) { // we are nested
				pName = externalName;
			}

			List<? extends DataflowOutputPort> outputPorts = df
			.getOutputPorts();

			for (DataflowOutputPort op : outputPorts) {

				Var outputVar = new Var();

				outputVar.setPName(pName);
				outputVar.setWfInstanceRef(dataflowID);
				outputVar.setVName(op.getName());
				outputVar.setTypeNestingLevel(op.getDepth());
				outputVar.setInput(false);  // CHECK PM modified 11/08 -- output vars are actually outputs of output processors... 
				vars.add(outputVar);
			}

			pw.addVariables(vars, dataflowID);

			// ////
			// add arc records using the dataflow links
			// retrieving the processor names requires navigating from links to
			// source/sink and from there to the processors
			// ////
			List<? extends Datalink> links = df.getLinks();

			for (Datalink l : links) {

				// TODO cover the case of arcs from an input and to an output to
				// the entire dataflow

				String sourcePname = null;
				String sinkPname = null;

				if (l.getSource() instanceof ProcessorOutputPort) {
					sourcePname = ((ProcessorOutputPort) l.getSource())
					.getProcessor().getLocalName();
				} else {
//					System.out.println("found link from dataflow input");
				}

				if (l.getSink() instanceof ProcessorInputPort) {
					sinkPname = ((ProcessorInputPort) l.getSink())
					.getProcessor().getLocalName();
				} else {
//					System.out.println("found link to dataflow output");
				}

				if (sourcePname != null && sinkPname != null) {
//					System.out.println("adding regular internal arc");

					pw.addArc(l.getSource().getName(), sourcePname, l.getSink()
							.getName(), sinkPname, dataflowID);

				} else if (sourcePname == null) {
					// link is from dataflow input or subflow input
					if (externalName != null) {  // link from subflow input
						sourcePname = externalName;
					} else {
						sourcePname = INPUT_CONTAINER_PROCESSOR;
					}

					//Ian added this logic since there were some null sinkPnameRefs with merge ports

					if (sinkPname == null) {
						// link is to dataflow output

						if (externalName != null) {  // link from subflow input
							sinkPname = externalName;
						} else {
							sinkPname = OUTPUT_CONTAINER_PROCESSOR;
						}
					}

//					System.out.println("adding arc from dataflow input");

					pw.addArc(l.getSource().getName(),
							sourcePname, l.getSink().getName(),
							sinkPname, dataflowID);

				} else if (sinkPname == null) {
					// link is to dataflow output

					if (externalName != null) {  // link from subflow input
						sinkPname = externalName;
					} else {
						sinkPname = OUTPUT_CONTAINER_PROCESSOR;
					}

					//Ian added this bit at the same time as the null sinkPnameRef logic above - hope it is correct

					if (sourcePname == null) {
						// link is from dataflow input or subflow input
						if (externalName != null) {  // link from subflow input
							sourcePname = externalName;
						} else {
							sourcePname = INPUT_CONTAINER_PROCESSOR;
						}
					}

					//					System.out.println("adding arc to dataflow output");

					pw.addArc(l.getSource().getName(), sourcePname, l.getSink()
							.getName(), sinkPname,
							dataflowID);
				}
			}
//			logger.info("completed processing dataflow " + dataflowID);

		} catch (Exception e) {
			logger.warn("Problem processing provenance for dataflow: " + e);
		}

//		logger.debug("wfInstanceID at the end of processDataflowStructure: "+getWfInstanceID());

		return dataflowID;
	}



	private Element stripWfInstanceHeader(String content) {

		SAXBuilder  b = new SAXBuilder();
		Document d;

		try {
			d = b.build (new StringReader(content));

			// get identifier from <workflowItem> element
			Element root = d.getRootElement();

			setWfInstanceID(root.getAttributeValue("identifier"));

			Namespace ns = Namespace.getNamespace("http://taverna.sf.net/2008/xml/t2flow");

			Element workflowEl = root.getChild("workflow", ns);

			return workflowEl;

		} catch (JDOMException e) {
			logger.warn("Problem stripping workflow instance header: " + e);
		} catch (IOException e) {
			logger.warn("Problem stripping workflow instance header: " + e);
		}


		return null;
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

		if (provenanceItem.getEventType().equals(SharedVocabulary.PROCESS_EVENT_TYPE)) {

			String parentId = provenanceItem.getParentId();  // this is the workflowID
			String identifier = provenanceItem.getIdentifier();  // use this as wfInstanceID if this is the top-level process

			parentChildMap.put(identifier, parentId);
			ProcBinding pb = new ProcBinding();
			pb.setExecIDRef(getWfInstanceID());  
			pb.setWfNameRef(currentWorkflowID);
			procBindingMap.put(identifier, pb);

		} else if (provenanceItem.getEventType().equals(SharedVocabulary.PROCESSOR_EVENT_TYPE)) {

			String identifier = provenanceItem.getIdentifier();
			String parentId = provenanceItem.getParentId();
			String processID = provenanceItem.getProcessId(); // this is the external process ID

			// this has the weird form facade0:dataflowname:pname  need to extract pname from here
			String[] processName = processID.split(":");
			procBindingMap.get(parentId).setPNameRef(processName[processName.length-1]);  // 3rd component of composite name


			parentChildMap.put(identifier, parentId);

		} else if (provenanceItem.getEventType().equals(SharedVocabulary.ACTIVITY_EVENT_TYPE)) {

			String identifier = provenanceItem.getIdentifier();
			String parentId = provenanceItem.getParentId();
			procBindingMap.get(parentChildMap.get(parentId)).setActName(identifier);
			parentChildMap.put(identifier, parentId);

		} else if (provenanceItem.getEventType().equals(SharedVocabulary.ITERATION_EVENT_TYPE)) {

			// traverse up to root to retrieve ProcBinding that was created when we saw the process event 
			String iterationID = provenanceItem.getIdentifier();
			String activityID = provenanceItem.getParentId();
			String processorID = parentChildMap.get(activityID);
			String processID = parentChildMap.get(processorID);
			parentChildMap.put(iterationID, activityID);
			parentChildMap.put(iterationID, activityID);
			ProcBinding procBinding = procBindingMap.get(processID);

			String itVector = extractIterationVector(ProvenanceUtils.iterationToString(((IterationProvenanceItem)provenanceItem).getIteration()));
			procBinding.setIterationVector(itVector);
			InputDataProvenanceItem   inputDataEl = ((IterationProvenanceItem)provenanceItem).getInputDataItem();
						OutputDataProvenanceItem outputDataEl = ((IterationProvenanceItem)provenanceItem).getOutputDataItem();
			processInput(inputDataEl, procBinding, currentWorkflowID);
			processOutput(outputDataEl, procBinding, currentWorkflowID);

			try {
				getPw().addProcessorBinding(procBinding);
			} catch (SQLException e) {

				logger.info("WARNING: provenance has duplicate processor binding -- due to workflow nesting");
			}
		} else if (provenanceItem.getEventType().equals(SharedVocabulary.END_WORKFLOW_EVENT_TYPE)) {

			// use this event to do housekeeping on the input/output varbindings 

			dataflowDepth--;
			if (dataflowDepth == 0) {

				// process the outputs accumulated by WorkflowDataProcessor
				getWfdp().processTrees(provenanceItem.getWorkflowId(), wfInstanceID);

				patchTopLevelnputs();

				// PM changed 23/4/09
				reconcileTopLevelOutputs();  // patchTopLevelOutputs		
				workflowStructureDone = false; // CHECK reset for next run... 
			}


		} else if (provenanceItem.getEventType().equals(SharedVocabulary.WORKFLOW_DATA_EVENT_TYPE)) {
			// give this event to a WorkflowDataProcessor object for pre-processing
//			try {
			// TODO may generate an exception when the data is an error CHECK
			getWfdp().addWorkflowDataItem(provenanceItem);
//			} catch (NumberFormatException e) {
//			logger.error(e);
//			}
//			logger.info("Received workflow data - not processing");
			//FIXME not sure  - needs to be stored somehow

		} else if (provenanceItem.getEventType().equals((SharedVocabulary.ERROR_EVENT_TYPE))) {
			//TODO process the error

		} else {
			// TODO broken, should we throw something here?
			return;
		}

	}

	/**
	 * fills in the VBs for the global inputs -- this removes the need for explicit events
	 * that account for these value bindings...
	 */
	public void patchTopLevelnputs() {

		// for each input I to topLevelDataflow:
		// pick first outgoing arc with sink P:X
		// copy value X to I -- this can be a collection, so copy everything

		// get all global input vars

//		logger.info("\n\n BACKPATCHING GLOBAL INPUTS with dataflowDepth = "+dataflowDepth+"*******\n");

		List<Var> inputs=null;
		try {
			inputs = getPq().getInputVars(topLevelDataflowName, topLevelDataflowID, getWfInstanceID());

			for (Var input:inputs)  {

//				logger.info("global input: "+input.getVName());

				Map<String,String> queryConstraints = new HashMap<String,String>();

				queryConstraints.put("sourceVarNameRef", input.getVName());
				queryConstraints.put("sourcePNameRef", input.getPName());

				List<Arc> outgoingArcs = getPq().getArcs(queryConstraints);

				// any arc will do, use the first
				String targetPname = outgoingArcs.get(0).getSinkPnameRef();
				String targetVname = outgoingArcs.get(0).getSinkVarNameRef();

//				logger.info("copying values from ["+targetPname+":"+targetVname+"] for instance ID: ["+wfInstanceID+"]");

				queryConstraints.clear();
				queryConstraints.put("varNameRef", targetVname);
				queryConstraints.put("V.pNameRef", targetPname);
				queryConstraints.put("VB.wfInstanceRef", getWfInstanceID());
				queryConstraints.put("V.wfInstanceRef", topLevelDataflowID);

				List<VarBinding> VBs = getPq().getVarBindings(queryConstraints);

//				logger.info("found the following VBs:");
				for (VarBinding vb:VBs) {
//					logger.info(vb.getValue());

					// insert VarBinding back into VB with the global input varname
					vb.setPNameRef(input.getPName());
					vb.setVarNameRef(input.getVName());
					getPw().addVarBinding(vb);

//					logger.info("added");

				}

			}
		} catch (SQLException e) {
			logger.warn("Patch top level inputs problem for provenance: " + e);
		} catch (IndexOutOfBoundsException e) {
			logger.error(e);
		}

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
	public void reconcileTopLevelOutputs() {
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

		Map<String,String> queryConstraints = new HashMap<String,String>();

		List<Var> outputs=null;
		try {

			outputs = pq.getOutputVars(topLevelDataflowName, topLevelDataflowID, null);  // null InstanceID 

			// for each output O
			for (Var output:outputs)  {

				// collect all VBs for O
//				String oPName = output.getPName();
//				String oVName = output.getVName();
//				queryConstraints.put("varNameRef", oVName);
//				queryConstraints.put("V.pNameRef", oPName);
//				queryConstraints.put("VB.wfInstanceRef", wfInstanceID);
//				queryConstraints.put("V.wfInstanceRef", topLevelDataflowID);

//				List<VarBinding> OValues = pq.getVarBindings(queryConstraints);

				// find all records for the immediate precedessor Y of O
				queryConstraints.clear();
				queryConstraints.put("sinkVarNameRef", output.getVName());
				queryConstraints.put("sinkPNameRef", output.getPName());

				List<Arc> incomingArcs = pq.getArcs(queryConstraints);

				// there can be only one -- but check that there is one!
				if (incomingArcs.size()==0)  continue;

				String sourcePname = incomingArcs.get(0).getSourcePnameRef();
				String sourceVname = incomingArcs.get(0).getSourceVarNameRef();

				queryConstraints.clear();
				queryConstraints.put("varNameRef", sourceVname);
				queryConstraints.put("V.pNameRef", sourcePname);
				queryConstraints.put("VB.wfInstanceRef", wfInstanceID);
				queryConstraints.put("V.wfInstanceRef", topLevelDataflowID);

				List<VarBinding> YValues = pq.getVarBindings(queryConstraints);

				// for each YValue look for a match in OValues
				// (assume the YValues values are a superset of OValues)!)

				for (VarBinding yValue:YValues) {


//					System.out.println("reconcileTopLevelOutputs:: processing "+
//					yValue.getPNameRef()+"/"+yValue.getVarNameRef()+"/"+yValue.getValue()+
//					" with collid "+yValue.getCollIDRef());

					// look for a matching record in VarBinding for output O
					queryConstraints.clear();
					queryConstraints.put("varNameRef", output.getVName());
					queryConstraints.put("V.pNameRef", output.getPName());
					queryConstraints.put("VB.wfInstanceRef", wfInstanceID);
					queryConstraints.put("V.wfInstanceRef", topLevelDataflowID);
					queryConstraints.put("VB.iteration", yValue.getIteration());
					if (yValue.getCollIDRef()!= null) {
						queryConstraints.put("VB.collIDRef", yValue.getCollIDRef());
						queryConstraints.put("VB.positionInColl", Integer.toString(yValue.getPositionInColl()));
					}
					List<VarBinding> matchingOValues = pq.getVarBindings(queryConstraints);

//					System.out.println("querying for matching oValues: ");

					// result at most size 1
					if (matchingOValues.size() > 0) {

						VarBinding oValue = matchingOValues.get(0);

//						System.out.println("found "+oValue.getPNameRef()+"/"+oValue.getVarNameRef()+"/"+oValue.getValue()+
//						" with collid "+oValue.getCollIDRef());

						// copy collection info from oValue to yValue						
						yValue.setCollIDRef(oValue.getCollIDRef());
						yValue.setPositionInColl(oValue.getPositionInColl());

						pw.updateVarBinding(yValue);

//						System.out.println("oValue copied to yValue");
					} else {

//						System.out.println("no match found");

						// copy the yValue to O 
						// insert VarBinding back into VB with the global output varname
						yValue.setPNameRef(output.getPName());
						yValue.setVarNameRef(output.getVName());
						pw.addVarBinding(yValue);
					}

				} // for each yValue in YValues

				// copy all Collection records for O to Y 

				// get all collections refs for O
				queryConstraints.clear();
				queryConstraints.put("wfInstanceRef", wfInstanceID);
				queryConstraints.put("PNameRef", output.getPName());
				queryConstraints.put("varNameRef", output.getVName());

				List<NestedListNode> oCollections = pq.getNestedListNodes(queryConstraints);

				// insert back as collection refs for Y -- catch duplicates
				for (NestedListNode nln:oCollections) {
//					System.out.println("collection: "+nln.getCollId());

					nln.setPNameRef(sourcePname);
					nln.setPNameRef(sourceVname);

					getPw().replaceCollectionRecord(nln, sourcePname, sourceVname);
				}

			} // for each output var

		} catch (SQLException e) {
			logger.warn("Problem reconciling top level outputs: " + e);
		}

	}




	@SuppressWarnings("unchecked")
	private void processOutput(OutputDataProvenanceItem provenanceItem, ProcBinding procBinding, String currentWorkflowID) {

		Element dataItemAsXML = ProvenanceUtils.getDataItemAsXML(provenanceItem);
		List<Element> outputPorts = dataItemAsXML.getChildren("port");
		for (Element outputport : outputPorts) {

			String portName = outputport.getAttributeValue("name");

			// value type may vary
			List<Element> valueElements = outputport.getChildren();
			if (valueElements != null && valueElements.size() > 0) {

				Element valueEl = valueElements.get(0); // only really 1 child

				processVarBinding(valueEl,  procBinding.getPNameRef(), portName, procBinding.getIterationVector(),
						getWfInstanceID(), currentWorkflowID);
			}
		}

	}


	/**
	 * this method reconciles values in varBindings across an arc: Firstly, if vb's value is within a collection,
	 *  _and_ it is copied from a value generated during a previous iteration,
	 * then this method propagates the list reference to that iteration value, which wouldn't have it.
	 * Conversely, if vb is going to be input to an iteration, then it's lost its containing list node, and we
	 * put it back in by looking at the corresponding predecessor 
	 * @param vb
	 * @throws SQLException 
	 */
	private void backpatchIterationResults(List<VarBinding> newBindings) throws SQLException {

		logger.debug("backpatchIterationResults: start");
		for (VarBinding vb:newBindings) {

			logger.debug("backpatchIterationResults: processing vb "+vb.getPNameRef()+"/"+vb.getVarNameRef()+"="+vb.getValue());

			if (vb.getCollIDRef()!= null)  {  // this is a member of a collection
				logger.debug("...which is inside a collection ");
			}

			// look for its antecedent
			Map<String,String> queryConstraints = new HashMap<String,String>();

			queryConstraints.put("sinkVarNameRef", vb.getVarNameRef());
			queryConstraints.put("sinkPNameRef", vb.getPNameRef());				
			queryConstraints.put("wfInstanceRef", pq.getWfNames(vb.getWfInstanceRef()).get(0));  // CHECK picking first element in list...

			List<Arc> incomingArcs = pq.getArcs(queryConstraints);

			// there can be only one -- but check that there is one!
			if (incomingArcs.size()==0)  return;

			String sourcePname = incomingArcs.get(0).getSourcePnameRef();
			String sourceVname = incomingArcs.get(0).getSourceVarNameRef();

			logger.debug("antecedent: "+sourcePname+":"+sourceVname);

			// get the varbindings for this port and select the one with the same iteration vector as its successor
			queryConstraints.clear();
			queryConstraints.put("varNameRef", sourceVname);
			queryConstraints.put("V.pNameRef", sourcePname);
			queryConstraints.put("VB.value", vb.getValue());
			queryConstraints.put("VB.wfInstanceRef", vb.getWfInstanceRef());

			List<VarBinding> VBs = pq.getVarBindings(queryConstraints);

			if (VBs.size() == 0) { logger.debug("nothing to reconcile"); }
			
			// reconcile
			for (VarBinding b:VBs) {

				logger.debug("backpatching "+sourceVname+" "+sourcePname);

				if (vb.getCollIDRef() != null && b.getCollIDRef() == null) {
					
					logger.debug("successor "+vb.getVarNameRef()+" is in collection "+vb.getCollIDRef()+" but pred "+b.getVarNameRef()+" is not");
					logger.debug("putting "+b.getVarNameRef()+" in collection "+vb.getCollIDRef()+" at pos "+vb.getPositionInColl());
					b.setCollIDRef(vb.getCollIDRef());
					b.setPositionInColl(vb.getPositionInColl());
					getPw().updateVarBinding(b);
					
				} else if (vb.getCollIDRef() == null && b.getCollIDRef() != null) {
					
					logger.debug("successor "+vb.getVarNameRef()+" is NOT in collection but pred "+b.getVarNameRef()+" IS");
					logger.debug("putting "+vb.getVarNameRef()+" in collection "+b.getCollIDRef()+" at pos "+b.getPositionInColl());
					vb.setCollIDRef(b.getCollIDRef());
					vb.setPositionInColl(b.getPositionInColl());
					getPw().updateVarBinding(vb);						
				}
			}
		}
	}


	/**
	 * create one new VarBinding record for each input port binding
	 * @param currentWorkflowID 
	 */
	@SuppressWarnings("unchecked")
	private void processInput(InputDataProvenanceItem provenanceItem, ProcBinding procBinding, String currentWorkflowID) {

		Element dataItemAsXML = ProvenanceUtils.getDataItemAsXML(provenanceItem);
		List<Element> inputPorts = dataItemAsXML.getChildren("port");
		int order = 0;
		for (Element inputport : inputPorts) {

			String portName = inputport.getAttributeValue("name");
//			logger.info("processInput: processing VarBinding for "+procBinding.getPNameRef()+"  "+portName);

			try {
				// add process order sequence to Var for this portName

				Map<String, String> queryConstraints = new HashMap<String, String>();
				queryConstraints.put("wfInstanceRef", currentWorkflowID);
				queryConstraints.put("pnameRef", procBinding.getPNameRef());
				queryConstraints.put("varName", portName);
				queryConstraints.put("inputOrOutput", "1");

				List<Var> vars = getPq().getVars(queryConstraints);
				try {
					Var v = vars.get(0);
					v.setPortNameOrder(order++);
					getPw().updateVar(v);
				}
				catch (IndexOutOfBoundsException e) {
					logger.error(e);
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				logger.error(e1);
			}

			// value type may vary
			List<Element> valueElements = inputport.getChildren(); // hopefully
			// in the
			// right
			// order...
			if (valueElements != null && valueElements.size() > 0) {

				Element valueEl = valueElements.get(0); // expect only 1 child
//				processVarBinding(valueEl, processor, portName, iterationVector,
//				dataflow);

				List<VarBinding> newBindings = 
					processVarBinding(valueEl, procBinding.getPNameRef(), portName, procBinding.getIterationVector(),
						wfInstanceID, currentWorkflowID);
				// this is a list whenever valueEl is of type list: in this case processVarBinding recursively
				// processes all values within the collection, and generates one VarBinding record for each of them

				allInputVarBindings.addAll(newBindings);
				
//				logger.debug("newBindings now has "+newBindings.size()+" elements");

				//				// if the new binding involves list values, then check to see if they need to be propagated back to 
//				// results of iterations
				try {
					backpatchIterationResults(newBindings);
				} catch (SQLException e) {
					logger.warn("Problem with back patching iteration results: " + e);

				}

			} else {
				  if (valueElements != null)  logger.debug("port name "+portName+"  "+valueElements.size());
				  else logger.debug("valueElements is null for port name "+portName);
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
	 * @param wfInstanceRef
	 * @param currentWorkflowID 
	 */
	private List<VarBinding> processVarBinding(Element valueEl, String processorId,
			String portName, String iterationId, String wfInstanceRef, String currentWorkflowID) {

		// uses the defaults:
		// collIdRef = null
		// parentcollectionRef = null
		// positionInCollection = 1
		return processVarBinding(valueEl, processorId, portName, null, 1, null,
				iterationId, wfInstanceRef, null, currentWorkflowID);
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
	 * @param wfInstanceRef
	 * @param currentWorkflowID 
	 */
	@SuppressWarnings("unchecked")
	private List<VarBinding>  processVarBinding(Element valueEl, String processorId,
			String portName, String collIdRef, int positionInCollection,
			String parentCollectionRef, String iterationId, String wfInstanceRef, String itVector, String currentWorkflowID) {

		List<VarBinding> newBindings = new ArrayList<VarBinding>();

		String valueType = valueEl.getName();
//		logger.info("value element for " + processorId + ": "
//		+ valueType);

		String iterationVector = null;

		if (itVector == null) 
			iterationVector = extractIterationVector(iterationId);
		else iterationVector = itVector;

		VarBinding vb = new VarBinding();

		vb.setWfNameRef(currentWorkflowID);
		vb.setWfInstanceRef(wfInstanceRef);
		vb.setPNameRef(processorId);
		vb.setValueType(valueType);
		vb.setVarNameRef(portName);
		vb.setCollIDRef(collIdRef);
		vb.setPositionInColl(positionInCollection);

		logger.debug("new input VB with wfNameRef="+currentWorkflowID+" processorId="+processorId+
				" valueType="+valueType+" portName="+portName+" collIdRef="+collIdRef+" position="+positionInCollection+" itvector="+iterationVector);
		
		newBindings.add(vb);
		
		if (valueType.equals("literal")) {

//			logger.warn("input of type literal");

			try {

				vb.setIterationVector(iterationVector);
				vb.setValue(valueEl.getAttributeValue("id"));

//				logger.info("calling addVarBinding on "+vb.getPNameRef()+" : "+vb.getVarNameRef()); 
				getPw().addVarBinding(vb);

			} catch (SQLException e) {
				logger.warn("Process Var Binding problem with provenance" + e.getMessage());
			}

		} else if (valueType.equals("referenceSet")) {

			vb.setIterationVector(iterationVector);
			vb.setValue(valueEl.getAttributeValue("id"));
			vb.setRef(valueEl.getChildText("reference"));

			try {
//				logger.debug("calling addVarBinding on "+vb.getPNameRef()+" : "+vb.getVarNameRef()+" with it "+vb.getIteration()); 
				getPw().addVarBinding(vb);
			} catch (SQLException e) {
				logger.warn("Problem processing var binding: " + e);
			}

		} else if (valueType.equals("list")) {

//			logger.warn("input of type list");

			// add entries to the Collection and to the VarBinding tables
			// list id --> Collection.collId

			String collId = valueEl.getAttributeValue("id");
			try {

				parentCollectionRef = getPw().addCollection(processorId, collId,
						parentCollectionRef, iterationVector, portName,
						wfInstanceRef);

				// iterate over each list element
				List<Element> listElements = valueEl.getChildren();

				positionInCollection = 1;  // also use this as a suffix to extend the iteration vector

				// extend iteration vector to account for additional levels within the list

				String originalIterationVector = iterationVector;

				// children can be any base type, including list itself -- so
				// use recursion
				for (Element el : listElements) {

					if (originalIterationVector.length() >2)  { // vector is not empty
						iterationVector = originalIterationVector.substring(0, 
								originalIterationVector.length()-1) + ","+ 
								Integer.toString(positionInCollection-1) + "]";
					} else {
						iterationVector = "["+ Integer.toString(positionInCollection-1) + "]";
					}

					List<VarBinding> bindings = processVarBinding(el, processorId, portName, collId,
							positionInCollection, parentCollectionRef,
							iterationId, wfInstanceRef, iterationVector, currentWorkflowID);

					newBindings.addAll(bindings);

					positionInCollection++;
				}

			} catch (SQLException e) {
				logger.warn("Problem processing var binding: " + e);
			}
		} else if (valueType.equals("error")) {
			try {
				vb.setIterationVector(iterationVector);
				vb.setValue(valueEl.getAttributeValue("id"));

				getPw().addVarBinding(vb);

			} catch (SQLException e) {
				logger.warn("Process Var Binding problem with provenance"
						+ e.getMessage());
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
	String extractIterationVector(String iteration) {

		return iteration;
		// return iteration.substring(1, iteration.length() - 1);
		// iteration is of the form "[n]" so we extract n
		// String iterationN = iteration.substring(1, iteration.length()-1);

		// if (iterationN.length() == 0) return 0;

		// return Integer.parseInt(iterationN);
	}

	/**
	 * log raw event to file system
	 * 
	 * @param content
	 * @param eventType
	 * @throws IOException
	 */
	public void saveEvent(ProvenanceItem provenanceItem, SharedVocabulary eventType) throws IOException {

		// HACK -- XMLEncoder fails on IterationEvents and there is no way to catch the exception...
		// so avoid this case
		if (eventType.equals(SharedVocabulary.ITERATION_EVENT_TYPE))  {
			return;
		}

//		System.out.println("saveEvent: start");

		File f1 = null;

		f1 = new File(TEST_EVENTS_FOLDER);
		FileUtils.forceMkdir(f1);

		String fname = "event_" + eventCnt++ + "_" + eventType + ".xml";
		File f = new File(f1, fname);

//		FileWriter fw = new FileWriter(f);

		XMLEncoder en = new XMLEncoder(new BufferedOutputStream(
				new FileOutputStream(f)));

		en.setExceptionListener(new ExceptionListener()
		{
			public void exceptionThrown(Exception e)
			{
				logger.warn("XML encoding ERROR: " + e);
				return;
			}
		});

		logger.debug("saving to " + f); // save event for later inspection
		logger.debug(provenanceItem);

		en.writeObject(provenanceItem);

		logger.debug("writer ok");
		en.close();
		logger.debug("closed");

//		fw.write(content);
//		fw.flush();
//		fw.close();

//		FileWriter fw = new FileWriter(f);
//		fw.write(content);
//		fw.flush();
//		fw.close();

//		System.out.println("saved as file " + fname);


	}

	/**
	 * for each arc of the form (_INPUT_/I, P/V): propagate VarBinding for P/V
	 * to var _INPUT_/I <br/>
	 * 
	 * @throws SQLException
	 */
	public void fillInputVarBindings(Object context) throws SQLException {

		// System.out.println("*** fillInputVarBindings: ***");

		// retrieve appropriate arcs
		Map<String, String> constraints = new HashMap<String, String>();
		constraints.put("sourcePnameRef", "_INPUT_");
		constraints.put("W.instanceID", getWfInstanceID());
		List<Arc> arcs = getPq().getArcs(constraints);

		// backpropagate VarBinding from the target var of the arc to the source
		for (Arc aArc : arcs) {

//			logger.info("propagating VarBinding from ["
//			+ aArc.getSinkPnameRef() + "/" + aArc.getSinkVarNameRef()
//			+ "] to input [" + aArc.getSourcePnameRef() + "/"
//			+ aArc.getSourceVarNameRef() + "]");

			// get the varBinding for the arc sinks
			Map<String, String> vbConstraints = new HashMap<String, String>();
			vbConstraints.put("VB.PNameRef", aArc.getSinkPnameRef());
			vbConstraints.put("VB.varNameRef", aArc.getSinkVarNameRef());
			vbConstraints.put("VB.wfInstanceRef", wfInstanceID);

			List<VarBinding> vbList = getPq().getVarBindings(vbConstraints); // DB
			// QUERY

			for (VarBinding vb : vbList) {
				// add a new VarBinding for the input

				vb.setPNameRef(aArc.getSourcePnameRef());
				vb.setVarNameRef(aArc.getSourceVarNameRef());
				// all other attributes are the same --> CHECK!!

				getPw().addVarBinding(vb);
			}
		}
	}

	/**
	 * for each arc of the form (P/V, _OUTPUT_/O): propagate VarBinding for P/V
	 * to var _OUTPUT_/O <br/>
	 * 
	 * @throws SQLException
	 */
	public void fillOutputVarBindings(Object context) throws SQLException {

		//System.out.println("*** fillOutputVarBindings: ***");

		// retrieve appropriate arcs
		Map<String, String> constraints = new HashMap<String, String>();
		constraints.put("sinkPnameRef", "_OUTPUT_");
		constraints.put("wfInstanceRef", getWfInstanceID());
		List<Arc> arcs = getPq().getArcs(constraints);

		// fowd propagate VarBinding from the source var of the arc to the
		// output
		for (Arc aArc : arcs) {

//			logger.info("fwd propagating VarBinding from ["
//			+ aArc.getSourcePnameRef() + "/"
//			+ aArc.getSourceVarNameRef() + "] to input ["
//			+ aArc.getSinkPnameRef() + "/" + aArc.getSinkVarNameRef()
//			+ "]");

			// get the varBinding for the arc sinks
			Map<String, String> vbConstraints = new HashMap<String, String>();
			vbConstraints.put("VB.PNameRef", aArc.getSourcePnameRef());
			vbConstraints.put("VB.varNameRef", aArc.getSourceVarNameRef());
			vbConstraints.put("VB.wfInstanceRef", getWfInstanceID());

			List<VarBinding> vbList = getPq().getVarBindings(vbConstraints); // DB
			// QUERY

			for (VarBinding vb : vbList) {
				// add a new VarBinding for the input
				getPw().addVarBinding(vb); // DB UPDATE
			}

		}
	}


	/**
	 * silly class to hold pairs of strings. any better way??
	 * @author paolo
	 *
	 */
	class Pair {
		String v1, v2;

		public Pair(String current, String wfNameRef) {
			v1=current; v2=wfNameRef;
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

	public List<Pair> toposort(String dataflowName, String wfInstanceId) throws SQLException  {

//		String wfNameRef = pq.getWfNameForDataflow(dataflowName, wfInstanceID);
		String wfNameRef = pq.getWfNameForDataflow(dataflowName);

		// fetch processors along with the count of their predecessors
		Map<String, Integer> predecessorsCount = getPq().getPredecessorsCount(wfInstanceId);
		Map<String, List<String>> successorsOf = new HashMap<String, List<String>>();
//		List<String> procList = pq.getContainedProcessors(dataflowName, wfInstanceId);
		List<String> procList = pq.getContainedProcessors(dataflowName);

//		logger.debug("toposort on "+dataflowName);

//		logger.debug("contained procs: ");
		for (String s:procList) { 

			List<String> successors = getPq().getSuccProcessors(s, wfNameRef, wfInstanceId);
			successorsOf.put(s, successors);

//			logger.debug(s+" with "+predecessorsCount.get(s)+" predecessors and successors:");

//			for (String s1:successors) { logger.debug(s1); }
		}

		List<Pair>  sorted = tsort(procList, dataflowName, predecessorsCount, successorsOf, wfNameRef, wfInstanceId);

//		logger.debug("tsort:");
//		for (String p : sorted) { logger.debug(p); }

		for (int i=0; i< sorted.size(); i++) {

			String procName = sorted.get(i).getV1();

			if (pq.isDataflow(procName) && !procName.equals(dataflowName)) {  // handle weirdness: a dataflow is contained within itself..
				// recurse on procName

//				logger.debug("recursion on "+procName);
				List<Pair> sortedSublist = toposort(procName, wfInstanceId);

				// replace procName with sortedSublist in sorted
				sorted.remove(i);
				sorted.addAll(i, sortedSublist);				
			}
		}
		return sorted;
	}



	/**
	 * STUB
	 * @param procList
	 * @param predecessorsCount 
	 * @param successorsOf 
	 * @param wfInstanceId 
	 * @return
	 * @throws SQLException 
	 */
	public List<Pair> tsort(List<String> procList, 
			String dataflowName, 
			Map<String, Integer> predecessorsCount, 
			Map<String, List<String>> successorsOf, 
			String wfNameRef, String wfInstanceId) throws SQLException {

		List<Pair> L = new ArrayList<Pair>();		// holds sorted elements
		List<String> Q = new ArrayList<String>(); 		// temp queue
		Set<String> visited = new HashSet<String>();

//		logger.debug("queue init with procList");
		// init queue with procList processors that have no predecessors
		for (String proc:procList) {			

//			logger.debug("dataflowName: "+dataflowName+" proc: "+proc);

			if (predecessorsCount.get(proc) == null || predecessorsCount.get(proc) == 0 &&
					!proc.equals(dataflowName)) {

				Q.add(proc);				
			}
//			logger.debug(proc + " added to queue");
//			} else 
//			logger.debug(proc+" not added to queue");
		}

//		logger.debug("queue has "+Q.size()+" elements");
		while (!Q.isEmpty()) {

			String current = Q.remove(0);
//			logger.debug("extracted "+current+" and added to L");
			L.add(new Pair(current,wfNameRef));

//			for (String s:L) logger.debug(s);

			List<String> successors = successorsOf.get(current);

//			logger.debug("\n****successors of "+current);

			if (successors == null) continue;

			// reduce the number of predecessors to each of the successors by one
			// NB we must traverse an additional arc through a nested workflow input if the successor is a dataflow!!
			for (String succ : successors) {

//				logger.debug(succ);

				// decrease edge count for each successor processor
				Integer cnt = predecessorsCount.get(succ);
				predecessorsCount.put(succ, new Integer(cnt.intValue() - 1));

//				logger.debug("now "+succ+" has "+predecessorsCount.get(succ)+" predecessors");

				if (predecessorsCount.get(succ) == 0 && !succ.equals(dataflowName)) {
					Q.add(succ);
//					logger.debug("adding "+succ+" to queue");
				}
			}
		} // end loop on Q
		return L;
	}


	public void propagateANL(String wfInstanceId) throws SQLException {

		String top = pq.getTopLevelDataflowName(wfInstanceId);

		// //////////////////////
		// PHASE I: toposort the processors in the whole graph
		// //////////////////////
		List<Pair> sorted = toposort(top, wfInstanceId);

		logger.debug("final sorted list of processors");
		for (Pair p:sorted) {  logger.debug(p.getV1()+"  in wfnameRef "+p.getV2()); }

		// //////////////////////
		// PHASE II: traverse and set anl on each port
		// //////////////////////

//		logger.debug("***** STARTING ANL *****");

//		// sorted processor names in L at this point
//		// process them in order
		for (Pair pnameInContext : sorted) {

//			logger.debug("setting ANL for "+pnameInContext.getV1()+" input vars");

//			// process pname's inputs -- set ANL to be the DNL if not set in prior steps
			String pname     = pnameInContext.getV1();
			String wfNameRef = pnameInContext.getV2();

//			logger.debug("processor "+pname);

			List<Var> inputs = getPq().getInputVars(pname, wfNameRef, wfInstanceId); // null -> do not use instance (??) CHECK

//			logger.debug(inputs.size()+" inputs for "+pnameInContext.getV1());

			int totalANL = 0;
			for (Var iv : inputs) {

				if (iv.isANLset() == false) {
					iv.setActualNestingLevel(iv.getTypeNestingLevel());
					iv.setANLset(true);
					getPw().updateVar(iv);

//					logger.debug("var: "+iv.getVName()+" set at nominal level "+iv.getActualNestingLevel());					
				}

				int delta_nl = iv.getActualNestingLevel() - iv.getTypeNestingLevel();

				// if delta_nl < 0 then Taverna wraps the value into a list --> use dnl(X) in this case
				if (delta_nl < 0 ) delta_nl = 0;// CHECK iv.getTypeNestingLevel();
//				logger.debug("delta for "+iv.getVName()+" "+delta_nl);

				totalANL += delta_nl;

				// this should take care of the special case of the top level dataflow with inputs that have successors in the graph
				// propagate this through all the links from this var
//				List<Var> successors = getPq().getSuccVars(pname, iv.getVName(), wfInstanceId);

//				logger.debug(successors.size()+ " successors for var "+iv.getVName());

//				for (Var v : successors) {
//				v.setActualNestingLevel(iv.getActualNestingLevel());
//				v.setANLset(true);
//				getPw().updateVar(v);
//				}
			}
//			logger.debug("total anl: "+totalANL);

//			logger.debug("now setting ANL for "+pname+" output vars");

			// process pname's outputs -- set ANL based on the sum formula (see
			// paper)
			List<Var> outputs = getPq().getOutputVars(pname, wfNameRef, wfInstanceId);
			for (Var ov : outputs) {

				ov.setActualNestingLevel(ov.getTypeNestingLevel() + totalANL);

				logger.debug("anl for "+pname+":"+ov.getVName()+" = "+(ov.getTypeNestingLevel() + totalANL));
				ov.setANLset(true);
				getPw().updateVar(ov);

				// propagate this through all the links from this var
				List<Var> successors = getPq().getSuccVars(pname, ov.getVName(), wfNameRef);

//				logger.debug(successors.size()+ " successors for var "+ov.getVName());

				for (Var v : successors) {

					List<Var> toBeProcessed = new ArrayList<Var>();
					toBeProcessed.add(v);

					if (pq.isDataflow(v.getPName()) && v.isInput()) {  // this is the input to a nested workflow

//						String tempWfNameRef = pq.getWfNameForDataflow(v.getPName(), wfInstanceId);
						String tempWfNameRef = pq.getWfNameForDataflow(v.getPName());
						List<Var> realSuccessors = getPq().getSuccVars(v.getPName(), v.getVName(), tempWfNameRef);	

//						logger.debug("realSuccessors size = "+realSuccessors.size());

						toBeProcessed.remove(0);
						toBeProcessed.addAll(realSuccessors);

					}  else if (pq.isDataflow(v.getPName()) && !v.isInput()) {  // this is the output to a nested workflow

//						String tempWfNameRef = pq.getWfNameForDataflow(v.getPName(), wfInstanceId);
						String tempWfNameRef = pq.getWfNameForDataflow(v.getPName());
						List<Var> realSuccessors = getPq().getSuccVars(v.getPName(), v.getVName(), null);	

//						logger.debug("realSuccessors size = "+realSuccessors.size());

						toBeProcessed.remove(0);
						toBeProcessed.addAll(realSuccessors);

					}

					for (Var v1:toBeProcessed) {
						v1.setActualNestingLevel(ov.getActualNestingLevel());
						logger.debug("anl for "+v1.getPName()+":"+v1.getVName()+" = "+ov.getActualNestingLevel());

						v1.setANLset(true);
						getPw().updateVar(v1);
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

	public void setWfInstanceID(String wfInstanceID) {
		this.wfInstanceID = wfInstanceID;
	}

	public String getWfInstanceID() {
		return wfInstanceID;
	}

	public void setWfdp(WorkflowDataProcessor wfdp) {
		this.wfdp = wfdp;
	}

	public WorkflowDataProcessor getWfdp() {
		return wfdp;
	}

}
