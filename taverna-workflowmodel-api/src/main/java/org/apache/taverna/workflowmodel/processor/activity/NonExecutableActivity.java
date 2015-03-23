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

package org.apache.taverna.workflowmodel.processor.activity;

import java.util.Map;

import org.apache.taverna.reference.T2Reference;

/**
 * A non-executable activity is a wrapper for an Activity that cannot be
 * executed, for example because it is offline or unrecognized.
 * 
 * @author alanrw
 */
public abstract class NonExecutableActivity<T> extends
		AbstractAsynchronousActivity<T> {
	public static final String URI = "http://ns.taverna.org.uk/2010/activity/nonExecutable";

	/**
	 * It is not possible to create a "naked" NonExecutableActivity.
	 */
	protected NonExecutableActivity() {
		super();
	}

	/**
	 * Add an input to the NonExecutableActivity with the specified name.
	 * 
	 * @param portName
	 */
	public void addProxyInput(String portName) {
		super.addInput(portName, 0, true, null, null);
	}

	/**
	 * Add an input to the NonExecutableActivity with the specified name and
	 * depth.
	 * 
	 * @param portName
	 * @param depth
	 */
	public void addProxyInput(String portName, int depth) {
		super.addInput(portName, depth, true, null, null);
	}

	/**
	 * Add an output to the NonExecutableActivity with the specified name
	 * 
	 * @param portName
	 */
	public void addProxyOutput(String portName) {
		super.addOutput(portName, 0);
	}

	/**
	 * Add an output to the NonExecutableActivity with the specified name and
	 * depth
	 * 
	 * @param portName
	 * @param depth
	 */
	public void addProxyOutput(String portName, int depth) {
		super.addOutput(portName, depth);
	}

	/**
	 * Attempting to run a NonExecutableActivity will always fail.
	 */
	@Override
	public void executeAsynch(Map<String, T2Reference> data,
			final AsynchronousActivityCallback callback) {
		callback.requestRun(new Runnable() {
			@Override
			public void run() {
				callback.fail("The service is not executable");
			}
		});
	}
}