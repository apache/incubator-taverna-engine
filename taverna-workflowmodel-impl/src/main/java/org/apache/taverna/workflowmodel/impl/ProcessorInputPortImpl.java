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

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.ProcessorInputPort;

/**
 * An implementation of the filtering input port interface used as an input for
 * a ProcessorImpl. If the filter level is undefined this input port will always
 * throw workflow structure exceptions when you push data into it. This port
 * must be linked to a crystalizer or something which offers the same
 * operational contract, it requires a full hierarchy of data tokens (i.e. if
 * you push something in with an index you must at some point subsequent to that
 * push at least a single list in with the empty index)
 * 
 * @author Tom Oinn
 */
class ProcessorInputPortImpl extends AbstractFilteringInputPort implements
		ProcessorInputPort {
	private ProcessorImpl parent;

	protected ProcessorInputPortImpl(ProcessorImpl parent, String name,
			int depth) {
		super(name, depth);
		this.parent = parent;
	}

	@Override
	public String transformOwningProcess(String oldOwner) {
		return oldOwner + ":" + parent.getLocalName();
	}

	@Override
	protected void pushCompletion(String portName, String owningProcess,
			int[] index, InvocationContext context) {
		parent.iterationStack.receiveCompletion(portName, owningProcess, index,
				context);
	}

	@Override
	protected void pushData(String portName, String owningProcess, int[] index,
			T2Reference data, InvocationContext context) {
		parent.iterationStack.receiveData(portName, owningProcess, index, data,
				context);
	}

	@Override
	public Processor getProcessor() {
		return this.parent;
	}
}
