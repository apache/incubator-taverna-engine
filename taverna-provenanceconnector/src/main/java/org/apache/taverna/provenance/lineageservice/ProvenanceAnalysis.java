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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.provenance.api.NativeAnswer;
import org.apache.taverna.provenance.api.QueryAnswer;
import org.apache.taverna.provenance.lineageservice.utils.DataLink;
import org.apache.taverna.provenance.lineageservice.utils.Port;
import org.apache.taverna.provenance.lineageservice.utils.PortBinding;
import org.apache.taverna.provenance.lineageservice.utils.ProvenanceProcessor;
import org.apache.taverna.provenance.lineageservice.utils.QueryPort;
import org.apache.taverna.provenance.lineageservice.utils.WorkflowRun;
import org.apache.taverna.provenance.opm.OPMManager;
import org.apache.taverna.reference.T2Reference;

import org.apache.log4j.Logger;
import org.tupeloproject.kernel.OperatorException;
import org.tupeloproject.provenance.ProvenanceArtifact;
import org.tupeloproject.provenance.ProvenanceException;
import org.tupeloproject.provenance.ProvenanceRole;

/**
 * The main class for querying the lineage DB.
 * Assumes a provenance DB ready to be queried.
 * @author paolo
 */
public class ProvenanceAnalysis {

	private static Logger logger = Logger.getLogger(ProvenanceAnalysis.class);

	private static final String OUTPUT_CONTAINER_PROCESSOR = "_OUTPUT_";
	public static final String ALL_PATHS_KEYWORD = "ALL";

	private ProvenanceQuery pq = null;
	private AnnotationsLoader al = new AnnotationsLoader();  // FIXME singleton

	// paths collected by lineageQuery and to be used by naive provenance query
	private Map<ProvenanceProcessor, List<List<String>>> validPaths = new HashMap<>();

	private List<String> currentPath;
	private Map<String,List<String>> annotations = null;  // user-made annotations to processors

	private boolean ready = false; // set to true as soon as init succeeds. this means pa is ready to answer queries

	private boolean returnOutputs = false; // by default only return input bindings

	private boolean includeDataValue = false; // forces the lineage queries to return de-referenced data values

	private boolean generateOPMGraph = true;

	// TODO extract this to prefs -- selects which OPMManager is to be used to export to OPM
	private String OPMManagerClass = "net.sf.taverna.t2.provenance.lineageservice.ext.pc3.PANSTARRSOPMManager";

	private OPMManager aOPMManager = null;

	private boolean recordArtifactValues = false;

	private InvocationContext ic = null;

	public ProvenanceAnalysis() {
	}

	public ProvenanceAnalysis(ProvenanceQuery pq)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		this.pq = pq;
		setReady(tryInit());
	}

	private boolean tryInit() throws SQLException {
		List<?> wris = getWorkflowRunIds();
		if (wris != null && !wris.isEmpty()) {
			initGraph(); // init OPM provenance graph
			return true;
		} else
			return false;
	}

	/**
	 * Call to create the opm graph and annotation loader. this may fail due to
	 * queries being issued before DB is populated, minimally with workflowRunId
	 */
	public void initGraph() {
		// OPM management
		try {
			aOPMManager  = (OPMManager) Class.forName(OPMManagerClass).newInstance();
		} catch (InstantiationException e1) {
			logger.error("Problem initialising opm graph: ",  e1);
		} catch (IllegalAccessException e1) {
			logger.error("Problem initialising opm graph: ", e1);
		} catch (ClassNotFoundException e1) {
			logger.info("chosen OPMmanager: "+OPMManagerClass+" not available, reverting to default");
			aOPMManager  = new OPMManager();
		}

		try {
			aOPMManager.createAccount(getWorkflowRunIds().get(0).getWorkflowRunId());
		} catch (SQLException e) {
			logger.error("Could not initialise OPM Manager: ", e);
		} catch (ProvenanceException e) {
			logger.warn("Could not add account", e);
		}
	}


	/**
	 * asks the OPM manager to convert its current RDF OPMGraph to XML
	 * @return the filename of the OPM XML file
	 * @throws OperatorException
	 * @throws IOException
	 * @throws JAXBException
	 */
	//	public String OPMRdf2Xml() throws OperatorException, IOException, JAXBException {
	//	if (isReady()) {
	//	return aOPMManager.Rdf2Xml();
	//	}
	//	return null;
	//	}

	/**
	 * asks the OPM manager to create a dot file representing its current RDF OPMGraph<br/>
	 * needs fixing
	 * @return
	 * @throws IOException
	 * @throws OperatorException
	 */
	/*
	public String OPMRdf2Dot() throws OperatorException, IOException {
		if (aOPMManager != null && aOPMManager.isActive() && isReady()) {
			return aOPMManager.Rdf2Dot();
		}
		return null;
	}
	 */

	public void setAnnotationFile(String annotationFile) {
		annotations = al.getAnnotations(annotationFile);
		if (annotations == null) {
			logger.warn("no annotations have been loaded");
			return;
		}

		logger.info("processor annotations for lineage refinement: ");
		for (Map.Entry<String, List<String>> entry : annotations.entrySet()) {
			logger.info("annotations for proc " + entry.getKey());
			for (String ann : entry.getValue())
				logger.info(ann);
		}
	}

	/**
	 * returns all available instances across all workflows
	 *
	 * @return
	 * @throws SQLException
	 */
	public List<WorkflowRun> getWorkflowRunIds() throws SQLException {
		return getPq().getRuns(null, null);
	}

	/**
	 * returns all available instances for workflow workflowId
	 *
	 * @param workflowId
	 * @return
	 * @throws SQLException
	 */
	public List<WorkflowRun> getWorkflowRunsForWorkflow(String workflowId)
			throws SQLException {
		return getPq().getRuns(workflowId, null);
	}

	/**
	 * @param workflowRun
	 *            lineage scope -- a specific instance
	 * @param pname
	 *            for a specific processor [required]
	 * @param a
	 *            specific (input or output) variable [optional]
	 * @param iteration
	 *            and a specific iteration [optional]
	 * @param workflowId
	 * @return a lineage query ready to be executed, or null if we cannot return
	 *         an answer because we are not ready (for instance the DB is not
	 *         yet populated)
	 * @throws SQLException
	 */
	public Dependencies fetchIntermediateResult(String workflowRun,
			String workflowId, String pname, String vname, String iteration)
			throws SQLException {
		if (!isReady()) {
			setReady(tryInit());
			if (!isReady())
				return null;
		}

		LineageSQLQuery lq = getPq().simpleLineageQuery(workflowRun,
				workflowId, pname, vname, iteration);

		return getPq().runLineageQuery(lq, isIncludeDataValue());
	}

	public QueryAnswer lineageQuery(List<QueryPort> qvList, String workflowRun,
			List<ProvenanceProcessor> selectedProcessors) throws SQLException {
		QueryAnswer completeAnswer = new QueryAnswer();
		NativeAnswer nativeAnswer = new NativeAnswer();

		Map<QueryPort, Map<String, List<Dependencies>>> answerContent = new HashMap<>();

		// launch a lineage query for each target variable
		for (QueryPort qv : qvList) {

			// full lineage query
			logger.info(String.format(
					"************\n lineage query: [instance, workflow, proc, port, path] = "
							+ "[%s,%s,%s,%s,[%s]]\n***********", workflowRun,
					qv.getWorkflowId(), qv.getProcessorName(),
					qv.getPortName(), qv.getPath()));

			// the OPM manager builds an OPM graph behind the scenes as a side-effect
			Map<String, List<Dependencies>> a = computeLineageSingleVar(
					workflowRun, qv.getWorkflowId(), qv.getPortName(),
					qv.getProcessorName(), qv.getPath(), selectedProcessors);

			answerContent.put(qv, a);
		}

		nativeAnswer.setAnswer(answerContent);
		completeAnswer.setNativeAnswer(nativeAnswer);

		if (aOPMManager != null && aOPMManager.isActive()) {
			//			String _OPM_asXML_File;
			//			try {

			//			_OPM_asXML_File = aOPMManager.Rdf2Xml();
			String _OPM_asRDF_File = aOPMManager.writeGraph();

			completeAnswer.setOPMAnswer_AsRDF(_OPM_asRDF_File);
			//			completeAnswer.setOPMAnswer_AsXML(_OPM_asXML_File);

			//			} catch (OperatorException e) {
			//			logger.error("Problem running query: " + e);
			//			} catch (IOException e) {
			//			logger.error("Problem running query: " + e);
			//			} catch (JAXBException e) {
			//			logger.error("Problem running query: " + e);
			//			}
		}
		return completeAnswer;
	}

	/**
	 * facade for computeLineage: if path == ALL then it retrieves all VBs for
	 * (proc,var) ignoring path (i.e., all values within the collection bound to
	 * var) and invokes computeLineageSingleBinding() on each path</br> if path
	 * is specified, however, this just passes the request to
	 * computeLineageSingleBinding. in this case the result map only contains
	 * one entry
	 *
	 * @param workflowRun
	 * @param var
	 * @param proc
	 * @param path
	 * @param string
	 * @param selectedProcessors
	 * @return a map <tt>{ path -> List&lt;LineageQueryResult&gt; }</tt>, one entry for each path
	 * @throws SQLException
	 */
	public Map<String, List<Dependencies>> computeLineageSingleVar(
			String workflowRun,   // dynamic scope
			String workflowId,    // static scope
			String var,   // target var
			String proc,   // qualified with its processor name
			String path,   // possibly empty when no collections or no granular lineage required
			List<ProvenanceProcessor> selectedProcessors) throws SQLException {
		if (!isReady()) {
			setReady(tryInit());
			if (!isReady())
				return null;
		}

		// are we returning all outputs in addition to the inputs?
		logger.debug("return outputs: " + isReturnOutputs());

		Map<String, List<Dependencies>> qa = new HashMap<>();

		// run a query for each variable in the entire workflow graph
		if (path.equals(ALL_PATHS_KEYWORD)) {
			Map<String, String> vbConstraints = new HashMap<>();
			vbConstraints.put("VB.processorNameRef", proc);
			vbConstraints.put("VB.portName", var);
			vbConstraints.put("VB.workflowRunId", workflowRun);

			List<PortBinding> vbList = getPq().getPortBindings(vbConstraints); // DB

			if (vbList.isEmpty())
				logger.warn(ALL_PATHS_KEYWORD
						+ " specified for paths but no varBindings found. nothing to compute");

			for (PortBinding vb : vbList) {
				// path is of the form [x,y..] we need it as x,y...
				path = vb.getIteration().substring(1,
						vb.getIteration().length() - 1);

				List<Dependencies> result = computeLineageSingleBinding(
						workflowRun, workflowId, var, proc, path,
						selectedProcessors);
				qa.put(vb.getIteration(), result);
			}
		} else {
			qa.put(path,
					computeLineageSingleBinding(workflowRun, workflowId, var,
							proc, path, selectedProcessors));
		}
		return qa;
	}

	/**
	 * main lineage query method. queries the provenance DB with a single
	 * originating proc/var/path and a set of selected Processors
	 *
	 * @param workflowRunId
	 * @param var
	 * @param proc
	 * @param path
	 * @param path2
	 * @param selectedProcessors
	 * @return a list of bindings. each binding involves an input var for one of
	 *         the selectedProcessors. Note each var can contribute multiple
	 *         bindings, i.e., when all elements in a collection bound to the
	 *         var are retrieved. Note also that bindings for input vars are
	 *         returned as well, when the query is configured with returnOutputs
	 *         = true {@link ProvenanceAnalysis#isReturnOutputs() }
	 * @throws SQLException
	 */
	public List<Dependencies> computeLineageSingleBinding(
			String workflowRunId,   // dynamic scope
			String workflowId,  // static scope
			String var,   // target var
			String proc,   // qualified with its processor name
			String path,   // possibly empty when no collections or no granular lineage required
			List<ProvenanceProcessor> selectedProcessors) throws SQLException {
		long start = System.currentTimeMillis();
		List<LineageSQLQuery> lqList = searchDataflowGraph(workflowRunId,
				workflowId, var, proc, path, selectedProcessors);
		long stop = System.currentTimeMillis();

		long gst = stop - start;

		// execute queries in the LineageSQLQuery list
		logger.debug("\n****************  executing lineage queries:  (includeDataValue is "
				+ isIncludeDataValue() + "**************\n");

		start = System.currentTimeMillis();
		List<Dependencies> results = getPq().runLineageQueries(lqList,
				isIncludeDataValue());
		stop = System.currentTimeMillis();

		long qrt = stop - start;
		logger.debug("search time: " + gst
				+ "ms\nlineage query response time: " + qrt + " ms");
		logger.debug("total exec time " + (gst + qrt) + "ms");

		return results;
	}

	/**
	 * compute lineage queries using path projections
	 * @param workflowRunId the (single) instance defines the scope of a query<br/>
	 * added 2/9: collect a list of paths in the process to be used by the naive query. In practice
	 * we use this as the graph search phase that is needed by the naive query anyway
	 * @param var
	 * @param proc
	 * @param path  within var (can be empty but not null)
	 * @param selectedProcessors pairs (wfID, proceName), encoded as a Map. only report lineage when you reach any of these processors
	 * @throws SQLException
	 */
	public List<LineageSQLQuery> searchDataflowGraph(
			String workflowRunId,   // dymamic scope
			String workflowId,  // static scope
			String var,   // target var
			String proc,   // qualified with its processor name
			String path,  // can be empty but not null
			List<ProvenanceProcessor> selectedProcessors) throws SQLException {
		List<LineageSQLQuery> lqList = new ArrayList<>();

		// TODO we are ignoring the wfId context information in the list of selected processors!!

		// init paths accumulation. here "path" is a path in the graph, not within a collection!
		//  associate an empty list of paths to each selected processor
		for (ProvenanceProcessor s : selectedProcessors)
			validPaths.put(s, new ArrayList<List<String>>());

		currentPath = new ArrayList<>();

		// start with xfer or xform depending on whether initial var is output or input

		// get (var, proc) from Port  to see if it's input/output
		Map<String, String> varQueryConstraints = new HashMap<>();
		varQueryConstraints.put("V.processorName", proc);
		varQueryConstraints.put("V.portName", var);
		varQueryConstraints.put("V.workflowId", workflowId);

		List<Port> vars = getPq().getPorts(varQueryConstraints);

		if (vars.isEmpty())  {
			logger.info("variable ("+var+","+proc+") not found, lineage query terminated, constraints: " + varQueryConstraints);
			return null;
		}

		logger.info("Found " + vars);
		Port v = vars.get(0); 		// expect exactly one record
		// CHECK there can be multiple (pname, portName) pairs, i.e., in case of nested workflows
		// here we pick the first that turns up -- we would need to let users choose, or process all of them...

		if (v.isInputPort() || v.getProcessorId() == null) {
			// if vName is input, then do a xfer() step

			// rec. accumulates SQL queries into lqList
			xferStep(workflowRunId, workflowId, v, path, selectedProcessors,
					lqList);
		} else { // start with xform
			// rec. accumulates SQL queries into lqList
			xformStep(workflowRunId, workflowId, v, proc, path,
					selectedProcessors, lqList);
		}

		return lqList;
	}  // end searchDataflowGraph

	/**
	 * accounts for an inverse transformation from one output to all inputs of a
	 * processor
	 *
	 * @param workflowRunId
	 * @param var
	 *            the output var
	 * @param proc
	 *            the processor
	 * @param selectedProcessors
	 *            the processors for which we are interested in producing
	 *            lineage
	 * @param path
	 *            iteration vector within a PortBinding collection
	 * @param lqList
	 *            partial list of spot lineage queries, to be added to
	 * @throws SQLException
	 */
	@SuppressWarnings("deprecation")
	private void xformStep(
			String workflowRunId,
			String workflowId,
			Port outputVar, // we need the dnl from this output var
			String proc, String path,
			List<ProvenanceProcessor> selectedProcessors,
			List<LineageSQLQuery> lqList) throws SQLException {
		// retrieve input vars for current processor
		Map<String, String> varsQueryConstraints = new HashMap<>();

		List<Port> inputVars = null;

		/*
		 * here we fetch the input vars for the current proc. however, it may be
		 * the case that we are looking at a dataflow port (for the entire
		 * dataflow or for a subdataflow) rather than a real processor. in this
		 * case we treat this as a special processor that does nothing -- so we
		 * "input var" in this case is a copy of the port, and we are ready to
		 * go for the next xfer step. in this way we can seamlessly traverse the
		 * graph over intermediate I/O that are part of nested dataflows
		 */

		if (getPq().isDataflow(proc)) { // if we are looking at the output of an entire dataflow
			// force the "input vars" for this step to be the output var itself
			// this causes the following xfer step to trace back to the next processor _within_ proc
			inputVars = new ArrayList<>();
			inputVars.add(outputVar);
		} else if (proc.equals(OUTPUT_CONTAINER_PROCESSOR)) {  // same action as prev case, but may change in the future
			inputVars = new ArrayList<>();
			inputVars.add(outputVar);
		} else {
			varsQueryConstraints.put("W.workflowId", workflowId);
			varsQueryConstraints.put("processorName", proc);
			varsQueryConstraints.put("isInputPort", "1");

			inputVars = getPq().getPorts(varsQueryConstraints);
		}

		///////////
		/// path projections
		///////////
		// maps each var to its projected path
		Map<Port,String> var2Path = new HashMap<>();
		Map<Port,Integer> var2delta = new HashMap<>();

		if (path == null) {  // nothing to split
			for (Port inputVar : inputVars)
				var2Path.put(inputVar, null);
		} else {
			int minPathLength = 0;  // if input path is shorter than this we give up granularity altogether
			for (Port inputVar : inputVars) {
				int resolvedDepth = 0;
				if (inputVar.getResolvedDepth() != null)
					resolvedDepth = inputVar.getResolvedDepth();
				int delta = resolvedDepth - inputVar.getDepth();
				var2delta.put(inputVar, delta);
				minPathLength += delta;
			}

			String iterationVector[] = path.split(",");

			if (iterationVector.length < minPathLength) {  // no path is propagated
				for (Port inputVar: inputVars)
					var2Path.put(inputVar, null);
			} else { // compute projected paths
				String[] projectedPath;

				int start = 0;
				for (Port inputVar: inputVars) {
					// 24/7/08 get DNL (declared nesting level) and ANL (actual nesting level) from VAR
					// TODO account for empty paths
					int projectedPathLength = var2delta.get(inputVar);  // this is delta

					if (projectedPathLength == 0) {
						// associate empty path to this var
						var2Path.put(inputVar, null);
						continue;
					}

					// this var is involved in iteration
					projectedPath = new String[projectedPathLength];
					for (int i = 0; i < projectedPathLength; i++)
						projectedPath[i] = iterationVector[start + i];
					start += projectedPathLength;

					StringBuilder iterationFragment = new StringBuilder();
					for (String s : projectedPath)
						iterationFragment.append(s + ",");
					iterationFragment
							.deleteCharAt(iterationFragment.length() - 1);

					var2Path.put(inputVar, iterationFragment.toString());
				}
			}
		}

		// accumulate this proc to current path
		currentPath.add(proc);

		/*
		 * if this is a selected processor, add a copy of the current path to
		 * the list of paths for the processor
		 */

		// is <workflowId, proc>  in selectedProcessors?
		boolean isSelected = false;
		for (ProvenanceProcessor pp : selectedProcessors)
			if (pp.getWorkflowId().equals(workflowId)
					&& pp.getProcessorName().equals(proc)) {
				List<List<String>> paths = validPaths.get(pp);

				// copy the path since the original will change
				// also remove spurious dataflow processors at this point
				List<String> pathCopy = new ArrayList<>();
				for (String s : currentPath)
					if (!getPq().isDataflow(s))
						pathCopy.add(s);
				paths.add(pathCopy);
				isSelected = true;
				break;
			}

		///////////
		/// generate SQL if necessary -- for all input vars, based on the current path
		/// the projected paths are required to determine the level in the collection at which
		/// we look at the value assignment
		///////////

		Map<String, ProvenanceArtifact> var2Artifact = new HashMap<>();
		Map<String, ProvenanceRole> var2ArtifactRole = new HashMap<>();

		// if this transformation is important to the user, produce an output and also an OPM graph fragment
		if (selectedProcessors.isEmpty() || isSelected) {
			List<LineageSQLQuery> newLqList = getPq().lineageQueryGen(
					workflowRunId, proc, var2Path, outputVar, path,
					isReturnOutputs() || var2Path.isEmpty());
			lqList.addAll(newLqList);

			// BEGIN OPM update section
			//
			// create OPM artifact and role for the output var of this xform
			//
			boolean doOPM = (aOPMManager != null && aOPMManager.isActive());  // any problem below will set this to false

			if (doOPM) {
				// fetch value for this variable and assert it as an Artifact in the OPM graph
				Map<String, String> vbConstraints = new HashMap<>();
				vbConstraints.put("VB.processorNameRef",
						outputVar.getProcessorName());
				vbConstraints.put("VB.portName", outputVar.getPortName());
				vbConstraints.put("VB.workflowRunId", workflowRunId);

				if (path != null) {
					/*
					 * account for x,y,.. format as well as [x,y,...] depending
					 * on where the request is coming from
					 */
					// TODO this is just irritating must be removed
					if (path.startsWith("["))
						vbConstraints.put("VB.iteration", path);
					else
						vbConstraints.put("VB.iteration", "[" + path + "]");
				}

				List<PortBinding> vbList = getPq().getPortBindings(vbConstraints); // DB

				/*
				 * use only the first result (expect only one) -- in this method
				 * we assume path is not null
				 */

				// map the resulting varBinding to an Artifact
				if (vbList == null || vbList.size() == 0) {
					logger.debug("no entry corresponding to conditions: proc="
							+ outputVar.getProcessorName() + " var = "
							+ outputVar.getPortName() + " iteration = " + path);
					doOPM = false;
				} else {
					PortBinding vb = vbList.get(0);

					if (aOPMManager != null && !pq.isDataflow(proc)) {
						if (isRecordArtifactValues()) {
							T2Reference ref = getInvocationContext()
									.getReferenceService().referenceFromString(
											vb.getValue());

							Object data = ic.getReferenceService()
									.renderIdentifier(ref, Object.class, ic);

							// ReferenceSetImpl o = (ReferenceSetImpl) ic.getReferenceService().resolveIdentifier(ref, null, ic);
							logger.debug("deref value for ref: " + ref + " "
									+ data + " of class "
									+ data.getClass().getName());

							try {
								aOPMManager.addArtifact(vb.getValue(), data);
							} catch (ProvenanceException e) {
								logger.warn("Could not add artifact", e);
							}
						} else {
							try {
								aOPMManager.addArtifact(vb.getValue());
							} catch (ProvenanceException e) {
								logger.warn("Could not add artifact", e);
							}
						}
						aOPMManager.createRole(vb.getWorkflowRunId(),
								vb.getWorkflowId(), vb.getProcessorName(),
								vb.getIteration());
					}

					/*
					 * assert proc as Process -- include iteration vector to
					 * separate different activations of the same process
					 */
					try {
						aOPMManager.addProcess(proc, vb.getIteration(),
								workflowId, vb.getWorkflowRunId());
					} catch (ProvenanceException e) {
						logger.warn("Could not add process", e);
					}

					/*
					 * create OPM generatedBy property between output value and
					 * this process node avoid the pathological case where a
					 * dataflow generates its own inputs
					 */
					try {
						aOPMManager.assertGeneratedBy(
								aOPMManager.getCurrentArtifact(),
								aOPMManager.getCurrentProcess(),
								aOPMManager.getCurrentRole(),
								aOPMManager.getCurrentAccount(), true);
					} catch (ProvenanceException e) {
						logger.warn("Could not add assertion", e);
					}
				}
			}
			//
			// create OPM process for this xform
			//
			for (LineageSQLQuery lq : newLqList) {
				// if OPM is on, execute the query so we get the value we need for the Artifact node
				Dependencies inputs = getPq().runLineageQuery(lq,
						isIncludeDataValue());

				if (doOPM && inputs.getRecords().size() > 0) { // && !pq.isDataflow(proc)) {
					//	update OPM graph with inputs and used properties
					for (LineageQueryResultRecord resultRecord: inputs.getRecords()) {
						// process inputs only
						if (!resultRecord.isInputPort())
							continue;

						// map each input var in the resultRecord to an Artifact
						// create new Resource for the resultRecord
						//    use the value as URI for the Artifact, and resolvedValue as the actual value

						//
						// create OPM artifact and role for the input var obtained by path projection
						//
						if (resultRecord.isCollection()) {
							try {
								aOPMManager.addArtifact(resultRecord
										.getCollectionT2Reference());
							} catch (ProvenanceException e) {
								logger.warn("Could not add artifact", e);
							}
						} else if (isRecordArtifactValues()) {
							T2Reference ref = getInvocationContext()
									.getReferenceService().referenceFromString(
											resultRecord.getValue());
							Object data = ic.getReferenceService()
									.renderIdentifier(ref, Object.class, ic);
							logger.debug("deref value for ref: " + ref + " "
									+ data + " of class "
									+ data.getClass().getName());
							try {
								aOPMManager.addArtifact(
										resultRecord.getValue(), data);
							} catch (ProvenanceException e) {
								logger.warn("Could not add artifact", e);
							}
						} else {
							try {
								aOPMManager
										.addArtifact(resultRecord.getValue());
							} catch (ProvenanceException e) {
								logger.warn("Could not add artifact", e);
							}
							var2Artifact.put(resultRecord.getPortName(),
									aOPMManager.getCurrentArtifact());

							aOPMManager.createRole(
									resultRecord.getWorkflowRunId(),
									resultRecord.getworkflowId(),
									resultRecord.getProcessorName(),
									resultRecord.getIteration());
							var2ArtifactRole.put(resultRecord.getPortName(),
									aOPMManager.getCurrentRole());

							//
							// create OPM used property between process and the input var obtained by path projection
							//
							// avoid output variables, it would assert that P used one of its outputs!

							try {
								aOPMManager.assertUsed(
										aOPMManager.getCurrentArtifact(),
										aOPMManager.getCurrentProcess(),
										aOPMManager.getCurrentRole(),
										aOPMManager.getCurrentAccount(), true);
							} catch (ProvenanceException e) {
								logger.warn("Could not add artifact", e);
							}

							// true -> prevent duplicates CHECK
						}
					}
				}
				// END OPM update section
			}

			// recursion -- xfer path is next up
			for (Port inputVar : inputVars)
				xferStep(workflowRunId, workflowId, inputVar,
						var2Path.get(inputVar), selectedProcessors, lqList);
		}
		currentPath.remove(currentPath.size()-1);  // CHECK
	}  // end xformStep

	private void xferStep(String workflowRunId, String workflowId, Port port,
			String path, List<ProvenanceProcessor> selectedProcessors,
			List<LineageSQLQuery> lqList) throws SQLException {

		// retrieve all Datalinks ending with (var,proc) -- ideally there is exactly one
		// (because multiple incoming datalinks are disallowed)
		Map<String, String> datalinksQueryConstraints = new HashMap<>();
		datalinksQueryConstraints
				.put("destinationPortId", port.getIdentifier());
		List<DataLink> datalinks = getPq().getDataLinks(
				datalinksQueryConstraints);

		if (datalinks.isEmpty())
			return; // CHECK

		DataLink a = datalinks.get(0);

		// get source node
		String sourceProcName = a.getSourceProcessorName();
//		String sourcePortName = a.getSourcePortName();

		// CHECK transfer same path with only exception: when anl(sink) > anl(source)
		// in this case set path to null

		// retrieve full record for var:
		// retrieve input vars for current processor
		Map<String, String> varsQueryConstraints = new HashMap<>();

//		varsQueryConstraints.put("W.workflowId", workflowRunId);
		varsQueryConstraints.put("portId", a.getSourcePortId());
//		varsQueryConstraints.put("processorNameRef", sourceProcName);
//		varsQueryConstraints.put("portName", sourcePortName);
		List<Port> varList = getPq().getPorts(varsQueryConstraints);

		Port outputVar = varList.get(0);

		// recurse on xform
		xformStep(workflowRunId, workflowId, outputVar, sourceProcName, path,
				selectedProcessors, lqList);
	} // end xferStep2

	/**
	 * this class represents the annotation (single or sequence, to be
	 * determined) that are produced upon visiting the graph structure and that
	 * drive the generation of a pinpoint lineage query<br/>
	 * this is still a placeholder
	 */
	class LineageAnnotation {
		private List<String> path = new ArrayList<>();

		private boolean isXform = true;

		private String iteration = "";  // this is the iteration projected on a single variable. Used for propagation upwards default is no iteration --
		private String iterationVector = ""; // iteration vector accounts for cross-products. Used to be matched exactly in queries.
		private int iic = 0;  // index in collection -- default is 0
		private int collectionNesting = 0;  // n indicates granularity is n levels from leaf.
		// This quantifies loss of lineage precision when working with collections
		private String collectionRef = null;
		private String proc;
		private String var;
		private String varType = null;   // dtring, XML,... see Taverna type system

		private int DNL = 0; // declared nesting level -- copied from VAR
		private int ANL  = 0;  // actual nesting level -- copied from Port

		private String workflowRun;  // TODO generalize to list / time interval?

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (isXform)
				sb.append(" xform: ");
			else
				sb.append(" xfer: ");
			sb.append("<PROC/VAR/VARTYPE, IT, IIC, ITVECTOR, COLLNESTING> = "
					+ proc + "/" + var + "/" + varType + "," + "[" + iteration
					+ "]" + "," + iic + ", [" + iterationVector + "]" + ","
					+ collectionNesting);
			return sb.toString();
		}

		public void addStep(String step) {
			path.add(step);
		}

		public void removeLastStep() {
			path.remove(path.size() - 1);
		}

		/**
		 * @return the path
		 */
		public List<String> getPath() {
			return path;
		}

		/**
		 * @param path
		 *            the path to set
		 */
		public void setPath(List<String> path) {
			this.path = path;
		}

		/**
		 * @return the iteration
		 */
		public String getIteration() {
			return iteration;
		}

		/**
		 * @param iteration
		 *            the iteration to set
		 */
		public void setIteration(String iteration) {
			this.iteration = iteration;
		}

		/**
		 * @return the iic
		 */
		public int getIic() {
			return iic;
		}

		/**
		 * @param iic
		 *            the iic to set
		 */
		public void setIic(int iic) {
			this.iic = iic;
		}

		/**
		 * @return the collectionRef
		 */
		public String getCollectionRef() {
			return collectionRef;
		}

		/**
		 * @param collectionRef
		 *            the collectionRef to set
		 */
		public void setCollectionRef(String collectionRef) {
			this.collectionRef = collectionRef;
		}

		/**
		 * @return the proc
		 */
		public String getProc() {
			return proc;
		}

		/**
		 * @param proc
		 *            the proc to set
		 */
		public void setProc(String proc) {
			this.proc = proc;
		}

		/**
		 * @return the var
		 */
		public String getVar() {
			return var;
		}

		/**
		 * @param var
		 *            the var to set
		 */
		public void setVar(String var) {
			this.var = var;
		}

		/**
		 * @return the varType
		 */
		public String getVarType() {
			return varType;
		}

		/**
		 * @param varType
		 *            the varType to set
		 */
		public void setVarType(String varType) {
			this.varType = varType;
		}

		/**
		 * @return the workflowRun
		 */
		public String getWorkflowRun() {
			return workflowRun;
		}

		/**
		 * @param workflowRun
		 *            the workflowRun to set
		 */
		public void setWorkflowRun(String workflowRun) {
			this.workflowRun = workflowRun;
		}

		/**
		 * @return the isXform
		 */
		public boolean isXform() {
			return isXform;
		}

		/**
		 * @param isXform
		 *            the isXform to set
		 */
		public void setXform(boolean isXform) {
			this.isXform = isXform;
		}

		/**
		 * @return the collectionNesting
		 */
		public int getCollectionNesting() {
			return collectionNesting;
		}

		/**
		 * @param collectionNesting
		 *            the collectionNesting to set
		 */
		public void setCollectionNesting(int collectionNesting) {
			this.collectionNesting = collectionNesting;
		}

		/**
		 * @return the iterationVector
		 */
		public String getIterationVector() {
			return iterationVector;
		}

		/**
		 * @param iterationVector
		 *            the iterationVector to set
		 */
		public void setIterationVector(String iterationVector) {
			this.iterationVector = iterationVector;
		}

		/**
		 * @return the dNL
		 */
		public int getDNL() {
			return DNL;
		}

		/**
		 * @param dnl
		 *            the dNL to set
		 */
		public void setDNL(int dnl) {
			DNL = dnl;
		}

		/**
		 * @return the aNL
		 */
		public int getANL() {
			return ANL;
		}

		/**
		 * @param anl
		 *            the aNL to set
		 */
		public void setANL(int anl) {
			ANL = anl;
		}
	}

	/**
	 * @return the validPaths
	 */
	public Map<ProvenanceProcessor, List<List<String>>> getValidPaths() {
		return validPaths;
	}

	/**
	 * @param validPaths
	 *            the validPaths to set
	 */
	public void setValidPaths(
			Map<ProvenanceProcessor, List<List<String>>> validPaths) {
		this.validPaths = validPaths;
	}

	public void setPq(ProvenanceQuery pq) {
		this.pq = pq;
	}

	public ProvenanceQuery getPq() {
		return pq;
	}

	/**
	 * @return the ready
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * @param ready
	 *            the ready to set
	 */
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	/**
	 * @return the returnOutputs
	 */
	public boolean isReturnOutputs() {
		return returnOutputs;
	}

	/**
	 * @param returnOutputs
	 *            the returnOutputs to set
	 */
	public void setReturnOutputs(boolean returnOutputs) {
		this.returnOutputs = returnOutputs;
	}

	/**
	 * @return the recordArtifactValues
	 */
	public boolean isRecordArtifactValues() {
		return recordArtifactValues;
	}

	/**
	 * @param recordArtifactValues
	 *            the recordArtifactValues to set
	 */
	public void setRecordArtifactValues(boolean recordArtifactValues) {
		this.recordArtifactValues = recordArtifactValues;

	}

	/**
	 * @return the includeDataValue
	 */
	public boolean isIncludeDataValue() {
		return includeDataValue;
	}

	/**
	 * @param includeDataValue
	 *            the includeDataValue to set
	 */
	public void setIncludeDataValue(boolean includeDataValue) {
		this.includeDataValue = includeDataValue;
	}

	/**
	 * @return the generateOPMGraph
	 */
	public boolean isGenerateOPMGraph() {
		return generateOPMGraph;
	}

	/**
	 * @param generateOPMGraph
	 *            the generateOPMGraph to set
	 */
	public void setGenerateOPMGraph(boolean generateOPMGraph) {
		this.generateOPMGraph = generateOPMGraph;
		if (aOPMManager != null)
			aOPMManager.setActive(generateOPMGraph);
	}

	public void setInvocationContext(InvocationContext context) {
		this.ic = context;
	}

	public InvocationContext getInvocationContext() {
		return this.ic;
	}
}
