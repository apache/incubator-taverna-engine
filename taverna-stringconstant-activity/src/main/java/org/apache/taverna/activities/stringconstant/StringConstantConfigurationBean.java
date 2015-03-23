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

package org.apache.taverna.activities.stringconstant;

import org.apache.taverna.workflowmodel.processor.config.ConfigurationBean;
import org.apache.taverna.workflowmodel.processor.config.ConfigurationProperty;

/**
 * Configuration bean for setting up a StringConstantActivity.<br>
 * The only thing to be configured is the string value, since the ports are fixed.
 * 
 * @author Stuart Owen
 * @see StringConstantActivity
 */
@ConfigurationBean(uri = StringConstantActivity.URI + "#Config")
public class StringConstantConfigurationBean {
	private String value;

	public String getValue() {
		return value;
	}

	@ConfigurationProperty(name = "string", label = "Constant String Value", description = "The value of the string constant")
	public void setValue(String value) {
		this.value = value;
	}
}
