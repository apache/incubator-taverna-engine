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

package org.apache.taverna.workflowmodel.processor.dispatch;

import org.apache.taverna.invocation.Completion;
import org.apache.taverna.workflowmodel.processor.activity.Job;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchCompletionEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchResultEvent;

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

	@Override
	public void configure(Object config) {
		// Do nothing
	}

	@Override
	public Object getConfiguration() {
		return null;
	}

}
