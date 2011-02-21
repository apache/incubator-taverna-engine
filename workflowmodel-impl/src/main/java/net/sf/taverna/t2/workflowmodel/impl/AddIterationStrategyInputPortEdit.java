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

import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategy;
import net.sf.taverna.t2.workflowmodel.processor.iteration.NamedInputPortNode;
import net.sf.taverna.t2.workflowmodel.processor.iteration.impl.IterationStrategyImpl;

/**
 * Adds an iteration strategy input port node to an iteration strategy.
 * 
 * @author David Withers
 */
public class AddIterationStrategyInputPortEdit implements Edit<IterationStrategy> {

	private final IterationStrategy iterationStrategy;
	private final NamedInputPortNode namedInputPortNode;
	private boolean applied;

	public AddIterationStrategyInputPortEdit(IterationStrategy iterationStrategy,
			NamedInputPortNode namedInputPortNode) {
		this.iterationStrategy = iterationStrategy;
		this.namedInputPortNode = namedInputPortNode;
	}

	@Override
	public IterationStrategy doEdit() throws EditException {
		if (applied) {
			throw new EditException("Edit has already been applied");
		}
		if (!(iterationStrategy instanceof IterationStrategyImpl)) {
			throw new EditException(
					"Object being edited must be instance of IterationStrategyImpl");
		}
		((IterationStrategyImpl) iterationStrategy).addInput(namedInputPortNode);
		applied = true;
		return iterationStrategy;
	}

	@Override
	public void undo() {
		if (!applied) {
			throw new RuntimeException(
					"Attempt to undo edit that was never applied");
		}
		((IterationStrategyImpl) iterationStrategy).removeInput(namedInputPortNode);
		applied = false;
	}

	@Override
	public IterationStrategy getSubject() {
		return iterationStrategy;
	}

	@Override
	public boolean isApplied() {
		return applied;
	}

}
