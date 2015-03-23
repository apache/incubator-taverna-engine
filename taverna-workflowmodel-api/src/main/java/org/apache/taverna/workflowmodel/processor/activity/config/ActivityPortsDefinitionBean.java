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

import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.config.ConfigurationBean;
import org.apache.taverna.workflowmodel.processor.config.ConfigurationProperty;

/**
 * <p>
 * Defines a configuration type that relates directly to an {@link Activity} and
 * in particular defines details its input and output ports.<br>
 * An Activity that has its ports implicitly defined may define a ConfigType
 * that extends this class, but this is not enforced.
 * </p>
 * 
 * @author Stuart Owen
 */
@ConfigurationBean(uri = "http://ns.taverna.org.uk/2010/scufl2#ActivityPortsDefinition")
public class ActivityPortsDefinitionBean {
	private List<ActivityInputPortDefinitionBean> inputs = new ArrayList<>();
	private List<ActivityOutputPortDefinitionBean> outputs = new ArrayList<>();

	/**
	 * @return a list of {@link ActivityInputPortDefinitionBean} that describes
	 *         each input port
	 */
	public List<ActivityInputPortDefinitionBean> getInputPortDefinitions() {
		return inputs;
	}

	/**
	 * @return a list of {@link ActivityOutputPortDefinitionBean} that describes
	 *         each output port.
	 */
	public List<ActivityOutputPortDefinitionBean> getOutputPortDefinitions() {
		return outputs;
	}

	/**
	 * @param portDefinitions
	 *            a list of {@link ActivityInputPortDefinitionBean} that
	 *            describes each input port
	 */
	@ConfigurationProperty(name = "inputPortDefinition", label = "Input Ports", description = "", required = false, ordering = ConfigurationProperty.OrderPolicy.NON_ORDERED)
	public void setInputPortDefinitions(
			List<ActivityInputPortDefinitionBean> portDefinitions) {
		inputs = portDefinitions;
	}

	/**
	 * @param portDefinitions
	 *            a list of {@link ActivityOutputPortDefinitionBean} that
	 *            describes each output port
	 */
	@ConfigurationProperty(name = "outputPortDefinition", label = "Output Ports", description = "", required = false, ordering = ConfigurationProperty.OrderPolicy.NON_ORDERED)
	public void setOutputPortDefinitions(
			List<ActivityOutputPortDefinitionBean> portDefinitions) {
		outputs = portDefinitions;
	}
}
