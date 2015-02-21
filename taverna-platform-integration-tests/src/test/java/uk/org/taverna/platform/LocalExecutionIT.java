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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.ServiceReference;

import uk.org.taverna.platform.data.api.Data;
import uk.org.taverna.platform.data.api.DataService;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class LocalExecutionIT extends PlatformIT {

	private ExecutionService executionService;
	private DataService dataService;
	private Set<ExecutionEnvironment> executionEnvironments;

	protected void setup() throws Exception {
		super.setup();
		if (executionService == null) {
			ServiceReference[] executionServiceReferences = bundleContext.getServiceReferences(
					"uk.org.taverna.platform.execution.api.ExecutionService",
					"(org.springframework.osgi.bean.name=localExecution)");
			assertEquals(1, executionServiceReferences.length);
			executionService = (ExecutionService) bundleContext
					.getService(executionServiceReferences[0]);
			executionEnvironments = executionService.getExecutionEnvivonments();
			assertEquals(1, executionEnvironments.size());
		}
		if (dataService == null) {
			ServiceReference dataServiceReference = bundleContext
					.getServiceReference("uk.org.taverna.platform.data.DataService");
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
