/*******************************************************************************
 * Copyright (C) 2012 The University of Manchester
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
package uk.org.taverna.configuration.app;

import java.io.File;

import uk.org.taverna.commons.profile.xml.jaxb.ApplicationProfile;

/**
 * Represent the application config as it has been specified in
 * {@value #PROPERTIES}. This configuration specifies the application's name
 * and title, etc.
 * <p>
 * An application would typically provide the {@value #PROPERTIES} file on the classpath under
 * a <code>conf</code> directory, or in a <code>conf</code> directory in the
 * application's distribution directory.
 *
 * @author Stian Soiland-Reyes
 * @author David Withers
 */
public interface ApplicationConfiguration {

	public static final String CONF_DIR = "conf/";
	public static final String PLUGINS_DIR = "plugins";

	public String getName();

	public String getTitle();

	public File getStartupDir();

	public File getApplicationHomeDir();

	public File getUserPluginDir();

	public File getSystemPluginDir();

	public File getLogFile();

	public File getLogDir();

	public ApplicationProfile getApplicationProfile();

}