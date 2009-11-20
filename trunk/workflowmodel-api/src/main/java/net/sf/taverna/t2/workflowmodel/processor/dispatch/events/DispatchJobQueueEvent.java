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

import java.util.List;
import java.util.concurrent.BlockingQueue;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.IterationInternalEvent;
import net.sf.taverna.t2.invocation.ProcessIdentifierException;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchMessageType;

/**
 * A message within the dispatch stack containing a single reference to the job
 * queue from the iteration system along with an ordered list of Activity
 * instances.
 * 
 * @author Tom Oinn
 * 
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
	public DispatchJobQueueEvent(String owner, InvocationContext context,
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
	 * DispatchMessageType.JOB_QUEUE
	 */
	@Override
	public DispatchMessageType getMessageType() {
		return DispatchMessageType.JOB_QUEUE;
	}

}
