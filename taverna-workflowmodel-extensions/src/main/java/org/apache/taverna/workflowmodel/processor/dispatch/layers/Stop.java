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

package org.apache.taverna.workflowmodel.processor.dispatch.layers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.reference.WorkflowRunIdEntity;
import org.apache.taverna.workflowmodel.ConfigurationException;
import org.apache.taverna.workflowmodel.processor.dispatch.AbstractDispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobQueueEvent;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This layer allows for the cancellation, pausing and resuming of workflow
 * runs. It does so by intercepting jobs sent to the layer.
 *
 * @author alanrw
 */
public class Stop extends AbstractDispatchLayer<JsonNode> {
	public static final String URI = "http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Stop";
	/**
	 * The set of ids of workflow runs that have been cancelled.
	 */
	private static Set<String> cancelledWorkflowRuns = new HashSet<>();
	/**
	 * A map from workflow run ids to the set of Stop layers where jobs have
	 * been intercepted for that run.
	 */
	private static Map<String, Set<Stop>> pausedLayerMap = new HashMap<>();
	/**
	 * A map for a given Stop from ids of suspended workflow runs to the jobs
	 * that have been intercepted.
	 */
	private Map<String, Set<DispatchJobEvent>> suspendedJobEventMap = new HashMap<>();

	@Override
	public void configure(JsonNode conf) throws ConfigurationException {
		// nothing
	}

	@Override
	public JsonNode getConfiguration() {
		return null;
	}

	@Override
	public void receiveJob(final DispatchJobEvent jobEvent) {
		List<WorkflowRunIdEntity> entities = jobEvent.getContext().getEntities(
				WorkflowRunIdEntity.class);
		if (entities != null && !entities.isEmpty()) {
			final String wfRunId = entities.get(0).getWorkflowRunId();
			// If the workflow run is cancelled then simply "eat" the jobEvent.
			// This does a hard-cancel.
			if (cancelledWorkflowRuns.contains(wfRunId))
				return;
			// If the workflow run is paused
			if (pausedLayerMap.containsKey(wfRunId))
				synchronized (Stop.class) {
					// double check as pausedLayerMap may have been changed
					// waiting for the lock
					if (pausedLayerMap.containsKey(wfRunId)) {
						// Remember that this Stop layer was affected by the
						// workflow pause
						pausedLayerMap.get(wfRunId).add(this);
						if (!suspendedJobEventMap.containsKey(wfRunId))
							suspendedJobEventMap.put(wfRunId,
									new HashSet<DispatchJobEvent>());
						// Remember the suspended jobEvent
						suspendedJobEventMap.get(wfRunId).add(jobEvent);
						return;
					}
				}
		}
		// By default pass the jobEvent down to the next layer
		super.receiveJob(jobEvent);
	}

	@Override
	public void receiveJobQueue(DispatchJobQueueEvent jobQueueEvent) {
		super.receiveJobQueue(jobQueueEvent);
	}

	/**
	 * Cancel the workflow run with the specified id
	 *
	 * @param workflowRunId
	 *            The id of the workflow run to cancel
	 * @return If the workflow run was cancelled then true. If it was already
	 *         cancelled then false.
	 */
	public static synchronized boolean cancelWorkflow(String workflowRunId) {
		if (cancelledWorkflowRuns.contains(workflowRunId))
			return false;
		Set<String> cancelledWorkflowRunsCopy = new HashSet<>(
				cancelledWorkflowRuns);
		cancelledWorkflowRunsCopy.add(workflowRunId);
		cancelledWorkflowRuns = cancelledWorkflowRunsCopy;
		return true;
	}

	/**
	 * Pause the workflow run with the specified id
	 *
	 * @param workflowRunId
	 *            The id of the workflow run to pause
	 * @return If the workflow run was paused then true. If it was already
	 *         paused or cancelled then false.
	 */
	public static synchronized boolean pauseWorkflow(String workflowRunId) {
		if (cancelledWorkflowRuns.contains(workflowRunId))
			return false;
		if (pausedLayerMap.containsKey(workflowRunId))
			return false;
		Map<String, Set<Stop>> pausedLayerMapCopy = new HashMap<>(pausedLayerMap);
		pausedLayerMapCopy.put(workflowRunId, new HashSet<Stop>());
		pausedLayerMap = pausedLayerMapCopy;
		return true;
	}

	/**
	 * Resume the workflow run with the specified id
	 *
	 * @param workflowRunId
	 *            The id of the workflow run to resume
	 * @return If the workflow run was resumed then true. If the workflow run
	 *         was not paused or it was cancelled, then false.
	 */
	public static synchronized boolean resumeWorkflow(String workflowRunId) {
		if (cancelledWorkflowRuns.contains(workflowRunId))
			return false;
		if (!pausedLayerMap.containsKey(workflowRunId))
			return false;
		Map<String, Set<Stop>> pausedLayerMapCopy = new HashMap<>();
		pausedLayerMapCopy.putAll(pausedLayerMap);
		Set<Stop> stops = pausedLayerMapCopy.remove(workflowRunId);
		pausedLayerMap = pausedLayerMapCopy;
		for (Stop s : stops)
			s.resumeLayerWorkflow(workflowRunId);
		return true;
	}

	/**
	 * Resume the workflow run with the specified id on this Stop layer. This
	 * method processes any suspended job events.
	 *
	 * @param workflowRunId
	 *            The id of the workflow run to resume.
	 */
	private void resumeLayerWorkflow(String workflowRunId) {
		synchronized (Stop.class) {
			for (DispatchJobEvent dje : suspendedJobEventMap
					.remove(workflowRunId))
				receiveJob(dje);
		}
	}
}
