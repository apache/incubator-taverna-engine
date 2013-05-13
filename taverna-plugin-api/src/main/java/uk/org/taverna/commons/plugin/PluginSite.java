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

/**
 * A plugin site specifies the location of a site that contains plugins.
 * <p>
 * There are two types of plugin site:
 * <dl>
 * <dt>SYSTEM</dt>
 * <dd>plugin sites specified by the application profile</dd>
 * <dt>USER</dt>
 * <dd>plugin sites that can be added and removed by the user</dd>
 * </dl>
 *
 * @author David Withers
 */
public interface PluginSite {

	public static enum PluginSiteType {
		SYSTEM, USER
	};

	/**
	 * Returns the name of the plugin site.
	 *
	 * @return the name of the plugin site
	 */
	public String getName();

	/**
	 * Returns the URL of the plugin site.
	 *
	 * @return the URL of the plugin site
	 */
	public String getUrl();

	/**
	 * Returns the type of the plugin site.
	 * <p>
	 * The type is either {@code SYSTEM} or {@code USER}
	 *
	 * @return the type of the plugin site
	 */
	public PluginSiteType getType();

}
