/**
 * 
 */
package net.sf.taverna.t2.provenance.api.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;

import net.sf.taverna.t2.provenance.api.NativeAnswer;
import net.sf.taverna.t2.provenance.api.ProvenanceAccess;
import net.sf.taverna.t2.provenance.api.ProvenanceConnectorType;
import net.sf.taverna.t2.provenance.api.ProvenanceQueryParser;
import net.sf.taverna.t2.provenance.api.Query;
import net.sf.taverna.t2.provenance.api.QueryAnswer;
import net.sf.taverna.t2.provenance.lineageservice.Dependencies;
import net.sf.taverna.t2.provenance.lineageservice.LineageQueryResultRecord;
import net.sf.taverna.t2.provenance.lineageservice.utils.QueryVar;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

/**
 * @author paolo
 *
 */
public class ProvenanceAPISampleClient {

	private static final String DEFAULT_OPM_FILENAME = "src/test/resources/provenance-testing/OPMGraph.rdf";
	
	ProvenanceAccess pAccess = null;

	String DB_URL_LOCAL = PropertiesReader.getString("dbhost");  // URL of database server //$NON-NLS-1$
	String DB_USER = PropertiesReader.getString("dbuser");                        // database user id //$NON-NLS-1$
	String DB_PASSWD = PropertiesReader.getString("dbpassword"); //$NON-NLS-1$
	static String OPMGraphFilename = null;
	
	List<String> wfNames = null;
	Set<String> selectedProcessors = null;

	private static Logger logger = Logger.getLogger(ProvenanceAPISampleClient.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ProvenanceAPISampleClient client = new ProvenanceAPISampleClient();

		client.setUp();
		QueryAnswer answer = client.queryProvenance();
		
		client.reportAnswer(answer);
		client.saveOPMGraph(answer, OPMGraphFilename);
	}

	
	
	public void setUp() throws Exception {
		setDataSource();
		System.setProperty("raven.eclipse","true");
		pAccess = new ProvenanceAccess(ProvenanceConnectorType.MYSQL);  // creates and initializes the provenance API
		configureInterface();              // sets user-defined preferences
	}

	public void setDataSource() {

		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.osjava.sj.memory.MemoryContextFactory");
		System.setProperty("org.osjava.sj.jndi.shared", "true");

		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
		ds.setMaxActive(50);
		ds.setMinIdle(10);
		ds.setMaxIdle(50);
		ds.setDefaultAutoCommit(true);
		ds.setUsername(DB_USER);
		ds.setPassword(DB_PASSWD);

		try {
			ds.setUrl("jdbc:mysql://"+DB_URL_LOCAL+"/T2Provenance");

			InitialContext context = new InitialContext();
			context.rebind("jdbc/taverna", ds);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * set user-defined values for toggles on the API
	 */
	private void configureInterface() {

		// do we need to return output processor values in addition to inputs?
		String returnOutputsPref = PropertiesReader.getString("query.returnOutputs");
		if (returnOutputsPref != null) {
			pAccess.toggleIncludeProcessorOutputs(Boolean.parseBoolean(returnOutputsPref));	
		}

		// do we need to record actual values as part of the OPM graph?
		String recordArtifacValuesPref = PropertiesReader.getString("OPM.recordArtifactValues");
		if (recordArtifacValuesPref != null) {			
			pAccess.toggleAttachOPMArtifactValues(Boolean.parseBoolean(recordArtifacValuesPref));
			System.out.println("OPM.recordArtifactValues: "+ pAccess.isAttachOPMArtifactValues());
		}

		// are we recording the actual (de-referenced) values at all?!
		String includeDataValuePref = PropertiesReader.getString("query.returnDataValues");
		if (includeDataValuePref != null) {
			pAccess.toggleIncludeDataValues(Boolean.parseBoolean(includeDataValuePref));
			System.out.println("query.returnDataValues: "+pAccess.isIncludeDataValues());
		}

		String computeOPMGraph = PropertiesReader.getString("OPM.computeGraph");
		if (computeOPMGraph != null) {
			pAccess.toggleOPMGeneration(Boolean.parseBoolean(computeOPMGraph));
			System.out.println("OPM.computeGraph: "+pAccess.isOPMGenerationActive());			
		}
		
		// user-selected file name for OPM graph?
		OPMGraphFilename = PropertiesReader.getString("OPM.rdf.file");
		if (OPMGraphFilename == null) {
			OPMGraphFilename = DEFAULT_OPM_FILENAME;
			System.out.println("OPM.filename: "+OPMGraphFilename);			
		}
	}

	
	
	public QueryAnswer queryProvenance() {

		Query q = new Query();

		// get filename for XML query spec
		String querySpecFile = PropertiesReader.getString("query.file");
		logger.info("executing query "+querySpecFile);

		ProvenanceQueryParser pqp = new ProvenanceQueryParser();
		pqp.setPAccess(pAccess);

		q = pqp.parseProvenanceQuery(querySpecFile);
		
		if (q == null) {
			logger.fatal("query processing failed. So sorry.");
			return null;
		}
		logger.info("YOUR QUERY: "+q.toString());
		
		QueryAnswer answer=null;
		try {
			answer = pAccess.executeQuery (q);
		} catch (SQLException e) {
			logger.fatal("Exception while executing query: "+e.getMessage());
			return null;
		}
		return answer;
	}



	/**
	 * writes the RDF/XML OPM string to file
	 * @param opmFilename
	 */
	private void saveOPMGraph(QueryAnswer answer, String opmFilename) {
		
		if (answer.getOPMAnswer_AsRDF() == null) {
			logger.info("save OPM graph: OPM graph was NOT generated.");
			return;
		}
		
		try {
			FileWriter fw= new FileWriter(new File(opmFilename));
			fw.write(answer.getOPMAnswer_AsRDF());
			fw.close();
		} catch (IOException e) {
			logger.warn("saveOPMGraph: error saving graph to file "+opmFilename);
			logger.warn(e.getMessage());
		}
		logger.info("OPM graph saved to "+opmFilename);
	}



	private void reportAnswer(QueryAnswer answer) {

		NativeAnswer nAnswer = answer.getNativeAnswer();

		// nAnswer contains a Map of the form 
		// 	Map<QueryVar, Map<String, List<Dependencies>>>  answer;

		Map<QueryVar, Map<String, List<Dependencies>>>  dependenciesByVar = nAnswer.getAnswer();	
		for (QueryVar v:dependenciesByVar.keySet()) {
			logger.info("dependencies for port: "+v.getPname()+":"+v.getVname()+":"+v.getPath());

			Map<String, List<Dependencies>> deps = dependenciesByVar.get(v);
			for (String path:deps.keySet()) {
				logger.info("dependencies on path "+path);
				for (Dependencies dep:deps.get(path)) {

					for (LineageQueryResultRecord record: dep.getRecords()) {
						record.setPrintResolvedValue(false);
						logger.info(record.toString());
					}
				}
			}
		}		
	}


}
