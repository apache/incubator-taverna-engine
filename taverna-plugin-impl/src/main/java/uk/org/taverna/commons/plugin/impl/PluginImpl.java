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

import static uk.org.taverna.commons.plugin.Plugin.State.STARTED;
import static uk.org.taverna.commons.plugin.Plugin.State.STOPPED;
import static uk.org.taverna.commons.plugin.Plugin.State.UNINSTALLED;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import uk.org.taverna.commons.plugin.Plugin;
import uk.org.taverna.commons.plugin.PluginException;
import uk.org.taverna.commons.plugin.xml.jaxb.PluginInfo;

/**
 * @author David Withers
 */
public class PluginImpl implements Plugin {

	private static final Logger logger = Logger.getLogger(PluginImpl.class);

	private PluginManagerImpl pluginManager;

	private State state = UNINSTALLED;

	private File file;
	private String id, name, description, organization;
	private Version version;
	private Set<Bundle> bundles = new HashSet<Bundle>();

	public PluginImpl(PluginManagerImpl pluginManager, File file, PluginInfo pluginInfo) {
		this.pluginManager = pluginManager;
		this.file = file;
		id = pluginInfo.getId();
		name = pluginInfo.getName();
		description = pluginInfo.getDescription();
		organization = pluginInfo.getOrganization();
		version = Version.parseVersion(pluginInfo.getVersion());
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getOrganization() {
		return organization;
	}

	@Override
	public Version getVersion() {
		return version;
	}

	@Override
	public State getState() {
		return state;
	}

	void setState(State state) {
		this.state = state;
	}

	@Override
	public void start() throws PluginException {
		if (state == STARTED) {
			return;
		}
		if (state == UNINSTALLED) {
			throw new PluginException("Cannot start an uninstalled plugin");
		}
		List<Bundle> startedBundles = new ArrayList<Bundle>();
		for (Bundle bundle : getBundles()) {
			if (bundle.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
				if (bundle.getState() != Bundle.ACTIVE) {
					try {
						bundle.start();
						startedBundles.add(bundle);
					} catch (BundleException e) {
						// clean up by stopping bundles already started
						for (Bundle startedBundle : startedBundles) {
							try {
								startedBundle.stop();
							} catch (BundleException ex) {
								logger.warn("Error unistalling bundle", ex);
							}
						}
						throw new PluginException(String.format("Error starting bundle %1$s",
								bundle.getSymbolicName()), e);
					}
				}
			}
		}
	}

	@Override
	public void stop() throws PluginException {
		if (state == STARTED) {
			List<Plugin> installedPlugins = pluginManager.getInstalledPlugins();
			for (Bundle bundle : getBundles()) {
				// check if bundle is used by other plugins
				boolean bundleUsed = false;
				for (Plugin installedPlugin : installedPlugins) {
					if (!installedPlugin.equals(this) && installedPlugin.getState() == STARTED) {
						if (installedPlugin.getBundles().contains(bundle)) {
							bundleUsed = true;
							break;
						}
					}
				}
				if (!bundleUsed) {
					try {
						logger.info("Stopping bundle " + bundle.getSymbolicName());
						bundle.stop();
					} catch (BundleException e) {
						logger.warn(
								String.format("Error stopping bundle %1$s for plugin %2$s",
										bundle.getSymbolicName(), getName()), e);
					}
				}
			}
			state = STOPPED;
		}
	}

	@Override
	public void uninstall() throws PluginException {
		if (state != UNINSTALLED) {
			pluginManager.uninstallPlugin(this);
			state = UNINSTALLED;
		}
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public Set<Bundle> getBundles() {
		return bundles;
	}

}
