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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.impl.EditsImpl;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyLiteralDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyList;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;

/**
 * 
 * @author David Withers
 */
public class ActivityServiceImplTest {

	public static final String annotatedBeanURI = "test://ns.taverna.org.uk/activity/annotated";

	public static final String nonAnnotatedBeanURI = "test://ns.taverna.org.uk/activity/nonannotated";

	private ActivityServiceImpl activityServiceImpl;
	
	private Configuration configuration;
	
	private uk.org.taverna.scufl2.api.activity.Activity activity;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		activityServiceImpl = new ActivityServiceImpl();
		List<ActivityFactory> activityFactories = new ArrayList<ActivityFactory>();
		activityFactories.add(new ActivityFactory() {
			public Activity<Object> createActivity() {
				return new AbstractActivity<Object>() {
					Object configuration;					
					public void configure(Object configuration) throws ActivityConfigurationException {
						this.configuration = configuration;
					}
					public Object getConfiguration() {
						return configuration;
					}					
				};
			}

			public Object createActivityConfiguration() {
				return new Object();
			}

			public URI getActivityURI() {
				return URI.create(nonAnnotatedBeanURI);
			}

		});
		activityFactories.add(new ActivityFactory() {
			public Activity<Object> createActivity() {
				return new AbstractActivity<Object>() {
					Object configuration;					
					public void configure(Object configuration) throws ActivityConfigurationException {
						this.configuration = configuration;
					}
					public Object getConfiguration() {
						return configuration;
					}					
				};
			}

			public Object createActivityConfiguration() {
				return new ActivityTestBean();
			}

			public URI getActivityURI() {
				return URI.create(annotatedBeanURI);
			}

		});
		activityServiceImpl.setActivityFactories(activityFactories);
		
		activity = new uk.org.taverna.scufl2.api.activity.Activity();
		activity.setConfigurableType(URI.create(annotatedBeanURI));
		
		configuration = new Configuration();
		configuration.setConfigures(activity);
		Profile profile = new Profile();
		profile.setParent(new WorkflowBundle());
		configuration.setParent(profile);
		
		PropertyResource propertyResource = configuration.getPropertyResource();
		propertyResource.setTypeURI(URI.create(annotatedBeanURI + "#ConfigType"));
		PropertyResource propertyResource2 = new PropertyResource(URI.create(annotatedBeanURI + "/configuration2"));
		propertyResource2.addProperty(URI.create(annotatedBeanURI + "#stringType2"), new PropertyLiteral("string value 2"));
		PropertyResource propertyResource3 = new PropertyResource(URI.create(annotatedBeanURI + "/configuration2"));
		propertyResource3.addProperty(URI.create(annotatedBeanURI + "#stringType2"), new PropertyLiteral("string value 3"));
		PropertyResource propertyResource4 = new PropertyResource(URI.create(annotatedBeanURI + "/configuration2"));
		propertyResource4.addProperty(URI.create(annotatedBeanURI + "#stringType2"), new PropertyLiteral("string value 4"));
		
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#stringType"), new PropertyLiteral("string value"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#optionalStringType"), new PropertyLiteral("optional string value"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#integerType"), new PropertyLiteral(5));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#longType"), new PropertyLiteral(12l));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#floatType"), new PropertyLiteral(1.2f));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#doubleType"), new PropertyLiteral(36.2d));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#booleanType"), new PropertyLiteral(false));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#enumType"), new PropertyLiteral("A"));
		
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#beanType"), propertyResource2);
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#beanType2"), propertyResource4);

		PropertyList propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("array element 1"));
		propertyList.add(new PropertyLiteral("array element 2"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#arrayType"), propertyList);
		propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("1"));
		propertyList.add(new PropertyLiteral("2"));
		propertyList.add(new PropertyLiteral("3"));
		propertyList.add(new PropertyLiteral("4"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#listType"), propertyList);
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#unorderedListType"), new PropertyLiteral("a"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#unorderedListType"), new PropertyLiteral("b"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#unorderedListType"), new PropertyLiteral("c"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#setType"), new PropertyLiteral("x"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#setType"), new PropertyLiteral("y"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#setType"), new PropertyLiteral("z"));

		propertyList = new PropertyList();
		propertyList.add(propertyResource2);
		propertyList.add(propertyResource3);
		propertyList.add(propertyResource4);
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#arrayOfBeanType"), propertyList);
		propertyList = new PropertyList();
		propertyList.add(propertyResource3);
		propertyList.add(propertyResource4);
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#listOfBeanType"), propertyList);
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#setOfBeanType"), propertyResource2);
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#setOfBeanType"), propertyResource3);
		
		propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("A"));
		propertyList.add(new PropertyLiteral("B"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#arrayOfEnumType"), propertyList);
		propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("B"));
		propertyList.add(new PropertyLiteral("A"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#listOfEnumType"), propertyList);
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#setOfEnumType"), new PropertyLiteral("A"));
		propertyResource.addProperty(URI.create(annotatedBeanURI + "#setOfEnumType"), new PropertyLiteral("B"));
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.activity.impl.ActivityServiceImpl#getActivityURIs()}.
	 */
	@Test
	public void testGetActivityURIs() {
		assertEquals(
				Arrays.asList(new URI[] { URI.create(nonAnnotatedBeanURI),
						URI.create(annotatedBeanURI) }), activityServiceImpl.getActivityURIs());
		assertEquals(
				Arrays.asList(new URI[] { URI.create(nonAnnotatedBeanURI),
						URI.create(annotatedBeanURI) }), activityServiceImpl.getActivityURIs());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.activity.impl.ActivityServiceImpl#activityExists(java.net.URI)}
	 * .
	 */
	@Test
	public void testActivityExists() {
		assertTrue(activityServiceImpl.activityExists(URI.create(annotatedBeanURI)));
		assertTrue(activityServiceImpl.activityExists(URI.create(annotatedBeanURI)));
		assertTrue(activityServiceImpl.activityExists(URI.create(nonAnnotatedBeanURI)));
		assertFalse(activityServiceImpl.activityExists(URI
				.create("test://ns.taverna.org.uk/activities/nonExistantBean")));
		assertTrue(activityServiceImpl.activityExists(URI.create(nonAnnotatedBeanURI)));
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.activity.impl.ActivityServiceImpl#getActivityConfigurationDefinition(java.net.URI)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetActivityConfigurationDefinition() throws Exception {
		ConfigurationDefinition configurationDefinition = activityServiceImpl.getActivityConfigurationDefinition(URI
				.create(annotatedBeanURI));
		PropertyResourceDefinition propertyResourceDefinition = configurationDefinition.getPropertyResourceDefinition();

		assertEquals(URI.create(annotatedBeanURI), configurationDefinition.getConfigurableType());
		assertEquals(URI.create(annotatedBeanURI + "#ConfigType"), propertyResourceDefinition.getTypeURI());

		int definitionCount = 0;

		PropertyDefinition definition = propertyResourceDefinition.getPropertyDefinition(URI
				.create(annotatedBeanURI + "#stringType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#stringType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#optionalStringType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#optionalStringType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertFalse(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#integerType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#integerType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_INT, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#longType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#longType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_LONG, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#floatType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#floatType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_FLOAT, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#doubleType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#doubleType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_DOUBLE, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#booleanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#booleanType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_BOOLEAN, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#enumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#enumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[] { "A", "B" }, definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#beanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyResourceDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#beanType"),
				definition.getPredicate());
		assertEquals(URI.create(annotatedBeanURI + "/configuration2"), ((PropertyResourceDefinition) definition).getTypeURI());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		assertEquals(1, ((PropertyResourceDefinition) definition).getPropertyDefinitions().size());
		definition = ((PropertyResourceDefinition) definition).getPropertyDefinition(URI
				.create(annotatedBeanURI + "#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#beanType2"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyResourceDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#beanType2"),
				definition.getPredicate());
		assertEquals(URI.create(annotatedBeanURI + "/configuration2"), ((PropertyResourceDefinition) definition).getTypeURI());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		assertEquals(1, ((PropertyResourceDefinition) definition).getPropertyDefinitions().size());
		definition = ((PropertyResourceDefinition) definition).getPropertyDefinition(URI
				.create(annotatedBeanURI + "#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#arrayType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#arrayType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#listType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#listType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#unorderedListType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#unorderedListType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#setType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#setType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#arrayOfBeanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyResourceDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#arrayOfBeanType"),
				definition.getPredicate());
		assertEquals(URI.create(annotatedBeanURI + "/configuration2"), ((PropertyResourceDefinition) definition).getTypeURI());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());
		definition = ((PropertyResourceDefinition) definition).getPropertyDefinition(URI
				.create(annotatedBeanURI + "#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#listOfBeanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyResourceDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#listOfBeanType"),
				definition.getPredicate());
		assertEquals(URI.create(annotatedBeanURI + "/configuration2"), ((PropertyResourceDefinition) definition).getTypeURI());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());
		definition = ((PropertyResourceDefinition) definition).getPropertyDefinition(URI
				.create(annotatedBeanURI + "#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#setOfBeanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyResourceDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#setOfBeanType"),
				definition.getPredicate());
		assertEquals(URI.create(annotatedBeanURI + "/configuration2"), ((PropertyResourceDefinition) definition).getTypeURI());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		definition = ((PropertyResourceDefinition) definition).getPropertyDefinition(URI
				.create(annotatedBeanURI + "#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[0], definition.getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		
		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#arrayOfEnumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#arrayOfEnumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[] {"A", "B"}, definition.getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#listOfEnumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#listOfEnumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[] {"A", "B"}, definition.getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(URI.create(annotatedBeanURI
				+ "#setOfEnumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(URI.create(annotatedBeanURI + "#setOfEnumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertArrayEquals(new String[] {"A", "B"}, definition.getOptions());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());


		assertEquals(definitionCount, propertyResourceDefinition.getPropertyDefinitions().size());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.activity.impl.ActivityServiceImpl#getActivityConfigurationDefinition(java.net.URI)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test(expected = uk.org.taverna.platform.activity.ActivityConfigurationException.class)
	public void testGetActivityConfigurationDefinitionWithException() throws Exception {
		activityServiceImpl.getActivityConfigurationDefinition(URI.create(nonAnnotatedBeanURI));
	}
	
	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.activity.impl.ActivityServiceImpl#createActivity(java.net.URI, uk.org.taverna.scufl2.api.configurations.Configuration, java.util.Set, java.util.Set)}
	 * .
	 */
	@Test
	public void testCreateActivity() throws Exception {
		Activity<?> activity2 = activityServiceImpl.createActivity(URI.create(annotatedBeanURI), configuration);
		assertNotNull(activity2);
		Object configuration2 = activity2.getConfiguration();
		assertTrue(configuration2 instanceof ActivityTestBean);
		ActivityTestBean testBean = (ActivityTestBean) configuration2;
		ActivityTestBean2 testBean2 = new ActivityTestBean2();
		testBean2.stringType2 = "string value 2";
		ActivityTestBean2 testBean3 = new ActivityTestBean2();
		testBean3.stringType2 = "string value 3";
		ActivityTestBean2 testBean4 = new ActivityTestBean2();
		testBean4.stringType2 = "string value 4";
		assertEquals("string value", testBean.stringType);
		assertEquals("optional string value", testBean.optionalStringType);
		assertEquals(5, testBean.integerType);
		assertEquals(12l, testBean.longType);
		assertEquals(1.2f, testBean.floatType, 0.0001);
		assertEquals(36.2d, testBean.doubleType, 0.0001);
		assertEquals(false, testBean.booleanType);
		assertEquals(ActivityTestEnum.A, testBean.enumType);
		assertEquals(testBean2, testBean.beanType);
		assertEquals(testBean4, testBean.beanType2);
		assertArrayEquals(new String[] {"array element 1", "array element 2"}, testBean.arrayType);
		assertEquals(Arrays.asList("1","2","3","4"), testBean.listType);
		assertEquals(new HashSet<String>(Arrays.asList("x","y","z")), testBean.setType);
		assertEquals(Arrays.asList("1","2","3","4"), testBean.listType);
		assertEquals(new HashSet<String>(Arrays.asList("a","b","c")), new HashSet<String>(testBean.unorderedListType));
		assertArrayEquals(new ActivityTestBean2[] {testBean2, testBean3,testBean4}, testBean.arrayOfBeanType);
		assertEquals(Arrays.asList(testBean3,testBean4), testBean.listOfBeanType);
		assertEquals(new HashSet<ActivityTestBean2>(Arrays.asList(testBean2,testBean3)), testBean.setOfBeanType);
		assertArrayEquals(new ActivityTestEnum[] {ActivityTestEnum.A, ActivityTestEnum.B}, testBean.arrayOfEnumType);
		assertEquals(Arrays.asList(ActivityTestEnum.B, ActivityTestEnum.A), testBean.listOfEnumType);
		assertEquals(new HashSet<ActivityTestEnum>(Arrays.asList(ActivityTestEnum.B, ActivityTestEnum.A)), testBean.setOfEnumType);
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.activity.impl.ActivityServiceImpl#createActivity(java.net.URI, uk.org.taverna.scufl2.api.configurations.Configuration, java.util.Set, java.util.Set)}
	 * .
	 */
	@Test(expected = uk.org.taverna.platform.activity.ActivityConfigurationException.class)
	public void testCreateActivityWithException() throws Exception {
		activityServiceImpl.createActivity(URI.create(nonAnnotatedBeanURI), new Configuration());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.activity.impl.ActivityServiceImpl#setActivityFactories(java.util.List)}
	 * .
	 */
	@Test
	public void testSetActivityFactories() {
		assertTrue(activityServiceImpl.activityExists(URI.create(annotatedBeanURI)));
		assertTrue(activityServiceImpl.activityExists(URI.create(nonAnnotatedBeanURI)));
		assertFalse(activityServiceImpl.activityExists(URI.create("test://newBean")));
		activityServiceImpl.setActivityFactories(Arrays
				.asList(new ActivityFactory[] { new ActivityFactory() {
					public Activity<?> createActivity() {
						return null;
					}

					public Object createActivityConfiguration() {
						return null;
					}

					public URI getActivityURI() {
						return URI.create("test://newBean");
					}

				} }));
		assertFalse(activityServiceImpl.activityExists(URI.create(annotatedBeanURI)));
		assertFalse(activityServiceImpl.activityExists(URI.create(nonAnnotatedBeanURI)));
		assertTrue(activityServiceImpl.activityExists(URI.create("test://newBean")));
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.activity.impl.ActivityServiceImpl#setEdits(net.sf.taverna.t2.workflowmodel.Edits)}
	 * .
	 */
	@Test
	public void testSetEdits() {
		activityServiceImpl.setEdits(new EditsImpl());
	}

	/**
	 * Test method for
	 * {@link uk.org.taverna.platform.activity.impl.ActivityServiceImpl#addDynamicPorts(uk.org.taverna.scufl2.api.activity.Activity, uk.org.taverna.scufl2.api.configurations.Configuration)}
	 * .
	 */
	@Test
	@Ignore
	public void testAddDynamicPorts() {
		fail("Not yet implemented");
	}

}
