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

import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.processor.iteration.impl.IterationStrategyImpl;

import org.apache.log4j.Logger;

public class RemoveProcessorInputPortEdit extends AbstractProcessorEdit {

	private final ProcessorInputPort port;
	private static Logger logger = Logger
			.getLogger(RemoveProcessorInputPortEdit.class);

	public RemoveProcessorInputPortEdit(Processor processor, ProcessorInputPort port) {
		super(processor);
		this.port = port;
	}
	
	@Override
	protected void doEditAction(ProcessorImpl processor) throws EditException {
		if (processor.getInputPortWithName(port.getName())==null) throw new EditException("The processor doesn't have a port named:"+port.getName());
		for (IterationStrategyImpl is : processor.iterationStack.getStrategies()) {
			is.removeInputByName(port.getName());
		}
		processor.inputPorts.remove(port);
	}

	@Override
	protected void undoEditAction(ProcessorImpl processor) {
		try {
			new EditsImpl().getAddProcessorInputPortEdit(processor, port).doEdit();
		} catch (EditException e) {
			logger.error("There was an error adding an input port to a Processor whilst undoing a remove");
		}
	}
	
	

}
