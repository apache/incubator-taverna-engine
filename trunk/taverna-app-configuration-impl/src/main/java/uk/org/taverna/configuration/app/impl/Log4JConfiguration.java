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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;

import uk.org.taverna.configuration.app.ApplicationConfiguration;

public class Log4JConfiguration {
	public final static String LOG4J_PROPERTIES = "log4j.properties";

	private static boolean log4jConfigured = false;

	private ApplicationConfiguration applicationConfiguration;

	private Properties properties ;

	public Log4JConfiguration() {
//		prepareLog4J();
	}

	public void prepareLog4J() {
		if (!log4jConfigured) {
			Properties log4jProperties = getLogProperties();
			if (log4jProperties != null && ! log4jProperties.isEmpty()) {
				LogManager.resetConfiguration();
				PropertyConfigurator.configure(log4jProperties);
			}

			String logFilePath = applicationConfiguration.getLogFile().getAbsolutePath();
			PatternLayout layout = new PatternLayout("%-5p %d{ISO8601} (%c:%L) - %m%n");

			// Add file appender
			RollingFileAppender appender;
			try {
				appender = new RollingFileAppender(layout, logFilePath);
				appender.setMaxFileSize("1MB");
				appender.setEncoding("UTF-8");
				appender.setMaxBackupIndex(4);
				// Let root logger decide level
				appender.setThreshold(Level.ALL);
				LogManager.getRootLogger().addAppender(appender);
			} catch (IOException e) {
				System.err.println("Could not log to " + logFilePath);
			}

			log4jConfigured = true;
		}
	}

	/**
	 * Initialises and provides access to the list of Properties.
	 * @return
	 */
	public Properties getLogProperties() {
		if (properties == null) {
			InputStream is = getLogPropertiesInputStream();
			if (is != null) {
				try {
					properties = new Properties();
					properties.load(is);
//					properties.putAll(System.getProperties());
				}  catch (IOException e) {
					errorLog("An error occurred trying to load the " + LOG4J_PROPERTIES + " file",e);
				}
			}
		}
		return properties;
	}

	/**
	 * Return an input stream to the configuration file, or null if it can't be found
	 * @return
	 */
	private InputStream getLogPropertiesInputStream() {
		InputStream result = null;
		File propertiesFile = getLogPropertiesFile();
		if (propertiesFile!=null) {
			try {
				result=new FileInputStream(propertiesFile);
			} catch (FileNotFoundException e) {
				errorLog("Unable to find "+LOG4J_PROPERTIES,e);
			}
		}
		else {
			errorLog("Unable to determine file for "+LOG4J_PROPERTIES,null);
		}
		return result;
	}

	/**
	 * Returns a File object to the configuration file or null if it cannot be found.
	 *
	 * @return
	 */
	private File getLogPropertiesFile() {
		File home = applicationConfiguration.getApplicationHomeDir();
		File startup = applicationConfiguration.getStartupDir();
		File result=null;
		if (home!=null) {
			File file = new File(new File(home, ApplicationConfiguration.CONF_DIR), LOG4J_PROPERTIES);
			if (file.exists()) {
				result=file;
			}
		}
		if (result==null && startup!=null) {
			File file = new File(new File(startup, ApplicationConfiguration.CONF_DIR), LOG4J_PROPERTIES);
			if (file.exists()) {
				result=file;
			}
		}
		return result;
	}

	private void errorLog(String message, Throwable exception) {
		System.out.println(message);
		if (exception!=null) {
			exception.printStackTrace();
		}

	}

	/**
	 * Sets the applicationConfiguration.
	 *
	 * @param applicationConfiguration the new value of applicationConfiguration
	 */
	public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

}
