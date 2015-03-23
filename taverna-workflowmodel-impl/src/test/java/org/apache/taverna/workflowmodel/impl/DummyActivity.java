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

package org.apache.taverna.workflowmodel.impl;

import org.apache.taverna.workflowmodel.processor.activity.AbstractActivity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;

public class DummyActivity extends AbstractActivity<Integer> {

	private Integer bean=null;
	
	@Override
	public void configure(Integer conf) throws ActivityConfigurationException {
		bean=conf;
	}

	@Override
	public Integer getConfiguration() {
		return bean;
	}

}