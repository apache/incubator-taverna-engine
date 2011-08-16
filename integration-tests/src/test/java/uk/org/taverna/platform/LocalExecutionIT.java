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

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.platform.run.api.RunService;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class LocalExecutionIT extends PlatformIT {

	private ReferenceService referenceService;
	private ExecutionService executionService;

	public void testLocalExecution() throws Exception {
		setup();

		URL wfResource = getClass().getResource("/t2flow/in-out.t2flow");
		assertNotNull(wfResource);
		WorkflowBundle workflowBundle = new T2FlowParser().parseT2Flow(wfResource.openStream());
		Workflow workflow = workflowBundle.getMainWorkflow();
		Profile profile = workflowBundle.getProfiles().iterator().next();

		T2Reference reference = referenceService.register("test-input", 0, true, null);

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

		String executionId = executionService.createExecution(workflowBundle, workflow, profile,
				inputs, referenceService);
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
		setup();

		URL wfResource = getClass().getResource("/t2flow/beanshell.t2flow");
		assertNotNull(wfResource);
		T2FlowParser t2FlowParser = new T2FlowParser();
		t2FlowParser.setStrict(true);
		WorkflowBundle workflowBundle = t2FlowParser.parseT2Flow(wfResource.openStream());
		Workflow workflow = workflowBundle.getMainWorkflow();
		Profile profile = workflowBundle.getProfiles().iterator().next();

		T2Reference reference = referenceService.register("test-input", 0, true, null);
		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

		String executionId = executionService.createExecution(workflowBundle, workflow, profile,
				inputs, referenceService);
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
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) referenceService.renderIdentifier(results.get("out"),
				String.class, null);
		assertEquals(1000, result.size());
		assertEquals("test-input:0", result.get(0));
		assertEquals(State.COMPLETED, report.getState());
		System.out.println(report);
	}

	public void testLocalExecution3() throws Exception {
		setup();

		URL wfResource = getClass().getResource("/t2flow/beanshell.t2flow");
		assertNotNull(wfResource);
		T2FlowParser t2FlowParser = new T2FlowParser();
		t2FlowParser.setStrict(true);
		WorkflowBundle workflowBundle = t2FlowParser.parseT2Flow(wfResource.openStream());
		Workflow workflow = workflowBundle.getMainWorkflow();
		Profile profile = workflowBundle.getProfiles().iterator().next();

		T2Reference reference = referenceService.register("test-input", 0, true, null);
		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

		String executionId = executionService.createExecution(workflowBundle, workflow, profile,
				inputs, referenceService);
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
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) referenceService.renderIdentifier(results.get("out"),
				String.class, null);
		assertEquals(1000, result.size());
		assertEquals("test-input:0", result.get(0));
		assertEquals(State.COMPLETED, report.getState());
		System.out.println(report);
	}

	private void setup() throws InvalidSyntaxException {
		ServiceReference[] referenceServiceReferences = bundleContext.getServiceReferences(
				"net.sf.taverna.t2.reference.ReferenceService",
		"(org.springframework.osgi.bean.name=inMemoryReferenceService)");
		assertEquals(1, referenceServiceReferences.length);
		referenceService = (ReferenceService) bundleContext
				.getService(referenceServiceReferences[0]);

		ServiceReference[] executionServiceReferences = bundleContext.getServiceReferences(
				"uk.org.taverna.platform.execution.api.ExecutionService",
				"(org.springframework.osgi.bean.name=localExecution)");
		assertEquals(1, executionServiceReferences.length);
		executionService = (ExecutionService) bundleContext
				.getService(executionServiceReferences[0]);

	}

}