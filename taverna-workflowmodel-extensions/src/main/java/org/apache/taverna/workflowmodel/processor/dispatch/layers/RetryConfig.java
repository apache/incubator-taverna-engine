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

import org.apache.taverna.workflowmodel.processor.config.ConfigurationBean;
import org.apache.taverna.workflowmodel.processor.config.ConfigurationProperty;

@ConfigurationBean(uri = Retry.URI + "#Config")
public class RetryConfig {
	private static final float BACKOFF_FACTOR = 1.0f;
	private static final int MAX_DELAY = 5000;
	private static final int INITIAL_DELAY = 1000;
	private static final int MAX_RETRIES = 0;

	private float backoffFactor = BACKOFF_FACTOR;
	private int initialDelay = INITIAL_DELAY;
	private int maxDelay = MAX_DELAY;
	private int maxRetries = MAX_RETRIES;

	/**
	 * Factor by which the initial delay is multiplied for each retry after the
	 * first, this allows for exponential backoff of retry times up to a certain
	 * ceiling
	 *
	 * @return
	 */
	public float getBackoffFactor() {
		return this.backoffFactor;
	}

	/**
	 * Delay in milliseconds between the initial failure message and the first
	 * attempt to retry the failed job
	 *
	 * @return
	 */
	public int getInitialDelay() {
		return this.initialDelay;
	}

	/**
	 * Maximum delay in milliseconds between failure reception and retry. This
	 * acts as a ceiling for the exponential backoff factor allowing the retry
	 * delay to initially increase to a certain value then remain constant after
	 * that point rather than exploding to unreasonable levels.
	 */
	public int getMaxDelay() {
		return this.maxDelay;
	}

	/**
	 * Maximum number of retries for a failing process
	 *
	 * @return
	 */
	public int getMaxRetries() {
		return this.maxRetries;
	}

	@ConfigurationProperty(name = "backoffFactor", label = "Backoff Factor", description = "Factor by which the initial delay is multiplied for each retry after the first retry", required=false)
	public void setBackoffFactor(float factor) {
		this.backoffFactor = factor;
	}

	@ConfigurationProperty(name = "initialDelay", label = "Initial Delay", description = "Delay in milliseconds between the initial failure message and the first attempt to retry the failed job", required=false)
	public void setInitialDelay(int delay) {
		this.initialDelay = delay;
	}

	@ConfigurationProperty(name = "maxDelay", label = "Maximum Delay", description = "Maximum delay in milliseconds between failure reception and retry", required=false)
	public void setMaxDelay(int delay) {
		this.maxDelay = delay;
	}

	@ConfigurationProperty(name = "maxRetries", label = "Maximum Retries", description = "Maximum number of retries for a failing process", required=false)
	public void setMaxRetries(int max) {
		this.maxRetries = max;
	}
}
