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

import java.net.URI;
import java.util.Set;

import org.apache.taverna.platform.capability.api.ActivityConfigurationException;
import org.apache.taverna.platform.capability.api.ActivityNotFoundException;
import org.apache.taverna.platform.capability.api.DispatchLayerConfigurationException;
import org.apache.taverna.platform.capability.api.DispatchLayerNotFoundException;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The ExecutionEnvironment specifies the capabilities of a workflow execution environment.
 *
 * @author David Withers
 */
public interface ExecutionEnvironment {

	/**
	 * Returns the identifier for this ExecutionEnvironment.
	 *
	 * @return the identifier for this ExecutionEnvironment
	 */
	public String getID();

	/**
	 * Returns the name of this ExecutionEnvironment.
	 *
	 * @return the name of this ExecutionEnvironment
	 */
	public String getName();

	/**
	 * Returns a description of this ExecutionEnvironment.
	 *
	 * @return a description of this ExecutionEnvironment
	 */
	public String getDescription();

	/**
	 * Returns the ExecutionService that provides this ExecutionEnvironment.
	 *
	 * @return the ExecutionService that provides this ExecutionEnvironment
	 */
	public ExecutionService getExecutionService();

	/**
	 * Returns the activity types available in this ExecutionEnvironment.
	 *
	 * @return the activity types available in this ExecutionEnvironment
	 */
	public Set<URI> getActivityTypes();

	/**
	 * Returns true iff an activity exists for the specified URI in this ExecutionEnvironment.
	 *
	 * @param uri
	 *            the activity URI to check
	 * @return true if an activity exists for the specified URI in this ExecutionEnvironment
	 */
	public boolean activityExists(URI uri);

	/**
	 * Returns a JSON Schema for the configuration required by an activity.
	 *
	 * @param uri
	 *            a URI that identifies an activity
	 * @return a JSON Schema for the configuration required by an activity
	 * @throws ActivityNotFoundException
	 *             if an activity cannot be found for the specified URI
	 * @throws ActivityConfigurationException
	 *             if the ConfigurationDefinition cannot be created
	 */
	public JsonNode getActivityConfigurationSchema(URI uri)
			throws ActivityNotFoundException, ActivityConfigurationException;

	/**
	 * Returns the dispatch layer types available in this ExecutionEnvironment.
	 *
	 * @return the dispatch layer types available in this ExecutionEnvironment
	 */
	public Set<URI> getDispatchLayerTypes();

	/**
	 * Returns true iff a dispatch layer exists for the specified URI in this ExecutionEnvironment.
	 *
	 * @param uri
	 *            the dispatch layer URI to check
	 * @return true if a dispatch layer exists for the specified URI in this ExecutionEnvironment
	 */
	public boolean dispatchLayerExists(URI uri);

	/**
	 * Returns a JSON Schema for the configuration required by a dispatch layer.
	 *
	 * @param uri
	 *            a URI that identifies a dispatch layer
	 * @return
	 * @return a JSON Schema for the configuration required by a dispatch layer
	 * @throws DispatchLayerNotFoundException
	 *             if a dispatch layer cannot be found for the specified URI
	 * @throws DispatchLayerConfigurationException
	 *             if the ConfigurationDefinition cannot be created
	 */
	public JsonNode getDispatchLayerConfigurationSchema(URI uri)
			throws DispatchLayerNotFoundException, DispatchLayerConfigurationException;

}
