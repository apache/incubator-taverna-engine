package uk.org.taverna.commons.plugin.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.commons.plugin.PluginSite.PluginSiteType;

public class PluginSiteImplTest {

	PluginSiteImpl pluginSiteImpl;
	String name, url;
	PluginSiteType type;

	@Before
	public void setUp() throws Exception {
		name = "test name";
		url = "test url";
		type = PluginSiteType.SYSTEM;
		pluginSiteImpl = new PluginSiteImpl(name, url, type);
	}

	@Test
	public void testPluginSiteImpl() {
		pluginSiteImpl = new PluginSiteImpl();
	}

	@Test
	public void testPluginSiteImplStringStringPluginSiteType() {
		pluginSiteImpl = new PluginSiteImpl(null, null, null);
		pluginSiteImpl = new PluginSiteImpl("", "", PluginSiteType.USER);
	}

	@Test
	public void testGetName() {
		assertEquals(name, pluginSiteImpl.getName());
		assertEquals(name, pluginSiteImpl.getName());
	}

	@Test
	public void testSetName() {
		pluginSiteImpl.setName("name");
		assertEquals("name", pluginSiteImpl.getName());
	}

	@Test
	public void testGetUrl() {
		assertEquals(url, pluginSiteImpl.getUrl());
		assertEquals(url, pluginSiteImpl.getUrl());
	}

	@Test
	public void testSetUrl() {
		pluginSiteImpl.setName("http://www.example.com/");
		assertEquals("http://www.example.com/", pluginSiteImpl.getName());
	}

	@Test
	public void testGetType() {
		assertEquals(type, pluginSiteImpl.getType());
		assertEquals(type, pluginSiteImpl.getType());
	}

	@Test
	public void testSetType() {
		pluginSiteImpl.setType(PluginSiteType.USER);
		assertEquals(PluginSiteType.USER, pluginSiteImpl.getType());

	}

}
