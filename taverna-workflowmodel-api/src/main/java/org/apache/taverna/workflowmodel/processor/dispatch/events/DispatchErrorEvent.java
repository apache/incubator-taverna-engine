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

import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType.ERROR;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.ProcessIdentifierException;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType;

/**
 * Message within the dispatch stack representing a single error report. This
 * may then be handled by upstream layers to retry jobs etc. If it reaches the
 * top of the dispatch stack the behaviour is configurable but by default it
 * will abort that workflow instance, being treated as a catastrophic
 * unhandleable problem.
 * 
 * @author Tom Oinn
 */
public class DispatchErrorEvent extends
		AbstractDispatchEvent<DispatchErrorEvent> {
	private Throwable cause;
	private String message;
	private DispatchErrorType failureType;
	private Activity<?> failedActivity;

	/**
	 * Create a new error event
	 * 
	 * @param owningProcess
	 * @param index
	 * @param context
	 * @param errorMessage
	 * @param t
	 */
	public DispatchErrorEvent(String owningProcess, int[] index,
			InvocationContext context, String errorMessage, Throwable t,
			DispatchErrorType failureType, Activity<?> failedActivity) {
		super(owningProcess, index, context);
		this.message = errorMessage;
		this.cause = t;
		this.failureType = failureType;
		this.failedActivity = failedActivity;
	}

	/**
	 * Return the type of failure, this is used by upstream dispatch layers to
	 * determine whether they can reasonably handle the error message
	 */
	public DispatchErrorType getFailureType() {
		return this.failureType;
	}

	/**
	 * Return the Activity instance which failed to produce this error message
	 */
	public Activity<?> getFailedActivity() {
		return this.failedActivity;
	}

	/**
	 * Return the throwable behind this error, or null if there was no exception
	 * raised to create it.
	 * 
	 * @return
	 */
	public Throwable getCause() {
		return this.cause;
	}

	/**
	 * Return the textual message representing this error
	 * 
	 * @return
	 */
	public String getMessage() {
		return this.message;
	}

	@Override
	public DispatchErrorEvent popOwningProcess()
			throws ProcessIdentifierException {
		return new DispatchErrorEvent(popOwner(), index, context, message,
				cause, failureType, failedActivity);
	}

	@Override
	public DispatchErrorEvent pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException {
		return new DispatchErrorEvent(pushOwner(localProcessName), index,
				context, message, cause, failureType, failedActivity);
	}

	/**
	 * @return Always a {@link DispatchMessageType#ERROR}
	 */
	@Override
	public DispatchMessageType getMessageType() {
		return ERROR;
	}
}
