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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;
import net.sf.taverna.t2.workflowmodel.Merge;
import net.sf.taverna.t2.workflowmodel.MergeInputPort;
import net.sf.taverna.t2.workflowmodel.MergeOutputPort;

import org.junit.Before;
import org.junit.Test;

public class ConnectMergedDatalinkEditTest {

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
		Edit<Merge> theEdit = new ConnectMergedDatalinkEdit(merge,sourcePort,sinkPort);
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
		
		Edit<Merge> theEdit2 = new ConnectMergedDatalinkEdit(merge,sourcePort2,sinkPort);
		theEdit2.doEdit();
		assertEquals(1,merge.getOutputPort().getOutgoingLinks().size());
		assertEquals(2,merge.getInputPorts().size());
		assertTrue(merge.getInputPorts().get(1) instanceof MergeInputPort);
		assertEquals("source_port2TotheMerge_input0",merge.getInputPorts().get(1).getName());
		assertSame(sourcePort2,merge.getInputPorts().get(1).getIncomingLink().getSource());
	}
	
	@Test
	public void undo() throws Exception {
		Edit<Merge> theEdit = new ConnectMergedDatalinkEdit(merge,sourcePort,sinkPort);
		theEdit.doEdit();
		theEdit.undo();
		assertEquals(0,merge.getInputPorts().size());
		assertEquals(0,merge.getOutputPort().getOutgoingLinks().size());
		assertEquals(0,sourcePort.getOutgoingLinks().size());
		assertNull(sinkPort.getIncomingLink());
	}
	
	/**
	 * Check that the outgoing link to the sink port is retained when undoing a second merged input.
	 */
	@Test
	public void undoSecond() throws Exception {
		Edit<Merge> theEdit = new ConnectMergedDatalinkEdit(merge,sourcePort,sinkPort);
		theEdit.doEdit();
		ProcessorImpl p3=new ProcessorImpl();
		ProcessorOutputPortImpl sourcePort2=new ProcessorOutputPortImpl(p3,"source_port2",0,0);
		Edit<Merge> theEdit2 = new ConnectMergedDatalinkEdit(merge,sourcePort2,sinkPort);
		theEdit2.doEdit();
		theEdit2.undo();
		assertEquals(1,merge.getInputPorts().size());
		assertEquals(1,merge.getOutputPort().getOutgoingLinks().size());
	}
	
	@Test
	public void redo() throws Exception {
		Edit<Merge> theEdit = new ConnectMergedDatalinkEdit(merge,sourcePort,sinkPort);
		theEdit.doEdit();
		theEdit.undo();
		theEdit.doEdit();
		
		assertEquals(1,merge.getInputPorts().size());
		assertTrue(merge.getInputPorts().get(0) instanceof MergeInputPort);
		assertSame(sourcePort,merge.getInputPorts().get(0).getIncomingLink().getSource());
		
		assertEquals(1,merge.getOutputPort().getOutgoingLinks().size());
		assertSame(sinkPort,merge.getOutputPort().getOutgoingLinks().toArray(new Datalink[]{})[0].getSink());
		
		assertEquals(1,sourcePort.getOutgoingLinks().size());
		assertTrue(sourcePort.getOutgoingLinks().toArray(new Datalink[]{})[0].getSink() instanceof MergeInputPort);
		assertTrue(sinkPort.getIncomingLink().getSource() instanceof MergeOutputPort);
		
		assertSame(merge.getInputPorts().get(0),sourcePort.getOutgoingLinks().toArray(new Datalink[]{})[0].getSink());
		assertSame(sinkPort.getIncomingLink().getSource(),merge.getOutputPort());
	}
	
	@Test(expected=RuntimeException.class)
	public void nullMerge() throws Exception {
		new ConnectMergedDatalinkEdit(null,sourcePort,sinkPort);
	}
	
	@Test(expected=RuntimeException.class)
	public void nullSourcePort() throws Exception {
		Merge merge = new MergeImpl("merge");
		new ConnectMergedDatalinkEdit(merge,sourcePort,null);
	}
	
	@Test(expected=RuntimeException.class)
	public void nullSinkPort() throws Exception {
		Merge merge = new MergeImpl("merge");
		new ConnectMergedDatalinkEdit(merge,null,sinkPort);
	}
	
	@Test(expected=EditException.class)
	public void invalidSinkPort() throws Exception {
		Edit<Merge> theEdit = new ConnectMergedDatalinkEdit(merge,sourcePort,sinkPort);
		theEdit.doEdit();
		
		ProcessorImpl p2=new ProcessorImpl();
		ProcessorInputPortImpl sinkPort2=new ProcessorInputPortImpl(p2,"sink_port2",0);
		theEdit = new ConnectMergedDatalinkEdit(merge,sourcePort,sinkPort2);
		theEdit.doEdit();
		
	}
}
