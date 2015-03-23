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

package org.apache.taverna.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;
import org.apache.taverna.monitor.MonitorManager.AddPropertiesMessage;
import org.apache.taverna.monitor.MonitorManager.DeregisterNodeMessage;
import org.apache.taverna.monitor.MonitorManager.MonitorMessage;
import org.apache.taverna.monitor.MonitorManager.RegisterNodeMessage;

import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link MonitorManager}.
 * 
 * @author Stian Soiland-Reyes
 *
 */
public class TestMonitorManager {
	private MonitorManager monitorManager;

	@Test
	public void addMonitor() {
		TestMonitor testMonitor = new TestMonitor();
		monitorManager.addObserver(testMonitor);
		assertEquals(0, testMonitor.getCounts());
		// Make a fake registration
		Object workflowObject = "The workflow object as a string";
		String[] owningProcess = { "dataflow0", "process4", "42424" };
		Set<MonitorableProperty<?>> properties = new HashSet<>();
		properties.add(new ExampleProperty());
		monitorManager.registerNode(workflowObject, owningProcess, properties);

		assertEquals(1, testMonitor.getCounts());
		assertEquals(monitorManager, testMonitor.lastSender);
		MonitorMessage lastMessage = testMonitor.lastMessage;
		assertTrue("Owning process did not match", Arrays.equals(owningProcess,
				lastMessage.getOwningProcess()));

		assertTrue("Message was not a RegisterNodeMessage",
				lastMessage instanceof RegisterNodeMessage);
		RegisterNodeMessage registerNodeMessage = (RegisterNodeMessage) lastMessage;
		assertSame("Workflow object was not same", workflowObject,
				registerNodeMessage.getWorkflowObject());
		assertEquals(properties, registerNodeMessage.getProperties());

		assertEquals("Another event was received", 1, testMonitor.getCounts());
	}

	@Test
	public void addProperties() {
		TestMonitor testMonitor = new TestMonitor();
		monitorManager.addObserver(testMonitor);
		assertEquals(0, testMonitor.getCounts());
		// Make a fake add properties
		String[] owningProcess = { "dataflow0", "process4", "42424" };
		Set<MonitorableProperty<?>> newProperties = new HashSet<>();
		newProperties.add(new ExampleProperty());
		monitorManager.addPropertiesToNode(owningProcess, newProperties);

		assertEquals(1, testMonitor.getCounts());
		assertEquals(monitorManager, testMonitor.lastSender);
		MonitorMessage lastMessage = testMonitor.lastMessage;
		assertTrue("Owning process did not match", Arrays.equals(owningProcess,
				lastMessage.getOwningProcess()));

		assertTrue("Message was not a AddPropertiesMessage",
				lastMessage instanceof AddPropertiesMessage);
		AddPropertiesMessage registerNodeMessage = (AddPropertiesMessage) lastMessage;
		assertEquals(newProperties, registerNodeMessage.getNewProperties());

		assertEquals("Another event was received", 1, testMonitor.getCounts());
	}

	@Before
	public void findMonitorManager() {
		monitorManager = MonitorManager.getInstance();
	}

	@Test
	public void removeMonitor() {
		TestMonitor testMonitor = new TestMonitor();
		monitorManager.addObserver(testMonitor);
		assertEquals(0, testMonitor.getCounts());

		// Make a fake deregistration
		String[] owningProcess = { "dataflow0", "process4", "1337" };
		monitorManager.deregisterNode(owningProcess);

		assertEquals(1, testMonitor.getCounts());
		assertEquals(monitorManager, testMonitor.lastSender);
		MonitorMessage lastMessage = testMonitor.lastMessage;
		assertTrue("Owning process did not match", Arrays.equals(owningProcess,
				lastMessage.getOwningProcess()));
		assertTrue("Message was not a DeregisterNodeMessage",
				lastMessage instanceof DeregisterNodeMessage);
		assertEquals("Another event was received", 1, testMonitor.getCounts());
	}

	public class TestMonitor implements Observer<MonitorManager.MonitorMessage> {

		private int counts = 0;
		private MonitorMessage lastMessage;
		private Observable<MonitorMessage> lastSender;

		public int getCounts() {
			return counts;
		}

		public MonitorMessage getMessage() {
			return lastMessage;
		}

		public Observable<MonitorMessage> getSender() {
			return lastSender;
		}

		@Override
		public synchronized void notify(Observable<MonitorMessage> sender,
				MonitorMessage message) throws Exception {
			this.lastSender = sender;
			this.lastMessage = message;
			this.counts++;
		}
	}

	private final class ExampleProperty implements MonitorableProperty<String> {
		@Override
		public Date getLastModified() {
			return new Date();
		}

		@Override
		public String[] getName() {
			return new String[] { "monitor", "test", "example" };
		}

		@Override
		public String getValue() throws NoSuchPropertyException {
			return "Example property value";
		}
	}

}
