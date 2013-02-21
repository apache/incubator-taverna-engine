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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Component for selecting objects using tabs.
 *
 * @author David Withers
 */
public abstract class TabSelectorComponent<T> extends JPanel {

	private static final long serialVersionUID = 1L;

	private Map<T, Tab<T>> tabMap;
	private ButtonGroup tabGroup;
	private JComponent tabBar;
	private ScrollController scrollController;

	public TabSelectorComponent(Component component) {
		tabMap = new HashMap<T, Tab<T>>();
		tabGroup = new ButtonGroup();
		setLayout(new BorderLayout());
		tabBar = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setColor(Tab.midGrey);
				g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
				g2.dispose();
			}
		};
		scrollController = new ScrollController(tabBar);
		tabBar.add(scrollController.getScrollLeft());
		tabBar.add(scrollController.getScrollRight());
		tabBar.setLayout(new TabLayout(scrollController));
		add(tabBar, BorderLayout.NORTH);
		add(component, BorderLayout.CENTER);
	}

	protected abstract Tab<T> createTab(T object);

	public void addObject(T object) {
		Tab<T> button = createTab(object);
		tabMap.put(object, button);
		tabGroup.add(button);
		tabBar.add(button);
		button.setSelected(true);
	}

	public void removeObject(T object) {
		Tab<T> button = tabMap.remove(object);
		if (button != null) {
			tabGroup.remove(button);
			tabBar.remove(button);
			tabBar.repaint();
		}
	}

	public void selectObject(T object) {
		Tab<T> button = tabMap.get(object);
		if (button != null) {
			button.setSelected(true);
		} else {
			addObject(object);
		}
	}

}
