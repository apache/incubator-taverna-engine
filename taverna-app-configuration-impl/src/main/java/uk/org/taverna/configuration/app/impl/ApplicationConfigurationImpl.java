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
package uk.org.taverna.configuration.app.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import uk.org.taverna.commons.profile.xml.jaxb.ApplicationProfile;
import uk.org.taverna.configuration.app.ApplicationConfiguration;

/**
 * Represent the application config as it has been specified in {@value #PROPERTIES}. This
 * configuration specifies the application's name and title, etc.
 * <p>
 * An application would typically provide the {@value #PROPERTIES} file on the classpath under a
 * <code>conf</code> directory, or in a <code>conf</code> directory in the application's
 * distribution directory.
 *
 * @author Stian Soiland-Reyes
 * @author David Withers
 */
public class ApplicationConfigurationImpl implements ApplicationConfiguration {

	private static final Logger logger = Logger.getLogger(ApplicationConfigurationImpl.class);

	private static final String UNKNOWN_APPLICATION = "unknownApplication-"
			+ UUID.randomUUID().toString();

	public static final String APP_HOME = "taverna.app.home";
	public static final String APP_STARTUP = "taverna.app.startup";
	public static final String APPLICATION_PROFILE = "ApplicationProfile.xml";

	private File startupDir;
	private File homeDir;

	private ApplicationProfile applicationProfile;
	private ApplicationProfile defaultApplicationProfile;

	public ApplicationConfigurationImpl() {
	}

	@Override
	public String getName() {
		String name = null;
		ApplicationProfile profile = getDefaultApplicationProfile();
		if (profile != null) {
			name = profile.getName();
		}
		if (name == null) {
			logger.error("ApplicationConfig could not determine application name, using "
					+ UNKNOWN_APPLICATION);
			return UNKNOWN_APPLICATION;
		}
		return name;
	}

	@Override
	public String getTitle() {
		return getName();
	}

	@Override
	public File getStartupDir() {
		if (startupDir == null) {
			String startupDirName = System.getProperty(APP_STARTUP);
			if (startupDirName != null) {
				startupDir = new File(startupDirName).getAbsoluteFile();
			}
		}
		return startupDir;
	}

	@Override
	public synchronized File getApplicationHomeDir() {
		if (homeDir == null) {
			if (getName().equals(ApplicationConfigurationImpl.UNKNOWN_APPLICATION)) {
				try {
					// Make a temporary home directory as a backup
					homeDir = File.createTempFile(getName(), "home");
					homeDir.delete();
					homeDir.mkdirs();
				} catch (IOException e) {
					throw new IllegalStateException("Can't create temporary application home", e);
				}
				logger.warn("Could not determine application's user home,"
						+ " using temporary dir " + homeDir);
			} else {
				homeDir = new ApplicationUserHome(getName(), System.getProperty(APP_HOME)).getAppUserHome();
			}
			if (homeDir == null || !homeDir.isDirectory()) {
				throw new IllegalStateException("Could not create application home directory "
						+ homeDir);
			}
		}
		return homeDir;
	}

	@Override
	public File getUserPluginDir() {
		File userPluginsDir = new File(getApplicationHomeDir(), PLUGINS_DIR);
		try {
			userPluginsDir.mkdirs();
		} catch (SecurityException e) {
			logger.warn("Error creating user plugin directory at " + userPluginsDir, e);
		}
		return userPluginsDir;
	}

	@Override
	public File getSystemPluginDir() {
		File systemPluginsDir = new File(getStartupDir(), PLUGINS_DIR);
		try {
			systemPluginsDir.mkdirs();
		} catch (SecurityException e) {
			logger.debug("Error creating system plugin directory at " + systemPluginsDir, e);
		}
		return systemPluginsDir;
	}

	@Override
	public File getLogFile() {
		return new File(getLogDir(), getName() + ".log");
	}

	@Override
	public File getLogDir() {
		File logDir = new File(getApplicationHomeDir(), "logs");
		logDir.mkdirs();
		if (!logDir.isDirectory()) {
			throw new IllegalStateException("Could not create log directory " + logDir);
		}
		return logDir;
	}

	private void findInClassLoader(List<URI> configs, ClassLoader classLoader, String resourcePath) {
		Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(resourcePath);
		} catch (IOException ex) {
			System.err.println("Error looking for " + resourcePath + " in " + classLoader);
			ex.printStackTrace();
			return;
		}
		while (resources.hasMoreElements()) {
			URL configURL = resources.nextElement();
			try {
				configs.add(configURL.toURI());
			} catch (URISyntaxException ex) {
				throw new RuntimeException("Invalid URL from getResource(): " + configURL, ex);
			}
		}
	}

	/**
	 * Attempt to load application properties from propertyFileName.
	 * <p>
	 * Will attempt to load a property file from the locations below. The first non-empty properties
	 * successfully loaded will be returned.
	 * <ol>
	 * <li>$startup/conf/$resourceName</li>
	 * <li>$startup/$resourceName</li>
	 * <li>$contextClassPath/conf/$resourceName</li>
	 * <li>$contextClassPath/$resourceName</li>
	 * <li>$classpath/conf/$resourceName</li>
	 * <li>$classpath/$resourceName</li>
	 * </ol>
	 * <p>
	 * Where <code>$startup</code> is this application's startup directory as determined by
	 * {@link #getStartupDir()}, and <code>$contextClassPath</code> means a search using
	 * {@link ClassLoader#getResources(String)} from the classloader returned by
	 * {@link Thread#getContextClassLoader()} and then again <code>$classpath</code> for the
	 * classloader of {@link #getClass()} of this instance.
	 * </p>
	 * <p>
	 * If none of these sources could find a non-empty property file, a warning is logged, and an
	 * empty {@link Properties} instance is returned.
	 *
	 * @param resourceName
	 *            Relative filename of property file
	 *
	 * @return Loaded or empty {@link Properties} instance.
	 */
	protected Properties loadProperties(String resourceName) {
		// Ordered list of config locations to attempt to load
		// properties from
		List<URI> configs = new ArrayList<URI>();

		File startupDir = getStartupDir();
		if (startupDir != null) {
			configs.add(startupDir.toURI().resolve(CONF_DIR).resolve(resourceName));
			configs.add(startupDir.toURI().resolve(resourceName));
		}

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		findInClassLoader(configs, contextClassLoader, CONF_DIR + resourceName);
		findInClassLoader(configs, contextClassLoader, resourceName);

		findInClassLoader(configs, getClass().getClassLoader(), CONF_DIR + resourceName);
		findInClassLoader(configs, getClass().getClassLoader(), resourceName);

		Properties loadedProps = new Properties();
		for (URI config : configs) {
			try {
				InputStream inputStream = config.toURL().openStream();
				loadedProps.load(inputStream);
			} catch (MalformedURLException ex) {
				throw new RuntimeException("Invalid URL from URI: " + config, ex);
			} catch (IOException ex) {
				continue; // Probably not found/access denied
			}
			if (!loadedProps.isEmpty()) {
				logger.debug("Loaded " + resourceName + " from " + config);
				return loadedProps;
			}
		}
		logger.debug("Could not find application properties file " + resourceName);
		return loadedProps;
	}

	@Override
	public ApplicationProfile getApplicationProfile() {
		if (applicationProfile == null) {
			File applicationProfileFile = new File(getApplicationHomeDir(), APPLICATION_PROFILE);
			if (!applicationProfileFile.exists()) {
				logger.debug("Application profile not found at " + applicationProfileFile);
				return getDefaultApplicationProfile();
			}
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(ApplicationProfile.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				applicationProfile = (ApplicationProfile) unmarshaller.unmarshal(applicationProfileFile);
			} catch (JAXBException e) {
				logger.error("Could not read application profile from " + applicationProfileFile, e);
			}
			if (applicationProfile == null) {
				logger.debug("Application profile not found at " + applicationProfileFile);
				return getDefaultApplicationProfile();
			}
		}
		return applicationProfile;
	}

	public ApplicationProfile getDefaultApplicationProfile() {
		if (defaultApplicationProfile == null) {
			File applicationProfileFile = new File(getStartupDir(), APPLICATION_PROFILE);
			if (applicationProfileFile.exists()) {
				try {
					JAXBContext jaxbContext = JAXBContext.newInstance(ApplicationProfile.class);
					Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
					defaultApplicationProfile = (ApplicationProfile) unmarshaller.unmarshal(applicationProfileFile);
				} catch (JAXBException e) {
					throw new IllegalStateException("Could not read application profile from " + applicationProfileFile);
				}
			}
		}
		return defaultApplicationProfile;
	}

}
