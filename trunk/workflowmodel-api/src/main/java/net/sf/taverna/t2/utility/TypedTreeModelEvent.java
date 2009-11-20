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
 * Type aware version of TreeModelEvent
 * 
 * @author Tom Oinn
 * 
 * @see javax.swing.tree.TreeModelEvent
 * 
 * @param <NodeType>
 *            the node type parameter of the TypedTreeModel to which this event
 *            applies
 */
public class TypedTreeModelEvent<NodeType> {

	protected int[] childIndices;

	protected NodeType[] children;

	protected TreePath path;

	protected Object source;

	/**
	 * Used to create an event when the node structure has changed in some way,
	 * identifying the path to the root of a modified subtree as an array of
	 * Objects.
	 * 
	 * @param source
	 * @param path
	 */
	public TypedTreeModelEvent(Object source, NodeType[] path) {
		this.path = new TreePath(path);
		this.source = source;
		this.childIndices = new int[0];
		//this.children = NodeType[].class.cast(java.lang.reflect.Array
		//		.newInstance(NodeType[].class, 0));
	}

	/**
	 * Used to create an event when the node structure has changed in some way,
	 * identifying the path to the root of the modified subtree as a TreePath
	 * object.
	 * 
	 * @param source
	 * @param path
	 */
	public TypedTreeModelEvent(Object source, TreePath path) {
		this.path = path;
		this.source = source;
		this.childIndices = new int[0];
		//this.children = NodeType[].class.cast(java.lang.reflect.Array
		//		.newInstance(NodeType[].class, 0));
	}

	/**
	 * Used to create an event when nodes have been changed, inserted, or
	 * removed, identifying the path to the parent of the modified items as a
	 * TreePath object.
	 * 
	 * @param source
	 * @param path
	 * @param childIndices
	 * @param children
	 */
	public TypedTreeModelEvent(Object source, TreePath path,
			int[] childIndices, NodeType[] children) {
		this.source = source;
		this.path = path;
		this.childIndices = childIndices;
		this.children = children;
	}

	/**
	 * Used to create an event when nodes have been changed, inserted, or
	 * removed, identifying the path to the parent of the modified items as an
	 * array of Objects.
	 * 
	 * @param source
	 * @param path
	 * @param childIndices
	 * @param children
	 */
	public TypedTreeModelEvent(Object source, NodeType[] path,
			int[] childIndices, NodeType[] children) {
		this.path = new TreePath(path);
		this.source = source;
		this.childIndices = childIndices;
		this.children = children;
	}

	/**
	 * Returns the values of the child indexes.
	 * 
	 * @return
	 */
	public int[] getChildIndices() {
		return this.childIndices;
	}

	/**
	 * Returns the objects that are children of the node identified by getPath
	 * at the locations specified by getChildIndices.
	 * 
	 * @return
	 */
	public NodeType[] getChildren() {
		return this.children;
	}

	/**
	 * The object on which the Event initially occurred.
	 * 
	 * @return
	 */
	public Object getSource() {
		return this.source;
	}

	/**
	 * For all events, except treeStructureChanged, returns the parent of the
	 * changed nodes.
	 * 
	 * @return
	 */
	public TreePath getTreePath() {
		return path;
	}

	/**
	 * Returns a string that displays and identifies this object's properties.
	 */
	@Override
	public String toString() {
		return "Typed TreeModelEvent " + super.toString();
	}

}
