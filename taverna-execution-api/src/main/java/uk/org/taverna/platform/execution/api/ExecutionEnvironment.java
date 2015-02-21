/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester
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
package uk.org.taverna.platform.execution.api;

import java.net.URI;
import java.util.Set;

import uk.org.taverna.platform.capability.api.ActivityConfigurationException;
import uk.org.taverna.platform.capability.api.ActivityNotFoundException;
import uk.org.taverna.platform.capability.api.DispatchLayerConfigurationException;
import uk.org.taverna.platform.capability.api.DispatchLayerNotFoundException;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The ExecutionEnvironment specifies the capabilities of a workflow execution environment.
 *
 * @author David Withers
 */
public interface ExecutionEnvironment {

	/**
	 * Returns the identifier for this ExecutionEnvironment.
	 *
	 * @return the identifier for this ExecutionEnvironment
	 */
	public String getID();

	/**
	 * Returns the name of this ExecutionEnvironment.
	 *
	 * @return the name of this ExecutionEnvironment
	 */
	public String getName();

	/**
	 * Returns a description of this ExecutionEnvironment.
	 *
	 * @return a description of this ExecutionEnvironment
	 */
	public String getDescription();

	/**
	 * Returns the ExecutionService that provides this ExecutionEnvironment.
	 *
	 * @return the ExecutionService that provides this ExecutionEnvironment
	 */
	public ExecutionService getExecutionService();

	/**
	 * Returns the activity types available in this ExecutionEnvironment.
	 *
	 * @return the activity types available in this ExecutionEnvironment
	 */
	public Set<URI> getActivityTypes();

	/**
	 * Returns true iff an activity exists for the specified URI in this ExecutionEnvironment.
	 *
	 * @param uri
	 *            the activity URI to check
	 * @return true if an activity exists for the specified URI in this ExecutionEnvironment
	 */
	public boolean activityExists(URI uri);

	/**
	 * Returns a JSON Schema for the configuration required by an activity.
	 *
	 * @param uri
	 *            a URI that identifies an activity
	 * @return a JSON Schema for the configuration required by an activity
	 * @throws ActivityNotFoundException
	 *             if an activity cannot be found for the specified URI
	 * @throws ActivityConfigurationException
	 *             if the ConfigurationDefinition cannot be created
	 */
	public JsonNode getActivityConfigurationSchema(URI uri)
			throws ActivityNotFoundException, ActivityConfigurationException;

	/**
	 * Returns the dispatch layer types available in this ExecutionEnvironment.
	 *
	 * @return the dispatch layer types available in this ExecutionEnvironment
	 */
	public Set<URI> getDispatchLayerTypes();

	/**
	 * Returns true iff a dispatch layer exists for the specified URI in this ExecutionEnvironment.
	 *
	 * @param uri
	 *            the dispatch layer URI to check
	 * @return true if a dispatch layer exists for the specified URI in this ExecutionEnvironment
	 */
	public boolean dispatchLayerExists(URI uri);

	/**
	 * Returns a JSON Schema for the configuration required by a dispatch layer.
	 *
	 * @param uri
	 *            a URI that identifies a dispatch layer
	 * @return
	 * @return a JSON Schema for the configuration required by a dispatch layer
	 * @throws DispatchLayerNotFoundException
	 *             if a dispatch layer cannot be found for the specified URI
	 * @throws DispatchLayerConfigurationException
	 *             if the ConfigurationDefinition cannot be created
	 */
	public JsonNode getDispatchLayerConfigurationSchema(URI uri)
			throws DispatchLayerNotFoundException, DispatchLayerConfigurationException;

}
