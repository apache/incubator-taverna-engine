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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * A common super type for concrete implementations of <code>ExecutionService</code>s.
 *
 * @author David Withers
 */
public abstract class AbstractExecutionService implements ExecutionService {
	private final String ID;
	private final String name;
	private final String description;
	private final Map<String, Execution> executionMap;

	public AbstractExecutionService(String ID, String name, String description) {
		this.ID = ID;
		this.name = name;
		this.description = description;
		executionMap = Collections.synchronizedMap(new HashMap<String, Execution>());
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String createExecution(ExecutionEnvironment executionEnvironment,
			WorkflowBundle workflowBundle, Workflow workflow, Profile profile,
			Bundle dataBundle) throws InvalidWorkflowException {
		Execution execution = createExecutionImpl(workflowBundle, workflow, profile, dataBundle);
		executionMap.put(execution.getID(), execution);
		return execution.getID();
	}

	/**
	 * Creates an implementation of an Execution.
	 *
	 * To be implemented by concrete implementations of <code>ExecutionService</code>.
	 *
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the <code>Workflow</code>s required for
	 *            execution
	 * @param workflow
	 *            the <code>Workflow</code> to execute
	 * @param profile
	 *            the <code>Profile</code> to use when executing the <code>Workflow</code>
	 * @param dataBundle
	 *            the <code>Bundle</code> containing the data values for the <code>Workflow</code>
	 * @return a new Execution implementation
	 * @throws InvalidWorkflowException
	 *             if the specified workflow is invalid
	 */
	protected abstract Execution createExecutionImpl(
			WorkflowBundle workflowBundle, Workflow workflow, Profile profile,
			Bundle dataBundle) throws InvalidWorkflowException;

	@Override
	public WorkflowReport getWorkflowReport(String executionID)
			throws InvalidExecutionIdException {
		return getExecution(executionID).getWorkflowReport();
	}

	@Override
	public void delete(String executionID) throws InvalidExecutionIdException {
		getExecution(executionID).delete();
		executionMap.remove(executionID);
	}

	@Override
	public void start(String executionID) throws InvalidExecutionIdException {
		getExecution(executionID).start();
	}

	@Override
	public void pause(String executionID) throws InvalidExecutionIdException {
		getExecution(executionID).pause();
	}

	@Override
	public void resume(String executionID) throws InvalidExecutionIdException {
		getExecution(executionID).resume();
	}

	@Override
	public void cancel(String executionID) throws InvalidExecutionIdException {
		getExecution(executionID).cancel();
	}

	protected Execution getExecution(String executionID)
			throws InvalidExecutionIdException {
		Execution execution = executionMap.get(executionID);
		if (execution == null)
			throw new InvalidExecutionIdException("Execution ID " + executionID
					+ " is not valid");
		return execution;
	}

}
