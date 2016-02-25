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
package org.apache.taverna.platform;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.ServiceReference;

import org.apache.taverna.platform.execution.api.ExecutionEnvironment;
import org.apache.taverna.platform.execution.api.ExecutionService;
import org.apache.taverna.platform.report.State;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

public class LocalExecutionIT extends PlatformIT {

	private ExecutionService executionService;
	private Set<ExecutionEnvironment> executionEnvironments;

	protected void setup() throws Exception {
		super.setup();
		if (executionService == null) {
			ServiceReference[] executionServiceReferences = bundleContext.getServiceReferences(
					"org.apache.taverna.platform.execution.api.ExecutionService",
					"(org.springframework.osgi.bean.name=localExecution)");
			assertEquals(1, executionServiceReferences.length);
			executionService = (ExecutionService) bundleContext
					.getService(executionServiceReferences[0]);
			executionEnvironments = executionService.getExecutionEnvivonments();
			assertEquals(1, executionEnvironments.size());
		}
		if (dataService == null) {
			ServiceReference dataServiceReference = bundleContext
					.getServiceReference("org.apache.taverna.platform.data.DataService");
			dataService = (DataService) bundleContext.getService(dataServiceReference);
		}
	}

	public void testLocalExecution() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/in-out.t2flow");
		Workflow workflow = workflowBundle.getMainWorkflow();
		Profile profile = workflowBundle.getMainProfile();
		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			Map<String, Data> inputs = Collections.singletonMap("in", dataService.create("test-input"));

			String executionId = executionService.createExecution(executionEnvironment,
					workflowBundle, workflow, profile, inputs);
			WorkflowReport report = executionService.getWorkflowReport(executionId);
			assertEquals(State.CREATED, report.getState());
			executionService.start(executionId);

			Map<String, Data> results = report.getOutputs();
			assertNotNull(results);
			waitForResults(results, report, "out");

			Object result = results.get("out").getValue();
			assertEquals("test-input", result);
			assertEquals(State.COMPLETED, report.getState());
			System.out.println(report);
		}
	}

	public void testLocalExecution2() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/beanshell.t2flow");
		Workflow workflow = workflowBundle.getMainWorkflow();
		Profile profile = workflowBundle.getMainProfile();

		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			Map<String, Data> inputs = Collections.singletonMap("in", dataService.create("test-input"));

			String executionId = executionService.createExecution(executionEnvironment,
					workflowBundle, workflow, profile, inputs);
			WorkflowReport report = executionService.getWorkflowReport(executionId);
			System.out.println(report);
			assertEquals(State.CREATED, report.getState());
			executionService.start(executionId);
			System.out.println(report);

			Map<String, Data> results = report.getOutputs();
			waitForResults(results, report, "out");

			List<Data> result = results.get("out").getElements();
			assertEquals(1000, result.size());
			assertEquals("test-input:0", result.get(0).getValue());
			assertEquals(State.COMPLETED, report.getState());
			System.out.println(report);
		}
	}

}
