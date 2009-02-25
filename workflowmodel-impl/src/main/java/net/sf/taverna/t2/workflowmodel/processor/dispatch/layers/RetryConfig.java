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

public class RetryConfig {

	private float backoffFactor = 1f;
	private int initialDelay = 1000;
	private int maxDelay = 2000;
	private int maxRetries = 0;

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

	public void setBackoffFactor(float factor) {
		this.backoffFactor = factor;
	}

	public void setInitialDelay(int delay) {
		this.initialDelay = delay;
	}

	public void setMaxDelay(int delay) {
		this.maxDelay = delay;
	}

	public void setMaxRetries(int max) {
		this.maxRetries = max;
	}


}
