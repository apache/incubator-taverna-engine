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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.ProcessorInputPort;
import org.apache.taverna.workflowmodel.ProcessorOutputPort;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityInputPort;
import org.apache.taverna.workflowmodel.processor.activity.ActivityOutputPort;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MapProcessorPortsToActivityEditTest {
	private static Edits edits;

	@BeforeClass
	public static void createEditsInstance() {
		edits = new EditsImpl();
	}

	ProcessorImpl p;
	Edit<Processor> edit;

	@Before
	public void setupProcessorAndEdit() throws Exception {
		p=new ProcessorImpl();
		ProcessorInputPort ip1=edits.createProcessorInputPort(p, "inputPort1", 1);
		ProcessorInputPort ip2=edits.createProcessorInputPort(p, "inputPort2", 1);
		ProcessorOutputPort op1 = edits.createProcessorOutputPort(p, "outputPort1", 1, 1);
		ProcessorOutputPort op2 = edits.createProcessorOutputPort(p, "outputPort2", 1, 1);
		edits.getAddProcessorOutputPortEdit(p, op1).doEdit();
		edits.getAddProcessorOutputPortEdit(p, op2).doEdit();
		edits.getAddProcessorInputPortEdit(p, ip1).doEdit();
		edits.getAddProcessorInputPortEdit(p, ip2).doEdit();

		Activity<?> a = new DummyActivity();
		ActivityInputPort aip1 = edits.createActivityInputPort("inputPort1", 1, true, new ArrayList<Class<? extends ExternalReferenceSPI>>(), String.class);
		ActivityInputPort aip2 = edits.createActivityInputPort("newInputPort", 0, true, new ArrayList<Class<? extends ExternalReferenceSPI>>(), String.class);
		edits.getAddActivityInputPortEdit(a, aip1).doEdit();
		edits.getAddActivityInputPortEdit(a, aip2).doEdit();

		ActivityOutputPort aop1 = edits.createActivityOutputPort("outputPort1", 1, 1);
		ActivityOutputPort aop2 = edits.createActivityOutputPort("newOutputPort", 0, 0);
		edits.getAddActivityOutputPortEdit(a, aop1).doEdit();
		edits.getAddActivityOutputPortEdit(a, aop2).doEdit();

		edits.getAddActivityEdit(p, a).doEdit();

		edits.getAddActivityInputPortMappingEdit(a,"inputPort1","inputPort1").doEdit();
		edits.getAddActivityInputPortMappingEdit(a,"inputPort2","inputPort2").doEdit();
		edits.getAddActivityOutputPortMappingEdit(a,"outputPort1","outputPort1").doEdit();
		edits.getAddActivityOutputPortMappingEdit(a,"outputPort2","outputPort2").doEdit();

		edit = edits.getMapProcessorPortsForActivityEdit(p);
	}


	@Test
	public void testIsApplied() throws Exception {
		assertFalse(edit.isApplied());
		edit.doEdit();
		assertTrue(edit.isApplied());		
	}

	@Test
	public void testDoEdit() throws Exception {
		edit.doEdit();
		assertEquals("there should now be 1 input port",1,p.getInputPorts().size());
		assertEquals("there should now be 1 output port",1,p.getOutputPorts().size());
	}

	@Test
	public void testMapping() throws Exception {
		Activity<?>a = p.getActivityList().get(0);

		assertEquals(2,a.getInputPortMapping().size());
		assertEquals("inputPort1",a.getInputPortMapping().get("inputPort1"));
		assertEquals("inputPort2",a.getInputPortMapping().get("inputPort2"));
		assertEquals(2,a.getOutputPortMapping().size());
		assertEquals("outputPort1",a.getOutputPortMapping().get("outputPort1"));
		assertEquals("outputPort2",a.getOutputPortMapping().get("outputPort2"));

		edit.doEdit();

		assertEquals(1,a.getInputPortMapping().size());

		assertEquals("inputPort1",a.getInputPortMapping().get("inputPort1"));

		assertEquals(1,a.getOutputPortMapping().size());
		assertEquals("outputPort1",a.getOutputPortMapping().get("outputPort1"));

	}

	@Test
	public void testUnchangedPortsRemain() throws Exception {
		ProcessorOutputPort op1 = p.getOutputPortWithName("outputPort1");
		ProcessorInputPort ip1 = p.getInputPortWithName("inputPort1");
		edit.doEdit();
		assertSame(ip1,p.getInputPortWithName("inputPort1"));
		assertSame(op1,p.getOutputPortWithName("outputPort1"));
	}
}
