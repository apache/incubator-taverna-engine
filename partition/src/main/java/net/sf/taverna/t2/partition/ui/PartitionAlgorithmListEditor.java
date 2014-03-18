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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.taverna.t2.partition.PartitionAlgorithm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.List;

public class PartitionAlgorithmListEditor extends JPanel {

	// Serial version ID
	private static final long serialVersionUID = 8206805090698009524L;

	// Index of currently selected 'tab' or -1 if none selected
	private int selectedIndex = 1;

	// List of partition algorithm instances acting as the model for this
	// component
	private List<PartitionAlgorithm<?>> paList;

	private int cornerSep = 8;
	private int labelHorizontalPad = 10;
	private int labelBottomPad = 2;
	private int labelTopPad = 4;
	private float selectedStrokeWidth = 3f;
	
	public List<PartitionAlgorithm<?>> getPartitionAlgorithmList() {
		return null;
	}

	@Override
	public Dimension getPreferredSize() {
		if (paList.isEmpty()) {
			return new Dimension(0, 16 + labelBottomPad + labelTopPad);
		} else {
			return new Dimension(0, (int) new Tab(getLabelForPA(paList.get(0)))
					.getPreferredSize().getHeight());
		}
	}

	public PartitionAlgorithmListEditor(
			List<PartitionAlgorithm<?>> currentState) {
		super();
		this.paList = currentState;
	}

	protected JLabel getLabelForPA(PartitionAlgorithm<?> pa) {
		return new JLabel("Test...");
	}

	protected Color getBorderColorForPA(PartitionAlgorithm<?> pa) {
		return Color.black;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		// Enable anti-aliasing for the curved lines
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int selectedStartsAt = 0;
		int selectedEndsAt = 0;
		Color frameColor = null;
		int cumulativeTranslation = 0;

		super.paintComponent(g2d);

		for (int i = 0; i < paList.size(); i++) {

			Tab t = new Tab(getLabelForPA(paList.get(i)));
			t.setBackground(new Color(150, 150, 255));
			t.setSelected(i == selectedIndex);
			int width = (int) (t.getPreferredSize()).getWidth();
			t.setSize(new Dimension(width, getHeight()));
			t.paint(g2d);

			if (i < selectedIndex) {
				selectedStartsAt += width;
			} else if (i == selectedIndex) {
				selectedEndsAt = selectedStartsAt + width;
				frameColor = t.getBackground();
			}
			cumulativeTranslation += width;
			g2d.translate(width, 0);
		}
			
		
		// Reset the transform
		g2d.translate(-cumulativeTranslation, 0);
		if (selectedIndex > 0) {
			g2d.setStroke(new BasicStroke(selectedStrokeWidth, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER));
			int height = (int)(getHeight() - selectedStrokeWidth/2);
			// Render the selected index line...
			if (frameColor != null) {
				g2d.setPaint(frameColor.darker());
			}
			GeneralPath path = new GeneralPath();
			path.moveTo(0, height);
			path.lineTo(selectedStartsAt, height);
			path.lineTo(selectedStartsAt, cornerSep);
			path.curveTo(selectedStartsAt, cornerSep / 2, selectedStartsAt
					+ cornerSep / 2, 0, selectedStartsAt + cornerSep, 0);
			path.lineTo(selectedEndsAt - cornerSep, 0);
			path.curveTo(selectedEndsAt - cornerSep / 2, 0, selectedEndsAt,
					cornerSep / 2, selectedEndsAt, cornerSep);
			path.lineTo(selectedEndsAt, height);
			path.lineTo(getWidth(), height);

			g2d.draw(path);
		}
		g2d.dispose();
	}

	@SuppressWarnings("serial")
	// Renderer for a single tab in the partition algorithm list, used as a
	// rubber stamp for a single tab in the tab list.
	class Tab extends JComponent {

		// Label to use to render tab contents
		private JLabel label;

		// If this is selected then we don't draw the stroke as it'll be drawn
		// on later.
		private boolean selected = false;

		@Override
		// Always false as we don't render the corners
		public boolean isOpaque() {
			return false;
		}

		public void setSelected(boolean b) {
			this.selected = b;

		}

		@Override
		public Dimension getPreferredSize() {
			Dimension d = label.getPreferredSize();
			return new Dimension((int) (d.getWidth()) + labelHorizontalPad * 2,
					((int) d.getHeight()) + labelBottomPad + labelTopPad);
		}

		protected Tab(JLabel label) {
			super();
			this.label = label;
		}

		@Override
		public void setBackground(Color colour) {
			label.setBackground(colour);
		}

		@Override
		public Color getBackground() {
			return label.getBackground();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();

			int width = getWidth();
			int height = getHeight();

			// Create a general path to draw the tab shape
			g2d.setPaint(label.getBackground());
			GeneralPath path = new GeneralPath();
			path.moveTo(0, height);
			path.lineTo(0, cornerSep);
			path.curveTo(0, cornerSep / 2, cornerSep / 2, 0, cornerSep, 0);
			path.lineTo(width - cornerSep, 0);
			path.curveTo(width - cornerSep / 2, 0, width, cornerSep / 2, width,
					cornerSep);
			path.lineTo(width, height);
			path.closePath();
			g2d.fill(path);
			if (!selected) {
				g2d.setPaint(label.getBackground().darker());
				g2d.draw(path);
			}

			label.setSize(width - labelHorizontalPad * 2, height
					- (labelBottomPad + labelTopPad));
			g2d.translate(labelHorizontalPad, labelTopPad);
			label.paint(g2d);
			g2d.translate(-labelHorizontalPad, -labelTopPad);

			g2d.dispose();
		}
	}

}
