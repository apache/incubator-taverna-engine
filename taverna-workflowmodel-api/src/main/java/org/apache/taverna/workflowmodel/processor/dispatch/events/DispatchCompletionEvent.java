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

import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType.RESULT_COMPLETION;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.ProcessIdentifierException;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType;

/**
 * Dispatch event containing detailing a (potentially partial) completion of a
 * stream of streaming result events. Layers which do not support streaming by
 * definition can't cope with this event and the dispatch stack checker should
 * prevent them from ever seeing it.
 * 
 * @author Tom Oinn
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
		return RESULT_COMPLETION;
	}
}
