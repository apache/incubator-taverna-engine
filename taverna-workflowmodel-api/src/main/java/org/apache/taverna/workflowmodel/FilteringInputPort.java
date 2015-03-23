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

/**
 * A filtering input port is one capable of filtering events to only pass
 * through data events at a certain depth. Other events are either ignored (in
 * the case of finer granularity) or converted to completion events (for
 * coarser). Where the filter depth and the port depth are distinct this port
 * type will filter on the filter depth then drill into the data to get down to
 * the port depth. Filter depth must always be equal to or greater than port
 * depth.
 * <p>
 * This is used as the interface for Processor input ports.
 * <p>
 * A condition to use this type is that the stream of events for a given process
 * ID must terminate with a top level (i.e. zero length index array) token. This
 * can be accomplished by use of the crystalizer (as found on the output of a
 * Processor instance) or some other mechanism but is required. Similarly it is
 * assumed that all intermediate collections are emited in the correct sequence,
 * if this is not the case the filtering may not function correctly.
 * 
 * @author Tom Oinn
 */
public interface FilteringInputPort extends EventHandlingInputPort {
	/**
	 * Set the depth at which to filter events. Events at a lower depth than
	 * this are ignored completely, those at exactly this depth are passed
	 * through intact and those above are converted to completion events.
	 * 
	 * @param filterDepth
	 */
	int getFilterDepth();
}
