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
package uk.org.taverna.platform.capability.dispatch;

import java.net.URI;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;

/**
 * Service for discovering available dispatch layers and the properties required to configure the
 * layers.
 *
 * @author David Withers
 */
public interface DispatchLayerService {

	/**
	 * Returns a list URI's that identify available dispatch layers.
	 *
	 * @return a list URI's that identify available dispatch layers
	 */
	public List<URI> getDispatchLayerURIs();

	/**
	 * Returns true iff a dispatch layer exists for the specified URI.
	 *
	 * @param uri
	 *            the dispatch layer URI to check
	 * @return true if a dispatch layer exists for the specified URI
	 */
	public boolean dispatchLayerExists(URI uri);

	/**
	 * Returns a definition of the configuration required by a dispatch layer.
	 *
	 * @param uri
	 *            a URI that identifies a dispatch layer
	 * @return a definition of the configuration required by a dispatch layer
	 * @throws DispatchLayerNotFoundException
	 *             if a dispatch layer cannot be found for the specified URI
	 * @throws DispatchLayerConfigurationException
	 *             if the ConfigurationDefinition cannot be created
	 */
	public ConfigurationDefinition getDispatchLayerConfigurationDefinition(URI uri)
			throws DispatchLayerNotFoundException, DispatchLayerConfigurationException;

	/**
	 * Returns the dispatch layer for the specified URI.
	 *
	 * If configuration is not null the returned dispatch layer will be configured.
	 *
	 * @param uri
	 *            a URI that identifies a dispatch layer
	 * @param configuration
	 *            the configuration for the dispatch layer, can be <code>null</code>
	 * @return the dispatch layer for the specified URI
	 * @throws DispatchLayerNotFoundException
	 *             if a dispatch layer cannot be found for the specified URI
	 * @throws DispatchLayerConfigurationException
	 *             if the configuration is not valid
	 */
	public DispatchLayer<?> createDispatchLayer(URI uri, Configuration configuration)
			throws DispatchLayerNotFoundException, DispatchLayerConfigurationException;

}
