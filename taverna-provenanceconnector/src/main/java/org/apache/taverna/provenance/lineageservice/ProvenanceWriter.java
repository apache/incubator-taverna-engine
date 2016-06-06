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

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.taverna.provenance.connector.AbstractProvenanceConnector.ActivityTable;
import org.apache.taverna.provenance.connector.AbstractProvenanceConnector.DataBindingTable;
import org.apache.taverna.provenance.connector.AbstractProvenanceConnector.DataflowInvocationTable;
import org.apache.taverna.provenance.connector.AbstractProvenanceConnector.ProcessorEnactmentTable;
import org.apache.taverna.provenance.connector.AbstractProvenanceConnector.ServiceInvocationTable;
import org.apache.taverna.provenance.lineageservice.utils.NestedListNode;
import org.apache.taverna.provenance.lineageservice.utils.Port;
import org.apache.taverna.provenance.lineageservice.utils.PortBinding;
import org.apache.taverna.provenance.lineageservice.utils.ProvenanceProcessor;

import org.apache.log4j.Logger;

import org.apache.taverna.configuration.database.DatabaseManager;

/**
 * Handles all the writing out of provenance items to the database layer. Uses
 * standard SQL so all specific instances of this class can extend this writer
 * to handle all of the db writes
 *
 * @author Paolo Missier
 * @author Ian Dunlop
 * @author Stuart Owen
 *
 */
public class ProvenanceWriter {

	protected static Logger logger = Logger.getLogger(ProvenanceWriter.class);
	protected int cnt; // counts number of calls to PortBinding
	protected ProvenanceQuery pq = null;
	private final DatabaseManager databaseManager;

	public ProvenanceWriter(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	public Connection getConnection() throws SQLException {
		return databaseManager.getConnection();
	}

	public void closeCurrentModel() {

	}

	/**
	 * add each Port as a row into the Port DB table <strong>note: no static
	 * port type available as part of the dataflow...</strong>
	 *
	 * @param ports
	 * @param wfId
	 * @throws SQLException
	 */
	public void addPorts(List<Port> ports, String wfId) throws SQLException {
		String sql = "INSERT INTO Port "
				+ "(portName, processorName, isInputPort, depth, workflowId, portId, processorId)"
				+ "  VALUES(?,?,?,?,?,?,?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			for (Port v : ports) {
				ps.setString(1, v.getPortName());
				ps.setString(2, v.getProcessorName());
				ps.setBoolean(3, v.isInputPort());
				int depth = v.getDepth() >= 0 ? v.getDepth() : 0;
				ps.setInt(4, depth);
				ps.setString(5, wfId);
				ps.setString(6, v.getIdentifier());
				ps.setString(7, v.getProcessorId());

				try {
					ps.executeUpdate();
				} catch (Exception e) {
					logger.warn("Could not insert var " + v.getPortName(), e);
				}
			}
		}
	}

	@SuppressWarnings("static-access")
	public void addDataflowInvocation(
			org.apache.taverna.provenance.lineageservice.utils.DataflowInvocation invocation)
			throws SQLException {
		String sql = "INSERT INTO " + DataflowInvocation.DataflowInvocation
				+ "(" + DataflowInvocation.dataflowInvocationId + ","
				+ DataflowInvocation.workflowId + ","
				+ DataflowInvocation.invocationStarted + ","
				+ DataflowInvocation.invocationEnded + ","
				+ DataflowInvocation.inputsDataBinding + ","
				+ DataflowInvocation.outputsDataBinding + ","
				+ DataflowInvocation.parentProcessorEnactmentId + ","
				+ DataflowInvocation.workflowRunId + ","
				+ DataflowInvocation.completed + ") "
				+ " VALUES(?,?,?,?,?,?,?,?,?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, invocation.getDataflowInvocationId());
			ps.setString(2, invocation.getWorkflowId());
			ps.setTimestamp(3, invocation.getInvocationStarted());
			ps.setTimestamp(4, invocation.getInvocationEnded());
			ps.setString(5, invocation.getInputsDataBindingId());
			ps.setString(6, invocation.getOutputsDataBindingId());
			ps.setString(7, invocation.getParentProcessorEnactmentId());
			ps.setString(8, invocation.getWorkflowRunId());
			ps.setBoolean(9, invocation.getCompleted());

			ps.executeUpdate();
		}
	}

	/**
	 * inserts one row into the ARC DB table
	 *
	 * @param sourcePort
	 * @param destinationPort
	 * @param workflowId
	 */
	public void addDataLink(Port sourcePort, Port destinationPort,
			String workflowId) throws SQLException {
		String sql = "INSERT INTO Datalink (workflowId, sourceProcessorName, "
				+ " sourcePortName, destinationProcessorName, destinationPortName,"
				+ " sourcePortId, destinationPortId) "
				+ "VALUES(?,?,?,?,?,?,?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, workflowId);
			ps.setString(2, sourcePort.getProcessorName());
			ps.setString(3, sourcePort.getPortName());
			ps.setString(4, destinationPort.getProcessorName());
			ps.setString(5, destinationPort.getPortName());
			ps.setString(6, sourcePort.getIdentifier());
			ps.setString(7, destinationPort.getIdentifier());

			ps.executeUpdate();
		}
	}

	public void addDataBinding(
			org.apache.taverna.provenance.lineageservice.utils.DataBinding dataBinding)
			throws SQLException {
		String sql = "INSERT INTO " + DataBindingTable.DataBinding + "("
				+ DataBindingTable.dataBindingId + ","
				+ DataBindingTable.portId + "," + DataBindingTable.t2Reference
				+ "," + DataBindingTable.workflowRunId + ") VALUES(?,?,?,?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, dataBinding.getDataBindingId());
			ps.setString(2, dataBinding.getPort().getIdentifier());
			ps.setString(3, dataBinding.getT2Reference());
			ps.setString(4, dataBinding.getWorkflowRunId());
			ps.executeUpdate();
			if (logger.isDebugEnabled())
				logger.debug("adding DataBinding:\n " + dataBinding);
		}
	}

	public void addWFId(String wfId) throws SQLException {
		String sql = "INSERT INTO Workflow (workflowId) VALUES (?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, wfId);
			ps.executeUpdate();
		}
	}

	public void addWFId(String wfId, String parentWorkflowId,
			String externalName, Blob dataflow) throws SQLException {
		String sql = "INSERT INTO Workflow (workflowId, parentWorkflowId, externalName, dataflow) "
				+ "VALUES (?, ?, ?, ?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, wfId);
			ps.setString(2, parentWorkflowId);
			ps.setString(3, externalName);
			ps.setBlob(4, dataflow);

			ps.executeUpdate();
		}
	}

	public void addWorkflowRun(String wfId, String workflowRunId)
			throws SQLException {
		String sql = "INSERT INTO WorkflowRun (workflowRunId, workflowId) VALUES (?,?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, workflowRunId);
			ps.setString(2, wfId);

			ps.executeUpdate();
		}
	}

	/**
	 * insert new processor into the provenance DB
	 *
	 * @param name
	 * @throws SQLException
	 */
	public ProvenanceProcessor addProcessor(String name, String wfID,
			boolean isTopLevel) throws SQLException {
		ProvenanceProcessor provProc = new ProvenanceProcessor();
		provProc.setIdentifier(UUID.randomUUID().toString());
		provProc.setProcessorName(name);
		provProc.setWorkflowId(wfID);
		provProc.setTopLevelProcessor(isTopLevel);
		// pType is unknown
		addProcessor(provProc);
		return provProc;
	}

	/**
	 * add a processor to the static portion of the DB with given name, type and
	 * workflowId scope
	 *
	 * @param name
	 * @param type
	 * @param workflowId
	 * @throws SQLException
	 */
	public void addProcessor(ProvenanceProcessor provProc) throws SQLException {
		String sql = "INSERT INTO Processor (processorName, firstActivityClass, workflowId, isTopLevel, processorId) "
				+ "VALUES (?,?,?,?,?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, provProc.getProcessorName());
			ps.setString(2, provProc.getFirstActivityClassName());
			ps.setString(3, provProc.getWorkflowId());
			ps.setBoolean(4, provProc.isTopLevelProcessor());
			ps.setString(5, provProc.getIdentifier());

			ps.executeUpdate();
		}
	}

	public void addProcessorEnactment(
			org.apache.taverna.provenance.lineageservice.utils.ProcessorEnactment enactment)
			throws SQLException {
		String sql = "INSERT INTO "
				+ ProcessorEnactmentTable.ProcessorEnactment + "("
				+ ProcessorEnactmentTable.processEnactmentId + ","
				+ ProcessorEnactmentTable.workflowRunId + ","
				+ ProcessorEnactmentTable.processorId + ","
				+ ProcessorEnactmentTable.processIdentifier + ","
				+ ProcessorEnactmentTable.iteration + ","
				+ ProcessorEnactmentTable.parentProcessorEnactmentId + ","
				+ ProcessorEnactmentTable.enactmentStarted + ","
				+ ProcessorEnactmentTable.enactmentEnded + ","
				+ ProcessorEnactmentTable.initialInputsDataBindingId + ","
				+ ProcessorEnactmentTable.finalOutputsDataBindingId
				+ ") VALUES(?,?,?,?,?,?,?,?,?,?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, enactment.getProcessEnactmentId());
			ps.setString(2, enactment.getWorkflowRunId());
			ps.setString(3, enactment.getProcessorId());
			ps.setString(4, enactment.getProcessIdentifier());
			ps.setString(5, enactment.getIteration());
			ps.setString(6, enactment.getParentProcessorEnactmentId());
			ps.setTimestamp(7, enactment.getEnactmentStarted());
			ps.setTimestamp(8, enactment.getEnactmentEnded());
			ps.setString(9, enactment.getInitialInputsDataBindingId());
			ps.setString(10, enactment.getFinalOutputsDataBindingId());
			ps.executeUpdate();

			if (logger.isDebugEnabled())
				logger.debug("adding ProcessorEnactment binding:\n "
						+ enactment);
		}
	}

	public String addCollection(String processorId, String collId,
			String parentCollectionId, String iteration, String portName,
			String dataflowId) throws SQLException {
		String newParentCollectionId = null;
		String sql = "INSERT INTO Collection (processorNameRef, workflowRunId, portName, iteration, parentCollIdRef, collId) "
				+ "VALUES(?, ?, ?, ?, ?, ?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			if (parentCollectionId == null)
				// this is a top-level list
				parentCollectionId = "TOP";

			newParentCollectionId = collId;

			ps.setString(1, processorId);
			ps.setString(2, dataflowId);
			ps.setString(3, portName);
			ps.setString(4, iteration);
			ps.setString(5, parentCollectionId);
			ps.setString(6, collId);

			ps.executeUpdate();
		}

		return newParentCollectionId;
	}

	public void addData(String dataRef, String wfInstanceId, Object data)
			throws SQLException {
		String sql = "INSERT INTO Data (dataReference,wfInstanceID,data) VALUES (?,?,?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, dataRef);
			ps.setString(2, wfInstanceId);
			ps.setString(3, (String) data);

			ps.executeUpdate();

			cnt++;

			logger.debug("addData executed on data value from char: "
					+ data);
		} catch (SQLException e) {
			// the same ID will come in several times -- duplications are
			// expected, don't panic
		}
	}

	/**
	 * OBSOLETE
	 * <p/>
	 * adds (dataRef, data) pairs to the Data table (only for string data)
	 */
	public void addData(String dataRef, String wfInstanceId, byte[] data)
			throws SQLException {
		String sql = "INSERT INTO Data (dataReference,wfInstanceID,data) VALUES (?,?,?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, dataRef);
			ps.setString(2, wfInstanceId);
			ps.setBytes(3, data);

			ps.executeUpdate();

			cnt++;

			logger.debug("addData executed on data value from char: " + data);

		} catch (SQLException e) {
			// the same ID will come in several times -- duplications are
			// expected, don't panic
		}
	}

	public void addPortBinding(PortBinding vb) throws SQLException {
		logger.debug("START addVarBinding proc " + vb.getProcessorName()
				+ " port " + vb.getPortName());
		String sql = "INSERT INTO PortBinding (workflowId, processorNameRef, workflowRunId, portName, valueType, value, ref, collIdRef, iteration,positionInColl) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, vb.getWorkflowId());
			ps.setString(2, vb.getProcessorName());
			ps.setString(3, vb.getWorkflowRunId());
			ps.setString(4, vb.getPortName());
			ps.setString(5, vb.getValueType());
			ps.setString(6, vb.getValue());
			ps.setString(7, vb.getReference());
			ps.setString(8, vb.getCollIDRef());
			ps.setString(9, vb.getIteration());
			ps.setInt(10, vb.getPositionInColl());

			logger.debug("addVarBinding query: \n" + ps.toString());
			ps.executeUpdate();
			logger.debug("insert done");

			logger.debug("COMPLETE addVarBinding proc " + vb.getProcessorName()
					+ " port " + vb.getPortName());

			cnt++; // who uses this?
		}
	}

	/**
	 * persists var v back to DB
	 *
	 * @param v
	 * @throws SQLException
	 */
	public void updatePort(Port v) throws SQLException {
		String sql = "UPDATE Port SET isInputPort=?, depth=?,"
				+ "resolvedDepth=?, iterationStrategyOrder=? "
				+ "WHERE portId=?";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, v.isInputPort() ? 1 : 0);
			ps.setInt(2, v.getDepth());
			if (v.isResolvedDepthSet()) {
				ps.setInt(3, v.getResolvedDepth());
			} else {
				ps.setString(3, null);
			}
			ps.setInt(4, v.getIterationStrategyOrder());
			ps.setString(5, v.getIdentifier());
			ps.execute();
		}
	}

	public void updateProcessorEnactment(
			org.apache.taverna.provenance.lineageservice.utils.ProcessorEnactment enactment) {
		String sql = "UPDATE " + ProcessorEnactmentTable.ProcessorEnactment
				+ " SET " + ProcessorEnactmentTable.finalOutputsDataBindingId
				+ "=?, " + ProcessorEnactmentTable.enactmentEnded + "=?"
				+ " WHERE " + ProcessorEnactmentTable.processEnactmentId + "=?";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, enactment.getFinalOutputsDataBindingId());
			ps.setTimestamp(2, enactment.getEnactmentEnded());
			ps.setString(3, enactment.getProcessEnactmentId());

			ps.executeUpdate();
		} catch (SQLException e) {
			logger.warn("****  insert failed for query ", e);
		}
	}

	public void updatePortBinding(PortBinding vb) {
		String sql = "UPDATE PortBinding SET valueType = ?, value = ?, ref = ?, collIdRef = ?, positionInColl = ? "
				+ "WHERE portName = ? AND workflowRunId = ? AND processorNameRef = ? AND iteration = ?"				;
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			// Update values
			ps.setString(1, vb.getValueType());
			ps.setString(2, vb.getValue());
			ps.setString(3, vb.getReference());
			ps.setString(4, vb.getCollIDRef());
			ps.setInt(5, vb.getPositionInColl());
			// Where clauses
			ps.setString(6, vb.getPortName());
			ps.setString(7, vb.getWorkflowRunId());
			ps.setString(8, vb.getProcessorName());
			ps.setString(9, vb.getIteration());

			ps.executeUpdate();

			cnt++;

		} catch (SQLException e) {
			logger.warn("****  insert failed for query ", e);
		}
	}

	public void replaceCollectionRecord(NestedListNode nln, String prevPName,
			String prevPortName) {
		String sql = "DELETE FROM Collection WHERE collId = ? AND workflowRunId = ?"
				+ " AND portName = ? AND processorNameRef = ? AND iteration = ?";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, nln.getCollectionT2Reference());
			ps.setString(2, nln.getWorkflowRunId());
			ps.setString(3, prevPortName);
			ps.setString(4, prevPName);
			ps.setString(5, nln.getIteration());

			ps.executeUpdate();
		} catch (SQLException e) {
			logger.warn("Error replacing collection record", e);
		}

		try {
			addCollection(prevPName, nln.getCollectionT2Reference(),
					nln.getParentCollIdRef(), nln.getIteration(), prevPortName,
					nln.getWorkflowRunId());
		} catch (SQLException e) {
			logger.warn("Collection insert failed", e);
		}
	}

	/**
	 * deletes DB contents for the static structures -- called prior to each run
	 *
	 * @throws SQLException
	 */
	public void clearDBStatic() throws SQLException {
		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement()) {
			stmt.executeUpdate("DELETE FROM Workflow");
			stmt.executeUpdate("DELETE FROM Processor");
			stmt.executeUpdate("DELETE FROM Datalink");
			stmt.executeUpdate("DELETE FROM Port");
			stmt.executeUpdate("DELETE FROM " + ActivityTable.Activity);
			logger.info("DB cleared STATIC");
		} catch (SQLException e) {
			logger.warn("Could not clear static database", e);
		}
	}

	/**
	 * deletes DB contents for the static structures -- called prior to each run
	 *
	 * @throws SQLException
	 */
	public void clearDBStatic(String wfID) throws SQLException {
		try (Connection connection = getConnection()) {
			try (PreparedStatement ps = connection
					.prepareStatement("DELETE FROM Workflow WHERE workflowId = ?")) {
				ps.setString(1, wfID);
				ps.executeUpdate();
			}
			try (PreparedStatement ps = connection
					.prepareStatement("DELETE FROM Processor WHERE workflowId = ?")) {
				ps.setString(1, wfID);
				ps.executeUpdate();
			}
			try (PreparedStatement ps = connection
					.prepareStatement("DELETE FROM Datalink WHERE workflowId = ?")) {
				ps.setString(1, wfID);
				ps.executeUpdate();
			}
			try (PreparedStatement ps = connection
					.prepareStatement("DELETE FROM Port WHERE workflowId = ?")) {
				ps.setString(1, wfID);
				ps.executeUpdate();
			}
			try (PreparedStatement ps = connection
					.prepareStatement("DELETE FROM " + ActivityTable.Activity
							+ " WHERE " + ActivityTable.workflowId + "=?")) {
				ps.setString(1, wfID);
				ps.executeUpdate();
			}
		}
		logger.info("DB cleared STATICfor wfID " + wfID);
	}

	public Set<String> clearDBDynamic() throws SQLException {
		return clearDBDynamic(null);
	}

	private void delete(Connection connection, Object table, String runID) throws SQLException {
		if (runID != null) {
			try (PreparedStatement ps = connection
					.prepareStatement("DELETE FROM " + table
							+ " WHERE workflowRunId = ?")) {
				ps.setString(1, runID);
				ps.executeUpdate();
			}
		} else
			try (PreparedStatement ps = connection
					.prepareStatement("DELETE FROM " + table)) {
				ps.executeUpdate();
			}
	}
	/**
	 * deletes DB contents for all runs -- for testing purposes
	 *
	 * @throws SQLException
	 */
	public Set<String> clearDBDynamic(String runID) throws SQLException {
		Set<String> refsToRemove = collectValueReferences(runID);
		// collect all relevant refs from PortBinding and Collection

		try (Connection connection = getConnection()) {
			delete(connection, "WorkflowRun", runID);
			delete(connection, "PortBinding", runID);
			delete(connection, "Collection", runID);
			delete(connection, DataflowInvocationTable.DataflowInvocation, runID);
			delete(connection, ServiceInvocationTable.ServiceInvocation, runID);
			delete(connection, ProcessorEnactmentTable.ProcessorEnactment, runID);
			delete(connection, DataBindingTable.DataBinding, runID);
		}
		logger.info("DB cleared DYNAMIC");
		return refsToRemove;
	}

	private Set<String> collectValueReferences(String runID)
			throws SQLException {
		Set<String> refs = new HashSet<>();
		try (Connection connection = getConnection()) {
			String sql = "SELECT value FROM PortBinding";
			if (runID != null)
				sql += " WHERE workflowRunId = ?";
			try (PreparedStatement ps = connection.prepareStatement(sql)) {
				if (runID != null)
					ps.setString(1, runID);
				ResultSet rs = ps.executeQuery();
				while (rs.next())
					refs.add(rs.getString("value"));
			}

			sql = "SELECT collId FROM Collection";
			if (runID != null)
				sql += " WHERE workflowRunId = ?";
			try (PreparedStatement ps = connection.prepareStatement(sql)) {
				if (runID != null)
					ps.setString(1, runID);
				ResultSet rs = ps.executeQuery();
				while (rs.next())
					refs.add(rs.getString("collId"));
			}
		} catch (SQLException e) {
			logger.error("Problem collecting value references for: " + runID
					+ " : " + e);
		}
		return refs;
	}

	public void clearDD() {
		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement()) {
			stmt.executeUpdate("DELETE FROM DD");
		} catch (SQLException e) {
			logger.warn("Error execting delete query for provenance records", e);
		}
	}

	/**
	 * used to support the implementation of
	 *
	 * @param pname
	 * @param vFrom
	 * @param valFrom
	 * @param vTo
	 * @param valTo
	 * @param iteration
	 * @param workflowRunId
	 */
	public void writeDDRecord(String pFrom, String vFrom, String valFrom,
			String pTo, String vTo, String valTo, String iteration,
			String workflowRunId) {
		String sql = "INSERT INTO DD (PFrom,VFrom,valFrom,PTo,VTo,valTo,iteration,workflowRun) VALUES "
				+ "(?,?,?,?,?,?,?)";
		try (Connection connection = getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, pFrom);
			ps.setString(2, vFrom);
			ps.setString(3, valFrom);
			ps.setString(4, pTo);
			ps.setString(5, vTo);
			ps.setString(6, iteration);
			ps.setString(7, workflowRunId);
			ps.executeUpdate();
		} catch (SQLException e) {
			logger.warn("Error inserting record into DD", e);
		}
	}

	public void setQuery(ProvenanceQuery query) {
		this.pq = query;
	}

	public ProvenanceQuery getQuery() {
		return this.pq;
	}
}
