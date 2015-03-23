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

package org.apache.taverna.monitor.impl;

import static org.junit.Assert.fail;

import java.util.HashSet;

import org.apache.taverna.monitor.MonitorableProperty;

import org.junit.Test;

public class MonitorTreeModelTest {

	@Test
	public void testAddNodes() throws InterruptedException {
		MonitorTreeModel m = MonitorTreeModel.getInstance();
		m.registerNode(this, new String[] { "foo" },
				new HashSet<MonitorableProperty<?>>());
		m.registerNode(this, new String[] { "foo", "bar" },
				new HashSet<MonitorableProperty<?>>());
	}

	@Test
	public void testAddNodesShouldFail() {
		MonitorTreeModel m = MonitorTreeModel.getInstance();
		m.registerNode(this, new String[] { "foo" },
				new HashSet<MonitorableProperty<?>>());
		try {
			m.registerNode(this, new String[] { "bar", "wibble" },
					new HashSet<MonitorableProperty<?>>());
			fail("Should have thrown index out of bounds exception");
		} catch (IndexOutOfBoundsException ioobe) {
			// Okay, should see this
		}

	}
}
