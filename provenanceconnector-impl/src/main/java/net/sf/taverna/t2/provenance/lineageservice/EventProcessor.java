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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.provenance.lineageservice.utils.Arc;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProcBinding;
import net.sf.taverna.t2.provenance.lineageservice.utils.Var;
import net.sf.taverna.t2.provenance.lineageservice.utils.VarBinding;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.serialization.DeserializationException;
import net.sf.taverna.t2.workflowmodel.serialization.xml.XMLDeserializer;
import net.sf.taverna.t2.workflowmodel.serialization.xml.XMLDeserializerRegistry;

import org.apache.commons.io.FileUtils;
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
	private Map<String, ProcBinding> procBindingMap;

	/** A map of child ids to their parents in the hierarchy of events:
	 *  workflow -> process -> processor -> activity -> iteration
	 */
	private Map<String, String> parentChildMap;
	
	private static Logger logger = Logger.getLogger(EventProcessor.class);

	private static final String OUTPUT_CONTAINER_PROCESSOR = "_OUTPUT_";
	private static final String INPUT_CONTAINER_PROCESSOR = "_INPUT_";
	private static final String TEST_EVENTS_FOLDER = "/tmp/TEST-EVENTS";

	private static final String DATAFLOW_PROCESSOR_TYPE = "net.sf.taverna.t2.activities.dataflow.DataflowActivity";

	static int eventCnt = 0; // for events logging
	static boolean workflowStructureDone = false; // used to inhibit processing of multiple workflow events -- we only need the first
	static int dataflowDepth = 0; // incremented when we recurse on a subflow, decremented on exit

	static String wfInstanceID = null; // unique run ID. set when we see the first event of type "process"

	
	String topLevelDataflowName = null;
	String topLevelDataflowID   = null;

	Map<String, String> wfNestingMap = new HashMap<String, String>(); 

	ProvenanceWriter pw = null;
	ProvenanceQuery pq = null;

	private Connection openConnection;

	/**
	 * @param pw
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 */
	public EventProcessor(ProvenanceWriter pw, ProvenanceQuery pq)
	throws InstantiationException, IllegalAccessException,
	ClassNotFoundException, SQLException {
		this.pw = pw;
		this.pq = pq;

		procBindingMap = new HashMap<String, ProcBinding>();
		parentChildMap = new HashMap<String, String>();

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
	public String processWorkflowStructure(String content) {

		// this flag is set to prevent processing of separate workflowProvenanceItems that describe nestd workflows.
		// the processing of all nested workflows is done as part of the very first workflowProvenanceItem that we receive,
		// which is self-consistent. so we ignore all others
		if (workflowStructureDone)  {
//			logger.info("Some nested workflow structure -- moving on");
			return null;
		}

//		System.out.println("ep processing structure");

		// strip the new <workflowItem identifier="57a70081-3d8b-462c-aa35-3b63d7326002">
		// before passing the rest to the deser.
		Element wfStructureRootEl = stripWfInstanceHeader(content); // sets wfInstanceID (the id of this run)

		XMLDeserializerRegistry instance = XMLDeserializerRegistry.getInstance();
		XMLDeserializer deserializer = instance.getDeserializer();

		Dataflow df = null;

//		System.out.println("starting deserialiser");
		try {
			df = deserializer.deserializeDataflow(wfStructureRootEl);
		} catch (DeserializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // original content minus stripped topmost workflow element
//		System.out.println("deserialised");

		workflowStructureDone = true;

		topLevelDataflowName = df.getLocalName();
		topLevelDataflowID = df.getInternalIdentier();

		// check whether we already have this WF in the DB
		List<String> wfNames = null;
		try {
			wfNames = pq.getAllWFnames();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			pw.addProcessor(topLevelDataflowName, DATAFLOW_PROCESSOR_TYPE, topLevelDataflowID);
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
	public String processDataflowStructure(Dataflow df, String dataflowID, String externalName) {

		dataflowDepth++;
		
		try {

			List<Var> vars = new ArrayList<Var>();

			// check whether we already have this WF in the DB
			List<String> wfNames = null;
			try {
				wfNames = pq.getAllWFnames();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				pw.addWFId(dataflowID); // set its dataflowID with no parent

			} else {

				// we are processing a nested workflow structure
//				logger.info("this dataflow is nested within "+parentDataflow);

				pw.addWFId(dataflowID, parentDataflow); // set its dataflowID along with its parent

				// override wfInstanceID to point to top level
				//	wfInstanceID = pq.getWFInstanceID(parentDataflow);

			}

			pw.addWFInstanceId(dataflowID, wfInstanceID);  // wfInstanceID stripped by stripWfInstanceHeader() above


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
				pw.addProcessor(pName, pType, dataflowID);

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

		return dataflowID;
	}



	private Element stripWfInstanceHeader(String content) {

		SAXBuilder  b = new SAXBuilder();
		Document d;

		try {
			d = b.build (new StringReader(content));

			// get identifier from <workflowItem> element
			Element root = d.getRootElement();

			wfInstanceID = root.getAttributeValue("identifier");

			Namespace ns = Namespace.getNamespace("http://taverna.sf.net/2008/xml/t2flow");

			Element workflowEl = root.getChild("workflow", ns);

			return workflowEl;

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return null;
	}

	/**
	 * processes an elementary process execution event from T2. Collects info
	 * from events as they happen and sends them to the writer for processing
	 * when the iteration event is received. Uses the map of procBindings to
	 * process event id and the map of child ids to parent ids to ensure that
	 * the correct proc binding is used
	 * 
	 * @param d
	 */
	public void processProcessEvent(Document d) {

		Element root = d.getRootElement();

//		logger.info("PROCESSING EVENT of type "+root.getName());

		if (root.getName().equalsIgnoreCase("process")) {

			String parentId = root.getAttributeValue("parent");  // this is a the workflowID
			String identifier = root.getAttributeValue("identifier");  // use this as wfInstanceID if this is the top-level process

			parentChildMap.put(identifier, parentId);
			ProcBinding pb = new ProcBinding();
			pb.setExecIDRef(wfInstanceID);  // PM modified
			procBindingMap.put(identifier, pb);

		} else if (root.getName().equalsIgnoreCase("processor")) {

			String identifier = root.getAttributeValue("identifier");
			String parentId = root.getAttributeValue("parent");
			String processID = root.getAttributeValue("processID"); // this is the external process ID

			// this has the weird form facade0:dataflowname:pname  need to extract pname from here
			String[] processName = processID.split(":");
			procBindingMap.get(parentId).setPNameRef(processName[processName.length-1]);  // 3rd component of composite name


			parentChildMap.put(identifier, parentId);

		} else if (root.getName().equalsIgnoreCase("activity")) {

			String identifier = root.getAttributeValue("identifier");
			String parentId = root.getAttributeValue("parent");
			procBindingMap.get(parentChildMap.get(parentId)).setActName(identifier);
			parentChildMap.put(identifier, parentId);

		} else if (root.getName().equalsIgnoreCase("iteration")) {

			// traverse up to root to retrieve ProcBinding that was created when we saw the process event 
			String iterationID = root.getAttributeValue("identifier");
			String activityID = root.getAttributeValue("parent");
			String processorID = parentChildMap.get(activityID);
			String processID = parentChildMap.get(processorID);
			parentChildMap.put(iterationID, activityID);
			parentChildMap.put(iterationID, activityID);
			ProcBinding procBinding = procBindingMap.get(processID);

			String itVector = extractIterationVector(root.getAttributeValue("id"));
			procBinding.setIterationVector(itVector);
			Element inputDataEl = root.getChild("inputdata");
			Element outputDataEl = root.getChild("outputdata");
			processInput(inputDataEl, procBinding);
			processOutput(outputDataEl, procBinding);

			try {
				pw.addProcessorBinding(procBinding);
			} catch (SQLException e) {

//				logger.info("WARNING: provenance has duplicate processor binding -- due to workflow nesting");
				// e.printStackTrace();
			}
		} else if (root.getName().equals("DataflowRunComplete")) {
			
			// use this event to do housekeeping on the input/output varbindings 
//			logger.info("end of workflow processing");
			
			
			//fillInputVarBindings();  // OBSOLETE
			//fillOutputVarBindings();// OBSOLETE 
			
			dataflowDepth--;
			if (dataflowDepth == 0) {
				patchTopLevelnputs();
				workflowStructureDone = false; // CHECK reset for next run... 
			}

		} else if (root.getName().equalsIgnoreCase("workflowdata")) {

//			logger.info("Received workflow data - not processing");
			//FIXME not sure  - needs to be stored somehow

		} else {
			// TODO broken, should we throw something here?
			return;
		}

	}

	/**
	 * fills in the VBs for the global inputs -- for some reason there is no explicit event
	 * that accounts for these value bindings...
	 */
	public void patchTopLevelnputs() {

		// only do the patching on the top level
	//	if (dataflowDepth > 0) {
	//		System.out.println("patchTopLevelnputs called on a subflow -- nothing to do");
	//		return;
	//	}
		// for each input I to topLevelDataflow:
		// pick first outgoing arc with sink P:X
		// copy value X to I -- this can be a collection, so copy everything

		// get all global input vars
		
//		logger.info("\n\n BACKPATCHING GLOBAL INPUTS with dataflowDepth = "+dataflowDepth+"*******\n");

		List<Var> inputs=null;
		try {
			inputs = pq.getInputVars(topLevelDataflowName, topLevelDataflowID, wfInstanceID);

			for (Var input:inputs)  {
				
//				logger.info("global input: "+input.getVName());

				Map<String,String> queryConstraints = new HashMap<String,String>();

				queryConstraints.put("sourceVarNameRef", input.getVName());
				queryConstraints.put("sourcePNameRef", input.getPName());

				List<Arc> outgoingArcs = pq.getArcs(queryConstraints);

				// any arc will do, use the first
				String targetPname = outgoingArcs.get(0).getSinkPnameRef();
				String targetVname = outgoingArcs.get(0).getSinkVarNameRef();

//				logger.info("copying values from ["+targetPname+":"+targetVname+"] for instance ID: ["+wfInstanceID+"]");
				
				queryConstraints.clear();
				queryConstraints.put("varNameRef", targetVname);
				queryConstraints.put("V.pNameRef", targetPname);
				queryConstraints.put("VB.wfInstanceRef", wfInstanceID);
				queryConstraints.put("V.wfInstanceRef", topLevelDataflowID);

				List<VarBinding> VBs = pq.getVarBindings(queryConstraints);
				
//				logger.info("found the following VBs:");
				for (VarBinding vb:VBs) {
//					logger.info(vb.getValue());
	
					// insert VarBinding back into VB with the global input varname
					vb.setPNameRef(input.getPName());
					vb.setVarNameRef(input.getVName());
					pw.addVarBinding(vb);
					
//					logger.info("added");
					
				}
				
			}
		} catch (SQLException e) {
			logger.warn("Patch top level inputs problem for provenance: " + e);
		}

	}


	private void processOutput(Element outputDataEl, ProcBinding procBinding) {
		List<Element> outputPorts = outputDataEl.getChildren("port");
		String iterationVector = outputDataEl.getAttributeValue("id");
		String iteration = outputDataEl.getAttributeValue("parent");
		String activityId = parentChildMap.get(iteration);
		String processor = parentChildMap.get(activityId);
		String process = parentChildMap.get(processor);
		String dataflow = parentChildMap.get(process);

		for (Element outputport : outputPorts) {

			String portName = outputport.getAttributeValue("name");

			// value type may vary
			List<Element> valueElements = outputport.getChildren();
			if (valueElements != null && valueElements.size() > 0) {

				Element valueEl = valueElements.get(0); // only really 1 child
//				processVarBinding(valueEl, processor, portName, iterationVector,
//				dataflow);

				processVarBinding(valueEl,  procBinding.getPNameRef(), portName, procBinding.getIterationVector(),
						wfInstanceID);
			}
		}

	}

	private void processInput(Element inputDataEl, ProcBinding procBinding) {

		List<Element> inputPorts = inputDataEl.getChildren("port");
		String iterationVector = inputDataEl.getAttributeValue("id");

		// not sure any of this is needed
		String iteration = inputDataEl.getAttributeValue("parent");
		String activityId = parentChildMap.get(iteration); // activity
		String processor = parentChildMap.get(activityId);
		String process = parentChildMap.get(processor);
		String dataflow = parentChildMap.get(process);

		for (Element inputport : inputPorts) {

			String portName = inputport.getAttributeValue("name");

			// value type may vary
			List<Element> valueElements = inputport.getChildren(); // hopefully
			// in the
			// right
			// order...
			if (valueElements != null && valueElements.size() > 0) {

				Element valueEl = valueElements.get(0); // expect only 1 child
//				processVarBinding(valueEl, processor, portName, iterationVector,
//				dataflow);

				processVarBinding(valueEl, procBinding.getPNameRef(), portName, procBinding.getIterationVector(),
						wfInstanceID);
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
	 */
	private void processVarBinding(Element valueEl, String processorId,
			String portName, String iterationId, String wfInstanceRef) {

		// uses the defaults:
		// collIdRef = null
		// parentcollectionRef = null
		// positionInCollection = 1
		processVarBinding(valueEl, processorId, portName, null, 1, null,
				iterationId, wfInstanceRef);
	}

	private void processVarBinding(Element valueEl, String processorId,
			String portName, String collIdRef, int positionInCollection,
			String parentCollectionRef, String iterationId, String wfInstanceRef) {

		String valueType = valueEl.getName();
//		logger.info("value element for " + processorId + ": "
//				+ valueType);

		String iterationVector = extractIterationVector(iterationId);

		VarBinding vb = new VarBinding();

		vb.setWfInstanceRef(wfInstanceRef);
		vb.setPNameRef(processorId);
		vb.setValueType(valueType);
		vb.setVarNameRef(portName);
		vb.setCollIDRef(collIdRef);
		vb.setPositionInColl(positionInCollection);
		vb.setIterationVector(iterationVector);

		if (valueType.equals("literal")) {

//			logger.info("processing literal value");
			try {

				vb.setValue(valueEl.getAttributeValue("id"));

				pw.addVarBinding(vb);

			} catch (SQLException e) {
//				logger.info("Process Var Binding problem with provenance" + e.getMessage());
			}

		} else if (valueType.equals("referenceSet")) {

			String refValue = null;
			String ref = null; // used for non-literal values that need
			// de-referencing

//			logger.info("processing dataDocument value");
			vb.setValue(valueEl.getAttributeValue("id"));
			vb.setRef(valueEl.getChildText("reference"));

			try {
				pw.addVarBinding(vb);
			} catch (SQLException e) {
//				logger.warn("Problem processing var binding: " + e);
			}

		} else if (valueType.equals("list")) {

			// add entries to the Collection and to the VarBinding tables
			// list id --> Collection.collId

//			logger.info("processing list value");
			String collId = valueEl.getAttributeValue("id");
			try {

				parentCollectionRef = pw.addCollection(processorId, collId,
						parentCollectionRef, iterationVector, portName,
						wfInstanceRef);

				// iterate over each list element
				List<Element> listElements = valueEl.getChildren();

				positionInCollection = 1;
				// children can be any base type, including list itself -- so
				// use recursion
				for (Element el : listElements) {
					processVarBinding(el, processorId, portName, collId,
							positionInCollection, parentCollectionRef,
							iterationId, wfInstanceRef);
					positionInCollection++;
				}

			} catch (SQLException e) {
//				logger.warn("Problem processing var binding: " + e);
			}
		} else {
			logger.info("unrecognized value type element for "
					+ processorId + ": " + valueType);
		}

	}

	/**
	 * dummy impl -- waiting for T2 to provide the real ID as part of the event
	 * message
	 * 
	 * @param root
	 * @return
	 */
	public String getWorkflowID(Element root) {

		Element nameEl = root.getChild("name");

		if (nameEl != null)
			return nameEl.getText();

		else
			return "N/A";

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
	 * assume content is XML but this is really immaterial
	 * 
	 * @param content
	 * @param eventType
	 * @throws IOException
	 */
	public void saveEvent(String content, String eventType) throws IOException {

		// only save iteration events
//		if (!eventType.equals("iteration")) return;
		
		// URL resource =
		// getClass().getClassLoader().getResource(TEST_EVENTS_FOLDER);
		File f1 = null;

		f1 = new File(TEST_EVENTS_FOLDER);
		FileUtils.forceMkdir(f1);

		String fname = "event_" + eventCnt++ + "_" + eventType + ".xml";
		File f = new File(f1, fname);

//		System.out.println("saving to " + f); // save event for later inspection

		FileWriter fw = new FileWriter(f);
		fw.write(content);
		fw.flush();
		fw.close();

//		System.out.println("saved as file " + fname);
	}

	/**
	 * for each arc of the form (_INPUT_/I, P/V): propagate VarBinding for P/V
	 * to var _INPUT_/I <br/>
	 * 
	 * @throws SQLException
	 */
	public void fillInputVarBindings() throws SQLException {

		// System.out.println("*** fillInputVarBindings: ***");

		// retrieve appropriate arcs
		Map<String, String> constraints = new HashMap<String, String>();
		constraints.put("sourcePnameRef", "_INPUT_");
		constraints.put("W.instanceID", wfInstanceID);
		List<Arc> arcs = pq.getArcs(constraints);

		// backpropagate VarBinding from the target var of the arc to the source
		for (Arc aArc : arcs) {

//				logger.info("propagating VarBinding from ["
//			+ aArc.getSinkPnameRef() + "/" + aArc.getSinkVarNameRef()
//			+ "] to input [" + aArc.getSourcePnameRef() + "/"
//			+ aArc.getSourceVarNameRef() + "]");

			// get the varBinding for the arc sinks
			Map<String, String> vbConstraints = new HashMap<String, String>();
			vbConstraints.put("VB.PNameRef", aArc.getSinkPnameRef());
			vbConstraints.put("VB.varNameRef", aArc.getSinkVarNameRef());
			vbConstraints.put("VB.wfInstanceRef", wfInstanceID);

			List<VarBinding> vbList = pq.getVarBindings(vbConstraints); // DB
			// QUERY

			for (VarBinding vb : vbList) {
				// add a new VarBinding for the input

				vb.setPNameRef(aArc.getSourcePnameRef());
				vb.setVarNameRef(aArc.getSourceVarNameRef());
				// all other attributes are the same --> CHECK!!

				pw.addVarBinding(vb);
			}
		}
	}

	/**
	 * for each arc of the form (P/V, _OUTPUT_/O): propagate VarBinding for P/V
	 * to var _OUTPUT_/O <br/>
	 * 
	 * @throws SQLException
	 */
	public void fillOutputVarBindings() throws SQLException {

		//System.out.println("*** fillOutputVarBindings: ***");

		// retrieve appropriate arcs
		Map<String, String> constraints = new HashMap<String, String>();
		constraints.put("sinkPnameRef", "_OUTPUT_");
		constraints.put("wfInstanceRef", wfInstanceID);
		List<Arc> arcs = pq.getArcs(constraints);

		// fowd propagate VarBinding from the source var of the arc to the
		// output
		for (Arc aArc : arcs) {

//				logger.info("fwd propagating VarBinding from ["
//			+ aArc.getSourcePnameRef() + "/"
//			+ aArc.getSourceVarNameRef() + "] to input ["
//			+ aArc.getSinkPnameRef() + "/" + aArc.getSinkVarNameRef()
//			+ "]");

			// get the varBinding for the arc sinks
			Map<String, String> vbConstraints = new HashMap<String, String>();
			vbConstraints.put("VB.PNameRef", aArc.getSourcePnameRef());
			vbConstraints.put("VB.varNameRef", aArc.getSourceVarNameRef());
			vbConstraints.put("VB.wfInstanceRef", wfInstanceID);

			List<VarBinding> vbList = pq.getVarBindings(vbConstraints); // DB
			// QUERY

			for (VarBinding vb : vbList) {
				// add a new VarBinding for the input

				//		System.out.println("found binding to propagate:");
//				System.out
//				.println(vb.getPNameRef() + "/" + vb.getVarNameRef()
//				+ "/" + vb.getWfInstanceRef() + "/"
//				+ vb.getIteration());

//				vb.setPNameRef(aArc.getSinkPnameRef());
//				vb.setVarNameRef(aArc.getSinkVarNameRef());
				// all other attributes are the same --> CHECK!!

				//		System.out.println(vb.toString());

				pw.addVarBinding(vb); // DB UPDATE
			}

		}
	}



	/**
	 * recursively propagates anl() through the graph, using a toposort alg 
	 * on each nested workflow, starting with the top level dataflow
	 * 
	 * 
	 * @throws SQLException
	 */
	public void propagateANL(String wfInstanceRef) throws SQLException {

		// propagate through 1 level of processors, ignore nesting. A subworkflow here is
		// simply a processor
		Map<String, Integer> processors = propagateANLWithinSubflow(wfInstanceRef);

		// now fetch all children workflows and recurse
		List<String> children = pq.getChildrenOfWorkflow(wfInstanceRef);

		for (String childWFName: children) {			
			propagateANL(childWFName); // CHECK				
		}

//		// is any of the processors at this level a dataflow itself?
//		// if so, propagateANL on this as well -- note how this is recursive
//		for (String pname: processors.keySet()) {

//		List<ProvenanceProcessor> procList = pq.getProcessors(DATAFLOW_PROCESSOR_TYPE, wfInstanceRef);

//		for (ProvenanceProcessor dataflowProcessor: procList) { 

//		// because this processor is a workflow itself, it is in the Workflow table as well
//		// so we 
//		}
//		}
	}


	/**
	 * propagates anl() through the graph, using a toposort alg
	 * 
	 * @param wfInstanceRef the static wfNameRef of the dataflow whose processors we need to sort 
	 * @throws SQLException
	 * @return a list of processors that are immediately contained within wfInstanceRef. This is used by caller to recurse on 
	 * sub-workflows
	 */
	public Map<String, Integer> propagateANLWithinSubflow(String wfInstanceRef) throws SQLException {

		// //////////////////////
		// PHASE I: toposort the processors in the whole graph
		// //////////////////////

		// fetch processors along with the count of their predecessors
		Map<String, Integer> processorsLinks = pq.getProcessorsIncomingLinks(wfInstanceRef);

		// holds sorted elements
		List<String> L = new ArrayList<String>();

		// temp queue
		List<String> Q = new ArrayList<String>();

		// System.out.println("propagateANL: processors in the graph");

		// init Q with root nodes
		for (Map.Entry<String, Integer> entry : processorsLinks.entrySet()) {

			// System.out.println(entry.getKey()+" has "+entry.getValue().intValue()+" predecessors");

			if (entry.getValue().intValue() == 0) {
				Q.add(entry.getKey());
			}
		}

		while (!Q.isEmpty()) {

			String current = Q.remove(0);
			L.add(current);

			List<String> successors = pq.getSuccProcessors(current,
					wfInstanceRef);

			for (String succ : successors) {
				// decrease edge count for each successor processor

				Integer cnt = processorsLinks.get(succ);

				processorsLinks.put(succ, new Integer(cnt.intValue() - 1));

				if (cnt.intValue() == 1) {
					Q.add(succ);
				}
			}
		} // end loop on Q

//		System.out.println("toposort:");
//		for (String p : L) {
//		System.out.println(p);
//		}

		// sorted processor names in L at this point
		// process them in order
		for (String pname : L) {

			// process pname's inputs -- set ANL to be the DNL if not set in
			// prior steps
			List<Var> inputs = pq.getInputVars(pname, wfInstanceRef, null); // null -> do not use instance

			int totalANL = 0;
			for (Var iv : inputs) {
				if (iv.isANLset() == false) {
					iv.setActualNestingLevel(iv.getTypeNestingLevel());
					iv.setANLset(true);
					pq.updateVar(iv);
				}

				int delta_nl = iv.getActualNestingLevel() - iv.getTypeNestingLevel();

				// if delta_nl < 0 then Taverna wraps the value into a list --> use dnl(X) in this case
				if (delta_nl < 0 ) delta_nl = iv.getTypeNestingLevel();

				totalANL += delta_nl;
			}

			// process pname's outputs -- set ANL based on the sum formula (see
			// paper)
			List<Var> outputs = pq.getOutputVars(pname, wfInstanceRef);
			for (Var ov : outputs) {

				ov.setActualNestingLevel(ov.getTypeNestingLevel() + totalANL);
				ov.setANLset(true);
				pq.updateVar(ov);

				// propagate this through all the links from this var
				List<Var> successors = pq.getSuccVars(pname, ov.getVName(),
						wfInstanceRef);

				for (Var v : successors) {
					v.setActualNestingLevel(ov.getActualNestingLevel());
					v.setANLset(true);
					pq.updateVar(v);
				}
			}
		}
		return processorsLinks;
	}

}
