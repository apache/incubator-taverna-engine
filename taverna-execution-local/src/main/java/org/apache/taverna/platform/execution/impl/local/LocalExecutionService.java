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

package org.apache.taverna.platform.execution.impl.local;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.Edits;

import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.platform.capability.api.ActivityService;
import org.apache.taverna.platform.capability.api.DispatchLayerService;
import org.apache.taverna.platform.execution.api.AbstractExecutionService;
import org.apache.taverna.platform.execution.api.Execution;
import org.apache.taverna.platform.execution.api.ExecutionEnvironment;
import org.apache.taverna.platform.execution.api.InvalidWorkflowException;
import org.apache.taverna.platform.execution.api.WorkflowCompiler;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * Service for executing Taverna workflows on a local Taverna Dataflow Engine.
 *
 * @author David Withers
 */
public class LocalExecutionService extends AbstractExecutionService implements
		WorkflowCompiler {
	private Edits edits;
	private ActivityService activityService;
	private DispatchLayerService dispatchLayerService;
	private ReferenceService referenceService;

	/**
	 * Constructs an execution service that executes workflows using the T2
	 * dataflow engine.
	 */
	public LocalExecutionService() {
		super(
				LocalExecutionService.class.getName(),
				"Taverna Local Execution Service",
				"Execution Service for executing Taverna workflows using a local Taverna Dataflow Engine");
	}

	@Override
	public Set<ExecutionEnvironment> getExecutionEnvironments() {
		Set<ExecutionEnvironment> executionEnvironments = new HashSet<>();
		executionEnvironments.add(new LocalExecutionEnvironment(this,
				activityService, dispatchLayerService));
		return executionEnvironments;
	}

	@Override
	protected Execution createExecutionImpl(WorkflowBundle workflowBundle,
			Workflow workflow, Profile profile, Bundle dataBundle)
			throws InvalidWorkflowException {
		return new LocalExecution(workflowBundle, workflow, profile,
				dataBundle, referenceService, edits, activityService,
				dispatchLayerService);
	}

	/**
	 * Sets the Edits Service for creating Taverna Dataflows.
	 *
	 * @param edits
	 *            the Edits Service for creating Taverna Dataflows
	 */
	public void setEdits(Edits edits) {
		this.edits = edits;
	}

	/**
	 * Sets the service for creating activities.
	 *
	 * @param activityService
	 *            the service for creating activities
	 */
	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	/**
	 * Sets the service for creating dispatch layers.
	 *
	 * @param dispatchLayerService
	 *            the service for creating dispatch layers
	 */
	public void setDispatchLayerService(DispatchLayerService dispatchLayerService) {
		this.dispatchLayerService = dispatchLayerService;
	}

	/**
	 * Sets the reference service.
	 *
	 * @param referenceService
	 *            the reference service
	 */
	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}

	private WeakHashMap<URI, WorkflowToDataflowMapper> cache = new WeakHashMap<>();

	private synchronized WorkflowToDataflowMapper getMapper(
			WorkflowBundle bundle) {
		WorkflowToDataflowMapper m = cache.get(bundle.getIdentifier());
		if (m == null) {
			m = new WorkflowToDataflowMapper(bundle, bundle.getMainProfile(),
					edits, activityService, dispatchLayerService);
			cache.put(bundle.getIdentifier(), m);
		}
		return m;
	}

	@Override
	public Dataflow getDataflow(Workflow workflow)
			throws InvalidWorkflowException {
		return getMapper(workflow.getParent()).getDataflow(workflow);
	}

	@Override
	public synchronized Dataflow getDataflow(WorkflowBundle bundle)
			throws InvalidWorkflowException {
		return getMapper(bundle).getDataflow(bundle.getMainWorkflow());
	}
}
