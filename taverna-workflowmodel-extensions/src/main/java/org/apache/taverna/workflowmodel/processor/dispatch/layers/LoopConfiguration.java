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

import java.util.Properties;

import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.config.ConfigurationBean;
import org.apache.taverna.workflowmodel.processor.config.ConfigurationProperty;

/**
 * Configuration bean for the {@link Loop}.
 * <p>
 * Set the {@link #setCondition(Activity)} for an activity with an output port
 * called "loop". The LoopLayer will re-send a job only if this port exist and
 * it's output can be dereferenced to a string equal to "true".
 * </p>
 * <p>
 * If {@link #isRunFirst()} is false, the loop layer will check the condition
 * before invoking the job for the first time, otherwise the condition will be
 * invoked after the job has come back with successful results.
 * </p>
 * 
 * @author Stian Soiland-Reyes
 * 
 */
@ConfigurationBean(uri = Loop.URI + "#Config")
public class LoopConfiguration implements Cloneable {
	private Activity<?> condition = null;
	private Boolean runFirst;
	private Properties properties;

	public Properties getProperties() {
		synchronized (this) {
			if (properties == null)
				properties = new Properties();
		}
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public LoopConfiguration clone() {
		LoopConfiguration clone;
		try {
			clone = (LoopConfiguration) super.clone();
			clone.condition = null;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unexpected CloneNotSupportedException",
					e);
		}
		return clone;
	}

	public Activity<?> getCondition() {
		return condition;
	}

	public boolean isRunFirst() {
		if (runFirst == null)
			return true;
		return runFirst;
	}

	@ConfigurationProperty(name = "condition", label = "Condition Activity", description = "The condition activity with an output port called \"loop\"", required = false)
	public void setCondition(Activity<?> activity) {
		this.condition = activity;
	}

	@ConfigurationProperty(name = "runFirst", label = "Check Condition On Run First", description = "Whether to check the condition before invoking the job for the first time", required = false)
	public void setRunFirst(boolean runFirst) {
		this.runFirst = runFirst;
	}
}
