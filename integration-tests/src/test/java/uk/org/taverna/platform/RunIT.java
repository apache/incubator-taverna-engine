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
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.security.credentialmanager.UsernamePassword;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.platform.run.api.RunProfile;
import uk.org.taverna.platform.run.api.RunService;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class RunIT extends PlatformIT {

	private RunService runService;

	protected void setup() throws Exception {
		super.setup();
		if (runService == null) {
			ServiceReference runServiceReference = bundleContext
					.getServiceReference("uk.org.taverna.platform.run.api.RunService");
			runService = (RunService) bundleContext.getService(runServiceReference);
		}
	}

	public void testRun() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/in-out.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test-input", 0, true, null));

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle,
				inputs));
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

		assertTrue(checkResult(referenceService, results.get("out"), "test-input"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunApiConsumer() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/apiconsumer.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test-input", 0, true, null));

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle,
				inputs));
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

		assertTrue(checkResult(referenceService, results.get("out"), "TEST-INPUT"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunBeanshell() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/beanshell.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test-input", 0, true, null));

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle,
				inputs));
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

	public void testRunBiomart() throws Exception {
		setup();
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/biomart.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
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
		assertEquals(5, result.size());
		assertEquals("ENSBTAG00000018854", result.get(0));
		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunDataflow() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/dataflow.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test input", 0, true, null));

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle,
				inputs));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertFalse(runService.getState(runId).equals(State.CREATED));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(referenceService, results.get("out"), "nested dataflow : test input"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunLocalworker() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/localworker.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("Tom", 0, true, null));

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle,
				inputs));
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

		assertTrue(checkResult(referenceService, results.get("out"), "Hello Tom"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunRest() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/rest.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		waitForResults(results, report, "out");
		T2Reference resultReference = results.get("out");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		String outResult = (String) referenceService.renderIdentifier(resultReference,
				String.class, null);
		assertTrue(outResult.contains("<name>AATM_RABIT</name>"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunRestSecureBasic() throws Exception {
		setup();
		credentialManager.addUsernameAndPasswordForService(new UsernamePassword("testuser",
				"testpasswd"), URI
				.create("http://heater.cs.man.ac.uk:7070/#Example+HTTP+BASIC+Authentication"));

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/secure-basic-authentication.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(
				referenceService,
				results.get("out"),
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
						+ "<HTML><HEAD><TITLE>Apache Tomcat Examples</TITLE>\n"
						+ "<META http-equiv=Content-Type content=\"text/html\">\n"
						+ "</HEAD>\n"
						+ "<BODY>\n"
						+ "<P>\n"
						+ "<H3>Secure Apache Tomcat Examples</H3>\n"
						+ "<P>Congratulations! If you see this page that means that you have authenticated yourself successfully using HTTP Basic Authentication.</P>\n"
						+ "</BODY></HTML>"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunRestSecureBasicHttps() throws Exception {
		setup();
		credentialManager.addUsernameAndPasswordForService(new UsernamePassword("testuser",
				"testpasswd"), URI
				.create("https://heater.cs.man.ac.uk:7443/#Example+HTTP+BASIC+Authentication"));

		URL trustedCertficateFileURL = getClass().getResource(
				"/security/tomcat_heater_certificate.pem");
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		X509Certificate trustedCertficate = (X509Certificate) certFactory
				.generateCertificate(trustedCertficateFileURL.openStream());
		credentialManager.addTrustedCertificate(trustedCertficate);

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/secure-basic-authentication-https.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(
				referenceService,
				results.get("out"),
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
						+ "<HTML><HEAD><TITLE>Apache Tomcat Examples</TITLE>\n"
						+ "<META http-equiv=Content-Type content=\"text/html\">\n"
						+ "</HEAD>\n"
						+ "<BODY>\n"
						+ "<P>\n"
						+ "<H3>Secure Apache Tomcat Examples</H3>\n"
						+ "<P>Congratulations! If you see this page that means that you have authenticated yourself successfully using HTTP Basic Authentication.</P>\n"
						+ "</BODY></HTML>"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunRestSecureDigest() throws Exception {
		setup();
		credentialManager.addUsernameAndPasswordForService(new UsernamePassword("testuser",
				"testpasswd"), URI
				.create("http://heater.cs.man.ac.uk:7070/#Example+HTTP+BASIC+Authentication"));

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/secure-digest-authentication.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(
				referenceService,
				results.get("out"),
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
						+ "<HTML><HEAD><TITLE>Apache Tomcat Examples</TITLE>\n"
						+ "<META http-equiv=Content-Type content=\"text/html\">\n"
						+ "</HEAD>\n"
						+ "<BODY>\n"
						+ "<P>\n"
						+ "<H3>Secure Apache Tomcat Examples</H3>\n"
						+ "<P>Congratulations! If you see this page that means that you have authenticated yourself successfully using HTTP Digest Authentication.</P>\n"
						+ "</BODY></HTML>"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunRestSecureDigestHttps() throws Exception {
		setup();
		credentialManager.addUsernameAndPasswordForService(new UsernamePassword("testuser",
				"testpasswd"), URI
				.create("http://heater.cs.man.ac.uk:7443/#Example+HTTP+BASIC+Authentication"));

		URL trustedCertficateFileURL = getClass().getResource(
				"/security/tomcat_heater_certificate.pem");
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		X509Certificate trustedCertficate = (X509Certificate) certFactory
				.generateCertificate(trustedCertficateFileURL.openStream());
		credentialManager.addTrustedCertificate(trustedCertficate);

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/secure-digest-authentication-https.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(
				referenceService,
				results.get("out"),
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
						+ "<HTML><HEAD><TITLE>Apache Tomcat Examples</TITLE>\n"
						+ "<META http-equiv=Content-Type content=\"text/html\">\n"
						+ "</HEAD>\n"
						+ "<BODY>\n"
						+ "<P>\n"
						+ "<H3>Secure Apache Tomcat Examples</H3>\n"
						+ "<P>Congratulations! If you see this page that means that you have authenticated yourself successfully using HTTP Digest Authentication.</P>\n"
						+ "</BODY></HTML>"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunSpreadsheetImport() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/spreadsheetimport.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		T2Reference resultReference = results.get("out");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) referenceService.renderIdentifier(resultReference,
				String.class, null);
		assertEquals(35, result.size());
		assertEquals("1971.0", result.get(1));
		assertEquals("2004.0", result.get(34));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunSoaplab() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/soaplab.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "sequence");

		assertTrue(checkResult(referenceService, results.get("sequence"),
				"ID   X52524; SV 1; linear; genomic DNA; STD; INV; 4507 BP."));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunStringConstant() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/stringconstant.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		WorkflowReport report = runService.getWorkflowReport(runId);
		assertEquals(State.CREATED, runService.getState(runId));

		runService.start(runId);
		assertTrue(runService.getState(runId).equals(State.RUNNING)
				|| runService.getState(runId).equals(State.COMPLETED));

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(referenceService, results.get("out"), "Test Value"));

		assertEquals(State.COMPLETED, runService.getState(runId));
	}

	public void testRunTool() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/tool.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		WorkflowReport report = runService.getWorkflowReport(runId);
		assertEquals(State.CREATED, runService.getState(runId));

		runService.start(runId);
		assertTrue(runService.getState(runId).equals(State.RUNNING)
				|| runService.getState(runId).equals(State.COMPLETED));

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(referenceService, results.get("out"), "HelloWorld"));

		assertEquals(State.COMPLETED, runService.getState(runId));
	}

	@SuppressWarnings("unchecked")
	public void testRunIteration() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/iteration.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test", 0, true, null));

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle,
				inputs));
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

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		// assertTrue(checkResult(referenceService, results.get("out"),
		// "Apache Axis version: 1.4\nBuilt on Apr 22, 2006 (06:55:48 PDT)"));
		assertTrue(checkResult(referenceService, results.get("out"),
				"Apache Axis version: 1.2\nBuilt on May 03, 2005 (02:20:24 EDT)"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunWSDLSecure() throws Exception {
		setup();
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("http://heater.cs.man.ac.uk:7070/axis/services/HelloService-PlaintextPassword?wsdl"));

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/wsdl-secure.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(referenceService, results.get("out"), "Hello Alan!"));

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

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out_plaintext", "out_digest", "out_digest_timestamp",
				"out_plaintext_timestamp");

		assertTrue(checkResult(referenceService, results.get("out_plaintext"), "Hello Alan!"));
		assertTrue(checkResult(referenceService, results.get("out_digest"), "Hello Stian!"));
		assertTrue(checkResult(referenceService, results.get("out_digest_timestamp"),
				"Hello David!"));
		assertTrue(checkResult(referenceService, results.get("out_plaintext_timestamp"),
				"Hello Alex!"));

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

		URL trustedCertficateFileURL = getClass().getResource(
				"/security/tomcat_heater_certificate.pem");
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		X509Certificate trustedCertficate = (X509Certificate) certFactory
				.generateCertificate(trustedCertficateFileURL.openStream());
		credentialManager.addTrustedCertificate(trustedCertficate);

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/secure-ws-https.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out_plaintext", "out_digest", "out_digest_timestamp",
				"out_plaintext_timestamp");

		assertTrue(checkResult(referenceService, results.get("out_plaintext"), "Hello Alan!"));
		assertTrue(checkResult(referenceService, results.get("out_digest"), "Hello Stian!"));
		assertTrue(checkResult(referenceService, results.get("out_digest_timestamp"),
				"Hello David!"));
		assertTrue(checkResult(referenceService, results.get("out_plaintext_timestamp"),
				"Hello Alex!"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunXMLSplitter() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/xmlSplitter.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("firstName", referenceService.register("John", 0, true, null));
		inputs.put("lastName", referenceService.register("Smith", 0, true, null));
		inputs.put("age", referenceService.register("21", 0, true, null));

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle,
				inputs));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertTrue(runService.getState(runId).equals(State.RUNNING)
				|| runService.getState(runId).equals(State.COMPLETED));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(referenceService, results.get("out"),
				"John Smith (21) of 40, Oxford Road. Manchester."));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunXPath() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/xpath.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertEquals(1, executionEnvironments.size());
		ExecutionEnvironment executionEnvironment = executionEnvironments.iterator().next();
		ReferenceService referenceService = executionEnvironment.getReferenceService();

		String runId = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
		assertEquals(State.CREATED, runService.getState(runId));

		WorkflowReport report = runService.getWorkflowReport(runId);
		System.out.println(report);

		runService.start(runId);
		assertTrue(runService.getState(runId).equals(State.RUNNING)
				|| runService.getState(runId).equals(State.COMPLETED));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResults(results, report, "out");

		assertTrue(checkResult(referenceService, results.get("out"),
				"<test-element>test</test-element>"));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

}
