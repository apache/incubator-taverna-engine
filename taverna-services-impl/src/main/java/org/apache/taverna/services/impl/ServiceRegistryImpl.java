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

package org.apache.taverna.services.impl;

import java.net.URI;
import java.util.Set;

import org.apache.taverna.services.ActivityTypeNotFoundException;
import org.apache.taverna.services.InvalidConfigurationException;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.platform.capability.api.ActivityConfigurationException;
import org.apache.taverna.platform.capability.api.ActivityNotFoundException;
import org.apache.taverna.platform.capability.api.ActivityService;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Simple implementation of a ServiceRegistry that discovers available services from the
 * ActivityService.
 *
 * @author David Withers
 */
public class ServiceRegistryImpl implements ServiceRegistry {

	private ActivityService activityService;

	@Override
	public Set<URI> getActivityTypes() {
		return activityService.getActivityTypes();
	}

	@Override
	public JsonNode getActivityConfigurationSchema(URI activityType)
			throws InvalidConfigurationException, ActivityTypeNotFoundException {
		try {
			return activityService.getActivityConfigurationSchema(activityType);
		} catch (ActivityConfigurationException e) {
			throw new InvalidConfigurationException(e);
		} catch (ActivityNotFoundException e) {
			throw new ActivityTypeNotFoundException(e);
		}
	}

	@Override
	public Set<InputActivityPort> getActivityInputPorts(URI activityType, JsonNode configuration)
			throws InvalidConfigurationException, ActivityTypeNotFoundException {
		try {
			return activityService.getActivityInputPorts(activityType, configuration);
		} catch (ActivityConfigurationException e) {
			throw new InvalidConfigurationException(e);
		} catch (ActivityNotFoundException e) {
			throw new ActivityTypeNotFoundException(e);
		}
	}

	@Override
	public Set<OutputActivityPort> getActivityOutputPorts(URI activityType, JsonNode configuration)
			throws InvalidConfigurationException, ActivityTypeNotFoundException {
		try {
			return activityService.getActivityOutputPorts(activityType, configuration);
		} catch (ActivityConfigurationException e) {
			throw new InvalidConfigurationException(e);
		} catch (ActivityNotFoundException e) {
			throw new ActivityTypeNotFoundException(e);
		}
	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

}
