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

import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.platform.run.api.RunProfile;
import uk.org.taverna.platform.run.api.RunService;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class RunIT extends PlatformIT {

	private ReferenceService referenceService;
	private ExecutionService executionService;
	private RunService runService;

	public void testRun() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/in-out.t2flow");

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test-input", 0, true, null));

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs,
				referenceService, executionService));
		assertEquals(State.CREATED, runService.getState(runId));
		runService.start(runId);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		assertNotNull(results);
		waitForResult(results, "out", runService.getWorkflowReport(runId));

		T2Reference resultReference = results.get("out");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		String result = (String) referenceService.renderIdentifier(resultReference, String.class,
				null);
		assertEquals("test-input", result);
		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(runService.getWorkflowReport(runId));
	}

	public void testRunBeanshell() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/beanshell.t2flow");

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("test-input", 0, true, null));

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs,
				referenceService, executionService));
		WorkflowReport report = runService.getWorkflowReport(runId);
		assertEquals(State.CREATED, runService.getState(runId));
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResult(results, "out", report);

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

	public void testRunLocalworker() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/localworker.t2flow");

		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", referenceService.register("Tom", 0, true, null));

		String runId = runService.createRun(new RunProfile(workflowBundle, inputs,
				referenceService, executionService));
		WorkflowReport report = runService.getWorkflowReport(runId);
		assertEquals(State.CREATED, runService.getState(runId));
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResult(results, "out", report);

		T2Reference resultReference = results.get("out");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		String result = (String) referenceService.renderIdentifier(resultReference, String.class,
				null);
		assertEquals("Hello Tom", result);
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
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		String result = (String) referenceService.renderIdentifier(resultReference, String.class,
				null);
		assertEquals("Test Value", result);
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
		WorkflowReport report = runService.getWorkflowReport(runId);
		assertEquals(State.CREATED, runService.getState(runId));
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		
		waitForResult(results, "cross", report);
		T2Reference resultReference = results.get("cross");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		List<List<String>> crossResult = (List<List<String>>) referenceService.renderIdentifier(resultReference,
				String.class, null);
		assertEquals(10, crossResult.size());
		assertEquals(10, crossResult.get(0).size());
		assertEquals(10, crossResult.get(5).size());
		assertEquals("test:0test:0", crossResult.get(0).get(0));
		assertEquals("test:0test:1", crossResult.get(0).get(1));
		assertEquals("test:4test:2", crossResult.get(4).get(2));
		assertEquals("test:7test:6", crossResult.get(7).get(6));

		waitForResult(results, "dot", report);
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


		waitForResult(results, "crossdot", report);
		resultReference = results.get("crossdot");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		List<List<String>> crossdotResult = (List<List<String>>) referenceService.renderIdentifier(resultReference,
				String.class, null);
		assertEquals(10, crossdotResult.size());
		assertEquals(10, crossdotResult.get(0).size());
		assertEquals(10, crossdotResult.get(5).size());
		assertEquals("test:0test:0test", crossdotResult.get(0).get(0));
		assertEquals("test:0test:1test", crossdotResult.get(0).get(1));
		assertEquals("test:4test:2test", crossdotResult.get(4).get(2));
		assertEquals("test:7test:6test", crossdotResult.get(7).get(6));

		waitForResult(results, "dotcross", report);
		resultReference = results.get("dotcross");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		List<String> dotcrossResult = (List<String>) referenceService.renderIdentifier(resultReference,
				String.class, null);
		assertEquals(10, dotResult.size());
		assertEquals("test:0test:0test", dotcrossResult.get(0));
		assertEquals("test:5test:5test", dotcrossResult.get(5));

		waitForResult(results, "dotdot", report);
		resultReference = results.get("dotdot");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		List<String> dotdotResult = (List<String>) referenceService.renderIdentifier(resultReference,
				String.class, null);
		assertEquals(10, dotResult.size());
		assertEquals("test:0test:0test:0", dotdotResult.get(0));
		assertEquals("test:5test:5test:5", dotdotResult.get(5));

		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	public void testRunWSDL() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/wsdl.t2flow");

		String runId = runService.createRun(new RunProfile(workflowBundle,
				referenceService, executionService));
		WorkflowReport report = runService.getWorkflowReport(runId);
		assertEquals(State.CREATED, runService.getState(runId));
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResult(results, "out", report);

		T2Reference resultReference = results.get("out");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		String result = (String) referenceService.renderIdentifier(resultReference, String.class,
				null);
		System.out.println(result);
		assertEquals("Apache Axis version: 1.4\nBuilt on Apr 22, 2006 (06:55:48 PDT)", result);
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
		WorkflowReport report = runService.getWorkflowReport(runId);
		assertEquals(State.CREATED, runService.getState(runId));
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResult(results, "out", report);

		T2Reference resultReference = results.get("out");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		String result = (String) referenceService.renderIdentifier(resultReference, String.class,
				null);
		System.out.println(result);
		assertEquals("John Smith (21) of 40, Oxford Road. Manchester.", result);
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
		WorkflowReport report = runService.getWorkflowReport(runId);
		assertEquals(State.CREATED, runService.getState(runId));
		System.out.println(report);

		runService.start(runId);
		assertEquals(State.RUNNING, runService.getState(runId));
		System.out.println(report);

		Map<String, T2Reference> results = runService.getOutputs(runId);
		waitForResult(results, "out", report);

		T2Reference resultReference = results.get("out");
		if (resultReference.containsErrors()) {
			printErrors(referenceService, resultReference);
		}
		assertFalse(resultReference.containsErrors());
		String result = (String) referenceService.renderIdentifier(resultReference, String.class,
				null);
		System.out.println(result);
		assertEquals("nested dataflow : test input", result);
		assertEquals(State.COMPLETED, runService.getState(runId));
		System.out.println(report);
	}

	private void setup() {
		ServiceReference referenceServiceReference = bundleContext
				.getServiceReference("net.sf.taverna.t2.reference.ReferenceService");
		referenceService = (ReferenceService) bundleContext.getService(referenceServiceReference);

		ServiceReference executionServiceReference = bundleContext
				.getServiceReference("uk.org.taverna.platform.execution.api.ExecutionService");
		executionService = (ExecutionService) bundleContext.getService(executionServiceReference);

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
