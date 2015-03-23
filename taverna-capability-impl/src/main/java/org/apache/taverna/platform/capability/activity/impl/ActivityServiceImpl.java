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

package org.apache.taverna.platform.capability.activity.impl;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityFactory;
import org.apache.taverna.workflowmodel.processor.activity.ActivityInputPort;
import org.apache.taverna.workflowmodel.processor.activity.ActivityOutputPort;
import org.apache.taverna.platform.capability.api.ActivityConfigurationException;
import org.apache.taverna.platform.capability.api.ActivityNotFoundException;
import org.apache.taverna.platform.capability.api.ActivityService;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;

import com.fasterxml.jackson.databind.JsonNode;

public class ActivityServiceImpl implements ActivityService {
	private List<ActivityFactory> activityFactories;

	@Override
	public Set<URI> getActivityTypes() {
		Set<URI> activityTypes = new HashSet<>();
		for (ActivityFactory activityFactory : activityFactories)
			activityTypes.add(activityFactory.getActivityType());
		return activityTypes;
	}

	@Override
	public boolean activityExists(URI uri) {
		for (ActivityFactory activityFactory : activityFactories)
			if (activityFactory.getActivityType().equals(uri))
				return true;
		return false;
	}

	@Override
	public JsonNode getActivityConfigurationSchema(URI activityType)
			throws ActivityNotFoundException {
		ActivityFactory factory = getActivityFactory(activityType);
		return factory.getActivityConfigurationSchema();
	}

	@Override
	public Activity<?> createActivity(URI activityType, JsonNode configuration)
			throws ActivityNotFoundException, ActivityConfigurationException {
		ActivityFactory factory = getActivityFactory(activityType);
		@SuppressWarnings("unchecked")
		Activity<JsonNode> activity = (Activity<JsonNode>) factory
				.createActivity();
		try {
			if (configuration != null)
				activity.configure(configuration);
		} catch (org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException e) {
			throw new ActivityConfigurationException(e);
		}
		return activity;
	}

	@Override
	public Set<InputActivityPort> getActivityInputPorts(URI activityType,
			JsonNode configuration) throws ActivityNotFoundException,
			ActivityConfigurationException {
		Set<InputActivityPort> inputPorts = new HashSet<>();
		try {
			for (ActivityInputPort port : getActivityFactory(activityType)
					.getInputPorts(configuration)) {
				InputActivityPort inputActivityPort = new InputActivityPort();
				inputActivityPort.setName(port.getName());
				inputActivityPort.setDepth(port.getDepth());
				inputPorts.add(inputActivityPort);
			}
			return inputPorts;
		} catch (org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException e) {
			throw new ActivityConfigurationException(e);
		}
	}

	@Override
	public Set<OutputActivityPort> getActivityOutputPorts(URI activityType,
			JsonNode configuration) throws ActivityNotFoundException,
			ActivityConfigurationException {
		Set<OutputActivityPort> outputPorts = new HashSet<>();
		try {
			for (ActivityOutputPort port : getActivityFactory(activityType)
					.getOutputPorts(configuration)) {
				OutputActivityPort outputActivityPort = new OutputActivityPort();
				outputActivityPort.setName(port.getName());
				outputActivityPort.setDepth(port.getDepth());
				outputActivityPort.setGranularDepth(port.getGranularDepth());
				outputPorts.add(outputActivityPort);
			}
		} catch (org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException e) {
			throw new ActivityConfigurationException(e);
		}
		return outputPorts;
	}

	/**
	 * Sets the list of available <code>ActivityFactory</code>s. In a production
	 * environment this should be set by Spring DM.
	 * 
	 * @param activityFactories
	 *            the list of available <code>ActivityFactory</code>s
	 */
	public void setActivityFactories(List<ActivityFactory> activityFactories) {
		this.activityFactories = activityFactories;
	}

	private ActivityFactory getActivityFactory(URI activityType)
			throws ActivityNotFoundException {
		for (ActivityFactory activityFactory : activityFactories)
			if (activityFactory.getActivityType().equals(activityType))
				return activityFactory;
		throw new ActivityNotFoundException("Could not find an activity for "
				+ activityType);
	}
}
