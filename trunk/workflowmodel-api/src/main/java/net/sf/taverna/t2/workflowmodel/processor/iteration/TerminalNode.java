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
package net.sf.taverna.t2.workflowmodel.processor.iteration;

import javax.swing.tree.MutableTreeNode;

/**
 * The terminal node is the root of the iteration strategy tree, it is
 * responsible for forwarding all events up to the iteration strategy itself
 * which can then propogate them to the strategy stack.
 */
public abstract class TerminalNode extends AbstractIterationStrategyNode {
	@Override
	public synchronized void insert(MutableTreeNode child, int index) {
		if (getChildCount() > 0 && getChildAt(0) != child) {
			throw new IllegalStateException(
					"The terminal node can have maximum one child");
		}
		super.insert(child, index);
	}
}
