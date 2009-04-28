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
package net.sf.taverna.t2.provenance.connector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.log4j.Logger;

import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.item.WorkflowProvenanceItem;
import net.sf.taverna.t2.provenance.lineageservice.EventProcessor;
import net.sf.taverna.t2.provenance.lineageservice.LineageQueryResult;
import net.sf.taverna.t2.provenance.lineageservice.LineageQueryResultRecord;
import net.sf.taverna.t2.provenance.lineageservice.LineageSQLQuery;
import net.sf.taverna.t2.provenance.lineageservice.Provenance;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceQuery;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceWriter;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceAnalysis;
import net.sf.taverna.t2.provenance.reporter.ProvenanceReporter;

/**
 * Collects {@link ProvenanceItem}s as it travels up and down the dispatch stack
 * inside the InvocationContext
 * 
 * @author Ian Dunlop
 * 
 */
public abstract class ProvenanceConnector implements ProvenanceReporter {

	private static Logger logger = Logger.getLogger(ProvenanceConnector.class);

	private String saveEvents;

	protected Connection connection;

	private ProvenanceAnalysis provenanceAnalysis;

	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
			10);

	private boolean isClearDB = false;

	private Provenance provenance;

	private String dbURL;

	private boolean finished = false;

	private String sessionID;

	public ProvenanceConnector() {

	}

	public ProvenanceConnector(Provenance provenance,
			ProvenanceAnalysis provenanceAnalysis, String dbURL,
			boolean isClearDB, String saveEvents) {
		this.provenance = provenance;
		this.setProvenanceAnalysis(provenanceAnalysis);
		this.dbURL = dbURL;
		this.isClearDB = isClearDB;
		this.saveEvents = saveEvents;
	}

	protected Connection getConnection() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		if (connection == null) {
			openConnection();
		}
		return connection;
	}

	/**
	 * Used by database backed provenance stores. Ask the implementation to
	 * create the database. Requires each datbase type to create all its own
	 * tables
	 */
	public abstract void createDatabase();

	/**
	 * Used by database backed provenance stores. Ask the implementation to
	 * delete the database.
	 */
	public abstract void deleteDatabase();

	/**
	 * Clear all the values in the database but keep the db there
	 */
	public void clearDatabase() {
		String q = null;

		Statement stmt = null;
		try {
			stmt = getConnection().createStatement();
		} catch (SQLException e) {
			logger.warn("Could not create database statement :" + e);
		} catch (InstantiationException e) {
			logger.warn("Could not create database statement :" + e);
		} catch (IllegalAccessException e) {
			logger.warn("Could not create database statement :" + e);
		} catch (ClassNotFoundException e) {
			logger.warn("Could not create database statement :" + e);
		}

		q = "DELETE FROM Workflow";
		try {
			stmt.executeUpdate(q);
		} catch (SQLException e) {
			logger.warn("Could not execute statement " + q + " :" + e);
		}

		q = "DELETE FROM Processor";
		try {
			stmt.executeUpdate(q);
		} catch (SQLException e) {
			logger.warn("Could not execute statement " + q + " :" + e);
		}

		q = "DELETE FROM Arc";
		try {
			stmt.executeUpdate(q);
		} catch (SQLException e) {
			logger.warn("Could not execute statement " + q + " :" + e);
		}

		q = "DELETE FROM Var";
		try {
			stmt.executeUpdate(q);
		} catch (SQLException e) {
			logger.warn("Could not execute statement " + q + " :" + e);
		}

		q = "DELETE FROM WfInstance";
		try {
			stmt.executeUpdate(q);
		} catch (SQLException e) {
			logger.warn("Could not execute statement " + q + " :" + e);
		}

		q = "DELETE FROM ProcBinding";
		try {
			stmt.executeUpdate(q);
		} catch (SQLException e) {
			logger.warn("Could not execute statement " + q + " :" + e);
		}

		q = "DELETE FROM VarBinding";
		try {
			stmt.executeUpdate(q);
		} catch (SQLException e) {
			logger.warn("Could not execute statement " + q + " :" + e);
		}

		q = "DELETE FROM Collection";
		try {
			stmt.executeUpdate(q);
		} catch (SQLException e) {
			logger.warn("Could not execute statement " + q + " :" + e);
		}

		q = "DELETE FROM Data";
		try {
			stmt.executeUpdate(q);
		} catch (SQLException e) {
			logger.warn("Could not execute statement " + q + " :" + e);
		}
	}

	/**
	 * The name for this type of provenance connector. Is used by the workbench
	 * to ensure it adds the correct one to the InvocationContext
	 * 
	 * @return
	 */
	public abstract String getName();

	/**
	 * A unique identifier for this run of provenance, should correspond to the
	 * initial {@link WorkflowProvenanceItem} idenifier that gets sent through
	 * 
	 * @param identifier
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * What is the unique identifier used by this connector
	 * 
	 * @return
	 */
	public String getSessionID() {
		return sessionID;
	}

	public boolean isClearDB() {
		return isClearDB;
	}

	public void setClearDB(boolean isClearDB) {
		this.isClearDB = isClearDB;
	}

	/**
	 * Set up the the {@link EventProcessor}, {@link ProvenanceWriter} &
	 * {@link ProvenanceQuery}. Since it is an SPI you don't want any code
	 * cluttering the default constructor. Call this method after instantiation
	 * and after the dbURL has been set.
	 */
	public abstract void init();

	public List<LineageQueryResultRecord> getIntermediateValues(
			final String wfInstance, final String pname, final String vname,
			final String iteration) throws Exception {
		LineageQueryResult fetchIntermediateResult = getProvenanceAnalysis()
				.fetchIntermediateResult(wfInstance, pname, vname, iteration);

		LineageQueryResult result = null;
		FutureTask<LineageQueryResult> future = new FutureTask<LineageQueryResult>(
				new Callable<LineageQueryResult>() {

					public LineageQueryResult call() throws Exception {
						try {
//							LineageSQLQuery simpleLineageQuery = provenance
//									.getPq().simpleLineageQuery(wfInstance,
//											pname, vname, iteration);
							LineageQueryResult runLineageQuery = getProvenanceAnalysis()
									.fetchIntermediateResult(wfInstance, pname,
											vname, iteration);

							// runLineageQuery = provenance.getPq()
							// .runLineageQuery(simpleLineageQuery);
							return runLineageQuery;
						} catch (SQLException e) {
							throw e;
						}
					}

				});

		getExecutor().submit(future);

		try {
			return future.get().getRecords();
		} catch (InterruptedException e1) {
			throw e1;
		} catch (ExecutionException e1) {
			throw e1;
		}

	}

	public List<LineageQueryResultRecord> computeLineage(String wfInstance,
			String var, String proc, String path, Set<String> selectedProcessors) {
		return null;
	}

	public String getDataflowInstance(String dataflowId) {
		String instanceID = null;
		try {
			instanceID = (getProvenance()).getPq().getWFInstanceID(dataflowId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instanceID;
	}

	/**
	 * @return the saveEvents
	 */
	public String getSaveEvents() {
		return saveEvents;
	}

	/**
	 * @param saveEvents
	 *            the saveEvents to set
	 */
	public void setSaveEvents(String saveEvents) {
		this.saveEvents = saveEvents;
	}

	protected abstract void openConnection() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException;

	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}

	public String getDbURL() {
		return dbURL;
	}

	public void setProvenance(Provenance provenance) {
		this.provenance = provenance;
	}

	public Provenance getProvenance() {
		return provenance;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setExecutor(ScheduledThreadPoolExecutor executor) {
		this.executor = executor;
	}

	public synchronized ScheduledThreadPoolExecutor getExecutor() {
		return executor;
	}

	public void setProvenanceAnalysis(ProvenanceAnalysis provenanceAnalysis) {
		this.provenanceAnalysis = provenanceAnalysis;
	}

	/**
	 * Use this {@link ProvenanceAnalysis} to carry out lineage queries on the
	 * provenance
	 * 
	 * @return
	 */
	public ProvenanceAnalysis getProvenanceAnalysis() {
		return provenanceAnalysis;
	}

}
