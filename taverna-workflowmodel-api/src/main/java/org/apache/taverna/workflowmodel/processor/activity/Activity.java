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

import static org.apache.taverna.annotation.HierarchyRole.CHILD;

import java.util.Map;
import java.util.Set;

import org.apache.taverna.annotation.Annotated;
import org.apache.taverna.annotation.HierarchyTraversal;
import org.apache.taverna.workflowmodel.Configurable;
import org.apache.taverna.workflowmodel.Edits;

/**
 * Defines a single abstract or concrete invokable activity. Each Processor
 * contains at least one of these and may contain many, similarly the dispatch
 * stack may create new Activity instances from e.g. dynamic lookup or
 * resolution of an abstract activity to a concrete activity or set of
 * activities.
 * 
 * @param <ConfigurationType>
 *            the ConfigurationType associated with the Activity. This is an
 *            arbitrary java class that provides details on how the Activity is
 *            configured..
 * @author Tom Oinn
 * @author David Withers
 */
public interface Activity<ConfigurationType> extends Annotated<Activity<?>>,
		Configurable<ConfigurationType> {
	/**
	 * An Activity contains a set of named input ports. Names must be unique
	 * within this set.
	 *
	 * @return the set of input ports for this activity
	 */
	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	Set<ActivityInputPort> getInputPorts();

	/**
	 * A processor may have different input port names to the activity or
	 * activities it contains. This map is keyed on the processor input port
	 * names with the corresponding value being the activity port name.
	 * 
	 * @return mapping from processor input port names to activity input port
	 *         names
	 */
	Map<String, String> getInputPortMapping();

	/**
	 * An Activity contains a set of named output ports. As with input ports
	 * names must be unique within the set.
	 * 
	 * @return
	 */
	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	Set<ActivityOutputPort> getOutputPorts();

	/**
	 * Outputs of the activity may be named differently to those of the
	 * processor. This map is keyed on an activity output port name with each
	 * corresponding value being the processor output port name to which the
	 * activity output is bound.
	 * 
	 * @return mapping from activity output port name to processor output port
	 *         name
	 */
	Map<String, String> getOutputPortMapping();

	@Override
	abstract void configure(ConfigurationType conf)
			throws ActivityConfigurationException;

	void setEdits(Edits edits);
}
