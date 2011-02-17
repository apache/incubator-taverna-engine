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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import uk.org.taverna.scufl2.api.configurations.PropertyReferenceDefinition;
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
	
	public static final URI activityTestBeanURI = URI.create(annotatedBeanURI + "#Configuration");
	public static final URI activityTestBean2URI = URI.create(annotatedBeanURI + "/configuration2");
	public static final URI subclassTestBeanURI = URI.create(annotatedBeanURI + "/subclass");

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
		propertyResource.setTypeURI(activityTestBeanURI.resolve("#Configuration"));
		PropertyResource propertyResource2 = new PropertyResource();
		propertyResource2.setTypeURI(activityTestBean2URI);
		propertyResource2.addProperty(activityTestBean2URI.resolve("#stringType"), new PropertyLiteral("string value 2.1"));
		propertyResource2.addProperty(activityTestBean2URI.resolve("#stringType2"), new PropertyLiteral("string value 2.2"));

		PropertyResource propertyResource3 = new PropertyResource();
		propertyResource3.setTypeURI(activityTestBean2URI);
		propertyResource3.addProperty(activityTestBean2URI.resolve("#stringType"), new PropertyLiteral("string value 3.1"));
		propertyResource3.addProperty(activityTestBean2URI.resolve("#stringType2"), new PropertyLiteral("string value 3.2"));

		PropertyResource propertyResource4 = new PropertyResource();
		propertyResource4.setTypeURI(activityTestBean2URI);
		propertyResource4.addProperty(activityTestBean2URI.resolve("#stringType"), new PropertyLiteral("string value 4.1"));
		propertyResource4.addProperty(activityTestBean2URI.resolve("#stringType2"), new PropertyLiteral("string value 4.2"));
		
		PropertyResource propertyResource5 = new PropertyResource();
		propertyResource5.setTypeURI(subclassTestBeanURI);
		propertyResource5.addProperty(activityTestBean2URI.resolve("#stringType2"), new PropertyLiteral("string value 5.1"));
		propertyResource5.addProperty(subclassTestBeanURI.resolve("#stringType2"), new PropertyLiteral("string value 5.2"));
		propertyResource5.addProperty(subclassTestBeanURI.resolve("#overriding"), new PropertyLiteral("string value 5.3"));
		
		
		propertyResource.addProperty(activityTestBeanURI.resolve("#stringType"), new PropertyLiteral("string value"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#optionalStringType"), new PropertyLiteral("optional string value"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#integerType"), new PropertyLiteral(5));
		propertyResource.addProperty(activityTestBeanURI.resolve("#longType"), new PropertyLiteral(12l));
		propertyResource.addProperty(activityTestBeanURI.resolve("#floatType"), new PropertyLiteral(1.2f));
		propertyResource.addProperty(activityTestBeanURI.resolve("#doubleType"), new PropertyLiteral(36.2d));
		propertyResource.addProperty(activityTestBeanURI.resolve("#booleanType"), new PropertyLiteral(false));
		propertyResource.addProperty(activityTestBeanURI.resolve("#enumType"), new PropertyLiteral("A"));
		
		propertyResource.addProperty(activityTestBeanURI.resolve("#beanType"), propertyResource2);
		propertyResource.addProperty(activityTestBeanURI.resolve("#beanType2"), propertyResource4);
		propertyResource.addProperty(activityTestBeanURI.resolve("#subclass"), propertyResource5);
		

		PropertyList propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("array element 1"));
		propertyList.add(new PropertyLiteral("array element 2"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#arrayType"), propertyList);
		propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("1"));
		propertyList.add(new PropertyLiteral("2"));
		propertyList.add(new PropertyLiteral("3"));
		propertyList.add(new PropertyLiteral("4"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#listType"), propertyList);
		propertyResource.addProperty(activityTestBeanURI.resolve("#unorderedListType"), new PropertyLiteral("a"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#unorderedListType"), new PropertyLiteral("b"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#unorderedListType"), new PropertyLiteral("c"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#setType"), new PropertyLiteral("x"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#setType"), new PropertyLiteral("y"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#setType"), new PropertyLiteral("z"));

		propertyList = new PropertyList();
		propertyList.add(propertyResource2);
		propertyList.add(propertyResource3);
		propertyList.add(propertyResource4);
		propertyResource.addProperty(activityTestBeanURI.resolve("#arrayOfBeanType"), propertyList);
		propertyList = new PropertyList();
		propertyList.add(propertyResource3);
		propertyList.add(propertyResource4);
		propertyResource.addProperty(activityTestBeanURI.resolve("#listOfBeanType"), propertyList);
		propertyResource.addProperty(activityTestBeanURI.resolve("#setOfBeanType"), propertyResource2);
		propertyResource.addProperty(activityTestBeanURI.resolve("#setOfBeanType"), propertyResource3);
		
		propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("A"));
		propertyList.add(new PropertyLiteral("B"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#arrayOfEnumType"), propertyList);
		propertyList = new PropertyList();
		propertyList.add(new PropertyLiteral("B"));
		propertyList.add(new PropertyLiteral("A"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#listOfEnumType"), propertyList);
		propertyResource.addProperty(activityTestBeanURI.resolve("#setOfEnumType"), new PropertyLiteral("A"));
		propertyResource.addProperty(activityTestBeanURI.resolve("#setOfEnumType"), new PropertyLiteral("B"));
		
		propertyResource.addPropertyReference(activityTestBeanURI.resolve("#uriType"), URI.create("http://www.example.com/"));
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
		assertEquals(activityTestBeanURI, propertyResourceDefinition.getTypeURI());

		int definitionCount = 0;

		PropertyDefinition definition = propertyResourceDefinition.getPropertyDefinition(
				activityTestBeanURI.resolve("#stringType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#stringType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve("#optionalStringType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#optionalStringType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertFalse(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#integerType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#integerType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_INT, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#longType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#longType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_LONG, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#floatType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#floatType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_FLOAT, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#doubleType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#doubleType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_DOUBLE, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#booleanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#booleanType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_BOOLEAN, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#enumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#enumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new LinkedHashSet<String>(Arrays.asList("A", "B")), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		PropertyResourceDefinition beanType = (PropertyResourceDefinition) propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#beanType"));
		definitionCount++;
		assertNotNull(beanType);
		assertTrue(beanType instanceof PropertyResourceDefinition);
		assertEquals(activityTestBeanURI.resolve("#beanType"),
				beanType.getPredicate());
		assertEquals(URI.create(annotatedBeanURI + "/configuration2"), beanType.getTypeURI());
		assertEquals("", beanType.getDescription());
		assertEquals("", beanType.getLabel());
		assertFalse(beanType.isMultiple());
		assertFalse(beanType.isOrdered());
		assertTrue(beanType.isRequired());
		assertEquals(2, beanType.getPropertyDefinitions().size());
		

		definition = beanType.getPropertyDefinition(activityTestBean2URI.resolve("#stringType"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBean2URI.resolve("#stringType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		
		definition = beanType.getPropertyDefinition(activityTestBean2URI.resolve("#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBean2URI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		

		// Test ActivTestBean2
		PropertyResourceDefinition beanType2 = (PropertyResourceDefinition) propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#beanType2"));
		definitionCount++;
		assertNotNull(beanType2);
		assertTrue(beanType2 instanceof PropertyResourceDefinition);
		assertEquals(activityTestBeanURI.resolve("#beanType2"),
				beanType2.getPredicate());
		assertEquals(activityTestBean2URI, beanType2.getTypeURI());
		assertEquals("", beanType2.getDescription());
		assertEquals("", beanType2.getLabel());
		assertFalse(beanType2.isMultiple());
		assertFalse(beanType2.isOrdered());
		assertTrue(beanType2.isRequired());
		assertEquals(2, beanType2.getPropertyDefinitions().size());
		
		definition = beanType2.getPropertyDefinition(activityTestBean2URI.resolve("#stringType"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBean2URI.resolve("#stringType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		
		definition = beanType2.getPropertyDefinition(activityTestBean2URI.resolve("#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBean2URI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		
		

		PropertyResourceDefinition subclass = (PropertyResourceDefinition) propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#subclass"));
		definitionCount++;
		assertNotNull(subclass);
		assertTrue(subclass instanceof PropertyResourceDefinition);
		assertEquals(activityTestBeanURI.resolve("#subclass"),
				subclass.getPredicate());
		assertEquals(URI.create(annotatedBeanURI + "/subclass"), ((PropertyResourceDefinition) subclass).getTypeURI());
		assertEquals("", subclass.getDescription());
		assertEquals("", subclass.getLabel());
		assertFalse(subclass.isMultiple());
		assertFalse(subclass.isOrdered());
		assertTrue(subclass.isRequired());
		assertEquals(3, ((PropertyResourceDefinition) subclass).getPropertyDefinitions().size());
		
		definition = subclass.getPropertyDefinition(activityTestBean2URI.resolve("#stringType"));
		assertNull(definition);
		definition = subclass.getPropertyDefinition(subclassTestBeanURI.resolve("#stringType"));
		assertNull(definition);
		definition = subclass.getPropertyDefinition(subclassTestBeanURI.resolve("#overriding"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(subclassTestBeanURI.resolve("#overriding"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		
		
		definition = ((PropertyResourceDefinition) subclass).getPropertyDefinition(activityTestBean2URI.resolve("#stringType2"));		
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBean2URI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());


		definition = ((PropertyResourceDefinition) subclass).getPropertyDefinition(subclassTestBeanURI.resolve("#stringType2"));		
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(subclassTestBeanURI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		
		
		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#arrayType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#arrayType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#listType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#listType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#unorderedListType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#unorderedListType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#setType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#setType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#arrayOfBeanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyResourceDefinition);
		assertEquals(activityTestBeanURI.resolve("#arrayOfBeanType"),
				definition.getPredicate());
		assertEquals(activityTestBean2URI, ((PropertyResourceDefinition) definition).getTypeURI());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());
		definition = ((PropertyResourceDefinition) definition).getPropertyDefinition(
				activityTestBean2URI.resolve("#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBean2URI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#listOfBeanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyResourceDefinition);
		assertEquals(activityTestBeanURI.resolve("#listOfBeanType"),
				definition.getPredicate());
		assertEquals(activityTestBean2URI, ((PropertyResourceDefinition) definition).getTypeURI());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());
		definition = ((PropertyResourceDefinition) definition).getPropertyDefinition(
				activityTestBean2URI.resolve("#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBean2URI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#setOfBeanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyResourceDefinition);
		assertEquals(activityTestBeanURI.resolve("#setOfBeanType"),
				definition.getPredicate());
		assertEquals(activityTestBean2URI, ((PropertyResourceDefinition) definition).getTypeURI());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		definition = ((PropertyResourceDefinition) definition).getPropertyDefinition(
				activityTestBean2URI.resolve("#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBean2URI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		
		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#arrayOfEnumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#arrayOfEnumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new LinkedHashSet<String>(Arrays.asList("A", "B")), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#listOfEnumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#listOfEnumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new LinkedHashSet<String>(Arrays.asList("A", "B")), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#setOfEnumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(activityTestBeanURI.resolve("#setOfEnumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new LinkedHashSet<String>(Arrays.asList("A", "B")), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(activityTestBeanURI.resolve(
				"#uriType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyReferenceDefinition);
		assertEquals(activityTestBeanURI.resolve("#uriType"),
				definition.getPredicate());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertFalse(definition.isMultiple());
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
		testBean2.stringType = "string value 2.1";
		testBean2.stringType2 = "string value 2.2";
		ActivityTestBean2 testBean3 = new ActivityTestBean2();
		testBean3.stringType = "string value 3.1";
		testBean3.stringType2 = "string value 3.2";
		ActivityTestBean2 testBean4 = new ActivityTestBean2();
		testBean4.stringType = "string value 4.1";
		testBean4.stringType2 = "string value 4.2";
		SubclassActivityTestBean testBean5 = new SubclassActivityTestBean();
		testBean5.stringType2 = "string value 5.1";
		testBean5.conflicting = "string value 5.2";
		testBean5.stringType = null;
		testBean5.subclassStringType = "string value 5.3";
		
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
		assertEquals(testBean5, testBean.subclass);
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
		assertEquals(URI.create("http://www.example.com/"), testBean.uriType);
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
		assertFalse(activityServiceImpl.activityExists(activityTestBeanURI));
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
