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

import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType.JOB_QUEUE;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.IterationInternalEvent;
import org.apache.taverna.invocation.ProcessIdentifierException;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType;

/**
 * A message within the dispatch stack containing a single reference to the job
 * queue from the iteration system along with an ordered list of Activity
 * instances.
 * 
 * @author Tom Oinn
 */
public class DispatchJobQueueEvent extends
		AbstractDispatchEvent<DispatchJobQueueEvent> {
	private BlockingQueue<IterationInternalEvent<? extends IterationInternalEvent<?>>> queue;
	private List<? extends Activity<?>> activities;

	/**
	 * Create a new job queue event, specifying the queue of Completion and Job
	 * objects and the list of activities which will be used to process the
	 * corresponding dispatch events
	 * 
	 * @param owner
	 * @param context
	 * @param queue
	 * @param activities
	 */
	public DispatchJobQueueEvent(
			String owner,
			InvocationContext context,
			BlockingQueue<IterationInternalEvent<? extends IterationInternalEvent<?>>> queue,
			List<? extends Activity<?>> activities) {
		super(owner, new int[] {}, context);
		this.queue = queue;
		this.activities = activities;
	}

	public BlockingQueue<IterationInternalEvent<? extends IterationInternalEvent<?>>> getQueue() {
		return this.queue;
	}

	public List<? extends Activity<?>> getActivities() {
		return this.activities;
	}

	@Override
	public DispatchJobQueueEvent popOwningProcess()
			throws ProcessIdentifierException {
		return new DispatchJobQueueEvent(popOwner(), context, queue, activities);
	}

	@Override
	public DispatchJobQueueEvent pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException {
		return new DispatchJobQueueEvent(pushOwner(localProcessName), context,
				queue, activities);
	}

	/**
	 * @return Always a {@link DispatchMessageType#JOB_QUEUE}.
	 */
	@Override
	public DispatchMessageType getMessageType() {
		return JOB_QUEUE;
	}

}
