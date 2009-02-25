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

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;

/**
 * An implementation of the filtering input port interface used as an input for
 * a ProcessorImpl. If the filter level is undefined this input port will always
 * throw workflow structure exceptions when you push data into it. This port
 * must be linked to a crystalizer or something which offers the same
 * operational contract, it requires a full hierarchy of data tokens (i.e. if
 * you push something in with an index you must at some point subsequent to that
 * push at least a single list in with the empty index)
 * 
 * @author Tom Oinn
 * 
 */
public class ProcessorInputPortImpl extends AbstractFilteringInputPort implements
		ProcessorInputPort {

	private ProcessorImpl parent;

	protected ProcessorInputPortImpl(ProcessorImpl parent, String name,
			int depth) {
		super(name, depth);
		this.parent = parent;
	}

	@Override
	public String transformOwningProcess(String oldOwner) {
		return oldOwner + ":" + parent.getLocalName();
	}
	
	@Override
	protected void pushCompletion(String portName, String owningProcess, int[] index, InvocationContext context) {
		parent.iterationStack.receiveCompletion(portName, owningProcess, index, context);	
	}

	@Override
	protected void pushData(String portName, String owningProcess, int[] index, T2Reference data, InvocationContext context) {
		parent.iterationStack.receiveData(portName, owningProcess, index, data, context);
	}
	
	public Processor getProcessor() {
		return this.parent;
	}
	
}
