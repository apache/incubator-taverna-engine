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

package org.apache.taverna.provenance.api;

import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
import static javax.naming.Context.INITIAL_CONTEXT_FACTORY;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.impl.InvocationContextImpl;
import org.apache.taverna.provenance.Provenance;
import org.apache.taverna.provenance.ProvenanceConnectorFactory;
import org.apache.taverna.provenance.connector.AbstractProvenanceConnector;
import org.apache.taverna.provenance.lineageservice.Dependencies;
import org.apache.taverna.provenance.lineageservice.LineageQueryResultRecord;
import org.apache.taverna.provenance.lineageservice.ProvenanceAnalysis;
import org.apache.taverna.provenance.lineageservice.ProvenanceQuery;
import org.apache.taverna.provenance.lineageservice.ProvenanceWriter;
import org.apache.taverna.provenance.lineageservice.utils.Collection;
import org.apache.taverna.provenance.lineageservice.utils.DataLink;
import org.apache.taverna.provenance.lineageservice.utils.DataflowInvocation;
import org.apache.taverna.provenance.lineageservice.utils.PortBinding;
import org.apache.taverna.provenance.lineageservice.utils.ProcessorEnactment;
import org.apache.taverna.provenance.lineageservice.utils.ProvenanceProcessor;
import org.apache.taverna.provenance.lineageservice.utils.Port;
import org.apache.taverna.provenance.lineageservice.utils.Workflow;
import org.apache.taverna.provenance.lineageservice.utils.WorkflowRun;
import org.apache.taverna.provenance.lineageservice.utils.WorkflowTree;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

/**
 * This API is the single access point into the Taverna provenance database. Its
 * main functionality is to let clients query the content of the DB, either
 * using dedicated methods that retrieve specific entity values from the DB, or
 * through a more general XML-based query language. Examples of XML provenance
 * queries can be found in the external package
 * {@link net.sf.taverna.t2.provenance.apic.client.resources}. Class
 * {@link net.sf.taverna.t2.provenance.api.client.ProvenanceAPISampleClient}
 * provides an example of API client that third parties would use to interact
 * with this API.
 * <p/>
 * The XML schema for the XML query language is {@code pquery.xsd} in
 * {@link net.sf.taverna.t2.provenance.apic.client.resources}
 *
 * @author Paolo Missier
 * @author Stuart Owen
 */
public class ProvenanceAccess implements Provenance {
	private static Logger logger = Logger.getLogger(ProvenanceAccess.class);

	private AbstractProvenanceConnector connector = null;
	private ProvenanceAnalysis analyser = null;
	private ProvenanceQuery querier;
	private ProvenanceWriter writer;

	private String connectorType;
	private final List<ProvenanceConnectorFactory> provenanceConnectorFactories;

	public ProvenanceAccess(String connectorType,
			List<ProvenanceConnectorFactory> provenanceConnectorFactories) {
		this.connectorType = connectorType;
		this.provenanceConnectorFactories = provenanceConnectorFactories;
		init();
	}

	public ProvenanceAccess(String connectorType, InvocationContext context,
			List<ProvenanceConnectorFactory> provenanceConnectorFactories) {
		this.connectorType = connectorType;
		this.provenanceConnectorFactories = provenanceConnectorFactories;
		init(context);
	}

	/**
	 * The recommended data source intitialisation method, where only a driver
	 * name and jdbc url are required.<p/>
	 * If the driver supports multiple connections, then a pool will be created
	 * of 10 min idle, 50 max idle, and 50 max active connections.
	 *
	 * @param driverClassName
	 * @param jdbcUrl
	 */
	public static void initDataSource(String driverClassName, String jdbcUrl) {
		initDataSource(driverClassName, jdbcUrl, null, null, 10, 50, 50);
	}

	/**
	 * Initialises a named JNDI DataSource if not already set up externally. The
	 * DataSource is named jdbc/taverna
	 *
	 * @param driverClassName
	 *            - the classname for the driver to be used.
	 * @param jdbcUrl
	 *            - the jdbc connection url
	 * @param username
	 *            - the username, if required (otherwise null)
	 * @param password
	 *            - the password, if required (oteherwise null)
	 * @param minIdle
	 *            - if the driver supports multiple connections, then the
	 *            minumum number of idle connections in the pool
	 * @param maxIdle
	 *            - if the driver supports multiple connections, then the
	 *            maximum number of idle connections in the pool
	 * @param maxActive
	 *            - if the driver supports multiple connections, then the
	 *            minumum number of connections in the pool
	 */
	public static void initDataSource(String driverClassName, String jdbcUrl,
			String username, String password, int minIdle, int maxIdle,
			int maxActive) {
		System.setProperty(INITIAL_CONTEXT_FACTORY,
				"org.osjava.sj.memory.MemoryContextFactory");
		System.setProperty("org.osjava.sj.jndi.shared", "true");

		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driverClassName);
		ds.setDefaultTransactionIsolation(TRANSACTION_READ_UNCOMMITTED);
		ds.setMaxActive(maxActive);
		ds.setMinIdle(minIdle);
		ds.setMaxIdle(maxIdle);
		ds.setDefaultAutoCommit(true);
		if (username != null)
			ds.setUsername(username);
		if (password != null)
			ds.setPassword(password);
		ds.setUrl(jdbcUrl);

		try {
			new InitialContext().rebind("jdbc/taverna", ds);
		} catch (NamingException ex) {
			logger.error("Problem rebinding the jdbc context", ex);
		}
	}

	/**
	 * Initialises a default Reference Service for storing data and their associated references.
	 * This creates a reference service using the named JNDI Data Source 'jdbc/taverna'.<br/>
	 * the new Reference Service is associated to the {@link AbstractProvenanceConnector}, enabling data references to be resolved
	 */
	@Override
	public InvocationContext initDefaultReferenceService() {
		// FIXME
		return initReferenceService("hibernateReferenceServiceContext.xml");
	}

	/**
	 * Initialises the Reference Service for a given hibernate context definition.
	 * This mapping file must be available in the root of the classpath.
	 * @see #initDefaultReferenceService()
	 * @param hibernateContext
	 */
	@Override
	public InvocationContext initReferenceService(String hibernateContext) {
		// FIXME
		return new InvocationContextImpl(refService, connector);
	}

	private ReferenceService refService;
	/**
	 * Set the Reference Service for the connector of this ProvenanceAccess
	 * if you do not 'like' the default one created when ProvenanceAccess is created.
	 */
	public void setReferenceService(ReferenceService refService) {
		this.refService = refService;
		if (connector != null)
			connector.setReferenceService(refService);
	}

	@Override
	public void init() {
		init(initDefaultReferenceService());
	}

	@Override
	public void init(InvocationContext context) {
		for (ProvenanceConnectorFactory factory : provenanceConnectorFactories)
			if (connectorType.equalsIgnoreCase(factory.getConnectorType()))
				connector = factory.getProvenanceConnector();
		logger.info("Provenance being captured using: " + connector);

		//slight change, the init is outside but it also means that the init call has to ensure that the dbURL
		//is set correctly
		connector.init();

		connector.setReferenceService(context.getReferenceService()); // CHECK context.getReferenceService());
		connector.setInvocationContext(context);

		analyser = connector.getProvenanceAnalysis();
		analyser.setInvocationContext(context);

		querier = connector.getQuery();
		writer = connector.getWriter();
		writer.setQuery(querier);

		logger.info("using writer of type: " + writer.getClass());
	}

	/*
	 * main provenance query methods
	 */

	/**
	 * Executes a provenance query. Please see separate doc. for the XML query language schema.
	 * @throws SQLException
	 */
	@Override
	public QueryAnswer executeQuery(Query pq) throws SQLException {
		return analyser.lineageQuery(pq.getTargetPorts(), pq.getRunIDList().get(0),
				pq.getSelectedProcessors());
	}

	/**
	 * Returns individal records from the provenance DB in response to a query
	 * that specifies specific elements within values associated with a
	 * processor port, in the context of a specific run of a workflow. <br/>
	 * This is used in the workbench to retrieve the "intermediate results" at
	 * various points during workflow execution, as opposed to a set of
	 * dependencies in response to a full-fledged provenance query.
	 *
	 * @param workflowRunId
	 *            lineage scope -- a specific instance
	 * @param processorName
	 *            for a specific processor [required]
	 * @param a
	 *            specific (input or output) variable [optional]
	 * @param iteration
	 *            and a specific iteration [optional]
	 * @return a list of @ LineageQueryResultRecord} , encapsulated in a
	 *         {@link Dependencies} object
	 * @throws SQLException
	 */
	@Override
	public Dependencies fetchPortData(String workflowRunId, String workflowId,
			String processorName, String portName, String iteration) {
		logger.info("running fetchPortData on instance " + workflowRunId
				+ " workflow " + workflowId + " processor " + processorName
				+ " port " + portName + " iteration " + iteration);
		// TODO add context workflowID to query
		try {
			return analyser.fetchIntermediateResult(workflowRunId, workflowId,
					processorName, portName, iteration);
		} catch (SQLException e) {
			logger.error("Problem with fetching intermediate results", e);
			return null;
		}
	}

	/**
	 * @param record a record representing a single value -- possibly within a list hierarchy
	 * @return the URI for topmost containing collection when the input record is within a list hierarchy, or null otherwise
	 */
	@Override
	public String getContainingCollection(LineageQueryResultRecord record)  {
		return querier.getContainingCollection(record);
	}

	/*
	 * manage instances
	 */

	/**
	 * @param workflowId
	 *            defines the scope of the query - if null then the query runs
	 *            on all available workflows
	 * @param conditions
	 *            additional conditions to be defined. This is a placeholder as
	 *            conditions are currently ignored
	 * @return a list of workflowRunId, each representing one run of the input
	 *         workflowID
	 */
	@Override
	public List<WorkflowRun> listRuns(String workflowId,
			Map<String, String> conditions) {
		try {
			return querier.getRuns(workflowId, conditions);
		} catch (SQLException e) {
			logger.error("Problem with listing runs", e);
			return null;
		}
	}

	@Override
	public boolean isTopLevelDataflow(String workflowId) {
		return querier.isTopLevelDataflow(workflowId);
	}

	@Override
	public boolean isTopLevelDataflow(String workflowId, String workflowRunId) {
		return querier.isTopLevelDataflow(workflowId, workflowRunId);
	}

	@Override
	public String getLatestRunID() throws SQLException {
		return querier.getLatestRunID();
	}

	/**
	 * Removes all records that pertain to a specific run (but not the static
	 * specification of the workflow run)
	 *
	 * @param runID
	 *            the internal ID of a run. This can be obtained using
	 *            {@link #listRuns(String, Map)}
	 * @return the set of data references that pertain to the deleted run. This
	 *         can be used by the Data Manager to ensure that no dangling
	 *         references are left in the main Taverna data repositorry
	 */
	@Override
	public Set<String> removeRun(String runID) {
		// implement using clearDynamic() method or a variation. Collect references and forward
		try {
			Set<String> danglingDataRefs = writer.clearDBDynamic(runID);

			if (logger.isDebugEnabled())
				logger.debug("references collected during removeRun: " + danglingDataRefs);

			// TODO send the list of dangling refs to the Data manager for removal of the corresponding data values
			return danglingDataRefs;
		} catch (SQLException e) {
			logger.error("Problem while removing run : " + runID, e);
			return null;
		}
	}

	/**
	 * removes all records pertaining to the static structure of a workflow.
	 *
	 * @param workflowId
	 *            the ID (not the external name) of the workflow whose static
	 *            structure is to be deleted from the DB
	 */
	@Override
	public void removeWorkflow(String workflowId) {
		try {
			writer.clearDBStatic(workflowId);
		} catch (SQLException e) {
			logger.error("Problem with removing static workflow: " + workflowId, e);
		}
	}

	/**
	 * returns a set of workflowIDs for a given runID. The set is a singleton if
	 * the workflow has no nesting, but in general the list contains one
	 * workflowID for each nested workflow involved in the run
	 *
	 * @param runID
	 *            the internal ID for a specific workflow run
	 * @return a list of workflow IDs, one for each nested workflow involved in
	 *         the input run
	 */
	@Override
	public List<String> getWorkflowID(String runID) {
		try {
			return querier.getWorkflowIdsForRun(runID);
		} catch (SQLException e) {
			logger.error("Problem getting workflow ID: " + runID, e);
			return null;
		}
	}

	/**
	 * @param runID
	 *            the internal ID for a specific workflow run
	 * @return the ID of the top-level workflow that executed during the input
	 *         run
	 */
	@Override
	public String getTopLevelWorkflowID(String runID) {
		try {
			return querier.getTopLevelWorkflowIdForRun(runID);
		} catch (SQLException e) {
			logger.error("Problem getting top level workflow: " + runID, e);
			return null;
		}
	}

	@Override
	public List<Workflow> getWorkflowsForRun(String runID) {
		try {
			return querier.getWorkflowsForRun(runID);
		} catch (SQLException e) {
			logger.error("Problem getting workflows for run:" + runID, e);
			return null;
		}
	}

	/**
	 * @return a list of {@link WorkflowRun} beans, each representing the
	 *         complete description of a workflow run (note that this is not
	 *         just the ID of the run)
	 */
	@Override
	public List<WorkflowRun> getAllWorkflowIDs() {
		try {
			return querier.getRuns(null, null);
		} catch (SQLException e) {
			logger.error("Problem getting all workflow IDs", e);
			return null;
		}
	}

//	/ access static workflow structure

	/**
	 * @param workflowID
	 * @return a Map: workflowID -> [ @ link ProvenanceProcessor} ] Each entry
	 *         in the list pertains to one composing sub-workflow (if no nesting
	 *         then this contains only one workflow, namely the top level one)
	 */
	@Override
	public Map<String, List<ProvenanceProcessor>> getProcessorsInWorkflow(
			String workflowID) {
		return querier.getProcessorsDeep(null, workflowID);
	}

	@Override
	public List<Collection> getCollectionsForRun(String wfInstanceID) {
		return querier.getCollectionsForRun(wfInstanceID);
	}

	@Override
	public List<PortBinding> getPortBindings(Map<String, String> constraints)
			throws SQLException {
		return querier.getPortBindings(constraints);
	}

	/**
	 * lists all ports for a workflow
	 *
	 * @param workflowID
	 * @return a list of {@link Port} beans, each representing an input or
	 *         output port for the workflow
	 */
	@Override
	public List<Port> getPortsForDataflow(String workflowID) {
		return querier.getPortsForDataflow(workflowID);
	}

	/**
	 * lists all ports for a workflow
	 *
	 * @param workflowID
	 * @return a list of {@link Port} beans, each representing an input or
	 *         output port for the workflow or a processor in the workflow
	 */
	@Override
	public List<Port> getAllPortsInDataflow(String workflowID) {
		return querier.getAllPortsInDataflow(workflowID);
	}

	/**
	 * list all ports for a specific processor within a workflow
	 *
	 * @param workflowID
	 * @param processorName
	 * @return a list of {@link Port} beans, each representing an input or
	 *         output port for the input processor
	 */
	@Override
	public List<Port> getPortsForProcessor(String workflowID,
			String processorName) {
		return querier.getPortsForProcessor(workflowID, processorName);
	}

	// PM added 5/2010
	@Override
	public String getWorkflowNameByWorkflowID(String workflowID) {
		return querier.getWorkflow(workflowID).getExternalName();
	}

	@Override
	public WorkflowTree getWorkflowNestingStructure(String workflowID)
			throws SQLException {
		return querier.getWorkflowNestingStructure(workflowID);
	}

//	public List<ProvenanceProcessor> getSuccessors(String workflowID, String processorName, String portName) {
//	return null; // TODO
//	}

//	public List<String>   getActivities(String workflowID, String processorName) {
//	return null; // TODO
//	}

//	/ configure provenance query functionality

	/**
	 * include valus of output ports in the query result? input port values are
	 * always included<br>
	 * default is FALSE
	 */
	@Override
	public void toggleIncludeProcessorOutputs(boolean active) {
		analyser.setReturnOutputs(active);
	}

	@Override
	public boolean isIncludeProcessorOutputs() {
		return analyser.isReturnOutputs();
	}

	/**
	 * @return an instance of {@link InvocationContext} that can be used by a
	 *         client to deref a Taverna data reference
	 */
	@Override
	public InvocationContext getInvocationContext() {
		return getProvenanceConnector().getInvocationContext();
	}

//	/ OPM management

	/**
	 * should an OPM graph be generated in response to a query?<br>
	 * default is TRUE
	 */
	@Override
	public void toggleOPMGeneration(boolean active) {
		analyser.setGenerateOPMGraph(active);
	}

	/**
	 *
	 * @return true if OPM is set to be generated in response to a query
	 */
	@Override
	public boolean isOPMGenerationActive() {
		return analyser.isGenerateOPMGraph();
	}

	/**
	 * should actual artifact values be attached to OPM artifact nodes?<br>
	 * default is FALSE<br/>
	 * THIS IS CURRENTLY UNSUPPORTED -- DEFAULTS TO FALSE
	 *
	 * @param active
	 */
	@Override
	public void toggleAttachOPMArtifactValues(boolean active) {
		analyser.setRecordArtifactValues(active);
	}

	/**
	 * @return true if the OPM graph artifacts are annotated with actual values
	 */
	@Override
	public boolean isAttachOPMArtifactValues() {
		return analyser.isRecordArtifactValues();
	}

	/**
	 * @deprecated as workflow 'names' are not globally unique, this method
	 *             should not be used!
	 * @param workflowName
	 * @return
	 */
	@Override
	public String getWorkflowIDForExternalName(String workflowName) {
		return querier.getWorkflowIdForExternalName(workflowName);
	}

	@Override
	public List<ProvenanceProcessor> getProcessorsForWorkflowID(
			String workflowID) {
		return querier.getProcessorsForWorkflow(workflowID);
	}

	/**
	 * @return the singleton {@link AbstractProvenanceConnector} used by the API
	 *         to operate on the DB. Currently we support MySQL
	 *         {@link MySQLProvenanceConnector} and Derby
	 *         {@link DerbyProvenanceConnector} connectors. The set of supported
	 *         connectors is extensible. The available connectors are discovered
	 *         automatically by the API upon startup, and it includes all the
	 *         connectors that are mentioned in the &lt;dependencies> section of
	 *         pom.xml for Maven module
	 *         {@code net.sf.taverna.t2.core.provenanceconnector}
	 */
	@Override
	public AbstractProvenanceConnector getProvenanceConnector() {
		return connector;
	}

	/**
	 * @param provenanceConnector
	 *            a specific provenanceConnector used by the API
	 */
	public void setProvenanceConnector(
			AbstractProvenanceConnector provenanceConnector) {
		this.connector = provenanceConnector;
	}

	/**
	 * @return
	 */
	@Override
	public ProvenanceAnalysis getAnalysis() {
		return analyser;
	}

	/**
	 * @param pa
	 *            the pa to set
	 */
	public void setPa(ProvenanceAnalysis pa) {
		this.analyser = pa;
	}

	/**
	 * @return the pq
	 */
	@Override
	public ProvenanceQuery getQuery() {
		return querier;
	}

	/**
	 * @param pq
	 *            the pq to set
	 */
	public void setPq(ProvenanceQuery pq) {
		this.querier = pq;
	}

	@Override
	public List<ProcessorEnactment> getProcessorEnactments(
			String workflowRunId, String... processorPath) {
		return querier.getProcessorEnactments(workflowRunId, processorPath);
	}

	@Override
	public ProcessorEnactment getProcessorEnactmentByProcessId(
			String workflowRunId, String processIdentifier, String iteration) {
		return querier.getProcessorEnactmentByProcessId(workflowRunId,
				processIdentifier, iteration);
	}

	@Override
	public ProcessorEnactment getProcessorEnactment(String processorEnactmentId) {
		return querier.getProcessorEnactment(processorEnactmentId);
	}

	@Override
	public ProvenanceProcessor getProvenanceProcessor(String workflowId,
			String processorNameRef) {
		return querier.getProvenanceProcessorByName(workflowId, processorNameRef);
	}

	@Override
	public ProvenanceProcessor getProvenanceProcessor(String processorId) {
		return querier.getProvenanceProcessorById(processorId);
	}

	@Override
	public Map<Port, T2Reference> getDataBindings(String dataBindingId) {
		Map<Port, T2Reference> references = new HashMap<>();
		for (Entry<Port, String> entry : querier.getDataBindings(dataBindingId)
				.entrySet())
			references.put(entry.getKey(), getProvenanceConnector()
					.getReferenceService()
					.referenceFromString(entry.getValue()));
		return references;
	}

	@Override
	public DataflowInvocation getDataflowInvocation(String workflowRunId) {
		return querier.getDataflowInvocation(workflowRunId);
	}

	@Override
	public DataflowInvocation getDataflowInvocation(
			ProcessorEnactment processorEnactment) {
		return querier.getDataflowInvocation(processorEnactment);
	}

	@Override
	public List<DataflowInvocation> getDataflowInvocations(String workflowRunId) {
		return querier.getDataflowInvocations(workflowRunId);
	}

	@Override
	public List<DataLink> getDataLinks(String workflowId) {
		try {
			Map<String, String> queryConstraints = new HashMap<>();
			queryConstraints.put("workflowId", workflowId);
			return querier.getDataLinks(queryConstraints);
		} catch (SQLException e) {
			logger.error(
					"Problem getting datalinks for workflow:" + workflowId, e);
			return null;
		}
	}
}
