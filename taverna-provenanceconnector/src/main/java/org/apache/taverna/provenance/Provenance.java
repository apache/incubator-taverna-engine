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
package org.apache.taverna.provenance;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.provenance.api.Query;
import org.apache.taverna.provenance.api.QueryAnswer;
import org.apache.taverna.provenance.connector.AbstractProvenanceConnector;
import org.apache.taverna.provenance.lineageservice.Dependencies;
import org.apache.taverna.provenance.lineageservice.LineageQueryResultRecord;
import org.apache.taverna.provenance.lineageservice.ProvenanceAnalysis;
import org.apache.taverna.provenance.lineageservice.ProvenanceQuery;
import org.apache.taverna.provenance.lineageservice.utils.Collection;
import org.apache.taverna.provenance.lineageservice.utils.DataLink;
import org.apache.taverna.provenance.lineageservice.utils.DataflowInvocation;
import org.apache.taverna.provenance.lineageservice.utils.Port;
import org.apache.taverna.provenance.lineageservice.utils.PortBinding;
import org.apache.taverna.provenance.lineageservice.utils.ProcessorEnactment;
import org.apache.taverna.provenance.lineageservice.utils.ProvenanceProcessor;
import org.apache.taverna.provenance.lineageservice.utils.Workflow;
import org.apache.taverna.provenance.lineageservice.utils.WorkflowRun;
import org.apache.taverna.provenance.lineageservice.utils.WorkflowTree;
import org.apache.taverna.reference.T2Reference;

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
public interface Provenance {

	/**
	 * Initialises a default Reference Service for storing data and their associated references.
	 * This creates a reference service using the named JNDI Data Source 'jdbc/taverna'.<br/>
	 * the new Reference Service is associated to the {@link AbstractProvenanceConnector}, enabling data references to be resolved
	 */
	InvocationContext initDefaultReferenceService();

	/**
	 * Initialises the Reference Service for a given hibernate context definition.
	 * This mapping file must be available in the root of the classpath.
	 * @see #initDefaultReferenceService()
	 * @param hibernateContext
	 */
	InvocationContext initReferenceService(String hibernateContext);

	void init();

	void init(InvocationContext context);

	/**
	 * Executes a provenance query. Please see separate doc. for the XML query language schema.
	 * @throws SQLException
	 */
	QueryAnswer executeQuery(Query pq) throws SQLException;

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
	Dependencies fetchPortData(String workflowRunId, String workflowId,
			String processorName, String portName, String iteration);

	/**
	 * @param record a record representing a single value -- possibly within a list hierarchy
	 * @return the URI for topmost containing collection when the input record is within a list hierarchy, or null otherwise
	 */
	String getContainingCollection(LineageQueryResultRecord record);

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
	List<WorkflowRun> listRuns(String workflowId, Map<String, String> conditions);

	boolean isTopLevelDataflow(String workflowId);

	boolean isTopLevelDataflow(String workflowId, String workflowRunId);

	String getLatestRunID() throws SQLException;

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
	Set<String> removeRun(String runID);

	/**
	 * removes all records pertaining to the static structure of a workflow.
	 *
	 * @param workflowId
	 *            the ID (not the external name) of the workflow whose static
	 *            structure is to be deleted from the DB
	 */
	void removeWorkflow(String workflowId);

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
	List<String> getWorkflowID(String runID);

	/**
	 * @param runID
	 *            the internal ID for a specific workflow run
	 * @return the ID of the top-level workflow that executed during the input
	 *         run
	 */
	String getTopLevelWorkflowID(String runID);

	List<Workflow> getWorkflowsForRun(String runID);

	/**
	 * @return a list of {@link WorkflowRun} beans, each representing the
	 *         complete description of a workflow run (note that this is not
	 *         just the ID of the run)
	 */
	List<WorkflowRun> getAllWorkflowIDs();

	/**
	 * @param workflowID
	 * @return a Map: workflowID -> [ @ link ProvenanceProcessor} ] Each entry
	 *         in the list pertains to one composing sub-workflow (if no nesting
	 *         then this contains only one workflow, namely the top level one)
	 */
	Map<String, List<ProvenanceProcessor>> getProcessorsInWorkflow(
			String workflowID);

	List<Collection> getCollectionsForRun(String wfInstanceID);

	List<PortBinding> getPortBindings(Map<String, String> constraints)
			throws SQLException;

	/**
	 * lists all ports for a workflow
	 *
	 * @param workflowID
	 * @return a list of {@link Port} beans, each representing an input or
	 *         output port for the workflow
	 */
	List<Port> getPortsForDataflow(String workflowID);

	/**
	 * lists all ports for a workflow
	 *
	 * @param workflowID
	 * @return a list of {@link Port} beans, each representing an input or
	 *         output port for the workflow or a processor in the workflow
	 */
	List<Port> getAllPortsInDataflow(String workflowID);

	/**
	 * list all ports for a specific processor within a workflow
	 *
	 * @param workflowID
	 * @param processorName
	 * @return a list of {@link Port} beans, each representing an input or
	 *         output port for the input processor
	 */
	List<Port> getPortsForProcessor(String workflowID, String processorName);

	// PM added 5/2010
	String getWorkflowNameByWorkflowID(String workflowID);

	WorkflowTree getWorkflowNestingStructure(String workflowID)
			throws SQLException;

	/**
	 * include valus of output ports in the query result? input port values are
	 * always included<br>
	 * default is FALSE
	 */
	void toggleIncludeProcessorOutputs(boolean active);

	boolean isIncludeProcessorOutputs();

	/**
	 * @return an instance of {@link InvocationContext} that can be used by a
	 *         client to deref a Taverna data reference
	 */
	InvocationContext getInvocationContext();

	/**
	 * should an OPM graph be generated in response to a query?<br>
	 * default is TRUE
	 */
	void toggleOPMGeneration(boolean active);

	/**
	 *
	 * @return true if OPM is set to be generated in response to a query
	 */
	boolean isOPMGenerationActive();

	/**
	 * should actual artifact values be attached to OPM artifact nodes?<br>
	 * default is FALSE<br/>
	 * THIS IS CURRENTLY UNSUPPORTED -- DEFAULTS TO FALSE
	 *
	 * @param active
	 */
	void toggleAttachOPMArtifactValues(boolean active);

	/**
	 * @return true if the OPM graph artifacts are annotated with actual values
	 */
	boolean isAttachOPMArtifactValues();

	/**
	 * @deprecated as workflow 'names' are not globally unique, this method
	 *             should not be used!
	 * @param workflowName
	 * @return
	 */
	String getWorkflowIDForExternalName(String workflowName);

	List<ProvenanceProcessor> getProcessorsForWorkflowID(String workflowID);

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
	AbstractProvenanceConnector getProvenanceConnector();

	/**
	 * @return
	 */
	ProvenanceAnalysis getAnalysis();

	/**
	 * @return the pq
	 */
	ProvenanceQuery getQuery();

	List<ProcessorEnactment> getProcessorEnactments(String workflowRunId,
			String... processorPath);

	ProcessorEnactment getProcessorEnactmentByProcessId(String workflowRunId,
			String processIdentifier, String iteration);

	ProcessorEnactment getProcessorEnactment(String processorEnactmentId);

	ProvenanceProcessor getProvenanceProcessor(String workflowId,
			String processorNameRef);

	ProvenanceProcessor getProvenanceProcessor(String processorId);

	Map<Port, T2Reference> getDataBindings(String dataBindingId);

	DataflowInvocation getDataflowInvocation(String workflowRunId);

	DataflowInvocation getDataflowInvocation(
			ProcessorEnactment processorEnactment);

	List<DataflowInvocation> getDataflowInvocations(String workflowRunId);

	List<DataLink> getDataLinks(String workflowId);

}
