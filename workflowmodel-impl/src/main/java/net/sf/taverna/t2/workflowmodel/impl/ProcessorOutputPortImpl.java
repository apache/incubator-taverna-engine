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

import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;

/**
 * Extension of AbstractOutputPort for use as the output port on a
 * ProcessorImpl. Contains additional logic to relay workflow data tokens from
 * the internal crystalizer to each in a set of target FilteringInputPort
 * instances.
 * 
 * @author Tom Oinn
 * @author Stuart Owen
 * 
 */
public class ProcessorOutputPortImpl extends BasicEventForwardingOutputPort implements ProcessorOutputPort{

	private ProcessorImpl parent = null;
	
	protected ProcessorOutputPortImpl(ProcessorImpl parent,String portName, int portDepth,
			int granularDepth) {
		super(portName, portDepth, granularDepth);
		this.parent = parent;
	}

	/**
	 * Strip off the last id in the owning process stack (as this will have been
	 * pushed onto the stack on entry to the processor) and relay the event to
	 * the targets.
	 * 
	 */
	protected void receiveEvent(WorkflowDataToken token) {
		sendEvent(token.popOwningProcess());
	}
	
	public Processor getProcessor() {
		return this.parent;
	}
	
}
