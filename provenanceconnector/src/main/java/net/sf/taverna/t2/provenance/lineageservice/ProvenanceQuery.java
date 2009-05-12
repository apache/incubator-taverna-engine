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

import net.sf.taverna.t2.provenance.lineageservice.utils.Arc;
import net.sf.taverna.t2.provenance.lineageservice.utils.DDRecord;
import net.sf.taverna.t2.provenance.lineageservice.utils.NestedListNode;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProcBinding;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.provenance.lineageservice.utils.Var;
import net.sf.taverna.t2.provenance.lineageservice.utils.VarBinding;

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
 * 
 */
public abstract class ProvenanceQuery {

	Logger logger = Logger.getLogger(ProvenanceQuery.class);

	protected Connection connection;

	private String dbURL;

	public static String DATAFLOW_TYPE = "net.sf.taverna.t2.activities.dataflow.DataflowActivity";

	public Connection getConnection() throws InstantiationException,
	IllegalAccessException, ClassNotFoundException {
		if (connection == null) {
			openConnection();
		}
		return connection;
	}

	protected abstract void openConnection() throws InstantiationException,
	IllegalAccessException, ClassNotFoundException;

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
				q.append(" " + entry.getKey() + " = \'" + entry.getValue()
						+ "\' ");
				first = false;
			}
		}

		return q.toString();
	}

	private String addOrderByToQuery(String q0, List<String> orderAttr,
			boolean terminate) {

		// complete query according to constraints
		StringBuffer q = new StringBuffer(q0);

		boolean first = true;
		if (orderAttr != null && orderAttr.size() > 0) {
			q.append(" ORDER BY ");

			int i = 1;
			for (String attr : orderAttr) {
				q.append(attr);
				if (i++ < orderAttr.size())
					q.append(",");
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

		Statement stmt;
		try {
			stmt = getConnection().createStatement();
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
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		// System.out.println("getVars: executing query\n"+q.toString());

		return result;
	}

	private List<Var> getVarsNoInstance(Map<String, String> queryConstraints)
	throws SQLException {

		List<Var> result = new ArrayList<Var>();

		String q0 = "SELECT  * FROM Var V";

		String q = addWhereClauseToQuery(q0, queryConstraints, true);

		Statement stmt;
		try {
			stmt = getConnection().createStatement();
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
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		// System.out.println("getVars: executing query\n"+q.toString());

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

		Statement stmt;
		try {
			stmt = getConnection().createStatement();
			boolean success = stmt.execute(q.toString());

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {
					result.add(rs.getString("value"));
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		// System.out.println("getVars: executing query\n"+q.toString());

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
	public List<Var> getOutputVars(String pname, String wfInstanceId)
	throws SQLException {
		// get (var, proc) from Var to see if it's input/output
		Map<String, String> varQueryConstraints = new HashMap<String, String>();

		varQueryConstraints.put("V.wfInstanceRef", wfInstanceId);
		varQueryConstraints.put("V.pnameRef", pname);
		varQueryConstraints.put("V.inputOrOutput", "0");

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

		Statement stmt;
		try {
			stmt = getConnection().createStatement();
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
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		// System.out.println("getArcs: executing query\n"+q.toString());

		return result;
	}

	public String getWfNameRef(String wfInstanceID)  throws SQLException {
		String q = "SELECT wfnameRef FROM WfInstance where instanceID = \""+wfInstanceID+"\"";

		Statement stmt = null;
		try {
			stmt = getConnection().createStatement();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean success = stmt.execute(q);

		if (success) {
			ResultSet rs = stmt.getResultSet();

			if (rs.next()) {  return  rs.getString("wfnameRef"); }
		}

		return null;

	}


	/**
	 * 
	 * @param dataflowID
	 * @return
	 * @throws SQLException
	 */
	public String getWFInstanceID(String dataflowID) throws SQLException {
		//		String q = "SELECT instanceID FROM WfInstance where wfnameRef = \'"
		//				+ dataflowID + "\'";
		//
		PreparedStatement ps = null;
		//		Statement stmt;
		try {
			ps = getConnection().prepareStatement("SELECT instanceID FROM WfInstance where wfnameRef = ?");
			ps.setString(1, dataflowID);
			//			stmt = getConnection().createStatement();
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				if (rs.next()) {
					return rs.getString("instanceID");
				}
			}

		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}
		return null;

	}

	public String getWFNameFromInstanceID(String wfInstanceID)  throws SQLException {

		String q = "SELECT wfnameRef FROM WfInstance where instanceID = \""+wfInstanceID+"\";";
		PreparedStatement ps = null;
		//		Statement stmt;
		try {
			ps = getConnection().prepareStatement("SELECT wfnameRef FROM WfInstance where instanceID = ?");
			ps.setString(1, wfInstanceID);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean success = ps.execute();

		if (success) {
			ResultSet rs = ps.getResultSet();

			if (rs.next()) {  return  rs.getString("wfnameRef"); }
		}

		return null;

	}

	/**
	 * all WF instances, in reverse chronological order
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<String> getWFInstanceIDs() throws SQLException {
		List<String> result = new ArrayList<String>();

		String q = "SELECT instanceID FROM WfInstance order by timestamp desc";

		Statement stmt;
		try {
			stmt = getConnection().createStatement();
			boolean success = stmt.execute(q);

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {

					result.add(rs.getString("instanceID"));

				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		return result;
	}

	/*
	 * @return a list of pairs <wfanme, wfinstance>
	 * 
	 * @seenet.sf.taverna.t2.provenance.lineageservice.mysql.ProvenanceQuery#
	 * getWFInstanceIDs()
	 */
	public List<String> getWFNamesByTime() throws SQLException {

		List<String> result = new ArrayList<String>();

		String q = "SELECT instanceID, wfnameRef FROM WfInstance order by timestamp desc";

		Statement stmt;
		try {
			stmt = getConnection().createStatement();
			boolean success = stmt.execute(q);

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {

					result.add(rs.getString("wfnameRef"));

				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
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

		Statement stmt;
		try {
			stmt = getConnection().createStatement();
//			System.out.println("getProcBindings: executing: " + q);

			boolean success = stmt.execute(q);
			// System.out.println("result: "+success);

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {
					ProcBinding pb = new ProcBinding();

					pb.setActName(rs.getString("actName"));
					pb.setExecIDRef(rs.getString("execIDRef"));
					pb.setIterationVector(rs.getString("iteration"));
					pb.setPNameRef(rs.getString("pnameRef"));

					result.add(pb);

				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		return result;

	}

	/**
	 * TODO this currently returns the data value as a string, which is incorrect as it is an untyped byte array
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

//		String q = "SELECT * FROM VarBinding VB join Var V "
//		+ "on (VB.varNameRef = V.varName and VB.PNameRef =  V.PNameRef) "
//		+ "JOIN WfInstance W ON VB.wfInstanceRef = W.instanceID and V.wfInstanceRef = wfnameRef ";

		String q = "SELECT * FROM VarBinding VB join Var V "+
		"on (VB.varNameRef = V.varName and VB.PNameRef =  V.PNameRef) "+
		"JOIN WfInstance W ON VB.wfInstanceRef = W.instanceID and V.wfInstanceRef = wfnameRef " + 
		"LEFT OUTER JOIN Data D ON D.wfInstanceID = VB.wfInstanceRef and D.dataReference = VB.value";

		q = addWhereClauseToQuery(q, constraints, true);

		Statement stmt;
		try {
			stmt = getConnection().createStatement();
			boolean success = stmt.execute(q);
			// System.out.println("result: "+success);

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {
					VarBinding vb = new VarBinding();

					vb.setVarNameRef(rs.getString("varNameRef"));
					vb.setWfInstanceRef(rs.getString("wfInstanceRef"));
					vb.setValue(rs.getString("value"));

					if (rs.getString("collIdRef") == null  || rs.getString("collIdRef").equals("null")) 
						vb.setCollIDRef(null); 
					else vb.setCollIDRef(rs.getString("collIdRef"));

					vb.setIterationVector(rs.getString("iteration"));
					vb.setPNameRef(rs.getString("PNameRef"));
					vb.setPositionInColl(rs.getInt("positionInColl"));
					vb.setResolvedValue(rs.getString("D.data"));

					result.add(vb);
				}

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}


	
	
	
	public List<NestedListNode> getNestedListNodes(Map<String, String> constraints) throws SQLException { 

		List<NestedListNode> result = new ArrayList<NestedListNode>();

		String q = "SELECT * FROM Collection C ";

		q = addWhereClauseToQuery(q, constraints, true);

		Statement stmt = null;
		try {
			stmt = getConnection().createStatement();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println("executing: "+q);

		boolean success = stmt.execute(q);
		//System.out.println("result: "+success);

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
		return result;
	}

	/**
	 * used in the toposort phase -- propagation of anl() values through the
	 * graph
	 * 
	 * @param wfnameRef reference to static wf name
	 * @return a map <processor name> --> <incoming links count> for each processor, without counting the arcs from 
	 * the dataflow input to processors. So a processor is at the root of the graph if it has no incoming links, or
	 * all of its incoming links are from dataflow inputs.<br/>
	 * Note: this must be checked for processors that are roots of sub-flows... are these counted as top-level root nodes??
	 */
	public Map<String, Integer> getProcessorsIncomingLinks(String wfnameRef)
	throws SQLException {
		Map<String, Integer> result = new HashMap<String, Integer>();

		boolean success;

		PreparedStatement ps = null;

		// get all processors and init their incoming links to 0
		// String q = "SELECT pName FROM Processor "
		// + "WHERE wfInstanceRef = \'" + wfnameRef + "\'";
		//
		Statement stmt;
		try {
			ps = getConnection().prepareStatement(
			"SELECT pName FROM Processor WHERE wfInstanceRef = ?");
			ps.setString(1, wfnameRef);
			// stmt = getConnection().createStatement();
			success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();
				while (rs.next()) {
					result.put(rs.getString("pName"), new Integer(0));
				}
			}
		} catch (InstantiationException e1) {
			logger.warn("Could not execute query: " + e1);
		} catch (IllegalAccessException e1) {
			logger.warn("Could not execute query: " + e1);
		} catch (ClassNotFoundException e1) {
			logger.warn("Could not execute query: " + e1);
		}
		// System.out.println("executing: "+q);

		// fetch the name of the top-level dataflow. We use this to exclude arcs
		// outgoing from its inputs

		String parentWF = getParentOfWorkflow(wfnameRef);
		if (parentWF == null)
			parentWF = wfnameRef;

		List<ProvenanceProcessor> procs = getProcessors(
				"net.sf.taverna.t2.activities.dataflow.DataflowActivity",
				parentWF);

		StringBuffer pNames = new StringBuffer();
		pNames.append("(");
		boolean first = true;
		for (ProvenanceProcessor p : procs) {
			if (!first)
				pNames.append(",");
			else
				first = false;
			pNames.append(" '" + p.getPname() + "' ");
		}
		pNames.append(")");

		// List<String> pNames = new ArrayList<String>();
		// for (ProvenanceProcessor p:procs) { pNames.add(p.getPname()); }
		//		
		// String topLevelFlowName = pNames.get(0);

		// exclude processors connected to inputs -- those have 0 predecessors
		// for our purposes
		// and we add them later
		String q = "SELECT sinkPNameRef, count(*) as cnt " + "FROM Arc "
		+ "WHERE wfInstanceRef = \'" + wfnameRef + "\' "
		+ "AND sinkPNameRef NOT IN " + pNames + " "
		+ "AND sourcePNameRef NOT IN " + pNames
		+ " GROUP BY sinkPNameRef";

		try {
			stmt = getConnection().createStatement();
			success = stmt.execute(q);
			if (success) {
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {

					// e
					result.put(rs.getString("sinkPNameRef"), new Integer(rs
							.getInt("cnt")));
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}
		// System.out.println("executing: "+q);

		return result;
	}

	public List<Var> getSuccVars(String pName, String vName,
			String wfInstanceRef) throws SQLException {
		List<Var> result = new ArrayList<Var>();

		PreparedStatement ps = null;

		// String q = "SELECT v.*   "
		// + "FROM Arc a JOIN Var v ON a.sinkPNameRef = v.pnameRef "
		// + "AND  a.sinkVarNameRef = v.varName "
		// + "AND a.wfInstanceRef = v.wfInstanceRef "
		// + "WHERE a.wfInstanceRef = \'" + wfInstanceRef + "\' "
		// + "AND sourceVarNameRef = \'" + vName + "\' "
		// + "AND sourcePNameRef = \'" + pName + "\'";

		// Statement stmt;
		try {
			ps = getConnection()
			.prepareStatement(
					"SELECT v.* "
					+ "FROM Arc a JOIN Var v ON a.sinkPNameRef = v.pnameRef "
					+ "AND  a.sinkVarNameRef = v.varName "
					+ "AND a.wfInstanceRef = v.wfInstanceRef "
					+ "WHERE a.wfInstanceRef = ? AND sourceVarNameRef = ? AND sourcePNameRef = ?");

			ps.setString(1, wfInstanceRef);
			ps.setString(2, vName);
			ps.setString(3, pName);
			// stmt = getConnection().createStatement();
			boolean success = ps.execute();
			// System.out.println("result: "+success);

			if (success) {
				ResultSet rs = ps.getResultSet();

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
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		// System.out.println("executing: "+q);

		return result;
	}

	public List<String> getSuccProcessors(String pName, String wfInstanceRef)
	throws SQLException {
		List<String> result = new ArrayList<String>();

		PreparedStatement ps = null;

		// String q = "SELECT distinct sinkPNameRef " + "FROM Arc "
		// + "WHERE wfInstanceRef = \'" + wfInstanceRef + "\' "
		// + "AND sourcePNameRef = \'" + pName + "\'";

		Statement stmt;
		try {
			ps = getConnection().prepareStatement(
					"SELECT distinct sinkPNameRef FROM Arc "
					+ "WHERE wfInstanceRef = ? AND sourcePNameRef = ?");
			ps.setString(1, wfInstanceRef);
			ps.setString(2, pName);
			// stmt = getConnection().createStatement();
			boolean success = ps.execute();
			// System.out.println("result: "+success);

			if (success) {
				ResultSet rs = ps.getResultSet();

				while (rs.next()) {
					result.add(rs.getString("sinkPNameRef"));
				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		// System.out.println("executing: "+q);

		return result;
	}

	/**
	 * get all processors of a given type within a structure identified by
	 * wfnameRef (static reference). type constraint is ignored if value is null
	 * 
	 * @param wfnameRef
	 * @param type
	 * @return a list, that contains at most one element
	 * @throws SQLException
	 */
	public List<ProvenanceProcessor> getProcessors(String type, String wfnameRef)
	throws SQLException {
		Map<String, String> constraints = new HashMap<String, String>();

		constraints.put("P.wfInstanceRef", wfnameRef);
		if (type != null) {
			constraints.put("P.type", type);
		}
		return getProcessors(constraints);
	}

	public List<ProvenanceProcessor> getProcessors(
			Map<String, String> constraints) throws SQLException {
		List<ProvenanceProcessor> result = new ArrayList<ProvenanceProcessor>();

		String q = "SELECT * FROM Processor P JOIN wfInstance W ON P.wfInstanceRef = W.wfnameRef";

		q = addWhereClauseToQuery(q, constraints, true);

		Statement stmt;
		try {
			stmt = getConnection().createStatement();
			boolean success = stmt.execute(q);
			// System.out.println("result: "+success);

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {
					ProvenanceProcessor proc = new ProvenanceProcessor();

					proc.setPname(rs.getString("pname"));
					proc.setType(rs.getString("type"));
					proc.setWfInstanceRef(rs.getString("wfInstanceRef"));

					result.add(proc);

				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		// System.out.println("executing: "+q);

		return result;
	}

	/**
	 * simplest possible pinpoint query. Uses iteration info straight away
	 * 
	 * @param wfInstance
	 * @param pname
	 * @param vname
	 * @param iteration
	 * @return
	 */
	public LineageSQLQuery simpleLineageQuery(String wfInstance, String pname,
			String vname, String iteration) {
		LineageSQLQuery lq = new LineageSQLQuery();

		// base query
//		String q1 = "SELECT * FROM VarBinding VB JOIN WfInstance W ON "
//			+ "VB.wfInstanceRef = W.instanceID "
//			+ "JOIN Var V on "
//			+ "V.wfInstanceRef = W.wfnameRef and VB.PNameRef = V.pnameRef and VB.varNameRef = V.varName";

		String q1 = "SELECT * FROM VarBinding VB join Var V "+
		"on (VB.varNameRef = V.varName and VB.PNameRef =  V.PNameRef) "+
		"JOIN WfInstance W ON VB.wfInstanceRef = W.instanceID and V.wfInstanceRef = wfnameRef " + 
		"LEFT OUTER JOIN Data D ON D.wfInstanceID = VB.wfInstanceRef and D.dataReference = VB.value";
		
		// constraints:
		Map<String, String> lineageQueryConstraints = new HashMap<String, String>();

		lineageQueryConstraints.put("W.instanceID", wfInstance);
		lineageQueryConstraints.put("VB.PNameRef", pname);

		if (vname != null)
			lineageQueryConstraints.put("VB.varNameRef", vname);
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

		// System.out.println("generated query: \n"+q1);
		logger.info("Query is: " + q1);
		lq.setSQLQuery(q1);

		return lq;
	}

	/**
	 * if var2Path is null this generates a trivial query for the current output
	 * var and current path
	 * 
	 * @param proc
	 * @param var2Path
	 * @param path
	 * @param returnOutputs returns inputs *and* outputs if set to true
	 * @return
	 */
	public LineageSQLQuery lineageQueryGen(String wfInstance, String proc,
			Map<Var, String> var2Path, Var outputVar, String path, boolean returnOutputs) {
		// setup
		StringBuffer effectivePath = new StringBuffer();

		// System.out.println("generating SQL for proc ="+proc);
		// System.out.println("input vars:");
		// for (Var v: var2Path.keySet()) {
		// System.out.println(v.getVName()+
		// " with delta-nl "+
		// (v.getActualNestingLevel()-v.getTypeNestingLevel())+
		// " and path "+var2Path.get(v));
		// }

		int outputVarDnl = outputVar.getTypeNestingLevel();
		// System.out.println("dnl of output var "+outputVar.getVName()+": "+outputVarDnl);
		// System.out.println("original path: "+path);

		if (path != null) {

			String pathArray[] = path.split(",");

			for (int i = 0; i < pathArray.length - outputVarDnl; i++) {
				effectivePath.append(pathArray[i] + ",");
			}
			if (effectivePath.length() > 0)
				effectivePath.deleteCharAt(effectivePath.length() - 1);

			// System.out.println("path used for query: "+effectivePath);
		}

		// generation
		if (!var2Path.isEmpty()) { // generate query to retrieve inputs

			// returnOutputs => SQL generator will *not* constrain to return inputs only  
			return generateSQL(wfInstance, proc, effectivePath.toString(), returnOutputs); 
			// ->
			// fetch
			// input
			// vars

		} else { // generate query to retrieve outputs (this is a special case
			// where processor has no inputs)

			// System.out.println("lineageQueryGen: proc has no inputs => return output values instead");
			return generateSQL(wfInstance, proc, effectivePath.toString(),
					false); // false -> fetch output vars

		}
	}

	/**
	 * if effectivePath is not null: query varBinding using: wfInstanceRef =
	 * wfInstance, iteration = effectivePath, PNameRef = proc if input vars is
	 * null, then use the output var this returns the bindings for the set of
	 * input vars at the correct iteration if effectivePath is null: fetch
	 * VarBindings for all input vars, without constraint on the iteration<br/>
	 * added outer join with Data
	 * 
	 * @param wfInstance
	 * @param proc
	 * @param effectivePath
	 * @param returnOutputs returns both inputs and outputs if set to true
	 * @return
	 */
	public LineageSQLQuery generateSQL(String wfInstance, String proc,
			String effectivePath, boolean returnOutputs) {

		LineageSQLQuery lq = new LineageSQLQuery();

		// base query
		String q1 = "SELECT * FROM VarBinding VB JOIN wfInstance W ON " +
		"VB.wfInstanceRef = W.instanceID " +
		"JOIN Var V on "+
		"V.wfInstanceRef = W.wfnameRef and VB.PNameRef = V.pnameRef and VB.varNameRef = V.varName "+
		"LEFT OUTER JOIN Data D ON D.wfInstanceID = VB.wfInstanceRef and D.dataReference = VB.value";
		// constraints:
		Map<String, String> lineageQueryConstraints = new HashMap<String, String>();

		lineageQueryConstraints.put("W.instanceID", wfInstance);
		lineageQueryConstraints.put("VB.PNameRef", proc);

		// limit to inputs? 
		if (!returnOutputs)	lineageQueryConstraints.put("V.inputOrOutput", "1");


		if (effectivePath != null ) {

//			if  (fetchInputs)  { // path is iteration vector -- THIS IS NOT CLEAR

			if (effectivePath.length() > 0 ) 
				lineageQueryConstraints.put("VB.iteration", "["+effectivePath.toString()+"]");  // PM 1/09 -- path format [x1,x2,..]
//			} else {   // use path as collection, rather than iteration
//			// PM CHECK THIS MAY BE OBSOLETE -- we are now assigned a list to each loose iteration result 24/4/09
//			lineageQueryConstraints.put("VB.positionInColl", effectivePath.toString());  
//			}
		}

		q1 = addWhereClauseToQuery(q1, lineageQueryConstraints, false);

		List<String> orderAttr = new ArrayList<String>();
		orderAttr.add("varNameRef");
		orderAttr.add("iteration");

		q1 = addOrderByToQuery(q1, orderAttr, true);

		// System.out.println("generated query: \n"+q1);

		lq.setSQLQuery(q1);

		return lq;
	}

	
	/**
	 * executes one of the lineage queries produced by the graph visit algorithm
	 * @param lq a lineage query computed during the graph traversal
	 * @param includeDataValue if true, then the referenced value is included in the result. This may only be necessary
	 * for testing: the data reference in field value (which is a misleading field name, and actually refers to the data reference)
	 * should be sufficient
	 * @return
	 * @throws SQLException
	 */
	public LineageQueryResult runLineageQuery(LineageSQLQuery lq, boolean includeDataValue)
	throws SQLException {
		Statement stmt;
		String q = lq.getSQLQuery();
		try {
			stmt = getConnection().createStatement();
			boolean success = stmt.execute(q);

			if (success) {
				ResultSet rs = stmt.getResultSet();

				LineageQueryResult lqr = new LineageQueryResult();

				while (rs.next()) {

					String type = lqr.ATOM_TYPE; // temp -- FIXME

					String wfInstance = rs.getString("wfInstanceRef");
					String proc = rs.getString("PNameRef");
					String var  = rs.getString("varNameRef");
					String it   = rs.getString("iteration");
					String coll = rs.getString("collIDRef");
					String value = rs.getString("value");
					boolean isInput = (rs.getInt("inputOrOutput") == 1) ? true : false;

					//FIXME there is no D and no VB - this is in generateSQL, not simpleLineageQuery
					if (includeDataValue) {
						String resolvedValue = rs.getString("D.data");

//						System.out.println("resolved value: "+resolvedValue);
						lqr.addLineageQueryResultRecord(proc, var, wfInstance, it, coll, value, resolvedValue, type, isInput);
					}  else {
					//	System.out.println("proc ["+proc+"] var ["+var+"] iteration ["+it+"] collection ["+ coll+"] value ["+value+"]");

					// lqr.addLineageQueryResultRecord(proc, var, wfInstance,
					// it,
					// value, resolvedValue, type);
					//FIXME if the data is required then the query needs fixing
						lqr.addLineageQueryResultRecord(proc, var, wfInstance, it, coll, value, null, type, isInput);
					}
				}

				return lqr;

			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}
		return null;

	}

	public List<LineageQueryResult> runLineageQueries(List<LineageSQLQuery> lqList, boolean includeDataValue) throws SQLException {
		List<LineageQueryResult> allResults = new ArrayList<LineageQueryResult>();

		for (LineageSQLQuery lq : lqList) {
			if (lq == null)
				continue;
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
	public Document recordsToCollection(LineageQueryResult lqr) {
		// process each var name in turn
		// lqr ordered by var name and by iteration number
		Document d = new Document(new Element("list"));

		String currentVar = null;
		for (ListIterator<LineageQueryResultRecord> it = lqr.iterator(); it
		.hasNext();) {

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

	public List<String> getChildrenOfWorkflow(String parentWFName)
	throws SQLException {
		List<String> result = new ArrayList<String>();

		PreparedStatement ps = null;

		// String q = "SELECT wfname FROM Workflow WHERE parentWFname = \'"
		// + parentWFName + "\'";

		// Statement stmt;
		try {
			ps = getConnection().prepareStatement(
			"SELECT wfname FROM Workflow WHERE parentWFname = ? ");
			ps.setString(1, parentWFName);
			// stmt = getConnection().createStatement();
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				while (rs.next()) {

					result.add(rs.getString("wfname"));

				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
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

		// String q = "SELECT parentWFname FROM Workflow WHERE wfname = \'"
		// + childWFName + "\'";

		PreparedStatement ps = null;
		String result = null;

		// Statement stmt;
		try {
			ps = getConnection().prepareStatement(
			"SELECT parentWFname FROM Workflow WHERE wfname = ?");
			ps.setString(1, childWFName);
			// stmt = getConnection().createStatement();
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				while (rs.next()) {

					result = rs.getString("parentWFname");
					break;

				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		return result;
	}

	public List<String> getAllWFnames() throws SQLException {
		List<String> result = new ArrayList<String>();

		String q = "SELECT wfname FROM Workflow";

		Statement stmt;
		try {
			stmt = getConnection().createStatement();
			boolean success = stmt.execute(q);

			if (success) {
				ResultSet rs = stmt.getResultSet();

				while (rs.next()) {

					result.add(rs.getString("wfname"));

				}
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
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

		//		String q = "SELECT type FROM Processor WHERE pname = \'" + procName
		//				+ "\'";

		//		Statement stmt;
		try {
			ps = getConnection().prepareStatement("SELECT type FROM Processor WHERE pname = ?");
			ps.setString(1, procName);
			//			stmt = getConnection().createStatement();
			boolean success = ps.execute();

			if (success) {
				ResultSet rs = ps.getResultSet();

				if (rs.next() && rs.getString("type").equals(DATAFLOW_TYPE))
					return true;
			}
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		return false;
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
		if (value != null)
			queryConstraints.put("valTo", value);
		if (iteration != null)
			queryConstraints.put("iteration", iteration);
		if (wfInstance != null)
			queryConstraints.put("wfInstance", wfInstance);

		String q = "SELECT * FROM   DD ";

		q = addWhereClauseToQuery(q, queryConstraints, true); // true: terminate
		// SQL statement

		Statement stmt;
		try {
			stmt = getConnection().createStatement();
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
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}
		return null;
	}

	public Set<DDRecord> queryArcsForDD(String p, String v, String val,
			String wfInstance) throws SQLException {

		Set<DDRecord> result = new HashSet<DDRecord>();

		PreparedStatement ps = null;

		String q = "SELECT DISTINCT A.sourcePNameRef AS p, A.sourceVarNameRef AS var, VB.value AS val "
			+ "FROM   Arc A JOIN VarBinding VB ON VB.varNameRef = A.sinkVarNameRef AND VB.PNameRef = A.sinkPNameRef "
			+ "JOIN   WFInstance WF ON WF.wfnameRef = A.wfInstanceRef AND WF.instanceID = VB.wfInstanceRef  "
			+ "WHERE  WF.instanceID = '"
			+ wfInstance
			+ "' AND A.sinkPNameRef = '"
			+ p
			+ "' AND A.sinkVarNameRef = '"
			+ v + "' AND VB.value = '" + val + "' ";

		// Statement stmt;
		try {
			ps = getConnection()
			.prepareStatement(
					"SELECT DISTINCT A.sourcePNameRef AS p, A.sourceVarNameRef AS var, VB.value AS val "
					+ "FROM   Arc A JOIN VarBinding VB ON VB.varNameRef = A.sinkVarNameRef AND VB.PNameRef = A.sinkPNameRef "
					+ "JOIN   WFInstance WF ON WF.wfnameRef = A.wfInstanceRef AND WF.instanceID = VB.wfInstanceRef  "
					+ "WHERE  WF.instanceID = ? AND A.sinkPNameRef = ? AND A.sinkVarNameRef = ? AND VB.value = ?");

			ps.setString(1, wfInstance);
			ps.setString(2, p);
			ps.setString(3, v);
			ps.setString(4, val);

			// stmt = getConnection().createStatement();
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
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}
		return null;
	}

	public Set<DDRecord> queryAllFromValues(String wfInstance)
	throws SQLException {

		Set<DDRecord> result = new HashSet<DDRecord>();

		// String q =
		// "SELECT DISTINCT PFrom, vFrom, valFrom FROM DD where wfInstance = '"+wfInstance+"'";

		PreparedStatement ps = null;

		// Statement stmt;
		try {
			ps = getConnection()
			.prepareStatement(
			"SELECT DISTINCT PFrom, vFrom, valFrom FROM DD where wfInstance = ?");
			ps.setString(1, wfInstance);
			// stmt = getConnection().createStatement();
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
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		}

		return null;

	}



	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}

	public String getDbURL() {
		return dbURL;
	}

}