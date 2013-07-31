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
package uk.org.taverna.configuration.proxy.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.axis.AxisProperties;
import org.apache.log4j.Logger;

import uk.org.taverna.configuration.AbstractConfigurable;
import uk.org.taverna.configuration.ConfigurationManager;
import uk.org.taverna.configuration.proxy.HttpProxyConfiguration;

/**
 * The HttpProxyConfiguration handles the configuration of HTTP
 * proxy when Taverna is launched.
 *
 * @author alanrw
 * @author David Withers
 */
public class HttpProxyConfigurationImpl extends AbstractConfigurable implements
		HttpProxyConfiguration {

	private Map<String, String> defaultPropertyMap;

	/**
	 * A Properties that holds the original System settings for HTTP proxy. They
	 * need to be copied as they are overwritten if something other than those
	 * System settings are used.
	 */
	private Properties originalSystemSettings;

	private static Logger logger = Logger.getLogger(HttpProxyConfigurationImpl.class);

	/**
	 * Read the original System settings. Read the configuration file and set
	 * the proxy settings accordingly.
	 */
	private HttpProxyConfigurationImpl(ConfigurationManager configurationManager) {
		super(configurationManager);
		changeProxySettings();
	}

	/**
	 * Return the original System property value for the specified key. Null of
	 * no such property existed.
	 *
	 * @param key
	 * @return
	 */
	public String getOriginalSystemSetting(String key) {
		if (originalSystemSettings == null) {
			originalSystemSettings = new Properties();
			originalSystemSettings.putAll(System.getProperties());
		}
		return originalSystemSettings.getProperty(key);
	}

	@Override
	public void changeProxySettings() {
		String option = getProperty(PROXY_USE_OPTION);
		if (option.equals(USE_SYSTEM_PROPERTIES_OPTION)) {
			changeSystemProperty(PROXY_HOST, getOriginalSystemSetting(PROXY_HOST));
			changeSystemProperty(PROXY_PORT, getOriginalSystemSetting(PROXY_PORT));
			changeSystemProperty(PROXY_USER, getOriginalSystemSetting(PROXY_USER));
			changeSystemProperty(PROXY_PASSWORD, getOriginalSystemSetting(PROXY_PASSWORD));
			changeSystemProperty(NON_PROXY_HOSTS, getOriginalSystemSetting(NON_PROXY_HOSTS));
		} else if (option.equals(USE_NO_PROXY_OPTION)) {
			changeSystemProperty(PROXY_HOST, null);
			changeSystemProperty(PROXY_PORT, null);
			changeSystemProperty(PROXY_USER, null);
			changeSystemProperty(PROXY_PASSWORD, null);
			changeSystemProperty(NON_PROXY_HOSTS, null);
		} else if (option.equals(USE_SPECIFIED_VALUES_OPTION)) {
			changeSystemProperty(PROXY_HOST, getProperty(TAVERNA_PROXY_HOST));
			changeSystemProperty(PROXY_PORT, getProperty(TAVERNA_PROXY_PORT));
			changeSystemProperty(PROXY_USER, getProperty(TAVERNA_PROXY_USER));
			changeSystemProperty(PROXY_PASSWORD, getProperty(TAVERNA_PROXY_PASSWORD));
			changeSystemProperty(NON_PROXY_HOSTS, getProperty(TAVERNA_NON_PROXY_HOSTS));
		}
		logger.info(PROXY_HOST + " is " + System.getProperty(PROXY_HOST));
		logger.info(PROXY_PORT + " is " + System.getProperty(PROXY_PORT));
		logger.info(PROXY_USER + " is " + System.getProperty(PROXY_USER));
		logger.info(NON_PROXY_HOSTS + " is " + System.getProperty(NON_PROXY_HOSTS));
	}

	/**
	 * Change the specified System property to the given value. If the value is
	 * null then the property is cleared.
	 *
	 * @param key
	 * @param value
	 */
	private void changeSystemProperty(String key, String value) {
		if ((value == null) || value.equals("")) {
			System.clearProperty(key);
		} else {
			System.setProperty(key, value);
		}
		AxisProperties.setProperty(key, (value == null ? "" : value));
	}

	@Override
	public Map<String, String> getDefaultPropertyMap() {
		if (defaultPropertyMap == null) {
			defaultPropertyMap = new HashMap<String, String>();
			defaultPropertyMap.put(PROXY_USE_OPTION, USE_SYSTEM_PROPERTIES_OPTION);
			String proxyHost = getOriginalSystemSetting(PROXY_HOST);
			defaultPropertyMap.put(SYSTEM_PROXY_HOST, proxyHost == null ? "" : proxyHost);
			String proxyPort = getOriginalSystemSetting(PROXY_PORT);
			defaultPropertyMap.put(SYSTEM_PROXY_PORT, proxyPort == null ? "" : proxyPort);
			String proxyUser = getOriginalSystemSetting(PROXY_USER);
			defaultPropertyMap.put(SYSTEM_PROXY_USER, proxyUser == null ? "" : proxyUser);
			String proxyPassword = getOriginalSystemSetting(PROXY_PASSWORD);
			defaultPropertyMap.put(SYSTEM_PROXY_PASSWORD, proxyPassword == null ? "" : proxyPassword);
			String nonProxyHosts = getOriginalSystemSetting(NON_PROXY_HOSTS);
			defaultPropertyMap.put(SYSTEM_NON_PROXY_HOSTS, nonProxyHosts == null ? "" : nonProxyHosts);
			defaultPropertyMap.put(TAVERNA_PROXY_HOST, "");
			defaultPropertyMap.put(TAVERNA_PROXY_PORT, "");
			defaultPropertyMap.put(TAVERNA_PROXY_USER, "");
			defaultPropertyMap.put(TAVERNA_PROXY_PASSWORD, "");
			defaultPropertyMap.put(TAVERNA_NON_PROXY_HOSTS, "");
		}
		return defaultPropertyMap;
	}

	@Override
	public String getUUID() {
		return "B307A902-F292-4D2F-B8E7-00CC983982B6";
	}

	@Override
	public String getDisplayName() {
		return "HTTP proxy";
	}

	@Override
	public String getFilePrefix() {
		return "HttpProxy";
	}

	@Override
	public String getCategory() {
		return "general";
	}
}
