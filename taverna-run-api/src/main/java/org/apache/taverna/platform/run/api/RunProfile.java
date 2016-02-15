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

import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.platform.execution.api.ExecutionEnvironment;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * A <code>RunProfile</code> specifies the parameters required to run a
 * {@link org.apache.taverna.scufl2.api.core.Workflow}.
 * 
 */
public class RunProfile {
	private ExecutionEnvironment executionEnvironment;
	private WorkflowBundle workflowBundle;
	private Bundle dataBundle;
	private String workflowName;
	private String profileName;

	/**
	 * Constructs a <code>RunProfile</code> that specifies the parameters
	 * required to run a {@link org.apache.taverna.scufl2.api.core.Workflow}. The
	 * main <code>Workflow</code> and <code>Profile</code> from the
	 * <code>WorkflowBundle</code> are used.
	 * 
	 * @param executionEnvironment
	 *            the {@link ExecutionEnvironment} used to execute the
	 *            <code>Workflow</code>
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the
	 *            <code>Workflow</code> to run
	 * @param dataBundle
	 *            the <code>Bundle</code> containing the data values for the
	 *            <code>Workflow</code>
	 */
	public RunProfile(ExecutionEnvironment executionEnvironment,
			WorkflowBundle workflowBundle, Bundle dataBundle) {
		this(executionEnvironment, workflowBundle, null, null, dataBundle);
	}

	/**
	 * Constructs a <code>RunProfile</code> that specifies the parameters
	 * required to run a {@link org.apache.taverna.scufl2.api.core.Workflow}.
	 * 
	 * @param executionEnvironment
	 *            the {@link ExecutionEnvironment} used to execute the
	 *            <code>Workflow</code>
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the
	 *            <code>Workflow</code> to run
	 * @param workflow
	 *            the <code>Workflow</code> to run. If <code>null</code> uses
	 *            the main <code>Workflow</code> from the
	 *            <code>WorkflowBundle</code>
	 * @param profile
	 *            the {@link org.apache.taverna.scufl2.api.profiles.Profile} to use
	 *            when running the <code>Workflow</code>. If null uses the main
	 *            <code>Profile</code> from the <code>WorkflowBundle</code>
	 * @param dataBundle
	 *            the <code>Bundle</code> containing the data values for the
	 *            <code>Workflow</code>
	 */
	public RunProfile(ExecutionEnvironment executionEnvironment,
			WorkflowBundle workflowBundle, String workflowName,
			String profileName, Bundle dataBundle) {
		this.executionEnvironment = executionEnvironment;
		this.workflowBundle = workflowBundle;
		this.workflowName = workflowName;
		this.profileName = profileName;
		this.dataBundle = dataBundle;
	}

	/**
	 * Returns the <code>WorkflowBundle</code>.
	 * 
	 * @return the <code>WorkflowBundle</code>
	 */
	public WorkflowBundle getWorkflowBundle() {
		return workflowBundle;
	}

	/**
	 * Sets the <code>WorkflowBundle</code> containing the <code>Workflow</code>
	 * to run.
	 * 
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the
	 *            <code>Workflow</code> to run
	 */
	public void setWorkflowBundle(WorkflowBundle workflowBundle) {
		this.workflowBundle = workflowBundle;
	}

	/**
	 * Returns the name of the <code>Workflow</code> to run. If no
	 * <code>Workflow</code> name is set the main <code>Workflow</code> from the
	 * <code>WorkflowBundle</code> will be run.
	 * 
	 * @return the <code>Workflow</code> to run
	 */
	public String getWorkflowName() {
		if (workflowName == null && workflowBundle.getMainWorkflow() != null)
			return workflowBundle.getMainWorkflow().getName();
		return workflowName;
	}

	/**
	 * Sets the name of the <code>Workflow</code> to run. If no
	 * <code>Workflow</code> name is set the main <code>Workflow</code> from the
	 * <code>WorkflowBundle</code> will be run.
	 * 
	 * @param workflowName
	 *            the name of the <code>Workflow</code> to run
	 */
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	/**
	 * Returns the name of the <code>Profile</code> to use when running the
	 * <code>Workflow</code>. If no <code>Profile</code> name is set the main
	 * <code>Profile</code> from the <code>WorkflowBundle</code> will be used.
	 * 
	 * @return the <code>Profile</code> to use when running the
	 *         <code>Workflow</code>
	 */
	public String getProfileName() {
		if (profileName == null && workflowBundle.getMainProfile() != null) {
			return workflowBundle.getMainProfile().getName();
		}
		return profileName;
	}

	/**
	 * Sets the name of the <code>Profile</code> to use when running the
	 * <code>Workflow</code>.
	 * <p>
	 * If no <code>Profile</code> name is set the main <code>Profile</code> from
	 * the <code>WorkflowBundle</code> will be used.
	 * 
	 * @param profileName
	 *            the name of the <code>Profile</code> to use when running the
	 *            <code>Workflow</code>
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	/**
	 * Returns the <code>Bundle</code> containing the data values for the
	 * <code>Workflow</code>.
	 * 
	 * @return the <code>Bundle</code> containing the data values for the
	 *         <code>Workflow</code>
	 */
	public Bundle getDataBundle() {
		return dataBundle;
	}

	/**
	 * Sets the <code>Bundle</code> containing the data values for the
	 * <code>Workflow</code>.
	 * 
	 * @param dataBundle
	 *            the <code>Bundle</code> containing the data values for the
	 *            <code>Workflow</code>
	 */
	public void setDataBundle(Bundle dataBundle) {
		this.dataBundle = dataBundle;
	}

	/**
	 * Returns the <code>ExecutionEnvironment</code> used to execute the
	 * <code>Workflow</code>.
	 * 
	 * @return the <code>ExecutionEnvironment</code> used to execute the
	 *         <code>Workflow</code>
	 */
	public ExecutionEnvironment getExecutionEnvironment() {
		return executionEnvironment;
	}

	/**
	 * Sets the <code>ExecutionEnvironment</code> used to execute the
	 * <code>Workflow</code>.
	 * 
	 * @param executionEnvironment
	 *            the <code>ExecutionEnvironment</code> used to execute the
	 *            <code>Workflow</code>
	 */
	public void setExecutionEnvironment(
			ExecutionEnvironment executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}
}
