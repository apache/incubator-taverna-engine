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
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * A plugin adds functionality to the application by providing implementations of application
 * services.
 *
 * @author David Withers
 */
public interface Plugin {

	public static enum State {
		UNINSTALLED, INSTALLED, STARTED, STOPPED
	}

	public String getId();

	public String getName();

	public String getDescription();

	public String getOrganization();

	public Version getVersion();

	/**
	 * Returns the state of the plugin.
	 *
	 * @return the state of the plugin
	 */
	public State getState();

	/**
	 * Starts the plugin and sets the state to STARTED.
	 * <p>
	 * If the plugin state is STARTED this method will have no effect.
	 * <p>
	 * All plugin bundles are not currently started will be started.
	 *
	 * @throws PluginException
	 *             if the plugin state is UNINSTALLED or any of the plugin bundles cannot be started
	 */
	public void start() throws PluginException;

	/**
	 * Stops the plugin and sets the state to STOPPED.
	 * <p>
	 * If the plugin state is not STARTED this method will have no effect.
	 * <p>
	 * All plugin bundles not used elsewhere will be stopped.
	 *
	 * @throws PluginException
	 *             if any of the plugin bundles cannot be stopped
	 */
	public void stop() throws PluginException;

	public void uninstall() throws PluginException;

	public File getFile();

	public Set<Bundle> getBundles();

}
