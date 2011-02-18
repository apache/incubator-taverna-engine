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

		T2Reference reference = referenceService.register("test-input", 0, true, null);
		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

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

	public void testRunWSDL() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/wsdl.t2flow");

		T2Reference reference = referenceService.register("in", 0, true, null);
		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

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

		T2Reference reference = referenceService.register("test input", 0, true, null);
		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		inputs.put("in", reference);

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
