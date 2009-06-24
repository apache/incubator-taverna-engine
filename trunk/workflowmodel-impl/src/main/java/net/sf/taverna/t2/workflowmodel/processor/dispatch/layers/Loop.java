/*******************************************************************************
 * Copyright (C) 2008 The University of Manchester   
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
package net.sf.taverna.t2.workflowmodel.processor.dispatch.layers;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivityCallback;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.AbstractDispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.AbstractDispatchEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchCompletionEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchErrorEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchErrorType;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchJobQueueEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchResultEvent;

import org.apache.log4j.Logger;

/**
 * A layer that allows while-style loops.
 * <p>
 * The layer is configured with a {@link LoopConfiguration}, where an activity
 * has been set as the
 * {@link LoopConfiguration#setCondition(net.sf.taverna.t2.workflowmodel.processor.activity.Activity)
 * condition}.
 * </p>
 * <p>
 * After a job has been successful further down the dispatch stack, the loop
 * layer will invoke the conditional activity to determine if the job will be
 * invoked again. If {@link LoopConfiguration#isRunFirst()} is false, this test
 * will be performed even before the first invocation. (The default
 * runFirst=true is equivalent to a do..while construct, while runFirst=false is
 * equivalent to a while.. construct.)
 * </p>
 * <p>
 * A job will be resent down the dispatch stack only if the conditional activity
 * returns a reference to a string equal to "true" on its output port "loop".
 * </p>
 * <p>
 * If a job or the conditional activity fails, the while-loop is interrupted and
 * the error is sent further up.
 * </p>
 * <p>
 * Note that the LoopLayer will be invoked for each item in an iteration, if you
 * want to do the loop for the whole collection (ie. re-iterating if the
 * loop-condition fails after processing the full list) - create a nested
 * workflow with the desired depths on it's input ports and insert this
 * LoopLayer in the stack of the nested workflow's processor in parent workflow.
 * </p>
 * <p>
 * It is recommended that the LoopLayer is to be inserted after the
 * {@link ErrorBounce} layer, as this layer is needed for registering errors
 * produced by the LoopLayer. If the user requires {@link Retry retries} and
 * {@link Failover failovers} before checking the while condition, such layers
 * should be below LoopLayer.
 * </p>
 * 
 * @author Stian Soiland-Reyes
 * 
 */

@SuppressWarnings("unchecked")
public class Loop extends AbstractDispatchLayer<LoopConfiguration> {

	private static Logger logger = Logger.getLogger(Loop.class);

	private LoopConfiguration config = new LoopConfiguration();

	protected Map<String, AbstractDispatchEvent> incomingJobs = new HashMap<String, AbstractDispatchEvent>();

	protected Map<String, AbstractDispatchEvent> outgoingJobs = new HashMap<String, AbstractDispatchEvent>();

	public void configure(LoopConfiguration config) {
		this.config = config;
	}

	@Override
	public void finishedWith(String owningProcess) {
		String prefix = owningProcess + "[";
		synchronized (outgoingJobs) {
			for (String key : new ArrayList<String>(outgoingJobs.keySet())) {
				if (key.startsWith(prefix)) {
					outgoingJobs.remove(key);
				}
			}
		}
		synchronized (incomingJobs) {
			for (String key : new ArrayList<String>(incomingJobs.keySet())) {
				if (key.startsWith(prefix)) {
					incomingJobs.remove(key);
				}
			}
		}
	}

	public LoopConfiguration getConfiguration() {
		return config;
	}

	@Override
	public void receiveJob(DispatchJobEvent jobEvent) {
		synchronized (incomingJobs) {
			incomingJobs.put(jobIdentifier(jobEvent), jobEvent);
		}
		if (config.isRunFirst()) {
			// We'll do the conditional in receiveResult instead
			super.receiveJob(jobEvent);
			return;
		}
		checkCondition(jobEvent);
	}

	@Override
	public void receiveJobQueue(DispatchJobQueueEvent jobQueueEvent) {
		synchronized (incomingJobs) {
			incomingJobs.put(jobIdentifier(jobQueueEvent), jobQueueEvent);
		}
		if (config.isRunFirst()) {
			// We'll do the conditional in receiveResult instead
			super.receiveJobQueue(jobQueueEvent);
			return;
		}
		checkCondition(jobQueueEvent);
	}

	@Override
	public void receiveResult(DispatchResultEvent resultEvent) {
		Activity<?> condition = config.getCondition();
		if (condition == null) {
			super.receiveResult(resultEvent);
			return;
		}
		synchronized (outgoingJobs) {
			outgoingJobs.put(jobIdentifier(resultEvent), resultEvent);
		}
		checkCondition(resultEvent);
	}

	@Override
	public void receiveResultCompletion(DispatchCompletionEvent completionEvent) {
		Activity<?> condition = config.getCondition();
		if (condition == null) {
			super.receiveResultCompletion(completionEvent);
			return;
		}
		synchronized (outgoingJobs) {
			outgoingJobs.put(jobIdentifier(completionEvent), completionEvent);
		}
		checkCondition(completionEvent);
	}

	private void checkCondition(AbstractDispatchEvent event) {
		Activity<?> condition;
		condition = config.getCondition();
		if (condition == null) {
			super.receiveError(new DispatchErrorEvent(event.getOwningProcess(),
					event.getIndex(), event.getContext(),
					"Can't invoke conditional activity: null", null,
					DispatchErrorType.INVOCATION, condition));
			return;
		}
		if (!(condition instanceof AbstractAsynchronousActivity)) {
			DispatchErrorEvent errorEvent = new DispatchErrorEvent(
					event.getOwningProcess(),
					event.getIndex(),
					event.getContext(),
					"Can't invoke conditional activity "
							+ condition
							+ " is not an instance of AbstractAsynchronousActivity",
					null, DispatchErrorType.INVOCATION, condition);
			super.receiveError(errorEvent);
			return;
		}
		AbstractAsynchronousActivity asyncCondition = (AbstractAsynchronousActivity) condition;
		String jobIdentifier = jobIdentifier(event);
		Map<String, T2Reference> inputs = prepareInputs(asyncCondition,
				jobIdentifier);
		AsynchronousActivityCallback callback = new ConditionCallBack(
				jobIdentifier);
		asyncCondition.executeAsynch(inputs, callback);
	}

	private Map<String, T2Reference> prepareInputs(
			AbstractAsynchronousActivity asyncCondition, String jobIdentifier) {
		Map<String, T2Reference> inputs = new HashMap<String, T2Reference>();
		Map<String, T2Reference> inData = getInData(jobIdentifier);
		Map<String, T2Reference> outData = getOutData(jobIdentifier);

		Set<ActivityInputPort> inputPorts = asyncCondition.getInputPorts();
		for (ActivityInputPort conditionIn : inputPorts) {
			String conditionPort = conditionIn.getName();
			if (outData.containsKey(conditionPort)) {
				// Copy from previous output
				inputs.put(conditionPort, outData.get(conditionPort));
			} else if (inData.containsKey(conditionPort)) {
				// Copy from original input
				inputs.put(conditionPort, inData.get(conditionPort));
			}
		}
		return inputs;
	}

	private Map<String, T2Reference> getInData(String jobIdentifier) {
		AbstractDispatchEvent inEvent;
		synchronized (incomingJobs) {
			inEvent = incomingJobs.get(jobIdentifier);
		}
		Map<String, T2Reference> inData = new HashMap<String, T2Reference>();
		if (inEvent instanceof DispatchJobEvent) {
			inData = ((DispatchJobEvent) inEvent).getData();
		}
		return inData;
	}

	private Map<String, T2Reference> getOutData(String jobIdentifier) {
		AbstractDispatchEvent outEvent;
		synchronized (outgoingJobs) {
			outEvent = outgoingJobs.get(jobIdentifier);
		}
		Map<String, T2Reference> outData = new HashMap<String, T2Reference>();
		if (outEvent instanceof DispatchResultEvent) {
			outData = ((DispatchResultEvent) outEvent).getData();
		}
		return outData;
	}

	private String jobIdentifier(AbstractDispatchEvent event) {
		String jobId = event.getOwningProcess()
				+ Arrays.toString(event.getIndex());
		return jobId;
	}

	public static final String LOOP_PORT = "loop";
	
	
	public class ConditionCallBack implements AsynchronousActivityCallback {
		private InvocationContext context;
		private final String jobIdentifier;
		private String processId;

		public ConditionCallBack(String jobIdentifier) {
			this.jobIdentifier = jobIdentifier;
			AbstractDispatchEvent originalEvent;
			synchronized (incomingJobs) {
				originalEvent = incomingJobs.get(jobIdentifier);
			}
			context = originalEvent.getContext();
			processId = originalEvent.getOwningProcess() + ":condition";
		}

		public void fail(String message) {
			fail(message, null, DispatchErrorType.INVOCATION);
		}

		public void fail(String message, Throwable t) {
			fail(message, t, DispatchErrorType.INVOCATION);
		}

		public void fail(String message, Throwable t,
				DispatchErrorType errorType) {
			logger.warn("Failed (" + errorType + ") invoking conditional "
					+ jobIdentifier + ":" + message, t);

			AbstractDispatchEvent originalEvent;
			synchronized (incomingJobs) {
				originalEvent = incomingJobs.get(jobIdentifier);
			}
			receiveError(new DispatchErrorEvent(originalEvent
					.getOwningProcess(), originalEvent.getIndex(),
					originalEvent.getContext(),
					"Can't invoke conditional activity ", t,
					DispatchErrorType.INVOCATION, null));
		}

		public InvocationContext getContext() {
			return context;
		}

		public String getParentProcessIdentifier() {
			return processId;
		}

		public void receiveCompletion(int[] completionIndex) {
			// Ignore streaming
		}

		public void receiveResult(Map<String, T2Reference> data, int[] index) {
			if (index.length > 0) {
				// Ignore streaming
				return;
			}
			T2Reference loopRef = data.get(LOOP_PORT);
			if (loopRef == null) {
				fail("Conditional activity didn't contain output port " + LOOP_PORT);
				return;
			}
			if (loopRef.containsErrors()) {
				fail("Conditional activity failed: " + loopRef);
				return;
			}
			if (loopRef.getDepth() != 0) {
				fail("Conditional activity output " + LOOP_PORT
						+ " depth is not 0, but " + loopRef.getDepth());
			}
			ReferenceService referenceService = context.getReferenceService();
			String loop = (String) referenceService.renderIdentifier(loopRef,
					String.class, context);

			if (Boolean.parseBoolean(loop)) {
				// Push it down again
				AbstractDispatchEvent dispatchEvent;
				synchronized (incomingJobs) {
					dispatchEvent = incomingJobs.get(jobIdentifier);
				}
				if (dispatchEvent == null) {
					fail("Unknown job identifier " + jobIdentifier);
				}
				if (dispatchEvent instanceof DispatchJobEvent) {
					DispatchJobEvent newJobEvent = prepareNewJobEvent(data,
							dispatchEvent);
					getBelow().receiveJob(newJobEvent);
				} else if (dispatchEvent instanceof DispatchJobQueueEvent) {
					getBelow().receiveJobQueue(
							(DispatchJobQueueEvent) dispatchEvent);
				} else {
					fail("Unknown type of incoming event " + dispatchEvent);
				}
				return;

			} else {
				// We'll push it up, end of loop for now

				AbstractDispatchEvent outgoingEvent;
				synchronized (outgoingJobs) {
					outgoingEvent = outgoingJobs.get(jobIdentifier);
				}
				if (outgoingEvent == null && !config.isRunFirst()) {
					fail("Initial loop condition failed");
				}
				if (outgoingEvent instanceof DispatchCompletionEvent) {
					getAbove().receiveResultCompletion(
							(DispatchCompletionEvent) outgoingEvent);
				} else if (outgoingEvent instanceof DispatchResultEvent) {
					getAbove().receiveResult(
							(DispatchResultEvent) outgoingEvent);
				} else {
					fail("Unknown type of outgoing event " + outgoingEvent);
				}
			}

		}

		private DispatchJobEvent prepareNewJobEvent(
				Map<String, T2Reference> data,
				AbstractDispatchEvent dispatchEvent) {
			DispatchJobEvent dispatchJobEvent = (DispatchJobEvent) dispatchEvent;
			Map<String, T2Reference> newInputs = new HashMap<String, T2Reference>(
					dispatchJobEvent.getData());
			newInputs.putAll(data);
			DispatchJobEvent newJobEvent = new DispatchJobEvent(dispatchEvent
					.getOwningProcess(), dispatchEvent.getIndex(),
					dispatchEvent.getContext(), newInputs,
					((DispatchJobEvent) dispatchEvent).getActivities());
			// TODO: Should this be registered as an incomingJobs? If so the
			// conditional
			// could even feed to itself, and we should also keep a list of
			// originalJobs.
			return newJobEvent;
		}

		public void requestRun(Runnable runMe) {
			String newThreadName = "Conditional "
					+ getParentProcessIdentifier();
			Thread thread = new Thread(runMe, newThreadName);
			thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				public void uncaughtException(Thread t, Throwable e) {
					fail("Uncaught exception while invoking " + jobIdentifier,
							e);
				}
			});
			thread.start();
		}
	}

	public Processor getProcessor() {
		if (dispatchStack == null) {
			return null;
		}
		return dispatchStack.getProcessor();
	}

}
