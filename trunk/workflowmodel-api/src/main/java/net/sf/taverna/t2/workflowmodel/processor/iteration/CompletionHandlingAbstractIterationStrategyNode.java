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
package net.sf.taverna.t2.workflowmodel.processor.iteration;

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.invocation.Completion;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.workflowmodel.processor.activity.Job;

/**
 * A superclass for all iteration strategy nodes which are required to propagate
 * final completion events formed from multiple inputs. This is all the 'real'
 * iteration strategy nodes (but not the internal ones within the iteration
 * strategy object itself or the named input port nodes). All events are passed
 * to delegates in subclasses after which the completion state is checked, the
 * logic is as follows :
 * <p>
 * If the event received is final, that is to say it has an index of zero, and
 * final events have been received on all other inputs and at least one final
 * completion has been received then emit a final completion, otherwise do
 * nothing.
 * <p>
 * This means that subclasses should not emit final completion events themselves
 * - these will be handled by this superclass and emiting them in the subclass
 * will lead to duplicatation.
 * 
 * @author Tom Oinn
 * 
 */
public abstract class CompletionHandlingAbstractIterationStrategyNode extends
		AbstractIterationStrategyNode {

	/**
	 * Container class for the state of completion for a given process
	 * identifier
	 * 
	 * @author Tom Oinn
	 * 
	 */
	private final class CompletionState {
		protected CompletionState(int indexLength) {
			inputComplete = new boolean[indexLength];
			for (int i = 0; i < indexLength; i++) {
				inputComplete[i] = false;
			}
		}

		protected boolean[] inputComplete;
		protected boolean receivedCompletion = false;

		/**
		 * Return true iff all inputs have completed
		 */
		protected boolean isComplete() {
			for (boolean inputCompletion : inputComplete) {
				if (!inputCompletion) {
					return false;
				}
			}
			return true;
		}
	}

	private Map<String, CompletionState> ownerToCompletion = new HashMap<String, CompletionState>();

	public final void receiveCompletion(int inputIndex, Completion completion) {
		innerReceiveCompletion(inputIndex, completion);
		if (completion.getIndex().length == 0) {
			pingCompletionState(inputIndex, completion.getOwningProcess(),
					true, completion.getContext());
		}
	}

	public final void receiveJob(int inputIndex, Job newJob) {
		innerReceiveJob(inputIndex, newJob);
		if (newJob.getIndex().length == 0) {
			pingCompletionState(inputIndex, newJob.getOwningProcess(), false,
					newJob.getContext());
		}
	}

	/**
	 * Called after a final completion event has been emited for a given owning
	 * process, should be used by subclasses to do any tidying required,
	 * removing state etc.
	 * 
	 * @param owningProcess
	 */
	protected abstract void cleanUp(String owningProcess);

	private void pingCompletionState(int inputIndex, String owningProcess,
			boolean isCompletion, InvocationContext context) {
		synchronized (ownerToCompletion) {
			CompletionState cs = getCompletionState(owningProcess);
			cs.inputComplete[inputIndex] = true;
			if (isCompletion) {
				cs.receivedCompletion = true;
			}
			if (cs.isComplete() && cs.receivedCompletion) {
				ownerToCompletion.remove(owningProcess);
				cleanUp(owningProcess);
				pushCompletion(new Completion(owningProcess, new int[0],
						context));
			}
		}
	}

	private CompletionState getCompletionState(String owningProcess) {
		synchronized (ownerToCompletion) {
			if (ownerToCompletion.containsKey(owningProcess)) {
				return ownerToCompletion.get(owningProcess);
			} else {
				CompletionState cs = new CompletionState(getChildCount());
				ownerToCompletion.put(owningProcess, cs);
				return cs;
			}
		}
	}

	protected abstract void innerReceiveCompletion(int inputIndex,
			Completion completion);

	protected abstract void innerReceiveJob(int inputIndex, Job newJob);

}
