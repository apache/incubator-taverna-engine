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
import java.sql.Statement;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.item.WorkflowProvenanceItem;
import net.sf.taverna.t2.provenance.lineageservice.Dependencies;
import net.sf.taverna.t2.provenance.lineageservice.EventProcessor;
import net.sf.taverna.t2.provenance.lineageservice.LineageQueryResultRecord;
import net.sf.taverna.t2.provenance.lineageservice.Provenance;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceAnalysis;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceQuery;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceWriter;
import net.sf.taverna.t2.provenance.reporter.ProvenanceReporter;
import net.sf.taverna.t2.reference.ReferenceService;

import org.apache.log4j.Logger;

/**
 * Collects {@link ProvenanceItem}s as it travels up and down the dispatch stack
 * inside the InvocationContext
 * 
 * @author Ian Dunlop
 * @author Stuart Owen
 * 
 */
public abstract class ProvenanceConnector implements ProvenanceReporter {

    private static Logger logger = Logger.getLogger(ProvenanceConnector.class);
    private String saveEvents;    
    private ProvenanceAnalysis provenanceAnalysis;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isClearDB = false;
    private Provenance provenance;
    private boolean finished = false;
    private String sessionID;
	private InvocationContext invocationContext;
	private ReferenceService referenceService;

    public ProvenanceConnector() {
        
    }

    public ProvenanceConnector(Provenance provenance,
            ProvenanceAnalysis provenanceAnalysis,
            boolean isClearDB, String saveEvents) {

        setProvenance(provenance);
        this.setProvenanceAnalysis(provenanceAnalysis);
        this.isClearDB = isClearDB;
        this.saveEvents = saveEvents;
        getProvenance().setSaveEvents(this.saveEvents);
    }
    
   /**
   * Uses a {@link ScheduledThreadPoolExecutor} to process events in a Thread
   * safe manner
   */
  public synchronized void addProvenanceItem(
          final ProvenanceItem provenanceItem) {

      Runnable runnable = new Runnable() {

          public void run() {
              try {

                  getProvenance().acceptRawProvenanceEvent(
                          provenanceItem.getEventType(), provenanceItem);

              } catch (SQLException e) {
                  logger.warn("Could not add provenance for " + provenanceItem.getEventType() + " " + provenanceItem.getIdentifier() + " " + e);
              } catch (IOException e) {
                  logger.warn("Could not add provenance for " + provenanceItem.getEventType() + " " + provenanceItem.getIdentifier() + " " + e);
              }

          }
      };
      getExecutor().execute(runnable);

  }

    protected Connection getConnection() throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, SQLException {
        return JDBCConnector.getConnection();
    }

    /**
     * Used by database backed provenance stores. Ask the implementation to
     * create the database. Requires each datbase type to create all its own
     * tables
     */
    public abstract void createDatabase();    

    /**
     * Clear all the values in the database but keep the db there
     */
    public void clearDatabase() {
        String q = null;
        Connection connection = null;

        Statement stmt = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
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

        if (connection!=null) try {
            connection.close();
        } catch (SQLException ex) {
            logger.error("Error closing connection",ex);
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

        FutureTask<Dependencies> future = new FutureTask<Dependencies>(
                new Callable<Dependencies>() {

                    public Dependencies call() throws Exception {
                        try {

                            Dependencies runLineageQuery = getProvenanceAnalysis().fetchIntermediateResult(wfInstance, pname,
                                    vname, iteration);

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
            instanceID = (getProvenance()).getPq().getWFInstanceID(dataflowId).get(0).getInstanceID();
        } catch (SQLException e) {
            logger.error("Error finding the dataflow instance", e);
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
    
	public InvocationContext getInvocationContext() {
		return invocationContext;
	}

	public ReferenceService getReferenceService() {
		return referenceService;
	}

	public void setInvocationContext(InvocationContext invocationContext) {
		this.invocationContext = invocationContext;
		
	}

	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
		
	}
}
