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
package uk.org.taverna.commons.plugin;

import java.io.File;
import java.util.List;

import uk.org.taverna.commons.plugin.xml.jaxb.PluginVersions;

/**
 * Manages installing plugins and checking for plugin updates.
 *
 * @author David Withers
 */
public interface PluginManager {

	public static final String EVENT_TOPIC_ROOT = "uk/org/taverna/commons/plugin/PluginManager/";
	public static final String PLUGIN_INSTALLED = EVENT_TOPIC_ROOT + "PLUGIN_INSTALLED";
	public static final String PLUGIN_UNINSTALLED = EVENT_TOPIC_ROOT + "PLUGIN_UNINSTALLED";
	public static final String UPDATES_AVAILABLE = EVENT_TOPIC_ROOT + "UPDATES_AVAILABLE";

	/**
	 * Loads plugins from the system and user plugin directories.
	 * <p>
	 * If the plugins are not already installed they will be installed and started.
	 *
	 * @throws PluginException
	 */
	public void loadPlugins() throws PluginException;

	/**
	 * Check if there are new versions of installed plugins available.
	 * <p>
	 * If updates are available and event with topic {@link UPDATES_AVAILABLE} will be posted.
	 *
	 * @throws PluginException
	 */
	public void checkForUpdates() throws PluginException;

	/**
	 * Returns updated versions of installed plugins.
	 * <p>
	 * Only plugins that the user has permission to update are returned.
	 *
	 * @return
	 */
	public List<PluginVersions> getPluginUpdates() throws PluginException;

	/**
	 * Returns new plugins available from all plugin sites.
	 *
	 * @return new plugins available from all plugin sites.
	 * @throws PluginException
	 */
	public List<PluginVersions> getAvailablePlugins() throws PluginException;

	/**
	 * Returns all the installed plugins.
	 *
	 * @return
	 * @throws PluginException
	 */
	public List<Plugin> getInstalledPlugins() throws PluginException;

	/**
	 * Installs a plugin from a plugin file.
	 *
	 * @param pluginFile
	 *            the file to install the plugin from
	 * @return the installed plugin
	 * @throws PluginException
	 */
	public Plugin installPlugin(File pluginFile) throws PluginException;

	/**
	 * Installs a plugin from an update site.
	 *
	 * @param pluginSiteURL
	 * @param pluginFile
	 * @return
	 * @throws PluginException
	 */
	public Plugin installPlugin(String pluginSiteURL, String pluginFile) throws PluginException;

	public Plugin updatePlugin(PluginVersions pluginVersions) throws PluginException;

}
