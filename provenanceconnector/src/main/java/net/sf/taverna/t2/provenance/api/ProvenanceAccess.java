/**
 * 
 */
package net.sf.taverna.t2.provenance.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.provenance.ProvenanceConnectorRegistry;
import net.sf.taverna.t2.provenance.connector.ProvenanceConnector;
import net.sf.taverna.t2.provenance.lineageservice.Dependencies;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceAnalysis;
import net.sf.taverna.t2.provenance.lineageservice.ProvenanceQuery;
import net.sf.taverna.t2.provenance.lineageservice.utils.ProvenanceProcessor;
import net.sf.taverna.t2.provenance.lineageservice.utils.QueryVar;
import net.sf.taverna.t2.provenance.lineageservice.utils.Var;
import net.sf.taverna.t2.workbench.provenance.ProvenanceConfiguration;

import org.apache.log4j.Logger;

/**
 * @author paolo
 *
 */
public  class ProvenanceAccess {

	private static Logger logger = Logger.getLogger(ProvenanceAccess.class);

	ProvenanceAnalysis pa = null;
	ProvenanceQuery pq;
	Query q = null;	
	
	
	public ProvenanceAccess() {
		init();
	}

	
	public void init() {

		ProvenanceConnector provenanceConnector = null;
		if (ProvenanceConfiguration.getInstance().getProperty("enabled").equalsIgnoreCase("yes")) {
			String connectorType = ProvenanceConfiguration.getInstance().getProperty("connector");

			for (ProvenanceConnector connector:ProvenanceConnectorRegistry.getInstance().getInstances()) {
				if (connectorType.equalsIgnoreCase(connector.getName())) {
					provenanceConnector = connector;
				}
			}
			logger.info("Provenance being captured using: " + 
					provenanceConnector);
			
			//slight change, the init is outside but it also means that the init call has to ensure that the dbURL
			//is set correctly
			provenanceConnector.init();
			
			pa = provenanceConnector.getProvenanceAnalysis();
			pq = pa.getPq();
		}
		
	}
	
	/**
	 * TODO insert source selection code here
	 * @param configFile
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void init(String configFile) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		ReadProvenanceConfiguration config = new ReadProvenanceConfiguration(configFile);
		
	}


/////////
///  main provenance query methods
////////


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
			String pname,
			String vname,
			String iteration) {
		return null; // TODO
	}




//	/ manage instances


	/**
	 * allowable conditions to be determined and documented here
	
	 * @param workflowId defines the scope of the query - if null then the query runs on all available workflows
	 * @param conditions additional conditions to be defined
	 * @return a list of wfInstanceID, each representing one run 
	 */
	public List<String> listRuns(String workflowId, Map<String, String> conditions) {
		return null; // TODO
	}

	/**
	 * implement using clearDynamic() method or a variation. Collect references and forward
	 * them for deletion by the DataManager 
	 * @param runID
	 */
	public void removeRun(String runID) { 
		return; // TODO
	}

	public String getWorkflowID(String workflowInstanceID) {
		return null; // TODO
	}




//	/ access static workflow structure 



	/**
	 * 
	 * @param workflowID
	 * @return a map workflowID -> list(ProvenanceProcessor).
	 * Each entry is for one composing sub-workflow (if no nesting then this contains only one workflow, namely the top level one) 
	 */
	public Map<String, List<ProvenanceProcessor>> getProcessorsInWorkflow(String workflowID) {
		return null; // TODO
	}


	/**
	 * list all ports for a processor
	 * @param workflowID
	 * @param processorName
	 * @return
	 */
	public List<Var> getPortsForProcessor(String workflowID, String processorName) {
		return null; // TODO
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






}
