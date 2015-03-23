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

package org.apache.taverna.workflowmodel.processor.dispatch.layers;

import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.CREATE_PROCESS_STATE;
import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.NO_EFFECT;
import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.REMOVE_PROCESS_STATE;
import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType.JOB;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.taverna.invocation.Completion;
import org.apache.taverna.invocation.IterationInternalEvent;
import org.apache.taverna.monitor.MonitorManager;
import org.apache.taverna.monitor.MonitorableProperty;
import org.apache.taverna.monitor.NoSuchPropertyException;
import org.apache.taverna.workflowmodel.WorkflowStructureException;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.activity.Job;
import org.apache.taverna.workflowmodel.processor.dispatch.AbstractDispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.NotifiableLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.PropertyContributingDispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerErrorReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerJobQueueReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerResultCompletionReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerResultReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.SupportsStreamedResult;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchCompletionEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchErrorEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobQueueEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchResultEvent;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Dispatch layer which consumes a queue of events and fires off a fixed number
 * of simultaneous jobs to the layer below. It observes failure, data and
 * completion events coming up and uses these to determine when to push more
 * jobs downwards into the stack as well as when it can safely emit completion
 * events from the queue.
 *
 * @author Tom Oinn
 *
 */
@DispatchLayerErrorReaction(emits = {}, relaysUnmodified = true, stateEffects = {
		REMOVE_PROCESS_STATE, NO_EFFECT })
@DispatchLayerJobQueueReaction(emits = { JOB }, relaysUnmodified = false, stateEffects = { CREATE_PROCESS_STATE })
@DispatchLayerResultReaction(emits = {}, relaysUnmodified = true, stateEffects = {
		REMOVE_PROCESS_STATE, NO_EFFECT })
@DispatchLayerResultCompletionReaction(emits = {}, relaysUnmodified = true, stateEffects = {
		REMOVE_PROCESS_STATE, NO_EFFECT })
@SupportsStreamedResult
public class Parallelize extends AbstractDispatchLayer<JsonNode>
		implements NotifiableLayer,
		PropertyContributingDispatchLayer<JsonNode> {
	public static final String URI = "http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Parallelize";
	private static Logger logger = Logger.getLogger(Parallelize.class);

	private Map<String, StateModel> stateMap = new HashMap<>();
	private JsonNode config = JsonNodeFactory.instance.objectNode();
	int sentJobsCount = 0;
	int completedJobsCount = 0;

	public Parallelize() {
		super();
	}

	/**
	 * Test constructor, only used by unit tests, should probably not be public
	 * access here?
	 *
	 * @param maxJobs
	 */
	public Parallelize(int maxJobs) {
		super();
		((ObjectNode)config).put("maxJobs", maxJobs);
	}

	@Override
	public void eventAdded(String owningProcess) {
		StateModel stateModel;
		synchronized (stateMap) {
			stateModel = stateMap.get(owningProcess);
		}
		if (stateModel == null)
			/*
			 * Should never see this here, it means we've had duplicate
			 * completion events from upstream
			 */
			throw new WorkflowStructureException(
					"Unknown owning process " + owningProcess);
		synchronized (stateModel) {
			stateModel.fillFromQueue();
		}
	}

	@Override
	public void receiveJobQueue(DispatchJobQueueEvent queueEvent) {
		StateModel model = new StateModel(queueEvent,
				config.has("maxJobs") ? config.get("maxJobs").intValue() : 1);
		synchronized (stateMap) {
			stateMap.put(queueEvent.getOwningProcess(), model);
		}
		model.fillFromQueue();
	}

	public void receiveJob(Job job, List<? extends Activity<?>> activities) {
		throw new WorkflowStructureException(
				"Parallelize layer cannot handle job events");
	}

	@Override
	public void receiveError(DispatchErrorEvent errorEvent) {
		StateModel model;
		String owningProcess = errorEvent.getOwningProcess();
		synchronized(stateMap) {
			model = stateMap.get(owningProcess);
		}
		if (model == null) {
			logger.warn("Error received for unknown owning process: " + owningProcess);
			return;
		}
		model.finishWith(errorEvent.getIndex());
		getAbove().receiveError(errorEvent);
	}

	@Override
	public void receiveResult(DispatchResultEvent resultEvent) {
		StateModel model;
		String owningProcess = resultEvent.getOwningProcess();
		synchronized(stateMap) {
			model = stateMap.get(owningProcess);
		}
		if (model == null) {
			logger.warn("Error received for unknown owning process: " + owningProcess);
			return;
		}
		if (!resultEvent.isStreamingEvent()) {
			MonitorManager.getInstance().registerNode(resultEvent,
					owningProcess,
					new HashSet<MonitorableProperty<?>>());
		}
		model.finishWith(resultEvent.getIndex());
		getAbove().receiveResult(resultEvent);
	}

	/**
	 * Only going to receive this if the activity invocation was streaming, in
	 * which case we need to handle all completion events and pass them up the
	 * stack.
	 */
	@Override
	public void receiveResultCompletion(DispatchCompletionEvent completionEvent) {
		StateModel model;
		String owningProcess = completionEvent.getOwningProcess();
		synchronized(stateMap) {
			model = stateMap.get(owningProcess);
		}
		if (model == null) {
			logger.warn("Error received for unknown owning process: " + owningProcess);
			return;
		}
		model.finishWith(completionEvent.getIndex());
		getAbove().receiveResultCompletion(completionEvent);
	}

	@Override
	public void finishedWith(final String owningProcess) {
		// Delay the removal of the state to give the monitor a chance to poll
		cleanupTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				synchronized(stateMap) {
					stateMap.remove(owningProcess);
				}
			}
		}, CLEANUP_DELAY_MS);
	}

	@Override
	public void configure(JsonNode config) {
		this.config = config;
	}

	@Override
	public JsonNode getConfiguration() {
		return this.config;
	}

	/**
	 * Injects the following properties into its parent processor's property set:
	 * <ul>
	 * <li><code>dispatch.parallelize.queuesize [Integer]</code><br/>The current
	 * size of the incomming job queue, or -1 if the state isn't defined for the
	 * registered process identifier (which will be the case if the process
	 * hasn't started or has had its state purged after a final completion of
	 * some kind.</li>
	 * </ul>
	 */
	@Override
	public void injectPropertiesFor(final String owningProcess) {
		/**
		 * Property for the queue depth, will evaluate to -1 if there isn't a
		 * queue in the state model for this identifier (which will be the case
		 * if we haven't created the state yet or the queue has been collected)
		 */
		MonitorableProperty<Integer> queueSizeProperty = new MonitorableProperty<Integer>() {
			@Override
			public Date getLastModified() {
				return new Date();
			}

			@Override
			public String[] getName() {
				return new String[] { "dispatch", "parallelize", "queuesize" };
			}

			@Override
			public Integer getValue() throws NoSuchPropertyException {
				StateModel model;
				synchronized(stateMap) {
					model = stateMap.get(owningProcess);
				}
				if (model == null)
					return -1;
				return model.queueSize();
			}
		};
		dispatchStack.receiveMonitorableProperty(queueSizeProperty,
				owningProcess);

		MonitorableProperty<Integer> sentJobsProperty = new MonitorableProperty<Integer>() {
			@Override
			public Date getLastModified() {
				return new Date();
			}

			@Override
			public String[] getName() {
				return new String[] { "dispatch", "parallelize", "sentjobs" };
			}

			@Override
			public Integer getValue() throws NoSuchPropertyException {
				return sentJobsCount;
			}
		};
		dispatchStack.receiveMonitorableProperty(sentJobsProperty,
				owningProcess);

		MonitorableProperty<Integer> completedJobsProperty = new MonitorableProperty<Integer>() {
			@Override
			public Date getLastModified() {
				return new Date();
			}

			@Override
			public String[] getName() {
				return new String[] { "dispatch", "parallelize",
						"completedjobs" };
			}

			@Override
			public Integer getValue() throws NoSuchPropertyException {
				return completedJobsCount;
			}
		};
		dispatchStack.receiveMonitorableProperty(completedJobsProperty,
				owningProcess);
	}

	/**
	 * Holds the state for a given owning process
	 *
	 * @author Tom Oinn
	 *
	 */
	// suppressed to avoid jdk1.5 error messages caused by the declaration
	// IterationInternalEvent<? extends IterationInternalEvent<?>> e
	@SuppressWarnings("rawtypes")
	class StateModel {
		private DispatchJobQueueEvent queueEvent;
		private BlockingQueue<IterationInternalEvent> pendingEvents = new LinkedBlockingQueue<>();
		private int activeJobs = 0;
		private int maximumJobs;

		/**
		 * Construct state model for a particular owning process
		 *
		 * @param owningProcess
		 *            Process to track parallel execution
		 * @param queue
		 *            reference to the queue into which jobs are inserted by the
		 *            iteration strategy
		 * @param activities
		 *            activities to pass along with job events down into the
		 *            stack below
		 * @param maxJobs
		 *            maximum number of concurrent jobs to keep 'hot' at any
		 *            given point
		 */
		protected StateModel(DispatchJobQueueEvent queueEvent, int maxJobs) {
			this.queueEvent = queueEvent;
			this.maximumJobs = maxJobs;
		}

		Integer queueSize() {
			return queueEvent.getQueue().size();
		}

		/**
		 * Poll the queue repeatedly until either the queue is empty or we have
		 * enough jobs pulled from it. The semantics for this are:
		 * <ul>
		 * <li>If the head of the queue is a Job and activeJobs < maximumJobs
		 * then increment activeJobs, add the Job to the pending events list at
		 * the end and send the message down the stack
		 * <li>If the head of the queue is a Completion and the pending jobs
		 * list is empty then send it to the layer above
		 * <li>If the head of the queue is a Completion and the pending jobs
		 * list is not empty then add the Completion to the end of the pending
		 * jobs list and return
		 * </ul>
		 */
		protected void fillFromQueue() {
			synchronized (this) {
				while (queueEvent.getQueue().peek() != null
						&& activeJobs < maximumJobs) {
					final IterationInternalEvent e = queueEvent.getQueue()
							.remove();

					if (e instanceof Completion && pendingEvents.peek() == null) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								getAbove().receiveResultCompletion(
										new DispatchCompletionEvent(e
												.getOwningProcess(), e
												.getIndex(), e.getContext()));
							}
						}, "Parallelize " + e.getOwningProcess()).start();
						// getAbove().receiveResultCompletion((Completion) e);
					} else {
						pendingEvents.add(e);
					}
					if (e instanceof Job) {
						synchronized (this) {
							activeJobs++;
						}
						sentJobsCount++;

						DispatchJobEvent dispatchJobEvent = new DispatchJobEvent(e
								.getOwningProcess(), e
								.getIndex(), e.getContext(),
								((Job) e).getData(), queueEvent
										.getActivities());
						// Register with the monitor
						MonitorManager.getInstance().registerNode(dispatchJobEvent,
								e.getOwningProcess(),
								new HashSet<MonitorableProperty<?>>());

						getBelow().receiveJob(dispatchJobEvent);
					}
				}
			}
		}

		/**
		 * Returns true if the index matched an existing Job exactly, if this
		 * method returns false then you have a partial completion event which
		 * should be sent up the stack without modification.
		 *
		 * @param index
		 * @return
		 */
		protected boolean finishWith(int[] index) {
			synchronized (this) {
				for (IterationInternalEvent e : new ArrayList<>(pendingEvents)) {
					if (!(e instanceof Job))
						continue;
					Job j = (Job) e;
					if (!arrayEquals(j.getIndex(), index))
						continue;

					/*
					 * Found a job in the pending events list which has the
					 * same index, remove it and decrement the current count
					 * of active jobs
					 */
					pendingEvents.remove(e);
					activeJobs--;
					completedJobsCount++;
					/*
					 * Now pull any completion events that have reached the head
					 * of the queue - this indicates that all the job events
					 * which came in before them have been processed and we can
					 * emit the completions
					 */
					while (pendingEvents.peek() != null
							&& pendingEvents.peek() instanceof Completion) {
						Completion c = (Completion) pendingEvents.remove();
						getAbove().receiveResultCompletion(
								new DispatchCompletionEvent(c
										.getOwningProcess(), c.getIndex(), c
										.getContext()));
					}
					/*
					 * Refresh from the queue; as we've just decremented the
					 * active job count there should be a worker available
					 */
					fillFromQueue();
					/*
					 * Return true to indicate that we removed a job event from
					 * the queue, that is to say that the index wasn't that of a
					 * partial completion.
					 */
					return true;
				}
			}
			return false;
		}

		private boolean arrayEquals(int[] a, int[] b) {
			if (a.length != b.length)
				return false;
			for (int i = 0; i < a.length; i++)
				if (a[i] != b[i])
					return false;
			return true;
		}
	}
}
