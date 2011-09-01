/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester
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
package uk.org.taverna.platform;

import java.net.URI;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;
import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.TrustConfirmation;
import net.sf.taverna.t2.security.credentialmanager.TrustConfirmationProvider;
import net.sf.taverna.t2.security.credentialmanager.UsernamePassword;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.platform.run.api.RunProfile;
import uk.org.taverna.platform.run.api.RunService;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleReader;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowReader;

public class RunIT extends PlatformIT {

	private ExecutionService executionService;
	private RunService runService;
	private CredentialManager credentialManager;
	private WorkflowBundleReader workflowBundleReader;

	private void setup() throws InvalidSyntaxException {

		bundleContext.registerService(
				"net.sf.taverna.t2.security.credentialmanager.TrustConfirmationProvider",
				new TrustConfirmationProvider() {
					public TrustConfirmation shouldTrustCertificate(X509Certificate[] chain) {
						TrustConfirmation trustConfirmation = new TrustConfirmation();
						trustConfirmation.setShouldTrust(true);
						return trustConfirmation;
					}

				}, null);

		if (referenceService == null) {
			ServiceReference[] referenceServiceReferences = bundleContext.getServiceReferences(
					"net.sf.taverna.t2.reference.ReferenceService",
					"(org.springframework.osgi.bean.name=inMemoryReferenceService)");
			assertEquals(1, referenceServiceReferences.length);
			referenceService = (ReferenceService) bundleContext
					.getService(referenceServiceReferences[0]);
		}

		if (executionService == null) {
			ServiceReference[] executionServiceReferences = bundleContext.getServiceReferences(
					"uk.org.taverna.platform.execution.api.ExecutionService",
					"(org.springframework.osgi.bean.name=localExecution)");
			assertEquals(1, executionServiceReferences.length);
			executionService = (ExecutionService) bundleContext
					.getService(executionServiceReferences[0]);
		}

		if (runService == null) {
			ServiceReference runServiceReference = bundleContext
					.getServiceReference("uk.org.taverna.platform.run.api.RunService");
			runService = (RunService) bundleContext.getService(runServiceReference);
		}

		if (credentialManager == null) {
			ServiceReference credentialManagerReference = bundleContext
					.getServiceReference("net.sf.taverna.t2.security.credentialmanager.CredentialManager");
			credentialManager = (CredentialManager) bundleContext
					.getService(credentialManagerReference);
		}

		if (workflowBundleReader == null) {
			ServiceReference[] workflowBundleReaderReferences = bundleContext
					.getServiceReferences("uk.org.taverna.scufl2.api.io.WorkflowBundleReader", null);
			for (ServiceReference serviceReference : workflowBundleReaderReferences) {
				workflowBundleReader = (WorkflowBundleReader) bundleContext.getService(serviceReference);
				if (workflowBundleReader.getMediaTypes().contains(T2FlowReader.APPLICATION_VND_TAVERNA_T2FLOW_XML)) {
					break;
				}
			}
		}

		ServiceReference[] masterPasswordProviderReferences = bundleContext.getServiceReferences(
				"net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider", null);
		for (ServiceReference serviceReference : masterPasswordProviderReferences) {
			MasterPasswordProvider masterPasswordProvider = (MasterPasswordProvider) bundleContext
					.getService(serviceReference);
			masterPasswordProvider.setMasterPassword("test");
		}

	}

	public void testRun() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/in-out.t2flow");

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test-input", 0, true, null));

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs,
				referenceService, executionService));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertTrue(runService.getState(runId).equals(State.RUNNING)
				|| runService.getState(runId).equals(State.COMPLETED));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		assertNotNull(results);
		waitForResults(results, runService.getWorkflowReport(runId), "out");

		assertTrue(checkResult(results.get("out"), "test-input"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunBeanshell() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/beanshell.t2flow");

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test-input", 0, true, null));

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs,
				referenceService, executionService));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertTrue(runService.getState(runId).equals(State.RUNNING)
				|| runService.getState(runId).equals(State.COMPLETED));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		assertNotNull(results);
		waitForResults(results, report, "out");

		T2Reference resultReference = results.get("out");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) referenceService.renderIdentifier(resultReference,
				String.class, null);
		assertEquals(1000, result.size());
		assertEquals("test-input:0", result.get(0));
		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunDataflow() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/dataflow.t2flow");

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test input", 0, true, null));

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs,
				referenceService, executionService));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertFalse(runService.getState(runId).equals(State.CREATED));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(results.get("out"), "nested dataflow : test input"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunLocalworker() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/localworker.t2flow");

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("Tom", 0, true, null));

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs,
				referenceService, executionService));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertTrue(runService.getState(runId).equals(State.RUNNING)
				|| runService.getState(runId).equals(State.COMPLETED));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		assertNotNull(results);
		waitForResults(results, report, "out");

		assertTrue(checkResult(results.get("out"), "Hello Tom"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	// public void testRunRestSecure() throws Exception {
	// setup();
	//
	// WorkflowBundle workflowBundle = loadWorkflow("/t2flow/secure-basic-authentication.t2flow");
	//
	// String runId = runService.createRun(new RunProfile(workflowBundle, referenceService,
	// executionService));
	// assertEquals(State.CREATED, runService.getState(runId));
	//
	// WorkflowReport report = runService.getWorkflowReport(runId);
	// System.out.println(report);
	//
	// runService.start(runId);
	// assertEquals(State.RUNNING, runService.getState(runId));
	// System.out.println(report);
	//
	// Map<String, T2Reference> results = runService.getOutputs(runId);
	// waitForResults(results, report, "out");
	//
	// assertTrue(checkResult(
	// results.get("out"),
	// "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
	// + "<HTML><HEAD><TITLE>Apache Tomcat Examples</TITLE>\n"
	// + "<META http-equiv=Content-Type content=\"text/html\">\n"
	// + "</HEAD>\n"
	// + "<BODY>\n"
	// + "<P>\n"
	// + "<H3>Secure Apache Tomcat Examples</H3>\n"
	// +
	// "<P>Congratulations! If you see this page that means that you have authenticated yourself successfully using HTTP Basic Authentication.</P>\n"
	// + "</BODY></HTML>"));
	//
	// assertEquals(State.COMPLETED, runService.getState(runId));
	// System.out.println(report);
	// }

	public void testRunSoaplab() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/soaplab.t2flow");

		String runId = runService.createRun(new RunProfile(workflowBundle, referenceService,
				executionService));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "sequence");

		assertTrue(checkResult(results.get("sequence"),
				"ID   X52524; SV 1; linear; genomic DNA; STD; INV; 4507 BP."));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunStringConstant() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/stringconstant.t2flow");

		String runId = runService.createRun(new RunProfile(workflowBundle, referenceService,
				executionService));
		WorkflowReport report = runService.getWorkflowReport(runId);
		assertEquals(State.CREATED, runService.getState(runId));

		runService.start(runId);
		assertTrue(runService.getState(runId).equals(State.RUNNING)
				|| runService.getState(runId).equals(State.COMPLETED));

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(results.get("out"), "Test Value"));

		assertEquals(State.COMPLETED, runService.getState(runId));
	}

	@SuppressWarnings("unchecked")
	public void testRunIteration() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/iteration.t2flow");

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test", 0, true, null));

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs,
				referenceService, executionService));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);

		waitForResults(results, report, "cross");
		T2Reference resultReference = results.get("cross");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		List<List<String>> crossResult = (List<List<String>>) referenceService.renderIdentifier(
				resultReference, String.class, null);
		assertEquals(10, crossResult.size());
		assertEquals(10, crossResult.get(0).size());
		assertEquals(10, crossResult.get(5).size());
		assertEquals("test:0test:0", crossResult.get(0).get(0));
		assertEquals("test:0test:1", crossResult.get(0).get(1));
		assertEquals("test:4test:2", crossResult.get(4).get(2));
		assertEquals("test:7test:6", crossResult.get(7).get(6));

		waitForResults(results, report, "dot");
		resultReference = results.get("dot");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		List<String> dotResult = (List<String>) referenceService.renderIdentifier(resultReference,
				String.class, null);
		assertEquals(10, dotResult.size());
		assertEquals("test:0test:0", dotResult.get(0));
		assertEquals("test:5test:5", dotResult.get(5));

		waitForResults(results, report, "crossdot");
		resultReference = results.get("crossdot");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		List<List<String>> crossdotResult = (List<List<String>>) referenceService.renderIdentifier(
				resultReference, String.class, null);
		assertEquals(10, crossdotResult.size());
		assertEquals(10, crossdotResult.get(0).size());
		assertEquals(10, crossdotResult.get(5).size());
		assertEquals("test:0test:0test", crossdotResult.get(0).get(0));
		assertEquals("test:0test:1test", crossdotResult.get(0).get(1));
		assertEquals("test:4test:2test", crossdotResult.get(4).get(2));
		assertEquals("test:7test:6test", crossdotResult.get(7).get(6));

		waitForResults(results, report, "dotcross");
		resultReference = results.get("dotcross");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		List<String> dotcrossResult = (List<String>) referenceService.renderIdentifier(
				resultReference, String.class, null);
		assertEquals(10, dotResult.size());
		assertEquals("test:0test:0test", dotcrossResult.get(0));
		assertEquals("test:5test:5test", dotcrossResult.get(5));

		waitForResults(results, report, "dotdot");
		resultReference = results.get("dotdot");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		List<String> dotdotResult = (List<String>) referenceService.renderIdentifier(
				resultReference, String.class, null);
		assertEquals(10, dotResult.size());
		assertEquals("test:0test:0test:0", dotdotResult.get(0));
		assertEquals("test:5test:5test:5", dotdotResult.get(5));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunWSDL() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/wsdl.t2flow");

		String runId = runService.createRun(new RunProfile(workflowBundle, referenceService,
				executionService));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		// assertTrue(checkResult(results.get("out"),
		// "Apache Axis version: 1.4\nBuilt on Apr 22, 2006 (06:55:48 PDT)"));
		assertTrue(checkResult(results.get("out"),
				"Apache Axis version: 1.2\nBuilt on May 03, 2005 (02:20:24 EDT)"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunWSDLSecure() throws Exception {
		setup();
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/wsdl-secure.t2flow");
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("http://heater.cs.man.ac.uk:7070/axis/services/HelloService-PlaintextPassword?wsdl"));

		String runId = runService.createRun(new RunProfile(workflowBundle, referenceService,
				executionService));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(results.get("out"), "Hello Alan!"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunWSDLSecureFull() throws Exception {
		setup();
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("http://heater.cs.man.ac.uk:7070/axis/services/HelloService-PlaintextPassword?wsdl"));
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("http://heater.cs.man.ac.uk:7070/axis/services/HelloService-DigestPassword?wsdl"));
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("http://heater.cs.man.ac.uk:7070/axis/services/HelloService-DigestPassword-Timestamp?wsdl"));
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("http://heater.cs.man.ac.uk:7070/axis/services/HelloService-PlaintextPassword-Timestamp?wsdl"));

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/secure-ws.t2flow");

		String runId = runService.createRun(new RunProfile(workflowBundle, referenceService,
				executionService));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out_plaintext", "out_digest", "out_digest_timestamp",
				"out_plaintext_timestamp");

		assertTrue(checkResult(results.get("out_plaintext"), "Hello Alan!"));
		assertTrue(checkResult(results.get("out_digest"), "Hello Stian!"));
		assertTrue(checkResult(results.get("out_digest_timestamp"), "Hello David!"));
		assertTrue(checkResult(results.get("out_plaintext_timestamp"), "Hello Alex!"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunWSDLSecureSsh() throws Exception {
		setup();
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("https://heater.cs.man.ac.uk:7443/axis/services/HelloService-PlaintextPassword?wsdl"));
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("https://heater.cs.man.ac.uk:7443/axis/services/HelloService-DigestPassword?wsdl"));
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("https://heater.cs.man.ac.uk:7443/axis/services/HelloService-DigestPassword-Timestamp?wsdl"));
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("https://heater.cs.man.ac.uk:7443/axis/services/HelloService-PlaintextPassword-Timestamp?wsdl"));

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/secure-ws-https.t2flow");

		String runId = runService.createRun(new RunProfile(workflowBundle, referenceService,
				executionService));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out_plaintext", "out_digest", "out_digest_timestamp",
				"out_plaintext_timestamp");

		assertTrue(checkResult(results.get("out_plaintext"), "Hello Alan!"));
		assertTrue(checkResult(results.get("out_digest"), "Hello Stian!"));
		assertTrue(checkResult(results.get("out_digest_timestamp"), "Hello David!"));
		assertTrue(checkResult(results.get("out_plaintext_timestamp"), "Hello Alex!"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunXMLSplitter() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/xmlSplitter.t2flow");

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("firstName", referenceService.register("John", 0, true, null));
		inputs.put("lastName", referenceService.register("Smith", 0, true, null));
		inputs.put("age", referenceService.register("21", 0, true, null));

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs,
				referenceService, executionService));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertTrue(runService.getState(runId).equals(State.RUNNING)
				|| runService.getState(runId).equals(State.COMPLETED));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(results.get("out"),
				"John Smith (21) of 40, Oxford Road. Manchester."));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	private WorkflowBundle loadWorkflow(String t2FlowFile) throws Exception {
		URL wfResource = getClass().getResource(t2FlowFile);
		assertNotNull(wfResource);
		System.out.println(workflowBundleReader.getMediaTypes());
		return workflowBundleReader.readBundle(wfResource.openStream(), T2FlowReader.APPLICATION_VND_TAVERNA_T2FLOW_XML);
//		T2FlowParser t2FlowParser = new T2FlowParser();
//		t2FlowParser.setStrict(true);
//		return t2FlowParser.parseT2Flow(wfResource.openStream());
	}

}
