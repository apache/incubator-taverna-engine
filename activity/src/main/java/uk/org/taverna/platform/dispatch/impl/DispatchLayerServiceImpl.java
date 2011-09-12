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
package uk.org.taverna.platform.dispatch.impl;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.ConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayerFactory;

import org.apache.log4j.Logger;

import uk.org.taverna.platform.dispatch.DispatchLayerConfigurationException;
import uk.org.taverna.platform.dispatch.DispatchLayerNotFoundException;
import uk.org.taverna.platform.dispatch.DispatchLayerService;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;

/**
 *
 * @author David Withers
 */
public class DispatchLayerServiceImpl implements DispatchLayerService {

	private static Logger logger = Logger.getLogger(DispatchLayerServiceImpl.class);

	private List<DispatchLayerFactory> dispatchLayerFactories;

	@Override
	public List<URI> getDispatchLayerURIs() {
		List<URI> dispatchLayerURIs = new ArrayList<URI>();
		for (DispatchLayerFactory dispatchLayerFactory : dispatchLayerFactories) {
			dispatchLayerURIs.addAll(dispatchLayerFactory.getDispatchLayerURIs());
		}
		return dispatchLayerURIs;
	}

	@Override
	public boolean dispatchLayerExists(URI uri) {
		for (DispatchLayerFactory dispatchLayerFactory : dispatchLayerFactories) {
			if (dispatchLayerFactory.getDispatchLayerURIs().contains(uri)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ConfigurationDefinition getDispatchLayerConfigurationDefinition(URI uri)
			throws DispatchLayerNotFoundException, DispatchLayerConfigurationException {
		DispatchLayerFactory dispatchLayerFactory = getDispatchLayerFactory(uri);
		// TODO implement this
		return null;
	}

	@Override
	public DispatchLayer<?> createDispatchLayer(URI uri, Configuration configuration)
			throws DispatchLayerNotFoundException, DispatchLayerConfigurationException {
		DispatchLayerFactory factory = getDispatchLayerFactory(uri);
		DispatchLayer<?> dispatchLayer = factory.createDispatchLayer(uri);

		if (configuration != null) {
			// check configuration is for the correct activity
			Configurable configurable = configuration.getConfigures();
			if (configurable instanceof DispatchStackLayer) {
				DispatchStackLayer scufl2DispatchLayer = (DispatchStackLayer) configurable;
				if (!scufl2DispatchLayer.getConfigurableType().equals(uri)) {
					String message = MessageFormat.format(
							"Expected a configuration for {0} but got a configuration for {1}",
							uri, scufl2DispatchLayer.getConfigurableType());
					logger.debug(message);
					throw new DispatchLayerConfigurationException(message);
				}
			} else {
				String message = "Configuration does not configure an DispatchLayer";
				logger.debug(message);
				throw new DispatchLayerConfigurationException(message);
			}
			// create the configuration bean
			Object configurationBean = factory.createDispatchLayerConfiguration(uri);
//			ConfigurationDefinition definition = getDispatchLayerConfigurationDefinition(uri,
//					configurationBean.getClass());
			WorkflowBundle workflowBundle = configuration.getParent().getParent();
			try {
				// set the properties on the configuration bean
//				setConfigurationProperties(configurationBean, configuration, configuration.getPropertyResource(),
//						definition.getPropertyResourceDefinition(), uri, workflowBundle);
				// configure the activity with the configuration bean
				((net.sf.taverna.t2.workflowmodel.Configurable) dispatchLayer).configure(configurationBean);
//			} catch (PropertyException e) {
//				throw new DispatchLayerConfigurationException(e);
			} catch (ConfigurationException e) {
				throw new DispatchLayerConfigurationException(e);
			}
		}
		return dispatchLayer;
	}

	@Override
	public List<DispatchLayer<?>> createDefaultDispatchLayers() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sets the list of available <code>DispatchLayerFactory</code>s.
	 *
	 * In a production environment this should be set by Spring DM.
	 *
	 * @param dispatchLayerFactories
	 *            the list of available <code>DispatchLayerFactory</code>s
	 */
	public void setDispatchLayerFactories(List<DispatchLayerFactory> dispatchLayerFactories) {
		this.dispatchLayerFactories = dispatchLayerFactories;
	}

	private DispatchLayerFactory getDispatchLayerFactory(URI uri) throws DispatchLayerNotFoundException {
		for (DispatchLayerFactory dispatchLayerFactory : dispatchLayerFactories) {
			if (dispatchLayerFactory.getDispatchLayerURIs().contains(uri)) {
				return dispatchLayerFactory;
			}
		}
		throw new DispatchLayerNotFoundException("Could not find a dispatch layer for " + uri);
	}

}
