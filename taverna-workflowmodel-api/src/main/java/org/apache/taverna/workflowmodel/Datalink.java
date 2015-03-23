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

import org.apache.taverna.annotation.Annotated;

/**
 * A single point to point data link from an instance of
 * EventForwardingOutputPort to an instance of EventHandlingInputPort
 * 
 * @author Tom Oinn
 */
public interface Datalink extends Annotated<Datalink>, WorkflowItem {
	/**
	 * Get the sink for events flowing through this link
	 * 
	 * @return input port receiving events
	 */
	EventHandlingInputPort getSink();

	/**
	 * Get the source for events flowing through this link
	 * 
	 * @return output port generating events
	 */
	EventForwardingOutputPort getSource();

	/**
	 * Each datalink has a resolved depth, this being the constant sum of index
	 * array length + item depth for all tokens exchanged along this link. Where
	 * no iteration or data streaming is occuring this will evaluate to the
	 * output port depth the link is from (as is always the case with the
	 * internal output ports in dataflow inputs)
	 * 
	 * @return
	 */
	int getResolvedDepth();
}
