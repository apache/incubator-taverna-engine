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
import java.lang.reflect.TypeVariable;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sf.taverna.t2.workflowmodel.Configurable;
import net.sf.taverna.t2.workflowmodel.ConfigurationException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityFactory;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityPortsDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ConfigurationBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ConfigurationProperty;

import org.apache.log4j.Logger;

import uk.org.taverna.platform.activity.ActivityConfigurationException;
import uk.org.taverna.platform.activity.ActivityNotFoundException;
import uk.org.taverna.platform.activity.ActivityService;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyLiteralDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition;
import uk.org.taverna.scufl2.api.property.MultiplePropertiesException;
import uk.org.taverna.scufl2.api.property.PropertyException;
import uk.org.taverna.scufl2.api.property.PropertyList;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyNotFoundException;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.api.property.UnexpectedPropertyException;

public class ActivityServiceImpl implements ActivityService {

	private static Logger logger = Logger.getLogger(ActivityServiceImpl.class);

	private List<ActivityFactory> activityFactories;

	private Edits edits;

	// TODO inject Scufl2Tools as a service
	private Scufl2Tools scufl2Tools = new Scufl2Tools();

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
		return generateConfigurationDefinition(uri, factory.createActivityConfiguration()
				.getClass());
	}

	@Override
	public Activity<?> createActivity(URI uri, Configuration configuration/*,
			Set<InputActivityPort> inputs, Set<OutputActivityPort> outputs*/)
			throws ActivityNotFoundException, ActivityConfigurationException {
		ActivityFactory factory = getActivityFactory(uri);
		
		// check configuration is for the correct activity
		uk.org.taverna.scufl2.api.common.Configurable configurable = configuration.getConfigures();
		if (configurable instanceof uk.org.taverna.scufl2.api.activity.Activity) {
			uk.org.taverna.scufl2.api.activity.Activity scufl2Activity = (uk.org.taverna.scufl2.api.activity.Activity) configurable;
			if (!scufl2Activity.getConfigurableType().equals(uri)) {
				throw new ActivityConfigurationException(MessageFormat.format(
						"Expected a configuration for {0} but got a configuration for {1}", uri,
						scufl2Activity.getConfigurableType()));
			}
		} else {
			throw new ActivityConfigurationException("Configuration does not configure an Activity");
		}
		
		Activity<?> activity = factory.createActivity();
		activity.setEdits(edits);
		Object configurationBean = factory.createActivityConfiguration();

		try {
			setConfigurationProperties(configurationBean, configuration.getPropertyResource(), uri);
		} catch (PropertyException e) {
			throw new ActivityConfigurationException(e);
		}

		if (configurationBean instanceof ActivityPortsDefinitionBean) {
			// ActivityPortsDefinitionBean activityPortsDefinitionBean =
			// (ActivityPortsDefinitionBean) configurationBean;
			// // input port definitions
			// List<ActivityInputPortDefinitionBean> inputPortDefinitions = new
			// ArrayList<ActivityInputPortDefinitionBean>();
			// for (InputActivityPort inputActivityPort : inputs) {
			// ActivityInputPortDefinitionBean inputPortDefinitionBean = new
			// ActivityInputPortDefinitionBean();
			// inputPortDefinitionBean.setName(inputActivityPort.getName());
			// inputPortDefinitionBean.setDepth(inputActivityPort.getDepth());
			// inputPortDefinitionBean.setTranslatedElementType(String.class);
			// inputPortDefinitions.add(inputPortDefinitionBean);
			// }
			// // output port definitions
			// List<ActivityOutputPortDefinitionBean> outputPortDefinitions = new
			// ArrayList<ActivityOutputPortDefinitionBean>();
			// for (OutputActivityPort outputActivityPort : outputs) {
			// ActivityOutputPortDefinitionBean outputPortDefinitionBean = new
			// ActivityOutputPortDefinitionBean();
			// outputPortDefinitionBean.setName(outputActivityPort.getName());
			// outputPortDefinitionBean.setDepth(outputActivityPort.getDepth());
			// outputPortDefinitions.add(outputPortDefinitionBean);
			// }
			// activityPortsDefinitionBean.setInputPortDefinitions(inputPortDefinitions);
			// activityPortsDefinitionBean.setOutputPortDefinitions(outputPortDefinitions);
		} else {
			// for (InputActivityPort inputActivityPort : inputs) {
			// ActivityInputPort inputPort = edits.createActivityInputPort(
			// inputActivityPort.getName(), inputActivityPort.getDepth(), false, null,
			// null);
			// try {
			// edits.getAddActivityInputPortEdit(activity, inputPort).doEdit();
			// } catch (EditException e) {
			// throw new ActivityConfigurationException(e);
			// }
			// }
			// for (OutputActivityPort outputActivityPort : outputs) {
			// OutputPort outputPort = edits.createActivityOutputPort(
			// outputActivityPort.getName(), outputActivityPort.getDepth(),
			// outputActivityPort.getGranularDepth());
			// try {
			// edits.getAddActivityOutputPortEdit(activity, outputPort).doEdit();
			// } catch (EditException e) {
			// throw new ActivityConfigurationException(e);
			// }
			// }
		}

		try {
			((Configurable) activity).configure(configurationBean);
		} catch (ConfigurationException e) {
			throw new ActivityConfigurationException(e);
		}

		return activity;
	}

	private void setConfigurationProperties(Object configurationBean, PropertyResource propertyResource,
			URI uri) throws ActivityConfigurationException, PropertyException {
		try {
			Class<?> configurationClass = configurationBean.getClass();
			ConfigurationDefinition definition = generateConfigurationDefinition(uri, configurationClass);

			for (PropertyDefinition propertyDefinition : definition.getPropertyResourceDefinition().getPropertyDefinitions()) {
				URI predicate = propertyDefinition.getPredicate();
				Method method = getPropertySetMethod(configurationClass,
						propertyDefinition.getName());
				Class<?>[] parameterTypes = method.getParameterTypes();
				if (parameterTypes.length != 1) {
					throw new ActivityConfigurationException(MessageFormat.format(
							"Property set method has {0} parameters; expected 1",
							parameterTypes.length));
				}
				Class<?> parameterType = parameterTypes[0];
				List<Object> propertyValues = new ArrayList<Object>();
				URI type = null;
				if (propertyDefinition instanceof PropertyLiteralDefinition) {
					PropertyLiteralDefinition dataPropertyDefinition = (PropertyLiteralDefinition) propertyDefinition;
					List<PropertyLiteral> literals = new ArrayList<PropertyLiteral>();
					if (propertyDefinition.isOrdered()) {
						PropertyObject property = propertyResource.getProperty(predicate);
						if (property instanceof PropertyList) {
							PropertyList propertyList = (PropertyList) property;
							for (PropertyObject propertyObject : propertyList) {
								if (propertyObject instanceof PropertyLiteral) {
									literals.add((PropertyLiteral) propertyObject);
								} else {
									throw new UnexpectedPropertyException("Expected a PropertyList of PropertyLiteral", predicate, propertyResource);
								}
							}
						} else {
							throw new UnexpectedPropertyException("Expected a PropertyList", predicate, propertyResource);
						}
					} else {
						literals.addAll(propertyResource.getPropertiesAsLiterals(predicate));
					}
					if (literals.size() == 0 && propertyDefinition.isRequired()) {
						throw new PropertyNotFoundException(predicate, propertyResource);
					}
					if (literals.size() > 1 && !propertyDefinition.isMultiple()) {
						throw new MultiplePropertiesException(predicate, propertyResource);
					}
					type = dataPropertyDefinition.getLiteralType();
					for (PropertyLiteral literal : literals) {
						if (!literal.getLiteralType().equals(type)) {
							throw new ActivityConfigurationException(MessageFormat.format(
									"Expected property {0} to have type {1} but was {2}",
									propertyDefinition.getName(),
									dataPropertyDefinition.getLiteralType(),
									literal.getLiteralType()));
						}
						if (type.equals(PropertyLiteral.XSD_STRING)) {
							String literalValue = literal.getLiteralValue();
							if (parameterType.isEnum()) {
								for (Object enumConstant : parameterType.getEnumConstants()) {
									if (((Enum<?>) enumConstant).name().equals(literalValue)) {
										propertyValues.add(enumConstant);
										break;
									}
								}
							} else {
								propertyValues.add(literalValue);
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
					type = propertyResourceDefinition.getTypeURI();
					List<PropertyResource> resources = new ArrayList<PropertyResource>();
					if (propertyDefinition.isOrdered()) {
						PropertyObject property = propertyResource.getProperty(predicate);
						if (property instanceof PropertyList) {
							PropertyList propertyList = (PropertyList) property;
							for (PropertyObject propertyObject : propertyList) {
								if (propertyObject instanceof PropertyResource) {
									resources.add((PropertyResource) propertyObject);
								} else {
									throw new UnexpectedPropertyException("Expected a PropertyList of PropertyResource", predicate, propertyResource);
								}
							}
						} else {
							throw new UnexpectedPropertyException("Expected a PropertyList", predicate, propertyResource);
						}
					} else {
						resources.addAll(propertyResource.getPropertiesAsResources(predicate));
					}
					if (resources.isEmpty() && propertyDefinition.isRequired()) {
						throw new PropertyNotFoundException(predicate, propertyResource);
					}
					if (resources.size() > 1 && !propertyDefinition.isMultiple()) {
						throw new MultiplePropertiesException(predicate, propertyResource);
					}
					for (PropertyResource resource : resources) {
						Object configurationObject = parameterType.newInstance();
						setConfigurationProperties(configurationObject, resource, uri);
						propertyValues.add(configurationObject);
					}
				}
				if (propertyDefinition.isMultiple()) {
					if (Collection.class.isAssignableFrom(parameterType)) {
						Class<?> collectionImplementation = getCollectionImplementation(parameterType);
						Object collectionObject = collectionImplementation.getConstructor(Collection.class)
							.newInstance(propertyValues);
						method.invoke(configurationBean, collectionObject);
					} else if (parameterType.isArray()) {
						Object array = Array.newInstance(parameterType.getComponentType(), propertyValues.size());
						for (int i = 0; i < propertyValues.size(); i++) {
							Array.set(array, i, propertyValues.get(i));
						}
						method.invoke(configurationBean, array);
					} else {
						throw new ActivityConfigurationException(MessageFormat.format("Expected a Collection or an array but found {0}",
								parameterType));
					}
				} else {
					// TODO check that we have one value here
					for (Object object : propertyValues) {
						method.invoke(configurationBean, object);
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

	private Class<?> getCollectionImplementation(Class<?> collectionClass) {
		if (collectionClass.isInterface() || Modifier.isAbstract(collectionClass.getModifiers())) {
			if (Set.class.isAssignableFrom(collectionClass)) {
				return TreeSet.class;
			} else {
				return LinkedList.class;
			}
		}
		return collectionClass;
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

	@Override
	public void setActivityFactories(List<ActivityFactory> activityFactories) {
		this.activityFactories = activityFactories;
	}

	private ConfigurationDefinition generateConfigurationDefinition(URI uri,
			Class<?> configurationClass) throws ActivityConfigurationException {
		ConfigurationBean configurationBean = configurationClass
				.getAnnotation(ConfigurationBean.class);
		if (configurationBean == null) {
			// TODO special cases for Dataflow and Element (BioMART)
			throw new ActivityConfigurationException("Configuration bean is not annotated");
		}
		
		ConfigurationDefinition configurationDefinition = new ConfigurationDefinition(uri);
		PropertyResourceDefinition propertyResourceDefinition = configurationDefinition.getPropertyResourceDefinition();
		propertyResourceDefinition.setPropertyDefinitions(getPropertyDefinitions(uri,configurationClass));
		return configurationDefinition;
	}

	private List<PropertyDefinition> getPropertyDefinitions(URI activityURI,
			Class<?> configurationClass) {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		Method[] methods = configurationClass.getMethods();
		for (Method method : methods) {
			ConfigurationProperty property = method.getAnnotation(ConfigurationProperty.class);
			if (property != null && method.getParameterTypes().length == 1) {
				Type[] parameterTypes = method.getGenericParameterTypes();
				if (parameterTypes.length == 1) {
					Type type = parameterTypes[0];
					String name = property.name();
					String label = property.label();
					String description = property.description();
					boolean required = property.required();

					if (type instanceof ParameterizedType) {
						ParameterizedType parameterizedType = (ParameterizedType) type;
						Type rawType = parameterizedType.getRawType();
						if (rawType instanceof Class) {
							Class<?> rawClass = (Class<?>) rawType;
							if (Collection.class.isAssignableFrom(rawClass)) {
								Type[] actualTypeArguments = parameterizedType
										.getActualTypeArguments();
								if (actualTypeArguments.length == 1) {
									Type actualTypeArgument = actualTypeArguments[0];
									if (actualTypeArgument instanceof Class) {
										propertyDefinitions.add(createPropertyDefinition(
												activityURI, name, label, description, required,
												true, List.class.isAssignableFrom(rawClass),
												(Class<?>) actualTypeArgument));
									}
								}
							} else {
								propertyDefinitions.add(createPropertyDefinition(activityURI, name,
										label, description, required, false, false, rawClass));
							}
						}
					} else if (type instanceof GenericArrayType) {
						GenericArrayType genericArrayType = (GenericArrayType) type;
						Type componentType = genericArrayType.getGenericComponentType();
						if (componentType instanceof ParameterizedType) {
							ParameterizedType parameterizedType = (ParameterizedType) componentType;
							Type rawType = parameterizedType.getRawType();
							if (rawType instanceof Class) {
								propertyDefinitions.add(createPropertyDefinition(activityURI, name,
										label, description, required, true, true, (Class<?>) rawType));
							}
							// TODO else case
						} else if (componentType instanceof TypeVariable) {
							// TODO can't really support this case - throw an exception?
							propertyDefinitions.add(createPropertyDefinition(activityURI, name,
									label, description, required, true, true, Object.class));
						}
					} else if (type instanceof Class) {
						Class<?> classType = (Class<?>) type;
						if (classType.isArray()) {
							propertyDefinitions.add(createPropertyDefinition(activityURI, name,
									label, description, required, true, true,
									classType.getComponentType()));
						} else {
							propertyDefinitions.add(createPropertyDefinition(activityURI, name,
									label, description, required, false, false, classType));
						}
					}
					//TODO else case
				}

			}
		}
		return propertyDefinitions;
	}

	private PropertyDefinition createPropertyDefinition(URI activityURI, String name, String label,
			String description, boolean required, boolean multiple, boolean ordered, Class<?> type) {
		URI predicate = activityURI.resolve("#" + name);
		if (type.isEnum()) {
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XSD_STRING, name, label,
					description, required, multiple, ordered, getOptions(type));
		} else if (type.equals(String.class)) {
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XSD_STRING, name, label,
					description, required, multiple, ordered);
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
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XSD_DOUBLE, name, label,
					description, required, multiple, ordered);
		} else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XSD_BOOLEAN, name, label,
					description, required, multiple, ordered);
		} else {
			ConfigurationBean configurationBean = type.getAnnotation(ConfigurationBean.class);
			if (configurationBean == null) {
				// TODO special cases
				URI objectClass = URI.create("java:" + type.getName());
				return new PropertyResourceDefinition(predicate, objectClass, name, label,
						description, required, multiple, ordered);
			} else {
				URI objectClass = URI.create(configurationBean.uri());
				return new PropertyResourceDefinition(predicate, objectClass, name, label,
						description, required, multiple, ordered, getPropertyDefinitions(activityURI, type));
			}
		}
	}

	private String[] getOptions(Class<?> enumType) {
		List<String> options = new ArrayList<String>();
		for (Object option : enumType.getEnumConstants()) {
			options.add(option.toString());
		}
		return options.toArray(new String[options.size()]);
	}

	private ActivityFactory getActivityFactory(URI uri) throws ActivityNotFoundException {
		for (ActivityFactory activityFactory : activityFactories) {
			if (activityFactory.getActivityURI().equals(uri)) {
				return activityFactory;
			}
		}
		throw new ActivityNotFoundException("Could not find an activity for " + uri);
	}

	@Override
	public void setEdits(Edits edits) {
		this.edits = edits;
	}

	@Override
	public void addDynamicPorts(uk.org.taverna.scufl2.api.activity.Activity activity,
			Configuration configuration) throws ActivityNotFoundException,
			ActivityConfigurationException {
		createActivity(activity.getConfigurableType(), configuration);

	}

}
