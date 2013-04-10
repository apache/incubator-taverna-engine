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
package uk.org.taverna.platform.capability.api;

import java.net.URI;
import java.util.List;
import java.util.Set;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.ConfigurationDefinition;
import uk.org.taverna.scufl2.api.port.ActivityPort;

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
	 * @deprecated use {@link getActivityTypes}
	 */
	@Deprecated
	public List<URI> getActivityURIs();

	/**
	 * Returns the available activity types.
	 *
	 * @return the available activity types
	 */
	public Set<URI> getActivityTypes();

	/**
	 * Returns true iff the activity type exists.
	 *
	 * @param uri
	 *            the activity type to check
	 * @return true iff the activity type exists
	 */
	public boolean activityExists(URI activityType);

	/**
	 * Returns a definition of the configuration required by an activity.
	 *
	 * @param activityType
	 *            the activity type
	 * @return a definition of the configuration required by an activity
	 * @throws ActivityNotFoundException
	 *             if an activity cannot be found for the specified URI
	 * @throws ActivityConfigurationException
	 *             if the ConfigurationDefinition cannot be created
	 */
	public ConfigurationDefinition getActivityConfigurationDefinition(URI activityType)
			throws ActivityNotFoundException, ActivityConfigurationException;

	/**
	 * Returns the ports for an activity configured with the configuration.
	 *
	 * @param configuration an activity configuration
	 * @return the ports for an activity configured with the configuration
	 */

	/**
	 * Returns the ports that would be created by configuring the activity type with the
	 * configuration.
	 *
	 * If a configuration does not create any ports for the activity an empty set is returned.
	 *
	 * @param activityType
	 *            the type of the activity
	 * @param configuration
	 *            the configuration for the activity
	 * @throws ActivityNotFoundException
	 *             if a configurable version of the activity cannot be found
	 * @throws ActivityConfigurationException
	 *             if the activity cannot be configured
	 * @return
	 */
	public Set<ActivityPort> getActivityPorts(URI activityType, Configuration configuration)
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
	 * Returns the activity for the specified activity type.
	 *
	 * If configuration is not null the returned activity will be configured.
	 *
	 * @param activityType
	 *            the activity type
	 * @param configuration
	 *            the configuration for the activity, can be <code>null</code>
	 * @return the activity for the specified activityType
	 * @throws ActivityNotFoundException
	 *             if an activity cannot be found for the specified activity type
	 * @throws ActivityConfigurationException
	 *             if the configuration is not valid
	 */
	public net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?> createActivity(URI activityType,
			Configuration configuration) throws ActivityNotFoundException,
			ActivityConfigurationException;

}
