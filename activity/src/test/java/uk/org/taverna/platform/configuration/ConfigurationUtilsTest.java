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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.org.taverna.platform.configuration.TestUtils.annotatedBeanURI;
import static uk.org.taverna.platform.configuration.TestUtils.createTestConfiguration;
import static uk.org.taverna.platform.configuration.TestUtils.subclassTestBeanURI;
import static uk.org.taverna.platform.configuration.TestUtils.testBean2URI;
import static uk.org.taverna.platform.configuration.TestUtils.testBeanURI;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityFactory;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.platform.activity.impl.ActivityServiceImpl;
import uk.org.taverna.platform.property.ConfigurationUtils;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyLiteralDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyReferenceDefinition;
import uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;

/**
 *
 *
 * @author David Withers
 */
public class ConfigurationUtilsTest {

	private ActivityServiceImpl activityServiceImpl;

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
				return new TestBean();
			}

			public URI getActivityURI() {
				return URI.create(annotatedBeanURI);
			}

		});
		activityServiceImpl.setActivityFactories(activityFactories);
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.property.ConfigurationUtils#createPropertyDefinitions(java.lang.Class)}.
	 */
	@Test
	public void testCreatePropertyDefinitions() {

		int definitionCount = 0;

		PropertyResourceDefinition propertyResourceDefinition = new PropertyResourceDefinition();
		List<PropertyDefinition> propertyDefinitions = ConfigurationUtils.createPropertyDefinitions(TestBean.class);
		propertyResourceDefinition.setPropertyDefinitions(propertyDefinitions);

		PropertyDefinition definition = propertyResourceDefinition.getPropertyDefinition(
				testBeanURI.resolve("#stringType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#stringType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve("#optionalStringType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#optionalStringType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertFalse(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#integerType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#integerType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_INT, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#longType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#longType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_LONG, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#floatType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#floatType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_FLOAT, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#doubleType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#doubleType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_DOUBLE, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#booleanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#booleanType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_BOOLEAN, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#enumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#enumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new LinkedHashSet<String>(Arrays.asList("A", "B")), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		PropertyResourceDefinition beanType = (PropertyResourceDefinition) propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#beanType"));
		definitionCount++;
		assertNotNull(beanType);
		assertTrue(beanType instanceof PropertyResourceDefinition);
		assertEquals(testBeanURI.resolve("#beanType"),
				beanType.getPredicate());
		assertEquals(URI.create(annotatedBeanURI + "/configuration2"), beanType.getTypeURI());
		assertEquals("", beanType.getDescription());
		assertEquals("", beanType.getLabel());
		assertFalse(beanType.isMultiple());
		assertFalse(beanType.isOrdered());
		assertTrue(beanType.isRequired());
		assertEquals(2, beanType.getPropertyDefinitions().size());


		definition = beanType.getPropertyDefinition(testBean2URI.resolve("#stringType"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBean2URI.resolve("#stringType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = beanType.getPropertyDefinition(testBean2URI.resolve("#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBean2URI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());


		// Test ActivTestBean2
		PropertyResourceDefinition beanType2 = (PropertyResourceDefinition) propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#beanType2"));
		definitionCount++;
		assertNotNull(beanType2);
		assertTrue(beanType2 instanceof PropertyResourceDefinition);
		assertEquals(testBeanURI.resolve("#beanType2"),
				beanType2.getPredicate());
		assertEquals(testBean2URI, beanType2.getTypeURI());
		assertEquals("", beanType2.getDescription());
		assertEquals("", beanType2.getLabel());
		assertFalse(beanType2.isMultiple());
		assertFalse(beanType2.isOrdered());
		assertTrue(beanType2.isRequired());
		assertEquals(2, beanType2.getPropertyDefinitions().size());

		definition = beanType2.getPropertyDefinition(testBean2URI.resolve("#stringType"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBean2URI.resolve("#stringType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = beanType2.getPropertyDefinition(testBean2URI.resolve("#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBean2URI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());



		PropertyResourceDefinition subclass = (PropertyResourceDefinition) propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#subclass"));
		definitionCount++;
		assertNotNull(subclass);
		assertTrue(subclass instanceof PropertyResourceDefinition);
		assertEquals(testBeanURI.resolve("#subclass"),
				subclass.getPredicate());
		assertEquals(URI.create(annotatedBeanURI + "/subclass"), ((PropertyResourceDefinition) subclass).getTypeURI());
		assertEquals("", subclass.getDescription());
		assertEquals("", subclass.getLabel());
		assertFalse(subclass.isMultiple());
		assertFalse(subclass.isOrdered());
		assertTrue(subclass.isRequired());
		assertEquals(3, ((PropertyResourceDefinition) subclass).getPropertyDefinitions().size());

		definition = subclass.getPropertyDefinition(testBean2URI.resolve("#stringType"));
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


		definition = ((PropertyResourceDefinition) subclass).getPropertyDefinition(testBean2URI.resolve("#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBean2URI.resolve("#stringType2"),
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



		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#arrayType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#arrayType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#listType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#listType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#unorderedListType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#unorderedListType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#setType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#setType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#arrayOfBeanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyResourceDefinition);
		assertEquals(testBeanURI.resolve("#arrayOfBeanType"),
				definition.getPredicate());
		assertEquals(testBean2URI, ((PropertyResourceDefinition) definition).getTypeURI());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());
		definition = ((PropertyResourceDefinition) definition).getPropertyDefinition(
				testBean2URI.resolve("#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBean2URI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#listOfBeanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyResourceDefinition);
		assertEquals(testBeanURI.resolve("#listOfBeanType"),
				definition.getPredicate());
		assertEquals(testBean2URI, ((PropertyResourceDefinition) definition).getTypeURI());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());
		definition = ((PropertyResourceDefinition) definition).getPropertyDefinition(
				testBean2URI.resolve("#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBean2URI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#setOfBeanType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyResourceDefinition);
		assertEquals(testBeanURI.resolve("#setOfBeanType"),
				definition.getPredicate());
		assertEquals(testBean2URI, ((PropertyResourceDefinition) definition).getTypeURI());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());
		definition = ((PropertyResourceDefinition) definition).getPropertyDefinition(
				testBean2URI.resolve("#stringType2"));
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBean2URI.resolve("#stringType2"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new HashSet<String>(), ((PropertyLiteralDefinition) definition).getOptions());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#arrayOfEnumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#arrayOfEnumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new LinkedHashSet<String>(Arrays.asList("A", "B")), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#listOfEnumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#listOfEnumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new LinkedHashSet<String>(Arrays.asList("A", "B")), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertTrue(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#setOfEnumType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyLiteralDefinition);
		assertEquals(testBeanURI.resolve("#setOfEnumType"),
				definition.getPredicate());
		assertEquals(PropertyLiteral.XSD_STRING, ((PropertyLiteralDefinition) definition).getLiteralType());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertEquals(new LinkedHashSet<String>(Arrays.asList("A", "B")), ((PropertyLiteralDefinition) definition).getOptions());
		assertTrue(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		definition = propertyResourceDefinition.getPropertyDefinition(testBeanURI.resolve(
				"#uriType"));
		definitionCount++;
		assertNotNull(definition);
		assertTrue(definition instanceof PropertyReferenceDefinition);
		assertEquals(testBeanURI.resolve("#uriType"),
				definition.getPredicate());
		assertEquals("", definition.getDescription());
		assertEquals("", definition.getLabel());
		assertFalse(definition.isMultiple());
		assertFalse(definition.isOrdered());
		assertTrue(definition.isRequired());

		assertEquals(definitionCount, propertyResourceDefinition.getPropertyDefinitions().size());
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.property.ConfigurationUtils#setConfigurationProperties(java.lang.Object, uk.org.taverna.scufl2.api.configurations.Configuration, uk.org.taverna.scufl2.api.property.PropertyResource, uk.org.taverna.scufl2.api.configurations.PropertyResourceDefinition, java.net.URI, uk.org.taverna.scufl2.api.container.WorkflowBundle)}.
	 */
	@Test
	public void testSetConfigurationProperties() throws Exception {
		Configuration configuration = createTestConfiguration();
		ConfigurationDefinition definition = activityServiceImpl.getActivityConfigurationDefinition(URI.create(annotatedBeanURI));
		TestBean testBean = new TestBean();
		ConfigurationUtils.setConfigurationProperties(testBean, configuration,
				configuration.getPropertyResource(),
				definition.getPropertyResourceDefinition(), URI.create(annotatedBeanURI), configuration.getParent().getParent());

		TestBean2 testBean2 = new TestBean2();
		testBean2.stringType = "string value 2.1";
		testBean2.stringType2 = "string value 2.2";
		TestBean2 testBean3 = new TestBean2();
		testBean3.stringType = "string value 3.1";
		testBean3.stringType2 = "string value 3.2";
		TestBean2 testBean4 = new TestBean2();
		testBean4.stringType = "string value 4.1";
		testBean4.stringType2 = "string value 4.2";
		SubclassTestBean testBean5 = new SubclassTestBean();
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
		assertEquals(TestEnum.A, testBean.enumType);
		assertEquals(testBean2, testBean.beanType);
		assertEquals(testBean4, testBean.beanType2);
		assertEquals(testBean5, testBean.subclass);
		assertArrayEquals(new String[] {"array element 1", "array element 2"}, testBean.arrayType);
		assertEquals(Arrays.asList("1","2","3","4"), testBean.listType);
		assertEquals(new HashSet<String>(Arrays.asList("x","y","z")), testBean.setType);
		assertEquals(Arrays.asList("1","2","3","4"), testBean.listType);
		assertEquals(new HashSet<String>(Arrays.asList("a","b","c")), new HashSet<String>(testBean.unorderedListType));
		assertArrayEquals(new TestBean2[] {testBean2, testBean3,testBean4}, testBean.arrayOfBeanType);
		assertEquals(Arrays.asList(testBean3,testBean4), testBean.listOfBeanType);
		assertEquals(new HashSet<TestBean2>(Arrays.asList(testBean2,testBean3)), testBean.setOfBeanType);
		assertArrayEquals(new TestEnum[] {TestEnum.A, TestEnum.B}, testBean.arrayOfEnumType);
		assertEquals(Arrays.asList(TestEnum.B, TestEnum.A), testBean.listOfEnumType);
		assertEquals(new HashSet<TestEnum>(Arrays.asList(TestEnum.B, TestEnum.A)), testBean.setOfEnumType);
		assertEquals(URI.create("http://www.example.com/"), testBean.uriType);
		}

}
