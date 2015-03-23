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

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Wraps a typed tree model up in a standard tree model for use with JTree and
 * friends.
 * 
 * @author Tom Oinn
 */
public final class TreeModelAdapter {
	private static class TypedListenerPair<TT> {
		TypedTreeModelListener<TT> typedListener;

		TreeModelListener untypedListener;

		TypedListenerPair(TypedTreeModelListener<TT> typedListener,
				TreeModelListener untypedListener) {
			this.typedListener = typedListener;
			this.untypedListener = untypedListener;
		}
	}

	private static Set<TypedListenerPair<Object>> mapping = new HashSet<>();

	private static TreeModelAdapter instance = null;

	private TreeModelAdapter() {
		//
	}

	private synchronized static TreeModelAdapter getInstance() {
		if (instance == null)
			instance = new TreeModelAdapter();
		return instance;
	}

	/**
	 * Return an untyped TreeModel wrapper around the specified TypedTreeModel
	 * 
	 * @param <NodeType>
	 *            the node type of the typed model being wrapped
	 * @param typedModel
	 *            typed model to wrap
	 * @return a TreeModel acting as an untyped view of the typed tree model
	 */
	public static <NodeType extends Object> TreeModel untypedView(
			TypedTreeModel<NodeType> typedModel) {
		return getInstance().removeType(typedModel);
	}

	private <NodeType extends Object> TreeModel removeType(
			final TypedTreeModel<NodeType> typedModel) {
		return new TreeModel() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void addTreeModelListener(final TreeModelListener listener) {
				TypedTreeModelListener<NodeType> typedListener = new TypedTreeModelListener<NodeType>() {
					@Override
					public void treeNodesChanged(TypedTreeModelEvent<NodeType> e) {
						listener.treeNodesChanged(unwrapType(e));
					}

					@Override
					public void treeNodesInserted(
							TypedTreeModelEvent<NodeType> e) {
						listener.treeNodesInserted(unwrapType(e));
					}

					@Override
					public void treeNodesRemoved(TypedTreeModelEvent<NodeType> e) {
						listener.treeNodesRemoved(unwrapType(e));
					}

					@Override
					public void treeStructureChanged(
							TypedTreeModelEvent<NodeType> e) {
						listener.treeStructureChanged(unwrapType(e));
					}

					private TreeModelEvent unwrapType(
							final TypedTreeModelEvent<NodeType> e) {
						return new TreeModelEvent(e.getSource(),
								e.getTreePath(), e.getChildIndices(),
								e.getChildren());
					}
				};
				synchronized (mapping) {
					typedModel.addTreeModelListener(typedListener);
					mapping.add(new TypedListenerPair(typedListener, listener));
				}
			}

			@Override
			@SuppressWarnings("unchecked")
			public Object getChild(Object arg0, int arg1) {
				return typedModel.getChild((NodeType) arg0, arg1);
			}

			@Override
			@SuppressWarnings("unchecked")
			public int getChildCount(Object arg0) {
				return typedModel.getChildCount((NodeType) arg0);
			}

			@Override
			@SuppressWarnings("unchecked")
			public int getIndexOfChild(Object arg0, Object arg1) {
				return typedModel.getIndexOfChild((NodeType) arg0,
						(NodeType) arg1);
			}

			@Override
			public Object getRoot() {
				return typedModel.getRoot();
			}

			@Override
			@SuppressWarnings("unchecked")
			public boolean isLeaf(Object arg0) {
				return typedModel.isLeaf((NodeType) arg0);
			}

			@Override
			@SuppressWarnings("unchecked")
			public void removeTreeModelListener(TreeModelListener arg0) {
				synchronized (mapping) {
					TypedListenerPair<NodeType> toRemove = null;
					for (TypedListenerPair<Object> tpl : mapping)
						if (tpl.untypedListener == arg0) {
							toRemove = (TypedListenerPair<NodeType>) tpl;
							typedModel
									.removeTreeModelListener((TypedTreeModelListener<NodeType>) tpl.typedListener);
							break;
						}
					if (toRemove == null)
						return;
					mapping.remove(toRemove);
				}
			}

			@Override
			public void valueForPathChanged(TreePath arg0, Object arg1) {
				typedModel.valueForPathChanged(arg0, arg1);
			}
		};
	}
}
