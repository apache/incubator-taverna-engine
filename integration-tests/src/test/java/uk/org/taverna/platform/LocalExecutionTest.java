package uk.org.taverna.platform;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
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

	public void testDataflowExecution() throws Exception {
		URL wfResource = getClass().getResource("/t2flow/in-out.t2flow");
		assertNotNull(wfResource);
		WorkflowBundle researchObject = new T2FlowParser().parseT2Flow(wfResource.openStream());
		Workflow workflow = researchObject.getMainWorkflow();
		Profile profile = researchObject.getProfiles().iterator().next();

		ServiceReference referenceServiceReference = bundleContext
				.getServiceReference("net.sf.taverna.t2.reference.ReferenceService");
		ReferenceService referenceService = (ReferenceService) bundleContext
				.getService(referenceServiceReference);
		
		ServiceReference executionServiceReference = bundleContext
				.getServiceReference("uk.org.taverna.platform.execution.api.ExecutionService");
		ExecutionService executionService = (ExecutionService) bundleContext.getService(executionServiceReference);
		
		T2Reference reference = referenceService.register("test-input", 0, true, null);

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

		String executionId = executionService.createExecution(workflow, profile, inputs, referenceService);
		WorkflowReport report = executionService.getWorkflowReport(executionId);
		assertEquals(State.CREATED, report.getState());
		executionService.start(executionId);

		Map<String, T2Reference> results = report.getOutputs();
		waitForResult(results, "out", report);

		String result = (String) referenceService.renderIdentifier(results.get("out"), String.class, null);
		assertEquals("test-input", result);
		assertEquals(State.COMPLETED, report.getState());
		System.out.println(report);
	}

	private void waitForResult(Map<String, T2Reference> results, String port, WorkflowReport report)
			throws InterruptedException {
		int wait = 0;
		while (!results.containsKey(port) && wait++ < 10) {
			System.out.println(report);
			Thread.sleep(500);
		}
	}

}
