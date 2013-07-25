/*******************************************************************************
 * Copyright (C) 2013 The University of Manchester
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
package uk.org.taverna.commons.plugin.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.org.taverna.commons.download.DownloadException;
import uk.org.taverna.commons.download.DownloadManager;
import uk.org.taverna.commons.plugin.PluginException;
import uk.org.taverna.configuration.app.ApplicationConfiguration;

/**
 *
 *
 * @author David Withers
 */
@Ignore
public class PluginSiteManagerImplTest {

	private PluginSiteManagerImpl pluginSiteManager;
	private ApplicationConfiguration applicationConfiguration;
	private DownloadManager downloadManager;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		pluginSiteManager = new PluginSiteManagerImpl();
		applicationConfiguration = mock(ApplicationConfiguration.class);
	}

	/**
	 * Test method for {@link uk.org.taverna.commons.plugin.impl.PluginSiteManagerImpl#PluginSiteManagerImpl()}.
	 * @throws Exception 
	 */
	@Test
	public void testPluginSiteManagerImpl() throws Exception {
		new PluginSiteManagerImpl();
	}

	/**
	 * Test method for {@link uk.org.taverna.commons.plugin.impl.PluginSiteManagerImpl#getPluginSites()}.
	 */
	@Test
	public void testGetPluginSites() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link uk.org.taverna.commons.plugin.impl.PluginSiteManagerImpl#createPluginSite(java.net.URL)}.
	 * @throws DownloadException
	 */
	@Test
	public void testCreatePluginSite() throws Exception {
		downloadManager = mock(DownloadManager.class);
		doNothing().when(downloadManager).download(new URL("file:///"), null, "");

		pluginSiteManager.setDownloadManager(downloadManager);

		pluginSiteManager.createPluginSite(new URL("file:///"));

	}

	@Test(expected=PluginException.class)
	public void testCreatePluginSiteDownloadException() throws Exception {
		downloadManager = mock(DownloadManager.class);
		doThrow(DownloadException.class).when(downloadManager).download(new URL("file:///"), null, "");

		pluginSiteManager.setDownloadManager(downloadManager);

		pluginSiteManager.createPluginSite(new URL("file:///"));
	}

	/**
	 * Test method for {@link uk.org.taverna.commons.plugin.impl.PluginSiteManagerImpl#addPluginSite(uk.org.taverna.commons.plugin.PluginSite)}.
	 */
	@Test
	public void testAddPluginSite() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link uk.org.taverna.commons.plugin.impl.PluginSiteManagerImpl#removePluginSite(uk.org.taverna.commons.plugin.PluginSite)}.
	 */
	@Test
	public void testRemovePluginSite() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link uk.org.taverna.commons.plugin.impl.PluginSiteManagerImpl#getPlugins(uk.org.taverna.commons.plugin.PluginSite)}.
	 */
	@Test
	public void testGetPlugins() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link uk.org.taverna.commons.plugin.impl.PluginSiteManagerImpl#setApplicationConfiguration(uk.org.taverna.configuration.app.ApplicationConfiguration)}.
	 */
	@Test
	public void testSetApplicationConfiguration() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link uk.org.taverna.commons.plugin.impl.PluginSiteManagerImpl#setDownloadManager(uk.org.taverna.commons.download.DownloadManager)}.
	 */
	@Test
	public void testSetDownloadManager() {
		fail("Not yet implemented");
	}

}
