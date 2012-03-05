/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester
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
package uk.org.taverna.platform.capability.activity;

import java.net.URI;
import java.util.List;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;

/**
 * Service for discovering available activities and the properties required to configure the
 * activities.
 *
 * @author David Withers
 */
public interface ActivityService {

	/**
	 * Returns a list URI's that identify available activities.
	 *
	 * @return a list URI's that identify available activities
	 */
	public List<URI> getActivityURIs();

	/**
	 * Returns true iff an activity exists for the specified URI.
	 *
	 * @param uri
	 *            the activity URI to check
	 * @return true if an activity exists for the specified URI
	 */
	public boolean activityExists(URI uri);

	/**
	 * Returns a definition of the configuration required by an activity.
	 *
	 * @param uri
	 *            a URI that identifies an activity
	 * @return a definition of the configuration required by an activity
	 * @throws ActivityNotFoundException
	 *             if an activity cannot be found for the specified URI
	 * @throws ActivityConfigurationException
	 *             if the ConfigurationDefinition cannot be created
	 */
	public ConfigurationDefinition getActivityConfigurationDefinition(URI uri)
			throws ActivityNotFoundException, ActivityConfigurationException;

	/**
	 * Adds ports to the activity that would be created by configuring the activity with the
	 * configuration.
	 *
	 * A configuration may not create any dynamic ports for the activity.
	 *
	 * @param activity
	 *            the activity to add ports to
	 * @param configuration
	 *            the configuration for the activity
	 * @throws ActivityNotFoundException
	 *             if a configurable version of the activity cannot be found
	 * @throws ActivityConfigurationException
	 *             if the activity cannot be configured
	 */
	public void addDynamicPorts(Activity activity, Configuration configuration)
			throws ActivityNotFoundException, ActivityConfigurationException;

	/**
	 * Returns the activity for the specified URI.
	 *
	 * If configuration is not null the returned activity will be configured.
	 *
	 * @param uri
	 *            a URI that identifies an activity
	 * @param configuration
	 *            the configuration for the activity, can be <code>null</code>
	 * @return the activity for the specified URI
	 * @throws ActivityNotFoundException
	 *             if an activity cannot be found for the specified URI
	 * @throws ActivityConfigurationException
	 *             if the configuration is not valid
	 */
	public net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?> createActivity(URI uri,
			Configuration configuration) throws ActivityNotFoundException,
			ActivityConfigurationException;

}
