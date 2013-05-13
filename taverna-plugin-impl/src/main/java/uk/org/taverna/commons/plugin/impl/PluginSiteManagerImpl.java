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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import uk.org.taverna.commons.download.DownloadException;
import uk.org.taverna.commons.download.DownloadManager;
import uk.org.taverna.commons.plugin.PluginException;
import uk.org.taverna.commons.plugin.PluginSite;
import uk.org.taverna.commons.plugin.PluginSite.PluginSiteType;
import uk.org.taverna.commons.plugin.PluginSiteManager;
import uk.org.taverna.commons.plugin.xml.jaxb.PluginVersions;
import uk.org.taverna.commons.plugin.xml.jaxb.Plugins;
import uk.org.taverna.commons.profile.xml.jaxb.Updates;
import uk.org.taverna.configuration.app.ApplicationConfiguration;

/**
 * PluginSiteManager implementation.
 *
 * @author David Withers
 */
public class PluginSiteManagerImpl implements PluginSiteManager {

	private static final String PLUGIN_SITES_FILE = "plugin-sites.xml";
	private static final String DIGEST_ALGORITHM = "MD5";
	private static final String PLUGINS_FILE = "plugins.xml";

	private static final Logger logger = Logger.getLogger(PluginSiteManagerImpl.class);

	private ApplicationConfiguration applicationConfiguration;
	private DownloadManager downloadManager;

	private Unmarshaller unmarshaller;
	private Marshaller marshaller;

	private List<PluginSite> pluginSites;

	public PluginSiteManagerImpl() throws PluginException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Plugins.class, PluginSites.class);
			unmarshaller = jaxbContext.createUnmarshaller();
			marshaller = jaxbContext.createMarshaller();
		} catch (JAXBException e) {
			throw new PluginException("Error creating JAXBContext", e);
		}
	}

	@Override
	public List<PluginSite> getPluginSites() {
		if (pluginSites == null) {
			readPluginSitesFile();
			if (pluginSites == null) {
				pluginSites = new ArrayList<PluginSite>();
				pluginSites.addAll(getSystemPluginSites());
			}
		}
		return pluginSites;
	}

	@Override
	public PluginSite createPluginSite(URL pluginSiteURL) throws PluginException {
		try {
			File tempFile = File.createTempFile("plugins", null);
			tempFile.deleteOnExit();
			URL pluginFileURL = new URL(pluginSiteURL + "/" + PLUGINS_FILE);
			downloadManager.download(pluginFileURL, tempFile, DIGEST_ALGORITHM);
			return new PluginSiteImpl("", pluginSiteURL.toExternalForm());
		} catch (MalformedURLException e) {
			throw new PluginException(String.format("Invalid plugin site URL %1$s", pluginSiteURL), e);
		} catch (DownloadException e) {
			throw new PluginException(String.format("Error contacting plugin site at %1$s", pluginSiteURL), e);
		} catch (IOException e) {
			throw new PluginException(String.format("Error contacting plugin site at %1$s", pluginSiteURL), e);
		}
	}

	@Override
	public void addPluginSite(PluginSite pluginSite) throws PluginException {
		getPluginSites().add(pluginSite);
		writePluginSitesFile();
	}

	@Override
	public void removePluginSite(PluginSite pluginSite) throws PluginException {
		getPluginSites().remove(pluginSite);
		writePluginSitesFile();
	}

	@Override
	public List<PluginVersions> getPlugins(PluginSite pluginSite) throws PluginException {
		List<PluginVersions> plugins = new ArrayList<PluginVersions>();
		try {
			URL pluginSiteURL = new URL(pluginSite.getUrl() + "/" + PLUGINS_FILE);
			File pluginsFile = new File(getDataDirectory(), PLUGINS_FILE);
			downloadManager.download(pluginSiteURL, pluginsFile, DIGEST_ALGORITHM);
			Plugins pluginsXML = (Plugins) unmarshaller.unmarshal(pluginsFile);
			for (PluginVersions plugin : pluginsXML.getPlugin()) {
				plugin.setPluginSiteUrl(pluginSite.getUrl());
				plugins.add(plugin);
			}
		} catch (MalformedURLException e) {
			throw new PluginException(String.format("Plugin site %1$s has an invalid location",
					pluginSite.getName()), e);
		} catch (DownloadException e) {
			throw new PluginException(String.format("Error downloading from plugin site %1$s",
					pluginSite.getName()), e);
		} catch (JAXBException e) {
			throw new PluginException(String.format("Error getting plugins from plugin site %1$s",
					pluginSite.getName()), e);
		}
		return plugins;
	}

	private List<PluginSite> getSystemPluginSites() {
		List<PluginSite> systemPluginSites = new ArrayList<PluginSite>();
		Updates updates = applicationConfiguration.getApplicationProfile().getUpdates();
		systemPluginSites
				.add(new PluginSiteImpl("", updates.getPluginSite(), PluginSiteType.SYSTEM));
		return systemPluginSites;
	}

	private void writePluginSitesFile() {
		File pluginSitesFile = new File(getDataDirectory(), PLUGIN_SITES_FILE);
		try {
			marshaller.marshal(pluginSites, pluginSitesFile);
		} catch (JAXBException e) {
			logger.error("Error writing file " + pluginSitesFile, e);
		}
	}

	private void readPluginSitesFile() {
		File pluginSitesFile = new File(getDataDirectory(), PLUGIN_SITES_FILE);
		if (pluginSitesFile.exists()) {
			try {
				pluginSites = new ArrayList<PluginSite>();
				PluginSites pluginSitesStore = (PluginSites) unmarshaller
						.unmarshal(pluginSitesFile);
				for (PluginSiteImpl pluginSiteImpl : pluginSitesStore.getPluginSites()) {
					pluginSites.add(pluginSiteImpl);
				}
			} catch (JAXBException e) {
				logger.error("Error reading file " + pluginSitesFile, e);
			}
		}
	}

	private File getDataDirectory() {
		return new File(applicationConfiguration.getApplicationHomeDir(), "plugin-data");
	}

	public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

	public void setDownloadManager(DownloadManager downloadManager) {
		this.downloadManager = downloadManager;
	}

}
