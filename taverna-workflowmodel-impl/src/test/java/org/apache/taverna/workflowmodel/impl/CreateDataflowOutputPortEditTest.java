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
import static org.junit.Assert.assertSame;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowOutputPort;
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
public class CreateDataflowOutputPortEditTest {
	private static Edits edits;

	@BeforeClass
	public static void createEditsInstance() {
		edits = new EditsImpl();
	}

	private DataflowImpl dataflow;
	private String portName;

	@Before
	public void setUp() throws Exception {
		dataflow = new DataflowImpl();
		portName = "port name";
	}

	@Test
	public void testDoEditAction() throws EditException {
		Edit<Dataflow> edit = edits.getCreateDataflowOutputPortEdit(dataflow, portName);
		assertEquals(0, dataflow.getOutputPorts().size());
		edit.doEdit();
		assertEquals(1, dataflow.getOutputPorts().size());
		DataflowOutputPort outputPort = dataflow.getOutputPorts().get(0);
		assertSame(dataflow, outputPort.getDataflow());
		assertEquals(portName, outputPort.getName());
	}

	@Test
	public void testUndoEditAction() throws EditException {
		Edit<Dataflow> edit = edits.getCreateDataflowOutputPortEdit(dataflow, portName);
		assertEquals(0, dataflow.getOutputPorts().size());
		edit.doEdit();
		assertEquals(1, dataflow.getOutputPorts().size());
	}

	@Test
	public void testCreateDataflowOutputPortEdit() {
		Edit<Dataflow> edit = edits.getCreateDataflowOutputPortEdit(dataflow, portName);
		assertEquals(dataflow, edit.getSubject());
	}

}
