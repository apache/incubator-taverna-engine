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
package net.sf.taverna.t2.workflowmodel.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.*;

import java.util.ArrayList;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.serialization.DummyActivity;

import org.junit.Before;
import org.junit.Test;

public class MapProcessorPortsToActivityEditTest {

	ProcessorImpl p;
	EditsImpl edits = new EditsImpl();
	MapProcessorPortsForActivityEdit edit;
	
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
		
		OutputPort aop1 = edits.createActivityOutputPort("outputPort1", 1, 1);
		OutputPort aop2 = edits.createActivityOutputPort("newOutputPort", 0, 0);
		edits.getAddActivityOutputPortEdit(a, aop1).doEdit();
		edits.getAddActivityOutputPortEdit(a, aop2).doEdit();
		
		edits.getAddActivityEdit(p, a).doEdit();
		
		new AddActivityInputPortMappingEdit(a,"inputPort1","inputPort1").doEdit();
		new AddActivityInputPortMappingEdit(a,"inputPort2","inputPort2").doEdit();
		new AddActivityOutputPortMappingEdit(a,"outputPort1","outputPort1").doEdit();
		new AddActivityOutputPortMappingEdit(a,"outputPort2","outputPort2").doEdit();
		
		edit = new MapProcessorPortsForActivityEdit(p);
	}
	
	
	@Test
	public void testIsApplied() throws Exception {
		assertFalse(edit.isApplied());
		edit.doEdit();
		assertTrue(edit.isApplied());
		edit.undo();
		assertFalse(edit.isApplied());
	}
	
	@Test
	public void testDoEdit() throws Exception {
		edit.doEdit();
		assertEquals("there should now be 1 input port",1,p.getInputPorts().size());
		assertEquals("there should now be 1 output port",1,p.getOutputPorts().size());
	}
	
	@Test
	public void testUndo() throws Exception {
		edit.doEdit();
		edit.undo();
		assertEquals("there should now be 2 input ports",2,p.getInputPorts().size());
		assertEquals("there should now be 2 output ports",2,p.getOutputPorts().size());
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
		
		edit.undo();
		
		assertEquals(2,a.getInputPortMapping().size());
		assertEquals("inputPort1",a.getInputPortMapping().get("inputPort1"));
		assertEquals("inputPort2",a.getInputPortMapping().get("inputPort2"));
		assertEquals(2,a.getOutputPortMapping().size());
		assertEquals("outputPort1",a.getOutputPortMapping().get("outputPort1"));
		assertEquals("outputPort2",a.getOutputPortMapping().get("outputPort2"));
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
