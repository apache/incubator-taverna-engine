/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.workflowmodel.processor.activity;

import java.net.URI;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Factory for creating {@link Activity} instances.
 * 
 * @author David Withers
 */
public interface ActivityFactory {
	/**
	 * Creates a new <code>Activity</code> instance.
	 * 
	 * @return the new <code>Activity</code> instance
	 */
	Activity<?> createActivity();

	/**
	 * What type of <code>Activity</code>s can this factory create?
	 * 
	 * @return the type of the <code>Activity</code>s that this factory can
	 *         create
	 */
	URI getActivityType();

	/**
	 * Returns the JSON Schema for the configuration required by the
	 * <code>Activity</code>.
	 * 
	 * @return the JSON Schema for the configuration required by the
	 *         <code>Activity</code>
	 */
	JsonNode getActivityConfigurationSchema();

	/**
	 * Returns the <code>ActivityInputPort</code>s that the
	 * <code>Activity</code> requires to be present in order to execute with the
	 * specified configuration.
	 * <p>
	 * If the <code>Activity</code> does not require any input port for the
	 * configuration then an empty set is returned.
	 * 
	 * @param configuration
	 *            the configuration
	 * @return the <code>ActivityInputPort</code>s that the
	 *         <code>Activity</code> requires to be present in order to execute
	 */
	Set<ActivityInputPort> getInputPorts(JsonNode configuration)
			throws ActivityConfigurationException;

	/**
	 * Returns the <code>ActivityOutputPort</code>s that the
	 * <code>Activity</code> requires to be present in order to execute with the
	 * specified configuration.
	 * <p>
	 * If the <code>Activity</code> does not require any output ports for the
	 * configuration then an empty set is returned.
	 * 
	 * @param configuration
	 *            the configuration
	 * @return the <code>ActivityOutputPort</code>s that the
	 *         <code>Activity</code> requires to be present in order to execute
	 */
	Set<ActivityOutputPort> getOutputPorts(JsonNode configuration)
			throws ActivityConfigurationException;
}
