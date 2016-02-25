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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.ServiceReference;

import org.apache.taverna.platform.execution.api.AbstractExecutionEnvironment;
import org.apache.taverna.platform.execution.api.AbstractExecutionService;
import org.apache.taverna.platform.execution.api.Execution;
import org.apache.taverna.platform.execution.api.ExecutionEnvironment;
import org.apache.taverna.platform.execution.api.ExecutionEnvironmentService;
import org.apache.taverna.platform.execution.api.InvalidWorkflowException;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

public class ExecutionIT extends PlatformIT {

	private ExecutionEnvironmentService executionEnvironmentService;

	protected void setup() throws Exception {
		super.setup();
		ServiceReference[] executionServiceReferences = bundleContext.getServiceReferences(
				"org.apache.taverna.platform.execution.api.ExecutionEnvironmentService", null);
		assertEquals(1, executionServiceReferences.length);
		executionEnvironmentService = (ExecutionEnvironmentService) bundleContext
				.getService(executionServiceReferences[0]);

	}

	public void testGetExecutionEnvironments() throws Exception {
		setup();

		Set<ExecutionEnvironment> executionEnvironments = executionEnvironmentService
				.getExecutionEnvironments();
		int size = executionEnvironments.size();

		bundleContext.registerService("org.apache.taverna.platform.execution.api.ExecutionService",
				new AbstractExecutionService("test id", "test name", "test description") {
					public Set<ExecutionEnvironment> getExecutionEnvivonments() {
						return Collections
								.<ExecutionEnvironment> singleton(new AbstractExecutionEnvironment(
										"test id", "test name", "test description", this) {
									public List<URI> getDispatchLayerURIs() {
										return Collections.singletonList(URI
												.create("http://ns.taverna.org.uk/2010/dispatchlayer/testDispatchLayer"));
									}

									public List<URI> getActivityURIs() {
										return Collections.singletonList(URI
												.create("http://ns.taverna.org.uk/2010/activity/testActivity"));
									}

									public boolean dispatchLayerExists(URI uri) {
										return false;
									}

									public boolean activityExists(URI uri) {
										return false;
									}

									public ConfigurationDefinition getActivityConfigurationDefinition(
											URI uri) throws ActivityNotFoundException,
											ActivityConfigurationException {
										return null;
									}

									public ConfigurationDefinition getDispatchLayerConfigurationDefinition(
											URI uri) throws DispatchLayerNotFoundException,
											DispatchLayerConfigurationException {
										return null;
									}
								});
					}

					protected Execution createExecutionImpl(WorkflowBundle workflowBundle,
							Workflow workflow, Profile profile, Map<String, Data> inputs) throws InvalidWorkflowException {
						return null;
					}
				}, null);


		executionEnvironments = executionEnvironmentService
				.getExecutionEnvironments();
		assertEquals(size + 1, executionEnvironments.size());

		for (ExecutionEnvironment executionEnvironment : executionEnvironments) {
			System.out.println(executionEnvironment);
		}
	}

	public void testGetExecutionEnvironmentsProfile() throws Exception {
		setup();

		WorkflowBundle workflowBundle = loadWorkflow("/t2flow/beanshell.t2flow");

		Set<ExecutionEnvironment> executionEnvironments = executionEnvironmentService
				.getExecutionEnvironments(workflowBundle.getMainProfile());
		assertTrue(executionEnvironments.size() > 0);

		System.out.println(executionEnvironments.iterator().next());
	}

}
