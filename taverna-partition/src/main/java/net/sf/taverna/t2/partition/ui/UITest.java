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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;

import net.sf.taverna.t2.partition.PartitionAlgorithm;
import net.sf.taverna.t2.partition.algorithms.LiteralValuePartitionAlgorithm;

public class UITest extends JFrame {

	private static final long serialVersionUID = -734851883737477053L;

	public UITest() {
		super();
		getContentPane().setLayout(new BorderLayout());
		List<PartitionAlgorithm<?>> paList = new ArrayList<PartitionAlgorithm<?>>();
		paList.add(new LiteralValuePartitionAlgorithm());
		paList.add(new LiteralValuePartitionAlgorithm());
		paList.add(new LiteralValuePartitionAlgorithm());
		PartitionAlgorithmListEditor pale = new PartitionAlgorithmListEditor(paList);
		getContentPane().add(pale, BorderLayout.NORTH);
		setVisible(true);
		
	}
	
	public static void main(String[] args) {
		JLabel l = new JLabel("Foo");
		System.out.println(l.getPreferredSize());
		System.out.println(l.getWidth());
		
		new UITest();
	}
	
}
