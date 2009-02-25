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
package net.sf.taverna.t2.partition;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sf.taverna.t2.partition.algorithms.LiteralValuePartitionAlgorithm;
import net.sf.taverna.t2.partition.ui.TableTreeNodeColumn;
import net.sf.taverna.t2.partition.ui.TableTreeNodeRenderer;

/**
 * Exercise the partition algorithm codes
 * 
 * @author Tom Oinn
 * 
 */
public class PartitionTestApplication {

	final static PropertyExtractorRegistry reg = new ExampleExtractorRegistry();

	public static void main(String[] args) throws InterruptedException {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			//
		}

		JFrame frame = new JFrame("Partition test");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});

		RootPartition<ExampleItem> partition = new RootPartition<ExampleItem>(
				getAlgorithms(), reg);
		JTree partitionTree = new AlwaysOpenJTree(partition);
		partitionTree.setRowHeight(24);
		TreeCellRenderer oldRenderer = partitionTree.getCellRenderer();
		TableTreeNodeRenderer ttnr = new TableTreeNodeRenderer(oldRenderer, 50) {
			@Override
			public TableTreeNodeColumn[] getColumns() {
				return new TableTreeNodeColumn[] {
						new TableTreeNodeColumnImpl("int", new Color(150, 150,
								210), 60,reg),
						new TableTreeNodeColumnImpl("float", new Color(150,
								210, 150), 60,reg),
						new TableTreeNodeColumnImpl("name", new Color(210, 150,
								150), 60,reg) ,
						new TableTreeNodeColumnImpl("Fish", new Color(210, 150,
						150), 60,reg) };
			}
		};

		ttnr.setBorderColour(new Color(150, 150, 150));

		partitionTree.setCellRenderer(ttnr);

		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));
		frame.getContentPane().add(new JScrollPane(partitionTree));
		frame.getContentPane().add(new JScrollPane(partitionTree));
		frame.setSize(400, 200);
		frame.setVisible(true);
		boolean showFrames = false;
		//while (true) {
			ttnr.setDrawBorders(showFrames);
			showFrames = !showFrames;
			for (ExampleItem item : exampleItems) {
				Thread.sleep(200);
				partition.addOrUpdateItem(item);
			}
//			Thread.sleep(1000);
//			for (ExampleItem item : exampleItems) {
//				Thread.sleep(400);
//				partition.removeItem(item);
//			}
		//}
	}

	static ExampleItem[] exampleItems = new ExampleItem[] {
			new ExampleItem("foo", 1, 2.0f), new ExampleItem("bar", 1, 2.0f),
			new ExampleItem("foo", 4, 3.7f), new ExampleItem("foo", 3, 2.0f),
			new ExampleItem("bar", 1, 3.5f), new ExampleItem("bar", 1, 7.5f),
			new ExampleItem("foo", 1, 2.1f), new ExampleItem("bar", 1, 2.3f),
			new ExampleItem("foo", 4, 3.8f), new ExampleItem("foo", 3, 2.4f) };

	static class TableTreeNodeColumnImpl implements TableTreeNodeColumn {

		private String propertyName;
		private Color colour;
		private int columnWidth;
		private PropertyExtractorRegistry reg;

		public TableTreeNodeColumnImpl(String propertyName, Color colour,
				int width,PropertyExtractorRegistry reg) {
			this.propertyName = propertyName;
			this.colour = colour;
			this.columnWidth = width;
			this.reg=reg;
		}

		public Component getCellRenderer(Object value) {
			Object propertyValue = reg.getAllPropertiesFor(value).get(
					propertyName);
			if (propertyValue == null) {
				propertyValue = "Not defined";
			}
			return new JLabel(propertyValue.toString());
		}

		public Color getColour() {
			return this.colour;
		}

		public int getColumnWidth() {
			return columnWidth;
		}

		public String getDescription() {
			return "A description...";
		}

		public String getShortName() {
			return propertyName;
		}

	}

	static List<PartitionAlgorithm<?>> getAlgorithms() {
		List<PartitionAlgorithm<?>> paList = new ArrayList<PartitionAlgorithm<?>>();
		LiteralValuePartitionAlgorithm lvpa = new LiteralValuePartitionAlgorithm();
		lvpa.setPropertyName("float");
		
		LiteralValuePartitionAlgorithm lvpa2 = new LiteralValuePartitionAlgorithm();
		lvpa2.setPropertyName("int");
		LiteralValuePartitionAlgorithm lvpa3 = new LiteralValuePartitionAlgorithm();
		lvpa3.setPropertyName("name");
		
		paList.add(lvpa2);
		paList.add(lvpa);
		paList.add(lvpa3);
		
		return paList;
	}

	static class ExampleItem implements Comparable<Object>{
		private String name;
		private int intValue;
		private float floatValue;

		public String getName() {
			return this.name;
		}

		public int getIntValue() {
			return this.intValue;
		}

		public float getFloatValue() {
			return this.floatValue;
		}

		public ExampleItem(String name, int intValue, float floatValue) {
			this.name = name;
			this.intValue = intValue;
			this.floatValue = floatValue;
		}

		@Override
		public String toString() {
			return this.name;
		}

		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	static class ExampleExtractorRegistry implements PropertyExtractorRegistry {
		public Map<String, Object> getAllPropertiesFor(Object target) {
			Map<String, Object> properties = new HashMap<String, Object>();
			if (target instanceof ExampleItem) {
				ExampleItem item = (ExampleItem) target;
				properties.put("name", item.getName());
				properties.put("int", item.getIntValue());
				properties.put("float", item.getFloatValue());
				properties.put("Fish", "Soup");
			}
			
			return properties;
		}
	}

	static class AlwaysOpenJTree extends JTree {

		private static final long serialVersionUID = -3769998854485605447L;

		public AlwaysOpenJTree(TreeModel newModel) {
			super(newModel);
			setEditable(false);
			setExpandsSelectedPaths(false);
			setDragEnabled(false);
			setScrollsOnExpand(false);
			// setSelectionModel(SingleSelectionModel.sharedInstance());
		}

		@Override
		public void setModel(TreeModel model) {
			if (treeModel == model)
				return;
			if (treeModelListener == null)
				treeModelListener = new TreeModelHandler() {
					@Override
					public void treeNodesInserted(final TreeModelEvent ev) {
						if (ev.getChildren()[0] instanceof Partition == false) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									TreePath path = ev.getTreePath();
									setExpandedState(path, true);
									fireTreeExpanded(path);
								}
							});
						}

					}

				};
			if (model != null) {
				model.addTreeModelListener(treeModelListener);
			}
			TreeModel oldValue = treeModel;
			treeModel = model;
			firePropertyChange(TREE_MODEL_PROPERTY, oldValue, model);
		}

	}

}
