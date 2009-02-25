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
package net.sf.taverna.t2.monitor.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

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

import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.monitor.MonitorNode;
import net.sf.taverna.t2.monitor.MonitorableProperty;
import net.sf.taverna.t2.monitor.NoSuchPropertyException;
import net.sf.taverna.t2.monitor.MonitorManager.AddPropertiesMessage;
import net.sf.taverna.t2.monitor.MonitorManager.DeregisterNodeMessage;
import net.sf.taverna.t2.monitor.MonitorManager.MonitorMessage;
import net.sf.taverna.t2.monitor.MonitorManager.RegisterNodeMessage;

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
		if (instance == null) {
			instance = new MonitorTreeModel();
		}
		return instance;
	}

	private long nodeRemovalDelay = 1000;

	private DefaultTreeModel monitorTree;

	private java.util.Timer nodeRemovalTimer;

	/**
	 * Protected constructor, use singleton access {@link #getInstance()}
	 * instead.
	 * 
	 */
	protected MonitorTreeModel() {
		monitorTree = new DefaultTreeModel(new DefaultMutableTreeNode(this));
		// Create the node removal timer as a daemon thread
		nodeRemovalTimer = new java.util.Timer(true);
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
		if (limit == -1) {
			limit = owningProcess.length;
		}
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
			if (!found) {
				throw new IndexOutOfBoundsException(
						"Cannot locate node with process ID "
								+ printProcess(owningProcess));
			}
		}
		return currentNode;
	}

	protected String printProcess(String[] process) {
		StringBuffer sb = new StringBuffer();
		for (String part : process) {
			sb.append("{" + part + "}");
		}
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
			for (MonitorableProperty<?> prop : newProperties) {
				mn.addMonitorableProperty(prop);
			}
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
		// logger.debug("Remove node @" +
		// printProcess(owningProcess));
		final DefaultMutableTreeNode nodeToRemove = nodeAtProcessPath(
				owningProcess, -1);
		((MonitorNodeImpl) nodeToRemove.getUserObject()).expire();
		nodeRemovalTimer.schedule(new java.util.TimerTask() {
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
					if (o instanceof MonitorNode) {
						MonitorNode mn = (MonitorNode) o;
						if (mn.hasExpired()) {
							setEnabled(false);
						}
					}
				}
				return this;
			}
		}

		protected class TreeModelListener extends TreeModelHandler {
			@Override
			public void treeNodesInserted(final TreeModelEvent ev) {
				SwingUtilities.invokeLater(new Runnable() {
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

		public void addMonitorableProperty(MonitorableProperty<?> newProperty) {
			properties.add(newProperty);
		}

		public void expire() {
			expired = true;
		}

		public Date getCreationDate() {
			return creationDate;
		}

		public String[] getOwningProcess() {
			return owningProcess;
		}

		/**
		 * Return an unmodifiable copy of the property set
		 */
		public Set<? extends MonitorableProperty<?>> getProperties() {
			return Collections.unmodifiableSet(properties);
		}

		public Object getWorkflowObject() {
			return workflowObject;
		}

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
				int i = 0;
				for (String nameElement : prop.getName()) {
					sb.append(nameElement);
					i++;
					if (i < prop.getName().length) {
						sb.append(".");
					}
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
