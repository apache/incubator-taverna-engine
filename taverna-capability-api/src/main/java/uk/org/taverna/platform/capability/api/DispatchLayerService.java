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
package uk.org.taverna.platform.capability.api;

import java.net.URI;
import java.util.Set;

import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;

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
