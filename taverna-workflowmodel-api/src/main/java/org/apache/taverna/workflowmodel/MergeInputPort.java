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

package org.apache.taverna.workflowmodel;

import org.apache.taverna.invocation.WorkflowDataToken;

/**
 * Input port on a Merge object
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 */
public interface MergeInputPort extends EventHandlingInputPort, MergePort {
	/**
	 * Receive an arbitrary workflow event. The index of this port relative to
	 * its parent Merge object is prepended to the event index and the event
	 * forwarded through the Merge output port to any targets.
	 * <p>
	 * If this is a workflow data token and the first such received under a
	 * given owning process ID the implementing method also must also store the
	 * cardinality, i.e. length of index array + depth of token. Subsequent
	 * events are matched to this, if they have unequal cardinality the parent
	 * Merge operation will throw a WorkflowStructureException as the merge
	 * would result in a collection which violated the constraints defined by
	 * the Taverna 2 data model.
	 * 
	 * @param e
	 *            arbitrary workflow event, will be forwarded unchanged other
	 *            than an alteration of the index array by prefixing the index
	 *            of this input port relative to the parent Merge object
	 */
	@Override
	public void receiveEvent(WorkflowDataToken t);
}
