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
package net.sf.taverna.t2.workflowmodel.processor.activity;

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
	 * @return a new <code>Activity</code> instance
	 */
	public Activity<?> createActivity();

	/**
	 * Returns the type of the <code>Activity</code>s that this factory can create.
	 *
	 * @return the type of the <code>Activity</code>s that this factory can create
	 */
	public URI getActivityType();

	/**
	 * Returns the JSON Schema for the configuration required by the <code>Activity</code>.
	 *
	 * @return the JSON Schema for the configuration required by the <code>Activity</code>
	 */
	public JsonNode getActivityConfigurationSchema();

	/**
	 * Returns the <code>ActivityInputPort</code>s that the <code>Activity</code> requires to be
	 * present in order to execute with the specified configuration.
	 * <p>
	 * If the <code>Activity</code> does not require any input port for the configuration then an
	 * empty set is returned.
	 *
	 * @param configuration
	 *            the configuration
	 * @return the <code>ActivityInputPort</code>s that the <code>Activity</code> requires to be
	 *         present in order to execute
	 */
	public Set<ActivityInputPort> getInputPorts(JsonNode configuration) throws ActivityConfigurationException;

	/**
	 * Returns the <code>ActivityOutputPort</code>s that the <code>Activity</code> requires to be
	 * present in order to execute with the specified configuration.
	 * <p>
	 * If the <code>Activity</code> does not require any output ports for the configuration then an
	 * empty set is returned.
	 *
	 * @param configuration
	 *            the configuration
	 * @return the <code>ActivityOutputPort</code>s that the <code>Activity</code> requires to be
	 *         present in order to execute
	 */
	public Set<ActivityOutputPort> getOutputPorts(JsonNode configuration) throws ActivityConfigurationException;

}
