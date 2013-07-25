/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester
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
package uk.org.taverna.configuration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.commons.profile.xml.jaxb.ApplicationProfile;
import uk.org.taverna.configuration.AbstractConfigurable;
import uk.org.taverna.configuration.Configurable;
import uk.org.taverna.configuration.DummyConfigurable;
import uk.org.taverna.configuration.app.ApplicationConfiguration;

public class ConfigurationManagerImplTest {

	private File configFile;

	private ConfigurationManagerImpl manager;

	private DummyConfigurable dummyConfigurable;

	@Before
	public void setup() throws Exception {
		dummyConfigurable = new DummyConfigurable(manager);
		File f = new File(System.getProperty("java.io.tmpdir"));
		File configTestsDir = new File(f, "configTests");
		if (!configTestsDir.exists())
			configTestsDir.mkdir();
		final File d = new File(configTestsDir, UUID.randomUUID().toString());
		d.mkdir();
		manager = new ConfigurationManagerImpl(new ApplicationConfiguration() {
			public File getApplicationHomeDir() {
				return d;
			}

			public String getName() {
				return null;
			}

			public String getTitle() {
				return null;
			}

			public File getStartupDir() {
				return null;
			}

			public File getUserPluginDir() {
				return null;
			}

			public File getSystemPluginDir() {
				return null;
			}

			public File getLogFile() {
				return null;
			}

			public File getLogDir() {
				return null;
			}

			public Properties getProperties() {
				return null;
			}

			@Override
			public ApplicationProfile getApplicationProfile() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		configFile = new File(d, "conf/"+manager.generateFilename(dummyConfigurable));
		dummyConfigurable.restoreDefaults();
	}

	@Test
	public void testStore() throws Exception {
		Configurable conf = dummyConfigurable;
		manager.store(conf);
		assertTrue(configFile.exists());
	}

	@Test
	public void testDefaultValues() throws Exception {
		Configurable conf = dummyConfigurable;
		assertEquals("name should equal john", "john", conf.getProperty("name"));
		manager.store(conf);
		Properties props = new Properties();
		props.load(new FileInputStream(configFile));
		assertFalse("stored properties should not contain the default value",
				props.containsKey("name"));
		manager.populate(conf);
		assertEquals("default property name should still exist after re-populating", "john",
				conf.getProperty("name"));
	}

	@Test
	public void testRemoveNotDefaultValue() throws Exception {
		Configurable conf = dummyConfigurable;
		conf.setProperty("hhh", "iii");
		manager.store(conf);
		Properties props = new Properties();
		props.load(new FileInputStream(configFile));
		assertEquals("The stored file should contain the new entry", "iii", props.get("hhh"));
		conf.deleteProperty("hhh");
		manager.store(conf);
		manager.populate(conf);
		assertNull("The removed value should no longer exist", conf.getProperty("hhh"));
		props.clear();
		props.load(new FileInputStream(configFile));
		assertNull("The stored file should no longer contain the deleted entry", props.get("hhh"));
	}

	@Test
	public void testNewValues() throws Exception {
		Configurable conf = dummyConfigurable;
		conf.setProperty("country", "france");
		assertEquals("country should equal france", "france", conf.getProperty("country"));
		manager.store(conf);
		Properties props = new Properties();
		props.load(new FileInputStream(configFile));
		assertTrue("stored properties should contain the default value",
				props.containsKey("country"));
		assertEquals("stored property country should equal france", "france",
				props.getProperty("country"));
		manager.populate(conf);
		assertEquals("default property name should still exist after re-populating", "france",
				conf.getProperty("country"));
	}

	@Test
	public void testDeleteDefaultProperty() throws Exception {
		AbstractConfigurable conf = dummyConfigurable;
		assertEquals("name should equal john", "john", conf.getProperty("name"));
		conf.deleteProperty("name");
		manager.store(conf);
		manager.populate(conf);
		assertNull("value for name should be null", conf.getProperty("name"));

		Properties props = new Properties();
		props.load(new FileInputStream(configFile));
		assertTrue("Key name should be in stored props because its a deleted default value",
				props.containsKey("name"));
		assertEquals("name should have the special value to indicate its been deleted",
				AbstractConfigurable.DELETED_VALUE_CODE, props.getProperty("name"));
	}

	@Test
	public void testFilename() {
		assertTrue(configFile.getAbsolutePath().endsWith("dummyPrefix-cheese.config"));
	}
}
