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

import org.apache.log4j.Logger;

import org.apache.taverna.annotation.AbstractAnnotatedThing;
import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.EventHandlingInputPort;
import org.apache.taverna.workflowmodel.Port;

public class DiagnosticEventHandler extends AbstractAnnotatedThing<Port> implements EventHandlingInputPort {

	private static Logger logger = Logger.getLogger(DiagnosticEventHandler.class);
	
	protected int eventCount = 0;

	@Override
	public synchronized void receiveEvent(WorkflowDataToken token) {
		eventCount++;
		logger.debug(token);
	}

	public int getEventCount() {
		return this.eventCount;
	}

	public synchronized void reset() {
		this.eventCount = 0;
	}

	@Override
	public int getDepth() {
		return 0;
	}

	@Override
	public String getName() {
		return "Test port";
	}

	@Override
	public Datalink getIncomingLink() {
		// TODO Auto-generated method stub
		return null;
	}

}
