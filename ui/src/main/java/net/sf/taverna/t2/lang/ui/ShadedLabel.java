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
/**
 * This file is a component of the Taverna project,
 * and is licensed under the GNU LGPL.
 * Copyright Tom Oinn, EMBL-EBI
 */
package net.sf.taverna.t2.lang.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * A JLabel like component with a shaded background
 * 
 * @author Tom Oinn
 */
public class ShadedLabel extends JPanel {

	// If you change these, please make sure BLUE is really blue, etc.. :-)

	public static Color ORANGE = new Color(238, 206, 143);

	public static Color BLUE = new Color(213, 229, 246);

	public static Color GREEN = new Color(161, 198, 157);

	final JLabel label;

	Color colour;

	Color toColour = Color.WHITE;

	private String text;

	/**
	 * Create a ShadedLabel blending from the specified colour to white (left to
	 * right) and with the specified text.
	 */
	public ShadedLabel(String text, Color colour) {
		this(text, colour, false);
	}

	/**
	 * Create a ShadedLabel blending from the specified colour to either white
	 * if halfShade is false or to a colour halfway between the specified colour
	 * and white if true and with the specified text
	 */
	public ShadedLabel(String text, Color colour, boolean halfShade) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 3));

		if (halfShade) {
			toColour = halfShade(colour);
		}
		label = new JLabel("",
				SwingConstants.LEFT);
		setText(text);
		label.setOpaque(false);
		add(Box.createHorizontalStrut(5));
		add(label);
		add(Box.createHorizontalStrut(5));
		setOpaque(false);
		this.colour = colour;
	}

	public void setText(String text) {
		this.text = text;
		label.setText("<html><body><b>" + text + "</b></body></html>");
		invalidate();
	}
	
	public String getText() {
		return text;
	}

	@Override
	protected void paintComponent(Graphics g) {
		final int width = getWidth();
		final int height = getHeight();
		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();
		g2d.setPaint(new GradientPaint(0, 0, this.colour, width, height,
				toColour));
		g2d.fillRect(0, 0, width, height);
		g2d.setPaint(oldPaint);
		super.paintComponent(g);
	}

	public static Color halfShade(Color colour) {
		return new Color((colour.getRed() + 510) / 3,
				(colour.getGreen() + 510) / 3, (colour.getBlue() + 510) / 3);
	}

	@Override
	public boolean isFocusable() {
		return false;
	}

}
