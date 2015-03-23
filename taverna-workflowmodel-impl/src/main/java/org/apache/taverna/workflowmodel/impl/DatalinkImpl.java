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

package org.apache.taverna.workflowmodel.impl;

import org.apache.taverna.annotation.AbstractAnnotatedThing;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.EventForwardingOutputPort;
import org.apache.taverna.workflowmodel.EventHandlingInputPort;

/**
 * Naive bean implementation of Datalink
 * 
 * @author Tom Oinn
 */
class DatalinkImpl extends AbstractAnnotatedThing<Datalink> implements Datalink {
	private EventForwardingOutputPort source;
	private EventHandlingInputPort sink;
	private transient int resolvedDepth = -1;

	@Override
	public int getResolvedDepth() {
		return this.resolvedDepth;
	}

	protected void setResolvedDepth(int newResolvedDepth) {
		this.resolvedDepth = newResolvedDepth;
	}

	protected DatalinkImpl(EventForwardingOutputPort source,
			EventHandlingInputPort sink) {
		this.source = source;
		this.sink = sink;
	}

	@Override
	public EventHandlingInputPort getSink() {
		return sink;
	}

	@Override
	public EventForwardingOutputPort getSource() {
		return source;
	}

	@Override
	public String toString() {
		return "link(" + resolvedDepth + ")" + source.getName() + ":"
				+ sink.getName();
	}
}
