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

package org.apache.taverna.workflowmodel.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivity;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivityCallback;

public class AsynchEchoActivity extends
		AbstractAsynchronousActivity<EchoConfig> implements
		AsynchronousActivity<EchoConfig> {

	private EchoConfig config;

	@Override
	public void configure(EchoConfig conf) throws ActivityConfigurationException {
		addInput("input",0, true, new ArrayList<Class<? extends ExternalReferenceSPI>>(), String.class);
		addOutput("output",0,0);
		this.config = conf;
	}

	@Override
	public void executeAsynch(Map<String, T2Reference> data,
			AsynchronousActivityCallback callback) {
		T2Reference inputID = data.get("input");
		Map<String, T2Reference> outputMap = new HashMap<String, T2Reference>();
		outputMap.put("output", inputID);
		callback.receiveResult(outputMap, new int[0]);
	}

	@Override
	public EchoConfig getConfiguration() {
		return config;
	}

}
