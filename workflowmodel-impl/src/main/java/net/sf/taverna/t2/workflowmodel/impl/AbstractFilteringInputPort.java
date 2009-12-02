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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.reference.ContextualizedT2Reference;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.FilteringInputPort;
import net.sf.taverna.t2.workflowmodel.WorkflowStructureException;

/**
 * Abstract superclass for filtering input ports, extend and implement the
 * pushXXX methods to configure behaviour
 * 
 * @author Tom Oinn
 * 
 */
public abstract class AbstractFilteringInputPort extends
		AbstractEventHandlingInputPort implements FilteringInputPort {

	protected AbstractFilteringInputPort(String name, int depth) {
		super(name, depth);
		this.filterDepth = depth;
	}

	public int getFilterDepth() {
		return this.filterDepth;
	}

	private int filterDepth;

	public void receiveEvent(WorkflowDataToken token) {
		receiveToken(token);
	}

	public void pushToken(WorkflowDataToken dt, String owningProcess,
			int desiredDepth) {
		if (dt.getData().getDepth() == desiredDepth) {
			pushData(getName(), owningProcess, dt.getIndex(), dt.getData(), dt
					.getContext());
		} else {

			ReferenceService rs = dt.getContext().getReferenceService();

			Iterator<ContextualizedT2Reference> children = rs.traverseFrom(dt
					.getData(), dt.getData().getDepth() - 1);

			while (children.hasNext()) {
				ContextualizedT2Reference ci = children.next();
				int[] newIndex = new int[dt.getIndex().length
						+ ci.getIndex().length];
				int i = 0;
				for (int indx : dt.getIndex()) {
					newIndex[i++] = indx;
				}
				for (int indx : ci.getIndex()) {
					newIndex[i++] = indx;
				}
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
		if (filterDepth == -1) {
			throw new WorkflowStructureException(
					"Input depth filter not configured on input port, failing");
		} else {
			int tokenDepth = token.getData().getDepth();
			if (tokenDepth == filterDepth) {
				if (filterDepth == getDepth()) {
					// Pass event straight through, the filter depth is the same
					// as the desired input port depth
					pushData(getName(), newOwner, token.getIndex(), token
							.getData(), token.getContext());
				} else {
					pushToken(token, newOwner, getDepth());
					/**
					 * // Shred the input identifier into the appropriate port //
					 * depth and send the events through, pushing a //
					 * completion event at the end. DataManager dManager =
					 * ContextManager .getDataManager(newOwner); Iterator<ContextualizedIdentifier>
					 * children = dManager .traverse(token.getData(),
					 * getDepth()); while (children.hasNext()) {
					 * ContextualizedIdentifier ci = children.next(); int[]
					 * newIndex = new int[token.getIndex().length +
					 * ci.getIndex().length]; int i = 0; for (int indx :
					 * token.getIndex()) { newIndex[i++] = indx; } for (int indx :
					 * ci.getIndex()) { newIndex[i++] = indx; }
					 * pushData(getName(), newOwner, newIndex, ci.getDataRef()); }
					 * pushCompletion(getName(), newOwner, token.getIndex());
					 */

				}
			} else if (tokenDepth > filterDepth) {
				// Convert to a completion event and push into the iteration
				// strategy
				pushCompletion(getName(), newOwner, token.getIndex(), token
						.getContext());
			} else if (tokenDepth < filterDepth) {
				// Normally we can ignore these, but there is a special case
				// where token depth is less than filter depth and there is no
				// index array. In this case we can't throw the token away as
				// there will never be an enclosing one so we have to use the
				// data manager to register a new single element collection and
				// recurse.
				if (token.getIndex().length == 0) {
					T2Reference ref = token.getData();
					ReferenceService rs = token.getContext()
							.getReferenceService();
					int currentDepth = tokenDepth;
					while (currentDepth < filterDepth) {
						// Wrap in a single item list
						List<T2Reference> newList = new ArrayList<T2Reference>();
						newList.add(ref);
						ref = rs.getListService().registerList(newList, token.getContext()).getId();
						currentDepth++;
					}
					pushData(getName(), newOwner, new int[0], ref, token
							.getContext());
				}
			}
		}
	}

	public void setFilterDepth(int filterDepth) {
		this.filterDepth = filterDepth;
		if (filterDepth < getDepth()) {
			this.filterDepth = getDepth();
		}
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
