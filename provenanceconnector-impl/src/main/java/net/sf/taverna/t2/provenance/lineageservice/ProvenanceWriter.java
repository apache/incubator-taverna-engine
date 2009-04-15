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
import java.sql.SQLException;
import java.util.List;

import net.sf.taverna.t2.provenance.lineageservice.utils.ProcBinding;
import net.sf.taverna.t2.provenance.lineageservice.utils.Var;
import net.sf.taverna.t2.provenance.lineageservice.utils.VarBinding;

public interface ProvenanceWriter {


	public abstract Connection openConnection() 
	  throws InstantiationException, IllegalAccessException, ClassNotFoundException;

		/**
	 * add each Var as a row into the VAR DB table<br/>
	 * <strong>note: no static var type available as part of the dataflow...</strong>
	 * @param vars
	 * @param wfId
	 * @throws SQLException 
	 */
	public abstract void addVariables(List<Var> vars, String wfId)
			throws SQLException;

	/**
	 * inserts one row into the ARC DB table  -- OBSOLETE, see instead
	 * @param sourceVar
	 * @param sinkVar
	 * @param wfId
	 */
	public abstract void addArc(Var sourceVar, Var sinkVar, String wfId) throws SQLException;

	public abstract void addData(String dataRef, String wfInstanceId, String renderedData) throws SQLException;
	
	
	public abstract void addArc(String sourceVarName, String sourceProcName,
			String sinkVarName, String sinkProcName, String wfId)
			throws SQLException;

	public abstract void addWFId(String wfId) throws SQLException;

	public abstract void addWFId(String wfId, String parentWFname) throws SQLException;

	
	public abstract void addWFInstanceId(String wfId, String wfInstanceId)
			throws SQLException;

	/**
	 * insert new processor into the provenance DB
	 * @param name
	 * @throws SQLException 
	 */
	public abstract void addProcessor(String name, String wfID)
			throws SQLException;

	/**
	 * add a processor to the static portion of the DB with given name, type and wfnameRef scope 
	 * @param name
	 * @param type
	 * @param wfNameRef
	 * @throws SQLException
	 */
	public void addProcessor(String name, String type, String wfNameRef) throws SQLException; 
		
	public abstract void addProcessorBinding(ProcBinding pb)
			throws SQLException;

	public abstract String addCollection(String processorId, String collId,
			String parentCollectionId, String iteration, String portName,
			String dataflowId) throws SQLException;

	public abstract void addVarBinding(VarBinding vb, Object context) throws SQLException;

	/**
	 * deletes DB contents for the static structures -- called prior to each run 
	 * @throws SQLException 
	 */
	public abstract void clearDBStatic() throws SQLException;

	/**
	 * deletes DB contents for the static structures -- called prior to each run 
	 * @throws SQLException 
	 */
	public abstract void clearDBStatic(String wfID) throws SQLException;

	/**
	 * deletes DB contents for all runs -- for testing purposes 
	 * @throws SQLException 
	 */
	public abstract void clearDBDynamic() throws SQLException;

}