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

import java.util.Map;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.ProcessIdentifierException;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchMessageType;

/**
 * Dispatch event containing the results from an invocation. If the event is
 * part of a stream of such events from a single job invocation the streaming
 * flag will be set to true - when set layers that do not support streaming
 * should either disable any related functionality or complain bitterly. They
 * should never see such an event as the type checker will in the future catch
 * such cases before they occur but for now it's something to watch for.
 * 
 * @author Tom Oinn
 * 
 */
public class DispatchResultEvent extends
		AbstractDispatchEvent<DispatchResultEvent> {

	private Map<String, T2Reference> dataMap;
	private boolean streaming;

	/**
	 * Construct a new dispatch result event, specifying the data and whether
	 * the result is part of a stream of multiple results events from a single
	 * invocation
	 * 
	 * @param owner
	 * @param index
	 * @param context
	 * @param data
	 * @param streaming
	 */
	public DispatchResultEvent(String owner, int[] index,
			InvocationContext context, Map<String, T2Reference> data,
			boolean streaming) {
		super(owner, index, context);
		this.dataMap = data;
		this.streaming = streaming;
	}

	/**
	 * If this result is part of a stream, that is to say multiple result events
	 * from a single job event, then return true otherwise return false.
	 * 
	 * @return whether this is part of a streamed result set
	 */
	public boolean isStreamingEvent() {
		return this.streaming;
	}

	/**
	 * The result contains a map of named EntityIdentifier instances
	 * corresponding to the result data.
	 * 
	 * @return the result data for this event
	 */
	public Map<String, T2Reference> getData() {
		return this.dataMap;
	}

	@Override
	public DispatchResultEvent popOwningProcess()
			throws ProcessIdentifierException {
		return new DispatchResultEvent(popOwner(), index, context, dataMap,
				streaming);
	}

	@Override
	public DispatchResultEvent pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException {
		return new DispatchResultEvent(pushOwner(localProcessName), index,
				context, dataMap, streaming);
	}

	/**
	 * DispatchMessageType.RESULT
	 */
	@Override
	public DispatchMessageType getMessageType() {
		return DispatchMessageType.RESULT;
	}

}
