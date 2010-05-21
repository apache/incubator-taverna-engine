/*
 * @(#)JTreeTable.java	1.2 98/10/27
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package net.sf.taverna.t2.lang.ui.treetable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * This example shows how to create a simple JTreeTable component, by using a
 * JTree as a renderer (and editor) for the cells in a particular column in the
 * JTable.
 * 
 * @version 1.2 10/27/98
 * 
 * @author Philip Milne
 * @author Scott Violet
 * @author Tom Oinn
 */
@SuppressWarnings("serial")
public class JTreeTable extends JTable {
	/** A subclass of JTree. */
	protected TreeTableCellRenderer tree;

	public JTreeTable() {
		super();
	}

	public JTreeTable(TreeTableModel treeTableModel) {
		super();
		setModel(treeTableModel);
	}

	public void setModel(TreeTableModel model) {
		tree = new TreeTableCellRenderer(model);
		super.setModel(new TreeTableModelAdapter(model, tree));
		
		ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper();
		tree.setSelectionModel(selectionWrapper);
		setSelectionModel(selectionWrapper.getListSelectionModel());
		
		// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeTableModel.class, tree);
		setDefaultRenderer(Date.class, new DateCellRenderer());
		setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());
		
		// No grid.
		setShowGrid(true);

		// No intercell spacing
		setIntercellSpacing(new Dimension(0, 0));

		// Add a mouse listener to forward events on to the tree.
		// We need this as on MAC only double clicks seem to expand the tree.
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				for (int counter = getColumnCount() - 1; counter >= 0; counter--) {
					if (getColumnClass(counter) == TreeTableModel.class) {
						MouseEvent me = (MouseEvent) e;
						MouseEvent newME = new MouseEvent(tree, me.getID(), me
								.getWhen(), me.getModifiers(), me.getX()
								- getCellRect(0, counter, true).x, me.getY(),
								me.getClickCount(), me.isPopupTrigger());
						if (me.getClickCount() == 1) {
							tree.dispatchEvent(newME);
						}
					}
					else{
						
					}
				}
			}
		});

		// And update the height of the trees row to match that of
		// the table.

		if (tree.getRowHeight() < 1) {
			// Metal looks better like this.
			setRowHeight(18);
		}

	}

	/**
	 * Overridden to message super and forward the method to the tree. Since the
	 * tree is not actually in the component hieachy it will never receive this
	 * unless we forward it in this manner.
	 */
	public void updateUI() {
		super.updateUI();
		if (tree != null) {
			tree.updateUI();
		}
		// Use the tree's default foreground and background colors in the
		// table.
		LookAndFeel.installColorsAndFont(this, "Tree.background",
				"Tree.foreground", "Tree.font");
	}

	/*
	 * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to paint the
	 * renderers and editors and overriding setBounds() below is not the right
	 * thing to do for an editor. Returning -1 for the editing row in this case,
	 * ensures the editor is never painted.
	 */
	public int getEditingRow() {
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1
				: editingRow;
	}

	/**
	 * Returns the actual row that is editing as <code>getEditingRow</code> will
	 * always return -1.
	 */
	private int realEditingRow() {
		return editingRow;
	}

	/**
	 * This is overridden to invoke super's implementation, and then, if the
	 * receiver is editing a Tree column, the editor's bounds is reset. The
	 * reason we have to do this is because JTable doesn't think the table is
	 * being edited, as <code>getEditingRow</code> returns -1, and therefore
	 * doesn't automatically resize the editor for us.
	 */
	public void sizeColumnsToFit(int resizingColumn) {
		super.sizeColumnsToFit(resizingColumn);
		if (getEditingColumn() != -1
				&& getColumnClass(editingColumn) == TreeTableModel.class) {
			Rectangle cellRect = getCellRect(realEditingRow(),
					getEditingColumn(), false);
			Component component = getEditorComponent();
			component.setBounds(cellRect);
			component.validate();
		}
	}

	/**
	 * Overridden to pass the new rowHeight to the tree.
	 */
	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		if (tree != null && tree.getRowHeight() != rowHeight) {
			tree.setRowHeight(getRowHeight());
		}
	}

	/**
	 * Returns the tree that is being shared between the model.
	 */
	public JTree getTree() {
		return tree;
	}

	/**
	 * Overridden to invoke repaint for the particular location if the column
	 * contains the tree. This is done as the tree editor does not fill the
	 * bounds of the cell, we need the renderer to paint the tree in the
	 * background, and then draw the editor over it.
	 */
	public boolean editCellAt(int row, int column, EventObject e) {
		boolean retValue = super.editCellAt(row, column, e);
		if (retValue && getColumnClass(column) == TreeTableModel.class) {
			repaint(getCellRect(row, column, false));
		}
		return retValue;
	}

	/**
	 * A TreeCellRenderer that displays a JTree.
	 */
	public class TreeTableCellRenderer extends JTree implements
			TableCellRenderer {
		/** Last table/tree row asked to renderer. */
		protected int visibleRow;
		protected Border highlightBorder;

		public TreeTableCellRenderer(TreeModel model) {
			super(model);
		}

		/**
		 * updateUI is overridden to set the colors of the Tree's renderer to
		 * match that of the table.
		 */
		public void updateUI() {
			super.updateUI();
			// Make the tree's cell renderer use the table's cell selection
			// colors.
			TreeCellRenderer tcr = getCellRenderer();
			if (tcr instanceof DefaultTreeCellRenderer) {
				DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
				// For 1.1 uncomment this, 1.2 has a bug that will cause an
				// exception to be thrown if the border selection color is
				// null.
				// dtcr.setBorderSelectionColor(null);
				// dtcr.setTextSelectionColor(UIManager.getColor
				// ("Table.selectionForeground"));
				// dtcr.setBackgroundSelectionColor(UIManager.getColor
				// ("Table.selectionBackground"));
			}
		}

		/**
		 * Sets the row height of the tree, and forwards the row height to the
		 * table.
		 */
		public void setRowHeight(int rowHeight) {
			if (rowHeight > 0) {
				super.setRowHeight(rowHeight);
				if (JTreeTable.this != null
						&& JTreeTable.this.getRowHeight() != rowHeight) {
					JTreeTable.this.setRowHeight(getRowHeight());
				}
			}
		}

		/**
		 * This is overridden to set the height to match that of the JTable.
		 */
		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, 0, w, JTreeTable.this.getHeight());
		}

		/**
		 * Sublcassed to translate the graphics such that the last visible row
		 * will be drawn at 0,0.
		 */
		public void paint(Graphics g) {
			g.translate(0, -visibleRow * getRowHeight());
			super.paint(g);
			// Draw the Table border if we have focus.
			if (highlightBorder != null) {
				highlightBorder.paintBorder(this, g, 0, visibleRow
						* getRowHeight(), getWidth(), getRowHeight());
			}
		}

		/**
		 * TreeCellRenderer method. Overridden to update the visible row.
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Color background;
			Color foreground;

			if (isSelected) {
				background = table.getSelectionBackground();
				foreground = table.getSelectionForeground();
			} else {
				background = table.getBackground();
				foreground = table.getForeground();
			}
			highlightBorder = null;
			if (realEditingRow() == row && getEditingColumn() == column) {
				// background = UIManager.getColor("Table.focusCellBackground");
				// foreground = UIManager.getColor("Table.focusCellForeground");
			} else if (hasFocus) {
				highlightBorder = UIManager
						.getBorder("Table.focusCellHighlightBorder");
				if (isCellEditable(row, column)) {
					// background = UIManager.getColor
					// ("Table.focusCellBackground");
					background = table.getSelectionBackground();
					foreground = table.getSelectionForeground();
				}
			}

			visibleRow = row;
			setBackground(background);

			TreeCellRenderer tcr = getCellRenderer();
			if (tcr instanceof DefaultTreeCellRenderer) {
				DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
				if (isSelected) {
					dtcr.setTextSelectionColor(foreground);
					dtcr.setBackgroundSelectionColor(background);
				} else {
					dtcr.setTextNonSelectionColor(foreground);
					dtcr.setBackgroundNonSelectionColor(background);
				}
			}
			return this;
		}

	}

	/**
	 * A TreeCellRenderer that displays a date in a "yyyy-MM-dd HH:mm:ss"
	 * format.
	 */
	public class DateCellRenderer extends JLabel implements TableCellRenderer {
		/** Last table/tree row asked to renderer. */
		protected int visibleRow;
		protected Border highlightBorder;

		//Border selectedBorder = null;
		//Border unselectedBorder = null;

		public DateCellRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			if (isSelected) {
//				if (selectedBorder == null) {
//					selectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
//							5, table.getSelectionBackground());
//				}
//				setBorder(selectedBorder);
				this.setBackground(table.getSelectionBackground());
				this.setForeground(table.getSelectionForeground());
			} else {
//				if (unselectedBorder == null) {
//					unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
//							5, table.getBackground());
//				}
//				setBorder(unselectedBorder);
				this.setBackground(table.getBackground());
				this.setForeground(table.getForeground());
			}
			
			Date date = (Date) ((TreeTableModelAdapter) getModel()).getValueAt(
					row, column);
			if (date != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				this.setText(sdf.format(date));
			} else {
				this.setText(null);
			}
			return this;
		}

	}

	/**
	 * TreeTableCellEditor implementation. Component returned is the JTree.
	 */
	public class TreeTableCellEditor extends DefaultCellEditor {
		public TreeTableCellEditor() {
			super(new TreeTableTextField());
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int r, int c) {
			Component component = super.getTableCellEditorComponent(table,
					value, isSelected, r, c);
			JTree t = getTree();
			boolean rv = t.isRootVisible();
			int offsetRow = rv ? r : r - 1;
			Rectangle bounds = t.getRowBounds(offsetRow);
			int offset = bounds.x;
			TreeCellRenderer tcr = t.getCellRenderer();
			if (tcr instanceof DefaultTreeCellRenderer) {
				Object node = t.getPathForRow(offsetRow).getLastPathComponent();
				boolean isExpanded = t.isExpanded(t.getPathForRow(offsetRow));
				boolean isLeaf = t.getModel().isLeaf(node);
				Component renderer = tcr.getTreeCellRendererComponent(t, node,
						true, isExpanded, isLeaf, offsetRow, true);
				Icon icon = ((JLabel) renderer).getIcon();
				// if (t.getModel().isLeaf(node))
				// icon = ((DefaultTreeCellRenderer)tcr).getLeafIcon();
				// else if (tree.isExpanded(offsetRow))
				// icon = ((DefaultTreeCellRenderer)tcr).getOpenIcon();
				// else
				// icon = ((DefaultTreeCellRenderer)tcr).getClosedIcon();
				if (icon != null) {
					offset += ((DefaultTreeCellRenderer) tcr).getIconTextGap()
							+ icon.getIconWidth();
				}
			}
			((TreeTableTextField) getComponent()).offset = offset;
			return component;
		}

		// /**
		// * This is overridden to forward the event to the tree. This will
		// return
		// * true if the click count >= 3, or the event is null.
		// */
		// public boolean isCellEditable(EventObject e) {
		// /**
		// * if (e instanceof MouseEvent) { for (int counter =
		// * getColumnCount() - 1; counter >= 0; counter--) { if
		// * (getColumnClass(counter) == TreeTableModel.class) { MouseEvent me
		// * = (MouseEvent)e; MouseEvent newME = new MouseEvent(tree,
		// * me.getID(), me.getWhen(), me.getModifiers(), me.getX() -
		// * getCellRect(0, counter, true).x, me.getY(), me.getClickCount(),
		// * me.isPopupTrigger()); System.out.println(newME);
		// * tree.dispatchEvent(newME); break; } } }
		// */
		// if (e instanceof MouseEvent) {
		// MouseEvent me = (MouseEvent) e;
		// if (me.getClickCount() >= 3) {
		// return true;
		// }
		// }
		// if (e == null) {
		// return true;
		// }
		// return false;
		// }

		/**
		 * This is overridden to forward the event to the tree. This will return
		 * true if the click count >= 3, or the event is null.
		 */
		public boolean isCellEditable(EventObject e) {
			// Edit on double click rather than the default triple
			if (e instanceof MouseEvent) {
				
				MouseEvent me = (MouseEvent) e;
				if (me.getClickCount() == 1
						&& System.getProperty("os.name").equals("Mac OS X")) {
					// Workaround for buggy tree table on OS X. Open/close the
					// path
					// on any click on the column (not just on the > icon)
					for (int counter = getColumnCount() - 1; counter >= 0; counter--) {
						if (getColumnClass(counter) == TreeTableModel.class) {
							MouseEvent newME = new MouseEvent(tree, me.getID(),
									me.getWhen(), me.getModifiers(), me.getX()
											- getCellRect(0, counter, true).x,
									me.getY(), me.getClickCount(), me
											.isPopupTrigger());
							tree.dispatchEvent(newME);

							Point p = new Point(me.getX(), me.getY());
							int row = rowAtPoint(p);
							int column = columnAtPoint(p);
							if (column == 0) {
								boolean isExpanded = tree.isExpanded(tree
										.getPathForRow(row));
								if (isExpanded == false) {
									tree.expandPath(tree.getPathForRow(row));
								} else {
									tree.collapsePath(tree.getPathForRow(row));
								}
							}

							break;
						}
					}

				}
				/*
				 * if (me.getClickCount() >= 3) { return true; }
				 */// tree is no editable
			}
			if (e == null) {
				return true;
			}
			return false;
		}

	}

	/**
	 * Component used by TreeTableCellEditor. The only thing this does is to
	 * override the <code>reshape</code> method, and to ALWAYS make the x
	 * location be <code>offset</code>.
	 */
	public static class TreeTableTextField extends JTextField {
		public int offset;

		public void setBounds(int x, int y, int w, int h) {
			int newX = Math.max(x, offset);
			super.setBounds(newX, y, w - (newX - x), h);
		}
	}

	/**
	 * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel to
	 * listen for changes in the ListSelectionModel it maintains. Once a change
	 * in the ListSelectionModel happens, the paths are updated in the
	 * DefaultTreeSelectionModel.
	 */
	class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {
		/** Set to true when we are updating the ListSelectionModel. */
		protected boolean updatingListSelectionModel;

		public ListToTreeSelectionModelWrapper() {
			super();
			setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			getListSelectionModel().addListSelectionListener(
					createListSelectionListener());
		}

		/**
		 * Returns the list selection model. ListToTreeSelectionModelWrapper
		 * listens for changes to this model and updates the selected paths
		 * accordingly.
		 */
		ListSelectionModel getListSelectionModel() {
			return listSelectionModel;
		}

		/**
		 * This is overridden to set <code>updatingListSelectionModel</code> and
		 * message super. This is the only place DefaultTreeSelectionModel
		 * alters the ListSelectionModel.
		 */
		public void resetRowSelection() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					super.resetRowSelection();
				} finally {
					updatingListSelectionModel = false;
				}
			}
			// Notice how we don't message super if
			// updatingListSelectionModel is true. If
			// updatingListSelectionModel is true, it implies the
			// ListSelectionModel has already been updated and the
			// paths are the only thing that needs to be updated.
		}

		/**
		 * Creates and returns an instance of ListSelectionHandler.
		 */
		protected ListSelectionListener createListSelectionListener() {
			return new ListSelectionHandler();
		}

		/**
		 * If <code>updatingListSelectionModel</code> is false, this will reset
		 * the selected paths from the selected rows in the list selection
		 * model.
		 */
		protected void updateSelectedPathsFromSelectedRows() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					// This is way expensive, ListSelectionModel needs an
					// enumerator for iterating.
					int min = listSelectionModel.getMinSelectionIndex();
					int max = listSelectionModel.getMaxSelectionIndex();

					clearSelection();
					if (min != -1 && max != -1) {
						for (int counter = min; counter <= max; counter++) {
							if (listSelectionModel.isSelectedIndex(counter)) {
								TreePath selPath = tree.getPathForRow(counter);

								if (selPath != null) {
									addSelectionPath(selPath);
								}
							}
						}
					}
				} finally {
					updatingListSelectionModel = false;
				}
			}
		}

		/**
		 * Class responsible for calling updateSelectedPathsFromSelectedRows
		 * when the selection of the list change.
		 */
		class ListSelectionHandler implements ListSelectionListener {
			public void valueChanged(ListSelectionEvent e) {
				updateSelectedPathsFromSelectedRows();
			}
		}
	}
}
