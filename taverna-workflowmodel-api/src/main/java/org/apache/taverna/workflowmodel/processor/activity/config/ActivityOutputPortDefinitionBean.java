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

package org.apache.taverna.workflowmodel.processor.activity.config;

import org.apache.taverna.workflowmodel.OutputPort;
import org.apache.taverna.workflowmodel.processor.config.ConfigurationBean;

/**
 * A bean that describes properties of an Output port.
 * 
 * @author Stuart Owen
 */
@ConfigurationBean(uri = "http://ns.taverna.org.uk/2010/scufl2#OutputPortDefinition")
public class ActivityOutputPortDefinitionBean extends ActivityPortDefinitionBean {
	private int granularDepth;

	/**
	 * @return the granular depth of the port
	 * @see OutputPort#getGranularDepth()
	 */
	public int getGranularDepth() {
		return granularDepth;
	}

	/**
	 * @param granularDepth the granular depth of the port
	 */
	public void setGranularDepth(int granularDepth) {
		this.granularDepth = granularDepth;
	}	
}
