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
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.ProcessorOutputPort;

/**
 * Extension of AbstractOutputPort for use as the output port on a
 * ProcessorImpl. Contains additional logic to relay workflow data tokens from
 * the internal crystalizer to each in a set of target FilteringInputPort
 * instances.
 * 
 * @author Tom Oinn
 * @author Stuart Owen
 */
class ProcessorOutputPortImpl extends BasicEventForwardingOutputPort implements
		ProcessorOutputPort {
	private ProcessorImpl parent;

	protected ProcessorOutputPortImpl(ProcessorImpl parent, String portName,
			int portDepth, int granularDepth) {
		super(portName, portDepth, granularDepth);
		this.parent = parent;
	}

	/**
	 * Strip off the last id in the owning process stack (as this will have been
	 * pushed onto the stack on entry to the processor) and relay the event to
	 * the targets.
	 */
	protected void receiveEvent(WorkflowDataToken token) {
		sendEvent(token.popOwningProcess());
	}

	@Override
	public Processor getProcessor() {
		return this.parent;
	}
}
