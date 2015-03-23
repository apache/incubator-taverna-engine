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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.reference.ContextualizedT2Reference;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.FilteringInputPort;
import org.apache.taverna.workflowmodel.WorkflowStructureException;

/**
 * Abstract superclass for filtering input ports, extend and implement the
 * pushXXX methods to configure behaviour
 * 
 * @author Tom Oinn
 */
public abstract class AbstractFilteringInputPort extends
		AbstractEventHandlingInputPort implements FilteringInputPort {
	protected AbstractFilteringInputPort(String name, int depth) {
		super(name, depth);
		this.filterDepth = depth;
	}

	@Override
	public int getFilterDepth() {
		return this.filterDepth;
	}

	private int filterDepth;

	@Override
	public void receiveEvent(WorkflowDataToken token) {
		receiveToken(token);
	}

	public void pushToken(WorkflowDataToken dt, String owningProcess,
			int desiredDepth) {
		if (dt.getData().getDepth() == desiredDepth)
			pushData(getName(), owningProcess, dt.getIndex(), dt.getData(), dt
					.getContext());
		else {
			ReferenceService rs = dt.getContext().getReferenceService();
			Iterator<ContextualizedT2Reference> children = rs.traverseFrom(dt
					.getData(), dt.getData().getDepth() - 1);

			while (children.hasNext()) {
				ContextualizedT2Reference ci = children.next();
				int[] newIndex = new int[dt.getIndex().length
						+ ci.getIndex().length];
				int i = 0;
				for (int indx : dt.getIndex())
					newIndex[i++] = indx;
				for (int indx : ci.getIndex())
					newIndex[i++] = indx;
				pushToken(new WorkflowDataToken(owningProcess, newIndex, ci
						.getReference(), dt.getContext()), owningProcess,
						desiredDepth);
			}
			pushCompletion(getName(), owningProcess, dt.getIndex(), dt
					.getContext());
		}
	}

	public void receiveToken(WorkflowDataToken token) {
		String newOwner = transformOwningProcess(token.getOwningProcess());
		if (filterDepth == -1)
			throw new WorkflowStructureException(
					"Input depth filter not configured on input port, failing");

		int tokenDepth = token.getData().getDepth();
		if (tokenDepth == filterDepth) {
			if (filterDepth == getDepth())
				/*
				 * Pass event straight through, the filter depth is the same as
				 * the desired input port depth
				 */
				pushData(getName(), newOwner, token.getIndex(),
						token.getData(), token.getContext());
			else {
				pushToken(token, newOwner, getDepth());
				/*
				 * Shred the input identifier into the appropriate port depth
				 * and send the events through, pushing a completion event at
				 * the end.
				 */
			}
		} else if (tokenDepth > filterDepth) {
			// Convert to a completion event and push into the iteration strategy
			pushCompletion(getName(), newOwner, token.getIndex(), token
					.getContext());
		} else if (tokenDepth < filterDepth) {
			/*
			 * Normally we can ignore these, but there is a special case where
			 * token depth is less than filter depth and there is no index
			 * array. In this case we can't throw the token away as there will
			 * never be an enclosing one so we have to use the data manager to
			 * register a new single element collection and recurse.
			 */
			if (token.getIndex().length == 0) {
				T2Reference ref = token.getData();
				ReferenceService rs = token.getContext().getReferenceService();
				int currentDepth = tokenDepth;
				while (currentDepth < filterDepth) {
					// Wrap in a single item list
					List<T2Reference> newList = new ArrayList<>();
					newList.add(ref);
					ref = rs.getListService()
							.registerList(newList, token.getContext()).getId();
					currentDepth++;
				}
				pushData(getName(), newOwner, new int[0], ref,
						token.getContext());
			}
		}
	}

	public void setFilterDepth(int filterDepth) {
		this.filterDepth = filterDepth;
		if (filterDepth < getDepth())
			this.filterDepth = getDepth();
	}

	/**
	 * Action to take when the filter pushes a completion event out
	 * 
	 * @param portName
	 * @param owningProcess
	 * @param index
	 */
	protected abstract void pushCompletion(String portName,
			String owningProcess, int[] index, InvocationContext context);

	/**
	 * Action to take when a data event is created by the filter
	 * 
	 * @param portName
	 * @param owningProcess
	 * @param index
	 * @param data
	 */
	protected abstract void pushData(String portName, String owningProcess,
			int[] index, T2Reference data, InvocationContext context);

	/**
	 * Override this to transform owning process identifiers as they pass
	 * through the filter, by default this is the identity transformation
	 * 
	 * @param oldOwner
	 * @return
	 */
	protected String transformOwningProcess(String oldOwner) {
		return oldOwner;
	}
}
