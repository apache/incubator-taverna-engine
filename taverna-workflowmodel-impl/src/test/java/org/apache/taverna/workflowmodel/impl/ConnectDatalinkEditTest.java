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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.EventForwardingOutputPort;
import org.apache.taverna.workflowmodel.EventHandlingInputPort;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author David Withers
 * 
 */
public class ConnectDatalinkEditTest {
	private static Edits edits;

	@BeforeClass
	public static void createEditsInstance() {
		edits = new EditsImpl();
	}

	private DatalinkImpl datalink;
	private EventForwardingOutputPort source;
	private EventHandlingInputPort sink;

	@Before
	public void setUp() throws Exception {
		source = new BasicEventForwardingOutputPort("output", 0, 0);
		sink = new DataflowInputPortImpl("input", 0, 0, null);
		datalink = new DatalinkImpl(source, sink);
	}

	@Test
	public void testDoEditAction() throws EditException {
		Edit<Datalink> edit = edits.getConnectDatalinkEdit(datalink);
		assertEquals(0, datalink.getSource().getOutgoingLinks().size());
		assertNull(datalink.getSink().getIncomingLink());
		edit.doEdit();
		assertEquals(1, datalink.getSource().getOutgoingLinks().size());
		assertEquals(datalink, datalink.getSource().getOutgoingLinks()
				.iterator().next());
		assertEquals(datalink, datalink.getSink().getIncomingLink());
	}

	@Test
	public void testUndoEditAction() throws EditException {
		Edit<Datalink> edit = edits.getConnectDatalinkEdit(datalink);
		assertEquals(0, datalink.getSource().getOutgoingLinks().size());
		assertNull(datalink.getSink().getIncomingLink());
		edit.doEdit();
		assertEquals(1, datalink.getSource().getOutgoingLinks().size());
		assertNotNull(datalink.getSink().getIncomingLink());
	}

	@Test
	public void testConnectDatalinkEdit() {
		Edit<Datalink> edit = edits.getConnectDatalinkEdit(datalink);
		assertEquals(datalink, edit.getSubject());
	}

}
