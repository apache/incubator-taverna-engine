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

import static org.apache.taverna.provenance.connector.AbstractProvenanceConnector.DataflowInvocationTable.DataflowInvocation;
import static org.apache.taverna.provenance.connector.AbstractProvenanceConnector.DataflowInvocationTable.parentProcessorEnactmentId;
import static org.apache.taverna.provenance.lineageservice.utils.ProvenanceProcessor.DATAFLOW_ACTIVITY;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.taverna.provenance.connector.AbstractProvenanceConnector.CollectionTable;
import org.apache.taverna.provenance.connector.AbstractProvenanceConnector.DataBindingTable;
import org.apache.taverna.provenance.connector.AbstractProvenanceConnector.DataflowInvocationTable;
import org.apache.taverna.provenance.connector.AbstractProvenanceConnector.ProcessorEnactmentTable;
import org.apache.taverna.provenance.lineageservice.utils.Collection;
import org.apache.taverna.provenance.lineageservice.utils.DDRecord;
import org.apache.taverna.provenance.lineageservice.utils.DataLink;
import org.apache.taverna.provenance.lineageservice.utils.DataflowInvocation;
import org.apache.taverna.provenance.lineageservice.utils.NestedListNode;
import org.apache.taverna.provenance.lineageservice.utils.Port;
import org.apache.taverna.provenance.lineageservice.utils.PortBinding;
import org.apache.taverna.provenance.lineageservice.utils.ProcessorEnactment;
import org.apache.taverna.provenance.lineageservice.utils.ProvenanceProcessor;
import org.apache.taverna.provenance.lineageservice.utils.Workflow;
import org.apache.taverna.provenance.lineageservice.utils.WorkflowTree;
import org.apache.taverna.provenance.lineageservice.utils.WorkflowRun;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;

import org.apache.taverna.configuration.database.DatabaseManager;

/**
 * Handles all the querying of provenance items in the database layer. Uses
 * standard SQL so all specific instances of this class can extend this writer
 * to handle all of the db queries
 *
 * @author Paolo Missier
 * @author Ian Dunlop
 * @author Stuart Owen
 *
 */
public abstract class ProvenanceQuery {
	protected Logger logger = Logger.getLogger(ProvenanceQuery.class);
	private final DatabaseManager databaseManager;

	public ProvenanceQuery(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	public Connection getConnection() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		return databaseManager.getConnection();
	}

	private Q query(String baseQuery) {
		return new Q(baseQuery);
	}
	private class Q {
		private String q;
		private Map<String,String>where;
		private List<String> order;

		Q(String baseQuery) {
			q = baseQuery;
		}

		public Q where(String key, String value) {
			if (where == null)
				where = new HashMap<>();
			where.put(key, value);
			return this;
		}

		public Q where(Map<String, String> clauses) {
			if (where == null)
				where = new HashMap<>();
			where.putAll(clauses);
			return this;
		}

		public Q orderBy(String key) {
			if (order == null)
				order = new ArrayList<>();
			order.add(key);
			return this;
		}
		public ResultSet exec(Statement statement) throws SQLException {
			return statement.executeQuery(query());
		}
		public String query() {
			return addOrderByToQuery(addWhereClauseToQuery(q, where, false), order, false);
		}
	}

	/**
	 * implements a set of query constraints of the form var = value into a
	 * WHERE clause
	 *
	 * @param q
	 * @param queryConstraints
	 * @return
	 */
	protected String addWhereClauseToQuery(String q,
			Map<String, String> queryConstraints, boolean terminate) {

		// complete query according to constraints
		StringBuilder buffer = new StringBuilder(q);

		String sep = " WHERE ";
		if (queryConstraints != null)
			for (Entry<String, String> entry : queryConstraints.entrySet()) {
				buffer.append(sep).append(entry.getKey())
						.append(" = \'").append(entry.getValue()).append("\' ");
				sep = " AND ";
			}
		return buffer.toString();
	}

	protected String addOrderByToQuery(String q, List<String> orderAttr,
			boolean terminate) {
		// complete query according to constraints
		StringBuilder buffer = new StringBuilder(q);

		String sep = " ORDER BY ";
		if (orderAttr != null)
			for (String attr : orderAttr) {
				buffer.append(sep).append(attr);
				sep = ",";
			}
		return buffer.toString();
	}

	/**
	 * select Port records that satisfy constraints
	 */
	public List<Port> getPorts(Map<String, String> queryConstraints)
			throws SQLException {
		List<Port> result = new ArrayList<>();

		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = query(
						"SELECT DISTINCT V.* FROM Port V "
								+ "JOIN WorkflowRun W ON W.workflowId = V.workflowId")
						.where(queryConstraints)
						.orderBy("V.iterationStrategyOrder").exec(stmt)) {
			while (rs.next()) {
				Port aPort = new Port();

				aPort.setWorkflowId(rs.getString("workflowId"));
				aPort.setInputPort(rs.getBoolean("isInputPort"));
				aPort.setIdentifier(rs.getString("portId"));
				aPort.setProcessorName(rs.getString("processorName"));
				aPort.setProcessorId(rs.getString("processorId"));
				aPort.setPortName(rs.getString("portName"));
				aPort.setDepth(rs.getInt("depth"));
				if (rs.getString("resolvedDepth") != null)
					aPort.setResolvedDepth(rs.getInt("resolvedDepth"));
				result.add(aPort);
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return result;
	}


	/**
	 * return the input variables for a given processor and a workflowRunId
	 *
	 * @param pname
	 * @param workflowRunId
	 * @return list of input variables
	 * @throws SQLException
	 */
	public List<Port> getInputPorts(String pname, String wfID)
			throws SQLException {
		// get (var, proc) from Port to see if it's input/output
		Map<String, String> varQueryConstraints = new HashMap<>();

		varQueryConstraints.put("V.workflowId", wfID);
		varQueryConstraints.put("V.processorName", pname);
		varQueryConstraints.put("V.isInputPort", "1");
		return getPorts(varQueryConstraints);
	}

	/**
	 * return the output variables for a given processor and a workflowRunId
	 *
	 * @param pname
	 * @param workflowRunId
	 * @return list of output variables
	 * @throws SQLException
	 */
	public List<Port> getOutputPorts(String pname, String wfID)
			throws SQLException {
		// get (var, proc) from Port to see if it's input/output
		Map<String, String> varQueryConstraints = new HashMap<>();

		varQueryConstraints.put("V.workflowId", wfID);
		varQueryConstraints.put("V.processorName", pname);
		varQueryConstraints.put("V.isInputPort", "0");
		return getPorts(varQueryConstraints);
	}

	/**
	 * selects all Datalinks
	 *
	 * @param queryConstraints
	 * @return
	 * @throws SQLException
	 */
	public List<DataLink> getDataLinks(Map<String, String> queryConstraints)
			throws SQLException {
		List<DataLink> result = new ArrayList<>();

		String q = addWhereClauseToQuery("SELECT A.* FROM Datalink A", queryConstraints, true);

		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement()) {
			ResultSet rs = stmt.executeQuery(q);
			while (rs.next()) {
				DataLink aDataLink = new DataLink();

				aDataLink.setWorkflowId(rs.getString("workflowId"));
				aDataLink.setSourceProcessorName(rs
						.getString("sourceProcessorName"));
				aDataLink.setSourcePortName(rs.getString("sourcePortName"));
				aDataLink.setDestinationProcessorName(rs
						.getString("destinationProcessorName"));
				aDataLink.setDestinationPortName(rs
						.getString("destinationPortName"));
				aDataLink.setSourcePortId(rs.getString("sourcePortId"));
				aDataLink.setDestinationPortId(rs
						.getString("destinationPortId"));
				result.add(aDataLink);
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}

		return result;
	}

	public String getTopLevelWorkflowIdForRun(String runID) throws SQLException {
		for (Workflow w : getWorkflowsForRun(runID))
			if (w.getParentWorkflowId() == null)
				return w.getWorkflowId();
		return null;
	}

	/**
	 * returns the names of all workflows (top level + nested) for a given runID
	 * @param runID
	 * @return
	 * @throws SQLException
	 */
	public List<String> getWorkflowIdsForRun(String runID) throws SQLException {
		List<String> workflowIds = new ArrayList<>();
		for (Workflow w : getWorkflowsForRun(runID))
			workflowIds.add(w.getWorkflowId());
		return workflowIds;
	}

	/**
	 * returns the workflows associated to a single runID
	 * @param runID
	 * @return
	 * @throws SQLException
	 */
	public List<Workflow> getWorkflowsForRun(String runID) throws SQLException {
		List<Workflow> result = new ArrayList<>();
		String q = "SELECT DISTINCT W.* FROM WorkflowRun I JOIN Workflow W ON I.workflowId = W.workflowId WHERE workflowRunId = ?";
		try (Connection connection = getConnection();
				PreparedStatement stmt = connection.prepareStatement(q)) {
			stmt.setString(1, runID);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Workflow w = new Workflow();
				w.setWorkflowId(rs.getString("workflowId"));
				w.setParentWorkflowId(rs.getString("parentWorkflowId"));
				result.add(w);
			}
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			logger.error("Error finding the workflow reference", e);
		}
		return result;
	}

	public String getLatestRunID() throws SQLException {
		String q = "SELECT workflowRunId FROM WorkflowRun ORDER BY timestamp DESC";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(q)) {
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return rs.getString("workflowRunId");
		} catch (Exception e) {
			logger.warn("Could not execute query", e);
		}
		return null;
	}

	/**
	 * @param dataflowID
	 * @param conditions currently only understands "from" and "to" as timestamps for range queries
	 * @return
	 * @throws SQLException
	 */
	public List<WorkflowRun> getRuns(String dataflowID,
			Map<String, String> conditions) throws SQLException {
		List<WorkflowRun> result = new ArrayList<>();
		StringBuilder q = new StringBuilder(
				"SELECT * FROM WorkflowRun I join Workflow W on I.workflowId = W.workflowId");
		List<String> conds = new ArrayList<>();
		if (dataflowID != null)
			conds.add("I.workflowId = '" + dataflowID + "'");
		if (conditions != null) {
			if (conditions.get("from") != null)
				conds.add("timestamp >= " + conditions.get("from"));
			if (conditions.get("to") != null)
				conds.add("timestamp <= " + conditions.get("to"));
		}
		String sep = " where ";
		for (String cond : conds) {
			q.append(sep).append(cond);
			sep = " and ";
		}

		q.append(" ORDER BY timestamp desc ");

		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(q.toString())) {
			logger.debug(q);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				WorkflowRun i = new WorkflowRun();
				i.setWorkflowRunId(rs.getString("workflowRunId"));
				i.setTimestamp(rs.getString("timestamp"));
				i.setWorkflowId(rs.getString("workflowId"));
				i.setWorkflowExternalName(rs.getString("externalName"));
				Blob blob = rs.getBlob("dataflow");
				long length = blob.length();
				blob.getBytes(1, (int) length);
				i.setDataflowBlob(blob.getBytes(1, (int) length));
				result.add(i);
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return result;
	}

	/**
	 * @param constraints
	 *            a Map columnName -> value that defines the query constraints.
	 *            Note: columnName must be fully qualified. This is not done
	 *            well at the moment, i.e., processorNameRef should be
	 *            PortBinding.processorNameRef to avoid ambiguities
	 * @return
	 * @throws SQLException
	 */
	public List<PortBinding> getPortBindings(Map<String, String> constraints)
			throws SQLException {
		List<PortBinding> result = new ArrayList<>();

		String q = "SELECT * FROM PortBinding VB "
				+ "JOIN Port V ON VB.portName = V.portName "
				+ "AND VB.processorNameRef = V.processorName "
				+ "AND VB.workflowId = V.workflowId ";

		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement();) {
			ResultSet rs = stmt.executeQuery(addWhereClauseToQuery(q,
					constraints, true));
			while (rs.next()) {
				PortBinding vb = new PortBinding();

				vb.setWorkflowId(rs.getString("workflowId"));
				vb.setPortName(rs.getString("portName"));
				vb.setWorkflowRunId(rs.getString("workflowRunId"));
				vb.setValue(rs.getString("value"));

				if (rs.getString("collIdRef") == null || rs.getString("collIdRef").equals("null")) {
					vb.setCollIDRef(null);
				} else {
					vb.setCollIDRef(rs.getString("collIdRef"));
				}

				vb.setIteration(rs.getString("iteration"));
				vb.setProcessorName(rs.getString("processorNameRef"));
				vb.setPositionInColl(rs.getInt("positionInColl"));
				vb.setPortId(rs.getString("portId"));
				vb.setIsInputPort(rs.getBoolean("isInputPort"));
				result.add(vb);
			}
		} catch (Exception e) {
			logger.warn("Add VB failed", e);
		}
		return result;
	}

	public List<NestedListNode> getNestedListNodes(
			Map<String, String> constraints) throws SQLException {
		List<NestedListNode> result = new ArrayList<>();

		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = query("SELECT * FROM Collection C ").where(
						constraints).exec(stmt)) {
			while (rs.next()) {
				NestedListNode nln = new NestedListNode();

				nln.setCollectionT2Reference(rs.getString("collId"));
				nln.setParentCollIdRef(rs.getString("parentCollIdRef"));
				nln.setWorkflowRunId(rs.getString("workflowRunId"));
				nln.setProcessorName(rs.getString("processorNameRef"));
				nln.setPortName(rs.getString("portName"));
				nln.setIteration(rs.getString("iteration"));

				result.add(nln);
			}
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			logger.error("Error finding the nested list nodes", e);
		}
		return result;
	}

	public Map<String, Integer> getPredecessorsCount(String workflowRunId) {
		Map<String, Integer> result = new HashMap<>();

		// get all datalinks for the entire workflow structure for this particular instance
		try (Connection connection = getConnection()) {
			PreparedStatement ps = connection
					.prepareStatement("SELECT A.sourceProcessorName as source , A.destinationProcessorName as sink, A.workflowId as workflowId1, W1.workflowId as workflowId2, W2.workflowId as workflowId3 "
							+ "FROM Datalink A join WorkflowRun I on A.workflowId = I.workflowId "
							+ "left outer join Workflow W1 on W1.externalName = A.sourceProcessorName "
							+ "left outer join Workflow W2 on W2.externalName = A.destinationProcessorName "
							+ "where I.workflowRunId = ?");
			ps.setString(1, workflowRunId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String sink = rs.getString("sink");
				String source = rs.getString("source");

				if (result.get(sink) == null)
					result.put(sink, 0);

				String name1 = rs.getString("workflowId1");
				String name2 = rs.getString("workflowId2");
				String name3 = rs.getString("workflowId3");

				if (isDataflow(source) && name1.equals(name2))
					continue;
				if (isDataflow(sink) && name1.equals(name3))
					continue;

				result.put(sink, result.get(sink) + 1);
			}
		} catch (InstantiationException | SQLException | IllegalAccessException | ClassNotFoundException e1) {
			logger.warn("Could not execute query", e1);
		}
		return result;
	}

	/**
	 * new impl of getProcessorsIncomingLinks whicih avoids complications due to nesting, and relies on the workflowRunId
	 * rather than the workflowId
	 * @param workflowRunId
	 * @return
	 */
	public Map<String, Integer> getPredecessorsCountOld(String workflowRunId) {
		Map<String, Integer> result = new HashMap<>();

		// get all datalinks for the entire workflow structure for this particular instance
		try (Connection connection = getConnection()) {
			PreparedStatement ps = connection
					.prepareStatement("SELECT destinationProcessorName, P1.firstActivityClass, count(*) as pred "
							+ " FROM Datalink A join WorkflowRun I on A.workflowId = I.workflowId "
							+ " join Processor P1 on P1.processorName = A.destinationProcessorName "
							+ " join Processor P2 on P2.processorName = A.sourceProcessorName "
							+ "  where I.workflowRunId = ? "
							+ "  and P2.firstActivityClass <> '"
							+ DATAFLOW_ACTIVITY
							+ "' "
							+ " and ((P1.firstActivityClass = '"
							+ DATAFLOW_ACTIVITY
							+ "'  and P1.workflowId = A.workflowId) or "
							+ " (P1.firstActivityClass <> '"
							+ DATAFLOW_ACTIVITY
							+ "' )) "
							+ " group by A.destinationProcessorName, firstActivityClass");
			ps.setString(1, workflowRunId);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				result.put(rs.getString("destinationProcessorName"),
						new Integer(rs.getInt("pred")));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e1) {
			logger.warn("Could not execute query", e1);
		}
		return result;
	}

	/**
	 * used in the toposort phase -- propagation of anl() values through the
	 * graph
	 *
	 * @param workflowId
	 *            reference to static wf name
	 * @return a map <processor name> --> <incoming links count> for each
	 *         processor, without counting the datalinks from the dataflow input to
	 *         processors. So a processor is at the root of the graph if it has
	 *         no incoming links, or all of its incoming links are from dataflow
	 *         inputs.<br/>
	 *         Note: this must be checked for processors that are roots of
	 *         sub-flows... are these counted as top-level root nodes??
	 */
	public Map<String, Integer> getProcessorsIncomingLinks(String workflowId)
			throws SQLException {
		Map<String, Integer> result = new HashMap<>();

		String currentWorkflowProcessor = null;
		String sql = "SELECT processorName, firstActivityClass FROM Processor "
				+ "WHERE workflowId = ?";

		try (Connection c = getConnection()) {
			try (PreparedStatement ps = c.prepareStatement(sql)) {
				ps.setString(1, workflowId);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					// PM CHECK 6/09
					if (rs.getString("firstActivityClass").equals(
							DATAFLOW_ACTIVITY)) {
						currentWorkflowProcessor = rs
								.getString("processorName");
						logger.info("currentWorkflowProcessor = "
								+ currentWorkflowProcessor);
					}
					result.put(rs.getString("processorName"), 0);
				}
			}

			/*
			 * fetch the name of the top-level dataflow. We use this to exclude
			 * datalinks outgoing from its inputs
			 */

			// CHECK below -- gets confused on nested workflows
			String parentWF = getParentOfWorkflow(workflowId);
			if (parentWF == null)
				parentWF = workflowId; // null parent means we are the top
			logger.debug("parent WF: " + parentWF);

			// get nested dataflows -- we want to avoid these in the toposort algorithm
			List<ProvenanceProcessor> procs = getProcessorsShallow(c,
					DATAFLOW_ACTIVITY, parentWF);

			StringBuilder q = new StringBuilder("SELECT destinationProcessorName, count(*) AS cnt ");
			q.append("FROM Datalink WHERE workflowId = \'").append(workflowId)
					.append("\' AND destinationProcessorName NOT IN (");
			String sep = "";
			for (ProvenanceProcessor p : procs) {
				q.append(sep).append("'").append(p.getProcessorName())
						.append("'");
				sep = ",";
			}
			q.append(") GROUP BY destinationProcessorName");

			logger.info("executing \n" + q);

			try (Statement stmt = c.createStatement();
					ResultSet rs = stmt.executeQuery(q.toString())) {
				while (rs.next())
					if (!rs.getString("destinationProcessorName").equals(
							currentWorkflowProcessor))
						result.put(rs.getString("destinationProcessorName"),
								rs.getInt("cnt"));
				result.put(currentWorkflowProcessor, 0);
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}

		return result;
	}

	public List<Port> getSuccPorts(String processorName, String portName,
			String workflowId) throws SQLException {
		List<Port> result = new ArrayList<>();
		String sql = "SELECT v.* "
				+ "FROM Datalink a JOIN Port v ON a.destinationProcessorName = v.processorName "
				+ "AND  a.destinationPortName = v.portName "
				+ "AND a.workflowId = v.workflowId "
				+ "WHERE sourcePortName=? AND sourceProcessorName=?";
		if (workflowId != null)
			sql += " AND a.workflowId=?";

		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, portName);
			ps.setString(2, processorName);
			if (workflowId != null)
				ps.setString(3, workflowId);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Port aPort = new Port();

				aPort.setWorkflowId(rs.getString("workflowId"));
				aPort.setInputPort(rs.getBoolean("isInputPort"));
				aPort.setIdentifier(rs.getString("portId"));
				aPort.setProcessorName(rs.getString("processorName"));
				aPort.setProcessorId(rs.getString("processorId"));
				aPort.setPortName(rs.getString("portName"));
				aPort.setDepth(rs.getInt("depth"));
				if (rs.getString("resolvedDepth") != null)
					aPort.setResolvedDepth(rs.getInt("resolvedDepth"));
				result.add(aPort);
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return result;
	}

	public List<String> getSuccProcessors(String pName, String workflowId,
			String workflowRunId) throws SQLException {
		List<String> result = new ArrayList<>();
		String sql = "SELECT distinct destinationProcessorName FROM Datalink A JOIN WorkflowRun I on A.workflowId = I.workflowId "
				+ "WHERE A.workflowId = ? and I.workflowRunId = ? AND sourceProcessorName = ?";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, workflowId);
			ps.setString(2, workflowRunId);
			ps.setString(3, pName);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				result.add(rs.getString("destinationProcessorName"));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return result;
	}

	/**
	 * get all processors of a given type within a structure identified by
	 * workflowId (reference to dataflow). type constraint is ignored if value is null.<br>
	 * this only returns the processor for the input workflowId, without going into any neted workflows
	 *
	 * @param workflowId
	 * @param firstActivityClass
	 * @return a list, that contains at most one element
	 * @throws SQLException
	 */
	public List<ProvenanceProcessor> getProcessorsShallow(
			String firstActivityClass, String workflowId) throws SQLException {
		Map<String, String> constraints = new HashMap<>();
		constraints.put("P.workflowId", workflowId);
		if (firstActivityClass != null)
			constraints.put("P.firstActivityClass", firstActivityClass);
		return getProcessors(constraints);
	}

	private List<ProvenanceProcessor> getProcessorsShallow(Connection c,
			String firstActivityClass, String workflowId) throws SQLException {
		Map<String, String> constraints = new HashMap<>();
		constraints.put("P.workflowId", workflowId);
		if (firstActivityClass != null)
			constraints.put("P.firstActivityClass", firstActivityClass);
		return getProcessors(c, constraints);
	}

	public ProvenanceProcessor getProvenanceProcessorByName(
			String workflowId, String processorName) {
		Map<String, String> constraints = new HashMap<>();
		constraints.put("P.workflowId", workflowId);
		constraints.put("P.processorName", processorName);
		List<ProvenanceProcessor> processors;
		try {
			processors = getProcessors(constraints);
		} catch (SQLException e1) {
			logger.warn("Could not find processor for " + constraints, e1);
			return null;
		}
		if (processors.size() != 1) {
			logger.warn("Could not uniquely find processor for " + constraints + ", got: " + processors);
			return null;
		}
		return processors.get(0);
	}

	public ProvenanceProcessor getProvenanceProcessorById(String processorId) {
		Map<String, String> constraints = new HashMap<>();
		constraints.put("P.processorId", processorId);
		List<ProvenanceProcessor> processors;
		try {
			processors = getProcessors(constraints);
		} catch (SQLException e1) {
			logger.warn("Could not find processor for " + constraints, e1);
			return null;
		}
		if (processors.size() != 1) {
			logger.warn("Could not uniquely find processor for " + constraints
					+ ", got: " + processors);
			return null;
		}
		return processors.get(0);
	}

	/**
	 * this is similar to {@link #getProcessorsShallow(String, String)} but it
	 * recursively fetches all processors within nested workflows. The result is
	 * collected in the form of a map: workflowId -> {ProvenanceProcessor}
	 *
	 * @param firstActivityClass
	 * @param workflowId
	 * @return a map: workflowId -> {ProvenanceProcessor} where workflowId is
	 *         the name of a (possibly nested) workflow, and the values are the
	 *         processors within that workflow
	 */
	public Map<String, List<ProvenanceProcessor>> getProcessorsDeep(
			String firstActivityClass, String workflowId) {
		Map<String, List<ProvenanceProcessor>> result = new HashMap<>();

		try {
			List<ProvenanceProcessor> currentProcs = getProcessorsShallow(null,
					workflowId);
			List<ProvenanceProcessor> matchingProcessors = new ArrayList<>();
			result.put(workflowId, matchingProcessors);
			for (ProvenanceProcessor pp:currentProcs) {
				if (firstActivityClass == null
						|| pp.getFirstActivityClassName().equals(
								firstActivityClass))
					matchingProcessors.add(pp);
				if (pp.getFirstActivityClassName().equals(DATAFLOW_ACTIVITY)) {
					// Can't recurse as there's no way to find ID of nested workflow
					continue;
					//result.putAll(getProcessorsDeep(firstActivityClass, NESTED_WORKFLOW_ID));
				}
			}

			// Silly fallback - use the broken getChildrenOfWorkflow() assuming that no other workflows
			// have used the same nested workflow
			for (String childWf : getChildrenOfWorkflow(workflowId))
				result.putAll(getProcessorsDeep(firstActivityClass, childWf));
		} catch (SQLException e) {
			logger.error("Problem getting nested workflow processors for: " + workflowId, e);
		}
		return result;
	}

	public String getDataValue(String valueRef) {
		String q = "SELECT * FROM Data where dataReference = ?;";

		try (Connection connection = getConnection();
				PreparedStatement stmt = connection.prepareStatement(q)) {
			stmt.setString(1, valueRef);
			ResultSet rs = stmt.executeQuery(q);
			if (rs.next())
				return rs.getString("data");
		} catch (Exception e) {
			logger.warn("Could not execute query", e);
		}
		return null;
	}

	/**
	 * generic method to fetch processors subject to additional query constraints
	 * @param constraints
	 * @return
	 * @throws SQLException
	 */
	public List<ProvenanceProcessor> getProcessors(
			Map<String, String> constraints) throws SQLException {
		try (Connection connection = getConnection()) {
			return getProcessors(connection, constraints);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return new ArrayList<ProvenanceProcessor>();
	}

	private List<ProvenanceProcessor> getProcessors(Connection c,
			Map<String, String> constraints) throws SQLException {
		List<ProvenanceProcessor> result = new ArrayList<>();
		try (Statement stmt = c.createStatement();
				ResultSet rs = query("SELECT P.* FROM Processor P").where(
						constraints).exec(stmt)) {
			while (rs.next()) {
				ProvenanceProcessor proc = new ProvenanceProcessor();
				proc.setIdentifier(rs.getString("processorId"));
				proc.setProcessorName(rs.getString("processorName"));
				proc.setFirstActivityClassName(rs
						.getString("firstActivityClass"));
				proc.setWorkflowId(rs.getString("workflowId"));
				proc.setTopLevelProcessor(rs.getBoolean("isTopLevel"));
				result.add(proc);
			}
		}
		return result;
	}

	public List<ProvenanceProcessor> getProcessorsForWorkflow(String workflowID) {
		List<ProvenanceProcessor> result = new ArrayList<>();
		try (Connection connection = getConnection();
				PreparedStatement ps = connection
						.prepareStatement("SELECT * from Processor WHERE workflowId=?")) {
			ps.setString(1, workflowID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ProvenanceProcessor proc = new ProvenanceProcessor();
				proc.setIdentifier(rs.getString("processorId"));
				proc.setProcessorName(rs.getString("processorName"));
				proc.setFirstActivityClassName(rs
						.getString("firstActivityClass"));
				proc.setWorkflowId(rs.getString("workflowId"));
				proc.setTopLevelProcessor(rs.getBoolean("isTopLevel"));
				result.add(proc);
			}
		} catch (SQLException | InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			logger.error("Problem getting processor for workflow: "
					+ workflowID, e);
		}
		return result;
	}

	/**
	 * simplest possible pinpoint query. Uses iteration info straight away. Assumes result is in PortBinding not in Collection
	 *
	 * @param workflowRun
	 * @param pname
	 * @param vname
	 * @param iteration
	 * @return
	 */
	public LineageSQLQuery simpleLineageQuery(String workflowRun, String workflowId, String pname,
			String vname, String iteration) {
		LineageSQLQuery lq = new LineageSQLQuery();
		Q q = query("SELECT * FROM PortBinding VB "
				+ "JOIN Port V ON (VB.portName = V.portName AND VB.processorNameRef = V.processorName AND VB.workflowId = V.workflowId) "
				+ "JOIN WorkflowRun W ON VB.workflowRunId = W.workflowRunId AND VB.workflowId = W.workflowId ");

		// constraints:
		q.where("W.workflowRunId", workflowRun)
				.where("VB.processorNameRef", pname)
				.where("VB.workflowId", workflowId);

		if (vname != null)
			q.where("VB.portName", vname);
		if (iteration != null)
			q.where("VB.iteration", iteration);

		// add order by clauses
		q.orderBy("V.portName").orderBy("iteration");

		logger.debug("Query is: " + q.query());
		lq.setVbQuery(q.query());
		return lq;
	}

	/**
	 * if var2Path is null this generates a trivial query for the current output
	 * var and current path
	 *
	 * @param workflowRunId
	 * @param proc
	 * @param var2Path
	 * @param outputVar
	 * @param path
	 * @param returnOutputs
	 *            returns inputs *and* outputs if set to true
	 * @return
	 */
	public List<LineageSQLQuery> lineageQueryGen(String workflowRunId, String proc,
			Map<Port, String> var2Path, Port outputVar, String path,
			boolean returnOutputs) {
		// setup
		List<LineageSQLQuery> newQueries = new ArrayList<>();

		// use the calculated path for each input var
		boolean isInput = true;
		for (Port v : var2Path.keySet()) {
			LineageSQLQuery q = generateSQL2(workflowRunId, proc, v.getPortName(), var2Path.get(v), isInput);
			if (q != null)
				newQueries.add(q);
		}

		// is returnOutputs is true, then use proc, path for the output var as well
		if (returnOutputs) {
			isInput = false;
			LineageSQLQuery q = generateSQL2(workflowRunId, proc, outputVar.getPortName(), path, isInput);  // && !var2Path.isEmpty());
			if (q != null)
				newQueries.add(q);
		}
		return newQueries;
	}

	protected LineageSQLQuery generateSQL2(String workflowRun, String proc,
			String var, String path, boolean returnInput) {
		LineageSQLQuery lq = new LineageSQLQuery();
		Q q;

		// base Collection query
		q = query("SELECT C.*,W.workflowId,V.isInputPort FROM Collection C "
				+ "JOIN WorkflowRun W ON C.workflowRunId = W.workflowRunId "
				+ "JOIN Port V ON V.workflowId = W.workflowId "
				+ "AND C.processorNameRef = V.processorName "
				+ "AND C.portName = V.portName ");
		if (path != null && path.length() > 0)
			q.where("C.iteration", "[" + path + "]"); // PM 1/09 -- path
		lq.setCollQuery(q.where("W.workflowRunId", workflowRun).where("C.processorNameRef",
				proc).where("V.isInputPort", returnInput ? "1" : "0").query());

		// base PortBinding query
		q = query("SELECT VB.*,V.isInputPort FROM PortBinding VB "
				+ "JOIN WorkflowRun W ON VB.workflowRunId = W.workflowRunId "
				+ "JOIN Port V on V.workflowId = W.workflowId "
				+ "AND VB.processorNameRef = V.processorName "
				+ "AND VB.portName = V.portName ");
		if (path != null && path.length() > 0)
			q.where("VB.iteration", "[" + path + "]"); // PM 1/09 -- path
		lq.setVbQuery(q.where("W.workflowRunId", workflowRun)
				.where("VB.processorNameRef", proc).where("VB.portName", var)
				.where("V.isInputPort", returnInput ? "1" : "0")
				.orderBy("V.portName").orderBy("iteration").query());
		return lq;
	}

	/**
	 * if effectivePath is not null: query varBinding using: workflowRunId =
	 * workflowRun, iteration = effectivePath, processorNameRef = proc if input vars is
	 * null, then use the output var this returns the bindings for the set of
	 * input vars at the correct iteration if effectivePath is null: fetch
	 * PortBindings for all input vars, without constraint on the iteration<br/>
	 * additionally, try querying the collection table first -- if the query succeeds, it means
	 * the path is pointing to an internal node in the collection, and we just got the right node.
	 * Otherwise, query PortBinding for the leaves
	 *
	 * @param workflowRun
	 * @param proc
	 * @param effectivePath
	 * @param returnOutputs
	 *            returns both inputs and outputs if set to true
	 * @return
	 */
	public LineageSQLQuery generateSQL(String workflowRun, String proc,
			String effectivePath, boolean returnOutputs) {
		LineageSQLQuery lq = new LineageSQLQuery();
		Q q;

		// base Collection query
		q = query("SELECT * FROM Collection C "
				+ "JOIN WorkflowRun W ON C.workflowRunId = W.workflowRunId "
				+ "JOIN Port V ON V.workflowRunId = W.workflowId "
				+ "AND C.processorNameRef = V.processorNameRef "
				+ "AND C.portName = V.portName ");

		if (effectivePath != null && effectivePath.length() > 0)
			q.where("C.iteration", "[" + effectivePath.toString() + "]"); // PM 1/09 -- path
		// limit to inputs?
		if (returnOutputs)
			q.where("V.isInputPort", "1");

		lq.setCollQuery(q.where("W.workflowRunId", workflowRun).where("C.processorNameRef",
				proc).query());

		// base PortBinding query
		q = query("SELECT * FROM PortBinding VB "
				+ "JOIN WorkflowRun W ON VB.workflowRunId = W.workflowRunId "
				+ "JOIN Port V on V.workflowRunId = W.workflowId "
				+ "AND VB.processorNameRef = V.processorNameRef "
				+ "AND VB.portName = V.portName ");

		if (effectivePath != null && effectivePath.length() > 0)
			q.where("VB.iteration", "[" + effectivePath.toString() + "]"); // PM 1/09 -- path
		// limit to inputs?
		if (!returnOutputs)
			q.where("V.isInputPort", "1");

		lq.setVbQuery(q.where("W.workflowRunId", workflowRun)
				.where("VB.processorNameRef", proc).orderBy("portName")
				.orderBy("iteration").query());
		return lq;
	}

	public Dependencies runCollectionQuery(LineageSQLQuery lq) throws SQLException {
		String q = lq.getCollQuery();
		Dependencies lqr = new Dependencies();
		if (q == null)
			return lqr;

		logger.debug("running collection query: " + q);

		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement()) {
			ResultSet rs = stmt.executeQuery(q);
			while (rs.next()) {
				String type = Dependencies.ATOM_TYPE; // temp -- FIXME

				String workflowId = rs.getString("workflowId");
				String workflowRun = rs.getString("workflowRunId");
				String proc = rs.getString("processorNameRef");
				String var = rs.getString("portName");
				String it = rs.getString("iteration");
				String coll = rs.getString("collID");
				String parentColl = rs.getString("parentCollIDRef");
				//boolean isInput = rs.getBoolean("isInputPort");

				lqr.addLineageQueryResultRecord(workflowId, proc, var, workflowRun,
						it, coll, parentColl, null, null, type, false, true);  // true -> is a collection
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return lqr;
	}

	/**
	 *
	 * @param lq
	 * @param includeDataValue  IGNORED. always false
	 * @return
	 * @throws SQLException
	 */
	public Dependencies runVBQuery(LineageSQLQuery lq, boolean includeDataValue)
			throws SQLException {
		String q = lq.getVbQuery();

		logger.info("running VB query: " + q);

		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement()) {
			ResultSet rs = stmt.executeQuery(q);
			Dependencies lqr = new Dependencies();

			while (rs.next()) {
				String type = Dependencies.ATOM_TYPE; // temp -- FIXME

				String workflowId = rs.getString("workflowId");
				String workflowRun = rs.getString("workflowRunId");
				String proc = rs.getString("processorNameRef");
				String var = rs.getString("portName");
				String it = rs.getString("iteration");
				String coll = rs.getString("collIDRef");
				String value = rs.getString("value");
				boolean isInput = rs.getBoolean("isInputPort");

				// FIXME if the data is required then the query needs fixing
				lqr.addLineageQueryResultRecord(workflowId, proc, var, workflowRun,
						it, coll, null, value, null, type, isInput, false);
			}
			return lqr;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return null;
	}

	/**
	 * executes one of the lineage queries produced by the graph visit algorithm. This first executes the collection query, and then
	 * if no result is returned, the varBinding query
	 *
	 * @param lq
	 *            a lineage query computed during the graph traversal
	 * @param includeDataValue
	 *            if true, then the referenced value is included in the result.
	 *            This may only be necessary for testing: the data reference in
	 *            field value (which is a misleading field name, and actually
	 *            refers to the data reference) should be sufficient
	 * @return
	 * @throws SQLException
	 */
	public Dependencies runLineageQuery(LineageSQLQuery lq,
			boolean includeDataValue) throws SQLException {
		Dependencies result = runCollectionQuery(lq);
		if (result.getRecords().isEmpty())
			return runVBQuery(lq, includeDataValue);
		return result;
	}

	public List<Dependencies> runLineageQueries(List<LineageSQLQuery> lqList,
			boolean includeDataValue) throws SQLException {
		List<Dependencies> allResults = new ArrayList<>();
		if (lqList == null)
			logger.warn("lineage queries list is NULL, nothing to evaluate");
		else
			for (LineageSQLQuery lq : lqList)
				if (lq != null)
					allResults.add(runLineageQuery(lq, includeDataValue));
		return allResults;
	}

	/**
	 * takes an ordered set of records for the same variable with iteration
	 * indexes and builds a collection out of it
	 *
	 * @param lqr
	 * @return a jdom Document with the collection
	 */
	public Document recordsToCollection(Dependencies lqr) {
		// process each var name in turn
		// lqr ordered by var name and by iteration number
		Document d = new Document(new Element("list"));

		String currentVar = null;
		for (ListIterator<LineageQueryResultRecord> it = lqr.iterator(); it.hasNext();) {
			LineageQueryResultRecord record = it.next();

			if (currentVar != null && record.getPortName().equals(currentVar)) {
				// multiple occurrences
				addToCollection(record, d);
				// adds record to d in the correct position given by the iteration vector
			}
			if (currentVar == null)
				currentVar = record.getPortName();
		}
		return d;
	}

	private void addToCollection(LineageQueryResultRecord record, Document d) {
		Element root = d.getRootElement();
		String[] itVector = record.getIteration().split(",");
		Element currentEl = root;
		// each element gives us a corresponding child in the tree
		for (int i = 0; i < itVector.length; i++) {
			int index = Integer.parseInt(itVector[i]);
			List<?> children = currentEl.getChildren();
			if (index < children.size())
				currentEl = (Element) children.get(index);
			else if (i == itVector.length - 1)
				currentEl.addContent(new Element(record.getValue()));
			else
				currentEl.addContent(new Element("list"));
		}
	}

	/**
	 *
	 * returns the set of all processors that are structurally contained within
	 * the wf corresponding to the input dataflow name
	 * @param workflowName the name of a processor of type DataFlowActivity
	 * @return
	 *
	 * @deprecated as workflow 'names' are not globally unique, this method should not be used!
	 */
	@Deprecated
	public List<String> getContainedProcessors(String workflowName) {
		List<String> result = new ArrayList<>();

		// dataflow name -> wfRef
		String containerDataflow = getWorkflowIdForExternalName(workflowName);

		// get all processors within containerDataflow
		try (Connection connection = getConnection();
				PreparedStatement ps = connection
						.prepareStatement("SELECT processorName FROM Processor P "
								+ "WHERE workflowId = ?")) {
			ps.setString(1, containerDataflow);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				result.add(rs.getString("processorName"));
		} catch (InstantiationException | SQLException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return result;
	}

	public String getTopLevelDataflowName(String workflowRunId) {
		String sql = "SELECT processorName FROM Processor P "
				+ "JOIN WorkflowRun I on P.workflowId = I.workflowId "
				+ "WHERE I.workflowRunId = ? AND isTopLevel = 1";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, workflowRunId);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return rs.getString("processorName");
		} catch (InstantiationException | SQLException | IllegalAccessException
				| ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return null;
	}

	/**
	 * retrieve a tree structure starting from the top parent
	 * @param workflowID
	 * @return
	 * @throws SQLException
	 */
	public WorkflowTree getWorkflowNestingStructure(String workflowID) throws SQLException {
		WorkflowTree tree = new WorkflowTree();

	    Workflow wf = getWorkflow(workflowID);
	    tree.setNode(wf);

	    List<String> children = getChildrenOfWorkflow(workflowID);
	    for (String childWfName:children) {
	    	WorkflowTree childStructure = getWorkflowNestingStructure(childWfName);
	    	tree.addChild(childStructure);
	    }
	    return tree;
	}

	/**
	 * returns the internal ID of a dataflow given its external name
	 * @param externalName
	 * @param workflowRunId
	 * @return
	 * @deprecated as workflow 'names' are not globally unique, this method should not be used!
	 */
	@Deprecated
	public String getWorkflowIdForExternalName(String externalName) {
		//"SELECT workflowId FROM Workflow W join WorkflowRun I on W.workflowId = I.workflowId WHERE W.externalName = ? and I.workflowRunId = ?");
		String sql = "SELECT workflowId FROM Workflow W WHERE W.externalName = ?";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, externalName);
			// ps.setString(2, workflowRunId);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return rs.getString("workflowId");
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			logger.warn("Could not execute query", e);
		}
		return null;
	}

	/**
	 * This method is deprecated as parent workflow ID is not correctly
	 * recorded. If two workflows both contain the same nested workflow, only
	 * one of them (the most recently added) will return that nested workflow
	 * from this method.
	 *
	 * @deprecated
	 * @param parentWorkflowId
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
	public List<String> getChildrenOfWorkflow(String parentWorkflowId)
			throws SQLException {
		List<String> result = new ArrayList<>();
		try (Connection connection = getConnection();
				PreparedStatement ps = connection
						.prepareStatement("SELECT workflowId FROM Workflow WHERE parentWorkflowId = ? ")) {
			ps.setString(1, parentWorkflowId);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				result.add(rs.getString("workflowId"));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return result;
	}

	/**
	 * fetch children of parentWorkflowId from the Workflow table
	 *
	 * @return
	 * @param childworkflowId
	 * @throws SQLException
	 */
	public String getParentOfWorkflow(String childworkflowId)
			throws SQLException {
		String result = null;
		String q = "SELECT parentWorkflowId FROM Workflow WHERE workflowId = ?";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(q)) {
			ps.setString(1, childworkflowId);

			logger.debug("getParentOfWorkflow - query: " + q
					+ "  with workflowId = " + childworkflowId);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString("parentWorkflowId");
				logger.debug("result: " + result);
				break;
			}
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return result;
	}

	public List<String> getAllworkflowIds() throws SQLException {
		List<String> result = new ArrayList<>();
		String q = "SELECT workflowId FROM Workflow";
		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement()) {
			ResultSet rs = stmt.executeQuery(q);
			while (rs.next())
				result.add(rs.getString("workflowId"));
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return result;
	}

	/**
	 * @deprecated This method is not workflowId aware and should not be used
	 * @param procName
	 * @return true if procName is the external name of a dataflow, false
	 *         otherwise
	 * @throws SQLException
	 */
	public boolean isDataflow(String procName) throws SQLException {
		String sql = "SELECT firstActivityClass FROM Processor WHERE processorName = ?";
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, procName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()
					&& DATAFLOW_ACTIVITY.equals(rs
							.getString("firstActivityClass")))
				return true;
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return false;
	}

	public boolean isTopLevelDataflow(String workflowIdID) {
		String sql = "SELECT * FROM Workflow W where W.workflowId = ?";
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, workflowIdID);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return (rs.getString("parentWorkflowId") == null);
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return false;
	}

	public boolean isTopLevelDataflow(String workflowId, String workflowRunId) {
		String sql = "SELECT " + parentProcessorEnactmentId + " AS parent"
				+ " FROM " + DataflowInvocation + " W " + " WHERE "
				+ DataflowInvocationTable.workflowId + "=? AND "
				+ DataflowInvocationTable.workflowRunId + "=?";
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, workflowId);
			ps.setString(2, workflowRunId);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return (rs.getString("parent") == null);
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return false;
	}

	public String getTopDataflow(String workflowRunId) {
		String sql = "SELECT processorName FROM "
				+ "Processor P JOIN WorkflowRun I ON P.workflowId = I.workflowId "
				+ " WHERE I.workflowRunId = ? AND isTopLevel = 1 ";
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, workflowRunId);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return rs.getString("processorName");
		} catch (SQLException | InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return null;
	}

	/**
	 *
	 * @param p
	 *            pTo processor
	 * @param var
	 *            vTo
	 * @param value
	 *            valTo
	 * @return a set of DDRecord
	 * @throws SQLException
	 */
	public List<DDRecord> queryDD(String p, String var, String value,
			String iteration, String workflowRun) throws SQLException {
		Q q = query("SELECT * FROM DD ");
		q.where("pTo", p);
		q.where("vTo", var);
		if (value != null)
			q.where("valTo", value);
		if (iteration != null)
			q.where("iteration", iteration);
		if (workflowRun != null)
			q.where("workflowRun", workflowRun);

		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = q.exec(stmt)) {
			List<DDRecord> result = new ArrayList<>();
			while (rs.next()) {
				DDRecord aDDrecord = new DDRecord();
				aDDrecord.setPFrom(rs.getString("pFrom"));
				aDDrecord.setVFrom(rs.getString("vFrom"));
				aDDrecord.setValFrom(rs.getString("valFrom"));
				aDDrecord.setPTo(rs.getString("pTo"));
				aDDrecord.setVTo(rs.getString("vTo"));
				aDDrecord.setValTo(rs.getString("valTo"));
				result.add(aDDrecord);
			}
			return result;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return null;
	}

	public Set<DDRecord> queryDataLinksForDD(String p, String v, String val,
			String workflowRun) throws SQLException {
		String sql = "SELECT DISTINCT A.sourceProcessorName AS p, A.sourcePortName AS var, VB.value AS val "
				+ "FROM   Datalink A "
				+ "JOIN   PortBinding VB ON VB.portName = A.destinationPortName AND VB.processorNameRef = A.destinationProcessorName "
				+ "JOIN   WorkflowRun WF ON WF.workflowId = A.workflowId AND WF.workflowRunId = VB.workflowRunId  "
				+ "WHERE  WF.workflowRunId = ? AND A.destinationProcessorName = ? AND A.destinationPortName = ? AND VB.value = ?";

		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, workflowRun);
			ps.setString(2, p);
			ps.setString(3, v);
			ps.setString(4, val);
			ResultSet rs = ps.executeQuery();
			Set<DDRecord> result = new HashSet<>();
			while (rs.next()) {
				DDRecord aDDrecord = new DDRecord();
				aDDrecord.setPTo(rs.getString("p"));
				aDDrecord.setVTo(rs.getString("var"));
				aDDrecord.setValTo(rs.getString("val"));
				result.add(aDDrecord);
			}
			return result;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		}
		return null;
	}

	public Set<DDRecord> queryAllFromValues(String workflowRun)
			throws SQLException {
		String sql = "SELECT DISTINCT PFrom, vFrom, valFrom FROM DD where workflowRun = ?";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, workflowRun);
			ResultSet rs = ps.executeQuery();
			Set<DDRecord> result = new HashSet<>();
			while (rs.next()) {
				DDRecord aDDrecord = new DDRecord();
				aDDrecord.setPFrom(rs.getString("PFrom"));
				aDDrecord.setVFrom(rs.getString("vFrom"));
				aDDrecord.setValFrom(rs.getString("valFrom"));
				result.add(aDDrecord);
			}
			return result;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
			return null;
		}
	}

	public boolean isRootProcessorOfWorkflow(String procName, String workflowId,
			String workflowRunId) {
		String sql = "SELECT * FROM Datalink A JOIN WorkflowRun I ON A.workflowId = I.workflowId "
				+ "JOIN Processor P on P.processorName = A.sourceProcessorName "
				+ "WHERE sourceProcessorName = ? "
				+ "AND P.workflowId <> A.workflowId "
				+ "AND I.workflowRunId = ? "
				+ "AND destinationProcessorName = ? ";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, workflowId);
			ps.setString(2, workflowRunId);
			ps.setString(3, procName);
			if (ps.executeQuery().next())
				return true;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			logger.warn("Could not execute query", e);
		}
		return false;
	}

	/**
	 * returns a Workflow record from the DB given the workflow internal ID
	 * @param dataflowID
	 * @return
	 */
	public Workflow getWorkflow(String dataflowID) {
		String sql = "SELECT * FROM Workflow W WHERE workflowId = ? ";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, dataflowID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Workflow wf = new Workflow();
				wf.setWorkflowId(rs.getString("workflowId"));
				wf.setParentWorkflowId(rs.getString("parentWorkflowId"));
				wf.setExternalName(rs.getString("externalName"));

				return wf;
			} else {
				logger.warn("Could not find workflow " + dataflowID);
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			logger.warn("Could not execute query", e);
		}
		return null;
	}

	/**
	 * @param record
	 *            a record representing a single value -- possibly within a list
	 *            hierarchy
	 * @return the URI for topmost containing collection when the input record
	 *         is within a list hierarchy, or null otherwise
	 */
	public String getContainingCollection(LineageQueryResultRecord record) {
		if (record.getCollectionT2Reference() == null)
			return null;
		String sql = "SELECT * FROM Collection "
				+ "WHERE collID = ? and workflowRunId = ? and processorNameRef = ? and portName = ?";
		try (Connection connection = getConnection()) {
			String parentCollIDRef = null;
			try (PreparedStatement stmt = connection.prepareStatement(sql)) {
				stmt.setString(1, record.getCollectionT2Reference());
				stmt.setString(2, record.getWorkflowRunId());
				stmt.setString(3, record.getProcessorName());
				stmt.setString(4, record.getPortName());
				ResultSet rs = stmt.executeQuery();
				if (rs.next())
					parentCollIDRef = rs.getString("parentCollIDRef");
			}

			// INITIALLY not null -- would be TOP if the initial had no parent
			while (parentCollIDRef != null) {
				String oldParentCollIDRef = parentCollIDRef;

				// query Collection again for parent collection
				try (PreparedStatement stmt = connection.prepareStatement(sql)) {
					stmt.setString(1, oldParentCollIDRef);
					stmt.setString(2, record.getWorkflowRunId());
					stmt.setString(3, record.getProcessorName());
					stmt.setString(4, record.getPortName());
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						parentCollIDRef = rs.getString("parentCollIDRef");
						if (parentCollIDRef.equals("TOP"))
							return oldParentCollIDRef;
					}
				} catch (Exception e) {
					logger.warn("Could not execute query", e);
				}
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			logger.warn("Could not execute query", e);
		}
		return null;
	}

	public List<ProcessorEnactment> getProcessorEnactments(
			String workflowRunId, String... processorPath) {
		return getProcessorEnactments(workflowRunId,
				(List<ProcessorEnactment>) null, Arrays.asList(processorPath));
	}

	private List<ProcessorEnactment> getProcessorEnactments(
			String workflowRunId, List<ProcessorEnactment> parentProcessorEnactments,
			List<String> processorPath) {
		List<String> processorEnactmentIds = null;
		if (parentProcessorEnactments != null) {
			processorEnactmentIds = new ArrayList<>();
			for (ProcessorEnactment processorEnactment : parentProcessorEnactments)
				processorEnactmentIds.add(processorEnactment.getProcessEnactmentId());
		}
		if (processorPath.size() > 1) {
			return getProcessorEnactments(
					workflowRunId,
					getProcessorEnactmentsByProcessorName(workflowRunId,
							processorEnactmentIds, processorPath.get(0)),
					processorPath.subList(1, processorPath.size()));
		} else if (processorPath.size() == 1) {
			return getProcessorEnactmentsByProcessorName(workflowRunId,
					processorEnactmentIds, processorPath.get(0));
		} else {
			return getProcessorEnactmentsByProcessorName(workflowRunId,
					processorEnactmentIds, null);
		}
	}

	public List<ProcessorEnactment> getProcessorEnactmentsByProcessorName(
			String workflowRunId, List<String> parentProcessorEnactmentIds,
			String processorName) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT ")
				.append(ProcessorEnactmentTable.enactmentStarted).append(", ")
				.append(ProcessorEnactmentTable.enactmentEnded).append(", ")
				.append(ProcessorEnactmentTable.finalOutputsDataBindingId)
				.append(", ")
				.append(ProcessorEnactmentTable.initialInputsDataBindingId)
				.append(", ")
				.append(ProcessorEnactmentTable.ProcessorEnactment).append(".")
				.append(ProcessorEnactmentTable.processorId)
				.append(" AS procId, ")
				.append(ProcessorEnactmentTable.processIdentifier).append(", ")
				.append(ProcessorEnactmentTable.processEnactmentId)
				.append(", ")
				.append(ProcessorEnactmentTable.parentProcessorEnactmentId)
				.append(", ").append(ProcessorEnactmentTable.workflowRunId)
				.append(", ").append(ProcessorEnactmentTable.iteration)
				.append(", Processor.processorName FROM ")
				.append(ProcessorEnactmentTable.ProcessorEnactment)
				.append(" INNER JOIN Processor ON ")
				.append(ProcessorEnactmentTable.ProcessorEnactment).append(".")
				.append(ProcessorEnactmentTable.processorId)
				.append(" = Processor.processorId WHERE ")
				.append(ProcessorEnactmentTable.workflowRunId).append(" = ? ");

		if (processorName != null)
			// Specific processor
			query.append(" AND Processor.processorName = ? ");
		if ((parentProcessorEnactmentIds == null || parentProcessorEnactmentIds.isEmpty()) && processorName != null) {
			// null - ie. top level
			query.append(" AND " + ProcessorEnactmentTable.parentProcessorEnactmentId + " IS NULL");
		} else if (parentProcessorEnactmentIds != null) {
			// not null, ie. inside nested workflow
			query.append(" AND " + ProcessorEnactmentTable.parentProcessorEnactmentId + " IN (");
			for (int i=0; i<parentProcessorEnactmentIds.size(); i++) {
				query.append('?');
				if (i < (parentProcessorEnactmentIds.size()-1))
					query.append(',');
			}
			query.append(')');
		}

		ArrayList<ProcessorEnactment> procEnacts = new ArrayList<>();

		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(query
						.toString())) {
			int pos = 1;
			statement.setString(pos++, workflowRunId);
			if (processorName != null)
				statement.setString(pos++, processorName);
			if (parentProcessorEnactmentIds != null)
				for (String parentId : parentProcessorEnactmentIds)
					statement.setString(pos++, parentId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				ProcessorEnactment procEnact = readProcessorEnactment(resultSet);
				procEnacts.add(procEnact);
			}
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query " + query, e);
		}
		return procEnacts;
	}

	private ProcessorEnactment readProcessorEnactment(ResultSet resultSet) throws SQLException {
		Timestamp enactmentStarted = resultSet.getTimestamp(ProcessorEnactmentTable.enactmentStarted.name());
		Timestamp enactmentEnded = resultSet.getTimestamp(ProcessorEnactmentTable.enactmentEnded.name());
		//String pName = resultSet.getString("processorName");
		String finalOutputsDataBindingId = resultSet.getString(ProcessorEnactmentTable.finalOutputsDataBindingId.name());
		String initialInputsDataBindingId = resultSet.getString(ProcessorEnactmentTable.initialInputsDataBindingId.name());

		String iteration = resultSet.getString(ProcessorEnactmentTable.iteration.name());
		String processorId = resultSet.getString("procId");
		String processIdentifier = resultSet.getString(ProcessorEnactmentTable.processIdentifier.name());
		String processEnactmentId = resultSet.getString(ProcessorEnactmentTable.processEnactmentId.name());
		String parentProcessEnactmentId = resultSet.getString(ProcessorEnactmentTable.parentProcessorEnactmentId.name());
		String workflowRunId = resultSet.getString(ProcessorEnactmentTable.workflowRunId.name());

		ProcessorEnactment procEnact = new ProcessorEnactment();
		procEnact.setEnactmentEnded(enactmentEnded);
		procEnact.setEnactmentStarted(enactmentStarted);
		procEnact.setFinalOutputsDataBindingId(finalOutputsDataBindingId);
		procEnact.setInitialInputsDataBindingId(initialInputsDataBindingId);
		procEnact.setIteration(iteration);
		procEnact.setParentProcessorEnactmentId(parentProcessEnactmentId);
		procEnact.setProcessEnactmentId(processEnactmentId);
		procEnact.setProcessIdentifier(processIdentifier);
		procEnact.setProcessorId(processorId);
		procEnact.setWorkflowRunId(workflowRunId);
		return procEnact;
	}

	public ProcessorEnactment getProcessorEnactment(String processorEnactmentId) {
		String query  =
				"SELECT " + ProcessorEnactmentTable.enactmentStarted + ","
						+ ProcessorEnactmentTable.enactmentEnded + ","
						+ ProcessorEnactmentTable.finalOutputsDataBindingId + ","
						+ ProcessorEnactmentTable.initialInputsDataBindingId + ","
						+ ProcessorEnactmentTable.ProcessorEnactment + "."
						+ ProcessorEnactmentTable.processorId + " AS procId,"
						+ ProcessorEnactmentTable.processIdentifier + ","
						+ ProcessorEnactmentTable.workflowRunId + ","
						+ ProcessorEnactmentTable.processEnactmentId + ","
						+ ProcessorEnactmentTable.parentProcessorEnactmentId + ","
						+ ProcessorEnactmentTable.iteration
						+ " FROM "
						+ ProcessorEnactmentTable.ProcessorEnactment
						+ " WHERE "
						+ ProcessorEnactmentTable.processEnactmentId + "=?";

		ProcessorEnactment procEnact = null;
		try (Connection connection = getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query)) {
			statement.setString(1, processorEnactmentId);
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				logger.warn("Could not find ProcessorEnactment processEnactmentId="
						+ processorEnactmentId);
				return null;
			}
			procEnact = readProcessorEnactment(resultSet);
			if (resultSet.next()) {
				logger.error("Found more than one ProcessorEnactment processEnactmentId="
						+ processorEnactmentId);
				return null;
			}
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query " + query, e);
		}
		return procEnact;
	}

	public ProcessorEnactment getProcessorEnactmentByProcessId(
			String workflowRunId, String processIdentifier, String iteration) {
		String query = "SELECT " + ProcessorEnactmentTable.enactmentStarted
				+ "," + ProcessorEnactmentTable.enactmentEnded + ","
				+ ProcessorEnactmentTable.finalOutputsDataBindingId + ","
				+ ProcessorEnactmentTable.initialInputsDataBindingId + ","
				+ ProcessorEnactmentTable.ProcessorEnactment + "."
				+ ProcessorEnactmentTable.processorId + " AS procId,"
				+ ProcessorEnactmentTable.processIdentifier + ","
				+ ProcessorEnactmentTable.workflowRunId + ","
				+ ProcessorEnactmentTable.processEnactmentId + ","
				+ ProcessorEnactmentTable.parentProcessorEnactmentId + ","
				+ ProcessorEnactmentTable.iteration + " FROM "
				+ ProcessorEnactmentTable.ProcessorEnactment + " WHERE "
				+ ProcessorEnactmentTable.workflowRunId + "=?" + " AND "
				+ ProcessorEnactmentTable.processIdentifier + "=?" + " AND "
				+ ProcessorEnactmentTable.iteration + "=?";

		ProcessorEnactment procEnact = null;
		try (Connection connection = getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query)) {
			statement.setString(1, workflowRunId);
			statement.setString(2, processIdentifier);
			statement.setString(3, iteration);

			ResultSet resultSet = statement.executeQuery();
			String debugString = "ProcessorEnactment runId=" + workflowRunId
					+ " processIdentifier=" + processIdentifier + " iteration="
					+ iteration;
			if (!resultSet.next()) {
				logger.warn("Could not find " + debugString);
				return null;
			}
			procEnact = readProcessorEnactment(resultSet);
			if (resultSet.next()) {
				logger.error("Found more than one " + debugString);
				return null;
			}
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query " + query, e);
		}
		return procEnact;
	}

	public Map<Port, String> getDataBindings(String dataBindingId) {
		HashMap<Port, String> dataBindings = new HashMap<>();
		String query = "SELECT " + DataBindingTable.t2Reference + ","
				+ "Port.portId AS portId," + "Port.processorName,"
				+ "Port.processorId," + "Port.isInputPort," + "Port.portName,"
				+ "Port.depth," + "Port.resolvedDepth," + "Port.workflowId"
				+ " FROM " + DataBindingTable.DataBinding + " INNER JOIN "
				+ "Port" + " ON " + " Port.portId="
				+ DataBindingTable.DataBinding + "." + DataBindingTable.portId
				+ " WHERE " + DataBindingTable.dataBindingId + "=?";
		try (Connection connection = getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query)) {
			statement.setString(1, dataBindingId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String t2Ref = rs.getString(DataBindingTable.t2Reference.name());

				Port port = new Port();
				port.setWorkflowId(rs.getString("workflowId"));
				port.setInputPort(rs.getBoolean("isInputPort"));
				port.setIdentifier(rs.getString("portId"));
				port.setProcessorName(rs.getString("processorName"));
				port.setProcessorId(rs.getString("processorId"));
				port.setPortName(rs.getString("portName"));
				port.setDepth(rs.getInt("depth"));
				if (rs.getString("resolvedDepth") != null)
					port.setResolvedDepth(rs.getInt("resolvedDepth"));
				dataBindings.put(port, t2Ref);
			}
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query " + query, e);
		}
		return dataBindings;
	}

	public List<Port> getAllPortsInDataflow(String workflowID) {
		Map<String, String> queryConstraints = new HashMap<>();
		queryConstraints.put("V.workflowId", workflowID);
		try {
			return getPorts(queryConstraints);
		} catch (SQLException e) {
			logger.error("Problem getting ports for dataflow: " + workflowID, e);
			return null;
		}
	}

	public List<Port> getPortsForDataflow(String workflowID) {
		Workflow w = getWorkflow(workflowID);

		Map<String, String> queryConstraints = new HashMap<>();
		queryConstraints.put("V.workflowId", workflowID);
		queryConstraints.put("processorName", w.getExternalName());

		try {
			return getPorts(queryConstraints);
		} catch (SQLException e) {
			logger.error("Problem getting ports for dataflow: " + workflowID, e);
			return null;
		}
	}

	public List<Port> getPortsForProcessor(String workflowID,
			String processorName) {
		Map<String, String> queryConstraints = new HashMap<>();
		queryConstraints.put("V.workflowId", workflowID);
		queryConstraints.put("processorName", processorName);

		try {
			return getPorts(queryConstraints);
		} catch (SQLException e) {
			logger.error("Problem getting ports for processor: "
					+ processorName + " worflow: " + workflowID, e);
			return null;
		}
	}

	public DataflowInvocation getDataflowInvocation(String workflowRunId) {
		String query = "SELECT " +
				  DataflowInvocationTable.dataflowInvocationId + ","
				+ DataflowInvocationTable.inputsDataBinding + ","
				+ DataflowInvocationTable.invocationEnded + ","
				+ DataflowInvocationTable.invocationStarted + ","
				+ DataflowInvocationTable.outputsDataBinding + ","
				+ DataflowInvocationTable.parentProcessorEnactmentId + ","
				+ DataflowInvocationTable.workflowId + ","
				+ DataflowInvocationTable.workflowRunId + ","
				+ DataflowInvocationTable.completed
				+ " FROM "
				+ DataflowInvocationTable.DataflowInvocation +
				" WHERE "
				+ DataflowInvocationTable.parentProcessorEnactmentId + " IS NULL AND "
				+ DataflowInvocationTable.workflowRunId + "=?";
		DataflowInvocation dataflowInvocation = null;
		try (Connection connection = getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query)) {
			statement.setString(1, workflowRunId);
			ResultSet rs = statement.executeQuery();
			if (!rs.next()) {
				logger.warn("Could not find DataflowInvocation for workflowRunId=" + workflowRunId);
				return null;
			}
			dataflowInvocation = new DataflowInvocation();
			dataflowInvocation.setDataflowInvocationId(rs.getString(DataflowInvocationTable.dataflowInvocationId.name()));
			dataflowInvocation.setInputsDataBindingId(rs.getString(DataflowInvocationTable.inputsDataBinding.name()));
			dataflowInvocation.setInvocationEnded(rs.getTimestamp(DataflowInvocationTable.invocationEnded.name()));
			dataflowInvocation.setInvocationStarted(rs.getTimestamp(DataflowInvocationTable.invocationStarted.name()));
			dataflowInvocation.setOutputsDataBindingId(rs.getString(DataflowInvocationTable.outputsDataBinding.name()));
			dataflowInvocation.setParentProcessorEnactmentId(rs.getString(DataflowInvocationTable.parentProcessorEnactmentId.name()));
			dataflowInvocation.setWorkflowId(rs.getString(DataflowInvocationTable.workflowId.name()));
			dataflowInvocation.setWorkflowRunId(rs.getString(DataflowInvocationTable.workflowRunId.name()));
			dataflowInvocation.setCompleted(rs.getBoolean(DataflowInvocationTable.completed.name()));
			if (rs.next()) {
				logger.error("Found more than one DataflowInvocation for workflowRunId=" + workflowRunId);
				return null;
			}
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query " + query, e);
		}
		return dataflowInvocation;
	}

	public DataflowInvocation getDataflowInvocation(
			ProcessorEnactment processorEnactment) {
		String query = "SELECT " + DataflowInvocationTable.dataflowInvocationId
				+ "," + DataflowInvocationTable.inputsDataBinding + ","
				+ DataflowInvocationTable.invocationEnded + ","
				+ DataflowInvocationTable.invocationStarted + ","
				+ DataflowInvocationTable.outputsDataBinding + ","
				+ DataflowInvocationTable.parentProcessorEnactmentId + ","
				+ DataflowInvocationTable.workflowId + ","
				+ DataflowInvocationTable.workflowRunId + ","
				+ DataflowInvocationTable.completed + " FROM "
				+ DataflowInvocationTable.DataflowInvocation + " WHERE "
				+ DataflowInvocationTable.parentProcessorEnactmentId + "=?";
		DataflowInvocation dataflowInvocation = null;
		try (Connection connection = getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query)) {
			statement.setString(1, processorEnactment.getProcessEnactmentId());
			ResultSet rs = statement.executeQuery();
			if (!rs.next()) {
				logger.warn("Could not find DataflowInvocation for processorEnactmentId="
						+ processorEnactment.getProcessEnactmentId());
				return null;
			}
			dataflowInvocation = new DataflowInvocation();
			dataflowInvocation.setDataflowInvocationId(rs
					.getString(DataflowInvocationTable.dataflowInvocationId
							.name()));
			dataflowInvocation
					.setInputsDataBindingId(rs
							.getString(DataflowInvocationTable.inputsDataBinding
									.name()));
			dataflowInvocation.setInvocationEnded(rs
					.getTimestamp(DataflowInvocationTable.invocationEnded
							.name()));
			dataflowInvocation.setInvocationStarted(rs
					.getTimestamp(DataflowInvocationTable.invocationStarted
							.name()));
			dataflowInvocation.setOutputsDataBindingId(rs
					.getString(DataflowInvocationTable.outputsDataBinding
							.name()));
			dataflowInvocation
					.setParentProcessorEnactmentId(rs
							.getString(DataflowInvocationTable.parentProcessorEnactmentId
									.name()));
			dataflowInvocation.setWorkflowId(rs
					.getString(DataflowInvocationTable.workflowId.name()));
			dataflowInvocation.setWorkflowRunId(rs
					.getString(DataflowInvocationTable.workflowRunId.name()));
			dataflowInvocation.setCompleted(rs
					.getBoolean(DataflowInvocationTable.completed.name()));

			if (rs.next()) {
				logger.error("Found more than one DataflowInvocation for processorEnactmentId="
						+ processorEnactment.getProcessEnactmentId());
				return null;
			}
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query " + query, e);
		}
		return dataflowInvocation;
	}

	public List<DataflowInvocation> getDataflowInvocations(String workflowRunId) {
		String query = "SELECT " + DataflowInvocationTable.dataflowInvocationId
				+ "," + DataflowInvocationTable.inputsDataBinding + ","
				+ DataflowInvocationTable.invocationEnded + ","
				+ DataflowInvocationTable.invocationStarted + ","
				+ DataflowInvocationTable.outputsDataBinding + ","
				+ DataflowInvocationTable.parentProcessorEnactmentId + ","
				+ DataflowInvocationTable.workflowId + ","
				+ DataflowInvocationTable.workflowRunId + ","
				+ DataflowInvocationTable.completed + " FROM "
				+ DataflowInvocationTable.DataflowInvocation + " WHERE "
				+ DataflowInvocationTable.workflowRunId + "=?";
		List<DataflowInvocation> invocations = new ArrayList<>();
		try (Connection connection = getConnection();
				PreparedStatement statement = connection
						.prepareStatement(query)) {
			statement.setString(1, workflowRunId);
			ResultSet rs = statement.executeQuery();
			if (! rs.next()) {
				logger.warn("Could not find DataflowInvocation for workflowRunId=" + workflowRunId);
				return null;
			}
			DataflowInvocation dataflowInvocation = new DataflowInvocation();
			dataflowInvocation.setDataflowInvocationId(rs.getString(DataflowInvocationTable.dataflowInvocationId.name()));
			dataflowInvocation.setInputsDataBindingId(rs.getString(DataflowInvocationTable.inputsDataBinding.name()));
			dataflowInvocation.setInvocationEnded(rs.getTimestamp(DataflowInvocationTable.invocationEnded.name()));
			dataflowInvocation.setInvocationStarted(rs.getTimestamp(DataflowInvocationTable.invocationStarted.name()));
			dataflowInvocation.setOutputsDataBindingId(rs.getString(DataflowInvocationTable.outputsDataBinding.name()));
			dataflowInvocation.setParentProcessorEnactmentId(rs.getString(DataflowInvocationTable.parentProcessorEnactmentId.name()));
			dataflowInvocation.setWorkflowId(rs.getString(DataflowInvocationTable.workflowId.name()));
			dataflowInvocation.setWorkflowRunId(rs.getString(DataflowInvocationTable.workflowRunId.name()));
			dataflowInvocation.setCompleted(rs.getBoolean(DataflowInvocationTable.completed.name()));
			invocations.add(dataflowInvocation);
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.warn("Could not execute query " + query, e);
		}
		return invocations;
	}

	public List<Collection> getCollectionsForRun(String wfInstanceID) {
		ArrayList<Collection> result = new ArrayList<>();
		String sql = "SELECT * FROM Collection C WHERE workflowRunId = ?";
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(sql)) {
			ps.setString(1, wfInstanceID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Collection coll = new Collection();
				coll.setCollId(rs.getString(CollectionTable.collID.name()));
				coll.setParentIdentifier(rs
						.getString(CollectionTable.parentCollIDRef.name()));
				coll.setWorkflowRunIdentifier(rs
						.getString(CollectionTable.workflowRunId.name()));
				coll.setProcessorName(rs
						.getString(CollectionTable.processorNameRef.name()));
				coll.setPortName(rs.getString(CollectionTable.portName.name()));
				coll.setIteration(rs.getString(CollectionTable.iteration.name()));
				result.add(coll);
			}
		} catch (Exception e) {
			logger.warn("Could not execute query", e);
		}
		return result;
	}
}
