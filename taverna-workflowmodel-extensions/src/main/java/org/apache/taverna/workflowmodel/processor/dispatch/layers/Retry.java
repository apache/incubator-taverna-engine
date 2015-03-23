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

import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.CREATE_LOCAL_STATE;
import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.REMOVE_LOCAL_STATE;
import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.UPDATE_LOCAL_STATE;
import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType.JOB;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.taverna.workflowmodel.processor.dispatch.AbstractErrorHandlerLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerErrorReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerJobReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchLayerResultReaction;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchJobEvent;

/**
 * Implements retry policy with delay between retries and exponential backoff
 * <p>
 * Default properties are as follows :
 * <ul>
 * <li>maxRetries = 0 (int)</li>
 * <li>initialDelay = 1000 (milliseconds)</li>
 * <li>maxDelay = 2000 (milliseconds)</li>
 * <li>backoffFactor = 1.0 (double)</li>
 * </ul>
 *
 * @author Tom Oinn
 * @author David Withers
 * @author Stian Soiland-Reyes
 */
@DispatchLayerErrorReaction(emits = { JOB }, relaysUnmodified = true, stateEffects = {
		UPDATE_LOCAL_STATE, REMOVE_LOCAL_STATE })
@DispatchLayerJobReaction(emits = {}, relaysUnmodified = true, stateEffects = { CREATE_LOCAL_STATE })
@DispatchLayerResultReaction(emits = {}, relaysUnmodified = true, stateEffects = { REMOVE_LOCAL_STATE })
public class Retry extends AbstractErrorHandlerLayer<JsonNode> {
	private static final String BACKOFF_FACTOR = "backoffFactor";
    private static final String MAX_DELAY = "maxDelay";
    private static final String MAX_RETRIES = "maxRetries";
    private static final String INITIAL_DELAY = "initialDelay";
    public static final String URI = "http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Retry";

	private ObjectNode config;
    private int maxRetries;
    private int initialDelay;
    private int maxDelay;
    private double backoffFactor;

	private static Timer retryTimer = new Timer("Retry timer", true);

	public Retry() {
		super();
		configure(JsonNodeFactory.instance.objectNode());
	}

	public Retry(int maxRetries, int initialDelay, int maxDelay,
			double backoffFactor) {
		super();
		ObjectNode conf = JsonNodeFactory.instance.objectNode();
		conf.put(MAX_RETRIES, maxRetries);
		conf.put(INITIAL_DELAY, initialDelay);
		conf.put(MAX_DELAY, maxDelay);
		conf.put(BACKOFF_FACTOR, backoffFactor);
		configure(conf);
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
			if (currentRetryCount >= maxRetries)
				return false;
			int delay = (int) (initialDelay * Math.pow(backoffFactor, currentRetryCount));
			delay = Math.min(delay, maxDelay);
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

	@Override
	public void configure(JsonNode config) {
	    ObjectNode defaultConfig = defaultConfig();
        setAllMissingFields((ObjectNode) config, defaultConfig);
        checkConfig((ObjectNode)config);
        this.config = (ObjectNode) config;
        maxRetries = config.get(MAX_RETRIES).intValue();
        initialDelay = config.get(INITIAL_DELAY).intValue();
        maxDelay = config.get(MAX_DELAY).intValue();
        backoffFactor = config.get(BACKOFF_FACTOR).doubleValue();       
	}

    private void setAllMissingFields(ObjectNode config, ObjectNode defaults) {
        for (String fieldName : forEach(defaults.fieldNames()))
	        if (! config.has(fieldName) || config.get(fieldName).isNull())
	            config.put(fieldName, defaults.get(fieldName));
    }

	private <T> Iterable<T> forEach(final Iterator<T> iterator) {
	    return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }

    private void checkConfig(ObjectNode conf) {
        if (conf.get(MAX_RETRIES).intValue() < 0)
            throw new IllegalArgumentException("maxRetries < 0");
        if (conf.get(INITIAL_DELAY).intValue() < 0)
            throw new IllegalArgumentException("initialDelay < 0");
        if (conf.get(MAX_DELAY).intValue() < conf.get(INITIAL_DELAY).intValue())
            throw new IllegalArgumentException("maxDelay < initialDelay");
        if (conf.get(BACKOFF_FACTOR).doubleValue() < 0.0)
            throw new IllegalArgumentException("backoffFactor < 0.0");
    }

    public static ObjectNode defaultConfig() {
	    ObjectNode conf = JsonNodeFactory.instance.objectNode();
	    conf.put(MAX_RETRIES, 0);
	    conf.put(INITIAL_DELAY, 1000);
	    conf.put(MAX_DELAY, 5000);
	    conf.put(BACKOFF_FACTOR, 1.0);
	    return conf;
    }

    @Override
	public JsonNode getConfiguration() {
		return this.config;
	}
}
