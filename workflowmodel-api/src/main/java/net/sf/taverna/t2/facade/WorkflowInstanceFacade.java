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
package net.sf.taverna.t2.facade;

import java.util.WeakHashMap;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.TokenOrderException;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.monitor.MonitorNode;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.utility.TypedTreeModel;
import net.sf.taverna.t2.workflowmodel.ControlBoundary;
import net.sf.taverna.t2.workflowmodel.Dataflow;

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
 * 
 */
@ControlBoundary
public interface WorkflowInstanceFacade {

	/**
	 * A weak hash map of all workflow run IDs mapped against the corresponding WorkflowInstanceFacadeS.
	 * This is needed for activities with dependencies (such as beanshell and API consumer) to gain access
	 * to the current workflow via the WorkflowInstanceFacade.
	 */
	public static final WeakHashMap<String, WorkflowInstanceFacade> workflowRunFacades = new WeakHashMap<String, WorkflowInstanceFacade>();
	
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
	public void pushData(WorkflowDataToken token, String portName)
			throws TokenOrderException;

	/**
	 * Where a workflow has no inputs this method will cause it to start
	 * processing. Any processors within the workflow with no inputs are fired.
	 * 
	 * @throws IllegalStateException
	 *             if the workflow has already been fired or has had data pushed
	 *             to it.
	 */
	public void fire() throws IllegalStateException;

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
	public void addResultListener(ResultListener listener);

	/**
	 * Remove a previously registered result listener
	 * 
	 * @param listener
	 */
	public void removeResultListener(ResultListener listener);

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
	public void addFailureListener(FailureListener listener);

	/**
	 * Remove a previously registered failure listener
	 */
	public void removeFailureListener(FailureListener listener);

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
	public TypedTreeModel<MonitorNode> getStateModel();

	/**
	 * Return the dataflow this facade facades
	 */
	public Dataflow getDataflow();
	
	/**
     * Return the invocation context used by this facade
     */
	public InvocationContext getContext();
	
	/**
	 * Return a map of the data pushed on the named port
	 */
	public WeakHashMap<String, T2Reference> getPushedDataMap();

	
	/**
	 * Get the unique id of the wf run inside the facede.
	 */
	public String getWorkflowRunId();
}
