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

/**
 * Abstraction of an edit acting on a Processor instance. Handles the check to
 * see that the Processor supplied is really a ProcessorImpl.
 * 
 * @author Tom Oinn
 * 
 */
public abstract class AbstractProcessorEdit extends EditSupport<Processor> {
	private ProcessorImpl processor;

	protected AbstractProcessorEdit(Processor p) {
		if (p == null)
			throw new RuntimeException(
					"Cannot construct a processor edit with null processor");
		if (!(processor instanceof ProcessorImpl))
			throw new RuntimeException(
					"Edit cannot be applied to a Processor which isn't an instance of ProcessorImpl");
		this.processor = (ProcessorImpl) p;
	}

	@Override
	public final Processor applyEdit() throws EditException {
		synchronized (processor) {
			doEditAction(processor);
			return processor;
		}
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param processor
	 *            The ProcessorImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(ProcessorImpl processor)
			throws EditException;

	@Override
	public final Processor getSubject() {
		return processor;
	}
}
