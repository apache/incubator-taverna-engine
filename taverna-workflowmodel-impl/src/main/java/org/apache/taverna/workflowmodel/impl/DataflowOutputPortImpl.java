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

import static java.util.Collections.synchronizedList;

import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.facade.ResultListener;
import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.EventHandlingInputPort;

class DataflowOutputPortImpl extends BasicEventForwardingOutputPort
		implements DataflowOutputPort {
	protected InternalInputPort internalInput;
	/**
	 * Remember to synchronize access to this list
	 */
	protected List<ResultListener> resultListeners = synchronizedList(new ArrayList<ResultListener>());
	private Dataflow dataflow;

	DataflowOutputPortImpl(String portName, Dataflow dataflow) {
		super(portName, -1, -1);
		this.dataflow = dataflow;
		this.internalInput = new InternalInputPort(name, dataflow, portName);
	}

	@Override
	public EventHandlingInputPort getInternalInputPort() {
		return this.internalInput;
	}

	@Override
	public Dataflow getDataflow() {
		return this.dataflow;
	}

	void setDepths(int depth, int granularDepth) {
		this.depth = depth;
		this.granularDepth = granularDepth;
	}

	@Override
	public void addResultListener(ResultListener listener) {
		resultListeners.add(listener);		
	}

	@Override
	public void removeResultListener(ResultListener listener) {
		resultListeners.remove(listener);
	}
	
	@Override
	public void setName(String newName) {
		this.name = newName;
		internalInput.setName(newName);
	}

	/** This makes a thread-safe copy. */
	private List<ResultListener> getListeners() {
		synchronized (resultListeners) {
			return new ArrayList<>(resultListeners);
		}
	}

	private class InternalInputPort extends AbstractEventHandlingInputPort {
		InternalInputPort(String name, Dataflow dataflow, String portName) {
			super(name, -1);
		}

		/**
		 * Forward the event through the output port Also informs any
		 * ResultListeners on the output port to the new token.
		 */
		@Override
		public void receiveEvent(WorkflowDataToken token) {
			WorkflowDataToken newToken = token.popOwningProcess();
			sendEvent(newToken);
			for (ResultListener listener : getListeners())
				listener.resultTokenProduced(newToken, this.getName());
		}

		/**
		 * Always copy the value of the enclosing dataflow output port
		 */
		@Override
		public int getDepth() {
			return DataflowOutputPortImpl.this.getDepth();
		}
	}
}
