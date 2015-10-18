/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.platform.execution.impl.local;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import org.apache.taverna.platform.report.WorkflowReport;

/**
 * 
 * @author David Withers
 */
public class LocalExecutionTest {
	
	/**
	 * Test method for {@link org.apache.taverna.platform.execution.impl.local.LocalExecution#start()}.
	 */
	@Test
	@Ignore
	public void testStart() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.execution.impl.local.LocalExecution#pause()}.
	 */
	@Test
	@Ignore
	public void testPause() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.execution.impl.local.LocalExecution#resume()}.
	 */
	@Test
	@Ignore
	public void testResume() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.execution.impl.local.LocalExecution#cancel()}.
	 */
	@Test
	@Ignore
	public void testCancel() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.execution.impl.local.LocalExecution#DataflowExecution(org.apache.taverna.scufl2.api.core.Workflow, java.util.Map, net.sf.taverna.t2.reference.ReferenceService)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDataflowExecution() throws Exception {
		// URL wfResource = getClass().getResource("/t2flow/in-out.t2flow");
		// assertNotNull(wfResource);
		// TavernaResearchObject researchObject = new
		// T2FlowParser().parseT2Flow(wfResource.openStream());
		// Workflow workflow = researchObject.getMainWorkflow();
		// Profile profile = researchObject.getProfiles().iterator().next();
		//
		// T2Reference reference = context.getReferenceService().register("test-input", 0, true,
		// context);
		// Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		// inputs.put("in", reference);
		//
		// DataflowExecution execution = new DataflowExecution(workflow, profile, inputs,
		// context.getReferenceService());
		// WorkflowReport report = execution.getWorkflowReport();
		// assertEquals(State.CREATED, report.getState());
		// execution.start();
		//
		// Map<String, Object> results = execution.getResults();
		// waitForResult(results, "out", report);
		//
		// String result = (String) context.getReferenceService().renderIdentifier((T2Reference)
		// results.get("out"), String.class, context);
		// assertEquals("test-input", result);
		// assertEquals(State.COMPLETED, report.getState());
		// System.out.println(report);
	}

	// @Test
	// // @Ignore
	// public void testDataflowExecution2() throws Exception {
	// URL wfResource = getClass().getResource("/t2flow/beanshell.t2flow");
	// assertNotNull(wfResource);
	// T2FlowParser t2FlowParser = new T2FlowParser();
	// t2FlowParser.setStrict(true);
	// WorkflowBundle researchObject = t2FlowParser.parseT2Flow(wfResource.openStream());
	// Workflow workflow = researchObject.getMainWorkflow();
	// Profile profile = researchObject.getProfiles().iterator().next();
	//
	// InvocationContext context = null;
	// T2Reference reference = context.getReferenceService().register("test-input", 0, true,
	// context);
	// Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
	// inputs.put("in", reference);
	//
	// LocalExecution execution = new LocalExecution(workflow, profile, inputs,
	// context.getReferenceService(), new EditsImpl());
	// WorkflowReport report = execution.getWorkflowReport();
	// System.out.println(report);
	// assertEquals(State.CREATED, report.getState());
	// execution.start();
	// System.out.println(report);
	//
	// Map<String, Object> results = execution.getResults();
	// waitForResult(results, "out", report);
	//
	// List<String> result = (List<String>) context.getReferenceService().renderIdentifier(
	// (T2Reference) results.get("out"), String.class, context);
	// assertEquals(1000, result.size());
	// assertEquals("test-input:0", result.get(0));
	// assertEquals(State.COMPLETED, report.getState());
	// System.out.println(report);
	// }

	@SuppressWarnings("unused")
	private void waitForResult(Map<String, Object> results, String port, WorkflowReport report)
			throws InterruptedException {
		int wait = 0;
		while (!results.containsKey(port) && wait++ < 10) {
			System.out.println(report);
			Thread.sleep(500);
		}
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.execution.impl.local.LocalExecution#resultTokenProduced(net.sf.taverna.t2.invocation.WorkflowDataToken, java.lang.String)}
	 * .
	 */
	@Test
	public void testResultTokenProduced() {
		// fail("Not yet implemented");
	}

}
