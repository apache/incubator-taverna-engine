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

import java.util.Set;

/**
 * An extension of OutputPort defining a set of target EventReceivingInputPorts
 * to which internally generated events will be relayed. This is the interface
 * used by output ports on a workflow entity with internal logic generating or
 * relaying events.
 * 
 * @author Tom Oinn
 */
public interface EventForwardingOutputPort extends OutputPort {
	/**
	 * The set of EventHandlingInputPort objects which act as targets for events
	 * produced from this OutputPort
	 * 
	
	public Set<EventHandlingInputPort> getTargets();
*/ //FIXME What is happening here???

	/**
	 * The set of datalinks for which this output port is the source of events
	 */
	Set<? extends Datalink> getOutgoingLinks();
}
