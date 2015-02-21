/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package uk.org.taverna.platform.capability.dispatch.impl;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayerFactory;
import uk.org.taverna.platform.capability.api.DispatchLayerConfigurationException;
import uk.org.taverna.platform.capability.api.DispatchLayerNotFoundException;
import uk.org.taverna.platform.capability.api.DispatchLayerService;

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
		} catch (net.sf.taverna.t2.workflowmodel.ConfigurationException e) {
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
