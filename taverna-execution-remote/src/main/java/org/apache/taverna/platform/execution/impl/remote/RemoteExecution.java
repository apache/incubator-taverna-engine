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

package org.apache.taverna.platform.execution.impl.remote;

import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.platform.execution.api.AbstractExecution;
import org.apache.taverna.platform.report.ActivityReport;
import org.apache.taverna.platform.report.ProcessorReport;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * An {@link org.apache.taverna.platform.execution.api.Execution Execution} for executing a Taverna workflow on a Taverna Server.
 *
 * @author David Withers
 */
public class RemoteExecution extends AbstractExecution {

	public RemoteExecution(WorkflowBundle workflowBundle, Workflow workflow, Profile profile,
			Bundle inputs) {
		super(workflowBundle, workflow, profile, inputs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected WorkflowReport createWorkflowReport(Workflow workflow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ProcessorReport createProcessorReport(Processor processor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ActivityReport createActivityReport(Activity activity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

}
