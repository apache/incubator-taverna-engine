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
package net.sf.taverna.t2.partition.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public abstract class TableTreeNodeRenderer implements TreeCellRenderer {

	private static final long serialVersionUID = -7291631337751330696L;

	// The difference in indentation between a node and its child nodes, there
	// isn't an easy way to get this other than constructing a JTree and
	// measuring it - you'd think it would be a property of TreeUI but
	// apparently not.
	private static int perNodeOffset = -1;

	// Use this to rubber stamp the original node renderer in before rendering
	// the table
	private TreeCellRenderer nodeRenderer;

	// Determines the space allocated to leaf nodes and their parents when
	// applying the stamp defined by the nodeRenderer
	private int nodeWidth;

	// Number of pixels of space to leave between the node label and the table
	// header or rows
	private int labelToTablePad = 3;

	// Number of pixels to leave around the label rendered into the table cells
	private int cellPadding = 4;

	// Drawing borders?
	private boolean drawingBorders = true;

	// The number of pixels by which the height of the header is reduced
	// compared to the row height, this leaves a small border above the header
	// and separates it from the last row of the table above, if any.
	private int headerTopPad = 4;

	// The proportion of colour : black or colour : white used to create the
	// darker or lighter shades is blendFactor : 1
	private int blendFactor = 2;

	// Colour to use to draw the table borders when they're enabled
	private Color borderColour = Color.black;

	/**
	 * Set the colour to be used to draw the borders if they are displayed at
	 * all. Defaults to black.
	 */
	public void setBorderColour(Color borderColour) {
		this.borderColour = borderColour;
	}

	/**
	 * The blend factor determines how strong the colour component is in the
	 * shadow and highlight colours used in the bevelled boxes, the ratio of
	 * black / white to colour is 1 : blendFactor
	 * 
	 * @param blendFactor
	 */
	public void setBlendFactor(int blendFactor) {
		this.blendFactor = blendFactor;
	}

	/**
	 * Set whether the renderer will draw borders around the table cells - if
	 * this is false the table still has the bevelled edges of the cell painters
	 * so will still look semi-bordered. Defaults to true if not otherwise set.
	 * 
	 * @param drawingBorders
	 */
	public void setDrawBorders(boolean drawingBorders) {
		this.drawingBorders = drawingBorders;
	}

	/**
	 * Override and implement to get the list of columns for a given partition
	 * node - currently assumes all partitions use the same column structure
	 * which I need to fix so it doesn't take a partition as argument (yet).
	 * 
	 * @return an array of column specifications used to drive the renderer
	 */
	public abstract TableTreeNodeColumn[] getColumns();

	/**
	 * Construct a new TableTreeNodeRenderer
	 * 
	 * @param nodeRenderer
	 *            The inner renderer used to render the node labels
	 * @param nodeWidth
	 *            Width of the cell space into which the node label is rendered
	 *            in the table header and row nodes
	 */
	public TableTreeNodeRenderer(TreeCellRenderer nodeRenderer, int nodeWidth) {
		super();
		this.nodeRenderer = nodeRenderer;
		this.nodeWidth = nodeWidth;
	}

	/**
	 * Do the magic!
	 */
	public Component getTreeCellRendererComponent(final JTree tree,
			final Object value, final boolean selected, final boolean expanded,
			final boolean leaf, final int row, final boolean hasFocus) {
		final Component nodeLabel = nodeRenderer.getTreeCellRendererComponent(
				tree, value, false, expanded, leaf, row, false);
		final int nodeLabelHeight = (int) nodeLabel.getPreferredSize()
				.getHeight();
		if (leaf) {
			// Rendering the leaf nodes, therefore use the table rendering
			// strategy
			getPerNodeIndentation(tree, row);
			return new JComponent() {
				private static final long serialVersionUID = 4993815558563895266L;

				@Override
				public Dimension getPreferredSize() {
					int width = nodeWidth + labelToTablePad;
					for (TableTreeNodeColumn column : getColumns()) {
						width += column.getColumnWidth();
					}
					return new Dimension(width, nodeLabelHeight);
				}

				@Override
				protected void paintComponent(Graphics g) {

					Graphics2D g2d = (Graphics2D) g.create();
					AffineTransform originalTransform = g2d.getTransform();
					// Enable anti-aliasing for the curved lines
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);

					// This method should paint a bevelled container for the
					// original label but it doesn't really work terribly well
					// as we can't ensure that the original label is actually
					// honouring any opaque flags.
					if (drawingBorders) {
						paintRectangleWithBevel(g2d, nodeWidth
								+ labelToTablePad, getHeight(), Color.white);
					}

					// Paint original node label
					nodeLabel.setSize(new Dimension(
							nodeWidth - cellPadding * 2, getHeight()
									- (drawingBorders ? 2 : 1)));
					g2d.translate(cellPadding, 0);
					nodeLabel.paint(g2d);
					g2d.translate(-cellPadding, 0);

					if (drawingBorders) {
						paintRectangleBorder(g2d, nodeWidth + labelToTablePad,
								getHeight(), 0, 0, 1, 1, borderColour);
					}

					g2d.translate(nodeWidth + labelToTablePad, 0);
					boolean first = true;
					for (TableTreeNodeColumn column : getColumns()) {

						Color fillColour = column.getColour().brighter();
						Object parentNode = tree.getPathForRow(row)
								.getParentPath().getLastPathComponent();
						int indexInParent = tree.getModel().getIndexOfChild(
								parentNode, value);
						if ((indexInParent & 1) == 1) {
							fillColour = new Color(
									(fillColour.getRed() + column.getColour()
											.getRed()) / 2, (fillColour
											.getGreen() + column.getColour()
											.getGreen()) / 2, (fillColour
											.getBlue() + column.getColour()
											.getBlue()) / 2);
						}

						// Paint background and bevel
						paintRectangleWithBevel(g2d, column.getColumnWidth(),
								getHeight(), fillColour);

						// Paint cell component
						Component cellComponent = column.getCellRenderer(value);
						cellComponent.setSize(new Dimension(column
								.getColumnWidth()
								- cellPadding * 2, getHeight()));
						g2d.translate(cellPadding, 0);
						cellComponent.paint(g2d);
						g2d.translate(-cellPadding, 0);

						// Draw border
						if (drawingBorders) {
							paintRectangleBorder(g2d, column.getColumnWidth(),
									getHeight(), 0, 1, 1, first ? 1 : 0,
									borderColour);
						}
						first = false;

						g2d.translate(column.getColumnWidth(), 0);

					}
					if (selected) {
						g2d.setTransform(originalTransform);
						g2d.translate(2, 0);
						paintRectangleWithHighlightColour(g2d, getWidth()
								- (drawingBorders ? 4 : 2), getHeight()
								- (drawingBorders ? 2 : 0));
					}
				}
			};
		} else {
			// If there are no child nodes, or there are child nodes but they
			// aren't leaves then we render the cell as normal. If there are
			// child nodes and the first one is a leaf (we assume this means
			// they all are!) then we render the table header after the label.
			if (!expanded) {
				return getLabelWithHighlight(nodeLabel, selected);
			}
			// Expanded so do the model check...
			TreeModel model = tree.getModel();
			int childCount = model.getChildCount(value);
			if (childCount == 0) {
				return getLabelWithHighlight(nodeLabel, selected);
			}
			Object childNode = model.getChild(value, 0);
			if (!model.isLeaf(childNode)) {
				return getLabelWithHighlight(nodeLabel, selected);
			}
			getPerNodeIndentation(tree, row);
			// Got to here so we need to render a table header.
			return new JComponent() {
				private static final long serialVersionUID = -4923965850510357216L;

				@Override
				public Dimension getPreferredSize() {
					int width = nodeWidth + labelToTablePad + perNodeOffset;
					for (TableTreeNodeColumn column : getColumns()) {
						width += column.getColumnWidth();
					}
					return new Dimension(width, nodeLabelHeight);
				}

				@Override
				protected void paintComponent(Graphics g) {

					Graphics2D g2d = (Graphics2D) g.create();
					AffineTransform originalTransform = g2d.getTransform();
					// Enable anti-aliasing for the curved lines
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);

					// Paint original node label
					nodeLabel.setSize(new Dimension(nodeWidth + perNodeOffset,
							getHeight()));
					nodeLabel.paint(g2d);

					// Draw line under label to act as line above table row
					// below
					if (drawingBorders) {
						GeneralPath path = new GeneralPath();
						path.moveTo(perNodeOffset, getHeight() - 1);
						path.lineTo(
								perNodeOffset + nodeWidth + labelToTablePad,
								getHeight() - 1);
						g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
								BasicStroke.JOIN_MITER));
						g2d.setPaint(borderColour);
						g2d.draw(path);
					}

					// Move painting origin to the start of the header row
					g2d.translate(nodeWidth + perNodeOffset + labelToTablePad,
							0);

					// Paint columns
					boolean first = true;
					for (TableTreeNodeColumn column : getColumns()) {

						// Paint header cell background with bevel
						g2d.translate(0, headerTopPad);
						paintRectangleWithBevel(g2d, column.getColumnWidth(),
								getHeight() - headerTopPad, column.getColour());

						// Paint header label
						JLabel columnLabel = new JLabel(column.getShortName());
						columnLabel.setSize(new Dimension(column
								.getColumnWidth()
								- cellPadding * 2, getHeight() - headerTopPad));
						g2d.translate(cellPadding, 0);
						columnLabel.paint(g2d);
						g2d.translate(-cellPadding, 0);

						// Paint header borders
						if (drawingBorders) {
							paintRectangleBorder(g2d, column.getColumnWidth(),
									getHeight() - headerTopPad, 1, 1, 1,
									first ? 1 : 0, borderColour);
						}
						g2d.translate(0, -headerTopPad);
						first = false;
						g2d.translate(column.getColumnWidth(), 0);

					}
					if (selected) {
						g2d.setTransform(originalTransform);
						g2d.translate(1, headerTopPad);
						paintRectangleWithHighlightColour(g2d, perNodeOffset
								+ nodeWidth + labelToTablePad
								- (drawingBorders ? 2 : 0), getHeight()
								- (headerTopPad + 2));
					}
				}
			};

		}

	}

	private static Component getLabelWithHighlight(final Component c,
			boolean selected) {
		if (!selected) {
			return c;
		} else {
			return new JComponent() {
				private static final long serialVersionUID = -9175635475959046704L;

				@Override
				public Dimension getPreferredSize() {
					return c.getPreferredSize();
				}

				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2d = (Graphics2D) g.create();
					c.setSize(new Dimension(getWidth(), getHeight()));
					c.paint(g2d);
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.translate(1, 1);
					paintRectangleWithHighlightColour(g2d, getWidth() - 2,
							getHeight() - 2);
				}
			};
		}
	}

	private static void paintRectangleBorder(Graphics2D g2d, int width,
			int height, int north, int east, int south, int west, Color c) {
		Paint oldPaint = g2d.getPaint();
		Stroke oldStroke = g2d.getStroke();

		g2d.setPaint(c);

		GeneralPath path;

		if (north > 0) {
			g2d.setStroke(new BasicStroke(north, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER));
			path = new GeneralPath();
			path.moveTo(0, north - 1);
			path.lineTo(width - 1, north - 1);
			g2d.draw(path);
		}
		if (east > 0) {
			g2d.setStroke(new BasicStroke(east, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER));
			path = new GeneralPath();
			path.moveTo(width - east, 0);
			path.lineTo(width - east, height - 1);
			g2d.draw(path);
		}
		if (south > 0) {
			g2d.setStroke(new BasicStroke(south, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER));
			path = new GeneralPath();
			path.moveTo(0, height - south);
			path.lineTo(width - 1, height - south);
			g2d.draw(path);
		}
		if (west > 0) {
			g2d.setStroke(new BasicStroke(west, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER));
			path = new GeneralPath();
			path.moveTo(west - 1, 0);
			path.lineTo(west - 1, height - 1);
			g2d.draw(path);
		}

		g2d.setPaint(oldPaint);
		g2d.setStroke(oldStroke);
	}

	/**
	 * Paint a rectangle with the border colour set from the UIManager
	 * 'textHighlight' property and filled with the same colour at alpha 50/255.
	 * Paints from 0,0 to width-1,height-1 into the specified Graphics2D,
	 * preserving the existing paint and stroke properties on exit.
	 */
	private static void paintRectangleWithHighlightColour(Graphics2D g2d,
			int width, int height) {
		GeneralPath path = new GeneralPath();
		path.moveTo(0, 0);
		path.lineTo(width - 1, 0);
		path.lineTo(width - 1, height - 1);
		path.lineTo(0, height - 1);
		path.closePath();

		Paint oldPaint = g2d.getPaint();
		Stroke oldStroke = g2d.getStroke();

		Color c = UIManager.getColor("textHighlight");

		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER));
		g2d.setPaint(c);
		g2d.draw(path);

		Color alpha = new Color(c.getRed(), c.getGreen(), c.getBlue(), 50);
		g2d.setPaint(alpha);
		g2d.fill(path);

		g2d.setPaint(oldPaint);
		g2d.setStroke(oldStroke);

	}

	/**
	 * Paint a bevelled rectangle into the specified Graphics2D with shape from
	 * 0,0 to width-1,height-1 using the specified colour as a base and
	 * preserving colour and stroke in the Graphics2D
	 */
	private void paintRectangleWithBevel(Graphics2D g2d, int width, int height,
			Color c) {
		if (drawingBorders) {
			width = width - 1;
			height = height - 1;
		}

		GeneralPath path = new GeneralPath();
		path.moveTo(0, 0);
		path.lineTo(width - 1, 0);
		path.lineTo(width - 1, height - 1);
		path.lineTo(0, height - 1);
		path.closePath();

		Paint oldPaint = g2d.getPaint();
		Stroke oldStroke = g2d.getStroke();

		g2d.setPaint(c);
		g2d.fill(path);

		g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER));

		// Draw highlight (Northeast)
		path = new GeneralPath();
		path.moveTo(0, 0);
		path.lineTo(width - 1, 0);
		path.lineTo(width - 1, height - 1);
		Color highlightColour = new Color((c.getRed() * blendFactor + 255)
				/ (blendFactor + 1), (c.getGreen() * blendFactor + 255)
				/ (blendFactor + 1), (c.getBlue() * blendFactor + 255)
				/ (blendFactor + 1));
		g2d.setPaint(highlightColour);
		g2d.draw(path);

		// Draw shadow (Southwest)
		path = new GeneralPath();
		path.moveTo(0, 0);
		path.lineTo(0, height - 1);
		path.lineTo(width - 1, height - 1);
		Color shadowColour = new Color((c.getRed() * blendFactor)
				/ (blendFactor + 1), (c.getGreen() * blendFactor)
				/ (blendFactor + 1), (c.getBlue() * blendFactor)
				/ (blendFactor + 1));
		g2d.setPaint(shadowColour);
		g2d.draw(path);

		g2d.setPaint(oldPaint);
		g2d.setStroke(oldStroke);

	}

	/**
	 * The TreeUI which was used to determine the per node indentation in the
	 * JTree for which this is a renderer. If this hasn't been set yet then this
	 * is null.
	 */
	private static TreeUI cachedTreeUI = null;

	/**
	 * Use the current TreeUI to determine the indentation per-node in the tree,
	 * this only works when the treeRow passed in is not the root as it has to
	 * traverse up to the parent to do anything sensible. Cached and associated
	 * with the TreeUI so in theory if the look and feel changes the UI changes
	 * and this is re-generated within the renderer code.
	 * 
	 * @param tree
	 * @param treeRow
	 * @return
	 */
	private static int getPerNodeIndentation(JTree tree, int treeRow) {
		if (perNodeOffset > 0 && tree.getUI() == cachedTreeUI) {
			return perNodeOffset;
		}
		TreeUI uiModel = tree.getUI();
		cachedTreeUI = uiModel;
		TreePath path = tree.getPathForRow(treeRow);
		Rectangle nodeBounds = uiModel.getPathBounds(tree, path);
		Rectangle parentNodeBounds = uiModel.getPathBounds(tree, path
				.getParentPath());
		perNodeOffset = (int) nodeBounds.getMinX()
				- (int) parentNodeBounds.getMinX();
		return perNodeOffset;
	}

}
