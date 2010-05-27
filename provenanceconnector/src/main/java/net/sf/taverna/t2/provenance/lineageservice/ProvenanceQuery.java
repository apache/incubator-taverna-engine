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
import java.util.Set;
import java.util.Map.Entry;

import net.sf.taverna.t2.provenance.connector.JDBCConnector;
import net.sf.taverna.t2.provenance.connector.ProvenanceConnector;
import net.sf.taverna.t2.provenance.connector.ProvenanceConnector.DataBinding;
import net.sf.taverna.t2.provenance.lineageservice.utils.DDRecord;
import net.sf.taverna.t2.provenance.lineageservice.utils.DataLink;
import net.sf.taverna.t2.provenance.lineageservice.utils.DataflowInvocation;
import net.sf.taverna.t2.provenance.lineageservice.utils.NestedListNode;
import net.sf.taverna.t2.provenance.lineageservice.utils.Port;
import net.sf.taverna.t2.provenance.lineageservice.utils.PortBinding;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProcessorEnactment;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.provenance.lineageservice.utils.Workflow;
import net.sf.taverna.t2.provenance.lineageservice.utils.WorkflowRun;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

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
	public Connection getConnection() throws InstantiationException,
	IllegalAccessException, ClassNotFoundException, SQLException {
		return JDBCConnector.getConnection();
	}

	/**
	 * implements a set of query constraints of the form var = value into a
	 * WHERE clause
	 *
	 * @param q0
	 * @param queryConstraints
	 * @return
	 */
	protected String addWhereClauseToQuery(String q0,
			Map<String, String> queryConstraints, boolean terminate) {

		// complete query according to constraints
		StringBuffer q = new StringBuffer(q0);

		boolean first = true;
		if (queryConstraints != null && queryConstraints.size() > 0) {
			q.append(" where ");

			for (Entry<String, String> entry : queryConstraints.entrySet()) {
				if (!first) {
					q.append(" and ");
				}
				q.append(" " + entry.getKey() + " = \'" + entry.getValue() + "\' ");
				first = false;
			}
		}

		return q.toString();
	}

	protected String addOrderByToQuery(String q0, List<String> orderAttr,
			boolean terminate) {

		// complete query according to constraints
		StringBuffer q = new StringBuffer(q0);

		boolean first = true;
		if (orderAttr != null && orderAttr.size() > 0) {
			q.append(" ORDER BY ");

			int i = 1;
			for (String attr : orderAttr) {
				q.append(attr);
				if (i++ < orderAttr.size()) {
					q.append(",");
				}
			}
		}

		return q.toString();
	}

	/**
	 * select Port records that satisfy constraints
	 */
	public List<Port> getPorts(Map<String, String> queryConstraints)
	throws SQLException {
		List<Port> result = new ArrayList<Port>();

		String q0 = "SELECT DISTINCT V.* FROM Port V JOIN WfInstance W ON W.wfnameRef = V.workflowId";

		String q = addWhereClauseToQuery(q0, queryConstraints, true);

		List<String> orderAttr = new ArrayList<String>();
		orderAttr.add("V.iterationStrategyOrder");

		String q1 = addOrderByToQuery(q, orderAttr, true);

		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			boolean success = stmt.execute(q1.toString());

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {
					Port aPort = new Port();

					aPort.setWorkflowId(rs.getString("workflowId"));
                    aPort.setInputPort(rs.getBoolean("isInputPort"));
					aPort.setIdentifier(rs.getString("portId"));
                    aPort.setProcessorName(rs.getString("processorName"));
                    aPort.setProcessorId(rs.getString("processorId"));
                    aPort.setPortName(rs.getString("portName"));
                    aPort.setDepth(rs.getInt("depth"));
                    if (rs.getString("resolvedDepth") != null) {
						aPort.setResolvedDepth(rs.getInt("resolvedDepth"));
					}
					result.add(aPort);
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}


		return result;
	}


	/**
	 * return the input variables for a given processor and a wfInstanceId
	 *
	 * @param pname
	 * @param wfInstanceId
	 * @return list of input variables
	 * @throws SQLException
	 */
	public List<Port> getInputPorts(String pname, String wfID)
	throws SQLException {
		// get (var, proc) from Port to see if it's input/output
		Map<String, String> varQueryConstraints = new HashMap<String, String>();

		varQueryConstraints.put("V.workflowId", wfID);
		varQueryConstraints.put("V.processorName", pname);
		varQueryConstraints.put("V.isInputPort", "1");
		return getPorts(varQueryConstraints);
	}

	/**
	 * return the output variables for a given processor and a wfInstanceId
	 *
	 * @param pname
	 * @param wfInstanceId
	 * @return list of output variables
	 * @throws SQLException
	 */
	public List<Port> getOutputPorts(String pname, String wfID)
	throws SQLException {
		// get (var, proc) from Port to see if it's input/output
		Map<String, String> varQueryConstraints = new HashMap<String, String>();

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
		List<DataLink> result = new ArrayList<DataLink>();

		String q0 = "SELECT A.* FROM Datalink A";

		String q = addWhereClauseToQuery(q0, queryConstraints, true);

		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			boolean success = stmt.execute(q.toString());

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {

					DataLink aDataLink = new DataLink();

					aDataLink.setWorkflowId(rs.getString("workflowId"));
					aDataLink.setSourceProcessorName(rs.getString("sourceProcessorName"));
					aDataLink.setSourcePortName(rs.getString("sourcePortName"));
					aDataLink.setDestinationProcessorName(rs.getString("destinationProcessorName"));
					aDataLink.setDestinationPortName(rs.getString("destinationPortName"));
					aDataLink.setSourcePortId(rs.getString("sourcePortId"));
					aDataLink.setDestinationPortId(rs.getString("destinationPortId"));
					result.add(aDataLink);

				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		
		q = q + " ORDER BY timestamp desc ";

		return result;
	}


	public String getTopLevelWorkflowIdForRun(String runID) throws SQLException {

		List<Workflow> workflows = getWorkflowsForRun(runID);

		for (Workflow w:workflows) { 	
			if (w.getParentWFname() == null) { return w.getWfname(); }
		}		
		return null;		
	}

	/**
	 * returns the names of all workflows (top level + nested) for a given runID
	 * @param runID
	 * @return
	 * @throws SQLException
	 */
	public List<String> getWorkflowIdsForRun(String runID) throws SQLException {

		List<String> wfNames = new ArrayList<String>();

		List<Workflow> workflows = getWorkflowsForRun(runID);

		for (Workflow w:workflows) { wfNames.add(w.getWfname()); }

		return wfNames;
	}


	/**
	 * returns the workflows associated to a single runID
	 * @param runID
	 * @return
	 * @throws SQLException
	 */
	public List<Workflow> getWorkflowsForRun(String runID) throws SQLException {

		List<Workflow> result = new ArrayList<Workflow>();

		String q = "SELECT DISTINCT W.* FROM WfInstance I JOIN Workflow W ON I.wfnameRef = W.wfname WHERE instanceID = ?";

		PreparedStatement stmt = null;
		Connection connection = null;

		try {
			connection = getConnection();
			stmt = connection.prepareStatement(q);
			stmt.setString(1, runID);

			boolean success = stmt.execute();

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {

					Workflow w = new Workflow();
					w.setWfName(rs.getString("wfname"));
					w.setParentWFname(rs.getString("parentWFName"));

					result.add(w);					
				}
			}
		} catch (InstantiationException e) {
			logger.error("Error finding the workflow reference", e);
		} catch (IllegalAccessException e) {
			logger.error("Error finding the workflow reference", e);
		} catch (ClassNotFoundException e) {
			logger.error("Error finding the workflow reference", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return result;
	}



	/**
	 * @param dataflowID
	 * @param conditions currently only understands "from" and "to" as timestamps for range queries
	 * @return
	 * @throws SQLException
	 */
	public List<WorkflowRun> getRuns(String dataflowID, Map<String, String> conditions) throws SQLException {

		PreparedStatement ps = null;
		Connection connection = null;

		List<WorkflowRun> result = new ArrayList<WorkflowRun>();

		String q = "SELECT * FROM WfInstance I join Workflow W on I.wfnameRef = W.wfname";

		List<String> conds = new ArrayList<String>();

		if (dataflowID != null) { conds.add("wfnameRef = '"+dataflowID+"'"); }
		if (conditions != null) {
			if (conditions.get("from") != null) { conds.add("timestamp >= "+conditions.get("from")); }
			if (conditions.get("to") != null) { conds.add("timestamp <= "+conditions.get("to")); }
		}
		if (conds.size()>0) { q = q + " where "+conds.get(0); conds.remove(0); }

		while (conds.size()>0) {
			q = q + " and '"+conds.get(0)+"'"; 
			conds.remove(0); 
		}
		
		q = q + " ORDER BY timestamp desc ";

		try {
			connection = getConnection();
			ps = connection.prepareStatement(q);

			logger.debug(q);
			
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
					WorkflowRun i = new WorkflowRun();
					i.setInstanceID(rs.getString("instanceID"));
					i.setTimestamp(rs.getString("timestamp"));
					i.setWorkflowIdentifier(rs.getString("wfnameRef"));
					i.setWorkflowExternalName(rs.getString("externalName"));
					Blob blob = rs.getBlob("dataflow");
					long length = blob.length();
					blob.getBytes(1, (int) length);
					i.setDataflowBlob(blob.getBytes(1, (int) length));
					result.add(i);
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
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
		List<PortBinding> result = new ArrayList<PortBinding>();

		String q = "SELECT * FROM PortBinding VB " +
		"JOIN Port V ON " +
		"  VB.varNameRef = V.portName " +
		"  AND VB.processorNameRef = V.processorName " +
		"  AND VB.wfNameRef = V.workflowId ";

		q = addWhereClauseToQuery(q, constraints, true);

		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			boolean success = stmt.execute(q);

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {
					PortBinding vb = new PortBinding();

					vb.setWfNameRef(rs.getString("wfNameRef"));
					vb.setVarNameRef(rs.getString("varNameRef"));
					vb.setWfInstanceRef(rs.getString("wfInstanceRef"));
					vb.setValue(rs.getString("value"));

					if (rs.getString("collIdRef") == null || rs.getString("collIdRef").equals("null")) {
						vb.setCollIDRef(null);
					} else {
						vb.setCollIDRef(rs.getString("collIdRef"));
					}

					vb.setIterationVector(rs.getString("iteration"));
					vb.setprocessorNameRef(rs.getString("processorNameRef"));
					vb.setPositionInColl(rs.getInt("positionInColl"));

					result.add(vb);
				}

			}
		} catch (Exception e) {
			logger.warn("Add VB failed", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return result;
	}

	public List<NestedListNode> getNestedListNodes(
			Map<String, String> constraints) throws SQLException {

		List<NestedListNode> result = new ArrayList<NestedListNode>();

		String q = "SELECT * FROM Collection C ";

		q = addWhereClauseToQuery(q, constraints, true);

		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
		} catch (InstantiationException e) {
			logger.error("Error finding the nested list nodes", e);
		} catch (IllegalAccessException e) {
			logger.error("Error finding the nested list nodes", e);
		} catch (ClassNotFoundException e) {
			logger.error("Error finding the nested list nodes", e);
		}

		boolean success;
		try {
			success = stmt.execute(q);
			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {
					PortBinding vb = new PortBinding();

					NestedListNode nln = new NestedListNode();

					nln.setCollId(rs.getString("collId"));
					nln.setParentCollIdRef(rs.getString("parentCollIdRef"));
					nln.setWfInstanceRef(rs.getString("wfInstanceRef"));
					nln.setprocessorNameRef(rs.getString("processorNameRef"));
					nln.setVarNameRef(rs.getString("varNameRef"));
					nln.setIteration(rs.getString("iteration"));

					result.add(nln);

				}
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}


		return result;
	}

	public Map<String, Integer> getPredecessorsCount(String wfInstanceID) {

		PreparedStatement ps = null;

		Map<String, Integer> result = new HashMap<String, Integer>();

		// get all datalinks for the entire workflow structure for this particular instance
		Connection connection = null;
		try {

			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT A.sourceProcessorName as source , A.destinationProcessorName as sink, A.workflowId as wfName1, W1.wfName as wfName2, W2.wfName as wfName3 " +
					"FROM Datalink A join WfInstance I on A.workflowId = I.wfnameRef " +
					"left outer join Workflow W1 on W1.externalName = A.sourceProcessorName " +
					"left outer join Workflow W2 on W2.externalName = A.destinationProcessorName " +
			"where I.instanceID = ?");
			ps.setString(1, wfInstanceID);
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {

					String sink = rs.getString("sink");
					String source = rs.getString("source");

					if (result.get(sink) == null) {
						result.put(sink, 0);
					}

					String name1 = rs.getString("wfName1");
					String name2 = rs.getString("wfName2");
					String name3 = rs.getString("wfName3");

					if (isDataflow(source) && name1.equals(name2)) {
						continue;
					}
					if (isDataflow(sink) && name1.equals(name3)) {
						continue;
					}

					result.put(sink, result.get(sink) + 1);
				}
			}
		} catch (InstantiationException e1) {
			logger.warn("Could not execute query", e1);
		} catch (IllegalAccessException e1) {
			logger.warn("Could not execute query", e1);
		} catch (ClassNotFoundException e1) {
			logger.warn("Could not execute query", e1);
		} catch (SQLException e) {
			logger.error("Error executing query", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					logger.error("There was an error closing the database connection", ex);
				}
			}
		}
		return result;
	}

	/**
	 * new impl of getProcessorsIncomingLinks whicih avoids complications due to nesting, and relies on the wfInstanceID
	 * rather than the wfnameRef
	 * @param wfInstanceID
	 * @return
	 */
	public Map<String, Integer> getPredecessorsCountOld(String wfInstanceID) {

		PreparedStatement ps = null;

		Map<String, Integer> result = new HashMap<String, Integer>();

		// get all datalinks for the entire workflow structure for this particular instance
		Connection connection = null;
		try {

			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT destinationProcessorName, P1.firstActivityClass, count(*) as pred " +
					" FROM Datalink A join WfInstance I on A.workflowId = I.wfnameRef " +
					" join Processor P1 on P1.processorName = A.destinationProcessorName " +
					" join Processor P2 on P2.processorName = A.sourceProcessorName " +
					"  where I.instanceID = ? " +
					"  and P2.firstActivityClass <> '" + ProvenanceProcessor.DATAFLOW_ACTIVITY + "' " +
					" and ((P1.firstActivityClass = '" + ProvenanceProcessor.DATAFLOW_ACTIVITY + "'  and P1.workflowId = A.workflowId) or " +
					" (P1.firstActivityClass <> '" + ProvenanceProcessor.DATAFLOW_ACTIVITY + "' )) " +
			" group by A.destinationProcessorName, firstActivityClass");
			ps.setString(1, wfInstanceID);
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {

					int cnt = rs.getInt("pred");

					result.put(rs.getString("destinationProcessorName"), new Integer(cnt));
				}
			}
		} catch (InstantiationException e1) {
			logger.warn("Could not execute query", e1);
		} catch (IllegalAccessException e1) {
			logger.warn("Could not execute query", e1);
		} catch (ClassNotFoundException e1) {
			logger.warn("Could not execute query", e1);
		} catch (SQLException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					logger.error("There was an error closing the database connection", ex);
				}
			}
		}
		return result;
	}

	/**
	 * used in the toposort phase -- propagation of anl() values through the
	 * graph
	 *
	 * @param wfnameRef
	 *            reference to static wf name
	 * @return a map <processor name> --> <incoming links count> for each
	 *         processor, without counting the datalinks from the dataflow input to
	 *         processors. So a processor is at the root of the graph if it has
	 *         no incoming links, or all of its incoming links are from dataflow
	 *         inputs.<br/>
	 *         Note: this must be checked for processors that are roots of
	 *         sub-flows... are these counted as top-level root nodes??
	 */
	public Map<String, Integer> getProcessorsIncomingLinks(String wfnameRef)
	throws SQLException {
		Map<String, Integer> result = new HashMap<String, Integer>();

		boolean success;

		String currentWorkflowProcessor = null;

		PreparedStatement ps = null;

		Statement stmt;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
			"SELECT processorName, firstActivityClass FROM Processor WHERE workflowId = ?");
			ps.setString(1, wfnameRef);

			success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {

					// PM CHECK 6/09
					if (rs.getString("firstActivityClass").equals(ProvenanceProcessor.DATAFLOW_ACTIVITY)) {
						currentWorkflowProcessor = rs.getString("processorName");
						logger.info("currentWorkflowProcessor = " + currentWorkflowProcessor);
					}
					result.put(rs.getString("processorName"), new Integer(0));
				}
			}
		} catch (InstantiationException e1) {
			logger.warn("Could not execute query", e1);
		} catch (IllegalAccessException e1) {
			logger.warn("Could not execute query", e1);
		} catch (ClassNotFoundException e1) {
			logger.warn("Could not execute query", e1);
		} finally {
			try {
				connection.close();
			} catch (SQLException ex) {
				logger.error("An error occurred closing the database connection", ex);
			}
			connection = null;
		}

		// fetch the name of the top-level dataflow. We use this to exclude datalinks
		// outgoing from its inputs

		////////////////
		// CHECK below -- gets confused on nested workflows
		////////////////
		String parentWF = getParentOfWorkflow(wfnameRef);
		if (parentWF == null) {
			parentWF = wfnameRef;  // null parent means we are the top
		}
		logger.debug("parent WF: " + parentWF);

		// get nested dataflows -- we want to avoid these in the toposort algorithm
		List<ProvenanceProcessor> procs = getProcessorsShallow(
				ProvenanceProcessor.DATAFLOW_ACTIVITY,
				parentWF);

		StringBuffer pNames = new StringBuffer();
		pNames.append("(");
		boolean first = true;
		for (ProvenanceProcessor p : procs) {

			if (!first) {
				pNames.append(",");
			} else {
				first = false;
			}
			pNames.append(" '" + p.getProcessorName() + "' ");
		}
		pNames.append(")");


		// exclude processors connected to inputs -- those have 0 predecessors
		// for our purposes
		// and we add them later

		// PM 6/09 not sure we need to exclude datalinks going into sub-flows?? so commented out the condition
		String q = "SELECT destinationProcessorName, count(*) as cnt " + "FROM Datalink " + "WHERE workflowId = \'" + wfnameRef + "\' " + "AND destinationProcessorName NOT IN " + pNames + " " //		+ "AND sourceProcessorName NOT IN " + pNames
		+ " GROUP BY destinationProcessorName";

		logger.info("executing \n" + q);

		try {
			connection = getConnection();
			stmt = connection.createStatement();
			success = stmt.execute(q);
			if (success) {
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {

					if (!rs.getString("destinationProcessorName").equals(currentWorkflowProcessor)) {
						result.put(rs.getString("destinationProcessorName"), new Integer(rs.getInt("cnt")));
					}
				}
				result.put(currentWorkflowProcessor, 0);
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			try {
				connection.close();
			} catch (SQLException ex) {
				logger.error("An error occurred closing the database connection", ex);
			}
		}

		return result;
	}

	public List<Port> getSuccPorts(String processorName, String portName,
			String workflowId) throws SQLException {

		List<Port> result = new ArrayList<Port>();
		PreparedStatement ps = null;
		Connection connection = null;

		try {
			connection = getConnection();
			String sql = "SELECT v.* " + "FROM Datalink a JOIN Port v ON a.destinationProcessorName = v.processorName " + "AND  a.destinationPortName = v.portName " + "AND a.workflowId = v.workflowId " + "WHERE sourcePortName=? AND sourceProcessorName=?";
			if (workflowId != null) {
				sql = sql + 
				" AND a.workflowId=?";
			}
			ps = connection.prepareStatement(sql);

			ps.setString(1, portName);
			ps.setString(2, processorName);
			if (workflowId != null) {
				ps.setString(3, workflowId);
			}
			
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
                    Port aPort = new Port();

                    aPort.setWorkflowId(rs.getString("workflowId"));
                    aPort.setInputPort(rs.getBoolean("isInputPort"));
					aPort.setIdentifier(rs.getString("portId"));
                    aPort.setProcessorName(rs.getString("processorName"));
                    aPort.setProcessorId(rs.getString("processorId"));
                    aPort.setPortName(rs.getString("portName"));
                    aPort.setDepth(rs.getInt("depth"));
                    if (rs.getString("resolvedDepth") != null) {
						aPort.setResolvedDepth(rs.getInt("resolvedDepth"));
					}                    
                    result.add(aPort);
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}

		return result;
	}

	public List<String> getSuccProcessors(String pName, String wfNameRef, String wfInstanceId)
	throws SQLException {
		List<String> result = new ArrayList<String>();

		PreparedStatement ps = null;

		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT distinct destinationProcessorName FROM Datalink A JOIN Wfinstance I on A.workflowId = I.wfnameRef " + "WHERE A.workflowId = ? and I.instanceID = ? AND sourceProcessorName = ?");
			ps.setString(1, wfNameRef);
			ps.setString(2, wfInstanceId);
			ps.setString(3, pName);

			boolean success = ps.execute();


			if (success) {
				ResultSet rs = ps.getResultSet();

				while (rs.next()) {
					result.add(rs.getString("destinationProcessorName"));
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}

		return result;
	}

	/**
	 * get all processors of a given type within a structure identified by
	 * wfnameRef (reference to dataflow). type constraint is ignored if value is null.<br>
	 * this only returns the processor for the input wfNameRef, without going into any neted workflows
	 *
	 * @param workflowId
	 * @param firstActivityClass
	 * @return a list, that contains at most one element
	 * @throws SQLException
	 */
	public List<ProvenanceProcessor> getProcessorsShallow(String firstActivityClass, String workflowId)
	throws SQLException {
		Map<String, String> constraints = new HashMap<String, String>();

		constraints.put("P.workflowId", workflowId);
		if (firstActivityClass != null) {
			constraints.put("P.firstActivityClass", firstActivityClass);
		}
		return getProcessors(constraints);
	}
	

	public ProvenanceProcessor getProvenanceProcessorByName(
			String workflowId, String processorName) {
		
		Map<String, String> constraints = new HashMap<String, String>();
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
		Map<String, String> constraints = new HashMap<String, String>();
		constraints.put("P.processorId", processorId);
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


	/**
	 * this is similar to {@link #getProcessorsShallow(String, String)} but it recursively fetches all processors
	 * within nested workflows. The result is collected in the form of a map: wfName -> {ProvenanceProcessor}
	 * @param firstActivityClass
	 * @param workflowId
	 * @return a map: wfName -> {ProvenanceProcessor} where wfName is the name of a (possibly nested) workflow, and 
	 * the values are the processors within that workflow
	 */
	public Map<String, List<ProvenanceProcessor>> getProcessorsDeep(String firstActivityClass, String workflowId) {

		Map<String, List<ProvenanceProcessor>> result = new HashMap<String, List<ProvenanceProcessor>>();

		List<ProvenanceProcessor> currentProcs;

		try {
			currentProcs = getProcessorsShallow(null, workflowId);
			List<ProvenanceProcessor> matchingProcessors = result.put(workflowId, new ArrayList<ProvenanceProcessor>());
			for (ProvenanceProcessor pp:currentProcs) {
				if (firstActivityClass==null || pp.getFirstActivityClassName().equals(firstActivityClass)) {
					matchingProcessors.add(pp);
				}				
				if (pp.getFirstActivityClassName().equals(ProvenanceProcessor.DATAFLOW_ACTIVITY)) {
					// recurse 
					result.putAll(getProcessorsDeep(firstActivityClass, pp.getWorkflowId()));					
				}
			}
		} catch (SQLException e) {
			logger.error("Problem getting nested workflow processors for: " + workflowId, e);
		}
		return result;
	}

	/**
	 * generic method to fetch processors subject to additional query constraints
	 * @param constraints
	 * @return
	 * @throws SQLException
	 */
	public List<ProvenanceProcessor> getProcessors(
			Map<String, String> constraints) throws SQLException {
		List<ProvenanceProcessor> result = new ArrayList<ProvenanceProcessor>();

		String q = "SELECT P.* FROM Processor P";
				//" JOIN WfInstance W ON P.wfInstanceRef = W.wfnameRef "+
		         //  "JOIN Workflow WF on W.wfnameRef = WF.wfname";

		q = addWhereClauseToQuery(q, constraints, true);

		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			boolean success = stmt.execute(q);

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {
					ProvenanceProcessor proc = new ProvenanceProcessor();
					proc.setIdentifier(rs.getString("processorId"));
					proc.setProcessorName(rs.getString("processorName"));
					proc.setFirstActivityClassName(rs.getString("firstActivityClass"));
					proc.setWorkflowId(rs.getString("workflowId"));
					result.add(proc);

				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return result;
	}

	
	public String getProcessorForWorkflow(String workflowID) {
	
		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT processorName from Processor WHERE workflowId=?");
			ps.setString(1, workflowID);

			boolean success = ps.execute();
			if (success) {
				ResultSet rs = ps.getResultSet();
				if (rs.next()) {  return rs.getString("processorName"); }
			}
		} catch (SQLException e) {
			logger.error("Problem getting processor for workflow: " + workflowID, e);
		} catch (InstantiationException e) {
			logger.error("Problem getting processor for workflow: " + workflowID, e);
		} catch (IllegalAccessException e) {
			logger.error("Problem getting processor for workflow: " + workflowID, e);
		} catch (ClassNotFoundException e) {
			logger.error("Problem getting processor for workflow: " + workflowID, e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error("Problem getting processor for workflow: " + workflowID, e);
				}
			}
		}
		return null;
	}
	
	/**
	 * simplest possible pinpoint query. Uses iteration info straight away. Assumes result is in PortBinding not in Collection
	 *
	 * @param wfInstance
	 * @param pname
	 * @param vname
	 * @param iteration
	 * @return
	 */
	public LineageSQLQuery simpleLineageQuery(String wfInstance, String wfNameRef, String pname,
			String vname, String iteration) {
		LineageSQLQuery lq = new LineageSQLQuery();

		String q1 = "SELECT * FROM PortBinding VB join Port V " + 
		"on (VB.varNameRef = V.portName and VB.processorNameRef =  V.processorName and VB.wfNameRef=V.workflowId) " + 
		"JOIN WfInstance W ON VB.wfInstanceRef = W.instanceID and VB.wfNameRef = W.wfnameRef ";

		// constraints:
		Map<String, String> lineageQueryConstraints = new HashMap<String, String>();

		lineageQueryConstraints.put("W.instanceID", wfInstance);
		lineageQueryConstraints.put("VB.processorNameRef", pname);
		lineageQueryConstraints.put("VB.wfNameRef", wfNameRef);

		if (vname != null) {
			lineageQueryConstraints.put("VB.varNameRef", vname);
		}
		if (iteration != null) {
			lineageQueryConstraints.put("VB.iteration", iteration);
		}

		q1 = addWhereClauseToQuery(q1, lineageQueryConstraints, false); // false:
		// do
		// not
		// terminate
		// query

		// add order by clause
		List<String> orderAttr = new ArrayList<String>();
		orderAttr.add("varNameRef");
		orderAttr.add("iteration");

		q1 = addOrderByToQuery(q1, orderAttr, true);

		logger.debug("Query is: " + q1);
		lq.setVbQuery(q1);

		return lq;
	}

	/**
	 * if var2Path is null this generates a trivial query for the current output
	 * var and current path
	 *
	 * @param wfInstanceID
	 * @param proc
	 * @param var2Path
	 * @param outputVar
	 * @param path
	 * @param returnOutputs
	 *            returns inputs *and* outputs if set to true
	 * @return
	 */
	public List<LineageSQLQuery> lineageQueryGen(String wfInstanceID, String proc,
			Map<Port, String> var2Path, Port outputVar, String path,
			boolean returnOutputs) {
		// setup
		StringBuffer effectivePath = new StringBuffer();

		List<LineageSQLQuery> newQueries = new ArrayList<LineageSQLQuery>();

		// use the calculated path for each input var
		boolean isInput = true;
		for (Port v : var2Path.keySet()) {
			LineageSQLQuery q = generateSQL2(wfInstanceID, proc, v.getPortName(), var2Path.get(v), isInput);
			if (q != null) {
				newQueries.add(q);
			}
		}

		// is returnOutputs is true, then use proc, path for the output var as well
		if (returnOutputs) {

			isInput = false;

			LineageSQLQuery q = generateSQL2(wfInstanceID, proc, outputVar.getPortName(), path, isInput);  // && !var2Path.isEmpty());
			if (q != null) {
				newQueries.add(q);
			}
		}
		return newQueries;


	}

	protected LineageSQLQuery generateSQL2(String wfInstance, String proc,
			String var, String path, boolean returnInput) {

		LineageSQLQuery lq = new LineageSQLQuery();

		// constraints:
		Map<String, String> collQueryConstraints = new HashMap<String, String>();

		// base Collection query
		String collQuery = "SELECT * FROM Collection C JOIN WfInstance W ON " + "C.wfInstanceRef = W.instanceID " + "JOIN Port V on " + "V.wfInstanceRef = W.wfnameRef and C.processorNameRef = V.processorNameRef and C.varNameRef = V.varName ";

		collQueryConstraints.put("W.instanceID", wfInstance);
		collQueryConstraints.put("C.processorNameRef", proc);

		if (path != null && path.length() > 0) {
			collQueryConstraints.put("C.iteration", "[" + path + "]"); // PM 1/09 -- path
		}

		// inputs or outputs?
		if (returnInput) {
			collQueryConstraints.put("V.isInputPort", "1");
		} else {
			collQueryConstraints.put("V.isInputPort", "0");
		}

		collQuery = addWhereClauseToQuery(collQuery, collQueryConstraints, false);

		lq.setCollQuery(collQuery);

		//  vb query

		Map<String, String> vbQueryConstraints = new HashMap<String, String>();

		// base PortBinding query
		String vbQuery = "SELECT * FROM PortBinding VB JOIN WfInstance W ON " + 
						 "VB.wfInstanceRef = W.instanceID " + 
						 "JOIN Port V on " + 
						 "V.wfInstanceRef = W.wfnameRef and VB.processorNameRef = V.processorNameRef and VB.varNameRef = V.varName "; 

		vbQueryConstraints.put("W.instanceID", wfInstance);
		vbQueryConstraints.put("VB.processorNameRef", proc);
		vbQueryConstraints.put("VB.varNameRef", var);

		if (path != null && path.length() > 0) {
			vbQueryConstraints.put("VB.iteration", "[" + path + "]"); // PM 1/09 -- path
		}

		// limit to inputs?
		if (returnInput) {
			vbQueryConstraints.put("V.isInputPort", "1");
		} else {
			vbQueryConstraints.put("V.isInputPort", "0");
		}

		vbQuery = addWhereClauseToQuery(vbQuery, vbQueryConstraints, false);

		List<String> orderAttr = new ArrayList<String>();
		orderAttr.add("varNameRef");
		orderAttr.add("iteration");

		vbQuery = addOrderByToQuery(vbQuery, orderAttr, true);

		lq.setVbQuery(vbQuery);

		return lq;
	}

	/**
	 * if effectivePath is not null: query varBinding using: wfInstanceRef =
	 * wfInstance, iteration = effectivePath, processorNameRef = proc if input vars is
	 * null, then use the output var this returns the bindings for the set of
	 * input vars at the correct iteration if effectivePath is null: fetch
	 * PortBindings for all input vars, without constraint on the iteration<br/>
	 * additionally, try querying the collection table first -- if the query succeeds, it means
	 * the path is pointing to an internal node in the collection, and we just got the right node.
	 * Otherwise, query PortBinding for the leaves
	 *
	 * @param wfInstance
	 * @param proc
	 * @param effectivePath
	 * @param returnOutputs
	 *            returns both inputs and outputs if set to true
	 * @return
	 */
	public LineageSQLQuery generateSQL(String wfInstance, String proc,
			String effectivePath, boolean returnOutputs) {

		LineageSQLQuery lq = new LineageSQLQuery();

		// constraints:
		Map<String, String> collQueryConstraints = new HashMap<String, String>();

		// base Collection query
		String collQuery = "SELECT * FROM Collection C JOIN WfInstance W ON " + "C.wfInstanceRef = W.instanceID " + "JOIN Port V on " + "V.wfInstanceRef = W.wfnameRef and C.processorNameRef = V.processorNameRef and C.varNameRef = V.varName ";

		collQueryConstraints.put("W.instanceID", wfInstance);
		collQueryConstraints.put("C.processorNameRef", proc);

		if (effectivePath != null && effectivePath.length() > 0) {
			collQueryConstraints.put("C.iteration", "[" + effectivePath.toString() + "]"); // PM 1/09 -- path
		}

		// limit to inputs?
		if (returnOutputs) {
			collQueryConstraints.put("V.isInputPort", "1");
		}

		collQuery = addWhereClauseToQuery(collQuery, collQueryConstraints, false);

		lq.setCollQuery(collQuery);

		//  vb query

		Map<String, String> vbQueryConstraints = new HashMap<String, String>();

		// base PortBinding query
		String vbQuery = "SELECT * FROM PortBinding VB JOIN WfInstance W ON " + 
						 "VB.wfInstanceRef = W.instanceID " + 
						 "JOIN Port V on " + 
						 "V.wfInstanceRef = W.wfnameRef and VB.processorNameRef = V.processorNameRef and VB.varNameRef = V.varName "; 

		vbQueryConstraints.put("W.instanceID", wfInstance);
		vbQueryConstraints.put("VB.processorNameRef", proc);

		if (effectivePath != null && effectivePath.length() > 0) {
			vbQueryConstraints.put("VB.iteration", "[" + effectivePath.toString() + "]"); // PM 1/09 -- path
		}

		// limit to inputs?
		if (!returnOutputs) {
			vbQueryConstraints.put("V.isInputPort", "1");
		}

		vbQuery = addWhereClauseToQuery(vbQuery, vbQueryConstraints, false);

		List<String> orderAttr = new ArrayList<String>();
		orderAttr.add("varNameRef");
		orderAttr.add("iteration");

		vbQuery = addOrderByToQuery(vbQuery, orderAttr, true);


		lq.setVbQuery(vbQuery);

		return lq;
	}

	public Dependencies runCollectionQuery(LineageSQLQuery lq) throws SQLException {

		String q = lq.getCollQuery();

		Dependencies lqr = new Dependencies();

		if (q == null) {
			return lqr;
		}

		logger.debug("running collection query: " + q);

		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			boolean success = stmt.execute(q);

			if (success) {
				ResultSet rs = stmt.getResultSet();


				while (rs.next()) {

					String type = lqr.ATOM_TYPE; // temp -- FIXME

					String wfNameRef = rs.getString("wfNameRef");
					String wfInstance = rs.getString("wfInstanceRef");
					String proc = rs.getString("processorNameRef");
					String var = rs.getString("varNameRef");
					String it = rs.getString("iteration");
					String coll = rs.getString("collID");
					String parentColl = rs.getString("parentCollIDRef");

					lqr.addLineageQueryResultRecord(wfNameRef, proc, var, wfInstance,
							it, coll, parentColl, null, null, type, false, true);  // true -> is a collection
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
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
	public Dependencies runVBQuery(LineageSQLQuery lq, boolean includeDataValue) throws SQLException {

		String q = lq.getVbQuery();

		logger.debug("running VB query: " + q);

		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			boolean success = stmt.execute(q);

			if (success) {
				ResultSet rs = stmt.getResultSet();

				Dependencies lqr = new Dependencies();

				while (rs.next()) {

					String type = lqr.ATOM_TYPE; // temp -- FIXME

					String wfNameRef = rs.getString("wfNameRef");
					String wfInstance = rs.getString("wfInstanceRef");
					String proc = rs.getString("processorNameRef");
					String var = rs.getString("varNameRef");
					String it = rs.getString("iteration");
					String coll = rs.getString("collIDRef");
					String value = rs.getString("value");
					boolean isInput = (rs.getInt("isInputPort") == 1) ? true
							: false;

					
					// FIXME there is no D and no VB - this is in generateSQL,
					// not simpleLineageQuery
					// commented out as D table no longer available. Need to replace this with deref from DataManager
//					if (includeDataValue) {
//						String resolvedValue = rs.getString("D.data");

						// System.out.println("resolved value: "+resolvedValue);
//						lqr.addLineageQueryResultRecord(wfNameRef, proc, var, wfInstance,
//								it, coll, null, value, resolvedValue, type, isInput, false);  // false -> not a collection
//					} else {

						// FIXME if the data is required then the query needs
						// fixing
						lqr.addLineageQueryResultRecord(wfNameRef, proc, var, wfInstance,
								it, coll, null, value, null, type, isInput, false);
//					}
				}
				return lqr;
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
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

		if (result.getRecords().size() == 0) // query was really VB
		{
			return runVBQuery(lq, includeDataValue);
		}

		return result;
	}

	public List<Dependencies> runLineageQueries(
			List<LineageSQLQuery> lqList, boolean includeDataValue)
			throws SQLException {

		List<Dependencies> allResults = new ArrayList<Dependencies>();

		if (lqList == null) {
			logger.warn("lineage queries list is NULL, nothing to evaluate");
			return allResults;
		}

		for (LineageSQLQuery lq : lqList) {
			if (lq == null) {
				continue;
			}
			allResults.add(runLineageQuery(lq, includeDataValue));
		}

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

			if (currentVar != null && record.getVname().equals(currentVar)) { // multiple
				// occurrences
				addToCollection(record, d); // adds record to d in the correct
				// position given by the iteration
				// vector
			}
			if (currentVar == null) {
				currentVar = record.getVname();
			}
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

			List<Element> children = currentEl.getChildren();
			if (index < children.size()) { // we already have the child, just
				// descend
				currentEl = children.get(index);
			} else { // create child
				if (i == itVector.length - 1) { // this is a leaf --> atomic
					// element
					currentEl.addContent(new Element(record.getValue()));
				} else { // create internal element
					currentEl.addContent(new Element("list"));
				}
			}
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

		List<String> result = new ArrayList<String>();

		// dataflow name -> wfRef
		String containerDataflow = getWfNameForDataflow(workflowName);

		// get all processors within containerDataflow
		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
//					"SELECT processorName FROM Processor P  join wfInstance I on P.workflowId = I.wfnameRef " +
//			"where wfInstanceRef = ? and I.instanceID = ?");
					"SELECT processorName FROM Processor P " +
					"WHERE workflowId = ?");
			ps.setString(1, containerDataflow);
//			ps.setString(2, instanceID);


			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
					result.add(rs.getString("processorName"));
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} catch (SQLException e) {
			logger.warn("Could not execute query", e);
		} finally {
			try {
				connection.close();
			} catch (SQLException ex) {
				logger.error("An error occurred closing the database connection", ex);
			}
		}
		return result;
	}

	public String getTopLevelDataflowName(String wfInstanceID) {

		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT processorName FROM Processor P  JOIN WfInstance I on P.workflowId = I.wfnameRef " +
			"where  I.instanceID =? and isTopLevel = 1");


			ps.setString(1, wfInstanceID);
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				if (rs.next()) {
					return rs.getString("processorName");
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} catch (SQLException e) {
			logger.warn("Could not execute query", e);
		} finally {
			try {
				connection.close();
			} catch (SQLException ex) {
				logger.error("An error occurred closing the database connection", ex);
			}
		}
		return null;
	}

	
	/**
	 * returns the internal ID of a dataflow given its external name
	 * @param dataflowName
	 * @param instanceID
	 * @return
	 * @deprecated as workflow 'names' are not globally unique, this method should not be used!
	 */
	@Deprecated
	public String getWfNameForDataflow(String dataflowName) {

		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();

			ps = connection.prepareStatement(
//			"SELECT wfname FROM Workflow W join WfInstance I on W.wfname = I.wfNameRef WHERE W.externalName = ? and I.instanceID = ?");
			"SELECT wfname FROM Workflow W WHERE W.externalName = ?");
			ps.setString(1, dataflowName);
//			ps.setString(2, instanceID);

			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				if (rs.next()) {
					return rs.getString("wfname");
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} catch (SQLException e) {
			logger.error("Could not execute query", e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				logger.error("An error occurred closing the database connection", ex);
			}
		}
		return null;
	}

	public List<String> getChildrenOfWorkflow(String parentWorkflowId)
	throws SQLException {

		List<String> result = new ArrayList<String>();

		PreparedStatement ps = null;
		Connection connection = null;

		try {
			connection = getConnection();
			ps = connection.prepareStatement(
			"SELECT wfname FROM Workflow WHERE parentWFname = ? ");
			ps.setString(1, parentWorkflowId);

			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				while (rs.next()) {
					result.add(rs.getString("wfname"));
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return result;
	}

	/**
	 * fetch children of parentWFName from the Workflow table
	 *
	 * @return
	 * @param childWFName
	 * @throws SQLException
	 */
	public String getParentOfWorkflow(String childWFName) throws SQLException {

		PreparedStatement ps = null;
		String result = null;
		Connection connection = null;

		String q = "SELECT parentWFname FROM Workflow WHERE wfname = ?";
		// Statement stmt;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(q);
			ps.setString(1, childWFName);

			logger.debug("getParentOfWorkflow - query: " + q + "  with wfname = " + childWFName);

			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				while (rs.next()) {

					result = rs.getString("parentWFname");

					logger.debug("result: " + result);
					break;

				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}

		return result;
	}

	public List<String> getAllWFnames() throws SQLException {
		List<String> result = new ArrayList<String>();

		String q = "SELECT wfname FROM Workflow";

		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			boolean success = stmt.execute(q);

			if (success) {
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {
					result.add(rs.getString("wfname"));
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
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

		PreparedStatement ps = null;
		Connection connection = null;

		try {
			connection = getConnection();
			ps = connection.prepareStatement(
			"SELECT firstActivityClass FROM Processor WHERE processorName=?");
			ps.setString(1, procName);

			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				if (rs.next() && rs.getString("firstActivityClass") != null && rs.getString("firstActivityClass").equals(ProvenanceProcessor.DATAFLOW_ACTIVITY)) {
					return true;
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}

		return false;
	}

	
	public boolean isTopLevelDataflow(String wfNameID)  {
		
		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT * FROM Workflow W " +
					" where W.wfname = ? ");
			
			ps.setString(1, wfNameID);
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				if (rs.next()) {
					if (rs.getString("parentWFname") == null) return true;
					return false;
				}
			}
		} catch (SQLException e) {
			logger.warn("Could not execute query", e);
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				logger.error("An error occurred closing the database connection", ex);
			}
		}
		return false;
	}
	
	
	public String getTopDataflow(String wfInstanceID) {

		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT processorName FROM Processor P join WfInstance I on P.workflowId = I.wfNameRef " +
					" where I.instanceID = ? " +
			" and isTopLevel = 1 ");
			ps.setString(1, wfInstanceID);
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				if (rs.next()) {
					return rs.getString("processorName");
				}
			}
		} catch (SQLException e) {
			logger.warn("Could not execute query", e);
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				logger.error("An error occurred closing the database connection", ex);
			}
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
			String iteration, String wfInstance) throws SQLException {

		List<DDRecord> result = new ArrayList<DDRecord>();

		Map<String, String> queryConstraints = new HashMap<String, String>();

		queryConstraints.put("pTo", p);
		queryConstraints.put("vTo", var);
		if (value != null) {
			queryConstraints.put("valTo", value);
		}
		if (iteration != null) {
			queryConstraints.put("iteration", iteration);
		}
		if (wfInstance != null) {
			queryConstraints.put("wfInstance", wfInstance);
		}

		String q = "SELECT * FROM   DD ";

		q = addWhereClauseToQuery(q, queryConstraints, true); // true: terminate
		// SQL statement

		Statement stmt;
		Connection connection = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			boolean success = stmt.execute(q);

			if (success) {
				ResultSet rs = stmt.getResultSet();

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
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return null;
	}

	public Set<DDRecord> queryDataLinksForDD(String p, String v, String val,
			String wfInstance) throws SQLException {

		Set<DDRecord> result = new HashSet<DDRecord>();

		PreparedStatement ps = null;
		Connection connection = null;

		String q = "SELECT DISTINCT A.sourceProcessorName AS p, A.sourcePortName AS var, VB.value AS val " + "FROM   Datalink A JOIN PortBinding VB ON VB.varNameRef = A.destinationPortName AND VB.processorNameRef = A.destinationProcessorName " + "JOIN   WfInstance WF ON WF.wfnameRef = A.workflowId AND WF.instanceID = VB.wfInstanceRef  " + "WHERE  WF.instanceID = '" + wfInstance + "' AND A.destinationProcessorName = '" + p + "' AND A.destinationPortName = '" + v + "' AND VB.value = '" + val + "' ";

		// Statement stmt;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT DISTINCT A.sourceProcessorName AS p, A.sourcePortName AS var, VB.value AS val " + "FROM   Datalink A JOIN PortBinding VB ON VB.varNameRef = A.destinationPortName AND VB.processorNameRef = A.destinationProcessorName " + "JOIN   WfInstance WF ON WF.wfnameRef = A.workflowId AND WF.instanceID = VB.wfInstanceRef  " + "WHERE  WF.instanceID = ? AND A.destinationProcessorName = ? AND A.destinationPortName = ? AND VB.value = ?");

			ps.setString(1, wfInstance);
			ps.setString(2, p);
			ps.setString(3, v);
			ps.setString(4, val);

			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				while (rs.next()) {

					DDRecord aDDrecord = new DDRecord();
					aDDrecord.setPTo(rs.getString("p"));
					aDDrecord.setVTo(rs.getString("var"));
					aDDrecord.setValTo(rs.getString("val"));

					result.add(aDDrecord);
				}
				return result;
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return null;
	}

	public Set<DDRecord> queryAllFromValues(String wfInstance)
	throws SQLException {

		Set<DDRecord> result = new HashSet<DDRecord>();

		PreparedStatement ps = null;
		Connection connection = null;

		try {
			connection = getConnection();
			ps = connection.prepareStatement(
			"SELECT DISTINCT PFrom, vFrom, valFrom FROM DD where wfInstance = ?");
			ps.setString(1, wfInstance);

			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				while (rs.next()) {

					DDRecord aDDrecord = new DDRecord();
					aDDrecord.setPFrom(rs.getString("PFrom"));
					aDDrecord.setVFrom(rs.getString("vFrom"));
					aDDrecord.setValFrom(rs.getString("valFrom"));

					result.add(aDDrecord);
				}
				return result;
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}

		return null;

	}


	public boolean isRootProcessorOfWorkflow(String procName, String wfName,
			String wfInstanceId) {

		PreparedStatement ps = null;
		Connection connection = null;

		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT * FROM Datalink A JOIN WfInstance I ON A.workflowId = I.wfnameRef " +
					"JOIN Processor P on P.processorName = A.sourceProcessorName WHERE sourceProcessorName = ? " +
					"AND P.workflowId <> A.workflowId " +
					"AND I.instanceID = ? " +
			"AND destinationProcessorName = ? ");

			ps.setString(1, wfName);
			ps.setString(2, wfInstanceId);
			ps.setString(3, procName);

			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				if (rs.next()) {
					return true;
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} catch (SQLException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					logger.error("There was an error closing the database connection", ex);
				}
			}
		}
		return false;
	}

/**
 * returns a Workflow record from the DB given the workflow internal ID
 * @param dataflowID
 * @return
 */
	public Workflow getWorkflow(String dataflowID) {

		PreparedStatement ps = null;
		Connection connection = null;

		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT * FROM Workflow W "+
			"WHERE wfname = ? ");

			ps.setString(1, dataflowID);

			boolean success = ps.execute();
			if (success) {
				ResultSet rs = ps.getResultSet();

				if (rs.next()) {
					Workflow wf = new Workflow();
					wf.setWfName(rs.getString("wfname"));
					wf.setParentWFname(rs.getString("parentWFName"));
					wf.setExternalName(rs.getString("externalName"));

					return wf;
				} else {
					logger.warn("Could not find workflow " + dataflowID);
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} catch (SQLException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					logger.error("There was an error closing the database connection", ex);
				}
			}
		}
		return null;

	}
	
	/**
	 * @param record a record representing a single value -- possibly within a list hierarchy
	 * @return the URI for topmost containing collection when the input record is within a list hierarchy, or null otherwise
	 */
	public String getContainingCollection(LineageQueryResultRecord record) {

		if (record.getCollIdRef() == null) return null;
		
		String q = "SELECT * FROM Collection where collID = ? and wfInstanceRef = ? and processorNameRef = ? and varNameRef = ?";

		PreparedStatement stmt = null;
		Connection connection = null;

		String parentCollIDRef = null;
		try {
			connection = getConnection();
			stmt = connection.prepareStatement(q);
			
			stmt.setString(1, record.getCollIdRef());
			stmt.setString(2, record.getWfInstance());
			stmt.setString(3, record.getPname());
			stmt.setString(4, record.getVname());

			String tmp = stmt.toString();
			
			boolean success = stmt.execute();

			if (success) {
				ResultSet rs = stmt.getResultSet();

				if (rs.next()) {
					parentCollIDRef = rs.getString("parentCollIDRef");
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query", e);
		} catch (SQLException e) {
			logger.warn("Could not execute query", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.warn("Could not close connection", e);
				}
			}
		}
		
		while (parentCollIDRef != null) {  // INITIALLY not null -- would be TOP if the initial had no parent
			
			String oldParentCollIDRef = parentCollIDRef;
			
			// query Collection again for parent collection
			try {
				connection = getConnection();
				stmt = connection.prepareStatement(q);
				
				stmt.setString(1, oldParentCollIDRef);
				stmt.setString(2, record.getWfInstance());
				stmt.setString(3, record.getPname());
				stmt.setString(4, record.getVname());

				//String tmp = stmt.toString();

				boolean success = stmt.execute();
				if (success) {
					ResultSet rs = stmt.getResultSet();
					if (rs.next()) {
						parentCollIDRef = rs.getString("parentCollIDRef");
						if (parentCollIDRef.equals("TOP")) {
							return oldParentCollIDRef;
						}
					}
				}
			} catch (Exception e) {
				logger.warn("Could not execute query", e);
			}
		}
		return null;
	}

	public List<ProcessorEnactment> getProcessorEnactments(
			String workflowRunId, String... processorPath) {
		return getProcessorEnactments(workflowRunId, (ProcessorEnactment)null, Arrays.asList(processorPath));
	}
	
	private List<ProcessorEnactment> getProcessorEnactments(
			String workflowRunId, ProcessorEnactment processorEnactment,
			List<String> processorPath) {

		String parentProcessorEnactment = null;
		if (processorEnactment != null) {
			parentProcessorEnactment = processorEnactment.getProcessEnactmentId();
		}		
		if (processorPath.size() > 1) {
			// TODO: Make this more efficient by constructing mega-JOIN-SQL.. or specify iterations
			List<ProcessorEnactment> enactments = new ArrayList<ProcessorEnactment>();			
			List<ProcessorEnactment> parentEnactments = getProcessorEnactmentsByProcessorName(workflowRunId, 
					parentProcessorEnactment, processorPath.get(0));
			for (ProcessorEnactment parent : parentEnactments) {
				List<String> childPath = processorPath.subList(1, processorPath.size());
				enactments.addAll(getProcessorEnactments(workflowRunId, parent, childPath));
			}			
			return enactments;			
		} else if (processorPath.size() == 1) {
			return getProcessorEnactmentsByProcessorName(workflowRunId, parentProcessorEnactment, processorPath.get(0));
		} else  {
			return getProcessorEnactmentsByProcessorName(workflowRunId, parentProcessorEnactment, null);						
		}
	}

	public List<ProcessorEnactment> getProcessorEnactmentsByProcessorName(
			String workflowRunId, String parentProcessorEnactmentId, String processorName) {
		ProvenanceConnector.ProcessorEnactment ProcEnact = ProvenanceConnector.ProcessorEnactment.ProcessorEnactment;
		
		String query  = 
				"SELECT " + ProcEnact.enactmentStarted + ","
						+ ProcEnact.enactmentEnded + ","
						+ ProcEnact.finalOutputsDataBindingId + ","
						+ ProcEnact.initialInputsDataBindingId + ","
						+ ProcEnact.ProcessorEnactment + "." + ProcEnact.processorId + " AS procId,"
						+ ProcEnact.processIdentifier + ","
						+ ProcEnact.processEnactmentId + ","
						+ ProcEnact.parentProcessorEnactmentId + ","
						+ ProcEnact.workflowRunId + ","						
						+ ProcEnact.iteration + ","
						+ "Processor.processorName" + " FROM "
						+ ProcEnact.ProcessorEnactment
						+ " INNER JOIN " + "Processor" + " ON "
						+ ProcEnact.ProcessorEnactment + "."+ ProcEnact.processorId 
						+ " = " + "Processor.processorId" + " WHERE "
						+ ProcEnact.workflowRunId + "=? ";
		
		if (processorName != null) {
			// Specific processor
			query = query + " AND Processor.processorName=? ";
		}
		if (parentProcessorEnactmentId == null) {
			// null - ie. top level
			query = query + " AND " + ProcEnact.parentProcessorEnactmentId + " IS NULL";
		} else {
			// not null, ie. inside nested workflow
			query = query + " AND " + ProcEnact.parentProcessorEnactmentId + "=?";
		}
		
		
		ArrayList<ProcessorEnactment> procEnacts = new ArrayList<ProcessorEnactment>();

		PreparedStatement statement;
		Connection connection = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(query);
			int pos = 1;
			statement.setString(pos++, workflowRunId);
			if (processorName != null) {
				statement.setString(pos++, processorName);
			}
			if (parentProcessorEnactmentId != null) {
				statement.setString(pos++, parentProcessorEnactmentId);
			}
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				ProcessorEnactment procEnact = readProcessorEnactment(resultSet);
				procEnacts.add(procEnact);	
			}			
		} catch (SQLException e) {
			logger.warn("Could not execute query " + query, e);
		} catch (InstantiationException e) {
			logger.warn("Could not get database connection", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not get database connection", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not get database connection", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.warn("Could not close connection", e);
				}
			}
		}		
		return procEnacts;
	}
	

	private ProcessorEnactment readProcessorEnactment(ResultSet resultSet) throws SQLException {
		ProvenanceConnector.ProcessorEnactment ProcEnact = ProvenanceConnector.ProcessorEnactment.ProcessorEnactment;
		
		Timestamp enactmentStarted = resultSet.getTimestamp(ProcEnact.enactmentStarted.name());
		Timestamp enactmentEnded = resultSet.getTimestamp(ProcEnact.enactmentEnded.name());
		//String pName = resultSet.getString("processorName");
		String finalOutputsDataBindingId = resultSet.getString(ProcEnact.finalOutputsDataBindingId.name());
		String initialInputsDataBindingId = resultSet.getString(ProcEnact.initialInputsDataBindingId.name());
	
		String iteration = resultSet.getString(ProcEnact.iteration.name());
		String processorId = resultSet.getString("procId");
		String processIdentifier = resultSet.getString(ProcEnact.processIdentifier.name());
		String processEnactmentId = resultSet.getString(ProcEnact.processEnactmentId.name());
		String parentProcessEnactmentId = resultSet.getString(ProcEnact.parentProcessorEnactmentId.name());
		String workflowRunId = resultSet.getString(ProcEnact.workflowRunId.name());
		
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
		ProvenanceConnector.ProcessorEnactment ProcEnact = ProvenanceConnector.ProcessorEnactment.ProcessorEnactment;		
		String query  = 
				"SELECT " + ProcEnact.enactmentStarted + ","
						+ ProcEnact.enactmentEnded + ","
						+ ProcEnact.finalOutputsDataBindingId + ","
						+ ProcEnact.initialInputsDataBindingId + ","
						+ ProcEnact.ProcessorEnactment + "." 
						+ ProcEnact.processorId + " AS procId,"
						+ ProcEnact.processIdentifier + ","
						+ ProcEnact.workflowRunId + ","						
						+ ProcEnact.processEnactmentId + ","
						+ ProcEnact.parentProcessorEnactmentId + ","						
						+ ProcEnact.iteration 
						+ " FROM "
						+ ProcEnact.ProcessorEnactment
						+ " WHERE "
						+ ProcEnact.processEnactmentId + "=?";
					
		PreparedStatement statement;
		Connection connection = null;
		ProcessorEnactment procEnact = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, processorEnactmentId);
			ResultSet resultSet = statement.executeQuery();
			if (! resultSet.next()) {
				logger.warn("Could not find ProcessorEnactment processEnactmentId=" + processorEnactmentId);
				
				return null;
			}
			procEnact = readProcessorEnactment(resultSet);
			if (resultSet.next()) {
				logger.error("Found more than one ProcessorEnactment processEnactmentId=" + processorEnactmentId);
				return null;
			}
		} catch (SQLException e) {
			logger.warn("Could not execute query " + query, e);
		} catch (InstantiationException e) {
			logger.warn("Could not get database connection", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not get database connection", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not get database connection", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.warn("Could not close connection", e);
				}
			}
		}
		return procEnact;
	}
	
	
	public ProcessorEnactment getProcessorEnactmentByProcessId(String workflowRunId,
			String processIdentifier) {
ProvenanceConnector.ProcessorEnactment ProcEnact = ProvenanceConnector.ProcessorEnactment.ProcessorEnactment;		
		String query  = 
				"SELECT " + ProcEnact.enactmentStarted + ","
						+ ProcEnact.enactmentEnded + ","
						+ ProcEnact.finalOutputsDataBindingId + ","
						+ ProcEnact.initialInputsDataBindingId + ","
						+ ProcEnact.ProcessorEnactment + "." + ProcEnact.processorId + " AS procId,"
						+ ProcEnact.processIdentifier + ","
						+ ProcEnact.workflowRunId + ","						
						+ ProcEnact.processEnactmentId + ","
						+ ProcEnact.parentProcessorEnactmentId + ","						
						+ ProcEnact.iteration
						+ " FROM "
						+ ProcEnact.ProcessorEnactment
						+ " WHERE "
						+ ProcEnact.workflowRunId + "=?"
						+ " AND " + ProcEnact.processIdentifier + "=?";

		
		PreparedStatement statement;
		Connection connection = null;
		ProcessorEnactment procEnact = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, workflowRunId);
			statement.setString(2, processIdentifier);
			ResultSet resultSet = statement.executeQuery();
			if (! resultSet.next()) {
				logger.warn("Could not find ProcessorEnactment runId=" + workflowRunId + " processIdentifier=" +processIdentifier);
				return null;
			}
			procEnact = readProcessorEnactment(resultSet);
			if (resultSet.next()) {
				logger.error("Found more than one ProcessorEnactment runId=" + workflowRunId + " processIdentifier=" +processIdentifier);
				return null;
			}
		} catch (SQLException e) {
			logger.warn("Could not execute query " + query, e);
		} catch (InstantiationException e) {
			logger.warn("Could not get database connection", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not get database connection", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not get database connection", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.warn("Could not close connection", e);
				}
			}
		}
		return procEnact;
	}

	public Map<Port, String> getDataBindings(String dataBindingId) {
		HashMap<Port, String> dataBindings = new HashMap<Port, String>();
		String query = "SELECT " 
				+ DataBinding.t2Reference + ","
				+ "Port.portId AS portId," 
				+ "Port.processorName,"
				+ "Port.processorId,"
				+ "Port.isInputPort,"
				+ "Port.portName," 
				+ "Port.depth,"
				+ "Port.resolvedDepth," 
				+ "Port.workflowId"
				+ " FROM " + DataBinding.DataBinding
				+ " INNER JOIN " + "Port" + " ON " 
				+ " Port.portId=" + DataBinding.DataBinding + "." + DataBinding.portId
				+ " WHERE " + DataBinding.dataBindingId + "=?";
		PreparedStatement statement;
		Connection connection = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, dataBindingId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String t2Ref = rs.getString(DataBinding.t2Reference.name());
				
				Port port = new Port();
				port.setWorkflowId(rs.getString("workflowId"));
				port.setInputPort(rs.getBoolean("isInputPort"));				
				port.setIdentifier(rs.getString("portId"));
				port.setProcessorName(rs.getString("processorName"));
				port.setProcessorId(rs.getString("processorId"));
				port.setPortName(rs.getString("portName"));
				port.setDepth(rs.getInt("depth"));
				if (rs.getString("resolvedDepth") != null) {
					port.setResolvedDepth(rs.getInt("resolvedDepth"));
				}
				dataBindings.put(port, t2Ref);
			}
		} catch (SQLException e) {
			logger.warn("Could not execute query " + query, e);
		} catch (InstantiationException e) {
			logger.warn("Could not get database connection", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not get database connection", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not get database connection", e);			
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.warn("Could not close connection", e);
				}
			}
		}
		return dataBindings;		
	}

	public List<Port> getPortsForDataflow(String workflowID) {
		Workflow w = getWorkflow(workflowID);

		Map<String, String> queryConstraints = new HashMap<String, String>();
		queryConstraints.put("workflowId", workflowID);
		queryConstraints.put("processorName", w.getExternalName());

		try {
			return getPorts(queryConstraints);
		} catch (SQLException e) {
			logger.error("Problem getting ports for dataflow: " + workflowID, e);
		}
		return null;
	}

	public List<Port> getPortsForProcessor(String workflowID,
			String processorName) {
		Map<String, String> queryConstraints = new HashMap<String, String>();
		queryConstraints.put("workflowId", workflowID);
		queryConstraints.put("processorName", processorName);

		try {
			return getPorts(queryConstraints);
		} catch (SQLException e) {
			logger.error("Problem getting ports for processor: " + processorName + " worflow: " + workflowID, e);
		}
		return null;
	}

	@SuppressWarnings("static-access")
	public DataflowInvocation getDataflowInvocation(String workflowRunId) {
		net.sf.taverna.t2.provenance.connector.ProvenanceConnector.DataflowInvocation DI = 
			net.sf.taverna.t2.provenance.connector.ProvenanceConnector.DataflowInvocation.DataflowInvocation;
		String query = "SELECT " + 
				  DI.dataflowInvocationId + ","
				+ DI.inputsDataBinding + "," 
				+ DI.invocationEnded + ","
				+ DI.invocationStarted + "," 
				+ DI.outputsDataBinding + ","
				+ DI.parentProcessorEnactmentId + ","  
				+ DI.workflowId + "," 
				+ DI.workflowRunId
				+ " FROM "
				+ DI.DataflowInvocation +
				" WHERE "
				+ DI.parentProcessorEnactmentId + " IS NULL AND "
				+ DI.workflowRunId + "=?";
		PreparedStatement statement;
		Connection connection = null;
		DataflowInvocation dataflowInvocation = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, workflowRunId);
			ResultSet rs = statement.executeQuery();
			if (! rs.next()) {
				logger.warn("Could not find DataflowInvocation for workflowRunId=" + workflowRunId);
				return null;
			}
			dataflowInvocation = new DataflowInvocation();
			dataflowInvocation.setDataflowInvocationId(rs.getString(DI.dataflowInvocationId.name()));
			dataflowInvocation.setInputsDataBindingId(rs.getString(DI.inputsDataBinding.name()));			
			dataflowInvocation.setInvocationEnded(rs.getTimestamp(DI.invocationEnded.name()));
			dataflowInvocation.setInvocationStarted(rs.getTimestamp(DI.invocationStarted.name()));
			dataflowInvocation.setOutputsDataBindingId(rs.getString(DI.outputsDataBinding.name()));
			dataflowInvocation.setParentProcessorEnactmentId(rs.getString(DI.parentProcessorEnactmentId.name()));
			dataflowInvocation.setWorkflowId(rs.getString(DI.workflowId.name()));
			dataflowInvocation.setWorkflowRunId(rs.getString(DI.workflowRunId.name()));
			if (rs.next()) {
				logger.error("Found more than one DataflowInvocation for workflowRunId=" + workflowRunId);
				return null;
			}

		} catch (SQLException e) {
			logger.warn("Could not execute query " + query, e);
		} catch (InstantiationException e) {
			logger.warn("Could not get database connection", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not get database connection", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not get database connection", e);			
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.warn("Could not close connection", e);
				}
			}
		}
		return dataflowInvocation;
	}

	public DataflowInvocation getDataflowInvocation(
			ProcessorEnactment processorEnactment) {
		net.sf.taverna.t2.provenance.connector.ProvenanceConnector.DataflowInvocation DI = 
			net.sf.taverna.t2.provenance.connector.ProvenanceConnector.DataflowInvocation.DataflowInvocation;
		String query = "SELECT " + 
				  DI.dataflowInvocationId + ","
				+ DI.inputsDataBinding + "," 
				+ DI.invocationEnded + ","
				+ DI.invocationStarted + "," 
				+ DI.outputsDataBinding + ","
				+ DI.parentProcessorEnactmentId + ","  
				+ DI.workflowId + "," 
				+ DI.workflowRunId
				+ " FROM "
				+ DI.DataflowInvocation +
				" WHERE "
				+ DI.parentProcessorEnactmentId + "=?";
		PreparedStatement statement;
		Connection connection = null;
		DataflowInvocation dataflowInvocation = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, processorEnactment.getProcessEnactmentId());
			ResultSet rs = statement.executeQuery();
			if (! rs.next()) {
				logger.warn("Could not find DataflowInvocation for processorEnactmentId=" + processorEnactment.getProcessEnactmentId());
				return null;
			}
			dataflowInvocation = new DataflowInvocation();
			dataflowInvocation.setDataflowInvocationId(rs.getString(DI.dataflowInvocationId.name()));
			dataflowInvocation.setInputsDataBindingId(rs.getString(DI.inputsDataBinding.name()));			
			dataflowInvocation.setInvocationEnded(rs.getTimestamp(DI.invocationEnded.name()));
			dataflowInvocation.setInvocationStarted(rs.getTimestamp(DI.invocationStarted.name()));
			dataflowInvocation.setOutputsDataBindingId(rs.getString(DI.outputsDataBinding.name()));
			dataflowInvocation.setParentProcessorEnactmentId(rs.getString(DI.parentProcessorEnactmentId.name()));
			dataflowInvocation.setWorkflowId(rs.getString(DI.workflowId.name()));
			dataflowInvocation.setWorkflowRunId(rs.getString(DI.workflowRunId.name()));
			
			if (rs.next()) {
				logger.error("Found more than one DataflowInvocation for processorEnactmentId=" + processorEnactment.getProcessEnactmentId());
				return null;
			}
			
		} catch (SQLException e) {
			logger.warn("Could not execute query " + query, e);
		} catch (InstantiationException e) {
			logger.warn("Could not get database connection", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not get database connection", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not get database connection", e);			
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.warn("Could not close connection", e);
				}
			}
		}
		return dataflowInvocation;
	}
	
	@SuppressWarnings("static-access")
	public List<DataflowInvocation> getDataflowInvocations(String workflowRunId) {
		net.sf.taverna.t2.provenance.connector.ProvenanceConnector.DataflowInvocation DI = 
			net.sf.taverna.t2.provenance.connector.ProvenanceConnector.DataflowInvocation.DataflowInvocation;
		String query = "SELECT " + 
				  DI.dataflowInvocationId + ","
				+ DI.inputsDataBinding + "," 
				+ DI.invocationEnded + ","
				+ DI.invocationStarted + "," 
				+ DI.outputsDataBinding + ","
				+ DI.parentProcessorEnactmentId + ","  
				+ DI.workflowId + "," 
				+ DI.workflowRunId
				+ " FROM "
				+ DI.DataflowInvocation +
				" WHERE "
				+ DI.workflowRunId + "=?";
		PreparedStatement statement;
		Connection connection = null;
		List<DataflowInvocation> invocations = new ArrayList<DataflowInvocation>();
		try {
			connection = getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, workflowRunId);
			ResultSet rs = statement.executeQuery();
			if (! rs.next()) {
				logger.warn("Could not find DataflowInvocation for workflowRunId=" + workflowRunId);
				return null;
			}
			DataflowInvocation dataflowInvocation = new DataflowInvocation();
			dataflowInvocation.setDataflowInvocationId(rs.getString(DI.dataflowInvocationId.name()));
			dataflowInvocation.setInputsDataBindingId(rs.getString(DI.inputsDataBinding.name()));			
			dataflowInvocation.setInvocationEnded(rs.getTimestamp(DI.invocationEnded.name()));
			dataflowInvocation.setInvocationStarted(rs.getTimestamp(DI.invocationStarted.name()));
			dataflowInvocation.setOutputsDataBindingId(rs.getString(DI.outputsDataBinding.name()));
			dataflowInvocation.setParentProcessorEnactmentId(rs.getString(DI.parentProcessorEnactmentId.name()));
			dataflowInvocation.setWorkflowId(rs.getString(DI.workflowId.name()));
			dataflowInvocation.setWorkflowRunId(rs.getString(DI.workflowRunId.name()));
			invocations.add(dataflowInvocation);
		} catch (SQLException e) {
			logger.warn("Could not execute query " + query, e);
		} catch (InstantiationException e) {
			logger.warn("Could not get database connection", e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not get database connection", e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not get database connection", e);			
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.warn("Could not close connection", e);
				}
			}
		}
		return invocations;
	}



}


