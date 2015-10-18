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

package org.apache.taverna.services;

import java.net.URI;
import java.util.Set;

import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Register of Taverna services.
 *
 * @author David Withers
 */
public interface ServiceRegistry {

	/**
	 * Returns the activity types in the registry.
	 *
	 * @return the activity types in the registry
	 */
	public Set<URI> getActivityTypes();

	/**
	 * Returns the JSON Schema for the configuration required by an activity.
	 *
	 * @param activityType
	 *            the activity type
	 * @return the JSON Schema for the configuration required by an activity
	 * @throws ActivityTypeNotFoundException
	 *             if the activity type is not in the registry
	 */
	public JsonNode getActivityConfigurationSchema(URI activityType)
			throws InvalidConfigurationException, ActivityTypeNotFoundException;

	/**
	 * Returns the input ports that the activity type requires to be present in order to execute
	 * with the specified configuration.
	 * <p>
	 * If the activity does not require any input port for the configuration then an empty set is
	 * returned.
	 *
	 * @param configuration
	 *            the activity configuration
	 * @throws ActivityTypeNotFoundException
	 *             if the activity type is not in the registry
	 * @return the input ports that the activity requires to be present in order to execute
	 */
	public Set<InputActivityPort> getActivityInputPorts(URI activityType,
			JsonNode configuration) throws InvalidConfigurationException, ActivityTypeNotFoundException;

	/**
	 * Returns the output ports that the activity type requires to be present in order to execute
	 * with the specified configuration.
	 * <p>
	 * If the activity type does not require any output ports for the configuration then an empty
	 * set is returned.
	 *
	 * @param configuration
	 *            the activity configuration
	 * @throws ActivityTypeNotFoundException
	 *             if the activity type is not in the registry
	 * @return the output ports that the activity requires to be present in order to execute
	 */
	public Set<OutputActivityPort> getActivityOutputPorts(URI activityType,
			JsonNode configuration) throws InvalidConfigurationException, ActivityTypeNotFoundException;

}
