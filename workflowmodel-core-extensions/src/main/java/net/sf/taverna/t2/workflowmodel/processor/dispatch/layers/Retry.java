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
package net.sf.taverna.t2.workflowmodel.processor.dispatch.layers;

import static net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.CREATE_LOCAL_STATE;
import static net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.REMOVE_LOCAL_STATE;
import static net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.UPDATE_LOCAL_STATE;
import static net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchMessageType.JOB;

import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.sf.taverna.t2.workflowmodel.processor.dispatch.AbstractErrorHandlerLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchLayerErrorReaction;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchLayerJobReaction;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchLayerResultReaction;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchJobEvent;

/**
 * Implements retry policy with delay between retries and exponential backoff
 * <p>
 * Default properties are as follows :
 * <ul>
 * <li>maxRetries = 0 (int)</li>
 * <li>initialDelay = 1000 (milliseconds)</li>
 * <li>maxDelay = 2000 (milliseconds)</li>
 * <li>backoffFactor = 1.0 (float)</li>
 * </ul>
 *
 * @author Tom Oinn
 *
 */
@DispatchLayerErrorReaction(emits = { JOB }, relaysUnmodified = true, stateEffects = {
		UPDATE_LOCAL_STATE, REMOVE_LOCAL_STATE })
@DispatchLayerJobReaction(emits = {}, relaysUnmodified = true, stateEffects = { CREATE_LOCAL_STATE })
@DispatchLayerResultReaction(emits = {}, relaysUnmodified = true, stateEffects = { REMOVE_LOCAL_STATE })
public class Retry extends AbstractErrorHandlerLayer<JsonNode> {

	public static final String URI = "http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Retry";

	private JsonNode config = JsonNodeFactory.instance.objectNode();

	private static Timer retryTimer = new Timer("Retry timer", true);

	public Retry() {
		super();
	}

	public Retry(int maxRetries, int initialDelay, int maxDelay,
			float backoffFactor) {
		super();
		((ObjectNode) config).put("maxRetries", maxRetries);
		((ObjectNode) config).put("initialDelay", initialDelay);
		((ObjectNode) config).put("maxDelay", maxDelay);
		((ObjectNode) config).put("backoffFactor", backoffFactor);
	}

	class RetryState extends JobState {

		int currentRetryCount = 0;

		public RetryState(DispatchJobEvent jobEvent) {
			super(jobEvent);
		}

		/**
		 * Try to schedule a retry, returns true if a retry is scheduled, false
		 * if the retry count has already been reached (in which case no retry
		 * is scheduled
		 *
		 * @return
		 */
		@Override
		public boolean handleError() {
			if (currentRetryCount == config.get("maxRetries").intValue()) {
				return false;
			}
			int delay = (int) (config.get("initialDelay").intValue() * (Math.pow(config.get("backoffFactor").doubleValue(), currentRetryCount)));
			if (delay > config.get("maxRetries").intValue()) {
				delay = config.get("maxRetries").intValue();
			}
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					currentRetryCount++;
					getBelow().receiveJob(jobEvent);
				}

			};
			retryTimer.schedule(task, delay);
			return true;
		}

	}

	@Override
	protected JobState getStateObject(DispatchJobEvent jobEvent) {
		return new RetryState(jobEvent);
	}

	public void configure(JsonNode config) {
		this.config = config;
	}

	public JsonNode getConfiguration() {
		return this.config;
	}
}
