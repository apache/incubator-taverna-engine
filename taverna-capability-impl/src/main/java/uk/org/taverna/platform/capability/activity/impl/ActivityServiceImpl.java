/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester
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
package uk.org.taverna.platform.capability.activity.impl;

import static uk.org.taverna.platform.capability.property.ConfigurationUtils.createPropertyDefinitions;
import static uk.org.taverna.platform.capability.property.ConfigurationUtils.setConfigurationProperties;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sf.taverna.t2.workflowmodel.Configurable;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityFactory;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationBean;

import org.apache.log4j.Logger;

import uk.org.taverna.platform.capability.activity.ActivityConfigurationException;
import uk.org.taverna.platform.capability.activity.ActivityNotFoundException;
import uk.org.taverna.platform.capability.activity.ActivityService;
import uk.org.taverna.platform.capability.property.ConfigurationException;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyReferenceDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.property.PropertyException;

public class ActivityServiceImpl implements ActivityService {

	private static Logger logger = Logger.getLogger(ActivityServiceImpl.class);

	private List<ActivityFactory> activityFactories;

	private Edits edits;

	@Override
	public List<URI> getActivityURIs() {
		List<URI> activityURIs = new ArrayList<URI>();
		for (ActivityFactory activityFactory : activityFactories) {
			activityURIs.add(activityFactory.getActivityURI());
		}
		return activityURIs;
	}

	@Override
	public boolean activityExists(URI uri) {
		for (ActivityFactory activityFactory : activityFactories) {
			if (activityFactory.getActivityURI().equals(uri)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ConfigurationDefinition getActivityConfigurationDefinition(URI uri)
			throws ActivityNotFoundException, ActivityConfigurationException {
		ActivityFactory factory = getActivityFactory(uri);
		return createConfigurationDefinition(uri, factory.createActivityConfiguration().getClass());
	}

	@Override
	public Activity<?> createActivity(URI uri, Configuration configuration)
			throws ActivityNotFoundException, ActivityConfigurationException {
		ActivityFactory factory = getActivityFactory(uri);

		// create the activity and inject the edits
		Activity<?> activity = factory.createActivity();
		activity.setEdits(edits);

		if (configuration != null) {
			// check configuration is for the correct activity
			uk.org.taverna.scufl2.api.common.Configurable configurable = configuration
					.getConfigures();
			if (configurable instanceof uk.org.taverna.scufl2.api.activity.Activity) {
				uk.org.taverna.scufl2.api.activity.Activity scufl2Activity = (uk.org.taverna.scufl2.api.activity.Activity) configurable;
				if (!scufl2Activity.getConfigurableType().equals(uri)) {
					String message = MessageFormat.format(
							"Expected a configuration for {0} but got a configuration for {1}",
							uri, scufl2Activity.getConfigurableType());
					logger.debug(message);
					throw new ActivityConfigurationException(message);
				}
			} else {
				String message = "Configuration does not configure an Activity";
				logger.debug(message);
				throw new ActivityConfigurationException(message);
			}
			// create the configuration bean
			Object configurationBean = factory.createActivityConfiguration();
			ConfigurationDefinition definition = createConfigurationDefinition(uri,
					configurationBean.getClass());
			WorkflowBundle workflowBundle = configuration.getParent().getParent();
			try {
				// set the properties on the configuration bean
				setConfigurationProperties(configurationBean, configuration,
						configuration.getPropertyResource(),
						definition.getPropertyResourceDefinition(), uri, workflowBundle);
				// configure the activity with the configuration bean
				((Configurable) activity).configure(configurationBean);
			} catch (PropertyException e) {
				throw new ActivityConfigurationException(e);
			} catch (net.sf.taverna.t2.workflowmodel.ConfigurationException e) {
				throw new ActivityConfigurationException(e);
			} catch (ConfigurationException e) {
				throw new ActivityConfigurationException(e);
			}
		}
		return activity;
	}

	@Override
	public void addDynamicPorts(uk.org.taverna.scufl2.api.activity.Activity scufl2Activity,
			Configuration configuration) throws ActivityNotFoundException,
			ActivityConfigurationException {
		Activity<?> activity = createActivity(scufl2Activity.getConfigurableType(), configuration);
		Set<ActivityInputPort> inputPorts = activity.getInputPorts();
		for (ActivityInputPort inputPort : inputPorts) {
			InputActivityPort inputActivityPort = new InputActivityPort(scufl2Activity,
					inputPort.getName());
			inputActivityPort.setDepth(inputPort.getDepth());
		}
		Set<OutputPort> outputPorts = activity.getOutputPorts();
		for (OutputPort outputPort : outputPorts) {
			OutputActivityPort outputActivityPort = new OutputActivityPort(scufl2Activity,
					outputPort.getName());
			outputActivityPort.setDepth(outputPort.getDepth());
			outputActivityPort.setGranularDepth(outputPort.getGranularDepth());
		}
	}

	/**
	 * Sets the workflow model Edits service.
	 *
	 * In a production environment this should be set by Spring DM.
	 *
	 * @param edits
	 *            the workflow model Edits service
	 */
	public void setEdits(Edits edits) {
		this.edits = edits;
	}

	/**
	 * Sets the list of available <code>ActivityFactory</code>s.
	 *
	 * In a production environment this should be set by Spring DM.
	 *
	 * @param activityFactories
	 *            the list of available <code>ActivityFactory</code>s
	 */
	public void setActivityFactories(List<ActivityFactory> activityFactories) {
		this.activityFactories = activityFactories;
	}

	private ConfigurationDefinition createConfigurationDefinition(URI uri,
			Class<?> configurationClass) throws ActivityConfigurationException {

		ConfigurationDefinition configurationDefinition = new ConfigurationDefinition(uri);
		PropertyResourceDefinition propertyResourceDefinition = configurationDefinition
				.getPropertyResourceDefinition();

		ConfigurationBean configurationBean = configurationClass
				.getAnnotation(ConfigurationBean.class);
		if (configurationBean == null) {
			if (Dataflow.class.isAssignableFrom(configurationClass)) {
				PropertyDefinition referenceDefinition = new PropertyReferenceDefinition(
						uri.resolve("#workflow"), "workflow", "Nested workflow", "", true, false,
						false);
				propertyResourceDefinition.setPropertyDefinitions(Collections
						.singletonList(referenceDefinition));
			} else {
				throw new ActivityConfigurationException("Configuration bean for "+uri+" is not annotated");
			}
		} else {
			uri = URI.create(configurationBean.uri());
			propertyResourceDefinition.setTypeURI(uri);
			propertyResourceDefinition
					.setPropertyDefinitions(createPropertyDefinitions(configurationClass));
		}

		return configurationDefinition;
	}

	private ActivityFactory getActivityFactory(URI uri) throws ActivityNotFoundException {
		for (ActivityFactory activityFactory : activityFactories) {
			if (activityFactory.getActivityURI().equals(uri)) {
				return activityFactory;
			}
		}
		throw new ActivityNotFoundException("Could not find an activity for " + uri);
	}

}
