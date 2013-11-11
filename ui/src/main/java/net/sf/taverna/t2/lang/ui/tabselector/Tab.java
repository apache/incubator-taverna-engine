/*******************************************************************************
 * Copyright (C) 2012 The University of Manchester
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
package net.sf.taverna.t2.lang.ui.tabselector;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Tab button that includes a label and a close button.
 *
 * @author David Withers
 */
public abstract class Tab<T> extends JToggleButton {

	private static final long serialVersionUID = 1L;

	public final static Color midGrey = new Color(160,160,160);
	public final static Color lightGrey = new Color(200,200,200);

	protected final T selection;
	private String name;
	private Icon icon;
	private JLabel label;

	public Tab(String name, T selection) {
		this(name, null, selection);
	}

	public Tab(String name, Icon icon, T selection) {
		this.name = name;
		this.icon = icon;
		this.selection = selection;
		initialise();
	}

	private void initialise() {
		setUI(new BasicButtonUI());
		setLayout(new GridBagLayout());
		setOpaque(false);
		setBackground(Color.red);
		setBorder(null);

		GridBagConstraints c = new GridBagConstraints();

		label = new JLabel(this.name);
		label.setIcon(icon);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 5, 0, 5);
		c.weightx = 1;
		add(label, c);

		JButton button = new CloseWorkflowButton();
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 0, 0, 5);
		c.weightx = 0;
		add(button, c);

		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clickTabAction();
			}
		});
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (!this.name.equals(name)) {
			this.name = name;
			label.setText(name);
			repaint();
		}
	}

	public void setIcon(Icon icon) {
		label.setIcon(icon);
		repaint();
	}

	@Override
	public void updateUI() {
		// override to ignore UI update
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		if (getModel().isPressed()) {
			g2.translate(1, 1);
		}
		if (!getModel().isSelected()) {
			g2.setColor(lightGrey);
			g2.fillRoundRect(1, 0, getWidth() - 3, getHeight() - 1, 4, 10);
		}
		g2.setColor(midGrey);
		g2.drawRoundRect(1, 0, getWidth() - 3, getHeight(), 4, 10);
		if (getModel().isSelected()) {
			g2.setColor(getParent().getBackground());
			g2.drawLine(1, getHeight() - 1, getWidth() - 2, getHeight() - 1);
		}
		g2.dispose();
	}

	protected abstract void clickTabAction();

	protected abstract void closeTabAction();

	@SuppressWarnings("serial")
	private class CloseWorkflowButton extends JButton {

		private final int size = 15;
		private final int border = 4;

		public CloseWorkflowButton() {
			setUI(new BasicButtonUI());
			setPreferredSize(new Dimension(size, size));
			setMinimumSize(new Dimension(size, size));
			setContentAreaFilled(false);
			setFocusable(false);
			setToolTipText("Close workflow");

			setRolloverEnabled(true);
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					closeTabAction();
				}
			});
		}

		@Override
		public void updateUI() {
			// override to ignore UI update
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			// animate button press
			if (getModel().isPressed()) {
				g2.translate(1, 1);
			}
			g2.setColor(midGrey);
			if (getModel().isRollover()) {
				// draw armed button
				g2.fillRoundRect(0, 0, size - 1, size - 1, 4, 4);
				g2.setColor(Color.GRAY);
				g2.drawRoundRect(0, 0, size - 1, size - 1, 4, 4);
				g2.setColor(Color.RED);
			}
			// draw 'x'
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
			g2.drawLine(border, border, size - border, size - border);
			g2.drawLine(border, size - border, size - border, border);
			g2.dispose();
		}

	}

}
