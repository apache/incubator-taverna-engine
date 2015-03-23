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

import java.util.Set;

import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * Service for executing Taverna workflows. There may be several <code>ExecutionService</code>s
 * available that offer different execution environments, e.g. one <code>ExecutionService</code> may
 * execute workflows on a remote server while another executes workflows on the local machine.
 *
 * @author David Withers
 */
public interface ExecutionService {

	/**
	 * Returns the identifier for this ExecutionService.
	 *
	 * @return the identifier for this ExecutionService
	 */
	public String getID();

	/**
	 * Returns the name of this ExecutionService.
	 *
	 * @return the name of this ExecutionService
	 */
	public String getName();

	/**
	 * Returns a description of this ExecutionService.
	 *
	 * @return a description of this ExecutionService
	 */
	public String getDescription();

	/**
	 * Returns the ExecutionEnvironments available for this ExecutionService.
	 *
	 * @return the ExecutionEnvironments available for this ExecutionService
	 */
	public Set<ExecutionEnvironment> getExecutionEnvironments();

	/**
	 * Creates a workflow execution and returns its ID.
	 *
	 * @param executionEnvironment
	 *            the {@link ExecutionEnvironment} used to execute the
	 *            <code>Workflow</code>
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the workflows required for execution
	 * @param workflow
	 *            the workflow to execute
	 * @param profile
	 *            the profile to use when executing the workflow
	 * @param dataBundle
	 *            the <code>Bundle</code> containing the data values for the <code>Workflow</code>
	 * @return the ID of the created workflow execution
	 * @throws InvalidWorkflowException
	 */
	public String createExecution(ExecutionEnvironment executionEnvironment, WorkflowBundle workflowBundle, Workflow workflow, Profile profile,
			Bundle dataBundle)
			throws InvalidWorkflowException;

	/**
	 * Returns the workflow report for the specified execution.
	 *
	 * @param executionID
	 *            the ID of the execution
	 * @return the workflow report for this execution
	 */
	public WorkflowReport getWorkflowReport(String executionID) throws InvalidExecutionIdException;

	/**
	 * Deletes the execution of a workflow.
	 *
	 * @param executionID
	 *            the ID of the execution to delete
	 * @throws InvalidExecutionIdException
	 *             if the execution ID is not valid
	 */
	public void delete(String executionID) throws InvalidExecutionIdException;

	/**
	 * Starts the execution of a workflow.
	 *
	 * @param executionID
	 *            the ID of the execution to start
	 * @throws InvalidExecutionIdException
	 *             if the execution ID is not valid
	 */
	public void start(String executionID) throws InvalidExecutionIdException;

	/**
	 * Pauses the execution of a workflow.
	 *
	 * @param executionID
	 *            the ID of the execution to pause
	 * @throws InvalidExecutionIdException
	 *             if the execution ID is not valid
	 */
	public void pause(String executionID) throws InvalidExecutionIdException;

	/**
	 * Resumes the execution of a paused workflow.
	 *
	 * @param executionID
	 *            the ID of the execution to resume
	 * @throws InvalidExecutionIdException
	 *             if the execution ID is not valid
	 */
	public void resume(String executionID) throws InvalidExecutionIdException;

	/**
	 * Cancels the execution of a workflow.
	 *
	 * @param executionID
	 *            the ID of the execution to cancel
	 * @throws InvalidExecutionIdException
	 *             if the execution ID is not valid
	 */
	public void cancel(String executionID) throws InvalidExecutionIdException;

}