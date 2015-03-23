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

package org.apache.taverna.platform.capability.api;

import java.net.URI;
import java.util.Set;

import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Service for discovering available activities and the properties required to
 * configure the activities.
 * 
 * @author David Withers
 */
public interface ActivityService {
	/**
	 * Returns the available activity types.
	 * 
	 * @return the available activity types
	 */
	Set<URI> getActivityTypes();

	/**
	 * Returns true if and only if the activity type exists.
	 * 
	 * @param uri
	 *            the activity type to check
	 * @return whether the activity type exists
	 */
	boolean activityExists(URI activityType);

	/**
	 * Returns the JSON Schema for the configuration required by an activity.
	 * 
	 * @param activityType
	 *            the activity type
	 * @return the JSON Schema for the configuration required by an activity
	 * @throws ActivityNotFoundException
	 *             if an activity cannot be found for the specified URI
	 * @throws ActivityConfigurationException
	 *             if the JSON Schema cannot be created
	 */
	JsonNode getActivityConfigurationSchema(URI activityType)
			throws ActivityNotFoundException, ActivityConfigurationException;

	/**
	 * Returns the input ports that the activity type requires to be present in
	 * order to execute with the specified configuration.
	 * <p>
	 * If the activity does not require any input port for the configuration
	 * then an empty set is returned.
	 * 
	 * @param configuration
	 *            the activity configuration
	 * @throws ActivityNotFoundException
	 *             if the activity cannot be found
	 * @throws ActivityConfigurationException
	 *             if the activity configuration is incorrect
	 * @return the input ports that the activity requires to be present in order
	 *         to execute
	 */
	Set<InputActivityPort> getActivityInputPorts(URI activityType,
			JsonNode configuration) throws ActivityNotFoundException,
			ActivityConfigurationException;

	/**
	 * Returns the output ports that the activity type requires to be present in
	 * order to execute with the specified configuration.
	 * <p>
	 * If the activity type does not require any output ports for the
	 * configuration then an empty set is returned.
	 * 
	 * @param configuration
	 *            the activity configuration
	 * @throws ActivityNotFoundException
	 *             if the activity cannot be found
	 * @throws ActivityConfigurationException
	 *             if the activity configuration is incorrect
	 * @return the output ports that the activity requires to be present in
	 *         order to execute
	 */
	Set<OutputActivityPort> getActivityOutputPorts(URI activityType,
			JsonNode configuration) throws ActivityNotFoundException,
			ActivityConfigurationException;

	/**
	 * Returns the activity for the specified activity type. If configuration is
	 * not null the returned activity will be configured.
	 * 
	 * @param activityType
	 *            the activity type
	 * @param configuration
	 *            the configuration for the activity, can be <code>null</code>
	 * @return the activity for the specified activityType
	 * @throws ActivityNotFoundException
	 *             if an activity cannot be found for the specified activity
	 *             type
	 * @throws ActivityConfigurationException
	 *             if the configuration is not valid
	 */
	Activity<?> createActivity(URI activityType, JsonNode configuration)
			throws ActivityNotFoundException, ActivityConfigurationException;
}
