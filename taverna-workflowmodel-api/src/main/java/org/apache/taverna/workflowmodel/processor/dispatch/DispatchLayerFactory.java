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

package org.apache.taverna.workflowmodel.processor.dispatch;

import java.net.URI;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Factory for creating {@link DispatchLayer} instances.
 * 
 * @author David Withers
 */
public interface DispatchLayerFactory {
	/**
	 * Creates a new {@link DispatchLayer} instance.
	 * 
	 * @param dispatchLayerType
	 *            the type of the {@link DispatchLayer}
	 * @return a new <code>DispatchLayer</code> instance
	 */
	DispatchLayer<?> createDispatchLayer(URI dispatchLayerType);

	/**
	 * Returns the types of the {@link DispatchLayer}s that this factory
	 * can create.
	 * 
	 * @return the types of the {@link DispatchLayer}s that this factory
	 *         can create
	 */
	Set<URI> getDispatchLayerTypes();

	/**
	 * Returns the JSON Schema for the configuration required by the
	 * {@link DispatchLayer}.
	 * 
	 * @param dispatchLayerType
	 *            the type of the {@link DispatchLayer}
	 * @return the JSON Schema for the configuration required by the
	 *         {@link DispatchLayer}
	 */
	JsonNode getDispatchLayerConfigurationSchema(URI dispatchLayerType);
}
