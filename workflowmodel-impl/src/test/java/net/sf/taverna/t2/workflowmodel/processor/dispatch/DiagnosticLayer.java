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

import net.sf.taverna.t2.invocation.Completion;
import net.sf.taverna.t2.workflowmodel.processor.activity.Job;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchCompletionEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchResultEvent;

/**
 * Debug dispatch stack layer, prints to stdout when it receives a result or
 * completion and when the cache purge message is sent from the parent dispatch
 * stack.
 * 
 * @author Tom
 * 
 */
public class DiagnosticLayer extends AbstractDispatchLayer<Object> {

	public DiagnosticLayer() {
		super();
	}

	@Override
	public void receiveResult(DispatchResultEvent resultEvent) {
		System.out.println("  "
				+ new Job(resultEvent.getOwningProcess(), resultEvent
						.getIndex(), resultEvent.getData(), resultEvent
						.getContext()));
		getAbove().receiveResult(resultEvent);
	}

	@Override
	public void receiveResultCompletion(DispatchCompletionEvent completionEvent) {
		System.out.println("  "
				+ new Completion(completionEvent.getOwningProcess(),
						completionEvent.getIndex(), completionEvent
								.getContext()));
		getAbove().receiveResultCompletion(completionEvent);
	}

	@Override
	public void finishedWith(String process) {
		System.out.println("  Purging caches for " + process);
	}

	public void configure(Object config) {
		// Do nothing
	}

	public Object getConfiguration() {
		return null;
	}

}
