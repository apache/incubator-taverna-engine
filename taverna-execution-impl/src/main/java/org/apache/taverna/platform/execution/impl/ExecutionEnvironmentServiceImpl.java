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

package org.apache.taverna.platform.execution.impl;

import java.net.URI;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.taverna.platform.capability.api.ActivityConfigurationException;
import org.apache.taverna.platform.capability.api.ActivityNotFoundException;
import org.apache.taverna.platform.capability.api.DispatchLayerConfigurationException;
import org.apache.taverna.platform.capability.api.DispatchLayerNotFoundException;
import org.apache.taverna.platform.execution.api.ExecutionEnvironment;
import org.apache.taverna.platform.execution.api.ExecutionEnvironmentService;
import org.apache.taverna.platform.execution.api.ExecutionService;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Implementation of the ExecutionEnvironmentService.
 *
 * @author David Withers
 */
public class ExecutionEnvironmentServiceImpl implements ExecutionEnvironmentService {
	private static final Logger logger = Logger.getLogger(ExecutionEnvironmentServiceImpl.class.getName());

	@SuppressWarnings("unused")
	private final Scufl2Tools scufl2Tools = new Scufl2Tools();
	private Set<ExecutionService> executionServices;

	@Override
	public Set<ExecutionEnvironment> getExecutionEnvironments() {
		Set<ExecutionEnvironment> executionEnvironments = new HashSet<>();
		for (ExecutionService executionService : executionServices)
			executionEnvironments.addAll(executionService
					.getExecutionEnvironments());
		return executionEnvironments;
	}

	@Override
	public Set<ExecutionEnvironment> getExecutionEnvironments(Profile profile) {
		Set<ExecutionEnvironment> validExecutionEnvironments = new HashSet<>();
		for (ExecutionEnvironment executionEnvironment : getExecutionEnvironments())
			if (isValidExecutionEnvironment(executionEnvironment, profile))
				validExecutionEnvironments.add(executionEnvironment);
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
			if (!executionEnvironment.activityExists(activity.getType())) {
				logger.fine(MessageFormat.format("{0} does not contain activity {1}",
						executionEnvironment.getName(), activity.getType()));
				return false;
			}
			Configuration activityConfiguration = activity.getConfiguration();
			if (!isValidActivityConfiguration(executionEnvironment, activityConfiguration, activity)) {
				logger.fine(MessageFormat.format("Invalid activity configuration for {1} in {0}",
						executionEnvironment.getName(), activity.getType()));
				return false;
			}
			@SuppressWarnings("unused")
			Processor processor = processorBinding.getBoundProcessor();
			// TODO check that environment has required dispatch layers for processor configuration
//			for (DispatchStackLayer dispatchStackLayer : processor.getDispatchStack()) {
//				if (!executionEnvironment.dispatchLayerExists(dispatchStackLayer
//						.getType())) {
//					logger.fine(MessageFormat.format("{0} does not contain dispatch layer {1}",
//							executionEnvironment.getName(),
//							dispatchStackLayer.getType()));
//					return false;
//				}
//
//				List<Configuration> dispatchLayerConfigurations = scufl2Tools.configurationsFor(dispatchStackLayer, profile);
//				if (dispatchLayerConfigurations.size() > 1) {
//					logger.fine(MessageFormat.format("{0} contains multiple configurations for dispatch layer {1}",
//							executionEnvironment.getName(),
//							dispatchStackLayer.getType()));
//				} else if (dispatchLayerConfigurations.size() == 1) {
//					if (!isValidDispatchLayerConfiguration(executionEnvironment, dispatchLayerConfigurations.get(0), dispatchStackLayer)) {
//						logger.fine(MessageFormat.format("Invalid dispatch layer configuration for {1} in {0}",
//								executionEnvironment.getName(), dispatchStackLayer.getType()));
//						return false;
//					}
//				}
//			}
		}
		return true;
	}

	private boolean isValidActivityConfiguration(ExecutionEnvironment executionEnvironment,
			Configuration configuration, Activity activity) {
		try {
			configuration.getJson();
			configuration.getJsonSchema();
			@SuppressWarnings("unused")
			JsonNode environmentSchema = executionEnvironment.getActivityConfigurationSchema(activity.getType());
			// TODO validate against schema
		} catch (ActivityNotFoundException e) {
			logger.fine(MessageFormat.format("{0} does not contain activity {1}",
					executionEnvironment.getName(), activity.getType()));
			return false;
		} catch (ActivityConfigurationException e) {
			logger.fine(MessageFormat.format("Configuration for {1} is incorrect in {0}",
					executionEnvironment.getName(), activity.getType()));
			return false;
		}
		return true;
	}

	@SuppressWarnings("unused")
	private boolean isValidDispatchLayerConfiguration(ExecutionEnvironment executionEnvironment,
			Configuration configuration, URI dispatchLayerType) {
		try {
			JsonNode environmentSchema = executionEnvironment.getDispatchLayerConfigurationSchema(dispatchLayerType);
			// TODO validate against schema
		} catch (DispatchLayerNotFoundException e) {
			logger.fine(MessageFormat.format("{0} does not contain dispatch layer {1}",
					executionEnvironment.getName(), dispatchLayerType));
			return false;
		} catch (DispatchLayerConfigurationException e) {
			logger.fine(MessageFormat.format("Configuration for {1} is incorrect in {0}",
					executionEnvironment.getName(), dispatchLayerType));
			return false;
		}
		return true;
	}

//	/**
//	 * @param propertyResourceDefinition
//	 * @param propertyResource
//	 * @return
//	 */
//	private boolean isValidPropertyResource(Configuration configuration,
//			PropertyResourceDefinition propertyResourceDefinition, PropertyResource propertyResource) {
//		if (!propertyResourceDefinition.getTypeURI().equals(propertyResource.getTypeURI())) {
//			logger.fine(MessageFormat.format(
//					"Property type {0} does not equal property definition type {1}",
//					propertyResource.getTypeURI(), propertyResourceDefinition.getTypeURI()));
//			return false;
//		}
//		List<PropertyDefinition> propertyDefinitions = propertyResourceDefinition
//				.getPropertyDefinitions();
//		Map<URI, SortedSet<PropertyObject>> properties = propertyResource.getProperties();
//		for (PropertyDefinition propertyDefinition : propertyDefinitions) {
//			SortedSet<PropertyObject> propertySet = properties.get(propertyDefinition
//					.getPredicate());
//			if (propertySet == null) {
//				if (propertyDefinition.isRequired()) {
//					logger.fine(MessageFormat.format("Required property {0} is missing",
//							propertyDefinition.getPredicate()));
//					return false;
//				}
//			} else {
//				if (propertySet.size() == 0 && propertyDefinition.isRequired()) {
//					logger.fine(MessageFormat.format("Required property {0} is missing",
//							propertyDefinition.getPredicate()));
//					return false;
//				}
//				if (propertySet.size() > 1 && !propertyDefinition.isMultiple()) {
//					logger.fine(MessageFormat.format(
//							"{0} properties found for singleton property {1}", propertySet.size(),
//							propertyDefinition.getPredicate()));
//					return false;
//				}
//				if (propertySet.size() > 1 && propertyDefinition.isMultiple() && propertyDefinition.isOrdered()) {
//					logger.fine(MessageFormat.format(
//							"{0} property lists found for property {1}", propertySet.size(),
//							propertyDefinition.getPredicate()));
//					return false;
//				}
//				for (PropertyObject property : propertySet) {
//					if (propertyDefinition.isMultiple() && propertyDefinition.isOrdered()) {
//						if (property instanceof PropertyList) {
//							PropertyList propertyList = (PropertyList) property;
//							for (PropertyObject propertyObject : propertyList) {
//								if (!isValidProperty(configuration, propertyDefinition, propertyObject)) {
//									logger.fine(MessageFormat.format("Property {0} is invalid",
//											propertyDefinition.getPredicate()));
//									return false;
//								}
//							}
//						}
//
//					} else if (!isValidProperty(configuration, propertyDefinition, property)) {
//						logger.fine(MessageFormat.format("Property {0} is invalid",
//								propertyDefinition.getPredicate()));
//						return false;
//					}
//				}
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * @param propertyDefinition
//	 * @param property
//	 * @return
//	 */
//	private boolean isValidProperty(Configuration configuration,
//			PropertyDefinition propertyDefinition, PropertyObject property) {
//		if (propertyDefinition instanceof PropertyLiteralDefinition) {
//			if (property instanceof PropertyLiteral) {
//				PropertyLiteralDefinition propertyLiteralDefinition = (PropertyLiteralDefinition) propertyDefinition;
//				PropertyLiteral propertyLiteral = (PropertyLiteral) property;
//				if (!propertyLiteral.getLiteralType().equals(
//						propertyLiteralDefinition.getLiteralType())) {
//					logger.fine(MessageFormat.format(
//							"Property type {0} does not equal property definition type {1}",
//							propertyLiteral.getLiteralType(),
//							propertyLiteralDefinition.getLiteralType()));
//					return false;
//				}
//				LinkedHashSet<String> options = propertyLiteralDefinition.getOptions();
//				if (options != null && options.size() > 0) {
//					if (!options.contains(propertyLiteral.getLiteralValue())) {
//						logger.fine(MessageFormat.format("Property value {0} is not permitted",
//								propertyLiteral.getLiteralValue()));
//						return false;
//					}
//				}
//			} else {
//				logger.fine(MessageFormat.format("Expected a PropertyLiteral but got a {0}",
//						property.getClass().getSimpleName()));
//				return false;
//			}
//		} else if (propertyDefinition instanceof PropertyReferenceDefinition) {
//			if (property instanceof PropertyReference) {
//				PropertyReferenceDefinition propertyReferenceDefinition = (PropertyReferenceDefinition) propertyDefinition;
//				PropertyReference propertyReference = (PropertyReference) property;
//				LinkedHashSet<URI> options = propertyReferenceDefinition.getOptions();
//				if (options != null && options.size() > 0) {
//					if (!options.contains(propertyReference.getResourceURI())) {
//						logger.fine(MessageFormat.format("Property value {0} is not permitted",
//								propertyReference.getResourceURI()));
//						return false;
//					}
//				}
//			} else {
//				logger.fine(MessageFormat.format("Expected a PropertyReference but got a {0}",
//						property.getClass().getSimpleName()));
//				return false;
//			}
//		} else if (propertyDefinition instanceof PropertyResourceDefinition) {
//			if (property instanceof PropertyResource) {
//				PropertyResourceDefinition propertyResourceDefinition = (PropertyResourceDefinition) propertyDefinition;
//				PropertyResource propertyResource = (PropertyResource) property;
//				return isValidPropertyResource(configuration, propertyResourceDefinition,
//						propertyResource);
//			} else if (property instanceof PropertyReference) {
//				// special cases where a PropertyResource is actually a reference to a WorkflowBundle component
//				PropertyReference propertyReference = (PropertyReference) property;
//				WorkflowBundle workflowBundle = scufl2Tools.findParent(WorkflowBundle.class,
//						configuration);
//				URI configUri = uriTools.uriForBean(configuration);
//				URI referenceUri = configUri.resolve(propertyReference.getResourceURI());
//				if (workflowBundle != null) {
//					URI predicate = propertyDefinition.getPredicate();
//					WorkflowBean workflowBean = uriTools.resolveUri(referenceUri, workflowBundle);
//					if (workflowBean == null) {
//						logger.fine(MessageFormat.format(
//								"Cannot resolve {0} in WorkflowBundle {1}",
//								propertyReference.getResourceURI(), workflowBundle.getName()));
//					}
//					if (predicate.equals(SCUFL2.resolve("#definesInputPort"))) {
//						if (workflowBean == null) {
//							return false;
//						}
//						if (!(workflowBean instanceof InputActivityPort)) {
//							logger.fine(MessageFormat.format(
//									"{0} resolved to a {1}, expected a InputActivityPort",
//									propertyReference.getResourceURI(), workflowBean.getClass()
//											.getSimpleName()));
//							return false;
//						}
//					} else if (predicate.equals(SCUFL2.resolve("#definesOutputPort"))) {
//						if (workflowBean == null) {
//							return false;
//						}
//						if (!(workflowBean instanceof OutputActivityPort)) {
//							logger.fine(MessageFormat.format(
//									"{0} resolved to a {1}, expected a OutputActivityPort",
//									propertyReference.getResourceURI(), workflowBean.getClass()
//											.getSimpleName()));
//							return false;
//						}
//					} else {
//						logger.fine(MessageFormat.format("Unexpected reference to {0}", predicate));
//					}
//				} else {
//					logger.fine(MessageFormat
//							.format("Cannot resolve reference to {0} because Configuration {1} not contained within a WorkflowBundle",
//									referenceUri, configuration.getName()));
//				}
//			} else {
//				logger.fine(MessageFormat.format("Expected a PropertyResource or PropertyReference but got a {0}",
//						property.getClass().getSimpleName()));
//				return false;
//			}
//		} else {
//			logger.fine(MessageFormat.format("Unknown propery definition class {0}",
//					propertyDefinition.getClass().getSimpleName()));
//			return false;
//		}
//		return true;
//	}

}
