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

package org.apache.taverna.platform.execution.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.platform.report.ActivityReport;
import org.apache.taverna.platform.report.ProcessorReport;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * @author David Withers
 */
@Ignore
public class AbstractExecutionTest {
	private WorkflowBundle workflowBundle;
	private Execution execution;
	private Workflow workflow;
	private Profile profile;
	private Bundle dataBundle;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		workflowBundle = new WorkflowBundle();
		workflow = new Workflow();
		profile = new Profile();
		dataBundle = DataBundles.createBundle();
		execution = new AbstractExecution(workflowBundle, workflow, profile, dataBundle) {
			@Override
			public void start() {}
			@Override
			public void resume() {}
			@Override
			public void pause() {}
			@Override
			public void cancel() {}
			@Override
			public void delete() {}
			@Override
			protected WorkflowReport createWorkflowReport(Workflow workflow) {
				return new WorkflowReport(workflow) {
				};
			}
			@Override
			public ProcessorReport createProcessorReport(Processor processor) {
				return null;
			}
			@Override
			public ActivityReport createActivityReport(Activity activity) {
				return null;
			}
		};
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.execution.api.AbstractExecution#getID()}.
	 */
	@Test
	public void testGetID() {
		assertNotNull(execution.getID());
		assertEquals(execution.getID(), execution.getID());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.execution.api.AbstractExecution#getWorkflowBundle()}.
	 */
	@Test
	public void testGetWorkflowBundle() {
		assertEquals(workflowBundle, execution.getWorkflowBundle());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.execution.api.AbstractExecution#getWorkflow()}.
	 */
	@Test
	public void testGetWorkflow() {
		assertEquals(workflow, execution.getWorkflow());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.execution.api.AbstractExecution#getInputs()}.
	 */
	@Test
	public void testGetInputs() {
		assertEquals(dataBundle, execution.getDataBundle());
	}

	/**
	 * Test method for {@link org.apache.taverna.platform.execution.api.AbstractExecution#getWorkflowReport()}.
	 */
	@Test
	public void testGetWorkflowReport() {
		assertNotNull(execution.getWorkflowReport());
	}
}
