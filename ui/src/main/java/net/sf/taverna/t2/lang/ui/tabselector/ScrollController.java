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
package net.sf.taverna.t2.lang.ui.tabselector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * Controls tab scrolling when there is not enough space to show all the tabs.
 *
 * @author David Withers
 */
public class ScrollController {

	private int position;
	private final JButton scrollLeft;
	private final JButton scrollRight;

	public ScrollController(final JComponent component) {
		scrollLeft = new JButton("<");
		scrollRight = new JButton(">");
		scrollLeft.setOpaque(true);
		scrollRight.setOpaque(true);
		scrollLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				increment();
				component.doLayout();
			}
		});
		scrollRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				decrement();
				component.doLayout();
			}
		});
	}

	public JButton getScrollLeft() {
		return scrollLeft;
	}

	public JButton getScrollRight() {
		return scrollRight;
	}

	public int getPosition() {
		return position;
	}

	public void reset() {
		position = 0;
	}

	public void increment() {
		position++;
	}

	public void decrement() {
		position--;
	}

}
