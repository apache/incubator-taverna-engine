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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.taverna.t2.provenance.connector.JDBCConnector;
import net.sf.taverna.t2.provenance.lineageservice.utils.Arc;
import net.sf.taverna.t2.provenance.lineageservice.utils.DDRecord;
import net.sf.taverna.t2.provenance.lineageservice.utils.NestedListNode;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProcBinding;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.provenance.lineageservice.utils.Var;
import net.sf.taverna.t2.provenance.lineageservice.utils.VarBinding;
import net.sf.taverna.t2.provenance.lineageservice.utils.Workflow;
import net.sf.taverna.t2.provenance.lineageservice.utils.WorkflowInstance;

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
	public static String DATAFLOW_TYPE = "net.sf.taverna.t2.activities.dataflow.DataflowActivity";
	
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
	 * select Var records that satisfy constraints
	 */
	public List<Var> getVars(Map<String, String> queryConstraints)
	throws SQLException {
		List<Var> result = new ArrayList<Var>();

		String q0 = "SELECT  * FROM Var V JOIN WfInstance W ON W.wfnameRef = V.wfInstanceRef";

		String q = addWhereClauseToQuery(q0, queryConstraints, true);

		List<String> orderAttr = new ArrayList<String>();
		orderAttr.add("V.order");

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

					Var aVar = new Var();

					aVar.setWfInstanceRef(rs.getString("WfInstanceRef"));

					if (rs.getInt("inputOrOutput") == 1) {
						aVar.setInput(true);
					} else {
						aVar.setInput(false);
					}
					aVar.setPName(rs.getString("pnameRef"));
					aVar.setVName(rs.getString("varName"));
					aVar.setType(rs.getString("type"));
					aVar.setTypeNestingLevel(rs.getInt("nestingLevel"));
					aVar.setActualNestingLevel(rs.getInt("actualNestingLevel"));
					aVar.setANLset((rs.getInt("anlSet") == 1 ? true : false));
					result.add(aVar);

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

	private List<Var> getVarsNoInstance(Map<String, String> queryConstraints)
	throws SQLException {

		List<Var> result = new ArrayList<Var>();

		String q0 = "SELECT  * FROM Var V";

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

					Var aVar = new Var();

					aVar.setWfInstanceRef(rs.getString("WfInstanceRef"));

					if (rs.getInt("inputOrOutput") == 1) {
						aVar.setInput(true);
					} else {
						aVar.setInput(false);
					}
					aVar.setPName(rs.getString("pnameRef"));
					aVar.setVName(rs.getString("varName"));
					aVar.setType(rs.getString("type"));
					aVar.setTypeNestingLevel(rs.getInt("nestingLevel"));
					aVar.setActualNestingLevel(rs.getInt("actualNestingLevel"));
					aVar.setANLset((rs.getInt("anlSet") == 1 ? true : false));
					result.add(aVar);

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

	public List<String> getVarValues(String wfInstance, String pname,
			String vname) throws SQLException {

		List<String> result = new ArrayList<String>();

		String q0 = "SELECT  value FROM VarBinding VB";

		Map<String, String> queryConstraints = new HashMap<String, String>();
		queryConstraints.put("wfInstanceRef", wfInstance);
		queryConstraints.put("PNameRef", pname);
		queryConstraints.put("varNameRef", vname);

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
					result.add(rs.getString("value"));
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
	public List<Var> getInputVars(String pname, String wfID, String wfInstanceID)
	throws SQLException {
		// get (var, proc) from Var to see if it's input/output
		Map<String, String> varQueryConstraints = new HashMap<String, String>();

		varQueryConstraints.put("V.wfInstanceRef", wfID);
		varQueryConstraints.put("V.pnameRef", pname);
		varQueryConstraints.put("V.inputOrOutput", "1");
		if (wfInstanceID != null) {
			varQueryConstraints.put("W.instanceID", wfInstanceID);
			return getVars(varQueryConstraints);
		} else {
			return getVarsNoInstance(varQueryConstraints);
		}
	}

	/**
	 * return the output variables for a given processor and a wfInstanceId
	 *
	 * @param pname
	 * @param wfInstanceId
	 * @return list of output variables
	 * @throws SQLException
	 */
	public List<Var> getOutputVars(String pname, String wfID, String wfInstanceID)
	throws SQLException {
		// get (var, proc) from Var to see if it's input/output
		Map<String, String> varQueryConstraints = new HashMap<String, String>();

		varQueryConstraints.put("V.wfInstanceRef", wfID);
		varQueryConstraints.put("V.pnameRef", pname);
		varQueryConstraints.put("V.inputOrOutput", "0");
		if (wfInstanceID != null) {
			varQueryConstraints.put("W.instanceID", wfInstanceID);
		}

		return getVars(varQueryConstraints);
	}

	/**
	 * selects all Arcs
	 *
	 * @param queryConstraints
	 * @return
	 * @throws SQLException
	 */
	public List<Arc> getArcs(Map<String, String> queryConstraints)
	throws SQLException {
		List<Arc> result = new ArrayList<Arc>();

		String q0 = "SELECT * FROM Arc A JOIN WfInstance W ON W.wfnameRef = A.wfInstanceRef";

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

					Arc aArc = new Arc();

					aArc.setWfInstanceRef(rs.getString("WfInstanceRef"));
					aArc.setSourcePnameRef(rs.getString("sourcePNameRef"));
					aArc.setSourceVarNameRef(rs.getString("sourceVarNameRef"));
					aArc.setSinkPnameRef(rs.getString("sinkPNameRef"));
					aArc.setSinkVarNameRef(rs.getString("sinkVarNameRef"));

					result.add(aArc);

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


	public String getTopLevelWfName(String runID) throws SQLException {

		List<String> wfNames = new ArrayList<String>();

		List<Workflow> workflows = getWorkflowForRun(runID);

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
	public List<String> getWfNames(String runID) throws SQLException {

		List<String> wfNames = new ArrayList<String>();

		List<Workflow> workflows = getWorkflowForRun(runID);

		for (Workflow w:workflows) { wfNames.add(w.getWfname()); }

		return wfNames;
	}


	/**
	 * returns the workflows associated to a single runID
	 * @param runID
	 * @return
	 * @throws SQLException
	 */
	public List<Workflow> getWorkflowForRun(String runID) throws SQLException {

		List<Workflow> result = new ArrayList<Workflow>();

		String q = "SELECT * FROM WfInstance I join Workflow W on I.wfnameRef = W.wfname where instanceID = ?";

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
					w.setWfName(rs.getString("wfnameRef"));
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
	public List<WorkflowInstance> getRuns(String dataflowID, Map<String, String> conditions) throws SQLException {

		PreparedStatement ps = null;
		Connection connection = null;

		List<WorkflowInstance> result = new ArrayList<WorkflowInstance>();

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
					WorkflowInstance i = new WorkflowInstance();
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


	/*
	 * gets all available run instances, most recent first
	 * @return a list of pairs <wfanme, wfinstance>
	 * @see net.sf.taverna.t2.provenance.lineageservice.mysql.ProvenanceQuery#
	 * getWFInstanceIDs()
	 */
	public List<String> getWFNamesByTime() throws SQLException {

		List<String> result = new ArrayList<String>();

		String q = "SELECT instanceID, wfnameRef FROM WfInstance order by timestamp desc";

		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			boolean success = stmt.execute(q);

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {

					result.add(rs.getString("wfnameRef"));

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
	 * all ProCBinding records that satisfy the input constraints
	 *
	 * @param constraints
	 * @return
	 * @throws SQLException
	 */
	public List<ProcBinding> getProcBindings(Map<String, String> constraints)
	throws SQLException {
		List<ProcBinding> result = new ArrayList<ProcBinding>();

		String q = "SELECT * FROM ProcBinding PB ";

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
					ProcBinding pb = new ProcBinding();

					pb.setActName(rs.getString("actName"));
					pb.setExecIDRef(rs.getString("execIDRef"));
					pb.setIterationVector(rs.getString("iteration"));
					pb.setPNameRef(rs.getString("pnameRef"));
					pb.setWfNameRef(rs.getString("wfNameRef"));

					result.add(pb);
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
	 * TODO this currently returns the data value as a string, which is
	 * incorrect as it is an untyped byte array
	 *
	 * @param constraints
	 *            a Map columnName -> value that defines the query constraints.
	 *            Note: columnName must be fully qualified. This is not done
	 *            well at the moment, i.e., PNameRef should be
	 *            VarBinding.PNameRef to avoid ambiguities
	 * @return
	 * @throws SQLException
	 */
	public List<VarBinding> getVarBindings(Map<String, String> constraints)
	throws SQLException {
		List<VarBinding> result = new ArrayList<VarBinding>();

		String q = "SELECT * FROM VarBinding VB join Var V " + 
		"on (VB.varNameRef = V.varName and VB.PNameRef =  V.PNameRef and VB.wfNameRef = V.wfInstanceRef) " + 
		"JOIN WfInstance W ON VB.wfInstanceRef = W.instanceID and VB.wfNameRef = W.wfnameRef " + 
		"LEFT OUTER JOIN Data D ON D.wfInstanceID = VB.wfInstanceRef and D.dataReference = VB.value";

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
					VarBinding vb = new VarBinding();

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
					vb.setPNameRef(rs.getString("PNameRef"));
					vb.setPositionInColl(rs.getInt("positionInColl"));
					try {
						vb.setResolvedValue(rs.getString("D.data"));
					} catch (Exception e1) {
						// ignore this since D.data is experimental and will be
						// removed at some point
					}

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
					VarBinding vb = new VarBinding();

					NestedListNode nln = new NestedListNode();

					nln.setCollId(rs.getString("collId"));
					nln.setParentCollIdRef(rs.getString("parentCollIdRef"));
					nln.setWfInstanceRef(rs.getString("wfInstanceRef"));
					nln.setPNameRef(rs.getString("PNameRef"));
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

		// get all arcs for the entire workflow structure for this particular instance
		Connection connection = null;
		try {

			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT A.sourcePNameRef as source , A.sinkPNameRef as sink, A.wfInstanceRef as wfName1, W1.wfName as wfName2, W2.wfName as wfName3 " +
					"FROM Arc A join WfInstance I on A.wfInstanceRef = I.wfnameRef " +
					"left outer join Workflow W1 on W1.externalName = A.sourcePNameRef " +
					"left outer join Workflow W2 on W2.externalName = A.sinkPNameRef " +
			"where I.instanceID = ? order by wfInstanceRef");
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

		// get all arcs for the entire workflow structure for this particular instance
		Connection connection = null;
		try {

			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT sinkPnameRef, P1.type, count(*) as pred " +
					" FROM Arc A join WfInstance I on A.wfInstanceRef = I.wfnameRef " +
					" join Processor P1 on P1.pname = A.sinkPnameRef " +
					" join Processor P2 on P2.pname = A.sourcePnameRef " +
					"  where I.instanceID = ? " +
					"  and P2.type <> 'net.sf.taverna.t2.activities.dataflow.DataflowActivity' " +
					" and ((P1.type = 'net.sf.taverna.t2.activities.dataflow.DataflowActivity' and P1.wfInstanceRef = A.wfInstanceRef) or " +
					" (P1.type <> 'net.sf.taverna.t2.activities.dataflow.DataflowActivity')) " +
			" group by A.sinkPnameRef, type");
			ps.setString(1, wfInstanceID);
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {

					int cnt = rs.getInt("pred");

					result.put(rs.getString("sinkPnameRef"), new Integer(cnt));
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
	 *         processor, without counting the arcs from the dataflow input to
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
			"SELECT pName, type FROM Processor WHERE wfInstanceRef = ?");
			ps.setString(1, wfnameRef);

			success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {

					// PM CHECK 6/09
					if (rs.getString("type").equals("net.sf.taverna.t2.activities.dataflow.DataflowActivity")) {
						currentWorkflowProcessor = rs.getString("pName");
						logger.info("currentWorkflowProcessor = " + currentWorkflowProcessor);
					}
					result.put(rs.getString("pName"), new Integer(0));
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

		// fetch the name of the top-level dataflow. We use this to exclude arcs
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
				"net.sf.taverna.t2.activities.dataflow.DataflowActivity",
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
			pNames.append(" '" + p.getPname() + "' ");
		}
		pNames.append(")");


		// exclude processors connected to inputs -- those have 0 predecessors
		// for our purposes
		// and we add them later

		// PM 6/09 not sure we need to exclude arcs going into sub-flows?? so commented out the condition
		String q = "SELECT sinkPNameRef, count(*) as cnt " + "FROM Arc " + "WHERE wfInstanceRef = \'" + wfnameRef + "\' " + "AND sinkPNameRef NOT IN " + pNames + " " //		+ "AND sourcePNameRef NOT IN " + pNames
		+ " GROUP BY sinkPNameRef";

		logger.info("executing \n" + q);

		try {
			connection = getConnection();
			stmt = connection.createStatement();
			success = stmt.execute(q);
			if (success) {
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {

					if (!rs.getString("sinkPNameRef").equals(currentWorkflowProcessor)) {
						result.put(rs.getString("sinkPNameRef"), new Integer(rs.getInt("cnt")));
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

	public List<Var> getSuccVars(String pName, String vName,
			String wfInstanceRef) throws SQLException {

		List<Var> result = new ArrayList<Var>();
		PreparedStatement ps = null;
		Connection connection = null;

		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT v.* " + "FROM Arc a JOIN Var v ON a.sinkPNameRef = v.pnameRef " + "AND  a.sinkVarNameRef = v.varName " + "AND a.wfInstanceRef = v.wfInstanceRef " + "WHERE sourceVarNameRef = ? AND sourcePNameRef = ?");

			ps.setString(1, vName);
			ps.setString(2, pName);

			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				while (rs.next()) {

					if (wfInstanceRef != null && !rs.getString("v.wfInstanceRef").equals(wfInstanceRef)) {
						continue;
					}

					Var aVar = new Var();

					aVar.setWfInstanceRef(rs.getString("WfInstanceRef"));

					if (rs.getInt("inputOrOutput") == 1) {
						aVar.setInput(true);
					} else {
						aVar.setInput(false);
					}
					aVar.setPName(rs.getString("pnameRef"));
					aVar.setVName(rs.getString("varName"));
					aVar.setType(rs.getString("type"));
					aVar.setTypeNestingLevel(rs.getInt("nestingLevel"));
					aVar.setActualNestingLevel(rs.getInt("actualNestingLevel"));
					aVar.setANLset((rs.getInt("anlSet") == 1 ? true : false));

					result.add(aVar);

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
					"SELECT distinct sinkPNameRef FROM Arc A JOIN Wfinstance I on A.wfInstanceRef = I.wfnameRef " + "WHERE A.wfInstanceRef = ? and I.instanceID = ? AND sourcePNameRef = ?");
			ps.setString(1, wfNameRef);
			ps.setString(2, wfInstanceId);
			ps.setString(3, pName);

			boolean success = ps.execute();


			if (success) {
				ResultSet rs = ps.getResultSet();

				while (rs.next()) {
					result.add(rs.getString("sinkPNameRef"));
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
	 * @param wfnameRef
	 * @param type
	 * @return a list, that contains at most one element
	 * @throws SQLException
	 */
	public List<ProvenanceProcessor> getProcessorsShallow(String type, String wfnameRef)
	throws SQLException {
		Map<String, String> constraints = new HashMap<String, String>();

		constraints.put("P.wfInstanceRef", wfnameRef);
		if (type != null) {
			constraints.put("P.type", type);
		}
		return getProcessors(constraints);
	}


	/**
	 * this is similar to {@link #getProcessorsShallow(String, String)} but it recursively fetches all processors
	 * within nested workflows. The result is collected in the form of a map: wfName -> {ProvenanceProcessor}
	 * @param type
	 * @param wfnameRef
	 * @return a map: wfName -> {ProvenanceProcessor} where wfName is the name of a (possibly nested) workflow, and 
	 * the values are the processors within that workflow
	 */
	public Map<String, List<ProvenanceProcessor>> getProcessorsDeep(String type, String wfnameRef) {

		Map<String, List<ProvenanceProcessor>> result = new HashMap<String, List<ProvenanceProcessor>>();

		List<ProvenanceProcessor> currentProcs;

		try {
			currentProcs = getProcessorsShallow(type, wfnameRef);

			result.put(wfnameRef, currentProcs);

			for (ProvenanceProcessor pp:currentProcs) {
				if (pp.getType() == DATAFLOW_TYPE) {
					// recurse 
					Map<String, List<ProvenanceProcessor>> deepProcessors = getProcessorsDeep(type, pp.getWfInstanceRef());

					for (Map.Entry<String, List<ProvenanceProcessor>> entry: deepProcessors.entrySet()) {
						result.put(entry.getKey(), entry.getValue());
					}				
				}
			}
		} catch (SQLException e) {
			logger.error("Problem getting nested workflow processors for: " + wfnameRef, e);
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

		String q = "SELECT * FROM Processor P JOIN WfInstance W ON P.wfInstanceRef = W.wfnameRef "+
		           "JOIN Workflow WF on W.wfnameRef = WF.wfname";

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

					proc.setPname(rs.getString("pname"));
					proc.setType(rs.getString("type"));
					proc.setWfInstanceRef(rs.getString("wfInstanceRef"));
					proc.setWorkflowExternalName(rs.getString("externalName"));

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
					"SELECT * from Processor WHERE wfInstanceRef = ?");
			ps.setString(1, workflowID);

			boolean success = ps.execute();
			if (success) {
				ResultSet rs = ps.getResultSet();
				if (rs.next()) {  return rs.getString("pname"); }
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
	 * simplest possible pinpoint query. Uses iteration info straight away. Assumes result is in VarBinding not in Collection
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

		String q1 = "SELECT * FROM VarBinding VB join Var V " + 
		"on (VB.varNameRef = V.varName and VB.PNameRef =  V.PNameRef and VB.wfNameRef=V.wfInstanceRef) " + 
		"JOIN WfInstance W ON VB.wfInstanceRef = W.instanceID and VB.wfNameRef = W.wfnameRef ";
//		+ "LEFT OUTER JOIN Data D ON D.wfInstanceID = VB.wfInstanceRef and D.dataReference = VB.value";

		// constraints:
		Map<String, String> lineageQueryConstraints = new HashMap<String, String>();

		lineageQueryConstraints.put("W.instanceID", wfInstance);
		lineageQueryConstraints.put("VB.PNameRef", pname);
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
			Map<Var, String> var2Path, Var outputVar, String path,
			boolean returnOutputs) {
		// setup
		StringBuffer effectivePath = new StringBuffer();

		List<LineageSQLQuery> newQueries = new ArrayList<LineageSQLQuery>();

		// use the calculated path for each input var
		boolean isInput = true;
		for (Var v : var2Path.keySet()) {
			LineageSQLQuery q = generateSQL2(wfInstanceID, proc, v.getVName(), var2Path.get(v), isInput);
			if (q != null) {
				newQueries.add(q);
			}
		}

		// is returnOutputs is true, then use proc, path for the output var as well
		if (returnOutputs) {

			isInput = false;

			LineageSQLQuery q = generateSQL2(wfInstanceID, proc, outputVar.getVName(), path, isInput);  // && !var2Path.isEmpty());
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
		String collQuery = "SELECT * FROM Collection C JOIN WfInstance W ON " + "C.wfInstanceRef = W.instanceID " + "JOIN Var V on " + "V.wfInstanceRef = W.wfnameRef and C.PNameRef = V.pnameRef and C.varNameRef = V.varName ";

		collQueryConstraints.put("W.instanceID", wfInstance);
		collQueryConstraints.put("C.PNameRef", proc);

		if (path != null && path.length() > 0) {
			collQueryConstraints.put("C.iteration", "[" + path + "]"); // PM 1/09 -- path
		}

		// inputs or outputs?
		if (returnInput) {
			collQueryConstraints.put("V.inputOrOutput", "1");
		} else {
			collQueryConstraints.put("V.inputOrOutput", "0");
		}

		collQuery = addWhereClauseToQuery(collQuery, collQueryConstraints, false);

		lq.setCollQuery(collQuery);

		//  vb query

		Map<String, String> vbQueryConstraints = new HashMap<String, String>();

		// base VarBinding query
		String vbQuery = "SELECT * FROM VarBinding VB JOIN WfInstance W ON " + 
						 "VB.wfInstanceRef = W.instanceID " + 
						 "JOIN Var V on " + 
						 "V.wfInstanceRef = W.wfnameRef and VB.PNameRef = V.pnameRef and VB.varNameRef = V.varName "; 
//						 "LEFT OUTER JOIN Data D ON D.wfInstanceID = VB.wfInstanceRef and D.dataReference = VB.value";

		vbQueryConstraints.put("W.instanceID", wfInstance);
		vbQueryConstraints.put("VB.PNameRef", proc);
		vbQueryConstraints.put("VB.varNameRef", var);

		if (path != null && path.length() > 0) {
			vbQueryConstraints.put("VB.iteration", "[" + path + "]"); // PM 1/09 -- path
		}

		// limit to inputs?
		if (returnInput) {
			vbQueryConstraints.put("V.inputOrOutput", "1");
		} else {
			vbQueryConstraints.put("V.inputOrOutput", "0");
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
	 * wfInstance, iteration = effectivePath, PNameRef = proc if input vars is
	 * null, then use the output var this returns the bindings for the set of
	 * input vars at the correct iteration if effectivePath is null: fetch
	 * VarBindings for all input vars, without constraint on the iteration<br/>
	 * added outer join with Data<br/>
	 * additionally, try querying the collection table first -- if the query succeeds, it means
	 * the path is pointing to an internal node in the collection, and we just got the right node.
	 * Otherwise, query VarBinding for the leaves
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
		String collQuery = "SELECT * FROM Collection C JOIN WfInstance W ON " + "C.wfInstanceRef = W.instanceID " + "JOIN Var V on " + "V.wfInstanceRef = W.wfnameRef and C.PNameRef = V.pnameRef and C.varNameRef = V.varName ";

		collQueryConstraints.put("W.instanceID", wfInstance);
		collQueryConstraints.put("C.PNameRef", proc);

		if (effectivePath != null && effectivePath.length() > 0) {
			collQueryConstraints.put("C.iteration", "[" + effectivePath.toString() + "]"); // PM 1/09 -- path
		}

		// limit to inputs?
		if (returnOutputs) {
			collQueryConstraints.put("V.inputOrOutput", "1");
		}

		collQuery = addWhereClauseToQuery(collQuery, collQueryConstraints, false);

		lq.setCollQuery(collQuery);

		//  vb query

		Map<String, String> vbQueryConstraints = new HashMap<String, String>();

		// base VarBinding query
//		String vbQuery = "SELECT * FROM VarBinding VB JOIN wfInstance W ON " + "VB.wfInstanceRef = W.instanceID " + "JOIN Var V on " + "V.wfInstanceRef = W.wfnameRef and VB.PNameRef = V.pnameRef and VB.varNameRef = V.varName " + "LEFT OUTER JOIN Data D ON D.wfInstanceID = VB.wfInstanceRef and D.dataReference = VB.value";

		String vbQuery = "SELECT * FROM VarBinding VB JOIN WfInstance W ON " + 
						 "VB.wfInstanceRef = W.instanceID " + 
						 "JOIN Var V on " + 
						 "V.wfInstanceRef = W.wfnameRef and VB.PNameRef = V.pnameRef and VB.varNameRef = V.varName "; 

		vbQueryConstraints.put("W.instanceID", wfInstance);
		vbQueryConstraints.put("VB.PNameRef", proc);

		if (effectivePath != null && effectivePath.length() > 0) {
			vbQueryConstraints.put("VB.iteration", "[" + effectivePath.toString() + "]"); // PM 1/09 -- path
		}

		// limit to inputs?
		if (!returnOutputs) {
			vbQueryConstraints.put("V.inputOrOutput", "1");
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
					String proc = rs.getString("PNameRef");
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
					String proc = rs.getString("PNameRef");
					String var = rs.getString("varNameRef");
					String it = rs.getString("iteration");
					String coll = rs.getString("collIDRef");
					String value = rs.getString("value");
					boolean isInput = (rs.getInt("inputOrOutput") == 1) ? true
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
	 * returns the set of all processors that are structurally contained within
	 * the wf corresponding to the input dataflow name
	 * @param dataflowName the name of a processor of type DataFlowActivity
	 * @return
	 */
	public List<String> getContainedProcessors(String dataflowName) {

		List<String> result = new ArrayList<String>();

		// dataflow name -> wfRef
		String containerDataflow = getWfNameForDataflow(dataflowName);

		// get all processors within containerDataflow
		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
//					"SELECT pname FROM Processor P  join wfInstance I on P.wfInstanceRef = I.wfnameRef " +
//			"where wfInstanceRef = ? and I.instanceID = ?");
					"SELECT pname FROM Processor P " +
					"where wfInstanceRef = ?");
			ps.setString(1, containerDataflow);
//			ps.setString(2, instanceID);


			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
					result.add(rs.getString("pname"));
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
					"SELECT pname FROM Processor P  join WfInstance I on P.wfInstanceRef = I.wfnameRef " +
			"where  I.instanceID =? and isTopLevel = 1");


			ps.setString(1, wfInstanceID);
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				if (rs.next()) {
					return rs.getString("pname");
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
	 */
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

	public List<String> getChildrenOfWorkflow(String parentWFName)
	throws SQLException {

		List<String> result = new ArrayList<String>();

		PreparedStatement ps = null;
		Connection connection = null;

		try {
			connection = getConnection();
			ps = connection.prepareStatement(
			"SELECT wfname FROM Workflow WHERE parentWFname = ? ");
			ps.setString(1, parentWFName);

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
	 *
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
			"SELECT type FROM Processor WHERE pname = ?");
			ps.setString(1, procName);

			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				if (rs.next() && rs.getString("type") != null && rs.getString("type").equals(DATAFLOW_TYPE)) {
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
					"SELECT * FROM T2Provenance.Processor P join WfInstance I on P.wfInstanceRef = I.wfNameRef " +
					" where I.instanceID = ? " +
			" and isTopLevel = 1 ");
			ps.setString(1, wfInstanceID);
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				if (rs.next()) {
					return rs.getString("PName");
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

	public Set<DDRecord> queryArcsForDD(String p, String v, String val,
			String wfInstance) throws SQLException {

		Set<DDRecord> result = new HashSet<DDRecord>();

		PreparedStatement ps = null;
		Connection connection = null;

		String q = "SELECT DISTINCT A.sourcePNameRef AS p, A.sourceVarNameRef AS var, VB.value AS val " + "FROM   Arc A JOIN VarBinding VB ON VB.varNameRef = A.sinkVarNameRef AND VB.PNameRef = A.sinkPNameRef " + "JOIN   WfInstance WF ON WF.wfnameRef = A.wfInstanceRef AND WF.instanceID = VB.wfInstanceRef  " + "WHERE  WF.instanceID = '" + wfInstance + "' AND A.sinkPNameRef = '" + p + "' AND A.sinkVarNameRef = '" + v + "' AND VB.value = '" + val + "' ";

		// Statement stmt;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT DISTINCT A.sourcePNameRef AS p, A.sourceVarNameRef AS var, VB.value AS val " + "FROM   Arc A JOIN VarBinding VB ON VB.varNameRef = A.sinkVarNameRef AND VB.PNameRef = A.sinkPNameRef " + "JOIN   WfInstance WF ON WF.wfnameRef = A.wfInstanceRef AND WF.instanceID = VB.wfInstanceRef  " + "WHERE  WF.instanceID = ? AND A.sinkPNameRef = ? AND A.sinkVarNameRef = ? AND VB.value = ?");

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
					"SELECT * FROM Arc A join WfInstance I on A.wfInstanceRef = I.wfnameRef " +
					"join Processor P on P.pname = A.sourcePnameRef where sourcePnameRef = ? " +
					"and P.wfInstanceRef <> A.wfInstanceRef " +
					"and I.instanceID = ? " +
			"and sinkPNameRef = ? ");

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


	public List<Workflow> getContainingWorkflowsForProcessor(
			String pname) {

		List<Workflow> wfList = new ArrayList<Workflow>();

		PreparedStatement ps = null;
		Connection connection = null;

		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT * FROM T2Provenance.Processor P "+
					"join Workflow W on P.wfInstanceRef = W.wfName "+
			"where pname = ? ");

			ps.setString(1, pname);

			boolean success = ps.execute();
			if (success) {
				ResultSet rs = ps.getResultSet();

				while (rs.next()) {
					Workflow wf = new Workflow();
					wf.setWfName(rs.getString("wfInstanceRef"));
					wf.setParentWFname(rs.getString("parentWFName"));

					wfList.add(wf);
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
		return wfList;
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
					"SELECT * FROM T2Provenance.Workflow W "+
			"where wfname = ? ");

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
		
		String q = "SELECT * FROM Collection where collID = ? and wfInstanceRef = ? and PNameRef = ? and varNameRef = ?";

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


}


