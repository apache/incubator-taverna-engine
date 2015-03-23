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

package org.apache.taverna.facade;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.TokenOrderException;
import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.monitor.MonitorNode;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.utility.TypedTreeModel;
import org.apache.taverna.workflowmodel.ControlBoundary;
import org.apache.taverna.workflowmodel.Dataflow;

/**
 * The interaction point with a workflow instance. Technically there is no such
 * thing as a workflow instance in Taverna2, at least not in any real sense in
 * the code itself. The instance is more literally an identifier used as the
 * root of all data and error objects within this workflow and by which the top
 * level DataFlow or similar object is identified in the state tree. The
 * implementation of this interface should hide this though, automatically
 * prepending the internally stored (and hidden) identifier to all data push
 * messages and providing a subtree of the state model rooted at the internal
 * ID.
 * <p>
 * TODO - we should probably have callbacks for failure states here, but that
 * would need a decent definition (and maybe even ontology of) what failure
 * means. It's less obvious in a data streaming world what a failure is. At the
 * moment the dispatch stack can potentially treat unhandled error messages as
 * failing the processor, how do we get this exception information back up to
 * the workflow level?
 * 
 * @author Tom Oinn
 * @author Alex Nenadic
 * @author Stian Soiland-Reyes
 * @author Alan R Williams
 */
@ControlBoundary
public interface WorkflowInstanceFacade {
	public static enum State {
		/**
		 * Workflow has not yet been started using
		 * {@link WorkflowInstanceFacade#fire()}
		 */
		prepared,
		/**
		 * Workflow is running (or have been resumed using
		 * {@link WorkflowInstanceFacade#fire()})
		 */
		running,
		/**
		 * Workflow has been paused using
		 * {@link WorkflowInstanceFacade#pauseWorkflowRun()}
		 */
		paused,
		/**
		 * Workflow has completed, all processors are finished and all data
		 * delivered to all output ports.
		 */
		completed,
		/**
		 * Workflow has been cancelled using
		 * {@link WorkflowInstanceFacade#cancelWorkflowRun()}
		 */
		cancelled;
	}

	/**
	 * A weak hash map of all workflow run IDs mapped against the corresponding
	 * WorkflowInstanceFacadeS. This is needed for activities with dependencies
	 * (such as beanshell and API consumer) to gain access to the current
	 * workflow via the WorkflowInstanceFacade.
	 */
	static final WeakHashMap<String, WeakReference<WorkflowInstanceFacade>> workflowRunFacades = new WeakHashMap<>();

	/**
	 * Push a data token into the specified port. If the token is part of a
	 * stream the index contains the index of this particular token. If not the
	 * index should be the empty integer array.
	 * 
	 * @param token
	 *            A WorkflowDataToken containing the data to be pushed to the
	 *            workflow along with its current owning process identifier and
	 *            index
	 * @param portName
	 *            Port name to use
	 * @throws TokenOrderException
	 *             if ordering constraints on the token stream to each input
	 *             port are violated
	 */
	void pushData(WorkflowDataToken token, String portName)
			throws TokenOrderException;

	/**
	 * Where a workflow has no inputs this method will cause it to start
	 * processing. Any processors within the workflow with no inputs are fired.
	 * 
	 * @throws IllegalStateException
	 *             if the workflow has already been fired or has had data pushed
	 *             to it.
	 */
	void fire() throws IllegalStateException;

	/**
	 * The result listener is used to handle data tokens produced by the
	 * workflow.
	 * <p>
	 * If the listener is registered after the workflow has already produced
	 * results it will be immediately called with any results previously
	 * produced. Where the workflow has completed a stream of results it may
	 * only message the listener with the highest level one, so for a case where
	 * a list of results is emited one at a time the listener may either get the
	 * individual items followed by the list token or if registered after the
	 * list token has been emited only receive the list token.
	 * 
	 * @param listener
	 */
	void addResultListener(ResultListener listener);

	/**
	 * Remove a previously registered result listener
	 * 
	 * @param listener
	 */
	void removeResultListener(ResultListener listener);

	/**
	 * A failure listener reports on overall workflow failure. It is not
	 * triggered by the failure of individual processors unless that processor
	 * is marked as critical. In fact in T2 all processors are marked as
	 * critical by default as there are ways of handling errors within the data
	 * stream, if the processor actually fails something really bad has
	 * happened.
	 * <p>
	 * As with the result listener a failure listener registered after the
	 * workflow has already failed will be immediately called with the failure
	 * data.
	 */
	void addFacadeListener(FacadeListener listener);

	/**
	 * Remove a previously registered failure listener
	 */
	void removeFacadeListener(FacadeListener listener);

	/**
	 * Workflow state is available through a sub-tree of the monitor tree. For
	 * security reasons the full monitor tree is never accessible through this
	 * interface but the sub-tree rooted at the node representing this workflow
	 * instance is and can be used for both monitoring and steering functions.
	 * <p>
	 * Uses the standard TreeModel-like mechanisms for registering change events
	 * and can be plugged into a JTree for display purposes through the
	 * TreeModelAdapter class.
	 * 
	 * @return Typed version of TreeModel representing the state of this
	 *         workflow. Nodes in the tree are instances of MonitorNode
	 */
	TypedTreeModel<MonitorNode> getStateModel();

	/**
	 * Return the dataflow this facade facades
	 */
	Dataflow getDataflow();

	/**
	 * Return the invocation context used by this facade
	 */
	InvocationContext getContext();

	/**
	 * Return a map of the data pushed on the named port
	 */
	WeakHashMap<String, T2Reference> getPushedDataMap();

	/**
	 * Get the unique id of the wf run inside the facede.
	 */
	String getWorkflowRunId();

	/**
	 * Cancel the workflow run corresponding to this facade
	 * 
	 * @return true if the workflow run was successfully cancelled. Note that
	 *         this does not mean that all of the invocations associated with
	 *         the run have finished.
	 */
	boolean cancelWorkflowRun() throws IllegalStateException;

	/**
	 * Pause the workflow run corresponding to this facade
	 * 
	 * @return true if the workflow run was successfully paused.
	 */
	boolean pauseWorkflowRun() throws IllegalStateException;

	/**
	 * Resume the workflow run corresponding to this facade
	 * 
	 * @return true if the workflow run was successfully resumed
	 */
	boolean resumeWorkflowRun() throws IllegalStateException;

	/**
	 * Return the current workflow {@link State}.
	 * 
	 * @return The workflow state.
	 */
	State getState();

	/**
	 * An identifier that is unique to this facade.
	 * 
	 * @return a String representing a unique internal identifier.
	 */
	String getIdentifier();
}
