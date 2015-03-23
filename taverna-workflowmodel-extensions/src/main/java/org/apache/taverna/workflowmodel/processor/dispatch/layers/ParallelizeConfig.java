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

/**
 * Bean to hold the configuration for the parallelize layer, specifically a
 * single int property defining the number of concurrent jobs in that processor
 * instance per owning process ID.
 * 
 * @author Tom Oinn
 */
@ConfigurationBean(uri = Parallelize.URI + "#Config")
public class ParallelizeConfig {
	private int maxJobs;

	public ParallelizeConfig() {
		super();
		this.maxJobs = 1;
	}

	@ConfigurationProperty(name = "maxJobs", label = "Maximum Parallel Jobs", description = "The maximum number of jobs that can run in parallel", required = false)
	public void setMaximumJobs(int maxJobs) {
		this.maxJobs = maxJobs;
	}

	public int getMaximumJobs() {
		return this.maxJobs;
	}
}
