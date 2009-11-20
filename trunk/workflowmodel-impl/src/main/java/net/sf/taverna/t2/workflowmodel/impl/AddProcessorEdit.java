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
package net.sf.taverna.t2.workflowmodel.impl;

import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Processor;

/**
 * An Edit class responsible for add a Processor to the dataflow.
 * 
 * @author Stuart Owen
 *
 */
public class AddProcessorEdit extends AbstractDataflowEdit{
	
	private Processor processor;
	
	public Processor getProcessor() {
		return processor;
	}

	protected AddProcessorEdit(Dataflow dataflow, Processor processor) {
		super(dataflow);
		this.processor=processor;
	}

	/**
	 * Adds the Processor instance to the Dataflow
	 * 
	 * @throws EditException if the edit has already taken place (without an intermediate undo) or a processor with that name already exists.
	 */
	@Override
	protected void doEditAction(DataflowImpl dataflow) throws EditException {
		if (processor instanceof ProcessorImpl) {
			dataflow.addProcessor((ProcessorImpl)processor);
		}
		else {
			throw new EditException("The Processor is of the wrong implmentation, it should be of type ProcessorImpl");
		}
	}

	@Override
	protected void undoEditAction(DataflowImpl dataflow) {
		dataflow.removeProcessor(processor);
	}
}
