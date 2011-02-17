package uk.org.taverna.platform;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;

import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.platform.run.api.RunProfile;
import uk.org.taverna.platform.run.api.RunService;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class RunTest extends PlatformTest {

	private ReferenceService referenceService;
	private ExecutionService executionService;
	private RunService runService;

	public void testRun() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/in-out.t2flow");

		T2Reference reference = referenceService.register("test-input", 0, true, null);

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs, referenceService,
				executionService));
		assertEquals(State.CREATED, runService.getState(runId));
		runService.start(runId);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		assertNotNull(results);
		waitForResult(results, "out",  runService.getWorkflowReport(runId));

		String result = (String) referenceService.renderIdentifier(results.get("out"),
				String.class, null);
		assertEquals("test-input", result);
		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println( runService.getWorkflowReport(runId));
	}

	public void testRunBeanshell() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/beanshell.t2flow");

		T2Reference reference = referenceService.register("test-input", 0, true, null);
		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs, referenceService,
				executionService));
		WorkflowReport report = runService.getWorkflowReport(runId);
		assertEquals(State.CREATED, runService.getState(runId));
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResult(results, "out", report);

		T2Reference resultReference = results.get("out");
		assertFalse(resultReference.containsErrors());
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) referenceService.renderIdentifier(
				(T2Reference) results.get("out"), String.class, null);
		assertEquals(1000, result.size());
		assertEquals("test-input:0", result.get(0));
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
		assertEquals(State.RUNNING, runService.getState(runId));

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResult(results, "out", report);

		T2Reference resultReference = results.get("out");
		assertFalse(resultReference.containsErrors());
		String result = (String) referenceService.renderIdentifier(
				(T2Reference) results.get("out"), String.class, null);
		assertEquals("Test Value", result);
		assertEquals(State.COMPLETED, runService.getState(runId));		
	}
	
	public void testRunWSDL() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/wsdl.t2flow");

		T2Reference reference = referenceService.register("in", 0, true, null);
		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs, referenceService,
				executionService));
		WorkflowReport report = runService.getWorkflowReport(runId);
		assertEquals(State.CREATED, runService.getState(runId));
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResult(results, "out", report);

		T2Reference resultReference = results.get("out");
		assertFalse(resultReference.containsErrors());
		String result = (String) referenceService.renderIdentifier(
				(T2Reference) results.get("out"), String.class, null);
		System.out.println(result);
		assertEquals("Apache Axis version: 1.4\nBuilt on Apr 22, 2006 (06:55:48 PDT)", result);
		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}
	
	private void setup() {
		ServiceReference referenceServiceReference = bundleContext
				.getServiceReference("net.sf.taverna.t2.reference.ReferenceService");
		referenceService = (ReferenceService) bundleContext
				.getService(referenceServiceReference);

		ServiceReference executionServiceReference = bundleContext
				.getServiceReference("uk.org.taverna.platform.execution.api.ExecutionService");
		executionService = (ExecutionService) bundleContext
				.getService(executionServiceReference);

		ServiceReference runServiceReference = bundleContext
				.getServiceReference("uk.org.taverna.platform.run.api.RunService");
		runService = (RunService) bundleContext.getService(runServiceReference);
	}
	
	private WorkflowBundle loadWorkflow(String t2FlowFile) throws Exception {
		URL wfResource = getClass().getResource(t2FlowFile);
		assertNotNull(wfResource);
		T2FlowParser t2FlowParser = new T2FlowParser();
		t2FlowParser.setStrict(true);
		return t2FlowParser.parseT2Flow(wfResource.openStream());
	}

}
