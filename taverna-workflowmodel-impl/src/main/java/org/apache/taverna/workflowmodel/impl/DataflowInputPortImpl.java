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

import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.EventForwardingOutputPort;

public class DataflowInputPortImpl extends AbstractEventHandlingInputPort
		implements DataflowInputPort {
	protected BasicEventForwardingOutputPort internalOutput;
	private int granularInputDepth;
	private Dataflow dataflow;

	DataflowInputPortImpl(String name, int depth, int granularDepth, Dataflow df) {
		super(name, depth);
		granularInputDepth = granularDepth;
		dataflow = df;
		internalOutput = new BasicEventForwardingOutputPort(name, depth,
				granularDepth);
	}

	@Override
	public int getGranularInputDepth() {
		return granularInputDepth;
	}

	void setDepth(int depth) {
		this.depth = depth;
		internalOutput.setDepth(depth);
	}
	
	void setGranularDepth(int granularDepth) {
		this.granularInputDepth = granularDepth;
		internalOutput.setGranularDepth(granularDepth);
	}
	
	@Override
	public EventForwardingOutputPort getInternalOutputPort() {
		return internalOutput;
	}

	/**
	 * Receive an input event, relay it through the internal output port to all
	 * connected entities
	 */
	@Override
	public void receiveEvent(WorkflowDataToken t) {
		WorkflowDataToken transformedToken = t.pushOwningProcess(dataflow.getLocalName());
		/*
		 * I'd rather avoid casting to the implementation but in this case we're
		 * in the same package - the only reason to do this is to allow dummy
		 * implementations of parts of this infrastructure during testing, in
		 * 'real' use this should always be a dataflowimpl
		 */
		if (dataflow instanceof DataflowImpl)
			((DataflowImpl) dataflow).tokenReceived(transformedToken
					.getOwningProcess(), t.getContext());
		for (Datalink dl : internalOutput.getOutgoingLinks())
			dl.getSink().receiveEvent(transformedToken);
	}

	@Override
	public Dataflow getDataflow() {
		return dataflow;
	}
	
	@Override
	public void setName(String newName) {
		this.name = newName;
		internalOutput.setName(newName);
	}	
}
