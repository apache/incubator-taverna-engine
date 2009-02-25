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

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.facade.ResultListener;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;

public class DataflowOutputPortImpl extends BasicEventForwardingOutputPort
		implements DataflowOutputPort {

	protected AbstractEventHandlingInputPort internalInput;
	protected List<ResultListener> resultListeners = new ArrayList<ResultListener>();

	private Dataflow dataflow;

	DataflowOutputPortImpl(final String portName, final Dataflow dataflow) {
		super(portName, -1, -1);
		this.dataflow = dataflow;
		this.internalInput = new AbstractEventHandlingInputPort(name, -1) {
			/**
			 * Forward the event through the output port Also informs any
			 * ResultListeners on the output port to the new token.
			 */
			public void receiveEvent(WorkflowDataToken token) {
				// Pull the dataflow process identifier from the owning process
				// and push the modified token out
				// I'd rather avoid casting to the implementation but in this
				// case we're in the same package - the only reason to do this
				// is to allow dummy implementations of parts of this
				// infrastructure during testing, in 'real' use this should
				// always be a dataflowimpl
				if (token.getIndex().length == 0
						&& dataflow instanceof DataflowImpl) {
					((DataflowImpl) dataflow).sentFinalToken(portName, token
							.getOwningProcess());
				}
				WorkflowDataToken newToken = token.popOwningProcess();
				sendEvent(newToken);
				for (ResultListener listener : resultListeners
						.toArray(new ResultListener[] {})) {
					listener.resultTokenProduced(newToken, this.getName());
				}
			}

			/**
			 * Always copy the value of the enclosing dataflow output port
			 */
			@Override
			public int getDepth() {
				return DataflowOutputPortImpl.this.getDepth();
			}
		};
	}

	public EventHandlingInputPort getInternalInputPort() {
		return this.internalInput;
	}

	public Dataflow getDataflow() {
		return this.dataflow;
	}

	void setDepths(int depth, int granularDepth) {
		this.depth = depth;
		this.granularDepth = granularDepth;
	}

	public void addResultListener(ResultListener listener) {
		resultListeners.add(listener);
	}

	public void removeResultListener(ResultListener listener) {
		resultListeners.remove(listener);
	}
	
	public void setName(String newName) {
		this.name = newName;
		internalInput.setName(newName);
	}
	
}
