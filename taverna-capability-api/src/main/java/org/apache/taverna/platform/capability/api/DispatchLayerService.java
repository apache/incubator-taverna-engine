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

import org.apache.taverna.workflowmodel.processor.dispatch.DispatchLayer;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Service for discovering available dispatch layers and the properties required
 * to configure the layers.
 * 
 * @author David Withers
 */
public interface DispatchLayerService {
	/**
	 * Returns the available dispatch layer types.
	 * 
	 * @return a the available dispatch layer types
	 */
	Set<URI> getDispatchLayerTypes();

	/**
	 * Returns true iff a dispatch layer exists for the specified URI.
	 * 
	 * @param dispatchLayerType
	 *            the dispatch layer type to check
	 * @return true if a dispatch layer exists for the specified URI
	 */
	boolean dispatchLayerExists(URI dispatchLayerType);

	/**
	 * Returns the JSON Schema for the configuration required by a dispatch
	 * layer.
	 * 
	 * @param activityType
	 *            the activity type
	 * @return the JSON Schema for the configuration required by a dispatch
	 *         layer
	 * @throws DispatchLayerNotFoundException
	 *             if a dispatch layer cannot be found for the specified URI
	 * @throws DispatchLayerConfigurationException
	 *             if the JSON Schema cannot be created
	 */
	JsonNode getDispatchLayerConfigurationSchema(URI dispatchLayerType)
			throws DispatchLayerNotFoundException,
			DispatchLayerConfigurationException;

	/**
	 * Returns the dispatch layer for the specified URI.
	 * 
	 * If configuration is not null the returned dispatch layer will be
	 * configured.
	 * 
	 * @param uri
	 *            a URI that identifies a dispatch layer
	 * @param configuration
	 *            the configuration for the dispatch layer, can be
	 *            <code>null</code>
	 * @return the dispatch layer for the specified URI
	 * @throws DispatchLayerNotFoundException
	 *             if a dispatch layer cannot be found for the specified URI
	 * @throws DispatchLayerConfigurationException
	 *             if the configuration is not valid
	 */
	DispatchLayer<?> createDispatchLayer(URI uri, JsonNode configuration)
			throws DispatchLayerNotFoundException,
			DispatchLayerConfigurationException;
}
