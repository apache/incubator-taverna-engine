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
package net.sf.taverna.t2.utility;

/**
 * Equivalent to TreeModelListener for use with the TypedTreeModel
 * 
 * @author Tom
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
	 * Use e.getPath() to get the parent of the changed node(s).
	 * e.getChildIndices() returns the index(es) of the changed node(s).
	 */
	void treeNodesChanged(TypedTreeModelEvent<NodeType> e);

	/**
	 * Invoked after nodes have been inserted into the tree.
	 * <p>
	 * Use e.getPath() to get the parent of the new node(s). e.getChildIndices()
	 * returns the index(es) of the new node(s) in ascending order.
	 */
	void treeNodesInserted(TypedTreeModelEvent<NodeType> e);

	/**
	 * Invoked after nodes have been removed from the tree. Note that if a
	 * subtree is removed from the tree, this method may only be invoked once
	 * for the root of the removed subtree, not once for each individual set of
	 * siblings removed.
	 * <p>
	 * Use e.getPath() to get the former parent of the deleted node(s).
	 * e.getChildIndices() returns, in ascending order, the index(es) the
	 * node(s) had before being deleted.
	 */
	void treeNodesRemoved(TypedTreeModelEvent<NodeType> e);

	/**
	 * Invoked after the tree has drastically changed structure from a given
	 * node down. If the path returned by e.getPath() is of length one and the
	 * first element does not identify the current root node the first element
	 * should become the new root of the tree.
	 * <p>
	 * Use e.getPath() to get the path to the node. e.getChildIndices() returns
	 * null.
	 */
	void treeStructureChanged(TypedTreeModelEvent<NodeType> e);

}
