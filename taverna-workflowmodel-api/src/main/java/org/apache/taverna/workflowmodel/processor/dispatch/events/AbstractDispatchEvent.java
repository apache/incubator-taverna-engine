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

package org.apache.taverna.workflowmodel.processor.dispatch.events;

import org.apache.taverna.invocation.Event;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType;

/**
 * Superclass of events within the dispatch stack
 * 
 * @author Tom Oinn
 */
public abstract class AbstractDispatchEvent<EventType extends AbstractDispatchEvent<EventType>>
		extends Event<EventType> {
	protected AbstractDispatchEvent(String owner, int[] index,
			InvocationContext context) {
		super(owner, index, context);
	}

	/**
	 * Return the DispatchMessageType for this event object
	 * 
	 * @return instance of DispatchMessageType represented by this event
	 */
	public abstract DispatchMessageType getMessageType();
}
