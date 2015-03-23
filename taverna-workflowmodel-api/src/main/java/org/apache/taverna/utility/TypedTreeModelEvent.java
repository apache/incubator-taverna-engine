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
