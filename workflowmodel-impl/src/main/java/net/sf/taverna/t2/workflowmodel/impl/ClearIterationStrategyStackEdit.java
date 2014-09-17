/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester   
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
package net.sf.taverna.t2.workflowmodel.impl;

import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategyStack;
import net.sf.taverna.t2.workflowmodel.processor.iteration.impl.IterationStrategyStackImpl;

/**
 * Removes all the iteration strategies from an iteration strategy stack.
 * 
 * @author David Withers
 */
class ClearIterationStrategyStackEdit extends EditSupport<IterationStrategyStack> {
	private final IterationStrategyStackImpl iterationStrategyStack;

	public ClearIterationStrategyStackEdit(IterationStrategyStack iterationStrategyStack) {
		if (!(iterationStrategyStack instanceof IterationStrategyStackImpl))
			throw new RuntimeException(
					"Object being edited must be instance of IterationStrategyStackImpl");
		this.iterationStrategyStack = (IterationStrategyStackImpl) iterationStrategyStack;
	}

	@Override
	public IterationStrategyStack applyEdit() throws EditException {
		iterationStrategyStack.clear();
		return iterationStrategyStack;
	}

	@Override
	public IterationStrategyStack getSubject() {
		return iterationStrategyStack;
	}
}
