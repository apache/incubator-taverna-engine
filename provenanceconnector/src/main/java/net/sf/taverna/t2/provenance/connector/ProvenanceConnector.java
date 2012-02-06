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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.item.WorkflowProvenanceItem;
import net.sf.taverna.t2.provenance.lineageservice.EventProcessor;
import net.sf.taverna.t2.provenance.lineageservice.LineageQueryResultRecord;
import net.sf.taverna.t2.provenance.lineageservice.Provenance;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceAnalysis;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceQuery;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceWriter;
import net.sf.taverna.t2.provenance.lineageservice.WorkflowDataProcessor;
import net.sf.taverna.t2.provenance.reporter.ProvenanceReporter;
import net.sf.taverna.t2.reference.ReferenceService;

import org.apache.log4j.Logger;

import uk.org.taverna.platform.database.DatabaseManager;

/**
 * Collects {@link ProvenanceItem}s as it travels up and down the dispatch stack
 * inside the InvocationContext
 *
 * @author Ian Dunlop
 * @author Stuart Owen
 *
 */
public abstract class ProvenanceConnector implements ProvenanceReporter {

	public static enum ActivityTable {
		Activity, activityId, activityDefinition, workflowId;

		public static String getCreateTable() {
			return "CREATE TABLE " + Activity + "(\n"
			+ activityId + " varchar(36) NOT NULL,\n"
			+ activityDefinition + " blob NOT NULL,\n"
			+ workflowId + " varchar(100) NOT NULL, \n"
			+ "PRIMARY KEY (" + activityId + ")\n" + ")";
		}
	}

	public static enum CollectionTable {
		Collection, collID, parentCollIDRef, workflowRunId, processorNameRef, portName, iteration;
		public static String getCreateTable() {
			return "CREATE TABLE " + Collection + " (\n"
				+ collID + " varchar(100) NOT NULL,\n"
				+ parentCollIDRef + " varchar(100) NOT NULL ,\n"
				+ workflowRunId + " varchar(36) NOT NULL,\n"
				+ processorNameRef + " varchar(100) NOT NULL,\n"
				+ portName + " varchar(100) NOT NULL,\n"
				+ iteration + " varchar(2000) NOT NULL default '',\n"
				+ " PRIMARY KEY (\n"
				+ collID + "," + workflowRunId + "," + processorNameRef
				+ "," + portName + "," + parentCollIDRef + "," + iteration
				+ "))";
		}
	}

	public static enum DataBindingTable {
		DataBinding, dataBindingId, portId, t2Reference, workflowRunId;

		public static String getCreateTable() {
			return "CREATE TABLE " + DataBinding + "(\n"
			+ dataBindingId + " varchar(36) NOT NULL,\n"
			+ portId + " varchar(36) NOT NULL,\n"
			+ t2Reference + " varchar(100) NOT NULL,\n"
			+ workflowRunId + " varchar(100) NOT NULL, \n"
			+ "PRIMARY KEY (" + dataBindingId + "," + portId + ")\n" + ")";
		}
	}

	public static enum DataflowInvocationTable {
		DataflowInvocation, dataflowInvocationId,
		workflowId,
		invocationStarted, invocationEnded,
		inputsDataBinding, outputsDataBinding,
		parentProcessorEnactmentId, workflowRunId, completed;

		public static String getCreateTable() {
			return "CREATE TABLE " + DataflowInvocation + "(\n"
			+ dataflowInvocationId + " varchar(36) NOT NULL,\n"
			+ workflowId + " varchar(100) NOT NULL, \n"
			+ invocationStarted + " timestamp, \n"
			+ invocationEnded + " timestamp, \n"
			+ inputsDataBinding + " varchar(36),\n"
			+ outputsDataBinding + " varchar(36),\n"
			+ parentProcessorEnactmentId + " varchar(36), \n"
			+ workflowRunId + " varchar(100) NOT NULL, \n"
			+ completed + " smallint NOT NULL,\n"
			+ "PRIMARY KEY (" + dataflowInvocationId+ ")\n" + ")";
		}
	}

	public static enum DataLinkTable {
		Datalink, sourcePortName, sourcePortId, destinationPortId,
		destinationPortName, sourceProcessorName, destinationProcessorName, workflowId;
		public static String getCreateTable() {
			return "CREATE TABLE " + Datalink + " (\n"
					+ sourcePortName + " varchar(100) NOT NULL ,\n"
					+ sourcePortId + " varchar(36) NOT NULL ,\n"
					+ destinationPortId + " varchar(36) NOT NULL ,\n"
					+ destinationPortName + " varchar(100) NOT NULL,\n"
					+ sourceProcessorName + " varchar(100) NOT NULL,\n"
					+ destinationProcessorName + " varchar(100) NOT NULL,\n"
					+ workflowId + " varchar(36) NOT NULL,"
					+ " PRIMARY KEY  ("
					+ sourcePortId + "," + destinationPortId + "," + workflowId
					+ "))";
		}
	}

	public static enum PortBindingTable {
		PortBinding, portName, workflowRunId, value, collIDRef, positionInColl, processorNameRef, valueType, ref, iteration, workflowId;
		public static String getCreateTable() {
			return  "CREATE TABLE " + PortBinding + " (\n"
			+ portName + " varchar(100) NOT NULL,\n"
			+ workflowRunId + " varchar(100) NOT NULL,\n"
			+ value + " varchar(100) default NULL,\n"
			+ collIDRef + " varchar(100),\n"
			+ positionInColl + " int NOT NULL,\n"
			+ processorNameRef + " varchar(100) NOT NULL,\n"
			+ valueType + " varchar(50) default NULL,\n"
			+ ref + " varchar(100) default NULL,\n"
			+ iteration + " varchar(2000) NOT NULL,\n"
			+ workflowId + " varchar(36),\n"
			+ "PRIMARY KEY (\n"
			+ portName + "," + workflowRunId + ","
			+ processorNameRef + "," + iteration + ", " + workflowId
			+ "))";
		}
	}

	public static enum PortTable {
		Port, portId, processorId, portName, isInputPort, processorName,
		workflowId, depth, resolvedDepth, iterationStrategyOrder;
		public static String getCreateTable() {
			return  "CREATE TABLE " + Port + " (\n"
			+ portId + " varchar(36) NOT NULL,\n"
			+ processorId + " varchar(36),\n"
			+ portName + " varchar(100) NOT NULL,\n"
			+ isInputPort + " smallint NOT NULL ,\n"
			+ processorName + " varchar(100) NOT NULL,\n"
			+ workflowId + " varchar(36) NOT NULL,\n"
			+ depth + " int,\n"
			+ resolvedDepth + " int,\n"
			+ iterationStrategyOrder + " smallint, \n"
			+ "PRIMARY KEY (" + "portId" + "),\n"
			+ "CONSTRAINT port_constraint UNIQUE (\n"
			+ portName + "," + isInputPort + "," + processorName + "," + workflowId + "\n"
			+ "))";
		}
	}

	public static enum ProcessorEnactmentTable {
		ProcessorEnactment, processEnactmentId, workflowRunId, processorId,
		processIdentifier, iteration, parentProcessorEnactmentId,
		enactmentStarted, enactmentEnded, initialInputsDataBindingId,
		finalOutputsDataBindingId;

		public static String getCreateTable() {
			return "CREATE TABLE " + ProcessorEnactment + " (\n"
			+ processEnactmentId + " varchar(36) NOT NULL, \n"
			+ workflowRunId + " varchar(100) NOT NULL, \n"
			+ processorId + " varchar(36) NOT NULL, \n"
			+ processIdentifier + " varchar(2047) NOT NULL, \n"
			+ iteration + " varchar(100) NOT NULL, \n"
			+ parentProcessorEnactmentId + " varchar(36), \n"
			+ enactmentStarted + " timestamp, \n"
			+ enactmentEnded + " timestamp, \n"
			+ initialInputsDataBindingId + " varchar(36), \n"
			+ finalOutputsDataBindingId + " varchar(36), \n"
			+ " PRIMARY KEY (" + processEnactmentId + ")" + ")";
		}
	}

	public static enum ProcessorTable {
		Processor,processorId, processorName,workflowId,firstActivityClass,isTopLevel ;
		public static String getCreateTable() {
			return  "CREATE TABLE "+ Processor +" (\n"
			+ processorId + " varchar(36) NOT NULL,\n"
			+ processorName + " varchar(100) NOT NULL,\n"
			+ workflowId + " varchar(36) NOT NULL ,\n\n"
			+ firstActivityClass + " varchar(100) default NULL,\n"
			+ isTopLevel + " smallint, \n"
			+ "PRIMARY KEY (" + processorId+ "),\n"
			+ "CONSTRAINT processor_constraint UNIQUE (\n"
			+	processorName + "," + workflowId + "))";
		}
	}

	public static enum ServiceInvocationTable {
		ServiceInvocation, processorEnactmentId, workflowRunId,
		invocationNumber, invocationStarted, invocationEnded,
		inputsDataBinding, outputsDataBinding, failureT2Reference,
		activityId, initiatingDispatchLayer, finalDispatchLayer;

		public static String getCreateTable() {
			return "CREATE TABLE " + ServiceInvocation + "(\n"
			+ processorEnactmentId + " varchar(36) NOT NULL,\n"
			+ workflowRunId + " varchar(100) NOT NULL, \n"
			+ invocationNumber + " bigint NOT NULL,\n"
			+ invocationStarted + " timestamp, \n"
			+ invocationEnded + " timestamp, \n"
			+ inputsDataBinding + " varchar(36),\n"
			+ outputsDataBinding + " varchar(36),\n"
			+ failureT2Reference + " varchar(100) default NULL,\n"
			+ activityId + " varchar(36),\n"
			+ initiatingDispatchLayer + " varchar(250) NOT NULL,\n"
			+ finalDispatchLayer + " varchar(250) NOT NULL,\n"
			+ "PRIMARY KEY (" + processorEnactmentId + ", "
			+ invocationNumber + "))";
		}
	}

	public static enum WorkflowRunTable {
		WorkflowRun, workflowRunId, workflowId, timestamp;
		public static String getCreateTable() {
			return  "CREATE TABLE " + WorkflowRun + " (\n"
			+ workflowRunId + " varchar(36) NOT NULL,\n"
			+ workflowId + " varchar(36) NOT NULL,\n"
			+ timestamp + " timestamp NOT NULL default CURRENT_TIMESTAMP,\n"
			+ " PRIMARY KEY (" + workflowRunId + ", " + workflowId + "))";
		}
	}

	public static enum WorkflowTable {
		WorkflowTable, workflowId, parentWorkflowId, externalName, dataflow;
		public static String getCreateTable() {
			return "CREATE TABLE " + "Workflow (\n" +
					workflowId	+ " varchar(36) NOT NULL,\n"
					+ parentWorkflowId + " varchar(100),\n"
					+ externalName + " varchar(100),\n"
					+ dataflow + " blob, \n"
					+ "PRIMARY KEY  (" + workflowId	+ "))";
		}
	}

	private static Logger logger = Logger.getLogger(ProvenanceConnector.class);
	private String saveEvents;
	private ProvenanceAnalysis provenanceAnalysis;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private boolean finished = false;
	private String sessionID;
	private InvocationContext invocationContext;
	private ReferenceService referenceService;

	private Provenance provenance;
	private ProvenanceWriter writer;
	private ProvenanceQuery query;
	private WorkflowDataProcessor wfdp;
	private EventProcessor eventProcessor;
	private final DatabaseManager databaseManager;


	public ProvenanceConnector(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	/**
	 * Set up the the {@link EventProcessor}, {@link ProvenanceWriter} &
	 * {@link ProvenanceQuery}. Since it is an SPI you don't want any code
	 * cluttering the default constructor. Call this method after instantiation
	 * and after the dbURL has been set.
	 */
	public void init() {

        createDatabase();

		try {
			setWfdp(new WorkflowDataProcessor());
			getWfdp().setPq(getQuery());
			getWfdp().setPw(getWriter());

			setEventProcessor(new EventProcessor());
			getEventProcessor().setPw(getWriter());
			getEventProcessor().setPq(getQuery());
			getEventProcessor().setWfdp(getWfdp());

			setProvenanceAnalysis(new ProvenanceAnalysis(getQuery()));
			setProvenance(new Provenance(getEventProcessor()));
		} catch (InstantiationException e) {
			logger.error("Problem with provenance initialisation: ",e);
		} catch (IllegalAccessException e) {
			logger.error("Problem with provenance initialisation: ",e);
		} catch (ClassNotFoundException e) {
			logger.error("Problem with provenance initialisation: ",e);
		} catch (SQLException e) {
			logger.error("Problem with provenance initialisation: ",e);
		}
	}



	/**
	 * @return the invocationContext
	 */
	public InvocationContext getInvocationContext() {
		return invocationContext;
	}

	/**
	 * @param invocationContext the invocationContext to set
	 */
	public void setInvocationContext(InvocationContext invocationContext) {
		this.invocationContext = invocationContext;
	}

	/**
	 * @return the referenceService
	 */
	public ReferenceService getReferenceService() {
		return referenceService;
	}

	/**
	 * @param referenceService the referenceService to set
	 */
	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}


	/**
	 * Uses a {@link ScheduledThreadPoolExecutor} to process events in a Thread
	 * safe manner
	 */
	public synchronized void addProvenanceItem(
			final ProvenanceItem provenanceItem) {

//		Runnable runnable = new Runnable() {
//
//			public void run() {
				try {

					getProvenance().acceptRawProvenanceEvent(
							provenanceItem.getEventType(), provenanceItem);

				} catch (SQLException e) {
					logger.warn("Could not add provenance for " + provenanceItem.getEventType() + " " + provenanceItem.getIdentifier(), e);
				} catch (IOException e) {
					logger.error("Could not add provenance for " + provenanceItem.getEventType() + " " + provenanceItem.getIdentifier(), e);
				} catch (RuntimeException e) {
					logger.error("Could not add provenance for " + provenanceItem.getEventType() + " " + provenanceItem.getIdentifier(), e);
				}
//
//			}
//		};
//		getExecutor().execute(runnable);

	}

	protected Connection getConnection() throws SQLException {
		return databaseManager.getConnection();
	}

	/**
	 * Used by database backed provenance stores. Ask the implementation to
	 * create the database. Requires each datbase type to create all its own
	 * tables
	 */
	public abstract void createDatabase();


	public void clearDatabase() { clearDatabase(true); }

	/**
	 * Clear all the values in the database but keep the db there
	 */
	public void clearDatabase(boolean isClearDB) {

		if (isClearDB) {
			logger.info("clearing DB");
			try {
				getWriter().clearDBStatic();

				Set<String> danglingDataRefs = getWriter().clearDBDynamic();

				logger.info("references collected during removeRun:");
				for (String s:danglingDataRefs) {
					logger.info(s);
				}

			} catch (SQLException e) {
				logger.error("Problem clearing database", e);
			}
		} else {
			logger.error("clearDB is FALSE: not clearing");
		}

//		String q = null;
//		Connection connection = null;

//		Statement stmt = null;
//		try {
//		connection = getConnection();
//		stmt = connection.createStatement();
//		} catch (SQLException e) {
//		logger.warn("Could not create database statement :" + e);
//		} catch (InstantiationException e) {
//		logger.warn("Could not create database statement :" + e);
//		} catch (IllegalAccessException e) {
//		logger.warn("Could not create database statement :" + e);
//		} catch (ClassNotFoundException e) {
//		logger.warn("Could not create database statement :" + e);
//		}

//		q = "DELETE FROM Workflow";
//		try {
//		stmt.executeUpdate(q);
//		} catch (SQLException e) {
//		logger.warn("Could not execute statement " + q + " :" + e);
//		}

//		q = "DELETE FROM Processor";
//		try {
//		stmt.executeUpdate(q);
//		} catch (SQLException e) {
//		logger.warn("Could not execute statement " + q + " :" + e);
//		}

//		q = "DELETE FROM Datalink";
//		try {
//		stmt.executeUpdate(q);
//		} catch (SQLException e) {
//		logger.warn("Could not execute statement " + q + " :" + e);
//		}

//		q = "DELETE FROM Port";
//		try {
//		stmt.executeUpdate(q);
//		} catch (SQLException e) {
//		logger.warn("Could not execute statement " + q + " :" + e);
//		}

//		q = "DELETE FROM WorkflowRun";
//		try {
//		stmt.executeUpdate(q);
//		} catch (SQLException e) {
//		logger.warn("Could not execute statement " + q + " :" + e);
//		}

//		q = "DELETE FROM ProcBinding";
//		try {
//		stmt.executeUpdate(q);
//		} catch (SQLException e) {
//		logger.warn("Could not execute statement " + q + " :" + e);
//		}

//		q = "DELETE FROM PortBinding";
//		try {
//		stmt.executeUpdate(q);
//		} catch (SQLException e) {
//		logger.warn("Could not execute statement " + q + " :" + e);
//		}

//		q = "DELETE FROM Collection";
//		try {
//		stmt.executeUpdate(q);
//		} catch (SQLException e) {
//		logger.warn("Could not execute statement " + q + " :" + e);
//		}


//		if (connection!=null) try {
//		connection.close();
//		} catch (SQLException ex) {
//		logger.error("Error closing connection",ex);
//		}
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


	public List<LineageQueryResultRecord> computeLineage(String workflowRun,
			String port, String proc, String path, Set<String> selectedProcessors) {
		return null;
	}

	public String getDataflowInstance(String dataflowId) {
		String workflowRunId = null;
		try {
			workflowRunId = (getProvenance()).getPq().getRuns(dataflowId, null).get(0).getWorkflowRunId();
		} catch (SQLException e) {
			logger.error("Error finding the dataflow instance", e);
		}
		return workflowRunId;
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

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public synchronized ExecutorService getExecutor() {
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

	/**
	 * @return the writer
	 */
	public ProvenanceWriter getWriter() {
		return writer;
	}

	/**
	 * @param writer the writer to set
	 */
	protected void setWriter(ProvenanceWriter writer) {
		this.writer = writer;
	}

	/**
	 * @return the query
	 */
	public ProvenanceQuery getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	protected void setQuery(ProvenanceQuery query) {
		this.query = query;
	}

	/**
	 * @return the wfdp
	 */
	public WorkflowDataProcessor getWfdp() {
		return wfdp;
	}

	/**
	 * @param wfdp the wfdp to set
	 */
	public void setWfdp(WorkflowDataProcessor wfdp) {
		this.wfdp = wfdp;
	}

	/**
	 * @return the eventProcessor
	 */
	public EventProcessor getEventProcessor() {
		return eventProcessor;
	}

	/**
	 * @param eventProcessor the eventProcessor to set
	 */
	public void setEventProcessor(EventProcessor eventProcessor) {
		this.eventProcessor = eventProcessor;
	}
}
