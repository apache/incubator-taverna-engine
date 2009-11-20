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
package net.sf.taverna.t2.workflowmodel.processor.dispatch.events;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.ProcessIdentifierException;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchMessageType;

/**
 * Dispatch event containing detailing a (potentially partial) completion of a
 * stream of streaming result events. Layers which do not support streaming by
 * definition can't cope with this event and the dispatch stack checker should
 * prevent them from ever seeing it.
 * 
 * @author Tom Oinn
 * 
 */
public class DispatchCompletionEvent extends
		AbstractDispatchEvent<DispatchCompletionEvent> {

	/**
	 * Construct a new dispatch result completion event
	 * 
	 * @param owner
	 * @param index
	 * @param context
	 */
	public DispatchCompletionEvent(String owner, int[] index,
			InvocationContext context) {
		super(owner, index, context);
	}

	@Override
	public DispatchCompletionEvent popOwningProcess()
			throws ProcessIdentifierException {
		return new DispatchCompletionEvent(popOwner(), index, context);
	}

	@Override
	public DispatchCompletionEvent pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException {
		return new DispatchCompletionEvent(pushOwner(localProcessName), index,
				context);
	}

	/**
	 * DispatchMessageType.RESULT_COMPLETION
	 */
	@Override
	public DispatchMessageType getMessageType() {
		return DispatchMessageType.RESULT_COMPLETION;
	}

}
