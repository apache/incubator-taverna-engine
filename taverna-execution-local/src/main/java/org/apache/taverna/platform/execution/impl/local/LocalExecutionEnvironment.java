/*******************************************************************************
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
import java.util.Set;

import org.apache.taverna.platform.capability.api.ActivityConfigurationException;
import org.apache.taverna.platform.capability.api.ActivityNotFoundException;
import org.apache.taverna.platform.capability.api.ActivityService;
import org.apache.taverna.platform.capability.api.DispatchLayerConfigurationException;
import org.apache.taverna.platform.capability.api.DispatchLayerNotFoundException;
import org.apache.taverna.platform.capability.api.DispatchLayerService;
import org.apache.taverna.platform.execution.api.AbstractExecutionEnvironment;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Execution Environment for a local Taverna Dataflow Engine
 *
 * @author David Withers
 */
public class LocalExecutionEnvironment extends AbstractExecutionEnvironment {

	private final ActivityService activityService;
	private final DispatchLayerService dispatchLayerService;

	public LocalExecutionEnvironment(LocalExecutionService localExecutionService,
			ActivityService activityService, DispatchLayerService dispatchLayerService) {
		super(LocalExecutionEnvironment.class.getName(), "Taverna Local Execution Environment",
				"Execution Environment for a local Taverna Dataflow Engine", localExecutionService);
		this.activityService = activityService;
		this.dispatchLayerService = dispatchLayerService;
	}

	@Override
	public Set<URI> getActivityTypes() {
		return activityService.getActivityTypes();
	}

	@Override
	public boolean activityExists(URI uri) {
		return activityService.activityExists(uri);
	}

	@Override
	public JsonNode getActivityConfigurationSchema(URI uri)
			throws ActivityNotFoundException, ActivityConfigurationException {
		return activityService.getActivityConfigurationSchema(uri);
	}

	@Override
	public Set<URI> getDispatchLayerTypes() {
		return dispatchLayerService.getDispatchLayerTypes();
	}

	@Override
	public boolean dispatchLayerExists(URI uri) {
		return dispatchLayerService.dispatchLayerExists(uri);
	}

	@Override
	public JsonNode getDispatchLayerConfigurationSchema(URI uri)
			throws DispatchLayerNotFoundException, DispatchLayerConfigurationException {
		return dispatchLayerService.getDispatchLayerConfigurationSchema(uri);
	}

}
