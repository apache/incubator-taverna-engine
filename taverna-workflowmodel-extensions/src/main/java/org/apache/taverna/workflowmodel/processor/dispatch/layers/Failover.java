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

import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.CREATE_LOCAL_STATE;
import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.REMOVE_LOCAL_STATE;
import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.UPDATE_LOCAL_STATE;
import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType.JOB;

import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.dispatch.AbstractErrorHandlerLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerErrorReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerJobReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerResultReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobEvent;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Failure handling dispatch layer, consumes job events with multiple activities
 * and emits the same job but with only the first activity. On failures the job
 * is resent to the layer below with a new activity list containing the second
 * in the original list and so on. If a failure is received and there are no
 * further activities to use the job fails and the failure is sent back up to
 * the layer above.
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 */
@DispatchLayerErrorReaction(emits = { JOB }, relaysUnmodified = true, stateEffects = {
		UPDATE_LOCAL_STATE, REMOVE_LOCAL_STATE })
@DispatchLayerJobReaction(emits = {}, relaysUnmodified = true, stateEffects = { CREATE_LOCAL_STATE })
@DispatchLayerResultReaction(emits = {}, relaysUnmodified = true, stateEffects = { REMOVE_LOCAL_STATE })
public class Failover extends AbstractErrorHandlerLayer<JsonNode> {
	public static final String URI = "http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Failover";

	@Override
	protected JobState getStateObject(DispatchJobEvent jobEvent) {
		return new FailoverState(jobEvent);
	}

	/**
	 * Receive a job from the layer above, store it in the state map then relay
	 * it to the layer below with a modified activity list containing only the
	 * activity at index 0
	 */
	@Override
	public void receiveJob(DispatchJobEvent jobEvent) {
		addJobToStateList(jobEvent);
		List<Activity<?>> newActivityList = new ArrayList<>();
		newActivityList.add(jobEvent.getActivities().get(0));
		getBelow().receiveJob(
				new DispatchJobEvent(jobEvent.getOwningProcess(), jobEvent
						.getIndex(), jobEvent.getContext(), jobEvent.getData(),
						newActivityList));
	}

	class FailoverState extends JobState {
		int currentActivityIndex = 0;

		public FailoverState(DispatchJobEvent jobEvent) {
			super(jobEvent);
		}

		@Override
		public boolean handleError() {
			currentActivityIndex++;
			if (currentActivityIndex == jobEvent.getActivities().size())
				return false;
			List<Activity<?>> newActivityList = new ArrayList<>();
			newActivityList.add(jobEvent.getActivities().get(
					currentActivityIndex));
			getBelow().receiveJob(
					new DispatchJobEvent(jobEvent.getOwningProcess(), jobEvent
							.getIndex(), jobEvent.getContext(), jobEvent
							.getData(), newActivityList));
			return true;
		}
	}

	@Override
	public void configure(JsonNode config) {
		// Do nothing - there is no configuration to do
	}

	@Override
	public JsonNode getConfiguration() {
		return null;
	}
}
