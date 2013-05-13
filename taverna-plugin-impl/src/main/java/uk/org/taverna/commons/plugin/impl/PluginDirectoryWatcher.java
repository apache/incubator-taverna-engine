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

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;

import uk.org.taverna.commons.plugin.Plugin;
import uk.org.taverna.commons.plugin.PluginException;

/**
 * Watches a plugin directory and adds or removes plugins when plugin files are added or removed
 * from the directory.
 * 
 * @author David Withers
 */
public class PluginDirectoryWatcher extends FileAlterationListenerAdaptor {

	private static final Logger logger = Logger.getLogger(PluginDirectoryWatcher.class);

	private final PluginManagerImpl pluginManager;
	private final File directory;

	private FileAlterationMonitor monitor;

	public PluginDirectoryWatcher(PluginManagerImpl pluginManager, File directory) {
		this.pluginManager = pluginManager;
		this.directory = directory;
		FileAlterationObserver observer = new FileAlterationObserver(directory);
		observer.addListener(this);
		monitor = new FileAlterationMonitor();
		monitor.addObserver(observer);
	}

	/**
	 * Starts watching the plugin directory.
	 * 
	 * @throws PluginException
	 */
	public void start() throws PluginException {
		try {
			monitor.start();
		} catch (Exception e) {
			throw new PluginException(String.format("Error starting watch on %1$s.",
					directory.getAbsolutePath()), e);
		}
	}

	/**
	 * Stops watching the plugin directory.
	 * 
	 * @throws PluginException
	 */
	public void stop() throws PluginException {
		try {
			monitor.stop();
		} catch (Exception e) {
			throw new PluginException(String.format("Error stopping watch on %1$s.",
					directory.getAbsolutePath()), e);
		}
	}

	@Override
	public void onFileCreate(File file) {
		try {
			Plugin plugin = pluginManager.installPlugin(file);
			plugin.start();
		} catch (PluginException e) {
			logger.warn("Error loading plugin file " + file, e);
		}
	}

	@Override
	public void onFileChange(File file) {
		onFileDelete(file);
		onFileCreate(file);
	}

	@Override
	public void onFileDelete(File file) {
		pluginManager.uninstallPlugin(file);
	}

}
