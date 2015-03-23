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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.workflowmodel.AbstractOutputPort;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.EventForwardingOutputPort;

/**
 * Extension of AbstractOutputPort implementing EventForwardingOutputPort
 * 
 * @author Tom Oinn
 * 
 */
public class BasicEventForwardingOutputPort extends AbstractOutputPort
		implements EventForwardingOutputPort {
	protected Set<DatalinkImpl> outgoingLinks;

	/**
	 * Construct a new abstract output port with event forwarding capability
	 * 
	 * @param portName
	 * @param portDepth
	 * @param granularDepth
	 */
	public BasicEventForwardingOutputPort(String portName, int portDepth,
			int granularDepth) {
		super(portName, portDepth, granularDepth);
		this.outgoingLinks = new HashSet<>();
	}

	/**
	 * Implements EventForwardingOutputPort
	 */
	@Override
	public final Set<? extends Datalink> getOutgoingLinks() {
		return Collections.unmodifiableSet(this.outgoingLinks);
	}

	/**
	 * Forward the specified event to all targets
	 * 
	 * @param e
	 */
	public void sendEvent(WorkflowDataToken e) {
		for (Datalink link : outgoingLinks)
			link.getSink().receiveEvent(e);
	}

	protected void addOutgoingLink(DatalinkImpl link) {
		if (outgoingLinks.contains(link) == false)
			outgoingLinks.add(link);
	}

	protected void removeOutgoingLink(Datalink link) {
		outgoingLinks.remove(link);
	}

	protected void setDepth(int depth) {
		this.depth = depth;
	}
	
	protected void setGranularDepth(int granularDepth) {
		this.granularDepth = granularDepth;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
}
