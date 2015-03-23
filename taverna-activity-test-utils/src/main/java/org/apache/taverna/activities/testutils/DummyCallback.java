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

package org.apache.taverna.activities.testutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.impl.InvocationContextImpl;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivityCallback;
import org.apache.taverna.workflowmodel.processor.dispatch.events.DispatchErrorType;

import org.apache.log4j.Logger;

/**
 * A DummyCallback to aid with testing Activities.
 * 
 * @author Stuart Owen
 * @author David Withers
 * @author Stian Soiland-Reyes
 *
 */
public class DummyCallback implements AsynchronousActivityCallback {
	
	private static Logger logger = Logger
	.getLogger(DummyCallback.class);

	public ReferenceService referenceService;
	public InvocationContext invocationContext;
	public Map<String, T2Reference> data;
	public Thread thread;

	public boolean failed = false;
	
	public List<RuntimeException> failures = new ArrayList<RuntimeException>();
	
	public DummyCallback(ReferenceService referenceService) {
		this.referenceService = referenceService;
		this.invocationContext = new InvocationContextImpl(referenceService, null);
	}

	public void fail(String message, Throwable t) {
		fail(message, t, null);
	}

	public void fail(String message) {
		fail(message, null, null);
	}

	public void fail(String message, Throwable t, DispatchErrorType arg2) {
		failed = true;
		failures.add(new RuntimeException(arg2+message, t));
		logger.error("", t);
	}
	
	/*public SecurityAgentManager getLocalSecurityManager() {
		// TODO Auto-generated method stub
		return null;
	}*/

	public void receiveCompletion(int[] completionIndex) {
		// TODO Auto-generated method stub

	}

	public void receiveResult(Map<String, T2Reference> data,
			int[] index) {
		this.data = data;
	}

	public void requestRun(Runnable runMe) {
		thread = new Thread(runMe);
		thread.start();
	}

	public InvocationContext getContext() {
		return invocationContext;
	}

	public String getParentProcessIdentifier() {
		// TODO Auto-generated method stub
		return "";
	}

}
