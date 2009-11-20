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

import javax.swing.tree.TreePath;

/**
 * A replacement for TreeModel where nodes are typed rather than being generic
 * objects. Because of the way interfaces and generics work this can't be
 * related in any inheritance heirarchy to the actual javax.swing.TreeModel
 * interface but is very similar (okay, identical) in operation. For cases where
 * you want to use a normal TreeModel such as using this as the backing data
 * model for a JTree you can use the TreeModelAdapter class to create an untyped
 * view over the typed version defined here.
 * 
 * @author Tom Oinn
 * 
 * @see javax.swing.tree.TreeModel
 * 
 * @param <NodeType>
 *            Each node in the tree is of this type
 */
public interface TypedTreeModel<NodeType> {

	/**
	 * Adds a listener for the TreeModelEvent posted after the tree changes.
	 */
	void addTreeModelListener(TypedTreeModelListener<NodeType> l);

	/**
	 * Returns the child of parent at index 'index' in the parent's child array.
	 * 
	 * @param parent
	 *            parent instance of typed node type
	 * @param index
	 *            index within parent
	 * @return child node at the specified index
	 */
	NodeType getChild(NodeType parent, int index);

	/**
	 * Returns the number of children of parent.
	 * 
	 * @param parent
	 *            node to count children for
	 * @return number of children
	 */
	int getChildCount(NodeType parent);

	/**
	 * Returns the index of child in parent.
	 * 
	 * @param parent
	 *            a node in the tree, obtained from this data source
	 * @param child
	 *            the node we are interested in
	 * @return the index of the child in the parent, or -1 if either child or
	 *         parent are null
	 */
	int getIndexOfChild(NodeType parent, NodeType child);

	/**
	 * Returns the root of the tree. Returns null only if the tree has no nodes.
	 * 
	 * @return the root of the tree
	 */
	NodeType getRoot();

	/**
	 * Returns true if node is a leaf. It is possible for this method to return
	 * false even if node has no children. A directory in a filesystem, for
	 * example, may contain no files; the node representing the directory is not
	 * a leaf, but it also has no children.
	 * 
	 * @param node
	 *            a node in the tree, obtained from this data source
	 * @return true if node is a leaf
	 */
	boolean isLeaf(NodeType node);

	/**
	 * Removes a listener previously added with addTreeModelListener.
	 * 
	 * @param l
	 *            typed tree model listener to remove
	 */
	void removeTreeModelListener(TypedTreeModelListener<NodeType> l);

	/**
	 * Messaged when the user has altered the value for the item identified by
	 * path to newValue. If newValue signifies a truly new value the model
	 * should post a treeNodesChanged event.
	 * 
	 * @param path
	 *            path to the node that the user has altered
	 * @param newValue
	 *            the new value from the TreeCellEditor
	 */
	void valueForPathChanged(TreePath path, Object newValue);

}
