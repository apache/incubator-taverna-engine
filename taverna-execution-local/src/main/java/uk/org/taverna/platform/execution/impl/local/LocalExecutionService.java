/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package uk.org.taverna.platform.execution.impl.local;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Edits;

import org.apache.taverna.robundle.Bundle;

import uk.org.taverna.platform.capability.api.ActivityService;
import uk.org.taverna.platform.capability.api.DispatchLayerService;
import uk.org.taverna.platform.execution.api.AbstractExecutionService;
import uk.org.taverna.platform.execution.api.Execution;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.execution.api.WorkflowCompiler;
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
