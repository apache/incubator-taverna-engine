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

import net.sf.taverna.t2.invocation.Event;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchMessageType;

/**
 * Superclass of events within the dispatch stack
 * 
 * @author Tom Oinn
 * 
 */
public abstract class AbstractDispatchEvent<EventType extends AbstractDispatchEvent<EventType>>
		extends Event<EventType> {

	protected AbstractDispatchEvent(String owner, int[] index,
			InvocationContext context) {
		super(owner, index, context);
	}

	/**
	 * Return the DispatchMessageType for this event object
	 * 
	 * @return instance of DispatchMessageType represented by this event
	 */
	public abstract DispatchMessageType getMessageType();

}
