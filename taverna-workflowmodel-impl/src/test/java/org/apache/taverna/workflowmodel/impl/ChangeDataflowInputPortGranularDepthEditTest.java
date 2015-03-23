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

import static org.junit.Assert.assertEquals;
import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author David Withers
 *
 */
public class ChangeDataflowInputPortGranularDepthEditTest {
	private static Edits edits;

	@BeforeClass
	public static void createEditsInstance() {
		edits = new EditsImpl();
	}

	private DataflowInputPortImpl dataflowInputPort;
	private int depth;
	private int granularDepth;
	
	@Before
	public void setUp() throws Exception {
		depth = 3;
		granularDepth = 1;
		dataflowInputPort = new DataflowInputPortImpl("port name", depth, granularDepth, null);
	}

	@Test
	public void testDoEditAction() throws EditException {
		int newGranularDepth = 2;
		Edit<DataflowInputPort> edit = edits.getChangeDataflowInputPortGranularDepthEdit(dataflowInputPort, newGranularDepth);
		assertEquals(depth, dataflowInputPort.getDepth());
		assertEquals(granularDepth, dataflowInputPort.getGranularInputDepth());		
		edit.doEdit();
		assertEquals(depth, dataflowInputPort.getDepth());
		assertEquals(newGranularDepth, dataflowInputPort.getGranularInputDepth());
	}

	@Test
	public void testCreateDataflowInputPortEdit() {
		Edit<DataflowInputPort> edit = edits.getChangeDataflowInputPortDepthEdit(dataflowInputPort, 0);
		assertEquals(dataflowInputPort, edit.getSubject());
	}

}
