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
package uk.org.taverna.platform.activity.impl;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.taverna.t2.workflowmodel.Configurable;
import net.sf.taverna.t2.workflowmodel.ConfigurationException;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityFactory;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationBean;
import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationProperty;

import org.apache.log4j.Logger;
import org.jdom.Element;

import uk.org.taverna.platform.activity.ActivityConfigurationException;
import uk.org.taverna.platform.activity.ActivityNotFoundException;
import uk.org.taverna.platform.activity.ActivityService;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyLiteralDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyReferenceDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.property.MultiplePropertiesException;
import uk.org.taverna.scufl2.api.property.PropertyException;
import uk.org.taverna.scufl2.api.property.PropertyList;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyNotFoundException;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.api.property.PropertyReference;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.api.property.UnexpectedPropertyException;

public class ActivityServiceImpl implements ActivityService {

	private static Logger logger = Logger.getLogger(ActivityServiceImpl.class);

	private List<ActivityFactory> activityFactories;

	private Edits edits;

	private URITools uriTools = new URITools();

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
				setConfigurationProperties(configurationBean, configuration, configuration.getPropertyResource(),
						definition.getPropertyResourceDefinition(), uri, workflowBundle);
				// configure the activity with the configuration bean
				((Configurable) activity).configure(configurationBean);
			} catch (PropertyException e) {
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

	@Override
	public void setEdits(Edits edits) {
		this.edits = edits;
	}

	@Override
	public void setActivityFactories(List<ActivityFactory> activityFactories) {
		this.activityFactories = activityFactories;
	}

	private void setConfigurationProperties(Object configurationBean, Configuration configuration, PropertyResource resource,
			PropertyResourceDefinition resourceDefinition, URI uri, WorkflowBundle bundle)
			throws ActivityConfigurationException, PropertyException {
		try {
			// special cases
			Class<?> configurationClass = configurationBean.getClass();

			for (PropertyDefinition propertyDefinition : resourceDefinition
					.getPropertyDefinitions()) {
				URI predicate = propertyDefinition.getPredicate();
				String propertyDefinitionName = propertyDefinition.getName();
				if ("definesInputPort".equals(propertyDefinitionName)) {
					URI resourceURI = resource.getPropertyAsResourceURI(predicate);
					URI configUri = uriTools.uriForBean(configuration);
					WorkflowBean workflowBean = uriTools.resolveUri(configUri.resolve(resourceURI), bundle);
					if (!(workflowBean instanceof InputActivityPort)) {
						throw new UnexpectedPropertyException("Expected reference to input port",
								uri.resolve("#definesInputPort"), resource);
					}
					if (!(configurationBean instanceof ActivityInputPortDefinitionBean)) {
						throw new ActivityConfigurationException(
								"Expected an ActivityInputPortDefinitionBean");
					}
					ActivityInputPortDefinitionBean inputPortDefinitionBean = (ActivityInputPortDefinitionBean) configurationBean;
					InputActivityPort inputActivityPort = (InputActivityPort) workflowBean;
					inputPortDefinitionBean.setName(inputActivityPort.getName());
					inputPortDefinitionBean.setDepth(inputActivityPort.getDepth());
					inputPortDefinitionBean.setTranslatedElementType(String.class);
				} else if ("definesOutputPort".equals(propertyDefinitionName)) {
					URI resourceURI = resource.getPropertyAsResourceURI(predicate);
					URI configUri = uriTools.uriForBean(configuration);
					WorkflowBean workflowBean = uriTools.resolveUri(configUri.resolve(resourceURI), bundle);
					if (!(workflowBean instanceof OutputActivityPort)) {
						throw new UnexpectedPropertyException("Expected reference to output port",
								uri.resolve("#definesOutputPort"), resource);
					}
					if (!(configurationBean instanceof ActivityOutputPortDefinitionBean)) {
						throw new ActivityConfigurationException(
								"Expected an ActivityOutputPortDefinitionBean");
					}
					ActivityOutputPortDefinitionBean outputPortDefinitionBean = (ActivityOutputPortDefinitionBean) configurationBean;
					OutputActivityPort outputActivityPort = (OutputActivityPort) workflowBean;
					outputPortDefinitionBean.setName(outputActivityPort.getName());
					outputPortDefinitionBean.setDepth(outputActivityPort.getDepth());
				} else {
					Method method = getPropertySetMethod(configurationClass, propertyDefinitionName);
					Class<?>[] parameterTypes = method.getParameterTypes();
					Type[] genericParameterTypes = method.getGenericParameterTypes();
					if (parameterTypes.length != 1 || genericParameterTypes.length != 1) {
						throw new ActivityConfigurationException(MessageFormat.format(
								"Property set method has {0} parameters; expected 1",
								parameterTypes.length));
					}
					Class<?> parameterType = parameterTypes[0];
					Type genericParameterType = genericParameterTypes[0];
					Class<?> propertyType = parameterType;
					Class<?> elementType = getElementType(genericParameterType);
					if (elementType != null) {
						propertyType = elementType;
					}

					List<Object> propertyValues = new ArrayList<Object>();
					if (propertyDefinition instanceof PropertyLiteralDefinition) {
						PropertyLiteralDefinition dataPropertyDefinition = (PropertyLiteralDefinition) propertyDefinition;
						URI type = dataPropertyDefinition.getLiteralType();
						List<PropertyLiteral> literals = getProperties(predicate,
								propertyDefinition, resource, PropertyLiteral.class);
						for (PropertyLiteral literal : literals) {
							if (!literal.getLiteralType().equals(type)) {
								throw new ActivityConfigurationException(MessageFormat.format(
										"Expected property {0} to have type {1} but was {2}",
										propertyDefinitionName,
										dataPropertyDefinition.getLiteralType(),
										literal.getLiteralType()));
							}
							if (type.equals(PropertyLiteral.XSD_STRING)) {
								if (propertyType.isEnum()) {
									for (Object enumConstant : propertyType.getEnumConstants()) {
										if (((Enum<?>) enumConstant).name().equals(
												literal.getLiteralValue())) {
											propertyValues.add(enumConstant);
											break;
										}
									}
								} else {
									propertyValues.add(literal.getLiteralValue());
								}
							} else if (type.equals(PropertyLiteral.XSD_INT)) {
								propertyValues.add(literal.getLiteralValueAsInt());
							} else if (type.equals(PropertyLiteral.XSD_LONG)) {
								propertyValues.add(literal.getLiteralValueAsLong());
							} else if (type.equals(PropertyLiteral.XSD_FLOAT)) {
								propertyValues.add(literal.getLiteralValueAsFloat());
							} else if (type.equals(PropertyLiteral.XSD_DOUBLE)) {
								propertyValues.add(literal.getLiteralValueAsDouble());
							} else if (type.equals(PropertyLiteral.XSD_BOOLEAN)) {
								propertyValues.add(literal.getLiteralValueAsBoolean());
							} else {
								// TODO
							}
						}
					} else if (propertyDefinition instanceof PropertyResourceDefinition) {
						PropertyResourceDefinition propertyResourceDefinition = (PropertyResourceDefinition) propertyDefinition;
						List<PropertyResource> resources = getProperties(predicate,
								propertyDefinition, resource, PropertyResource.class);
						for (PropertyResource resourceElement : resources) {
							Object configurationObject = propertyType.newInstance();
							setConfigurationProperties(configurationObject, configuration, resourceElement,
									propertyResourceDefinition, uri, bundle);
							propertyValues.add(configurationObject);
						}
					} else if (propertyDefinition instanceof PropertyReferenceDefinition) {
						List<PropertyReference> references = getProperties(predicate,
								propertyDefinition, resource, PropertyReference.class);
						for (PropertyReference referenceElement : references) {
							URI resourceURI = referenceElement.getResourceURI();
							propertyValues.add(resourceURI);
						}
					}

					if (propertyDefinition.isMultiple()) {
						if (Collection.class.isAssignableFrom(parameterType)) {
							Class<?> collectionImplementation = getCollectionImplementation(parameterType);
							Object collectionObject = collectionImplementation.getConstructor(
									Collection.class).newInstance(propertyValues);
							method.invoke(configurationBean, collectionObject);
						} else if (parameterType.isArray()) {
							Object array = Array.newInstance(parameterType.getComponentType(),
									propertyValues.size());
							for (int i = 0; i < propertyValues.size(); i++) {
								Array.set(array, i, propertyValues.get(i));
							}
							method.invoke(configurationBean, array);
						} else {
							throw new ActivityConfigurationException(MessageFormat.format(
									"Expected a Collection or an array but found {0}",
									parameterType));
						}
					} else {
						// TODO check that we have one value here
						for (Object object : propertyValues) {
							method.invoke(configurationBean, object);
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			throw new ActivityConfigurationException(e);
		} catch (SecurityException e) {
			throw new ActivityConfigurationException(e);
		} catch (InstantiationException e) {
			throw new ActivityConfigurationException(e);
		} catch (IllegalAccessException e) {
			throw new ActivityConfigurationException(e);
		} catch (InvocationTargetException e) {
			throw new ActivityConfigurationException(e);
		} catch (NoSuchMethodException e) {
			throw new ActivityConfigurationException(e);
		}
	}

	private <T extends PropertyObject> List<T> getProperties(URI predicate,
			PropertyDefinition propertyDefinition, PropertyResource propertyResource,
			Class<T> propertyType) throws PropertyNotFoundException, MultiplePropertiesException,
			UnexpectedPropertyException {
		List<T> properties = new ArrayList<T>();
		if (propertyDefinition.isOrdered()) {
			PropertyObject property = propertyResource.getProperty(predicate);
			if (property instanceof PropertyList) {
				PropertyList propertyList = (PropertyList) property;
				for (PropertyObject propertyObject : propertyList) {
					if (propertyType.isInstance(propertyObject)) {
						properties.add(propertyType.cast(propertyObject));
					} else {
						throw new UnexpectedPropertyException("Expected a PropertyList of "
								+ propertyType.getSimpleName(), predicate, propertyResource);
					}
				}
			} else {
				throw new UnexpectedPropertyException("Expected a PropertyList", predicate,
						propertyResource);
			}
		} else {
			properties.addAll(propertyResource.getPropertiesOfType(predicate, propertyType));
		}
		if (properties.size() == 0 && propertyDefinition.isRequired()) {
			throw new PropertyNotFoundException(predicate, propertyResource);
		}
		if (properties.size() > 1 && !propertyDefinition.isMultiple()) {
			throw new MultiplePropertiesException(predicate, propertyResource);
		}
		return properties;
	}

	private ConfigurationDefinition createConfigurationDefinition(URI uri,
			Class<?> configurationClass) throws ActivityConfigurationException {

		ConfigurationDefinition configurationDefinition = new ConfigurationDefinition(uri);
		PropertyResourceDefinition propertyResourceDefinition = configurationDefinition
				.getPropertyResourceDefinition();

		ConfigurationBean configurationBean = configurationClass.getAnnotation(ConfigurationBean.class);
		if (configurationBean == null) {
			if (Dataflow.class.isAssignableFrom(configurationClass)) {
				// TODO dataflow activity
				propertyResourceDefinition.setPredicate(uri.resolve("#dataflow"));
				propertyResourceDefinition
						.setTypeURI(URI.create("java:" + Dataflow.class.getName()));
				propertyResourceDefinition.setName("dataflow");
				propertyResourceDefinition.setLabel("Nested Workflow");
			} else if (Element.class.isAssignableFrom(configurationClass)) {
				// TODO biomart activity
			} else {
				throw new ActivityConfigurationException("Configuration bean is not annotated");
			}
		} else {
			propertyResourceDefinition.setPropertyDefinitions(createPropertyDefinitions(uri,
					configurationClass));
		}

		return configurationDefinition;
	}

	private List<PropertyDefinition> createPropertyDefinitions(URI activityURI,
			Class<?> configurationClass) {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		Method[] methods = configurationClass.getMethods();
		for (Method method : methods) {
			ConfigurationProperty property = method.getAnnotation(ConfigurationProperty.class);
			if (property != null && method.getParameterTypes().length == 1) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				Type[] genericParameterTypes = method.getGenericParameterTypes();
				if (parameterTypes.length == 1 && genericParameterTypes.length == 1) {
					Class<?> type = parameterTypes[0];
					Class<?> elementType = getElementType(genericParameterTypes[0]);
					String name = property.name();
					String label = property.label();
					String description = property.description();
					boolean required = property.required();
					boolean multiple = elementType != null;
					boolean ordered = (type.isArray() || List.class.isAssignableFrom(type))
							&& property.ordering() == ConfigurationProperty.OrderPolicy.DEFAULT;

					propertyDefinitions
							.add(createPropertyDefinition(activityURI, name, label, description,
									required, multiple, ordered, multiple ? elementType : type));
				}

			}
		}
		return propertyDefinitions;
	}

	private PropertyDefinition createPropertyDefinition(URI activityURI, String name, String label,
			String description, boolean required, boolean multiple, boolean ordered, Class<?> type) {
		URI predicate = activityURI.resolve("#" + name);
		if (type.isEnum()) {
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XSD_STRING, name,
					label, description, required, multiple, ordered, getOptions(type));
		} else if (type.equals(String.class)) {
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XSD_STRING, name,
					label, description, required, multiple, ordered);
		} else if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XSD_INT, name, label,
					description, required, multiple, ordered);
		} else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XSD_LONG, name, label,
					description, required, multiple, ordered);
		} else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XSD_FLOAT, name, label,
					description, required, multiple, ordered);
		} else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XSD_DOUBLE, name,
					label, description, required, multiple, ordered);
		} else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XSD_BOOLEAN, name,
					label, description, required, multiple, ordered);
		} else if (type.equals(URI.class)) {
			return new PropertyReferenceDefinition(predicate, name,
					label, description, required, multiple, ordered);
		} else {
			ConfigurationBean configurationBean = type.getAnnotation(ConfigurationBean.class);
			if (configurationBean == null) {
				List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
				URI typeURI = null;
				if (type.equals(ActivityInputPortDefinitionBean.class)) {
					typeURI = activityURI.resolve("#InputPortDefinition");
					propertyDefinitions.add(new PropertyResourceDefinition(activityURI
							.resolve("#definesInputPort"), null, "definesInputPort",
							"Activity Input Ports", "", true, true, false));

				} else if (type.equals(ActivityOutputPortDefinitionBean.class)) {
					typeURI = activityURI.resolve("#OutputPortDefinition");
					propertyDefinitions.add(new PropertyResourceDefinition(activityURI
							.resolve("#definesOutputPort"), null, "definesOutputPort",
							"Activity Output Ports", "", true, true, false));
				} else {
					// should throw an exception ? or XSD_ANY ?
					typeURI = URI.create("java:" + type.getName());
				}
				return new PropertyResourceDefinition(predicate, typeURI, name, label, description,
						required, multiple, ordered, propertyDefinitions);
			} else {
				URI typeURI = URI.create(configurationBean.uri());
				return new PropertyResourceDefinition(predicate, typeURI, name, label, description,
						required, multiple, ordered, createPropertyDefinitions(activityURI, type));
			}
		}
	}

	private Class<?> getElementType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			if (rawType instanceof Class) {
				Class<?> rawClass = (Class<?>) rawType;
				if (Collection.class.isAssignableFrom(rawClass)) {
					Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
					if (actualTypeArguments.length == 1) {
						Type actualTypeArgument = actualTypeArguments[0];
						if (actualTypeArgument instanceof Class) {
							return (Class<?>) actualTypeArgument;
						}
					}
				}
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			Type componentType = genericArrayType.getGenericComponentType();
			if (componentType instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) componentType;
				Type rawType = parameterizedType.getRawType();
				if (rawType instanceof Class) {
					return (Class<?>) rawType;
				}
			}
		} else if (type instanceof Class) {
			Class<?> classType = (Class<?>) type;
			if (classType.isArray()) {
				return classType.getComponentType();
			}
		}
		return null;
	}

	private Method getPropertySetMethod(Class<?> configurationClass, String name) {
		for (Method method : configurationClass.getMethods()) {
			ConfigurationProperty property = method.getAnnotation(ConfigurationProperty.class);
			if (property != null) {
				if (property.name().equals(name)) {
					return method;
				}
			}
		}
		return null;
	}

	private Class<?> getCollectionImplementation(Class<?> collectionClass) {
		if (collectionClass.isInterface() || Modifier.isAbstract(collectionClass.getModifiers())) {
			if (SortedSet.class.isAssignableFrom(collectionClass)) {
				return TreeSet.class;
			} else if (Set.class.isAssignableFrom(collectionClass)) {
				return HashSet.class;
			} else {
				return LinkedList.class;
			}
		}
		return collectionClass;
	}

	private LinkedHashSet<String> getOptions(Class<?> enumType) {
		List<String> options = new ArrayList<String>();
		for (Object option : enumType.getEnumConstants()) {
			options.add(option.toString());
		}
		return new LinkedHashSet<String>(options);
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
