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

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.ServiceReference;

import org.apache.taverna.platform.execution.api.ExecutionEnvironment;
import org.apache.taverna.platform.report.State;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.platform.run.api.RunProfile;
import org.apache.taverna.platform.run.api.RunService;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;

public class RunParallelIT extends PlatformIT {

	private RunService runService;

	private ExecutionEnvironment executionEnvironment;

	protected void setup() throws Exception {
		super.setup();
		if (runService == null) {
			ServiceReference runServiceReference = bundleContext
					.getServiceReference("org.apache.taverna.platform.run.api.RunService");
			runService = (RunService) bundleContext.getService(runServiceReference);
		}
		if (executionEnvironment == null) {
			executionEnvironment = runService.getExecutionEnvironments().iterator().next();
		}
		credentialManager.addUsernameAndPasswordForService(new UsernamePassword("testuser",
				"testpasswd"), URI
				.create("http://heater.cs.man.ac.uk:7070/#Example+HTTP+BASIC+Authentication"));
		credentialManager
				.addUsernameAndPasswordForService(
						new UsernamePassword("testuser", "testpasswd"),
						URI.create("http://heater.cs.man.ac.uk:7070/axis/services/HelloService-PlaintextPassword?wsdl"));
	}

	public void testRun() throws Exception {
		setup();

		String[] workflows = { "biomart", "rest", "secure-basic-authentication",
				"spreadsheetimport", "stringconstant", "wsdl", "wsdl-secure", "xpath" };
		Set<String> runIDs= new HashSet<String>();

		for (int i = 0; i < workflows.length; i++) {
			int runs = Math.max(1, (int) Math.ceil(Math.random() * 10));
			System.out.println("Creating " + runs + " runs for workflow " + workflows[i]);
			for (int j = 0; j < runs; j++) {
				WorkflowBundle workflowBundle = loadWorkflow("/t2flow/" + workflows[i] + ".t2flow");
				String run = runService.createRun(new RunProfile(executionEnvironment, workflowBundle));
				runService.getWorkflowReport(run).getSubject().setName(workflows[i] + j);
				runIDs.add(run);
			}
		}

		for (String runId : runIDs) {
			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println("Starting " + report.getSubject().getName());
			runService.start(runId);
		}

		for (String runId : runIDs) {
			WorkflowReport report = runService.getWorkflowReport(runId);
			System.out.println("Waiting for " + report.getSubject().getName() + " to complete");
			assertTrue(waitForState(report, State.COMPLETED, false));
		}

	}

}
