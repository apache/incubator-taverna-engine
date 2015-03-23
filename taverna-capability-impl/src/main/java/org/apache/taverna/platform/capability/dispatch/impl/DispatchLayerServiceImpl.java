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

package org.apache.taverna.platform.capability.dispatch.impl;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.taverna.workflowmodel.processor.dispatch.DispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchLayerFactory;
import org.apache.taverna.platform.capability.api.DispatchLayerConfigurationException;
import org.apache.taverna.platform.capability.api.DispatchLayerNotFoundException;
import org.apache.taverna.platform.capability.api.DispatchLayerService;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author David Withers
 */
public class DispatchLayerServiceImpl implements DispatchLayerService {
	@SuppressWarnings("unused")
	private static Logger logger = Logger
			.getLogger(DispatchLayerServiceImpl.class.getName());

	private List<DispatchLayerFactory> dispatchLayerFactories;

	@Override
	public Set<URI> getDispatchLayerTypes() {
		Set<URI> dispatchLayerTypes = new HashSet<>();
		for (DispatchLayerFactory dispatchLayerFactory : dispatchLayerFactories)
			dispatchLayerTypes.addAll(dispatchLayerFactory
					.getDispatchLayerTypes());
		return dispatchLayerTypes;
	}

	@Override
	public boolean dispatchLayerExists(URI dispatchLayerType) {
		for (DispatchLayerFactory dispatchLayerFactory : dispatchLayerFactories)
			if (dispatchLayerFactory.getDispatchLayerTypes().contains(
					dispatchLayerType))
				return true;
		return false;
	}

	@Override
	public JsonNode getDispatchLayerConfigurationSchema(URI dispatchLayerType)
			throws DispatchLayerNotFoundException {
		DispatchLayerFactory factory = getDispatchLayerFactory(dispatchLayerType);
		return factory.getDispatchLayerConfigurationSchema(dispatchLayerType);
	}

	@Override
	public DispatchLayer<?> createDispatchLayer(URI dispatchLayerType,
			JsonNode configuration) throws DispatchLayerNotFoundException,
			DispatchLayerConfigurationException {
		DispatchLayerFactory factory = getDispatchLayerFactory(dispatchLayerType);
		@SuppressWarnings("unchecked")
		DispatchLayer<JsonNode> dispatchLayer = (DispatchLayer<JsonNode>) factory
				.createDispatchLayer(dispatchLayerType);

		try {
			if (configuration != null)
				dispatchLayer.configure(configuration);
		} catch (org.apache.taverna.workflowmodel.ConfigurationException e) {
			throw new DispatchLayerConfigurationException(e);
		}
		return dispatchLayer;
	}

	/**
	 * Sets the list of available <code>DispatchLayerFactory</code>s.
	 * 
	 * In a production environment this should be set by Spring DM.
	 * 
	 * @param dispatchLayerFactories
	 *            the list of available <code>DispatchLayerFactory</code>s
	 */
	public void setDispatchLayerFactories(
			List<DispatchLayerFactory> dispatchLayerFactories) {
		this.dispatchLayerFactories = dispatchLayerFactories;
	}

	private DispatchLayerFactory getDispatchLayerFactory(URI dispatchLayerType)
			throws DispatchLayerNotFoundException {
		for (DispatchLayerFactory dispatchLayerFactory : dispatchLayerFactories)
			if (dispatchLayerFactory.getDispatchLayerTypes().contains(
					dispatchLayerType))
				return dispatchLayerFactory;
		throw new DispatchLayerNotFoundException(
				"Could not find a dispatch layer for " + dispatchLayerType);
	}
}
