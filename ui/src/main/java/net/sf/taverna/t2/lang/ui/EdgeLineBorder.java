/*******************************************************************************
 * Copyright (C) 2013 The University of Manchester
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
package net.sf.taverna.t2.lang.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 *
 *
 * @author David Withers
 */
public class EdgeLineBorder implements Border {

	public static final int TOP = 1;
	public static final int BOTTOM = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	private final int edge;
	private final Color color;

	public EdgeLineBorder(int edge, Color color) {
		this.edge = edge;
		this.color = color;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Color oldColor = g.getColor();
		g.setColor(color);
		switch (edge) {
		case TOP:
			g.drawLine(x, y, x+width, y);
			break;
		case BOTTOM:
			g.drawLine(x, y+height-2, x+width, y+height-2);
			break;
		case LEFT:
			g.drawLine(x, y, x+width, y+height);
			break;
		case RIGHT:
			g.drawLine(x+width, y, x+width, y+height);
			break;
		}
		g.setColor(oldColor);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 0, 0);
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(500, 500);
		JPanel panel = new JPanel();
		panel.setBorder(new EdgeLineBorder(TOP, Color.GRAY));
		frame.add(panel);
		frame.setVisible(true);
	}
}
