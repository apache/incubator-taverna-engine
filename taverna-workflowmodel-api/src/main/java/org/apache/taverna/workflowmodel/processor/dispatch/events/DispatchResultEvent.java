/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.workflowmodel.processor.dispatch.events;

import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType.RESULT;

import java.util.Map;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.ProcessIdentifierException;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType;

/**
 * Dispatch event containing the results from an invocation. If the event is
 * part of a stream of such events from a single job invocation the streaming
 * flag will be set to true - when set layers that do not support streaming
 * should either disable any related functionality or complain bitterly. They
 * should never see such an event as the type checker will in the future catch
 * such cases before they occur but for now it's something to watch for.
 * 
 * @author Tom Oinn
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
	 * @return Always a {@link DispatchMessageType#RESULT}.
	 */
	@Override
	public DispatchMessageType getMessageType() {
		return RESULT;
	}

}
