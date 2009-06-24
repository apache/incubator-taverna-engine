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
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;

public class DataflowInputPortImpl extends AbstractEventHandlingInputPort
		implements DataflowInputPort {

	protected BasicEventForwardingOutputPort internalOutput;

	private int granularInputDepth;

	private Dataflow dataflow;

	DataflowInputPortImpl(String name, int depth, int granularDepth, Dataflow df) {
		super(name, depth);
		granularInputDepth = granularDepth;
		dataflow = df;
		internalOutput = new BasicEventForwardingOutputPort(name, depth,
				granularDepth);
	}

	public int getGranularInputDepth() {
		return granularInputDepth;
	}

	void setDepth(int depth) {
		this.depth = depth;
		internalOutput.setDepth(depth);
	}
	
	void setGranularDepth(int granularDepth) {
		this.granularInputDepth = granularDepth;
		internalOutput.setGranularDepth(granularDepth);
	}
	
	public EventForwardingOutputPort getInternalOutputPort() {
		return internalOutput;
	}

	/**
	 * Receive an input event, relay it through the internal output port to all
	 * connected entities
	 */
	public void receiveEvent(WorkflowDataToken t) {
		WorkflowDataToken transformedToken = t.pushOwningProcess(dataflow.getLocalName());
		// I'd rather avoid casting to the implementation but in this
		// case we're in the same package - the only reason to do this
		// is to allow dummy implementations of parts of this
		// infrastructure during testing, in 'real' use this should
		// always be a dataflowimpl
		if (dataflow instanceof DataflowImpl) {
			((DataflowImpl) dataflow).tokenReceived(transformedToken
					.getOwningProcess(), t.getContext());
		}
		for (Datalink dl : internalOutput.getOutgoingLinks()) {
			dl.getSink().receiveEvent(transformedToken);
		}
	}

	public Dataflow getDataflow() {
		return dataflow;
	}
	
	public void setName(String newName) {
		this.name = newName;
		internalOutput.setName(newName);
	}
	
}
