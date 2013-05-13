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

import java.net.URL;
import java.util.List;

import uk.org.taverna.commons.plugin.xml.jaxb.PluginVersions;

/**
 * Manages plugin sites.
 *
 * @author David Withers
 */
public interface PluginSiteManager {

	/**
	 * Returns all the managed plugin sites.
	 * <p>
	 * If there are no plugin sites an empty list is returned.
	 *
	 * @return all the managed plugin sites
	 * @throws PluginException
	 */
	public List<PluginSite> getPluginSites();

	/**
	 * Contacts the plugin site at the specified URL and return a new plugin site.
	 *
	 * @param pluginSiteURL the plugin site URL
	 * @throws PluginException if there is a problem contacting the plugin site
	 */
	public PluginSite createPluginSite(URL pluginSiteURL) throws PluginException;

	/**
	 * Adds a plugin site.
	 * <p>
	 * If the plugin site already exists this method does nothing.
	 *
	 * @param pluginSite the plugin site to add
	 * @throws PluginException
	 */
	public void addPluginSite(PluginSite pluginSite) throws PluginException;

	/**
	 * Removes a plugin site.
	 * <p>
	 * If the plugin site does not exist this method does nothing.
	 *
	 * @param pluginSite the plugin site to remove
	 * @throws PluginException
	 */
	public void removePluginSite(PluginSite pluginSite) throws PluginException;

	/**
	 * Returns all the plugins available at the specified plugin site.
	 * <p>
	 * If no plugins are available an empty list is returned.
	 *
	 * @param pluginSite
	 *            the plugin site to contact
	 * @return all the plugins available at the specified plugin site
	 * @throws PluginException
	 *             if there is a plroblem contacting the plugin site
	 */
	public List<PluginVersions> getPlugins(PluginSite pluginSite) throws PluginException;

}
