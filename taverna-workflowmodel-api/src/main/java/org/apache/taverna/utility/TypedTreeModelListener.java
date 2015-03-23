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

package org.apache.taverna.utility;

/**
 * Equivalent to TreeModelListener for use with the TypedTreeModel
 * 
 * @author Tom Oinn
 * 
 * @see javax.swing.tree.TreeModelListener
 * 
 * @param <NodeType>
 *            Type of the node within the TypedTreeModel and used to
 *            parameterize the typed version of the tree model event.
 */
public interface TypedTreeModelListener<NodeType> {
	/**
	 * Invoked after a node (or a set of siblings) has changed in some way. The
	 * node(s) have not changed locations in the tree or altered their children
	 * arrays, but other attributes have changed and may affect presentation.
	 * Example: the name of a file has changed, but it is in the same location
	 * in the file system.
	 * <p>
	 * To indicate the root has changed, childIndices and children will be null.
	 * <p>
	 * Use <code>e.getPath()</code> to get the parent of the changed node(s).
	 * <code>e.getChildIndices()</code> returns the index(es) of the changed
	 * node(s).
	 */
	void treeNodesChanged(TypedTreeModelEvent<NodeType> e);

	/**
	 * Invoked after nodes have been inserted into the tree.
	 * <p>
	 * Use <code>e.getPath()</code> to get the parent of the new node(s).
	 * <code>e.getChildIndices()</code> returns the index(es) of the new node(s)
	 * in ascending order.
	 */
	void treeNodesInserted(TypedTreeModelEvent<NodeType> e);

	/**
	 * Invoked after nodes have been removed from the tree. Note that if a
	 * subtree is removed from the tree, this method may only be invoked once
	 * for the root of the removed subtree, not once for each individual set of
	 * siblings removed.
	 * <p>
	 * Use <code>e.getPath()</code> to get the former parent of the deleted
	 * node(s). <code>e.getChildIndices()</code> returns, in ascending order,
	 * the index(es) the node(s) had before being deleted.
	 */
	void treeNodesRemoved(TypedTreeModelEvent<NodeType> e);

	/**
	 * Invoked after the tree has drastically changed structure from a given
	 * node down. If the path returned by <code>e.getPath()</code> is of length
	 * one and the first element does not identify the current root node the
	 * first element should become the new root of the tree.
	 * <p>
	 * Use
	 * <code>e.getPath()<code> to get the path to the node. <code>e.getChildIndices()</code>
	 * returns <code>null</code>.
	 */
	void treeStructureChanged(TypedTreeModelEvent<NodeType> e);
}
