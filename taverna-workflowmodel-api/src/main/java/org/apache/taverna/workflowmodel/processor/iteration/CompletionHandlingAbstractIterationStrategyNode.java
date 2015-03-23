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

package org.apache.taverna.workflowmodel.processor.iteration;

import static java.util.Collections.synchronizedMap;

import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.invocation.Completion;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.workflowmodel.processor.activity.Job;

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
 * @author David Withers
 */
@SuppressWarnings("serial")
public abstract class CompletionHandlingAbstractIterationStrategyNode extends
		AbstractIterationStrategyNode {
	/**
	 * Container class for the state of completion for a given process
	 * identifier
	 * 
	 * @author Tom Oinn
	 */
	protected final class CompletionState {
		protected CompletionState(int indexLength) {
			inputComplete = new boolean[indexLength];
			for (int i = 0; i < indexLength; i++)
				inputComplete[i] = false;
		}

		protected boolean[] inputComplete;
		protected boolean receivedCompletion = false;

		/**
		 * Return true iff all inputs have completed
		 */
		protected boolean isComplete() {
			for (boolean inputCompletion : inputComplete)
				if (!inputCompletion)
					return false;
			return true;
		}
	}

	private Map<String, CompletionState> ownerToCompletion = synchronizedMap(new HashMap<String, CompletionState>());

	@Override
	public final void receiveCompletion(int inputIndex, Completion completion) {
		innerReceiveCompletion(inputIndex, completion);
		if (completion.getIndex().length == 0)
			pingCompletionState(inputIndex, completion.getOwningProcess(),
					true, completion.getContext());
	}

	@Override
	public final void receiveJob(int inputIndex, Job newJob) {
		innerReceiveJob(inputIndex, newJob);
		if (newJob.getIndex().length == 0)
			pingCompletionState(inputIndex, newJob.getOwningProcess(), false,
					newJob.getContext());
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
		CompletionState cs = getCompletionState(owningProcess);
		cs.inputComplete[inputIndex] = true;
		if (isCompletion)
			cs.receivedCompletion = true;
		if (cs.isComplete()) {
			cleanUp(owningProcess);
			ownerToCompletion.remove(owningProcess);
			if (cs.receivedCompletion)
				pushCompletion(new Completion(owningProcess, new int[0],
						context));
		}
	}

	protected CompletionState getCompletionState(String owningProcess) {
		if (ownerToCompletion.containsKey(owningProcess))
			return ownerToCompletion.get(owningProcess);
		CompletionState cs = new CompletionState(getChildCount());
		ownerToCompletion.put(owningProcess, cs);
		return cs;
	}

	protected abstract void innerReceiveCompletion(int inputIndex,
			Completion completion);

	protected abstract void innerReceiveJob(int inputIndex, Job newJob);
}
