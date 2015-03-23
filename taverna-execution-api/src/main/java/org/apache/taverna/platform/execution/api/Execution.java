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

import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * Interface for a single execution of a Taverna workflow.
 *
 * @author David Withers
 */
public interface Execution {

	/**
	 * Returns the identifier for this execution.
	 *
	 * @return the identifier for this execution
	 */
	public abstract String getID();

	/**
	 * Returns the <code>WorkflowBundle</code> containing the <code>Workflow</code>s required for execution.
	 *
	 * @return the <code>WorkflowBundle</code> containing the <code>Workflow</code>s required for execution
	 */
	public abstract WorkflowBundle getWorkflowBundle();

	/**
	 * Returns the <code>Bundle</code> containing the data values for the <code>Workflow</code>.
	 *
	 * @return the <code>Bundle</code> containing the data values for the <code>Workflow</code>
	 */
	public abstract Bundle getDataBundle();

	/**
	 * Returns the <code>Workflow</code> to execute.
	 *
	 * @return the <code>Workflow</code> to execute
	 */
	public abstract Workflow getWorkflow();

	/**
	 * Returns the <code>Profile</code> to use when executing the <code>Workflow</code>.
	 *
	 * @return the <code>Profile</code> to use when executing the <code>Workflow</code>
	 */
	public abstract Profile getProfile();

	/**
	 * Returns the <code>WorkflowReport</code> for the execution.
	 *
	 * @return the <code>WorkflowReport</code> for the execution
	 */
	public abstract WorkflowReport getWorkflowReport();

	/**
	 * Deletes the execution.
	 */
	public abstract void delete();

	/**
	 * Starts the execution.
	 */
	public abstract void start();

	/**
	 * Pauses the execution.
	 */
	public abstract void pause();

	/**
	 * Resumes a paused execution.
	 */
	public abstract void resume();

	/**
	 * Cancels the execution.
	 */
	public abstract void cancel();

}