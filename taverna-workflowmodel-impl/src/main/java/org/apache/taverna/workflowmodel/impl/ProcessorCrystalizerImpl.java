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

import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.invocation.Completion;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.OutputPort;
import org.apache.taverna.workflowmodel.WorkflowStructureException;
import org.apache.taverna.workflowmodel.processor.activity.Job;

/**
 * AbstractCrystalizer bound to a specific ProcessorImpl
 * 
 * @author Tom Oinn
 */
public class ProcessorCrystalizerImpl extends AbstractCrystalizer {
	private ProcessorImpl parent;

	/**
	 * Create and bind to the specified ProcessorImpl
	 * 
	 * @param parent
	 */
	protected ProcessorCrystalizerImpl(ProcessorImpl parent) {
		this.parent = parent;
	}

	@Override
	public void completionCreated(Completion completion) {
		throw new WorkflowStructureException(
				"Should never see this if everything is working,"
						+ "if this occurs it is likely that the internal "
						+ "logic is broken, talk to Tom");
	}

	@Override
	public void jobCreated(Job outputJob) {
		for (String outputPortName : outputJob.getData().keySet()) {
			WorkflowDataToken token = new WorkflowDataToken(
					outputJob.getOwningProcess(), outputJob.getIndex(),
					outputJob.getData().get(outputPortName),
					outputJob.getContext());
			parent.getOutputPortWithName(outputPortName).receiveEvent(token);
		}
	}

	@Override
	/**
	 * Used to construct a Job of empty lists at the appropriate depth in the
	 * event of a completion hitting the crystalizer before it sees a child
	 * node, i.e. the result of iterating over an empty collection structure of
	 * some kind.
	 */
	public Job getEmptyJob(String owningProcess, int[] index,
			InvocationContext context) {
		int wrappingDepth = parent.resultWrappingDepth;
		if (wrappingDepth < 0)
			throw new RuntimeException("Processor [" + owningProcess
					+ "] hasn't been configured, cannot emit empty job");
		/*
		 * The wrapping depth is the length of index array that would be used if
		 * a single item of the output port type were returned. We can examine
		 * the index array for the node we're trying to create and use this to
		 * work out how much we need to add to the output port depth to create
		 * empty lists of the right type given the index array.
		 */
		int depth = wrappingDepth - index.length;
		// TODO - why was this incrementing?
		// depth++;

		ReferenceService rs = context.getReferenceService();
		Map<String, T2Reference> emptyJobMap = new HashMap<>();
		for (OutputPort op : parent.getOutputPorts())
			emptyJobMap.put(op.getName(), rs.getListService()
					.registerEmptyList(depth + op.getDepth(), context).getId());
		return new Job(owningProcess, index, emptyJobMap, context);
	}

}
