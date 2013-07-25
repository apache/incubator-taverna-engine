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
package uk.org.taverna.configuration.proxy;

import uk.org.taverna.configuration.Configurable;

/**
 * The HttpProxyConfiguration handles the configuration of HTTP
 * proxy when Taverna is launched.
 *
 * @author David Withers
 */
public interface HttpProxyConfiguration extends Configurable {

	/**
	 * The acceptable values for which proxy values to use
	 */
	public static String USE_SYSTEM_PROPERTIES_OPTION = "useSystemProperties";
	public static String USE_NO_PROXY_OPTION = "useNoProxy";
	public static String USE_SPECIFIED_VALUES_OPTION = "useSpecifiedValues";

	/**
	 * The key within the Properties where the value will indicate which set of
	 * proxy values to use
	 */
	public static String PROXY_USE_OPTION = "proxyUseOption";

	/**
	 * The keys within the Properties for the ad hoc Taverna proxy settings
	 */
	public static String TAVERNA_PROXY_HOST = "tavernaProxyHost";
	public static String TAVERNA_PROXY_PORT = "tavernaProxyPort";
	public static String TAVERNA_PROXY_USER = "tavernaProxyUser";
	public static String TAVERNA_PROXY_PASSWORD = "tavernaProxyPassword";
	public static String TAVERNA_NON_PROXY_HOSTS = "tavernaNonProxyHosts";

	/**
	 * The keys within the Properties for the System proxy settings
	 */
	public static String SYSTEM_PROXY_HOST = "systemProxyHost";
	public static String SYSTEM_PROXY_PORT = "systemProxyPort";
	public static String SYSTEM_PROXY_USER = "systemProxyUser";
	public static String SYSTEM_PROXY_PASSWORD = "systemProxyPassword";
	public static String SYSTEM_NON_PROXY_HOSTS = "systemNonProxyHosts";

	/**
	 * The keys within the System Properties that are used for specifying HTTP
	 * proxy information
	 */
	public static String PROXY_HOST = "http.proxyHost";
	public static String PROXY_PORT = "http.proxyPort";
	public static String PROXY_USER = "http.proxyUser";
	public static String PROXY_PASSWORD = "http.proxyPassword";
	public static String NON_PROXY_HOSTS = "http.nonProxyHosts";

	/**
	 * Change the System Proxy settings according to the property values.
	 */
	public void changeProxySettings();

}