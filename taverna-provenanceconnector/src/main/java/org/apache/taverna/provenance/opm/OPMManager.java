/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.provenance.opm;

import static org.tupeloproject.rdf.Resource.literal;
import static org.tupeloproject.rdf.Resource.uriRef;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.taverna.provenance.lineageservice.URIGenerator;
import org.apache.taverna.provenance.lineageservice.utils.DataValueExtractor;

import org.apache.log4j.Logger;
import org.tupeloproject.kernel.Context;
import org.tupeloproject.kernel.OperatorException;
import org.tupeloproject.kernel.UnionContext;
import org.tupeloproject.kernel.impl.MemoryContext;
import org.tupeloproject.kernel.impl.ResourceContext;
import org.tupeloproject.provenance.ProvenanceAccount;
import org.tupeloproject.provenance.ProvenanceArtifact;
import org.tupeloproject.provenance.ProvenanceException;
import org.tupeloproject.provenance.ProvenanceGeneratedArc;
import org.tupeloproject.provenance.ProvenanceProcess;
import org.tupeloproject.provenance.ProvenanceRole;
import org.tupeloproject.provenance.ProvenanceUsedArc;
import org.tupeloproject.provenance.impl.ProvenanceContextFacade;
import org.tupeloproject.rdf.Literal;
import org.tupeloproject.rdf.Resource;
import org.tupeloproject.rdf.xml.RdfXmlWriter;


/**
 * @author paolo
 *
 */
public class OPMManager {
	private static Logger logger = Logger.getLogger(OPMManager.class);

	public static final String OPM_TAVERNA_NAMESPACE = "http://ns.taverna.org.uk/2011/provenance/opm/";
	private static final String VALUE_PROP = "value";

	private ProvenanceContextFacade graph = null;
	private Context context = null;

	private ProvenanceAccount currentAccount = null;
	private ProvenanceArtifact currentArtifact = null;
	private ProvenanceRole currentRole = null;
	private ProvenanceProcess currentProcess = null;

	private boolean isActive = true;

	public OPMManager() {
		// init Tupelo RDF provenance graph
		MemoryContext mc = new MemoryContext();
		ResourceContext rc = new ResourceContext("http://example.org/data/",
				"/provenanceExample/");
		context = new UnionContext();
		context.addChild(mc);
		context.addChild(rc);

		graph = new ProvenanceContextFacade(mc);
	}

	/**
	 * default implementation of this method returns null -- has no idea how to
	 * extract simple values from incoming artifact values
	 *
	 * @return
	 */
	public List<DataValueExtractor> getDataValueExtractor() {
		return null;
	}

	/**
	 * create new account to hold the causality graph and give it a Resource
	 * name
	 *
	 * @param accountName
	 * @throws ProvenanceException
	 */
	public void createAccount(String accountName) throws ProvenanceException {
		currentAccount = graph.newAccount("OPM-" + accountName,
				uriRef(uriGenerator.makeRunUri(accountName)));
		graph.assertAccount(currentAccount);
	}

	/**
	 * @param aName
	 * @param aValue
	 *            actual value can be used optionally as part of a separate
	 *            triple. Whether this is used or not depends on the settings,
	 *            see {@link OPMManager.addValueTriple}. This also sets the
	 *            currentArtifact to the newly created artifact
	 * @throws ProvenanceException
	 */
	public void addArtifact(String aName, Object aValue)
			throws ProvenanceException {
		Resource r = addArtifact(aName);

		if (aValue == null) {
			logger.debug("OPMManager::addArtifact: aValue for [" + aName
					+ "] is NULL");
			return;
		}

		logger.debug("OPMManager::addArtifact: aValue is NOT NULL");

		// if we have a valid DataValueExtractor, use it here
		List<DataValueExtractor> dveList;
		String extractedValue = (String) aValue;  // default is same value
		dveList = getDataValueExtractor();
		if (dveList != null)
			// try all available extractors... UGLY but data comes with NO TYPE at all!
			for (DataValueExtractor dve : dveList)
				try {
					logger.debug("OPMManager::addArtifact: trying extractor "
							+ dve.getClass().getName());
					extractedValue = dve.extractString(aValue);
					logger.debug("OPMManager::addArtifact: - extracted value = "
							+ extractedValue);
					break; // extractor worked
				} catch (Exception e) {
					// no panic, reset value and try another extractor
					logger.warn("OPMManager::addArtifact: extractor failed");
					extractedValue = (String) aValue;
				}

		logger.debug("OPMManager::addArtifact: using value " + extractedValue);
		try {
			Literal lValue = literal(extractedValue);
			context.addTriple(r, uriRef(OPM_TAVERNA_NAMESPACE + VALUE_PROP),
					lValue);
		} catch (OperatorException e) {
			logger.warn("OPM iteration triple creation exception", e);
		}
	}

	/**
	 * no actual value is recorded
	 *
	 * @param aName
	 * @return
	 * @throws ProvenanceException
	 */
	public Resource addArtifact(String aName) throws ProvenanceException {
		String artID = null;
		// make sure artifact name is a good URI
		try {
			URI artURI = new URI(aName);
			if (artURI.getScheme() != null) {
				if (artURI.getScheme().equals("t2"))
					artID = uriGenerator.makeT2ReferenceURI(aName);
				else
					artID = aName;
			}
		} catch (URISyntaxException e1) {
			// generate later
		}
		if (artID == null)
			artID = OPM_TAVERNA_NAMESPACE + "artifact/"
					+ uriGenerator.escape(aName);

		Resource r = uriRef(artID);
		currentArtifact = graph.newArtifact(artID, r);
		graph.assertArtifact(currentArtifact);
		return r;
	}

	public void createRole(String workflowRunId, String workflowId,
			String processorName, String iteration) {
		String aRole = uriGenerator.makeIteration(workflowRunId, workflowId,
				processorName, iteration);
		Resource r = Resource.uriRef(aRole);
		currentRole = graph.newRole(aRole, r);
	}

	private URIGenerator uriGenerator = new URIGenerator();

	public void addProcess(String processorName, String iterationVector,
			String workflowId, String workflowRunId) throws ProvenanceException {
		String processID;

		/*
		 * PM added 5/09 -- a process name may already be a URI -- this happens
		 * for example when we export back OPM after importing a workflow from
		 * our own OPM... in this case, do not pre-pend a new URI scheme
		 */
		try {
			URI procURI = new URI(processorName);

			if (procURI.getAuthority() == null) {
				processID = uriGenerator.makeProcessorURI(processorName, workflowId);
			} else {
				processID = processorName;
			}
		} catch (URISyntaxException e1) {
			processID = uriGenerator.makeProcessorURI(processorName, workflowId);
		}

		uriGenerator.makeIteration(workflowRunId, workflowId, processorName,
				iterationVector);

		Resource processResource = uriRef(processID);
		currentProcess = graph.newProcess(processID, processResource);
		graph.assertProcess(currentProcess);

		/*
		 * add a triple to specify the iteration vector for this occurrence of
		 * Process, if it is available
		 */
		try {
			if (! iterationVector.equals("[]"))
				// Resource inputProcessSubject = ((RdfProvenanceProcess) process).getSubject();
				context.addTriple(processResource, uriRef(OPM_TAVERNA_NAMESPACE
						+ "iteration"), iterationVector);
		} catch (OperatorException e) {
			logger.warn("OPM iteration triple creation exception", e);
		}
	}

	public void assertGeneratedBy(ProvenanceArtifact artifact,
			ProvenanceProcess process, ProvenanceRole role,
			ProvenanceAccount account, boolean noDuplicates)
			throws ProvenanceException {
		boolean found = false;
		if (noDuplicates && artifact != null)
			for (ProvenanceGeneratedArc datalink : graph
					.getGeneratedBy(artifact)) {
				ProvenanceProcess pp = datalink.getProcess();
				if (pp.getName().equals(process.getName())) {
					found = true;
					break;
				}
			}

		if (!noDuplicates || (noDuplicates && !found) && artifact != null)
			graph.assertGeneratedBy(artifact, process, role, account);
	}

	public void assertUsed(ProvenanceArtifact artifact,
			ProvenanceProcess process, ProvenanceRole role,
			ProvenanceAccount account, boolean noDuplicates)
			throws ProvenanceException {
		boolean found = false;

		if (noDuplicates)
			for (ProvenanceUsedArc datalink : graph.getUsed(process)) {
				ProvenanceArtifact pa = datalink.getArtifact();
				if (pa.getName().equals(artifact.getName())) {
					found = true;
					break;
				}
			}

		if (!noDuplicates || (noDuplicates && !found))
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
	 * @param account
	 *            the account to set
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
	 * @param currentRole
	 *            the currentRole to set
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
	 * @param currentArtifact
	 *            the currentArtifact to set
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
	 * @param currentAccount
	 *            the currentAccount to set
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
	 * @param currentProcess
	 *            the currentProcess to set
	 */
	public void setCurrentProcess(ProvenanceProcess currentProcess) {
		this.currentProcess = currentProcess;
	}

	public String writeGraph() {
		// print out OPM graph in RDF/XML form
		try {
			StringWriter sw = new StringWriter();
			new RdfXmlWriter().write(context.getTriples(), sw);
			return sw.toString();
		} catch (OperatorException | IOException e) {
			logger.error("Could not write graph", e);
		}
		return null;
	}

	/**
	 * IN THE RELEASE WE DO NOT SUPPORT XML -- ONE CAN CONVERT THE RDF TO XML OUT-OF-BAND
	 * simply invokes the org.openprovenance for converting an RDF OPM graph to an XML OPM graph
	 * @return a hard-coded filename for the converted XML OPM graph
	 * @throws OperatorException
	 * @throws IOException
	 * @throws JAXBException
	 */
//	public String Rdf2Xml() throws OperatorException, IOException, JAXBException {
//
//		OPMRdf2Xml converter = new OPMRdf2Xml();
//		converter.convert(OPM_RDF_GRAPH_FILE, OPM_XML_GRAPH_FILE);
//		return OPM_XML_GRAPH_FILE;
//	}

	/**
	 * creates a dot file from the current OPMGraph. <br/>
	 * DOT NOT USE NEEDS FIXING
	 * @return
	 * @throws IOException
	 * @throws OperatorException
	 */
	/*
	public String Rdf2Dot() throws OperatorException, IOException {

		OPMRdf2Xml converter = new OPMRdf2Xml();
		OPMGraph graph = converter.convert(OPM_RDF_GRAPH_FILE);

		List<Process> processes = graph.getProcesses().getProcess();
		for (Process p:processes) { p.setId("\""+p.getId()+"\""); }

		List<Artifact> artifacts = graph.getArtifacts().getArtifact();
		for (Artifact a:artifacts) { a.setId("\""+a.getId()+"\""); }

//		OPMToDot aOPMToDot = new OPMToDot(DOT_CONFIG_FILE);
		OPMToDot aOPMToDot = new OPMToDot();

		aOPMToDot.convert(graph, new File(OPM_DOT_FILE));
		return OPM_DOT_FILE;

	}

    */

	/**
	 * @param graph
	 *            the graph to set
	 */
	public void setGraph(ProvenanceContextFacade graph) {
		this.graph = graph;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public boolean isActive() {
		return isActive;
	}
}
