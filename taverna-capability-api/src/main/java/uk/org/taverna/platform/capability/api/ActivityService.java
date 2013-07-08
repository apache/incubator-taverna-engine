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
import java.util.Set;

import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Service for discovering available activities and the properties required to configure the
 * activities.
 *
 * @author David Withers
 */
public interface ActivityService {

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
	 * Returns the JSON Schema for the configuration required by an activity.
	 *
	 * @param activityType
	 *            the activity type
	 * @return the JSON Schema for the configuration required by an activity
	 * @throws ActivityNotFoundException
	 *             if an activity cannot be found for the specified URI
	 * @throws ActivityConfigurationException
	 *             if the JSON Schema cannot be created
	 */
	public JsonNode getActivityConfigurationSchema(URI activityType)
			throws ActivityNotFoundException, ActivityConfigurationException;

	/**
	 * Returns the input ports that the activity type requires to be present in order to execute
	 * with the specified configuration.
	 * <p>
	 * If the activity does not require any input port for the configuration then an empty set is
	 * returned.
	 *
	 * @param configuration
	 *            the activity configuration
	 * @throws ActivityNotFoundException
	 *             if the activity cannot be found
	 * @throws ActivityConfigurationException
	 *             if the activity configuration is incorrect
	 * @return the input ports that the activity requires to be present in order to execute
	 */
	public Set<InputActivityPort> getActivityInputPorts(URI activityType,
			JsonNode configuration) throws ActivityNotFoundException,
			ActivityConfigurationException;

	/**
	 * Returns the output ports that the activity type requires to be present in order to execute
	 * with the specified configuration.
	 * <p>
	 * If the activity type does not require any output ports for the configuration then an empty
	 * set is returned.
	 *
	 * @param configuration
	 *            the activity configuration
	 * @throws ActivityNotFoundException
	 *             if the activity cannot be found
	 * @throws ActivityConfigurationException
	 *             if the activity configuration is incorrect
	 * @return the output ports that the activity requires to be present in order to execute
	 */
	public Set<OutputActivityPort> getActivityOutputPorts(URI activityType,
			JsonNode configuration) throws ActivityNotFoundException,
			ActivityConfigurationException;

	/**
	 * Returns the activity for the specified activity type.
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
	public net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?> createActivity(
			URI activityType, JsonNode configuration) throws ActivityNotFoundException,
			ActivityConfigurationException;

}
