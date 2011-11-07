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
package uk.org.taverna.platform.configuration;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationBean;
import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationProperty;

import org.jdom.input.DOMBuilder;

import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
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

/**
 * Utility methods for creating configuration definitions.
 *
 * @author David Withers
 */
public class ConfigurationUtils {

	private static final Map<Class<?>, List<PropertyDefinition>> propertyDefinitionsMap = new HashMap<Class<?>, List<PropertyDefinition>>();

	private static final URI SCUFL2 = URI.create("http://ns.taverna.org.uk/2010/scufl2#");

	private static final URITools uriTools = new URITools();

	public static List<PropertyDefinition> createPropertyDefinitions(Class<?> configurationClass) {
		if (!propertyDefinitionsMap.containsKey(configurationClass)) {
			List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
			propertyDefinitionsMap.put(configurationClass, propertyDefinitions);
			Method[] methods = configurationClass.getMethods();
			for (Method method : methods) {
				ConfigurationProperty property = method.getAnnotation(ConfigurationProperty.class);
				if (property != null && method.getParameterTypes().length == 1) {
					Class<?>[] parameterTypes = method.getParameterTypes();
					Type[] genericParameterTypes = method.getGenericParameterTypes();
					if (parameterTypes.length == 1 && genericParameterTypes.length == 1) {
						ConfigurationBean parentConfigBean = method.getDeclaringClass()
								.getAnnotation(ConfigurationBean.class);
						URI typeURI = URI.create(parentConfigBean.uri());
						Class<?> type = parameterTypes[0];
						Class<?> elementType = getElementType(genericParameterTypes[0]);
						String name = property.name();
						String label = property.label();
						String description = property.description();
						boolean required = property.required();
						boolean multiple = elementType != null;
						boolean ordered = (type.isArray() || List.class.isAssignableFrom(type))
								&& property.ordering() == ConfigurationProperty.OrderPolicy.DEFAULT;
						String uri = property.uri();

						propertyDefinitions.add(createPropertyDefinition(typeURI, name, label,
								description, required, multiple, ordered, multiple ? elementType
										: type, uri));
					}

				}
			}
		}
		return propertyDefinitionsMap.get(configurationClass);
	}

	private static PropertyDefinition createPropertyDefinition(URI baseUri, String name,
			String label, String description, boolean required, boolean multiple, boolean ordered,
			Class<?> type, String uri) {
		URI predicate;
		if (uri.isEmpty()) {
			predicate = baseUri.resolve("#" + name);
		} else {
			predicate = URI.create(uri);
		}
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
		} else if (type.equals(org.w3c.dom.Element.class) || type.equals(org.jdom.Element.class)) {
			return new PropertyLiteralDefinition(predicate, PropertyLiteral.XML_LITERAL, name,
					label, description, required, multiple, ordered);
		} else if (type.equals(URI.class)) {
			return new PropertyReferenceDefinition(predicate, name, label, description, required,
					multiple, ordered);
		} else {
			ConfigurationBean configurationBean = type.getAnnotation(ConfigurationBean.class);
			List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
			URI typeURI = null;
			if (configurationBean == null) {
				// TODO should throw an exception ? or XSD_ANY ?
				typeURI = URI.create("java:" + type.getName());
			} else {
				typeURI = URI.create(configurationBean.uri());
				if (type.equals(ActivityInputPortDefinitionBean.class)) {
					typeURI = SCUFL2.resolve("#InputPortDefinition");
					propertyDefinitions.add(new PropertyResourceDefinition(SCUFL2
							.resolve("#definesInputPort"), null, "definesInputPort",
							"Activity Input Ports", "", true, true, false));

				} else if (type.equals(ActivityOutputPortDefinitionBean.class)) {
					typeURI = SCUFL2.resolve("#OutputPortDefinition");
					propertyDefinitions.add(new PropertyResourceDefinition(SCUFL2
							.resolve("#definesOutputPort"), null, "definesOutputPort",
							"Activity Output Ports", "", true, true, false));
				}
				propertyDefinitions.addAll(createPropertyDefinitions(type));
			}
			return new PropertyResourceDefinition(predicate, typeURI, name, label, description,
					required, multiple, ordered, propertyDefinitions);
		}
	}

	private static Class<?> getElementType(Type type) {
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

	private static Method getPropertySetMethod(Class<?> configurationClass, URI predicate) {
		for (Method method : configurationClass.getMethods()) {
			ConfigurationProperty property = method.getAnnotation(ConfigurationProperty.class);
			if (property != null) {
				URI uri;
				if (!property.uri().isEmpty()) {
					uri = URI.create(property.uri());
				} else {
					ConfigurationBean configBean = method.getDeclaringClass().getAnnotation(
							ConfigurationBean.class);
					uri = URI.create(configBean.uri()).resolve("#" + property.name());
				}
				if (uri.equals(predicate)) {
					return method;
				}
			}
		}
		return null;
	}

	private static Class<?> getCollectionImplementation(Class<?> collectionClass) {
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

	private static LinkedHashSet<String> getOptions(Class<?> enumType) {
		List<String> options = new ArrayList<String>();
		for (Object option : enumType.getEnumConstants()) {
			options.add(option.toString());
		}
		return new LinkedHashSet<String>(options);
	}

	public static void setConfigurationProperties(Object configurationBean,
			Configuration configuration, PropertyResource resource,
			PropertyResourceDefinition resourceDefinition, URI uri, WorkflowBundle bundle)
			throws ConfigurationException, PropertyException {
		try {
			for (PropertyDefinition propertyDefinition : resourceDefinition
					.getPropertyDefinitions()) {
				URI predicate = propertyDefinition.getPredicate();
				// special cases
				if (predicate.equals(SCUFL2.resolve("#definesInputPort"))) {
					URI resourceURI = resource.getPropertyAsResourceURI(predicate);
					URI configUri = uriTools.uriForBean(configuration);
					WorkflowBean workflowBean = uriTools.resolveUri(configUri.resolve(resourceURI),
							bundle);
					if (!(workflowBean instanceof InputActivityPort)) {
						throw new UnexpectedPropertyException("Expected reference to input port",
								SCUFL2.resolve("#definesInputPort"), resource);
					}
					if (!(configurationBean instanceof ActivityInputPortDefinitionBean)) {
						throw new ConfigurationException(
								"Expected an ActivityInputPortDefinitionBean");
					}
					ActivityInputPortDefinitionBean inputPortDefinitionBean = (ActivityInputPortDefinitionBean) configurationBean;
					InputActivityPort inputActivityPort = (InputActivityPort) workflowBean;
					inputPortDefinitionBean.setName(inputActivityPort.getName());
					inputPortDefinitionBean.setDepth(inputActivityPort.getDepth());
					// TODO TranslatedElementType should be set from the property
					inputPortDefinitionBean.setTranslatedElementType(String.class);
				} else if (predicate.equals(SCUFL2.resolve("#definesOutputPort"))) {
					URI resourceURI = resource.getPropertyAsResourceURI(predicate);
					URI configUri = uriTools.uriForBean(configuration);
					WorkflowBean workflowBean = uriTools.resolveUri(configUri.resolve(resourceURI),
							bundle);
					if (!(workflowBean instanceof OutputActivityPort)) {
						throw new UnexpectedPropertyException("Expected reference to output port",
								SCUFL2.resolve("#definesOutputPort"), resource);
					}
					if (!(configurationBean instanceof ActivityOutputPortDefinitionBean)) {
						throw new ConfigurationException(
								"Expected an ActivityOutputPortDefinitionBean");
					}
					ActivityOutputPortDefinitionBean outputPortDefinitionBean = (ActivityOutputPortDefinitionBean) configurationBean;
					OutputActivityPort outputActivityPort = (OutputActivityPort) workflowBean;
					outputPortDefinitionBean.setName(outputActivityPort.getName());
					outputPortDefinitionBean.setDepth(outputActivityPort.getDepth());
				} else {
					setConfigurationProperty(configurationBean, configuration, resource,
							propertyDefinition, uri, bundle);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new ConfigurationException(e);
		} catch (SecurityException e) {
			throw new ConfigurationException(e);
		}
	}

	private static void setConfigurationProperty(Object configurationBean,
			Configuration configuration, PropertyResource resource,
			PropertyDefinition propertyDefinition, URI uri, WorkflowBundle bundle)
			throws ConfigurationException, PropertyException {
		try {
			Class<?> configurationClass = configurationBean.getClass();
			URI predicate = propertyDefinition.getPredicate();

			Method method = getPropertySetMethod(configurationClass, predicate);
			Class<?>[] parameterTypes = method.getParameterTypes();
			Type[] genericParameterTypes = method.getGenericParameterTypes();
			if (parameterTypes.length != 1 || genericParameterTypes.length != 1) {
				throw new ConfigurationException(
						MessageFormat.format("Property set method has {0} parameters; expected 1",
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
				List<PropertyLiteral> literals = getProperties(predicate, propertyDefinition,
						resource, PropertyLiteral.class);
				for (PropertyLiteral literal : literals) {
					URI literalType = literal.getLiteralType();
					if (!literalType.equals(type)) {
						throw new ConfigurationException(MessageFormat.format(
								"Expected property {0} to have type {1} but was {2}",
								propertyDefinition.getName(),
								dataPropertyDefinition.getLiteralType(), literal.getLiteralType()));
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
					} else if (type.equals(PropertyLiteral.XML_LITERAL)) {
						if (propertyType.equals(org.w3c.dom.Element.class)) {
							propertyValues.add(literal.getLiteralValueAsElement());
						} else if (propertyType.equals(org.jdom.Element.class)) {
							propertyValues.add(new DOMBuilder().build(literal
									.getLiteralValueAsElement()));
						}
					} else {
						// TODO
					}
				}
			} else if (propertyDefinition instanceof PropertyResourceDefinition) {
				PropertyResourceDefinition propertyResourceDefinition = (PropertyResourceDefinition) propertyDefinition;
				List<PropertyResource> resources = getProperties(predicate, propertyDefinition,
						resource, PropertyResource.class);
				for (PropertyResource resourceElement : resources) {
					Object configurationObject = propertyType.newInstance();
					setConfigurationProperties(configurationObject, configuration, resourceElement,
							propertyResourceDefinition, uri, bundle);
					propertyValues.add(configurationObject);
				}
			} else if (propertyDefinition instanceof PropertyReferenceDefinition) {
				List<PropertyReference> references = getProperties(predicate, propertyDefinition,
						resource, PropertyReference.class);
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
					throw new ConfigurationException(MessageFormat.format(
							"Expected a Collection or an array but found {0}", parameterType));
				}
			} else {
				// TODO check that we have one value here
				for (Object object : propertyValues) {
					method.invoke(configurationBean, object);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new ConfigurationException(e);
		} catch (SecurityException e) {
			throw new ConfigurationException(e);
		} catch (InstantiationException e) {
			throw new ConfigurationException(e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(e);
		} catch (InvocationTargetException e) {
			throw new ConfigurationException(e);
		} catch (NoSuchMethodException e) {
			throw new ConfigurationException(e);
		}
	}

	private static <T extends PropertyObject> List<T> getProperties(URI predicate,
			PropertyDefinition propertyDefinition, PropertyResource propertyResource,
			Class<T> propertyType) throws PropertyNotFoundException, MultiplePropertiesException,
			UnexpectedPropertyException {
		List<T> properties = new ArrayList<T>();
		if (propertyDefinition.isOrdered()) {
			if (propertyResource.hasProperty(predicate)) {
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
			} else if (propertyDefinition.isRequired()) {
				throw new PropertyNotFoundException(predicate, propertyResource);
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

}
