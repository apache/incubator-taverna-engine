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
package net.sf.taverna.t2.workflowmodel.processor.dispatch;

import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchCompletionEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchErrorEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchJobQueueEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchResultEvent;

/**
 * Convenience abstract implementation of DispatchLayer
 * 
 * @author Tom Oinn
 * 
 */
public abstract class AbstractDispatchLayer<ConfigurationType> implements
		DispatchLayer<ConfigurationType> {

	public void setDispatchStack(DispatchStack parentStack) {
		this.dispatchStack = parentStack;
	}

	protected DispatchStack dispatchStack;

	protected final DispatchLayer<?> getAbove() {
		return this.dispatchStack.layerAbove(this);
	}

	protected final DispatchLayer<?> getBelow() {
		return this.dispatchStack.layerBelow(this);
	}

	public void receiveError(DispatchErrorEvent errorEvent) {
		DispatchLayer<?> above = dispatchStack.layerAbove(this);
		if (above != null) {
			above.receiveError(errorEvent);
		}
	}

	@SuppressWarnings("unchecked")
	public void receiveJob(DispatchJobEvent jobEvent) {
		DispatchLayer<?> below = dispatchStack.layerBelow(this);
		if (below != null) {
			below.receiveJob(jobEvent);
		}
	}

	@SuppressWarnings("unchecked")
	public void receiveJobQueue(DispatchJobQueueEvent jobQueueEvent) {
		DispatchLayer below = dispatchStack.layerBelow(this);
		if (below != null) {
			below.receiveJobQueue(jobQueueEvent);
		}

	}

	public void receiveResult(DispatchResultEvent resultEvent) {
		DispatchLayer<?> above = dispatchStack.layerAbove(this);
		if (above != null) {
			above.receiveResult(resultEvent);
		}
	}

	public void receiveResultCompletion(DispatchCompletionEvent completionEvent) {
		DispatchLayer<?> above = dispatchStack.layerAbove(this);
		if (above != null) {
			above.receiveResultCompletion(completionEvent);
		}

	}

	public void finishedWith(String owningProcess) {
		// Do nothing by default
	}

}
