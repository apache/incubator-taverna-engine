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
package uk.org.taverna.commons.services;

import java.net.URI;
import java.util.Set;

import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Register of Taverna services.
 *
 * @author David Withers
 */
public interface ServiceRegistry {

	/**
	 * Returns the activity types in the registry.
	 *
	 * @return the activity types in the registry
	 */
	public Set<URI> getActivityTypes();

	/**
	 * Returns the JSON Schema for the configuration required by an activity.
	 *
	 * @param activityType
	 *            the activity type
	 * @return the JSON Schema for the configuration required by an activity
	 * @throws ActivityTypeNotFoundException
	 *             if the activity type is not in the registry
	 */
	public JsonNode getActivityConfigurationSchema(URI activityType)
			throws InvalidConfigurationException, ActivityTypeNotFoundException;

	/**
	 * Returns the input ports that the activity type requires to be present in order to execute
	 * with the specified configuration.
	 * <p>
	 * If the activity does not require any input port for the configuration then an empty set is
	 * returned.
	 *
	 * @param configuration
	 *            the activity configuration
	 * @throws ActivityTypeNotFoundException
	 *             if the activity type is not in the registry
	 * @return the input ports that the activity requires to be present in order to execute
	 */
	public Set<InputActivityPort> getActivityInputPorts(URI activityType,
			JsonNode configuration) throws InvalidConfigurationException, ActivityTypeNotFoundException;

	/**
	 * Returns the output ports that the activity type requires to be present in order to execute
	 * with the specified configuration.
	 * <p>
	 * If the activity type does not require any output ports for the configuration then an empty
	 * set is returned.
	 *
	 * @param configuration
	 *            the activity configuration
	 * @throws ActivityTypeNotFoundException
	 *             if the activity type is not in the registry
	 * @return the output ports that the activity requires to be present in order to execute
	 */
	public Set<OutputActivityPort> getActivityOutputPorts(URI activityType,
			JsonNode configuration) throws InvalidConfigurationException, ActivityTypeNotFoundException;

}
