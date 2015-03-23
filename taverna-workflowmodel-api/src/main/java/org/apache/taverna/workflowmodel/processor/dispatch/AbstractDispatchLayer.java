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

import java.util.Timer;

import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchCompletionEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchErrorEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobQueueEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchResultEvent;

/**
 * Convenience abstract implementation of DispatchLayer
 * 
 * @author Tom Oinn
 */
public abstract class AbstractDispatchLayer<ConfigurationType> implements
		DispatchLayer<ConfigurationType> {
	protected static Timer cleanupTimer = new Timer(
			"Dispatch stack state cleanup", true);
	protected static final int CLEANUP_DELAY_MS = 1000;

	@Override
	public void setDispatchStack(DispatchStack parentStack) {
		this.dispatchStack = parentStack;
	}

	protected DispatchStack dispatchStack;

	protected final DispatchLayer<?> getAbove() {
		return dispatchStack.layerAbove(this);
	}

	protected final DispatchLayer<?> getBelow() {
		return dispatchStack.layerBelow(this);
	}

	@Override
	public void receiveError(DispatchErrorEvent errorEvent) {
		DispatchLayer<?> above = dispatchStack.layerAbove(this);
		if (above != null)
			above.receiveError(errorEvent);
	}

	@Override
	public void receiveJob(DispatchJobEvent jobEvent) {
		DispatchLayer<?> below = dispatchStack.layerBelow(this);
		if (below != null)
			below.receiveJob(jobEvent);
	}

	@Override
	public void receiveJobQueue(DispatchJobQueueEvent jobQueueEvent) {
		DispatchLayer<?> below = dispatchStack.layerBelow(this);
		if (below != null)
			below.receiveJobQueue(jobQueueEvent);
	}

	@Override
	public void receiveResult(DispatchResultEvent resultEvent) {
		DispatchLayer<?> above = dispatchStack.layerAbove(this);
		if (above != null)
			above.receiveResult(resultEvent);
	}

	@Override
	public void receiveResultCompletion(DispatchCompletionEvent completionEvent) {
		DispatchLayer<?> above = dispatchStack.layerAbove(this);
		if (above != null)
			above.receiveResultCompletion(completionEvent);
	}

	@Override
	public void finishedWith(String owningProcess) {
		// Do nothing by default
	}

	public Processor getProcessor() {
		if (dispatchStack == null)
			return null;
		return dispatchStack.getProcessor();
	}
}
