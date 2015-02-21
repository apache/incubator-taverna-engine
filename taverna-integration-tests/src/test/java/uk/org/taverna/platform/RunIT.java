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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;

import net.sf.taverna.t2.security.credentialmanager.CredentialManager;
import net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider;
import net.sf.taverna.t2.security.credentialmanager.UsernamePassword;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.databundle.DataBundles;
import uk.org.taverna.osgi.starter.TavernaStarter;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.platform.run.api.RunProfile;
import uk.org.taverna.platform.run.api.RunService;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;

public class RunIT extends PlatformIT {

	private static TavernaStarter tavernaStarter;
	private static BundleContext bundleContext;
	private static WorkflowBundleIO workflowBundleIO;
	private static CredentialManager credentialManager;
	private static RunService runService;

	public WorkflowBundle loadWorkflow(String t2FlowFile) throws Exception {
		return super.loadWorkflow(t2FlowFile, workflowBundleIO);
	}

	@BeforeClass
	public static void setup() throws Exception {
		tavernaStarter = new TavernaStarter(new File("/tmp"));
		tavernaStarter.start();
		bundleContext = tavernaStarter.getContext();
		runService = tavernaStarter.getRunService();
		credentialManager = tavernaStarter.getCredentialManager();
		workflowBundleIO = tavernaStarter.getWorkflowBundleIO();

		bundleContext.registerService(
				"net.sf.taverna.t2.security.credentialmanager.MasterPasswordProvider",
				new MasterPasswordProvider() {
					public String getMasterPassword(boolean firstTime) {
						return "test";
					}

					public void setMasterPassword(String password) {
					}

					public int getProviderPriority() {
						return 0;
					}
				}, null);

	}

	@AfterClass
	public static void shutdown() throws Exception {
		tavernaStarter.stop();
	}

	@Test
	public void testRun() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/in-out.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			Bundle inputBundle = DataBundles.createBundle();
			Path inputs = DataBundles.getInputs(inputBundle);
			Path port = DataBundles.getPort(inputs, "in");
			DataBundles.setStringValue(port, "test-input");

			String runId = runService.createRun(new RunProfile(executionEnvironment,
					workflowBundle, inputBundle));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out"), "test-input"));
		}
	}

	@Test
	public void testRunApiConsumer() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/apiconsumer.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			Bundle inputBundle = DataBundles.createBundle();
			Path inputs = DataBundles.getInputs(inputBundle);
			Path port = DataBundles.getPort(inputs, "in");
			DataBundles.setStringValue(port, "test-input");

			String runId = runService.createRun(new RunProfile(executionEnvironment,
					workflowBundle, inputBundle));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out"), "TEST-INPUT"));
		}
	}

	@Test
	public void testRunBeanshell() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/beanshell.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			Bundle inputBundle = DataBundles.createBundle();
			Path inputs = DataBundles.getInputs(inputBundle);
			Path port = DataBundles.getPort(inputs, "in");
			DataBundles.setStringValue(port, "test-input");

			String runId = runService.createRun(new RunProfile(executionEnvironment,
					workflowBundle, inputBundle));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			Path outPort = DataBundles.getPort(outputs, "out");
			assertTrue(DataBundles.isList(outPort));
			List<Path> result = DataBundles.getList(outPort);
			assertEquals(1000, result.size());
			assertEquals("test-input:0", DataBundles.getStringValue(result.get(0)));
		}
	}

	@Test
	public void testRunBiomart() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/biomart.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			Path outPort = DataBundles.getPort(outputs, "out");
			assertTrue(DataBundles.isList(outPort));
			List<Path> result = DataBundles.getList(outPort);
			assertEquals(6, result.size());
			assertEquals("ENSBTAG00000018278", DataBundles.getStringValue(result.get(0)));
		}
	}

	@Test
	@Ignore
	public void testRunBiomoby() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/biomoby.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assumeTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {

			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			Path outPort = DataBundles.getPort(outputs, "out");
			assertTrue(DataBundles.isList(outPort));
			List<Path> result = DataBundles.getList(outPort);
			assertEquals(5, result.size());
			assertEquals("ENSBTAG00000018854", DataBundles.getStringValue(result.get(0)));
		}
	}

	@Test
	public void testRunDataflow() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/dataflow.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			Bundle inputBundle = DataBundles.createBundle();
			Path inputs = DataBundles.getInputs(inputBundle);
			Path port = DataBundles.getPort(inputs, "in");
			DataBundles.setStringValue(port, "test-input");

			String runId = runService.createRun(new RunProfile(executionEnvironment,
					workflowBundle, inputBundle));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out"), "nested dataflow : test-input"));
		}
	}

	@Test
	public void testRunLocalworker() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/localworker.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			Bundle inputBundle = DataBundles.createBundle();
			Path inputs = DataBundles.getInputs(inputBundle);
			Path port = DataBundles.getPort(inputs, "in");
			DataBundles.setStringValue(port, "Tom");

			String runId = runService.createRun(new RunProfile(executionEnvironment,
					workflowBundle, inputBundle));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out"), "Hello Tom"));
		}
	}

	@Test
	public void testRunRest() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/rest.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			String outString = DataBundles.getStringValue(DataBundles.getPort(outputs, "out"));
			assertTrue(outString.contains("<name>AATM_RABIT</name>"));
		}
	}

	@Test
	public void testRunRestSecureBasic() throws Exception {
		credentialManager.addUsernameAndPasswordForService(new UsernamePassword("testuser",
				"testpasswd"), URI
				.create("http://heater.cs.man.ac.uk:7070/#Example+HTTP+BASIC+Authentication"));

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/secure-basic-authentication.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(
					DataBundles.getPort(outputs, "out"),
					"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
							+ "<HTML><HEAD><TITLE>Apache Tomcat Examples</TITLE>\n"
							+ "<META http-equiv=Content-Type content=\"text/html\">\n"
							+ "</HEAD>\n"
							+ "<BODY>\n"
							+ "<P>\n"
							+ "<H3>Secure Apache Tomcat Examples</H3>\n"
							+ "<P>Congratulations! If you see this page that means that you have authenticated yourself successfully using HTTP Basic Authentication.</P>\n"
							+ "</BODY></HTML>"));
		}
	}

	@Test
	public void testRunRestSecureBasicHttps() throws Exception {
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
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(
					DataBundles.getPort(outputs, "out"),
					"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
							+ "<HTML><HEAD><TITLE>Apache Tomcat Examples</TITLE>\n"
							+ "<META http-equiv=Content-Type content=\"text/html\">\n"
							+ "</HEAD>\n"
							+ "<BODY>\n"
							+ "<P>\n"
							+ "<H3>Secure Apache Tomcat Examples</H3>\n"
							+ "<P>Congratulations! If you see this page that means that you have authenticated yourself successfully using HTTP Basic Authentication.</P>\n"
							+ "</BODY></HTML>"));
		}
	}

	@Test
	public void testRunRestSecureDigest() throws Exception {
		credentialManager.addUsernameAndPasswordForService(new UsernamePassword("testuser",
				"testpasswd"), URI
				.create("http://heater.cs.man.ac.uk:7070/#Example+HTTP+BASIC+Authentication"));

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/secure-digest-authentication.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(
					DataBundles.getPort(outputs, "out"),
					"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
							+ "<HTML><HEAD><TITLE>Apache Tomcat Examples</TITLE>\n"
							+ "<META http-equiv=Content-Type content=\"text/html\">\n"
							+ "</HEAD>\n"
							+ "<BODY>\n"
							+ "<P>\n"
							+ "<H3>Secure Apache Tomcat Examples</H3>\n"
							+ "<P>Congratulations! If you see this page that means that you have authenticated yourself successfully using HTTP Digest Authentication.</P>\n"
							+ "</BODY></HTML>"));
		}
	}

	@Test
	public void testRunRestSecureDigestHttps() throws Exception {
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
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(
					DataBundles.getPort(outputs, "out"),
					"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
							+ "<HTML><HEAD><TITLE>Apache Tomcat Examples</TITLE>\n"
							+ "<META http-equiv=Content-Type content=\"text/html\">\n"
							+ "</HEAD>\n"
							+ "<BODY>\n"
							+ "<P>\n"
							+ "<H3>Secure Apache Tomcat Examples</H3>\n"
							+ "<P>Congratulations! If you see this page that means that you have authenticated yourself successfully using HTTP Digest Authentication.</P>\n"
							+ "</BODY></HTML>"));
		}
	}

	@Test
	public void testRunSpreadsheetImport() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/spreadsheetimport.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			Path outPort = DataBundles.getPort(outputs, "out");
			assertTrue(DataBundles.isList(outPort));
			List<Path> result = DataBundles.getList(outPort);
			assertEquals(35, result.size());
			assertEquals("1971.0", DataBundles.getStringValue(result.get(1)));
			assertEquals("2004.0", DataBundles.getStringValue(result.get(34)));
		}
	}

	@Test
	public void testRunSoaplab() throws Exception {
		// TODO find new soaplab service for testing
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/soaplab.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "sequence"),
					"ID   X52524; SV 1; linear; genomic DNA; STD; INV; 4507 BP."));
		}
	}

	@Test
	public void testRunStringConstant() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/stringconstant.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			WorkflowReport report = runService.getWorkflowReport(runId);
			assertEquals(State.CREATED, runService.getState(runId));

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out"), "Test Value"));
		}
	}

	@Test
	public void testRunTool() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/tool.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			WorkflowReport report = runService.getWorkflowReport(runId);
			assertEquals(State.CREATED, runService.getState(runId));

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out"), "HelloWorld"));
		}
	}

	@Test
	public void testRunIteration() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/iteration.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			Bundle inputBundle = DataBundles.createBundle();
			Path inputs = DataBundles.getInputs(inputBundle);
			Path port = DataBundles.getPort(inputs, "in");
			DataBundles.setStringValue(port, "test");

			String runId = runService.createRun(new RunProfile(executionEnvironment,
					workflowBundle, inputBundle));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			Path crossPort = DataBundles.getPort(outputs, "cross");
			assertTrue(DataBundles.isList(crossPort));
			List<Path> crossResult = DataBundles.getList(crossPort);
			assertEquals(10, crossResult.size());
			assertEquals(10, DataBundles.getList(crossResult.get(0)).size());
			assertEquals(10, DataBundles.getList(crossResult.get(5)).size());
			assertEquals("test:0test:0", DataBundles.getStringValue(DataBundles.getListItem(DataBundles.getListItem(crossPort, 0), 0)));
			assertEquals("test:0test:1", DataBundles.getStringValue(DataBundles.getListItem(DataBundles.getListItem(crossPort, 0), 1)));
			assertEquals("test:4test:2", DataBundles.getStringValue(DataBundles.getListItem(DataBundles.getListItem(crossPort, 4), 2)));
			assertEquals("test:7test:6", DataBundles.getStringValue(DataBundles.getListItem(DataBundles.getListItem(crossPort, 7), 6)));

			Path dotPort = DataBundles.getPort(outputs, "dot");
			assertTrue(DataBundles.isList(dotPort));
			List<Path> dotResult = DataBundles.getList(dotPort);
			assertEquals(10, dotResult.size());
			assertEquals("test:0test:0", DataBundles.getStringValue(DataBundles.getListItem(dotPort, 0)));
			assertEquals("test:5test:5", DataBundles.getStringValue(DataBundles.getListItem(dotPort, 5)));

			Path crossdotPort = DataBundles.getPort(outputs, "crossdot");
			assertTrue(DataBundles.isList(crossdotPort));
			List<Path> crossdotResult = DataBundles.getList(crossdotPort);
			assertEquals(10, crossdotResult.size());
			assertEquals(10, DataBundles.getList(crossdotResult.get(0)).size());
			assertEquals(10, DataBundles.getList(crossdotResult.get(5)).size());
			assertEquals("test:0test:0test", DataBundles.getStringValue(DataBundles.getListItem(DataBundles.getListItem(crossdotPort, 0), 0)));
			assertEquals("test:0test:1test", DataBundles.getStringValue(DataBundles.getListItem(DataBundles.getListItem(crossdotPort, 0), 1)));
			assertEquals("test:4test:2test", DataBundles.getStringValue(DataBundles.getListItem(DataBundles.getListItem(crossdotPort, 4), 2)));
			assertEquals("test:7test:6test", DataBundles.getStringValue(DataBundles.getListItem(DataBundles.getListItem(crossdotPort, 7), 6)));

			Path dotcrossPort = DataBundles.getPort(outputs, "dotcross");
			assertTrue(DataBundles.isList(dotcrossPort));
			List<Path> dotcrossResult = DataBundles.getList(crossdotPort);
			assertEquals(10, dotcrossResult.size());
			assertEquals("test:0test:0test", DataBundles.getStringValue(DataBundles.getListItem(dotcrossPort, 0)));
			assertEquals("test:5test:5test", DataBundles.getStringValue(DataBundles.getListItem(dotcrossPort, 5)));

			Path dotdotPort = DataBundles.getPort(outputs, "dotdot");
			assertTrue(DataBundles.isList(dotdotPort));
			List<Path> dotdotResult = DataBundles.getList(crossdotPort);
			assertEquals(10, dotdotResult.size());
			assertEquals("test:0test:0test:0", DataBundles.getStringValue(DataBundles.getListItem(dotdotPort, 0)));
			assertEquals("test:5test:5test:5", DataBundles.getStringValue(DataBundles.getListItem(dotdotPort, 5)));
		}
	}

	@Test
	public void testRunWSDL() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/wsdl.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			// assertTrue(checkResult(results.get("out"),
			// "Apache Axis version: 1.4\nBuilt on Apr 22, 2006 (06:55:48 PDT)"));
			assertTrue(checkResult(DataBundles.getPort(outputs, "out"),
					"Apache Axis version: 1.2\nBuilt on May 03, 2005 (02:20:24 EDT)"));
		}
	}

	@Test
	public void testRunWSDLSecure() throws Exception {
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("http://heater.cs.man.ac.uk:7070/axis/services/HelloService-PlaintextPassword?wsdl"));

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/wsdl-secure.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out"), "Hello Alan!"));
		}
	}

	@Test
	public void testRunWSDLSecureFull() throws Exception {
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
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out_plaintext"), "Hello Alan!"));
			assertTrue(checkResult(DataBundles.getPort(outputs, "out_digest"), "Hello Stian!"));
			assertTrue(checkResult(DataBundles.getPort(outputs, "out_digest_timestamp"), "Hello David!"));
			assertTrue(checkResult(DataBundles.getPort(outputs, "out_plaintext_timestamp"), "Hello Alex!"));
		}
	}

	@Test
	public void testRunWSDLSecureSsh() throws Exception {
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
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out_plaintext"), "Hello Alan!"));
			assertTrue(checkResult(DataBundles.getPort(outputs, "out_digest"), "Hello Stian!"));
			assertTrue(checkResult(DataBundles.getPort(outputs, "out_digest_timestamp"), "Hello David!"));
			assertTrue(checkResult(DataBundles.getPort(outputs, "out_plaintext_timestamp"), "Hello Alex!"));
		}
	}

	@Test
	public void testRunXMLSplitter() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/xmlSplitter.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			Bundle inputBundle = DataBundles.createBundle();
			Path inputs = DataBundles.getInputs(inputBundle);
			DataBundles.setStringValue(DataBundles.getPort(inputs, "firstName"), "John");
			DataBundles.setStringValue(DataBundles.getPort(inputs, "lastName"), "Smith");
			DataBundles.setStringValue(DataBundles.getPort(inputs, "age"), "21");

			String runId = runService.createRun(new RunProfile(executionEnvironment,
					workflowBundle, inputBundle));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out"),
					"John Smith (21) of 40, Oxford Road. Manchester."));
		}
	}

	@Test
	public void testRunXPath() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/xpath.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			String runId = runService
					.createRun(new RunProfile(executionEnvironment, workflowBundle, DataBundles.createBundle()));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out"), "<test-element>test</test-element>"));
		}
	}

	@Test
	public void testRunFromFile() throws Exception {
		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/in-out.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = runService
				.getExecutionEnvironments(workflowBundle);
		assertTrue(executionEnvironments.size() > 0);
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			Bundle inputBundle = DataBundles.createBundle();
			Path inputs = DataBundles.getInputs(inputBundle);
			File file = loadFile("/t2flow/input.txt");
			DataBundles.setReference(DataBundles.getPort(inputs, "in"), file.toURI());
			assertTrue(DataBundles.isReference(DataBundles.getPort(inputs, "in")));
			assertFalse(DataBundles.isValue(DataBundles.getPort(inputs, "in")));

			String runId = runService.createRun(new RunProfile(executionEnvironment,
					workflowBundle, inputBundle));
			assertEquals(State.CREATED, runService.getState(runId));

			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println(report);

			runService.start(runId);
			assertTrue(waitForState(report, State.COMPLETED));

			Bundle outputBundle = runService.getDataBundle(runId);
			assertNotNull(outputBundle);
			Path outputs = DataBundles.getOutputs(outputBundle);

			assertTrue(checkResult(DataBundles.getPort(outputs, "out"), "test input value"));
		}
	}

}
