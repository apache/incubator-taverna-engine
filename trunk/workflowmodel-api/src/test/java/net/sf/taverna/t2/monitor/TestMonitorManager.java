/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.monitor.MonitorManager.AddPropertiesMessage;
import net.sf.taverna.t2.monitor.MonitorManager.DeregisterNodeMessage;
import net.sf.taverna.t2.monitor.MonitorManager.MonitorMessage;
import net.sf.taverna.t2.monitor.MonitorManager.RegisterNodeMessage;

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
		Set<MonitorableProperty<?>> properties = new HashSet<MonitorableProperty<?>>();
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
		Set<MonitorableProperty<?>> newProperties = new HashSet<MonitorableProperty<?>>();
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

		public synchronized void notify(Observable<MonitorMessage> sender,
				MonitorMessage message) throws Exception {
			this.lastSender = sender;
			this.lastMessage = message;
			this.counts++;
		}
	}

	private final class ExampleProperty implements MonitorableProperty<String> {
		public Date getLastModified() {
			return new Date();
		}

		public String[] getName() {
			return new String[] { "monitor", "test", "example" };
		}

		public String getValue() throws NoSuchPropertyException {
			return "Example property value";
		}
	}

}
