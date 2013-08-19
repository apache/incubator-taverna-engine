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

import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationBean;
import net.sf.taverna.t2.workflowmodel.processor.config.ConfigurationProperty;

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
