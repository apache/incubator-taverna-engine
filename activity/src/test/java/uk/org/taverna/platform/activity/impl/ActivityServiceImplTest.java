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
import static uk.org.taverna.platform.configuration.TestUtils.annotatedBeanURI;
import static uk.org.taverna.platform.configuration.TestUtils.createTestConfiguration;
import static uk.org.taverna.platform.configuration.TestUtils.nonAnnotatedBeanURI;
import static uk.org.taverna.platform.configuration.TestUtils.testBeanURI;

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

import uk.org.taverna.platform.configuration.SubclassTestBean;
import uk.org.taverna.platform.configuration.TestBean;
import uk.org.taverna.platform.configuration.TestBean2;
import uk.org.taverna.platform.configuration.TestEnum;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;

/**
 * Unit tests for {@link uk.org.taverna.platform.activity.impl.ActivityServiceImpl ActivityServiceImpl}.
 *
 * @author David Withers
 */
public class ActivityServiceImplTest {

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
				return new TestBean();
			}

			public URI getActivityURI() {
				return URI.create(annotatedBeanURI);
			}

		});
		activityServiceImpl.setActivityFactories(activityFactories);

		activity = new uk.org.taverna.scufl2.api.activity.Activity();
		activity.setConfigurableType(URI.create(annotatedBeanURI));

		configuration = createTestConfiguration();
		configuration.setConfigures(activity);
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
		assertEquals(URI.create(annotatedBeanURI), configurationDefinition.getConfigurableType());
		assertEquals(testBeanURI, configurationDefinition.getPropertyResourceDefinition().getTypeURI());
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
		assertTrue(configuration2 instanceof TestBean);
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
		assertFalse(activityServiceImpl.activityExists(testBeanURI));
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
