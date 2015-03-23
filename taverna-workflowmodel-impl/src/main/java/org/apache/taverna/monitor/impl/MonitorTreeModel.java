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

package org.apache.taverna.monitor.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;
import org.apache.taverna.monitor.MonitorNode;
import org.apache.taverna.monitor.MonitorableProperty;
import org.apache.taverna.monitor.NoSuchPropertyException;
import org.apache.taverna.monitor.MonitorManager.AddPropertiesMessage;
import org.apache.taverna.monitor.MonitorManager.DeregisterNodeMessage;
import org.apache.taverna.monitor.MonitorManager.MonitorMessage;
import org.apache.taverna.monitor.MonitorManager.RegisterNodeMessage;

import org.apache.log4j.Logger;

/**
 * A relatively naive Monitor interface which holds all
 * state in a tree model. Use getMonitor() to get the monitor singleton, all
 * workflows under a given JVM use the same instance in this implementation with
 * the root node of the monitor tree corresponding to the monitor itself.
 * <p>
 * Internally we use a default tree model with default mutable tree nodes where
 * the user object is set to instances of MonitorNode, with the exception of the
 * 'true' root of the tree in which it is set to the MonitorImpl itself
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 * 
 */
public class MonitorTreeModel implements Observer<MonitorMessage> {
	private static MonitorTreeModel instance = null;
	private static Logger logger = Logger.getLogger(MonitorTreeModel.class);
	
	/**
	 * Get the MonitorImpl singleton
	 * 
	 * @return The MonitorImpl singleton
	 */
	public synchronized static MonitorTreeModel getInstance() {
		// TODO Convert to a bean?
		if (instance == null)
			instance = new MonitorTreeModel();
		return instance;
	}

	private long nodeRemovalDelay = 1000;
	private DefaultTreeModel monitorTree;
	private Timer nodeRemovalTimer;

	/**
	 * Protected constructor, use singleton access {@link #getInstance()}
	 * instead.
	 * 
	 */
	protected MonitorTreeModel() {
		monitorTree = new DefaultTreeModel(new DefaultMutableTreeNode(this));
		// Create the node removal timer as a daemon thread
		nodeRemovalTimer = new Timer(true);
	}

	/**
	 * Returns a tree view over the monitor.
	 * 
	 * @return a tree view over the monitor
	 */
	public JTree getJTree() {
		return new AlwaysOpenJTree(monitorTree);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notify(Observable<MonitorMessage> sender, MonitorMessage message)
			throws Exception {
		if (message instanceof RegisterNodeMessage) {
			RegisterNodeMessage regMessage = (RegisterNodeMessage) message;
			registerNode(regMessage.getWorkflowObject(), regMessage
					.getOwningProcess(), regMessage.getProperties());
		} else if (message instanceof DeregisterNodeMessage) {
			deregisterNode(message.getOwningProcess());
		} else if (message instanceof AddPropertiesMessage) {
			AddPropertiesMessage addMessage = (AddPropertiesMessage) message;
			addPropertiesToNode(addMessage.getOwningProcess(), addMessage
					.getNewProperties());
		} else {
			logger.warn("Unknown message " + message + " from " + sender);
		}
	}

	/**
	 * Nodes will be removed at least delayTime milliseconds after their initial
	 * deregistration request, this allows UI components to show nodes which
	 * would otherwise vanish almost instantaneously.
	 * 
	 * @param delayTime
	 *            time in milliseconds between the deregistration request and
	 *            attempt to actually remove the node in question
	 */
	public void setNodeRemovalDelay(long delayTime) {
		nodeRemovalDelay = delayTime;
	}

	/**
	 * Very simple UI!
	 */
	public void showMonitorFrame() {
		final JTree tree = new AlwaysOpenJTree(monitorTree);
		final JScrollPane jsp = new JScrollPane(tree);
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(jsp);
		frame.pack();
		frame.setVisible(true);
		new javax.swing.Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				jsp.repaint();
			}
		}).start();
	}

	/**
	 * Return the node pointed to by the first 'limit' number of elements in the
	 * owning process string array. If limit is -1 then use owningProcess.length
	 * 
	 * @param owningProcess
	 * @param limit
	 * @return
	 */
	protected DefaultMutableTreeNode nodeAtProcessPath(String[] owningProcess,
			int limit) throws IndexOutOfBoundsException {
		if (limit == -1)
			limit = owningProcess.length;
		DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) monitorTree
				.getRoot();
		for (int index = 0; index < limit && index < owningProcess.length; index++) {
			boolean found = false;
			for (int childIndex = 0; childIndex < monitorTree
					.getChildCount(currentNode)
					&& !found; childIndex++) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) monitorTree
						.getChild(currentNode, childIndex);
				MonitorNode childMonitorNode = (MonitorNode) childNode
						.getUserObject();
				if (childMonitorNode.getOwningProcess()[index]
						.equals(owningProcess[index])) {
					currentNode = childNode;
					found = true;
					// break;
				}
			}
			if (!found)
				throw new IndexOutOfBoundsException(
						"Cannot locate node with process ID "
								+ printProcess(owningProcess));
		}
		return currentNode;
	}

	protected String printProcess(String[] process) {
		StringBuffer sb = new StringBuffer();
		for (String part : process)
			sb.append("{").append(part).append("}");
		return sb.toString();
	}

	/**
	 * Inject properties into an existing node
	 */
	protected void addPropertiesToNode(String[] owningProcess,
			Set<MonitorableProperty<?>> newProperties) {
		try {
			DefaultMutableTreeNode node = nodeAtProcessPath(owningProcess, -1);
			MonitorNode mn = (MonitorNode) node.getUserObject();
			for (MonitorableProperty<?> prop : newProperties)
				mn.addMonitorableProperty(prop);
		} catch (IndexOutOfBoundsException ioobe) {
			// Fail silently here, the node wasn't found in the state tree
			logger.warn("Could not add properties to unknown node "
					+ printProcess(owningProcess));
		}
	}

	/**
	 * Request the removal of the specified node from the monitor tree. In this
	 * particular case the removal task will be added to a timer and executed at
	 * some (slightly) later time as determined by the removalDelay property.
	 */
	protected void deregisterNode(String[] owningProcess) {
		// logger.debug("Remove node @" + printProcess(owningProcess));
		final DefaultMutableTreeNode nodeToRemove = nodeAtProcessPath(
				owningProcess, -1);
		((MonitorNodeImpl) nodeToRemove.getUserObject()).expire();
		nodeRemovalTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				synchronized (monitorTree) {
					monitorTree.removeNodeFromParent(nodeToRemove);
				}
			}
		}, getNodeRemovalDelay());
	}

	/**
	 * Create a new node in the monitor
	 */
	protected void registerNode(final Object workflowObject,
			final String[] owningProcess,
			final Set<MonitorableProperty<?>> properties) {
		// logger.debug("Registering node " + printProcess(owningProcess));
	
		// Create a new MonitorNode
		final DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				new MonitorNodeImpl(workflowObject, owningProcess, properties));
		synchronized (monitorTree) {
			final MutableTreeNode parentNode = nodeAtProcessPath(owningProcess,
					owningProcess.length - 1);
			monitorTree.insertNodeInto(newNode, parentNode, monitorTree
					.getChildCount(parentNode));
		}
	}

	class AlwaysOpenJTree extends JTree {
		private static final long serialVersionUID = -3769998854485605447L;

		public AlwaysOpenJTree(TreeModel newModel) {
			super(newModel);
			setRowHeight(18);
			setLargeModel(true);
			setEditable(false);
			setExpandsSelectedPaths(false);
			setDragEnabled(false);
			setScrollsOnExpand(false);
			setSelectionModel(EmptySelectionModel.sharedInstance());
			setCellRenderer(new CellRenderer());
		}

		@Override
		public void setModel(TreeModel model) {
			if (treeModel == model)
				return;
			if (treeModelListener == null)
				treeModelListener = new TreeModelListener();
			if (model != null) {
				model.addTreeModelListener(treeModelListener);
			}
			TreeModel oldValue = treeModel;
			treeModel = model;
			firePropertyChange(TREE_MODEL_PROPERTY, oldValue, model);
		}

		protected class CellRenderer extends DefaultTreeCellRenderer {
			private static final long serialVersionUID = 7106767124654545039L;

			@Override
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel,
						expanded, leaf, row, hasFocus);
				if (value instanceof DefaultMutableTreeNode) {
					Object o = ((DefaultMutableTreeNode) value)
							.getUserObject();
					if (o instanceof MonitorNode)
						if (((MonitorNode) o).hasExpired())
							setEnabled(false);
				}
				return this;
			}
		}

		protected class TreeModelListener extends TreeModelHandler {
			@Override
			public void treeNodesInserted(final TreeModelEvent ev) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						TreePath path = ev.getTreePath();
						setExpandedState(path, true);
						fireTreeExpanded(path);
					}
				});
			}
			@Override
			public void treeStructureChanged(final TreeModelEvent ev) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						TreePath path = ev.getTreePath();
						setExpandedState(path, true);
						fireTreeExpanded(path);
					}
				});
			}
		}
	}

	class MonitorNodeImpl implements MonitorNode {
		private boolean expired = false;
		private String[] owningProcess;
		private Set<MonitorableProperty<?>> properties;
		private Object workflowObject;
		Date creationDate = new Date();

		MonitorNodeImpl(Object workflowObject, String[] owningProcess,
				Set<MonitorableProperty<?>> properties) {
			this.properties = properties;
			this.workflowObject = workflowObject;
			this.owningProcess = owningProcess;
		}

		@Override
		public void addMonitorableProperty(MonitorableProperty<?> newProperty) {
			properties.add(newProperty);
		}

		public void expire() {
			expired = true;
		}

		@Override
		public Date getCreationDate() {
			return creationDate;
		}

		@Override
		public String[] getOwningProcess() {
			return owningProcess;
		}

		/**
		 * Return an unmodifiable copy of the property set
		 */
		@Override
		public Set<? extends MonitorableProperty<?>> getProperties() {
			return Collections.unmodifiableSet(properties);
		}

		@Override
		public Object getWorkflowObject() {
			return workflowObject;
		}

		@Override
		public boolean hasExpired() {
			return this.expired;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(getWorkflowObject().getClass().getSimpleName());
			sb.append(", ");
			sb.append(owningProcess[owningProcess.length - 1]);
			sb.append(" : ");
			for (MonitorableProperty<?> prop : getProperties()) {
				String separator = "";
				for (String nameElement : prop.getName()) {
					sb.append(separator).append(nameElement);
					separator = ".";
				}
				sb.append("=");
				try {
					sb.append(prop.getValue().toString());
				} catch (NoSuchPropertyException nspe) {
					sb.append("EXPIRED");
				}
				sb.append(" ");
			}
			return sb.toString();
		}
	}

	public long getNodeRemovalDelay() {
		return nodeRemovalDelay;
	}

	protected DefaultTreeModel getMonitorTree() {
		return monitorTree;
	}
}
