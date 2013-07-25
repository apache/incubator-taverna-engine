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
package uk.org.taverna.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class AbstractConfigurableTest {

	private ConfigurationManager manager;

	private DummyConfigurable dummyConfigurable;

	@Before
	public void setup() throws Exception {
		dummyConfigurable = new DummyConfigurable(manager);
		File f = new File(System.getProperty("java.io.tmpdir"));
		File configTestsDir = new File(f,"configTests");
		if (!configTestsDir.exists()) configTestsDir.mkdir();
		final File d = new File(configTestsDir,UUID.randomUUID().toString());
		d.mkdir();
		manager = new ConfigurationManager() {
			private Map<String, Map<String, String>> store = new HashMap<String,  Map<String, String>>();
			@Override
			public void store(Configurable configurable) throws Exception {
				if (configurable != null) {
					store.put(configurable.getUUID(), new HashMap<String, String>(configurable.getInternalPropertyMap()));
				}
			}

			@Override
			public void populate(Configurable configurable) throws Exception {
				Map<String, String> map = store.get(configurable.getUUID());
				if (map != null) {
					configurable.clear();
					for (Entry<String, String> entry : map.entrySet()) {
						configurable.setProperty(entry.getKey(), entry.getValue());
					}
				}
			}

		};
		dummyConfigurable.restoreDefaults();
	}

	@Test
	public void testName() {
		assertEquals("Wrong name","dummyName",dummyConfigurable.getDisplayName());
	}

	@Test
	public void testCategory() {
		assertEquals("Wrong category","test",dummyConfigurable.getCategory());
	}

	@Test
	public void testUUID() {
		assertEquals("Wrong uuid","cheese",dummyConfigurable.getUUID());
	}

	@Test
	public void testGetProperty() {
		assertEquals("Should be john","john",dummyConfigurable.getProperty("name"));
	}

	@Test
	public void testSetProperty() {
		assertEquals("Should be blue","blue",dummyConfigurable.getProperty("colour"));
		assertNull("Should be null",dummyConfigurable.getProperty("new"));

		dummyConfigurable.setProperty("colour", "red");
		dummyConfigurable.setProperty("new", "new value");

		assertEquals("Should be red","red",dummyConfigurable.getProperty("colour"));
		assertEquals("Should be new value","new value",dummyConfigurable.getProperty("new"));
	}

	@Test
	public void testDeleteValue() {
		assertEquals("Should be blue","blue",dummyConfigurable.getProperty("colour"));
		assertNull("Should be null",dummyConfigurable.getProperty("new"));

		dummyConfigurable.setProperty("new", "new value");

		assertEquals("Should be new value","new value",dummyConfigurable.getProperty("new"));

		dummyConfigurable.deleteProperty("new");
		dummyConfigurable.deleteProperty("colour");

		assertNull("Should be null",dummyConfigurable.getProperty("new"));
		assertNull("Should be null",dummyConfigurable.getProperty("colour"));
	}

	@Test
	public void testDeleteValueBySettingNull() {
		assertEquals("Should be blue","blue",dummyConfigurable.getProperty("colour"));
		assertNull("Should be null",dummyConfigurable.getProperty("new"));

		dummyConfigurable.setProperty("new", "new value");

		assertEquals("Should be new value","new value",dummyConfigurable.getProperty("new"));

		dummyConfigurable.setProperty("new",null);
		dummyConfigurable.setProperty("colour",null);

		assertNull("Should be null",dummyConfigurable.getProperty("new"));
		assertNull("Should be null",dummyConfigurable.getProperty("colour"));
	}

	@Test
	public void testRestoreDefaults() {
		assertEquals("There should be 2 values",2,dummyConfigurable.getInternalPropertyMap().size());

		dummyConfigurable.setProperty("colour", "red");
		dummyConfigurable.setProperty("new", "new value");

		assertEquals("There should be 3 values",3,dummyConfigurable.getInternalPropertyMap().size());

		dummyConfigurable.restoreDefaults();

		assertEquals("There should be 2 values",2,dummyConfigurable.getInternalPropertyMap().size());

		assertEquals("Should be john","john",dummyConfigurable.getProperty("name"));
		assertEquals("Should be john","blue",dummyConfigurable.getProperty("colour"));
	}

	@Test
	public void testList() throws Exception {
		AbstractConfigurable c = dummyConfigurable;
		c.getInternalPropertyMap().clear();
		c.setPropertyStringList("list", new ArrayList<String>());

		manager.store(c);
		assertTrue("Should be an instanceof a list",c.getPropertyStringList("list") instanceof List);
		assertEquals("there should be 0 items",0,c.getPropertyStringList("list").size());
		manager.populate(c);

		assertTrue("Should be an instanceof a list",c.getPropertyStringList("list") instanceof List);
		assertEquals("there should be 0 items",0,c.getPropertyStringList("list").size());

		List<String> list = new ArrayList<String>(c.getPropertyStringList("list"));
		list.add("fred");
		c.setPropertyStringList("list", list);
		assertEquals("there should be 1 item",1,c.getPropertyStringList("list").size());

		manager.store(c);
		assertEquals("there should be 1 item",1,c.getPropertyStringList("list").size());
		manager.populate(c);

		assertEquals("there should be 1 item",1,c.getPropertyStringList("list").size());
		assertEquals("item should be fred","fred",c.getPropertyStringList("list").get(0));

		c.getInternalPropertyMap().clear();
		c.setProperty("list", "a,b,c");
		assertEquals("There should be 3 items in the list",3,c.getPropertyStringList("list").size());
		assertEquals("Item 1 should be a","a",c.getPropertyStringList("list").get(0));
		assertEquals("Item 1 should be b","b",c.getPropertyStringList("list").get(1));
		assertEquals("Item 1 should be c","c",c.getPropertyStringList("list").get(2));

	}

	@Test
	public void testListNotThere() throws Exception {
		AbstractConfigurable c = dummyConfigurable;
		c.getInternalPropertyMap().clear();
		assertNull("the property should be null",c.getProperty("sdflhsdfhsdfjkhsdfkhsdfkhsdfjkh"));
		assertNull("the list should be null if the property doesn't exist",c.getPropertyStringList("sdflhsdfhsdfjkhsdfkhsdfkhsdfjkh"));
	}

	@Test
	public void testListDelimeters() throws Exception {
		AbstractConfigurable c = dummyConfigurable;
		c.getInternalPropertyMap().clear();
		c.setPropertyStringList("list", new ArrayList<String>());

		assertTrue("Should be an instanceof a list",c.getPropertyStringList("list") instanceof List);
		assertEquals("there should be 0 items",0,((List<String>)c.getPropertyStringList("list")).size());

		List<String> list = new ArrayList<String>(c.getPropertyStringList("list"));
		list.add("a,b,c");
		c.setPropertyStringList("list",list);
		assertEquals("there should be 1 items",1,((List<String>)c.getPropertyStringList("list")).size());

		list = new ArrayList<String>(c.getPropertyStringList("list"));
		list.add("d");
		c.setPropertyStringList("list",list);
		assertEquals("there should be 2 items",2,((List<String>)c.getPropertyStringList("list")).size());
		assertEquals("The first item should be a,b,c","a,b,c",c.getPropertyStringList("list").get(0));
		assertEquals("The second item should be d","d",c.getPropertyStringList("list").get(1));

		manager.store(c);
		assertEquals("there should be 2 items",2,((List<String>)c.getPropertyStringList("list")).size());
		assertEquals("The first item should be a,b,c","a,b,c",c.getPropertyStringList("list").get(0));
		assertEquals("The second item should be d","d",c.getPropertyStringList("list").get(1));

		manager.populate(c);
		assertEquals("there should be 2 items",2,((List<String>)c.getPropertyStringList("list")).size());

		assertEquals("The first item should be a,b,c","a,b,c",c.getPropertyStringList("list").get(0));
		assertEquals("The second item should be d","d",c.getPropertyStringList("list").get(1));

	}

	@Test(expected=UnsupportedOperationException.class)
	public void testUnmodifiable() throws Exception {

		AbstractConfigurable c = dummyConfigurable;
		c.getInternalPropertyMap().clear();
		c.setPropertyStringList("list", new ArrayList<String>());
		c.getPropertyStringList("list").add("fred");

	}

}
