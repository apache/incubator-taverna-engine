/**
 * 
 */
package net.sf.taverna.t2.provenance.lineageservice.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tupeloproject.kernel.Context;
import org.tupeloproject.kernel.OperatorException;
import org.tupeloproject.kernel.UnionContext;
import org.tupeloproject.kernel.impl.MemoryContext;
import org.tupeloproject.kernel.impl.ResourceContext;
import org.tupeloproject.provenance.ProvenanceAccount;
import org.tupeloproject.provenance.ProvenanceArtifact;
import org.tupeloproject.provenance.ProvenanceGeneratedArc;
import org.tupeloproject.provenance.ProvenanceProcess;
import org.tupeloproject.provenance.ProvenanceRole;
import org.tupeloproject.provenance.ProvenanceUsedArc;
import org.tupeloproject.provenance.impl.ProvenanceContextFacade;
import org.tupeloproject.rdf.Literal;
import org.tupeloproject.rdf.Resource;
import org.tupeloproject.rdf.Triple;
import org.tupeloproject.rdf.xml.RdfXmlWriter;

/**
 * @author paolo
 *
 */
public class OPMManager {

	private static Logger logger = Logger.getLogger(OPMManager.class);

	private static final String OPM_TAVERNA_NAMESPACE = "http://taverna.opm.org/";
	private static final String OPM_GRAPH_FILE = "src/test/resources/provenance-testing/OPM/OPMGraph.rdf";
	private static final String VALUE_PROP = "value";

	ProvenanceContextFacade graph = null;
	Context context = null;

	ProvenanceAccount currentAccount = null;
	ProvenanceArtifact currentArtifact = null;
	ProvenanceRole     currentRole = null;
	ProvenanceProcess currentProcess = null;
	
	public OPMManager() {

		// init Tupelo RDF provenance graph
		MemoryContext mc = new MemoryContext();
		ResourceContext rc = new ResourceContext("http://example.org/data/","/provenanceExample/");
		context = new UnionContext();
		context.addChild(mc);
		context.addChild(rc);

		graph = new ProvenanceContextFacade(mc);
	}

	
	/**
	 * default implementation of tis method returns null -- has no idea how to extract simple values from incoming artifact values 
	 * @return
	 */
	public List<DataValueExtractor> getDataValueExtractor() { return null; }
	
	
	/**
	 * 	create new account to hold the causality graph
	 *  and give it a Resource name
	 * @param accountName
	 */
	public void createAccount(String accountName) {

		currentAccount = graph.newAccount("OPM-"+
				accountName, Resource.uriRef(OPM_TAVERNA_NAMESPACE+accountName));
		graph.assertAccount(currentAccount);
	}


	/**
	 * 
	 * @param aName
	 * @param aValue  actual value can be used optionally as part of a separate triple. Whether this is used or not 
	 * depends on the settings, see {@link OPMManager.addValueTriple}
	 */
	public void addArtifact(String aName, String aValue) {

		Resource r = Resource.uriRef(aName);
		currentArtifact = graph.newArtifact(aName, r);
		graph.assertArtifact(currentArtifact);

		if (aValue != null) {
//			System.out.println("OPMManager::addArtifact: aValue is NOT NULL");
			
			// if we have a valid DataValueExtractor, use it here
			List<DataValueExtractor> dveList;
			String extractedValue = aValue;  // default is same value
			if ((dveList = getDataValueExtractor()) != null) {

				// try all available extractors... UGLY but data comes with NO TYPE at all!
				for (DataValueExtractor dve: dveList) {
					try {
						
//						System.out.println("OPMManager::addArtifact: trying extractor "+dve.getClass().getName());
						extractedValue = dve.extractString(aValue);						
//						System.out.println("OPMManager::addArtifact: - extracted value = "+extractedValue);
						break; // extractor worked
					} catch (Exception e) {
						// no panic, reset value and try another extractor
//						System.out.println("OPMManager::addArtifact: extractor failed");
						extractedValue = aValue;
					}
				}
			}
			
//			System.out.println("OPMManager::addArtifact: using value "+extractedValue);
			try {
				Literal lValue = Resource.literal(extractedValue);
				context.addTriple(r, Resource.uriRef(OPM_TAVERNA_NAMESPACE+VALUE_PROP), lValue);
			} catch (OperatorException e) {
				logger.warn("OPM iteration triple creation exception: "+e.getMessage());
			}
		}  else {
//			System.out.println("OPMManager::addArtifact: aValue for ["+aName+"] is NULL");
		}
	}


	/**
	 * no actual value is recorded
	 * @param aName
	 */
	public void addArtifact(String aName) {

		Resource r = Resource.uriRef(aName);
		currentArtifact = graph.newArtifact(aName, r);
		graph.assertArtifact(currentArtifact);		
	}



	public void createRole(String aRole) {

		Resource r = Resource.uriRef(OPM_TAVERNA_NAMESPACE+aRole);		
		currentRole = graph.newRole(aRole, r);
	}


	public void addProcess(String proc, String iterationVector, String URIfriendlyIterationVector) {

		String processID;
		if (URIfriendlyIterationVector.length()>0) {
			processID = OPM_TAVERNA_NAMESPACE+proc+"?it="+URIfriendlyIterationVector;
		} else
			processID = OPM_TAVERNA_NAMESPACE+proc;

		Resource processResource = Resource.uriRef(processID);					
		currentProcess = graph.newProcess(processID, processResource);
		graph.assertProcess(currentProcess );

		// add a triple to specify the iteration vector for this occurrence of Process, if it is available
		if (URIfriendlyIterationVector.length() > 0) {
//			Resource inputProcessSubject = ((RdfProvenanceProcess) process).getSubject();
			try {
				context.addTriple(processResource, Resource.uriRef(OPM_TAVERNA_NAMESPACE+"iteration"), iterationVector);
			} catch (OperatorException e) {
				logger.warn("OPM iteration triple creation exception: "+e.getMessage());
			}
		}
	}


	public void assertGeneratedBy(ProvenanceArtifact artifact, 
			ProvenanceProcess process, 
			ProvenanceRole role, 
			ProvenanceAccount account,
			boolean noDuplicates) {

		boolean found = false;
		if (noDuplicates) {
			Collection<ProvenanceGeneratedArc> generatedBy = graph.getGeneratedBy(artifact);

			for (ProvenanceGeneratedArc arc:generatedBy) {						
				ProvenanceProcess pp = arc.getProcess();
				if (pp.getName().equals(process.getName())) { found = true; break; }						
			}
		}

		if (!noDuplicates || (noDuplicates && !found) )
			graph.assertGeneratedBy(artifact, process, role, account);
	}



	public void assertUsed(ProvenanceArtifact artifact,			
			ProvenanceProcess process, 
			ProvenanceRole role,
			ProvenanceAccount account, 
			boolean noDuplicates) {

		boolean found = false;

		if (noDuplicates) {
			Collection<ProvenanceUsedArc> used = graph.getUsed(process);

			for (ProvenanceUsedArc arc:used) {						
				ProvenanceArtifact pa = arc.getArtifact();
				if (pa.getName().equals(artifact.getName())) { found = true; break; }						
			}
		}

		if (!noDuplicates || (noDuplicates && !found) )
			graph.assertUsed(process, artifact, role, account);
	}


	public ProvenanceContextFacade getGraph() {
		return graph;
	}

	/**
	 * @return the account
	 */
	public ProvenanceAccount getAccount() {
		return currentAccount;
	}


	/**
	 * @param account the account to set
	 */
	public void setAccount(ProvenanceAccount account) {
		this.currentAccount = account;
	}

	/**
	 * @return the currentRole
	 */
	public ProvenanceRole getCurrentRole() {
		return currentRole;
	}

	/**
	 * @param currentRole the currentRole to set
	 */
	public void setCurrentRole(ProvenanceRole currentRole) {
		this.currentRole = currentRole;
	}

	/**
	 * @return the currentArtifact
	 */
	public ProvenanceArtifact getCurrentArtifact() {
		return currentArtifact;
	}

	/**
	 * @param currentArtifact the currentArtifact to set
	 */
	public void setCurrentArtifact(ProvenanceArtifact currentArtifact) {
		this.currentArtifact = currentArtifact;
	}

	/**
	 * @return the currentAccount
	 */
	public ProvenanceAccount getCurrentAccount() {
		return currentAccount;
	}

	/**
	 * @param currentAccount the currentAccount to set
	 */
	public void setCurrentAccount(ProvenanceAccount currentAccount) {
		this.currentAccount = currentAccount;
	}

	/**
	 * @return the currentProcess
	 */
	public ProvenanceProcess getCurrentProcess() {
		return currentProcess;
	}

	/**
	 * @param currentProcess the currentProcess to set
	 */
	public void setCurrentProcess(ProvenanceProcess currentProcess) {
		this.currentProcess = currentProcess;
	}

	public void writeGraph() {

		// print out OPM graph for diagnostics
		try {
			Set<Triple> allTriples = context.getTriples();

			RdfXmlWriter writer = new RdfXmlWriter();				
			writer.write(allTriples, new FileWriter(OPM_GRAPH_FILE));

			logger.info("OPM graph written to "+OPM_GRAPH_FILE);

		} catch (OperatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


}
