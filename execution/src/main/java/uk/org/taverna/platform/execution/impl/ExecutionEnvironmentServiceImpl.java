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
package uk.org.taverna.platform.execution.impl;

import java.net.URI;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import uk.org.taverna.platform.activity.ActivityConfigurationException;
import uk.org.taverna.platform.activity.ActivityNotFoundException;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.execution.api.ExecutionEnvironmentService;
import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyLiteralDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyReferenceDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.api.property.PropertyReference;
import uk.org.taverna.scufl2.api.property.PropertyResource;

/**
 * Implementation of the ExecutionEnvironmentService.
 *
 * @author David Withers
 */
public class ExecutionEnvironmentServiceImpl implements ExecutionEnvironmentService {

	private static final Logger logger = Logger.getLogger(ExecutionEnvironmentServiceImpl.class);

	private final Scufl2Tools scufl2Tools = new Scufl2Tools();

	private Set<ExecutionService> executionServices;

	@Override
	public Set<ExecutionEnvironment> getExecutionEnvironments() {
		Set<ExecutionEnvironment> executionEnvironments = new HashSet<ExecutionEnvironment>();
		for (ExecutionService executionService : executionServices) {
			executionEnvironments.addAll(executionService.getExecutionEnvivonments());
		}
		return executionEnvironments;
	}

	@Override
	public Set<ExecutionEnvironment> getExecutionEnvironments(Profile profile) {
		Set<ExecutionEnvironment> validExecutionEnvironments = new HashSet<ExecutionEnvironment>();
		for (ExecutionEnvironment executionEnvironment : getExecutionEnvironments()) {
			if (isValidExecutionEnvironment(executionEnvironment, profile)) {
				validExecutionEnvironments.add(executionEnvironment);
			}
		}
		return validExecutionEnvironments;
	}

	/**
	 * Sets the ExecutionServices that will be used to find ExecutionEnvironments.
	 *
	 * @param executionServices
	 *            the ExecutionServices that will be used to find ExecutionEnvironments
	 */
	public void setExecutionServices(Set<ExecutionService> executionServices) {
		this.executionServices = executionServices;
	}

	/**
	 * @param executionEnvironment
	 * @param profile
	 * @return
	 */
	private boolean isValidExecutionEnvironment(ExecutionEnvironment executionEnvironment,
			Profile profile) {
		NamedSet<ProcessorBinding> processorBindings = profile.getProcessorBindings();
		for (ProcessorBinding processorBinding : processorBindings) {
			Activity activity = processorBinding.getBoundActivity();
			if (!executionEnvironment.activityExists(activity.getConfigurableType())) {
				logger.debug(MessageFormat.format("{0} does not contain activity {1}",
						executionEnvironment.getName(), activity.getConfigurableType()));
				return false;
			}
			Configuration configuration = scufl2Tools.configurationFor(activity, profile);
			if (!isValidActivityConfiguration(executionEnvironment, configuration, activity)) {
				logger.debug(MessageFormat.format("Invalid activity configuration for {1} in {0}",
						executionEnvironment.getName(), activity.getConfigurableType()));
				return false;
			}
			Processor processor = processorBinding.getBoundProcessor();
			for (DispatchStackLayer dispatchStackLayer : processor.getDispatchStack()) {
				if (!executionEnvironment.dispatchLayerExists(dispatchStackLayer
						.getConfigurableType())) {
					logger.debug(MessageFormat.format("{0} does not contain dispatch layer {1}",
							executionEnvironment.getName(),
							dispatchStackLayer.getConfigurableType()));
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValidActivityConfiguration(ExecutionEnvironment executionEnvironment,
			Configuration configuration, Activity activity) {
		try {
			ConfigurationDefinition configurationDefinition = executionEnvironment
					.getActivityConfigurationDefinition(activity.getConfigurableType());
			PropertyResourceDefinition propertyResourceDefinition = configurationDefinition
					.getPropertyResourceDefinition();
			PropertyResource propertyResource = configuration.getPropertyResource();
			if (!isValidPropertyResource(propertyResourceDefinition, propertyResource)) {
				return false;
			}
		} catch (ActivityNotFoundException e) {
			logger.debug(MessageFormat.format("{0} does not contain activity {1}",
					executionEnvironment.getName(), activity.getConfigurableType()));
			return false;
		} catch (ActivityConfigurationException e) {
			logger.debug(MessageFormat.format("Configuration for {1} is incorrect in {0}",
					executionEnvironment.getName(), activity.getConfigurableType()));
			return false;
		}
		return true;
	}

	/**
	 * @param propertyResourceDefinition
	 * @param propertyResource
	 * @return
	 */
	private boolean isValidPropertyResource(PropertyResourceDefinition propertyResourceDefinition,
			PropertyResource propertyResource) {
		if (!propertyResourceDefinition.getTypeURI().equals(propertyResource.getTypeURI())) {
			logger.debug(MessageFormat.format(
					"Property type {0} does not equal property definition type {1}",
					propertyResource.getTypeURI(), propertyResourceDefinition.getTypeURI()));
			return false;
		}
		List<PropertyDefinition> propertyDefinitions = propertyResourceDefinition
				.getPropertyDefinitions();
		Map<URI, SortedSet<PropertyObject>> properties = propertyResource.getProperties();
		for (PropertyDefinition propertyDefinition : propertyDefinitions) {
			SortedSet<PropertyObject> propertySet = properties.get(propertyDefinition
					.getPredicate());
			if (propertySet == null) {
				if (propertyDefinition.isRequired()) {
					logger.debug(MessageFormat.format("Required property {0} is missing",
							propertyDefinition.getPredicate()));
					return false;
				}
			} else {
				if (propertySet.size() == 0 && propertyDefinition.isRequired()) {
					logger.debug(MessageFormat.format("Required property {0} is missing",
							propertyDefinition.getPredicate()));
					return false;
				}
				if (propertySet.size() > 1 && !propertyDefinition.isMultiple()) {
					logger.debug(MessageFormat.format(
							"{0} properties found for singleton property {1}", propertySet.size(),
							propertyDefinition.getPredicate()));
					return false;
				}
				for (PropertyObject property : propertySet) {
					if (!isValidProperty(propertyDefinition, property)) {
						logger.debug(MessageFormat.format("Property {0} is invalid",
								propertyDefinition.getPredicate()));
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * @param propertyDefinition
	 * @param property
	 * @return
	 */
	private boolean isValidProperty(PropertyDefinition propertyDefinition, PropertyObject property) {
		if (propertyDefinition instanceof PropertyLiteralDefinition) {
			if (property instanceof PropertyLiteral) {
				PropertyLiteralDefinition propertyLiteralDefinition = (PropertyLiteralDefinition) propertyDefinition;
				PropertyLiteral propertyLiteral = (PropertyLiteral) property;
				if (!propertyLiteralDefinition.getLiteralType().equals(
						propertyLiteral.getLiteralType())) {
					logger.debug(MessageFormat.format(
							"Property type {0} does not equal property definition type {1}",
							propertyLiteral.getLiteralType(),
							propertyLiteralDefinition.getLiteralType()));
					return false;
				}
				LinkedHashSet<String> options = propertyLiteralDefinition.getOptions();
				if (options != null && options.size() > 0) {
					if (!options.contains(propertyLiteral.getLiteralValue())) {
						logger.debug(MessageFormat.format("Property value {0} is not permitted",
								propertyLiteral.getLiteralValue()));
						return false;
					}
				}
			} else {
				logger.debug(MessageFormat.format("Expected a PropertyLiteral but got a {0}",
						property.getClass().getSimpleName()));
				return false;
			}
		} else if (propertyDefinition instanceof PropertyReferenceDefinition) {
			if (property instanceof PropertyReference) {
				PropertyReferenceDefinition propertyReferenceDefinition = (PropertyReferenceDefinition) propertyDefinition;
				PropertyReference propertyReference = (PropertyReference) property;
				LinkedHashSet<URI> options = propertyReferenceDefinition.getOptions();
				if (options != null && options.size() > 0) {
					if (!options.contains(propertyReference.getResourceURI())) {
						logger.debug(MessageFormat.format("Property value {0} is not permitted",
								propertyReference.getResourceURI()));
						return false;
					}
				}
			} else {
				logger.debug(MessageFormat.format("Expected a PropertyReference but got a {0}",
						property.getClass().getSimpleName()));
				return false;
			}
		} else if (propertyDefinition instanceof PropertyResourceDefinition) {
			if (property instanceof PropertyResource) {
				PropertyResourceDefinition propertyResourceDefinition = (PropertyResourceDefinition) propertyDefinition;
				PropertyResource propertyResource = (PropertyResource) property;
				return isValidPropertyResource(propertyResourceDefinition, propertyResource);
			} else if (property instanceof PropertyReference) {
				logger.debug("Expected a PropertyResource but got a PropertyReference");
				// TODO Do we need to follow the reference and check if refers to the correct
				// element? Would need access to workflow bundle to do so.
			} else {
				logger.debug(MessageFormat.format("Expected a PropertyResource but got a {0}",
						property.getClass().getSimpleName()));
				return false;
			}
		} else {
			logger.debug(MessageFormat.format("Unknown propery definition class {0}",
					propertyDefinition.getClass().getSimpleName()));
			return false;
		}
		return true;
	}

}
