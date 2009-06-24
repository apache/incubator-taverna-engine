/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.workflowmodel.impl;

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.invocation.Completion;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.WorkflowStructureException;
import net.sf.taverna.t2.workflowmodel.processor.activity.Job;

/**
 * AbstractCrystalizer bound to a specific ProcessorImpl
 * 
 * @author Tom Oinn
 * 
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

	public void completionCreated(Completion completion) {
		throw new WorkflowStructureException(
				"Should never see this if everything is working,"
						+ "if this occurs it is likely that the internal "
						+ "logic is broken, talk to Tom");
	}

	public void jobCreated(Job outputJob) {
		for (String outputPortName : outputJob.getData().keySet()) {
			WorkflowDataToken token = new WorkflowDataToken(outputJob
					.getOwningProcess(), outputJob.getIndex(), outputJob
					.getData().get(outputPortName), outputJob.getContext());
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
			throw new RuntimeException(
					"Processor ["+owningProcess+"] hasn't been configured, cannot emit empty job");
		// The wrapping depth is the length of index array that would be used if
		// a single item of the output port type were returned. We can examine
		// the index array for the node we're trying to create and use this to
		// work out how much we need to add to the output port depth to create
		// empty lists of the right type given the index array.
		int depth = wrappingDepth - index.length;
		// TODO - why was this incrementing?
		// depth++;

		ReferenceService rs = context.getReferenceService();
		Map<String, T2Reference> emptyJobMap = new HashMap<String, T2Reference>();
		for (OutputPort op : parent.getOutputPorts()) {
			emptyJobMap.put(op.getName(), rs.getListService()
					.registerEmptyList(depth + op.getDepth()).getId());
		}
		return new Job(owningProcess, index, emptyJobMap, context);
	}

}
