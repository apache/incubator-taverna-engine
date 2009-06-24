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

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.tree.MutableTreeNode;

import net.sf.taverna.t2.invocation.Completion;
import net.sf.taverna.t2.workflowmodel.processor.activity.Job;

/**
 * Interface for nodes within an iteration strategy layer
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 * 
 */
public interface IterationStrategyNode extends MutableTreeNode {

	/**
	 * Specialised return type of {@link TreeNode#children()}
	 */

	public Enumeration<IterationStrategyNode> children();

	/**
	 * Remove all children nodes and set the parent to <code>null</code>.
	 */
	public void clear();

	/**
	 * Specialised return type of {@link TreeNode#getChildAt(int)}
	 */
	public IterationStrategyNode getChildAt(int childIndex);

	/**
	 * Return a copy of the list of children nodes, or an empty list if
	 * {@link #getAllowsChildren()} is <code>false</code>.
	 * 
	 * @return List of children nodes.
	 */
	public List<IterationStrategyNode> getChildren();

	/**
	 * In the context of an enclosing iteration strategy each node should be
	 * able to return the iteration depth, i.e. the length of the index array,
	 * for items it will emit. In all cases other than leaf nodes this is
	 * defined in terms of the depth of child nodes. The input cardinalities for
	 * named ports are pushed through each node so that the terminal nodes
	 * corresponding to input port collators can evaluate this expression -
	 * pushing it through the entire evaluation means we don't have to keep
	 * state anywhere in the leaf nodes (standard dependency injection)
	 * <p>
	 * Nodes can choose to throw the IterationTypeMismatchException if their
	 * inputs aren't compatible with the operational semantics of the node such
	 * as in the case of a dot product node with inputs with different depths.
	 * 
	 * @param inputDepths
	 * @return
	 * @throws IterationTypeMismatchException
	 */
	public int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException;

	/**
	 * Specialised return type of {@link TreeNode#getParent()}
	 */
	public IterationStrategyNode getParent();

	/**
	 * Insert a new child node. The new child will be added in the end of the
	 * list, so this would be equivalent to insert(child, getChildCount()).
	 * 
	 * @param child
	 *            Child node to add
	 */
	public void insert(MutableTreeNode child);

	/**
	 * Nodes can also receive completion events, the simplest being one
	 * declaring that no further input is expected on the given input, or
	 * partial completion events which are interpreted as 'no event with an
	 * index array prefixed by the specified completion index array will be
	 * received on the specified index'
	 */
	public void receiveCompletion(int inputIndex, Completion completion);

	/**
	 * The nodes within the iteration strategy, a tree structure, are event
	 * based. When a new fragment of a job from upstream in the tree (towards
	 * leaves) arrives it is handled by this method. Implementations will
	 * probably have to handle state management, i.e. what jobs have we already
	 * seen, and emit appropriate jobs to downstream nodes.
	 * 
	 * @param inputIndex
	 * @param newJob
	 */
	public void receiveJob(int inputIndex, Job newJob);

}
