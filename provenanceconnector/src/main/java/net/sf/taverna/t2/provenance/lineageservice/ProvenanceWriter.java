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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import java.util.logging.Level;
import net.sf.taverna.t2.provenance.connector.JDBCConnector;
import org.apache.log4j.Logger;

import net.sf.taverna.t2.provenance.lineageservice.utils.NestedListNode;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProcBinding;
import net.sf.taverna.t2.provenance.lineageservice.utils.Var;
import net.sf.taverna.t2.provenance.lineageservice.utils.VarBinding;

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
public abstract class ProvenanceWriter {

	protected static Logger logger = Logger.getLogger(ProvenanceWriter.class);	

	private String dbURL;

	protected int cnt; // counts number of calls to VarBinding	

	public Connection getConnection() throws InstantiationException,
	IllegalAccessException, ClassNotFoundException,SQLException {
		return JDBCConnector.getConnection();
	}

	/**
	 * add each Var as a row into the VAR DB table<br/>
	 * <strong>note: no static var type available as part of the
	 * dataflow...</strong>
	 * 
	 * @param vars
	 * @param wfId
	 * @throws SQLException
	 */
	public void addVariables(List<Var> vars, String wfId) throws SQLException {
		PreparedStatement ps = null;
		Statement stmt = null;
                Connection connection = null;
		try {
                        connection= getConnection();
			ps = connection
			.prepareStatement(
			"INSERT INTO Var (varname, pNameRef, inputOrOutput, nestingLevel, wfInstanceRef) VALUES(?,?,?,?,?)");
			stmt = getConnection().createStatement();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String q;
		for (Var v : vars) {

			int isInput = v.isInput() ? 1 : 0;

			// q =
			// "INSERT INTO Var (varname, pNameRef, inputOrOutput, nestingLevel, wfInstanceRef) VALUES(\'"
			// + v.getVName()
			// + "\',\'"
			// + v.getPName()
			// + "\',"
			// + isInput
			// + ","
			// + (v.getTypeNestingLevel() >= 0 ? v.getTypeNestingLevel()
			// : 0) + ",\'" + wfId + "\')";

			int i = v.getTypeNestingLevel() >= 0 ? v.getTypeNestingLevel() : 0;
			ps.setString(1, v.getVName());
			ps.setString(2, v.getPName());
			ps.setInt(3, isInput);
			ps.setInt(4, i);
			ps.setString(5, wfId);
			//System.out.println("executing: "+ps);

			try {

				int result = ps.executeUpdate();
				// System.out.println(result+ " rows added to DB");

			} catch (Exception e) {
				// ignore this insert and continue
				continue;
			}

		}

                if (connection!=null) connection.close();
	}

	/**
	 * inserts one row into the ARC DB table -- OBSOLETE, see instead
	 * 
	 * @param sourceVar
	 * @param sinkVar
	 * @param wfId
	 */
	public void addArc(Var sourceVar, Var sinkVar, String wfId)
	throws SQLException {
		Statement stmt = null;
		PreparedStatement ps = null;
                Connection connection=null;
		try {
                        connection = getConnection();
			ps = connection
			.prepareStatement(
			"INSERT INTO Arc (wfInstanceRef, sourcePNameRef, SourceVarNameRef, sinkPNameRef,sinkVarNameRef) VALUES(?,?,?,?,?)");
			stmt = getConnection().createStatement();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

			// String q =
			// "INSERT INTO wfInstanceRef (wfInstanceRef, sourcePNameRef, SourceVarNameRef, sinkPNameRef,sinkVarNameRef) VALUES(\'"
			// + wfId
			// + "\',\'"
			// + sourceVar.getPName()
			// + "\',\'"
			// + sourceVar.getVName()
			// + "\',\'"
			// + sinkVar.getPName()
			// + "\',\'"
			// + sinkVar.getVName() + "\')";

			ps.setString(1, wfId);
			ps.setString(2, sourceVar.getPName());
			ps.setString(3, sourceVar.getVName());
			ps.setString(4, sinkVar.getPName());
			ps.setString(5, sinkVar.getVName());

			// System.out.println("executing: "+q);
			int result = ps.executeUpdate();
			// System.out.println("workflow id: "+result+" rows added to DB");
		} finally {
                    if (connection != null) connection.close();
                }

	}

	public void addArc(String sourceVarName, String sourceProcName,
			String sinkVarName, String sinkProcName, String wfId) {
		PreparedStatement ps = null;
		Connection connection = null;
		try {
                        connection = getConnection();
			ps = connection
			.prepareStatement(
			"INSERT INTO Arc (wfInstanceRef, sourcePNameRef, sourceVarNameRef, sinkPNameRef, sinkVarNameRef) VALUES(?,?,?,?,?)");
			

		// String q =
		// "INSERT INTO Arc (wfInstanceRef, sourcePNameRef, sourceVarNameRef, sinkPNameRef, sinkVarNameRef) VALUES(\'"
		// + wfId
		// + "\',\'"
		// + sourceProcName
		// + "\',\'"
		// + sourceVarName
		// + "\',\'" + sinkProcName + "\',\'" + sinkVarName + "\')";

		ps.setString(1, wfId);
		ps.setString(2, sourceProcName);
		ps.setString(3, sourceVarName);
		ps.setString(4, sinkProcName);
		ps.setString(5, sinkVarName);
		// System.out.println("executing: "+q);
		int result = ps.executeUpdate();
		// System.out.println("workflow id: "+result+" rows added to DB");
		
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} finally {
                    if (connection != null) try {
                connection.close();
            } catch (SQLException ex) {
                logger.error("There was an error closing the database connection",ex);
            }
                }

	}

	public void addWFId(String wfId) throws SQLException {
		
		PreparedStatement ps = null;
                Connection connection = null;
		try {
                        connection = getConnection();
			ps = connection.prepareStatement(
			"INSERT INTO Workflow (wfName) VALUES (?)");
                        ps.setString(1, wfId);
                        ps.executeUpdate();
			
		} catch (InstantiationException e1) {
			logger.warn(e1);
		} catch (IllegalAccessException e1) {
			logger.warn(e1);
		} catch (ClassNotFoundException e1) {
			logger.warn(e1);
		} finally {
                    if (connection!=null) {
                        connection.close();
                    }
                }




	}

	public void addWFId(String wfId, String parentWFname,String externalName) throws SQLException {
		Statement stmt = null;
		PreparedStatement ps = null;
                Connection connection = null;
		try {
                        connection = getConnection();
			ps = connection.prepareStatement(
			"INSERT INTO Workflow (wfname, parentWFname, externalName) VALUES (?,?,?)");
                        ps.setString(1, wfId);
		ps.setString(2, parentWFname);
		ps.setString(3, externalName);
		// String q = "INSERT INTO Workflow (wfname, parentWFname) VALUES (\'"
		// + wfId + "\'," + "\'" + parentWFname + "\')";

		// System.out.println("executing: "+q);
		ps.executeUpdate();
			
		} catch (InstantiationException e1) {
			logger.warn(e1);
		} catch (IllegalAccessException e1) {
			logger.warn(e1);
		} catch (ClassNotFoundException e1) {
			logger.warn(e1);
		} finally {
                    if (connection != null) connection.close();
                }

		
		// System.out.println("workflow id: "+result+" rows added to DB");
	}

	public void addWFInstanceId(String wfId, String wfInstanceId)
	throws SQLException {
		Statement stmt = null;
		PreparedStatement ps = null;
                Connection connection = null;
		try {
                        connection = getConnection();
			ps = connection
			.prepareStatement(
			"INSERT INTO WfInstance (instanceID, wfnameRef) VALUES (?,?)");

                        ps.setString(1, wfInstanceId);
		ps.setString(2, wfId);
		// String q =
		// "INSERT INTO WfInstance (instanceID, wfnameRef) VALUES (\'"
		// + wfInstanceId + "\'" + ", \'" + wfId + "\')";

		// System.out.println("executing: "+q);
		ps.executeUpdate();
		} catch (InstantiationException e1) {
			logger.warn(e1);
		} catch (IllegalAccessException e1) {
			logger.warn(e1);
		} catch (ClassNotFoundException e1) {
			logger.warn(e1);
		} finally {
                    if (connection!=null) connection.close();
                }

		
		// System.out.println("workflow id: "+result+" rows added to DB");
	}

	/**
	 * insert new processor into the provenance DB
	 * 
	 * @param name
	 * @throws SQLException
	 */
	public void addProcessor(String name, String wfID, boolean isTopLevel) throws SQLException {
		addProcessor(name, null, wfID, isTopLevel);  
	}

	/**
	 * add a processor to the static portion of the DB with given name, type and
	 * wfnameRef scope
	 * 
	 * @param name
	 * @param type
	 * @param wfNameRef
	 * @throws SQLException
	 */
	public void addProcessor(String name, String type, String wfNameRef, boolean isTopLevel)
	throws SQLException {

//		logger.debug("addProcessor: adding proc "+name+" to "+wfNameRef);
		
		PreparedStatement ps = null;
                Connection connection = null;
		try {
                        connection = getConnection();
			ps = connection
			.prepareStatement(
			"INSERT INTO Processor (pname, type, wfInstanceRef, isTopLevel) VALUES (?,?,?,?)");

                        ps.setString(1, name);
		ps.setString(2, type);
		ps.setString(3, wfNameRef);
		ps.setBoolean(4, isTopLevel);

		int result = ps.executeUpdate();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
                    if (connection != null) connection.close();
                }

		
	}
	
	

	public void addProcessorBinding(ProcBinding pb) throws SQLException {
		Statement stmt = null;
		PreparedStatement ps = null;
                Connection connection = null;
		try {
                        connection = getConnection();
			ps = connection
			.prepareStatement(
			"INSERT INTO ProcBinding (pnameRef, execIDRef, iteration, actName) VALUES(?,?,?,?)");
			ps.setString(1, pb.getPNameRef());
		ps.setString(2, pb.getExecIDRef());
		ps.setString(3, pb.getIterationVector());
		ps.setString(4, pb.getActName());
		// String q =
		// "INSERT INTO ProcBinding (pnameRef, execIDRef, iteration, actName) VALUES(\'"
		// + pb.getPNameRef()
		// + "\',\'"
		// + pb.getExecIDRef()
		// + "\',\'"
		// + pb.getIterationVector() + "\',\'" + pb.getActName() + "\')";

		// System.out.println("executing: "+q);

		ps.executeUpdate();

		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
                    if (connection != null) connection.close();
                }

		

		

	}

	public String addCollection(String processorId, String collId,
			String parentCollectionId, String iteration, String portName,
			String dataflowId) throws SQLException {
		String newParentCollectionId = null;

		Statement stmt = null;
		PreparedStatement ps = null;
                Connection connection = null;
		try {
                        connection = getConnection();
			ps = connection
			.prepareStatement(
			"INSERT INTO Collection (PNameRef, wfInstanceRef, varNameRef, iteration, parentCollIdRef, collId) VALUES(?,?,?,?,?,?)");

                        if (parentCollectionId == null) {
			// this is a top-level list
			parentCollectionId = "TOP";
		}

		newParentCollectionId = collId;

		ps.setString(1, processorId);
		ps.setString(2, dataflowId);
		ps.setString(3, portName);
		ps.setString(4, iteration);
		ps.setString(5, parentCollectionId);
		ps.setString(6, collId);
		// String q =
		// "INSERT INTO Collection (PNameRef, wfInstanceRef, varNameRef, iteration, parentCollIdRef, collId) VALUES(\'"
		// + processorId
		// + "\',\'"
		// + dataflowId
		// + "\',\'"
		// + portName
		// + "\',\'"
		// + iteration
		// + "\',\'"
		// + parentCollectionId
		// + "\',\'"
		// + collId + "\')";

		// System.out.println("collection: processor ["+processorId+"] collId ["+collId+"]");

		ps.executeUpdate();
			
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
                    if (connection != null) connection.close();
                }

		

		// System.out.println("*** collection: "+result+ " rows added to DB");

		return newParentCollectionId;
	}

	/**
	 * adds (dataRef, data) pairs to the Data table (only for string data)
	 */
	public void addData(String dataRef, String wfInstanceId, byte[] data)
	throws SQLException {

                Connection connection = null;
		Statement stmt;
		try {
			connection = getConnection();
			PreparedStatement ps = null;
			ps = connection
			.prepareStatement(
			"INSERT INTO Data (dataReference,wfInstanceID,data) VALUES (?,?,?)");
			ps.setString(1, dataRef);
			ps.setString(2, wfInstanceId);
			ps.setBytes(3, data);
			// String q =
			// "INSERT INTO Data (dataReference,wfInstanceID,data) VALUES (?1,?2,?3)";
			// +
			// "\'" +dataRef+"\', \'" + wfInstanceId+ "\'," + data + ")";

			int result = ps.executeUpdate();

			cnt++;

		} catch (SQLException e) {
//			System.out.println(e);
			// the same ID will come in several times -- duplications are
			// expected, don't panic
			// System.out.println("****  insert failed due to ["+e.getMessage()+"]");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
                    if (connection != null) connection.close();
                }
	}


	/**
	 * 
	 * @param dataRef
	 * @param wfInstanceId
	 * @param data  the data in bytearray form, untyped
	 * @param dve  an instance of a DataExtractor. This maps the data bytearray to a string according to the
	 * semantics of the data prior to inserting the data into the DB. It's a bit of a hack used in this impl. to extract significant parts of an XMLEncoded bean
	 * that can be then used in other contexts (mainly, in OPM graphs, where a raw byte array would not be interpreted).
	 * @throws SQLException
	 */


	public void addVarBinding(VarBinding vb) throws SQLException {
		Statement stmt = null;
		PreparedStatement ps = null;
                Connection connection = null;

		try {
                        connection = getConnection();
			ps = connection
			.prepareStatement(
			"INSERT INTO VarBinding (pnameRef, wfInstanceRef, varNameRef, valueType, value, ref, collIdRef, iteration,positionInColl) VALUES(?,?,?,?,?,?,?,?,?)");
			stmt = getConnection().createStatement();

			ps.setString(1, vb.getPNameRef());
			ps.setString(2, vb.getWfInstanceRef());
			ps.setString(3, vb.getVarNameRef());
			ps.setString(4, vb.getValueType());
			ps.setString(5, vb.getValue());
			ps.setString(6, vb.getRef());
			ps.setString(7, vb.getCollIDRef());
			ps.setString(8, vb.getIteration());
			ps.setInt(9, vb.getPositionInColl());

			// String q =
			// "INSERT INTO VarBinding (pnameRef, wfInstanceRef, varNameRef, valueType, value, ref, collIdRef, iteration,positionInColl) VALUES(\'"
			// + vb.getPNameRef()
			// + "\',\'"
			// + vb.getWfInstanceRef()
			// + "\',\'"
			// + vb.getVarNameRef()
			// + "\',\'"
			// + vb.getValueType()
			// + "\',\'"
			// + vb.getValue()
			// + "\',\'"
			// + vb.getRef()
			// + "\',\'"
			// + vb.getCollIDRef()
			// + "\',\'"
			// + vb.getIteration()
			// + "\',"
			// + vb.getPositionInColl() + ")";

			// if (cnt % 100 == 0) {
//			logger.debug("Var binding: instance ["
//					+ vb.getWfInstanceRef() + "] processor ["
//					+ vb.getPNameRef() + "] varName [" + vb.getVarNameRef()
//					+ "] collIdRef [" + vb.getCollIDRef() + "] iteration ["
//					+ vb.getIteration() + "] positionInCollection ["
//					+ vb.getPositionInColl() + "] value [" + vb.getValue()
//					+ "]");
			// }

			int result = ps.executeUpdate();

			cnt++;
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			logger.warn("Var binding insert failed due to ["
					+ e.getMessage() + "]");
		} finally {
                    if (connection != null) connection.close();
                }
	}

	/**
	 * persists var v back to DB
	 * 
	 * @param v
	 * @throws SQLException
	 */
	public void updateVar(Var v) throws SQLException {
		// Statement stmt;
		PreparedStatement ps = null;
		// String u = "UPDATE Var " + "SET type = \'" + v.getType() + "\'"
		// + ", inputOrOutput = \'" + (v.isInput() ? 1 : 0) + "\' "
		// + ", nestingLevel = \'" + v.getTypeNestingLevel() + "\' "
		// + ", actualNestingLevel = \'" + v.getActualNestingLevel()
		// + "\' " + ", anlSet = \'" + (v.isANLset() ? 1 : 0) + "\' "
		// + "WHERE varName = \'" + v.getVName() + "\' "
		// + "AND pnameRef = \'" + v.getPName() + "\' "
		// + "AND wfInstanceRef = \'" + v.getWfInstanceRef() + "\'";
                Connection connection = null;
		try {
                        connection = getConnection();
			ps = connection
			.prepareStatement(
					"UPDATE Var SET type = ?, inputOrOutput=?, nestingLevel = ?,"
					+ "actualNestingLevel = ?, anlSet = ? , Var.order = ? WHERE varName = ? AND pnameRef = ? AND wfInstanceRef = ?");
			ps.setString(1, v.getType());
			int i = v.isInput() ? 1 : 0;
			ps.setInt(2, i);
			ps.setInt(3, v.getTypeNestingLevel());
			ps.setInt(4, v.getActualNestingLevel());
			int j = v.isANLset() ? 1 : 0;
			ps.setInt(5, j);
			ps.setInt(6, v.getPortNameOrder());
			ps.setString(7, v.getVName());
			ps.setString(8, v.getPName());
			ps.setString(9, v.getWfInstanceRef());

			// stmt = getConnection().createStatement();
			//			
			// System.out.println("executing: "+u);

			boolean success = ps.execute();
		} catch (InstantiationException e) {
			logger.warn("Could not execute query: " + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not execute query: " + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not execute query: " + e);
		} finally {
                    if (connection != null) connection.close();
                }

		// System.out.println("update executed");
	}

	public void updateVarBinding(VarBinding vb) {

		Statement stmt;
		PreparedStatement ps = null;
                Connection connection = null;
		try {
                        connection = getConnection();
			ps = connection
			.prepareStatement(
					"UPDATE VarBinding SET valueType = ?, value = ?, ref = ?,"
					+ "collIdRef = ? WHERE varNameRef = ? AND wfInstanceRef = ? AND pnameRef = ? AND iteration = ?");

			ps.setString(1, vb.getValueType());
			ps.setString(2, vb.getValue());
			ps.setString(3, vb.getRef());
			ps.setString(4, vb.getCollIDRef());
			ps.setString(5, vb.getVarNameRef());
			ps.setString(6, vb.getWfInstanceRef());
			ps.setString(7, vb.getPNameRef());
//			ps.setInt(8, vb.getPositionInColl());
			ps.setString(8, vb.getIteration());

			// String q = "UPDATE VarBinding SET "+
			// "valueType = \""+vb.getValueType()+"\", "+
			// "value  = \""+vb.getValue()+"\", "+
			// "ref    = \""+vb.getRef()+"\", "+
			// "collIdRef    = \""+vb.getCollIDRef()+"\" "+
			// "WHERE varNameRef = \""+vb.getVarNameRef()+"\" and "+
			// "wfInstanceRef = \""+vb.getWfInstanceRef()+"\" and "+
			// "pnameRef = \""+vb.getPNameRef()+"\" and "+
			// "positionInColl = \""+vb.getPositionInColl()+"\" and "+
			// "iteration    = \""+vb.getIteration()+"\" ";

			// if (cnt % 100 == 0) {
//			System.out.println("updateVarBinding : "+ps.toString());

//			System.out.println("updateVarBinding with  instance ["+vb.getWfInstanceRef()+"] processor ["+vb.getPNameRef()+"] varName ["+vb.getVarNameRef()+
//			"] collIdRef ["+vb.getCollIDRef()+"] iteration ["+vb.getIteration()+
//			"] positionInCollection ["+vb.getPositionInColl()+"] value ["+vb.getValue()+"]");
			// }

			int result = ps.executeUpdate();

//			System.out.println("updateVarBinding : result "+result);

			cnt++;

		} catch (SQLException e) {
			logger.info("****  insert failed due to [" + e.getMessage() + "]");
		} catch (InstantiationException e) {
			logger.info("****  insert failed due to [" + e.getMessage() + "]");
		} catch (IllegalAccessException e) {
			logger.info("****  insert failed due to [" + e.getMessage() + "]");
		} catch (ClassNotFoundException e) {
			logger.info("****  insert failed due to [" + e.getMessage() + "]");
		} finally {
                    if (connection != null) try {
                connection.close();
            } catch (SQLException ex) {
                logger.error("There was an error closing the database connection",ex);
            }
                }
	}

	public void replaceCollectionRecord(NestedListNode nln, String prevPName,
			String prevVarName) {

		// Statement stmt;
		PreparedStatement ps = null;
                Connection connection = null;
		try {
                        connection = getConnection();
			ps = connection
			.prepareStatement(
					"DELETE FROM Collection WHERE collId = ? and wfInstanceRef = ?"
					+ " and varNameRef = ? and pnameRef = ? and iteration = ?");
			ps.setString(1, nln.getCollId());
			ps.setString(2, nln.getWfInstanceRef());
			ps.setString(3, prevVarName);
			ps.setString(4, prevPName);
			ps.setString(5, nln.getIteration());
			// stmt = getConnection().createStatement();

			// String q = "DELETE FROM Collection WHERE "+
			// "collId = \""+nln.getCollId()+"\" and "+
			// "wfInstanceRef = \""+nln.getWfInstanceRef()+"\" and "+
			// "varNameRef = \""+prevVarName+"\" and "+
			// "pnameRef = \""+prevPName+"\" and "+
			// "iteration = \""+nln.getIteration()+"\" ";

			int result = ps.executeUpdate();

		} catch (SQLException e) {
			logger.warn("delete failed due to [" + e.getMessage() + "]");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
                    if (connection != null) try {
                connection.close();
            } catch (SQLException ex) {
                logger.error("There was an error closing the database connection",ex);
            }
                }

		try {

			addCollection(prevPName, nln.getCollId(), nln.getParentCollIdRef(),
					nln.getIteration(), prevVarName, nln.getWfInstanceRef());

		} catch (SQLException e) {
			logger.warn("insert failed due to [" + e.getMessage()
					+ "]");
		}
	}

	/**
	 * deletes DB contents for the static structures -- called prior to each run
	 * 
	 * @throws SQLException
	 */
	public void clearDBStatic() throws SQLException {
		String q = null;
		int result = 0;

		Statement stmt = null;
                Connection connection = null;
		try {
                        connection = getConnection();
			stmt = connection.createStatement();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			q = "DELETE FROM Workflow";
			// System.out.println("executing: "+q);
			result = stmt.executeUpdate(q);

                        q = "DELETE FROM Processor";
			result = stmt.executeUpdate(q);

                        q = "DELETE FROM Arc";
			result = stmt.executeUpdate(q);

                        q = "DELETE FROM Var";
			// System.out.println("executing: "+q);
			result = stmt.executeUpdate(q);
		} catch (SQLException e) {

		} finally {
                    if (connection != null) connection.close();
                }
		
		logger.info("DB cleared STATIC");
	}

	/**
	 * deletes DB contents for the static structures -- called prior to each run
	 * 
	 * @throws SQLException
	 */
	public void clearDBStatic(String wfID) throws SQLException {
		String q = null;
		int result = 0;

		// Statement stmt = null;
		PreparedStatement ps = null;
                Connection connection = null;
		try {
                        connection = getConnection();
			ps = connection.prepareStatement(
			"DELETE FROM Workflow WHERE wfname = ?");
			ps.setString(1, wfID);
			ps.executeUpdate();
			ps = getConnection().prepareStatement(
			"DELETE FROM Processor WHERE wfInstanceRef = ?");
			ps.setString(1, wfID);
			ps.executeUpdate();
			ps = getConnection().prepareStatement(
			"DELETE FROM Arc WHERE wfInstanceRef = ?");
			ps.setString(1, wfID);
			ps.executeUpdate();
			ps = getConnection().prepareStatement(
			"DELETE FROM Var WHERE wfInstanceRef = ?");
			ps.setString(1, wfID);
			ps.executeUpdate();

			// stmt = getConnection().createStatement();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
                    if (connection != null) {
                        connection.close();
                    }
                }

		
		logger.info("DB cleared STATICfor wfID " + wfID);
	}

	/**
	 * deletes DB contents for all runs -- for testing purposes
	 * 
	 * @throws SQLException
	 */
	public void clearDBDynamic() throws SQLException {
		String q = null;
		int result = 0;
                Connection connection = null;
		Statement stmt = null;
		try {
                        connection = getConnection();
			stmt = connection.createStatement();

                        q = "DELETE FROM WfInstance";
		// System.out.println("executing: "+q);
		result = stmt.executeUpdate(q);
		// System.out.println(result+ " rows removed from DB");

		q = "DELETE FROM ProcBinding";
		// System.out.println("executing: "+q);
		result = stmt.executeUpdate(q);
		// System.out.println(result+ " rows removed from DB");

		q = "DELETE FROM VarBinding";
		// System.out.println("executing: "+q);
		result = stmt.executeUpdate(q);
		// System.out.println(result+ " rows removed from DB");

		q = "DELETE FROM Collection";
		// System.out.println("executing: "+q);
		result = stmt.executeUpdate(q);
		// System.out.println(result+ " rows removed from DB");

		q = "DELETE FROM Data";
		result = stmt.executeUpdate(q);

		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
                    if (connection != null) connection.close();
                }

		

		logger.info("DB cleared DYNAMIC");

	}

	public void clearDD() {

		Statement stmt = null;
                Connection connection = null;

		try {
                        connection = getConnection();
			stmt = connection.createStatement();
			String q = "DELETE FROM DD";
			stmt.executeUpdate(q);
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
                    if (connection != null) try {
                connection.close();
            } catch (SQLException ex) {
                logger.error("There was an error closing the database connection.",ex);
            }
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
	 * @param wfInstanceID
	 */
	// FIXME needs the db statement corrected
	public void writeDDRecord(String pFrom, String vFrom, String valFrom,
			String pTo, String vTo, String valTo, String iteration,
			String wfInstanceID) {

		Statement stmt = null;
                Connection connection = null;

		try {
                        connection = getConnection();
			stmt = connection.createStatement();
			String q = "INSERT INTO DD (PFrom,VFrom,valFrom,PTo,VTo,valTo,iteration,wfInstance) VALUES ("
				+ "\'"
				+ pFrom
				+ "\',"
				+ "\'"
				+ vFrom
				+ "\",  "
				+ "valFrom = \""
				+ valFrom
				+ "\", "
				+ "PTo = \""
				+ pTo
				+ "\", "
				+ "VTo = \""
				+ vTo
				+ "\", "
				+ "valTo  = \""
				+ valTo
				+ "\", "
				+ "iteration = \""
				+ iteration
				+ "\", "
				+ "wfInstance = \""
				+ wfInstanceID + "\"; ";

			stmt.executeUpdate(q);
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
                    if (connection != null) try {
                connection.close();
            } catch (SQLException ex) {
                logger.error("There was an error closing the database connection.",ex);
            }
                }
	}

	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}

	public String getDbURL() {
		return dbURL;
	}

}