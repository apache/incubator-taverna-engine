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
package uk.org.taverna.platform.dispatch.impl;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.AbstractDispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayerFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.org.taverna.platform.configuration.SubclassTestBean;
import uk.org.taverna.platform.configuration.TestBean;
import uk.org.taverna.platform.configuration.TestBean2;
import uk.org.taverna.platform.configuration.TestEnum;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;

/**
 * Unit tests for {@link uk.org.taverna.platform.dispatch.impl.DispatchLayerServiceImpl DispatchLayerServiceImpl}.
 *
 * @author David Withers
 */
public class DispatchLayerServiceImplTest {

	private DispatchLayerServiceImpl dispatchLayerServiceImpl;

	private Configuration configuration;

	private DispatchStackLayer dispatchStackLayer;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dispatchLayerServiceImpl = new DispatchLayerServiceImpl();
		List<DispatchLayerFactory> dispatchLayerFactories = new ArrayList<DispatchLayerFactory>();
		dispatchLayerFactories.add(new DispatchLayerFactory() {
			public DispatchLayer<Object> createDispatchLayer(URI uri) {
				return new AbstractDispatchLayer<Object>() {
					Object configuration;
					public void configure(Object configuration) throws ActivityConfigurationException {
						this.configuration = configuration;
					}
					public Object getConfiguration() {
						return configuration;
					}
				};
			}

			public Object createDispatchLayerConfiguration(URI uri) {
				return new Object();
			}

			public Set<URI> getDispatchLayerURIs() {
				return Collections.singleton(URI.create(nonAnnotatedBeanURI));
			}

		});
		dispatchLayerFactories.add(new DispatchLayerFactory() {
			public DispatchLayer<Object> createDispatchLayer(URI uri) {
				return new AbstractDispatchLayer<Object>() {
					Object configuration;
					public void configure(Object configuration) throws ActivityConfigurationException {
						this.configuration = configuration;
					}
					public Object getConfiguration() {
						return configuration;
					}
				};
			}

			public Object createDispatchLayerConfiguration(URI uri) {
				return new TestBean();
			}

			public Set<URI> getDispatchLayerURIs() {
				return Collections.singleton(URI.create(annotatedBeanURI));
			}

		});
		dispatchLayerServiceImpl.setDispatchLayerFactories(dispatchLayerFactories);

		dispatchStackLayer = new DispatchStackLayer();
		dispatchStackLayer.setConfigurableType(URI.create(annotatedBeanURI));

		configuration = createTestConfiguration();
		configuration.setConfigures(dispatchStackLayer);
}

	/**
	 * Test method for {@link uk.org.taverna.platform.dispatch.impl.DispatchLayerServiceImpl#getDispatchLayerURIs()}.
	 */
	@Test
	public void testGetDispatchLayerURIs() {
		assertEquals(
				Arrays.asList(new URI[] { URI.create(nonAnnotatedBeanURI),
						URI.create(annotatedBeanURI) }), dispatchLayerServiceImpl.getDispatchLayerURIs());
		assertEquals(
				Arrays.asList(new URI[] { URI.create(nonAnnotatedBeanURI),
						URI.create(annotatedBeanURI) }), dispatchLayerServiceImpl.getDispatchLayerURIs());
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.dispatch.impl.DispatchLayerServiceImpl#dispatchLayerExists(java.net.URI)}.
	 */
	@Test
	public void testDispatchLayerExists() {
		assertTrue(dispatchLayerServiceImpl.dispatchLayerExists(URI.create(annotatedBeanURI)));
		assertTrue(dispatchLayerServiceImpl.dispatchLayerExists(URI.create(annotatedBeanURI)));
		assertTrue(dispatchLayerServiceImpl.dispatchLayerExists(URI.create(nonAnnotatedBeanURI)));
		assertFalse(dispatchLayerServiceImpl.dispatchLayerExists(URI
				.create("test://ns.taverna.org.uk/activities/nonExistantBean")));
		assertTrue(dispatchLayerServiceImpl.dispatchLayerExists(URI.create(nonAnnotatedBeanURI)));
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.dispatch.impl.DispatchLayerServiceImpl#getDispatchLayerConfigurationDefinition(java.net.URI)}.
	 */
	@Test
	public void testGetDispatchLayerConfigurationDefinition() throws Exception {
		ConfigurationDefinition configurationDefinition = dispatchLayerServiceImpl.getDispatchLayerConfigurationDefinition(URI
				.create(annotatedBeanURI));
		assertEquals(URI.create(annotatedBeanURI), configurationDefinition.getConfigurableType());
		assertEquals(testBeanURI, configurationDefinition.getPropertyResourceDefinition().getTypeURI());
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.dispatch.impl.DispatchLayerServiceImpl#createDispatchLayer(java.net.URI, uk.org.taverna.scufl2.api.configurations.Configuration)}.
	 */
	@Test
	public void testCreateDispatchLayer() throws Exception {
		DispatchLayer<?> dispatchLayer = dispatchLayerServiceImpl.createDispatchLayer(URI.create(annotatedBeanURI), configuration);
		assertNotNull(dispatchLayer);
		Object configuration2 = dispatchLayer.getConfiguration();
		assertTrue(configuration2 instanceof TestBean);
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.dispatch.impl.DispatchLayerServiceImpl#createDefaultDispatchLayers()}.
	 */
	@Test
	@Ignore
	public void testCreateDefaultDispatchLayers() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link uk.org.taverna.platform.dispatch.impl.DispatchLayerServiceImpl#setDispatchLayerFactories(java.util.List)}.
	 */
	@Test
	public void testSetDispatchLayerFactories() {
		assertTrue(dispatchLayerServiceImpl.dispatchLayerExists(URI.create(annotatedBeanURI)));
		assertTrue(dispatchLayerServiceImpl.dispatchLayerExists(URI.create(nonAnnotatedBeanURI)));
		assertFalse(dispatchLayerServiceImpl.dispatchLayerExists(URI.create("test://newBean")));
		dispatchLayerServiceImpl.setDispatchLayerFactories(Arrays
				.asList(new DispatchLayerFactory[] { new DispatchLayerFactory() {
					public DispatchLayer<?> createDispatchLayer(URI uri) {
						return null;
					}

					public Object createDispatchLayerConfiguration(URI uri) {
						return null;
					}

					public Set<URI> getDispatchLayerURIs() {
						return Collections.singleton(URI.create("test://newBean"));
					}
				} }));
		assertFalse(dispatchLayerServiceImpl.dispatchLayerExists(testBeanURI));
		assertFalse(dispatchLayerServiceImpl.dispatchLayerExists(URI.create(nonAnnotatedBeanURI)));
		assertTrue(dispatchLayerServiceImpl.dispatchLayerExists(URI.create("test://newBean")));
	}

}
