/**
 * 
 */
package net.sf.taverna.t2.provenance.api;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.provenance.lineageservice.utils.QueryVar;
import net.sf.taverna.t2.provenance.lineageservice.utils.Var;
import net.sf.taverna.t2.provenance.lineageservice.utils.WorkflowInstance;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author paolo
 *
 */
public class ProvenanceQueryParser {

	private static Logger logger = Logger.getLogger(ProvenanceQueryParser.class);

	private static final String RUNS_EL = "runs";
	private static final String RUN_ID = "id";
	private static final Object RUN_EL = "run";
	private static final Object RANGE_EL = "range";
	private static final String RANGE_FROM = "from";
	private static final String RANGE_TO = "to";

	private static final String PORT_SELECTION_EL = "portSelection";
	private static final String WORKFLOW_NAME_ATTR = "externalName";
	private static final String PORT_EL = "port";
	private static final String WORKFLOW_EL = "workflow";
	private static final String PROCESSOR_EL = "processor";
	private static final String PORT_NAME_ATTR = "name";
	private static final String PROC_NAME_ATTR = "name";
	private static final String PORT_NAME_EL = "name";

	private static final String PROC_FOCUS_EL = "processorFocus";

	private ProvenanceAccess pAccess;

	private String mainWorkflowExternalName = null;
	private String mainWorkflowID = null;

	/**
	 * 
	 * @param XMLQuerySpec A string representation of the XML provenace query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Query parseProvenanceQuery(String XMLQuerySpecFilename) {

		Query q = new Query();

		Document d=null; 

		// parse the XML using JDOM
		// new code to read off an XML spec of the query
		SAXBuilder  b = new SAXBuilder();
		try {
			d = b.build (new FileReader((XMLQuerySpecFilename)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		q.setRunIDList(parseWorkflowAndRuns(d));  // sets the set of runs 
		q.setTargetVars(parsePortSelection(d));

		return q;
	}


	/**
	 * processor the <processorFocus> section of the query spec
	 * @param d
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ProvenanceProcessor> parseProcessorFocus(Document d) {

		List<ProvenanceProcessor> selectedProcessors = new ArrayList<ProvenanceProcessor>();

		Element root = d.getRootElement();
		Element processorFocusEl = root.getChild(PROC_FOCUS_EL);

		if (processorFocusEl == null) {  // completely implicit: set to output ports of topLevelWorkflowID
			return processProcessorFocus(mainWorkflowID, null);
		}

		logger.debug("setting explicit processor focus");

		// expect a sequence of a mix of PROCESSOR or WORKFLOW elements
		List<Element> children = processorFocusEl.getChildren();
		for (Element childEl: children) {
			logger.debug("processing element "+childEl.getName());
			if (childEl.getName().equals(WORKFLOW_EL)) { // 
				logger.debug("processorFocus>workflow");  // set new workflow scope
				selectedProcessors.addAll(processWorkflowFocus(childEl));
			} else if (childEl.getName().equals(PROCESSOR_EL)) { // ports within this processor
				selectedProcessors.addAll(processProcessorFocus(mainWorkflowID, childEl));
			}
		}
		return selectedProcessors;
	}



	/**
	 * here we are processing <processor> within <workflow> within <processorFocus>
	 */
	private List<ProvenanceProcessor> processProcessorFocus(String workflowID, Element childEl) {

		String processorNameScope = childEl.getAttributeValue(PROC_NAME_ATTR);
		if (processorNameScope == null) {
			logger.warn("no processor name found in <processor> tag");
			return null;
		}

		// get the ProvenanceProcessor object within the current scope

		// this gets a map workflowId -> [ProvenanceProcessor] for all workflows nested within the top workflowID
		Map<String, List<ProvenanceProcessor>> allProcessors = pAccess.getProcessorsInWorkflow(workflowID);

		List<ProvenanceProcessor> myProcs = allProcessors.get(workflowID);  // processors for this specific workflow
		for (ProvenanceProcessor pp:myProcs) {
			if (processorNameScope.equals(pp.getPname())) {
				List<ProvenanceProcessor> ppList = new ArrayList<ProvenanceProcessor>();
				ppList.add(pp);
				return ppList;
			}
		}
		return null;
	}


	/**
	 * here we are parsing <workflow> inside <processorFocus>
	 * @param childEl a <workflow> element
	 * @return
	 */
	private Collection<? extends ProvenanceProcessor> processWorkflowFocus(
			Element childEl) {

		List<ProvenanceProcessor> processors = new ArrayList<ProvenanceProcessor>();

		String workflowNameScope = childEl.getAttributeValue(WORKFLOW_NAME_ATTR);
		if (workflowNameScope == null) {
			logger.warn("no workflow name found in <workflow> tag");
			return null;
		}
		String workflowIDScope = pAccess.getWorkflowIDForExternalName(workflowNameScope);

		List<Element> children = childEl.getChildren();  // expect <processor> elements
		for (Element portEl:children) {
			processors.addAll(processProcessorFocus(workflowNameScope, childEl));
		}
		return processors;		
	}



	@SuppressWarnings("unchecked")
	private List<QueryVar> parsePorts(String workflowID, String procName, Element childEl) {

		List<QueryVar>  queryVars = new ArrayList<QueryVar>();
		List<String> portNames = new ArrayList<String>();

		List<Var> ports = pAccess.getPortsForProcessor(workflowID, procName);

		boolean allPorts = false;
		if (childEl == null) {  allPorts = true; }

		List<Element> children = childEl.getChildren();
		for (Element portEl:children) {
			portNames.add(portEl.getAttributeValue(PORT_NAME_ATTR));			
		}

		for (Var p:ports) {
			if (allPorts || portNames.contains(p.getVName())) {					
				QueryVar qv = new QueryVar();
				qv.setWfName(p.getWfInstanceRef());
				qv.setPname(p.getPName());
				qv.setVname(p.getVName());
				// any other field to set???
				queryVars.add(qv);
			}
		}
		return queryVars;
	}


	private List<QueryVar> parseProcessor(String workflowID, Element childEl) {
		String procName = childEl.getAttributeValue(PROC_NAME_ATTR);
		logger.debug("portSelection>processor");

		return parsePorts(workflowID, procName, childEl);  		// parse all ports within this processor		
	}


	private List<QueryVar> parseWorkflow(Element workflowEl) {

		List<QueryVar>  queryVars = new ArrayList<QueryVar>();

		String workflowNameScope = workflowEl.getAttributeValue(WORKFLOW_NAME_ATTR);
		String workflowIDScope = pAccess.getWorkflowIDForExternalName(workflowNameScope);

		// expect nested processor elements
		List<Element> children = workflowEl.getChildren();
		for (Element childEl:children) {
			if (childEl.getName().equals(PROCESSOR_EL)) {
				queryVars.addAll(parseProcessor(workflowIDScope, childEl));
			}
		}
		return queryVars;
	}



	/**
	 * the scope for a query can be partially specified. Please see doc elsewhere
	 * @param d
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<QueryVar> parsePortSelection(Document d) {

		List<QueryVar>  queryVars = new ArrayList<QueryVar>();
		Element root = d.getRootElement();
		Element portSelection = root.getChild(PORT_SELECTION_EL);

		if (portSelection == null) {  // completely implicit: set to output ports of topLevelWorkflowID
			return parsePorts(mainWorkflowID, mainWorkflowExternalName, null);
		}

		logger.debug("setting explicit port selections");

		// expect a sequence of a mix of PORT elements or PROCESSOR or WORKFLOW elements
		List<Element> children = portSelection.getChildren();
		for (Element childEl: children) {
			logger.debug("processing element "+childEl.getName());

			if (childEl.getName().equals(WORKFLOW_EL)) { // 
				logger.debug("portSelection>workflow");  // set new workflow scope
				queryVars.addAll(parseWorkflow(childEl));
			} else if (childEl.getName().equals(PROCESSOR_EL)) { // ports within this processor
				queryVars.addAll(parseProcessor(mainWorkflowID, childEl));
			} else if (childEl.getName().equals(PORT_EL)) { // ports within this processor
				queryVars.addAll(parsePorts(mainWorkflowID, mainWorkflowExternalName, childEl));
			}
		}
		return queryVars;
	}



	/**
	 * process runs scope, with the constraint that all the runs refer to the same (top level) workflow: queries over multiple workflows 
	 * are not supported.
	 * @param d the XML Document describing the query 
	 * @return
	 */	
	@SuppressWarnings("unchecked")
	private List<String> parseWorkflowAndRuns(Document d) {

		List<String> runsScope = new ArrayList<String>();  // list of run IDs
		List<WorkflowInstance> feasibleWfInstances = new ArrayList<WorkflowInstance>();
		List<String> feasibleRuns = new ArrayList<String>();

		Element root = d.getRootElement();
		Element workflowEl = root.getChild(WORKFLOW_EL);
		if (workflowEl != null) {
			mainWorkflowExternalName = workflowEl.getAttributeValue(WORKFLOW_NAME_ATTR);

			if (mainWorkflowExternalName == null) {
				logger.debug("no external name specified in workflow - giving up");
				return null;
			}

			//  validate this workflowID
			List<WorkflowInstance>  allWfInstances = pAccess.listRuns(null, null); // returns all available runs ordered by timestamp
			// is this workflow in one of the instances?

			for (WorkflowInstance i:allWfInstances) {
				if (mainWorkflowExternalName.equals(i.getWorkflowExternalName())) {
					mainWorkflowID = i.getWorkflowIdentifier();
					logger.debug("workflow name found corresponding to ID "+mainWorkflowID);
					feasibleWfInstances.add(i);
					feasibleRuns.add(i.getInstanceID());
				}
			}
		} else {
			logger.fatal("no top-level <workflow> tag found, giving up");
			return null;
		}
		if (feasibleWfInstances.size() == 0) {
			logger.debug("workflow "+mainWorkflowExternalName+" not found -- giving up");
			return null;
		}

		// get into the element and set the runs scope.
		Element runs = workflowEl.getChild(RUNS_EL);
		if (runs != null) {
			logger.debug("setting explicit runs scope");

			// expect a sequence of run elements and/ or a single <range> element
			List<Element> runElList = runs.getChildren();
			for (Element runEl:runElList) {

				// explicit runID given
				if (runEl.getName().equals(RUN_EL)) {
					String runID = runEl.getAttributeValue(RUN_ID);
					if (runID!=null) {
						if (feasibleRuns.contains(runID)) {
							logger.debug("adding runID "+runID+" to runs scope");
							runsScope.add(runID);
						} else {
							logger.debug("selected runID "+runID+" not in provenance DB -- ignored");
						}
					} else {
						logger.warn("<run> element with no ID");
					}					

					// time range given 
				} else if (runEl.getName().equals(RANGE_EL)) {
					String from = runEl.getAttributeValue(RANGE_FROM);
					String to   = runEl.getAttributeValue(RANGE_TO);

					logger.debug("processing runs range from "+from+" to "+to);

					for (WorkflowInstance i:feasibleWfInstances) {

						DateFormat f = new SimpleDateFormat();
						Date fromDate;
						try {
							fromDate = f.parse(from);
							Date fromInstanceDate = f.parse(i.getTimestamp());
							Date toDate = f.parse(to);
							Date toInstanceDate = f.parse(i.getTimestamp());
							if (fromDate == null || (fromDate != null && fromDate.before(fromInstanceDate))) {
								if (toDate == null || (toDate != null && toInstanceDate.before(toDate)));
								runsScope.add(i.getInstanceID());
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}


//					Map<String, String> conds  = new java.util.HashMap<String, String>();
//					conds.put("from", from);
//					conds.put("to", to);
//					List<WorkflowInstance> rangeWfInstances = pAccess.listRuns(null, conds);
//					for (WorkflowInstance i:rangeWfInstances) { runsScope.add(i.getInstanceID()); }
				}
			}
		} else {
			// no explicit run:  using latest from feasible
			logger.debug("null runs scope: using latest run");
			if (feasibleWfInstances != null) runsScope.add(feasibleWfInstances.get(0).getInstanceID());
		}

		logger.debug("runs scope:");
		for (String r:runsScope) logger.debug(r);

		return runsScope;
	}



	/**
	 * @return the provenance Access object
	 */
	public ProvenanceAccess getPAccess() {
		return pAccess;
	}

	/**
	 * @param access the pAccess to set
	 */
	public void setPAccess(ProvenanceAccess access) {
		pAccess = access;
	}
}
