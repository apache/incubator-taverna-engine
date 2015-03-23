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

import java.util.UUID;

import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.platform.report.ActivityReport;
import org.apache.taverna.platform.report.ProcessorReport;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * Abstract implementation of an {@link Execution}.
 *
 * @author David Withers
 */
public abstract class AbstractExecution implements Execution {

	private final String ID;
	private final WorkflowBundle workflowBundle;
	private final Bundle dataBundle;
	private final Workflow workflow;
	private final Profile profile;
	private final WorkflowReport workflowReport;

	private final Scufl2Tools scufl2Tools = new Scufl2Tools();

	/**
	 * Constructs an abstract implementation of an Execution.
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
	 * @throws InvalidWorkflowException
	 *             if the specified workflow is invalid
	 */
	public AbstractExecution(WorkflowBundle workflowBundle, Workflow workflow, Profile profile,
			Bundle dataBundle) {
		this.workflowBundle = workflowBundle;
		this.workflow = workflow;
		this.profile = profile;
		this.dataBundle = dataBundle;
		ID = UUID.randomUUID().toString();
		workflowReport = generateWorkflowReport(workflow);
	}

	protected abstract WorkflowReport createWorkflowReport(Workflow workflow);

	protected abstract ProcessorReport createProcessorReport(Processor processor);

	protected abstract ActivityReport createActivityReport(Activity activity);

	public WorkflowReport generateWorkflowReport(Workflow workflow) {
		WorkflowReport workflowReport = createWorkflowReport(workflow);
		for (Processor processor : workflow.getProcessors()) {
			ProcessorReport processorReport = createProcessorReport(processor);
			processorReport.setParentReport(workflowReport);
			workflowReport.addProcessorReport(processorReport);
			for (ProcessorBinding processorBinding : scufl2Tools.processorBindingsForProcessor(
					processor, profile)) {
				Activity boundActivity = processorBinding.getBoundActivity();
				ActivityReport activityReport = createActivityReport(boundActivity);
				activityReport.setParentReport(processorReport);
				if (scufl2Tools.containsNestedWorkflow(processor, profile)) {
					Workflow nestedWorkflow = scufl2Tools.nestedWorkflowForProcessor(processor,
							profile);
					WorkflowReport nestedWorkflowReport = generateWorkflowReport(nestedWorkflow);
					nestedWorkflowReport.setParentReport(activityReport);
					activityReport.setNestedWorkflowReport(nestedWorkflowReport);
				}
				processorReport.addActivityReport(activityReport);
			}
		}
		return workflowReport;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public WorkflowBundle getWorkflowBundle() {
		return workflowBundle;
	}

	@Override
	public Bundle getDataBundle() {
		return dataBundle;
	}

	@Override
	public Workflow getWorkflow() {
		return workflow;
	}

	@Override
	public Profile getProfile() {
		return profile;
	}

	@Override
	public WorkflowReport getWorkflowReport() {
		return workflowReport;
	}

}