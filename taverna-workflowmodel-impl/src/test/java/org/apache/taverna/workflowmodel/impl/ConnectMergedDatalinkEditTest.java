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
//import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.EventForwardingOutputPort;
import org.apache.taverna.workflowmodel.EventHandlingInputPort;
import org.apache.taverna.workflowmodel.Merge;
import org.apache.taverna.workflowmodel.MergeInputPort;
import org.apache.taverna.workflowmodel.MergeOutputPort;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConnectMergedDatalinkEditTest {
	private static Edits edits;

	@BeforeClass
	public static void createEditsInstance() {
		edits = new EditsImpl();
	}

	EventForwardingOutputPort sourcePort;
	EventHandlingInputPort sinkPort;
	Merge merge;
	
	@Before
	public void setup() throws Exception {
		merge = new MergeImpl("theMerge");
		ProcessorImpl p1 = new ProcessorImpl();
		ProcessorImpl p2 = new ProcessorImpl();
		sourcePort=new ProcessorOutputPortImpl(p1,"source_port",0,0);
		sinkPort=new ProcessorInputPortImpl(p2,"sink_port",0);
	}
	
	@Test
	public void applyEdit() throws Exception {
		Edit<Merge> theEdit = edits.getConnectMergedDatalinkEdit(merge,sourcePort,sinkPort);
		assertEquals(0,merge.getInputPorts().size());
		assertNotNull(merge.getOutputPort());
		assertTrue(merge.getOutputPort() instanceof MergeOutputPort);
		assertEquals(0,merge.getOutputPort().getOutgoingLinks().size());
		
		assertSame(merge,((MergeOutputPort)merge.getOutputPort()).getMerge());
		
		theEdit.doEdit();
		assertEquals(1,merge.getInputPorts().size());
		assertTrue(merge.getInputPorts().get(0) instanceof MergeInputPort);
		assertEquals("source_portTotheMerge_input0",merge.getInputPorts().get(0).getName());
		assertSame(sourcePort,merge.getInputPorts().get(0).getIncomingLink().getSource());
		
		assertEquals(1,merge.getOutputPort().getOutgoingLinks().size());
		assertSame(sinkPort,merge.getOutputPort().getOutgoingLinks().toArray(new Datalink[]{})[0].getSink());
		
		assertEquals(1,sourcePort.getOutgoingLinks().size());
		assertTrue(sourcePort.getOutgoingLinks().toArray(new Datalink[]{})[0].getSink() instanceof MergeInputPort);
		assertTrue(sinkPort.getIncomingLink().getSource() instanceof MergeOutputPort);
		
		assertSame(merge.getInputPorts().get(0),sourcePort.getOutgoingLinks().toArray(new Datalink[]{})[0].getSink());
		assertSame(sinkPort.getIncomingLink().getSource(),merge.getOutputPort());
		
		ProcessorImpl p3=new ProcessorImpl();
		ProcessorOutputPortImpl sourcePort2=new ProcessorOutputPortImpl(p3,"source_port2",0,0);
		
		Edit<Merge> theEdit2 = edits.getConnectMergedDatalinkEdit(merge,sourcePort2,sinkPort);
		theEdit2.doEdit();
		assertEquals(1,merge.getOutputPort().getOutgoingLinks().size());
		assertEquals(2,merge.getInputPorts().size());
		assertTrue(merge.getInputPorts().get(1) instanceof MergeInputPort);
		assertEquals("source_port2TotheMerge_input0",merge.getInputPorts().get(1).getName());
		assertSame(sourcePort2,merge.getInputPorts().get(1).getIncomingLink().getSource());
	}
	
	@Test(expected=RuntimeException.class)
	public void nullMerge() throws Exception {
		edits.getConnectMergedDatalinkEdit(null,sourcePort,sinkPort);
	}
	
	@Test(expected=RuntimeException.class)
	public void nullSourcePort() throws Exception {
		Merge merge = new MergeImpl("merge");
		edits.getConnectMergedDatalinkEdit(merge,sourcePort,null);
	}
	
	@Test(expected=RuntimeException.class)
	public void nullSinkPort() throws Exception {
		Merge merge = new MergeImpl("merge");
		edits.getConnectMergedDatalinkEdit(merge,null,sinkPort);
	}
	
	@Test(expected=EditException.class)
	public void invalidSinkPort() throws Exception {
		Edit<Merge> theEdit = edits.getConnectMergedDatalinkEdit(merge,sourcePort,sinkPort);
		theEdit.doEdit();
		
		ProcessorImpl p2=new ProcessorImpl();
		ProcessorInputPortImpl sinkPort2=new ProcessorInputPortImpl(p2,"sink_port2",0);
		theEdit = edits.getConnectMergedDatalinkEdit(merge,sourcePort,sinkPort2);
		theEdit.doEdit();
		
	}
}
