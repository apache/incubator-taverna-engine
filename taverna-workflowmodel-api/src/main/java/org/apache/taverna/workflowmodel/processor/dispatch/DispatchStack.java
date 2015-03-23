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

package org.apache.taverna.workflowmodel.processor.dispatch;

import static org.apache.taverna.annotation.HierarchyRole.CHILD;
import static org.apache.taverna.annotation.HierarchyRole.PARENT;

import java.util.List;

import org.apache.taverna.annotation.Annotated;
import org.apache.taverna.annotation.HierarchyTraversal;
import org.apache.taverna.monitor.MonitorableProperty;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.WorkflowItem;

/**
 * The dispatch stack is responsible for consuming a queue of jobs from the
 * iteration strategy and dispatching those jobs through a stack based control
 * flow to an appropriate invocation target. Conceptually the queue and
 * description of activities enter the stack at the top, travel down to an
 * invocation layer at the bottom from which results, errors and completion
 * events rise back up to the top layer. Dispatch stack layers are stored as an
 * ordered list with index 0 being the top of the stack.
 * 
 * @author Tom Oinn
 */
public interface DispatchStack extends Annotated<DispatchStack>, WorkflowItem {
	/**
	 * The DispatchStack consists of an ordered list of DispatchLayer instances
	 * where the DispatchLayer at index zero is at the bottom of the stack and
	 * is almost always an invocation layer of some kind (in any working
	 * dispatch stack configuration)
	 * 
	 */
	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	List<DispatchLayer<?>> getLayers();

	/**
	 * The dispatch stack is contained within a processor, this can be null if
	 * the stack is being used out of this context but layers may be relying on
	 * this link to get information about the processor input ports and their
	 * annotations for various reasons.
	 */
	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { PARENT })
	Processor getProcessor();

	/**
	 * Return the layer above (lower index!) the specified layer, or a reference
	 * to the internal top layer dispatch layer if there is no layer above the
	 * specified one. Remember - input data and activities go down, results,
	 * errors and completion events bubble back up the dispatch stack.
	 * <p>
	 * The top layer within the dispatch stack is always invisible and is held
	 * within the DispatchStackImpl object itself, being used to route data out
	 * of the entire stack
	 * 
	 * @param layer
	 * @return
	 */
	DispatchLayer<?> layerAbove(DispatchLayer<?> layer);

	/**
	 * Return the layer below (higher index) the specified layer, or null if
	 * there is no layer below this one.
	 * 
	 * @param layer
	 * @return
	 */
	DispatchLayer<?> layerBelow(DispatchLayer<?> layer);

	/**
	 * The dispatch stack acts as an aggregator for monitorable properties
	 * exposed by the dispatch layers. This is distinct from layers which are
	 * capable of rewriting the process idenfitier of tokens - these require
	 * their own nodes in the monitor in addition to any contributed properties.
	 * 
	 * @param prop
	 * @param processID
	 */
	void receiveMonitorableProperty(MonitorableProperty<?> prop,
			String processID);
}
