package uk.org.taverna.platform;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.StackTraceElementBean;
import net.sf.taverna.t2.reference.T2Reference;

import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class LocalExecutionTest extends PlatformTest {

	public void testLocalExecution() throws Exception {
		URL wfResource = getClass().getResource("/t2flow/in-out.t2flow");
		assertNotNull(wfResource);
		WorkflowBundle workflowBundle = new T2FlowParser().parseT2Flow(wfResource.openStream());
		Workflow workflow = workflowBundle.getMainWorkflow();
		Profile profile = workflowBundle.getProfiles().iterator().next();

		ServiceReference referenceServiceReference = bundleContext
				.getServiceReference("net.sf.taverna.t2.reference.ReferenceService");
		ReferenceService referenceService = (ReferenceService) bundleContext
				.getService(referenceServiceReference);

		ServiceReference executionServiceReference = bundleContext
				.getServiceReference("uk.org.taverna.platform.execution.api.ExecutionService");
		ExecutionService executionService = (ExecutionService) bundleContext
				.getService(executionServiceReference);

		T2Reference reference = referenceService.register("test-input", 0, true, null);

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

		String executionId = executionService.createExecution(workflowBundle, workflow, profile, inputs,
				referenceService);
		WorkflowReport report = executionService.getWorkflowReport(executionId);
		assertEquals(State.CREATED, report.getState());
		executionService.start(executionId);

		Map<String, T2Reference> results = report.getOutputs();
		assertNotNull(results);
		waitForResult(results, "out", report);

		String result = (String) referenceService.renderIdentifier(results.get("out"),
				String.class, null);
		assertEquals("test-input", result);
		assertEquals(State.COMPLETED, report.getState());
		System.out.println(report);
	}

	public void testLocalExecution2() throws Exception {
		URL wfResource = getClass().getResource("/t2flow/beanshell.t2flow");
		assertNotNull(wfResource);
		T2FlowParser t2FlowParser = new T2FlowParser();
		t2FlowParser.setStrict(true);
		WorkflowBundle workflowBundle = t2FlowParser.parseT2Flow(wfResource.openStream());
		Workflow workflow = workflowBundle.getMainWorkflow();
		Profile profile = workflowBundle.getProfiles().iterator().next();

		ServiceReference referenceServiceReference = bundleContext
				.getServiceReference("net.sf.taverna.t2.reference.ReferenceService");
		ReferenceService referenceService = (ReferenceService) bundleContext
				.getService(referenceServiceReference);

		ServiceReference executionServiceReference = bundleContext
				.getServiceReference("uk.org.taverna.platform.execution.api.ExecutionService");
		ExecutionService executionService = (ExecutionService) bundleContext
				.getService(executionServiceReference);

		T2Reference reference = referenceService.register("test-input", 0, true, null);
		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

		String executionId = executionService.createExecution(workflowBundle, workflow, profile, inputs,
				referenceService);
		WorkflowReport report = executionService.getWorkflowReport(executionId);
		System.out.println(report);
		assertEquals(State.CREATED, report.getState());
		executionService.start(executionId);
		System.out.println(report);

		Map<String, T2Reference> results = report.getOutputs();
		waitForResult(results, "out", report);

		T2Reference resultReference = results.get("out");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		List<String> result = (List<String>) referenceService.renderIdentifier(
				(T2Reference) results.get("out"), String.class, null);
		assertEquals(1000, result.size());
		assertEquals("test-input:0", result.get(0));
		assertEquals(State.COMPLETED, report.getState());
		System.out.println(report);
	}
	
	public void testLocalExecution3() throws Exception {
		URL wfResource = getClass().getResource("/t2flow/beanshell.t2flow");
		assertNotNull(wfResource);
		T2FlowParser t2FlowParser = new T2FlowParser();
		t2FlowParser.setStrict(true);
		WorkflowBundle workflowBundle = t2FlowParser.parseT2Flow(wfResource.openStream());
		Workflow workflow = workflowBundle.getMainWorkflow();
		Profile profile = workflowBundle.getProfiles().iterator().next();

		ServiceReference referenceServiceReference = bundleContext
				.getServiceReference("net.sf.taverna.t2.reference.ReferenceService");
		ReferenceService referenceService = (ReferenceService) bundleContext
				.getService(referenceServiceReference);

		ServiceReference executionServiceReference = bundleContext
				.getServiceReference("uk.org.taverna.platform.execution.api.ExecutionService");
		ExecutionService executionService = (ExecutionService) bundleContext
				.getService(executionServiceReference);

		T2Reference reference = referenceService.register("test-input", 0, true, null);
		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

		String executionId = executionService.createExecution(workflowBundle, workflow, profile, inputs,
				referenceService);
	}

	private void printErrors(ReferenceService referenceService, T2Reference resultReference) {
		if (resultReference.getDepth() > 0) {
			IdentifiedList<T2Reference> list = referenceService.getListService().getList(
					resultReference);
			for (T2Reference t2Reference : list) {
				printErrors(referenceService, t2Reference);
			}
		} else if (resultReference.containsErrors()) {
			ErrorDocument error = referenceService.getErrorDocumentService().getError(
					resultReference);
			String message = error.getMessage();
			if (message != null) {
				System.out.println(message);
			}
			String exceptionMessage = error.getExceptionMessage();
			if (exceptionMessage != null) {
				System.out.println(exceptionMessage);
			}
			for (StackTraceElementBean stackTraceElementBean : error.getStackTraceStrings()) {
				System.out.println(stackTraceElementBean);
			}
			Set<T2Reference> errorReferences = error.getErrorReferences();
			for (T2Reference t2Reference : errorReferences) {
				printErrors(referenceService, t2Reference);
			}
		}
	}

}
