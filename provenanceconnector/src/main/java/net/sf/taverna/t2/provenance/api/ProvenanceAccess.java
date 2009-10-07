/**
 * 
 */
package net.sf.taverna.t2.provenance.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import net.sf.taverna.platform.spring.RavenAwareClassPathXmlApplicationContext;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.provenance.ProvenanceConnectorRegistry;
import net.sf.taverna.t2.provenance.connector.ProvenanceConnector;
import net.sf.taverna.t2.provenance.lineageservice.Dependencies;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceAnalysis;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceQuery;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.provenance.lineageservice.utils.Var;
import net.sf.taverna.t2.provenance.lineageservice.utils.Workflow;
import net.sf.taverna.t2.provenance.lineageservice.utils.WorkflowInstance;

import net.sf.taverna.t2.provenance.reporter.ProvenanceReporter;
import net.sf.taverna.t2.reference.ReferenceService;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * @author paolo
 * @author Stuart Owen
 *
 */
public class ProvenanceAccess {

    
    private static Logger logger = Logger.getLogger(ProvenanceAccess.class);
    ProvenanceConnector provenanceConnector = null;
    ProvenanceAnalysis pa = null;
    ProvenanceQuery pq;
    Query q = null;
    private String connectorType;

    public ProvenanceAccess(String connectorType) {
        this.connectorType = connectorType;
        init();
    }

    
    /**
     * Initialises a named JNDI DataSource if not already set up externally.
     * The DataSource is named jdbc/taverna
     */
    public static void initDataSource(String driverClassName, String jdbcUrl, String username, String password, int minIdle, int maxIdle, int maxActive) {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.osjava.sj.memory.MemoryContextFactory");
            System.setProperty("org.osjava.sj.jndi.shared", "true");
            
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        ds.setMaxActive(maxActive);
        ds.setMinIdle(minIdle);
        ds.setMaxIdle(maxIdle);
        ds.setDefaultAutoCommit(true);
        if (username != null) {
            ds.setUsername(username);
        }
        if (password != null) {
            ds.setPassword(password);
        }

        ds.setUrl(jdbcUrl);

        InitialContext context;
        try {
            context = new InitialContext();
            context.rebind("jdbc/taverna", ds);
        } catch (NamingException ex) {
            java.util.logging.Logger.getLogger(ProvenanceAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialises a default Reference Service for storing data and their associated references.
     * This creates a reference service using the named JNDI Data Source 'jdbc/taverna'.
     * 
     */
    public void initDefaultReferenceService() {
       initReferenceService("hibernateReferenceServiceContext.xml");
    }

    /**
     * Initialises the Reference Service for a given hibernate context definition.
     * This mapping file must be available in the root of the classpath.
     *
     * @param hibernateContext
     */
    public void initReferenceService(String hibernateContext) {
        ApplicationContext appContext = new RavenAwareClassPathXmlApplicationContext(hibernateContext);

		final ReferenceService referenceService = (ReferenceService) appContext
		.getBean("t2reference.service.referenceService");

		InvocationContext context =  new InvocationContext() {

			public ReferenceService getReferenceService() {
				return referenceService;
			}

			public ProvenanceReporter getProvenanceReporter() {
				return provenanceConnector;
			}

			public <T> List<? extends T> getEntities(Class<T> entityType) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		provenanceConnector.setReferenceService(context.getReferenceService()); // CHECK context.getReferenceService());
		provenanceConnector.setInvocationContext(context);

    }

    public static void initDataSource(String driverClassName,String jdbcUrl) {
        initDataSource(driverClassName,jdbcUrl,null,null,10,50,50);
    }

    public void init() {

        for (ProvenanceConnector connector : ProvenanceConnectorRegistry.getInstance().getInstances()) {
            if (connectorType.equalsIgnoreCase(connector.getName())) {
                provenanceConnector = connector;
            }
        }
        logger.info("Provenance being captured using: " + provenanceConnector);

        //slight change, the init is outside but it also means that the init call has to ensure that the dbURL
        //is set correctly
        provenanceConnector.init();

        pa = provenanceConnector.getProvenanceAnalysis();
        pq = provenanceConnector.getQuery();
    }


/////////
//	/  main provenance query methods



	/**
	 * Main provenance query. Doc to be supplied. Needs to be extended to multiple runs.
	 * @throws SQLException
	 */
	public QueryAnswer executeQuery (Query pq) throws SQLException {
		return pa.lineageQuery(pq.getTargetVars(), pq.getRunID(), pq.getSelectedProcessors());
	}


	/**
	 * @param wfInstance lineage scope -- a specific instance
	 * @param pname for a specific processor [required]
	 * @param a specific (input or output) variable [optional]
	 * @param iteration and a specific iteration [optional]
	 * @return a lineage query ready to be executed, or null if we cannot return an answer because we are not ready
	 * (for instance the DB is not yet populated)
	 * @throws SQLException
	 */

	// implement using fetchIntermediateResults
	public Dependencies fetchPortData(
			String wfInstance,
			String workflowId,
			String pname,
			String port,
			String iteration) {

		logger.info("running fetchPortData on instance "+wfInstance+" workflow "+workflowId+
			     " processor "+pname+" port "+port+" iteration "+iteration);
		// TODO add context workflowID to query
		try {
			return pa.fetchIntermediateResult(wfInstance, pname, port, iteration);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}




//	/ manage instances


	/**
	 * allowable conditions to be determined and documented here.

	 * @param workflowId defines the scope of the query - if null then the query runs on all available workflows
	 * @param conditions additional conditions to be defined. They are currently ignored
	 * @return a list of wfInstanceID, each representing one run of the same workflow
	 */
	public List<WorkflowInstance> listRuns(String workflowId, Map<String, String> conditions) {

		try {
			return pq.getWFInstanceID(workflowId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * implement using clearDynamic() method or a variation. Collect references and forward
	 * them for deletion by the DataManager
	 * @param runID
	 */
	public void removeRun(String runID) {
		return; // TODO
	}


	/**
	 * returns a set of workflowIDs for a given runID. The set is a singleton if the workflow has no nesting,
	 * but in general it contains one workflowID for each nested workflow involved in the run
	 * @param workflowInstanceID
	 * @return
	 */
	public List<String> getWorkflowID(String runID) {

		try {
			return pq.getWfNames(runID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public String getTopLevelWorkflowID(String runID) {

		try {
			return pq.getTopLevelWfName(runID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}



	public List<WorkflowInstance> getAllWorkflowIDs() {
		try {
			return pq.getWFInstanceID(null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

//	/ access static workflow structure


	/**
	 * given a processor name, returns all workflow IDs that contain a processor by that name.
	 * This simply calls the underlying method with the same signature
	 */
	public List<Workflow> getContainingWorkflowsForProcessor(String pname) {
		return pq.getContainingWorkflowsForProcessor(pname);
	}



	/**
	 *
	 * @param workflowID
	 * @return a map workflowID -> list(ProvenanceProcessor).
	 * Each entry is for one composing sub-workflow (if no nesting then this contains only one workflow, namely the top level one)
	 */
	public Map<String, List<ProvenanceProcessor>> getProcessorsInWorkflow(String workflowID) {
		return pq.getProcessorsDeep(null, workflowID);
	}


	/**
	 * list all ports for a processor
	 * @param workflowID
	 * @param processorName
	 * @return
	 */
	public List<Var> getPortsForDataflow(String dataflowID) {

		Workflow w = pq.getWorkflow(dataflowID);

		Map<String, String> queryConstraints = new HashMap<String, String>();
		queryConstraints.put("wfInstanceRef", dataflowID);
		queryConstraints.put("pnameRef", w.getExternalName());

		try {
			return pq.getVars(queryConstraints);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * list all ports for a processor
	 * @param workflowID
	 * @param processorName
	 * @return
	 */
	public List<Var> getPortsForProcessor(String workflowID, String processorName) {

		Map<String, String> queryConstraints = new HashMap<String, String>();
		queryConstraints.put("wfInstanceRef", workflowID);
		queryConstraints.put("pnameRef", processorName);

		try {
			return pq.getVars(queryConstraints);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public List<ProvenanceProcessor> getSuccessors(String workflowID, String processorName, String portName) {
		return null; // TODO
	}

	public List<String>   getActivities(String workflowID, String processorName) {
		return null; // TODO
	}




//	/ configure provenance query functionality



	/**
	 * include valus of output ports in the query result? input port values are always included<br>
	 * default is FALSE
	 */
	public void toggleIncludeProcessorOutputs(boolean active) {
		return; // TODO
	}


	public boolean isIncludeProcessorOutputs() {
		return false; // TODO
	}

	/**
	 * include actual values that appear on ports, rather than just the references<br>
	 * default is FALSE
	 * @param active
	 */
	public void toggleIncludeDataValues(boolean active) {
		return; // TODO
	}

	public boolean isIncludeDataValues() {
		return false; // TODO
	}



//	/ OPM management


	/**
	 * should an OPM graph be generated in response to a query?<br>
	 * default is TRUE
	 */
	public void toggleOPMGeneration(boolean active) {
		return; // TODO
	}

	public boolean isOPMGenerationActive() {
		return false; // TODO
	}

	/**
	 * should actual artifact values be attached to OPM artifact nodes?<br>
	 * default is FALSE
	 * @param active
	 */
	public  void toggleAttachOPMArtifactValues(boolean active) {
		return; // TODO
	}


	public  boolean isAttachOPMArtifactValues() {
		return false; // TODO
	}


	/**
	 * @return the provenanceConnector
	 */
	public ProvenanceConnector getProvenanceConnector() {
		return provenanceConnector;
	}


	/**
	 * @param provenanceConnector the provenanceConnector to set
	 */
	public void setProvenanceConnector(ProvenanceConnector provenanceConnector) {
		this.provenanceConnector = provenanceConnector;
	}


	/**
	 * @return the pa
	 */
	public ProvenanceAnalysis getPa() {
		return pa;
	}


	/**
	 * @param pa the pa to set
	 */
	public void setPa(ProvenanceAnalysis pa) {
		this.pa = pa;
	}


	/**
	 * @return the pq
	 */
	public ProvenanceQuery getPq() {
		return pq;
	}


	/**
	 * @param pq the pq to set
	 */
	public void setPq(ProvenanceQuery pq) {
		this.pq = pq;
	}

}