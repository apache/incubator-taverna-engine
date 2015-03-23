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
import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.UPDATE_PROCESS_STATE;
import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType.RESULT;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.taverna.invocation.Event;
import org.apache.taverna.monitor.MonitorableProperty;
import org.apache.taverna.monitor.NoSuchPropertyException;
import org.apache.taverna.reference.ErrorDocument;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.OutputPort;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.processor.dispatch.AbstractDispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.PropertyContributingDispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerErrorReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerJobReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerResultCompletionReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerResultReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.SupportsStreamedResult;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchErrorEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchResultEvent;

/**
 * Receives job events, checks to see whether any parameters in the job are
 * error tokens or collections which contain errors. If so then sends a
 * corresponding result message back where all outputs are error tokens having
 * registered such with the invocation context's data manager. It also re-writes
 * any failure messages as result messages containing error tokens at the
 * appropriate depth - this means that it must be placed above any error
 * handling layers in order for those to have an effect at all. In general this
 * layer should be placed immediately below the parallelize layer in most
 * default cases (this will guarantee the processor never sees a failure message
 * though, which may or may not be desirable)
 * 
 * @author Tom Oinn
 * 
 */
@DispatchLayerErrorReaction(emits = { RESULT }, relaysUnmodified = false, stateEffects = {
		CREATE_PROCESS_STATE, UPDATE_PROCESS_STATE })
@DispatchLayerJobReaction(emits = { RESULT }, relaysUnmodified = true, stateEffects = {
		CREATE_PROCESS_STATE, UPDATE_PROCESS_STATE, NO_EFFECT })
@DispatchLayerResultReaction(emits = {}, relaysUnmodified = true, stateEffects = {})
@DispatchLayerResultCompletionReaction(emits = {}, relaysUnmodified = true, stateEffects = {})
@SupportsStreamedResult
public class ErrorBounce extends AbstractDispatchLayer<JsonNode> implements
		PropertyContributingDispatchLayer<JsonNode> {
	public static final String URI = "http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/ErrorBounce";

	/**
	 * Track the number of reflected and translated errors handled by this error
	 * bounce instance
	 */
	private Map<String, ErrorBounceState> state = new ConcurrentHashMap<>();

	private int totalTranslatedErrors = 0;
	private int totalReflectedErrors = 0;

	private synchronized ErrorBounceState getState(String owningProcess) {
		if (state.containsKey(owningProcess))
			return state.get(owningProcess);
		ErrorBounceState ebs = new ErrorBounceState();
		state.put(owningProcess, ebs);
		return ebs;
	}

	/**
	 * If the job contains errors, or collections which contain errors
	 * themselves then bounce a result message with error documents in back up
	 * to the layer above
	 */
	@Override
	public void receiveJob(DispatchJobEvent jobEvent) {
		Set<T2Reference> errorReferences = new HashSet<>();
		for (T2Reference ei : jobEvent.getData().values())
			if (ei.containsErrors())
				errorReferences.add(ei);
		if (errorReferences.isEmpty())
			// relay the message down...
			getBelow().receiveJob(jobEvent);
		else {
			getState(jobEvent.getOwningProcess()).incrementErrorsReflected();
			sendErrorOutput(jobEvent, null, errorReferences);
		}
	}

	/**
	 * Always send the error document job result on receiving a failure, at
	 * least for now! This should be configurable, in effect this is the part
	 * that ensures the processor never sees a top level failure.
	 */
	@Override
	public void receiveError(DispatchErrorEvent errorEvent) {
		getState(errorEvent.getOwningProcess()).incrementErrorsTranslated();
		sendErrorOutput(errorEvent, errorEvent.getCause(), null);
	}

	/**
	 * Construct and send a new result message with error documents in place of
	 * all outputs at the appropriate depth
	 * 
	 * @param event
	 * @param cause
	 * @param errorReferences
	 */
	private void sendErrorOutput(Event<?> event, Throwable cause,
			Set<T2Reference> errorReferences) {
		ReferenceService rs = event.getContext().getReferenceService();

		Processor p = dispatchStack.getProcessor();
		Map<String, T2Reference> outputDataMap = new HashMap<>();
		String[] owningProcessArray = event.getOwningProcess().split(":");
		String processor = owningProcessArray[owningProcessArray.length - 1];
		for (OutputPort op : p.getOutputPorts()) {
			String message = "Processor '" + processor + "' - Port '"
					+ op.getName() + "'";
			if (event instanceof DispatchErrorEvent)
				message += ": " + ((DispatchErrorEvent) event).getMessage();
			ErrorDocument ed;
			if (cause != null)
				ed = rs.getErrorDocumentService().registerError(message, cause,
						op.getDepth(), event.getContext());
			else
				ed = rs.getErrorDocumentService().registerError(message,
						errorReferences, op.getDepth(), event.getContext());
			outputDataMap.put(op.getName(), ed.getId());
		}
		DispatchResultEvent dre = new DispatchResultEvent(
				event.getOwningProcess(), event.getIndex(), event.getContext(),
				outputDataMap, false);
		getAbove().receiveResult(dre);
	}

	@Override
	public void configure(JsonNode config) {
		// Do nothing - no configuration required
	}

	@Override
	public JsonNode getConfiguration() {
		// Layer has no configuration associated
		return null;
	}

	@Override
	public void finishedWith(final String owningProcess) {
		/*
		 * Delay the removal of the state to give the monitor a chance to poll
		 */
		cleanupTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				state.remove(owningProcess);
			}
		}, CLEANUP_DELAY_MS);
	}

	/**
	 * Two properties, dispatch.errorbounce.reflected(integer) is the number of
	 * incoming jobs which have been bounced back as results with errors,
	 * dispatch.errorbounce.translated(integer) is the number of failures from
	 * downstream in the stack that have been re-written as complete results
	 * containing error documents.
	 */
	@Override
	public void injectPropertiesFor(final String owningProcess) {
		MonitorableProperty<Integer> errorsReflectedProperty = new MonitorableProperty<Integer>() {
			@Override
			public Date getLastModified() {
				return new Date();
			}

			@Override
			public String[] getName() {
				return new String[] { "dispatch", "errorbounce", "reflected" };
			}

			@Override
			public Integer getValue() throws NoSuchPropertyException {
				ErrorBounceState ebs = state.get(owningProcess);
				if (ebs == null)
					return 0;
				return ebs.getErrorsReflected();
			}
		};
		dispatchStack.receiveMonitorableProperty(errorsReflectedProperty,
				owningProcess);

		MonitorableProperty<Integer> errorsTranslatedProperty = new MonitorableProperty<Integer>() {
			@Override
			public Date getLastModified() {
				return new Date();
			}

			@Override
			public String[] getName() {
				return new String[] { "dispatch", "errorbounce", "translated" };
			}

			@Override
			public Integer getValue() throws NoSuchPropertyException {
				ErrorBounceState ebs = state.get(owningProcess);
				if (ebs == null)
					return 0;
				return ebs.getErrorsTranslated();
			}
		};
		dispatchStack.receiveMonitorableProperty(errorsTranslatedProperty,
				owningProcess);

		MonitorableProperty<Integer> totalTranslatedTranslatedProperty = new MonitorableProperty<Integer>() {
			@Override
			public Date getLastModified() {
				return new Date();
			}

			@Override
			public String[] getName() {
				return new String[] { "dispatch", "errorbounce",
						"totalTranslated" };
			}

			@Override
			public Integer getValue() throws NoSuchPropertyException {
				return totalTranslatedErrors;
			}
		};
		dispatchStack.receiveMonitorableProperty(
				totalTranslatedTranslatedProperty, owningProcess);

		MonitorableProperty<Integer> totalReflectedTranslatedProperty = new MonitorableProperty<Integer>() {
			@Override
			public Date getLastModified() {
				return new Date();
			}

			@Override
			public String[] getName() {
				return new String[] { "dispatch", "errorbounce",
						"totalReflected" };
			}

			@Override
			public Integer getValue() throws NoSuchPropertyException {
				return totalReflectedErrors;
			}
		};
		dispatchStack.receiveMonitorableProperty(
				totalReflectedTranslatedProperty, owningProcess);
	}

	public class ErrorBounceState {
		private int errorsReflected = 0;
		private int errorsTranslated = 0;

		/**
		 * Number of times the bounce layer has converted an incoming job event
		 * where the input data contained error tokens into a result event
		 * containing all errors.
		 */
		int getErrorsReflected() {
			return this.errorsReflected;
		}

		/**
		 * Number of times the bounce layer has converted an incoming failure
		 * event into a result containing error tokens
		 */
		int getErrorsTranslated() {
			return errorsTranslated;
		}

		void incrementErrorsReflected() {
			synchronized (this) {
				errorsReflected++;
			}
			synchronized (ErrorBounce.this) {
				totalReflectedErrors++;
			}
		}

		void incrementErrorsTranslated() {
			synchronized (this) {
				errorsTranslated++;
			}
			synchronized (ErrorBounce.this) {
				totalTranslatedErrors++;
			}
		}
	}
}
