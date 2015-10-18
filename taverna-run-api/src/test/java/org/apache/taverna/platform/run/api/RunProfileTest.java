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

package org.apache.taverna.platform.run.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.platform.execution.api.ExecutionEnvironment;
import org.apache.taverna.platform.execution.impl.local.LocalExecutionEnvironment;
import org.apache.taverna.platform.execution.impl.local.LocalExecutionService;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 *
 *
 * @author David Withers
 */
@Ignore
public class RunProfileTest {

	private RunProfile runProfile;
	private ExecutionEnvironment executionEnvironment;
	private WorkflowBundle workflowBundle;
	private LocalExecutionService executionService;
	private Workflow workflow, mainWorkflow;
	private Profile profile, mainProfile;
	private Bundle dataBundle;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		workflow = new Workflow();
		mainWorkflow = new Workflow();
		profile = new Profile();
		mainProfile = new Profile();
		workflowBundle = new WorkflowBundle();
		workflowBundle.setMainProfile(mainProfile);
		workflowBundle.setMainWorkflow(mainWorkflow);
		executionService = new LocalExecutionService();
		executionEnvironment = new LocalExecutionEnvironment(executionService, null, null);

		dataBundle = DataBundles.createBundle();
		runProfile = new RunProfile(executionEnvironment, workflowBundle, workflow.getName(), profile.getName(), dataBundle);
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#RunProfile(org.apache.taverna.scufl2.api.container.WorkflowBundle, java.util.Map, org.apache.taverna.reference.ReferenceService, org.apache.taverna.platform.execution.api.ExecutionService)}
	 * .
	 */
	@Test
	public void testRunProfileWorkflowBundleMapOfStringT2ReferenceReferenceServiceExecutionService() {
		runProfile = new RunProfile(executionEnvironment, workflowBundle, dataBundle);
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#RunProfile(org.apache.taverna.scufl2.api.container.WorkflowBundle, org.apache.taverna.scufl2.api.core.Workflow, org.apache.taverna.scufl2.api.profiles.Profile, java.util.Map, org.apache.taverna.reference.ReferenceService, org.apache.taverna.platform.execution.api.ExecutionService)}
	 * .
	 */
	@Test
	public void testRunProfileWorkflowBundleWorkflowProfileMapOfStringT2ReferenceReferenceServiceExecutionService() {
		runProfile = new RunProfile(executionEnvironment, workflowBundle, workflow.getName(), profile.getName(), dataBundle);
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#getWorkflowBundle()}.
	 */
	@Test
	public void testGetWorkflowBundle() {
		assertNotNull(runProfile.getWorkflowBundle());
		assertEquals(workflowBundle, runProfile.getWorkflowBundle());
		assertEquals(runProfile.getWorkflowBundle(), runProfile.getWorkflowBundle());
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#setWorkflowBundle(org.apache.taverna.scufl2.api.container.WorkflowBundle)}
	 * .
	 */
	@Test
	public void testSetWorkflowBundle() {
		runProfile.setWorkflowBundle(null);
		assertNull(runProfile.getWorkflowBundle());
		runProfile.setWorkflowBundle(workflowBundle);
		assertEquals(workflowBundle, runProfile.getWorkflowBundle());
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#getWorkflow()}.
	 */
	@Test
	public void testGetWorkflow() {
		assertNotNull(runProfile.getWorkflowName());
		assertEquals(workflow.getName(), runProfile.getWorkflowName());
		assertEquals(runProfile.getWorkflowName(), runProfile.getWorkflowName());
		runProfile.setWorkflowName(null);
		assertNotNull(runProfile.getWorkflowName());
		assertEquals(mainWorkflow.getName(), runProfile.getWorkflowName());
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#setWorkflow(org.apache.taverna.scufl2.api.core.Workflow)}
	 * .
	 */
	@Test
	public void testSetWorkflow() {
		runProfile.setWorkflowName(null);
		assertNotNull(runProfile.getWorkflowName());
		assertEquals(mainWorkflow.getName(), runProfile.getWorkflowName());
		runProfile.setWorkflowBundle(new WorkflowBundle());
		runProfile.setWorkflowName(null);
		assertNull(runProfile.getWorkflowName());
		runProfile.setWorkflowName(workflow.getName());
		assertEquals(workflow.getName(), runProfile.getWorkflowName());
		runProfile.setWorkflowName(mainWorkflow.getName());
		assertEquals(mainWorkflow.getName(), runProfile.getWorkflowName());
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#getProfile()}.
	 */
	@Test
	public void testGetProfile() {
		assertNotNull(runProfile.getProfileName());
		assertEquals(profile.getName(), runProfile.getProfileName());
		assertEquals(runProfile.getProfileName(), runProfile.getProfileName());
		runProfile.setProfileName(null);
		assertNotNull(runProfile.getProfileName());
		assertEquals(mainProfile.getName(), runProfile.getProfileName());
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#setProfile(org.apache.taverna.scufl2.api.profiles.Profile)}
	 * .
	 */
	@Test
	public void testSetProfile() {
		runProfile.setProfileName(null);
		assertNotNull(runProfile.getProfileName());
		assertEquals(mainProfile.getName(), runProfile.getProfileName());
		runProfile.setWorkflowBundle(new WorkflowBundle());
		runProfile.setProfileName(null);
		assertNull(runProfile.getProfileName());
		runProfile.setProfileName(profile.getName());
		assertEquals(profile.getName(), runProfile.getProfileName());
		runProfile.setProfileName(mainProfile.getName());
		assertEquals(mainProfile.getName(), runProfile.getProfileName());
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#getDataBundle()}.
	 */
	@Test
	public void testGetDataBundle() {
		assertNotNull(runProfile.getDataBundle());
		assertEquals(dataBundle, runProfile.getDataBundle());
		assertEquals(runProfile.getDataBundle(), runProfile.getDataBundle());
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#setDataBundle(org.apache.taverna.robundle.Bundle)}
	 * .
	 */
	@Test
	public void testSetDataBundle() {
		runProfile.setDataBundle(null);
		assertNull(runProfile.getDataBundle());
		runProfile.setDataBundle(dataBundle);
		assertEquals(dataBundle, runProfile.getDataBundle());
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#getExecutionEnvironment()}.
	 */
	@Test
	public void testGetExecutionEnvironment() {
		assertNotNull(runProfile.getExecutionEnvironment());
		assertEquals(executionEnvironment, runProfile.getExecutionEnvironment());
		assertEquals(runProfile.getExecutionEnvironment(), runProfile.getExecutionEnvironment());
	}

	/**
	 * Test method for
	 * {@link org.apache.taverna.platform.run.api.RunProfile#setExecutionEnvironment(org.apache.taverna.platform.execution.api.ExecutionEnvironment)}
	 * .
	 */
	@Test
	public void testSetExecutionEnvironment() {
		runProfile.setExecutionEnvironment(null);
		assertNull(runProfile.getExecutionEnvironment());
		runProfile.setExecutionEnvironment(executionEnvironment);
		assertEquals(executionEnvironment, runProfile.getExecutionEnvironment());
	}

}
