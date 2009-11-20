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
package net.sf.taverna.t2.workflowmodel.serialization.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import net.sf.taverna.t2.workflowmodel.Condition;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.MergeInputPort;
import net.sf.taverna.t2.workflowmodel.Processor;

import org.jdom.Element;
import org.junit.Test;


public class DataflowXMLDeserializerTest extends DeserializerTestsHelper {
	DataflowXMLDeserializer deserializer = DataflowXMLDeserializer.getInstance();
	
	@Test
	public void testMerge() throws Exception {
		Element el = loadXMLFragment("dataflow_with_merge.xml");
		Dataflow df = deserializer.deserializeDataflow(el,new HashMap<String, Element>());
		
		assertEquals("There should be 2 processors",2,df.getProcessors().size());
		Processor top=df.getProcessors().get(0);
		Processor bottom=df.getProcessors().get(1);
		
		assertEquals("Top processor should be called top","top",top.getLocalName());
		assertEquals("Bottom processor should be called top","bottom",bottom.getLocalName());
		
		assertEquals("Top should have 1 output port",1,top.getOutputPorts().size());
		
		assertEquals("There should be 1 outgoing link",1,top.getOutputPorts().get(0).getOutgoingLinks().size());
		
		Datalink link = top.getOutputPorts().get(0).getOutgoingLinks().iterator().next();
		
		assertTrue("Link sink should be Merge port",link.getSink() instanceof MergeInputPort);
	}
	
	@Test 
	public void testDataflowNameAndId() throws Exception {
		Element element = loadXMLFragment("empty_dataflow_with_ports.xml");
		Dataflow df = deserializer.deserializeDataflow(element,new HashMap<String, Element>());
		assertEquals("Dataflow should have an id of 123","123",df.getInternalIdentier());
		assertEquals("dataflow should have the name george","george",df.getLocalName());
	}
	
	@Test
	public void testDataflowPorts() throws Exception {
		Element element = loadXMLFragment("empty_dataflow_with_ports.xml");
		Dataflow df = deserializer.deserializeDataflow(element,new HashMap<String, Element>());
		
		assertEquals("there should be 2 input ports",2,df.getInputPorts().size());
		assertEquals("there should be 1 output port",1,df.getOutputPorts().size());
		
		DataflowInputPort port = df.getInputPorts().get(0);
		assertEquals("Name should be input1","input1",port.getName());
		assertEquals("depth should be 0",0,port.getDepth());
		assertEquals("granular depth should be 0",0,port.getGranularInputDepth());
		
		port = df.getInputPorts().get(1);
		assertEquals("Name should be input2","input2",port.getName());
		assertEquals("depth should be 1",1,port.getDepth());
		assertEquals("granular depth should be 1",1,port.getGranularInputDepth());
		
		DataflowOutputPort outputPort = df.getOutputPorts().get(0);
		assertEquals("Name should be output","output",outputPort.getName());
	}
	
	@Test
	public void testDataflowConditionLink() throws Exception {
		Element element = loadXMLFragment("dataflow_with_condition.xml");
		Dataflow df = deserializer.deserializeDataflow(element,new HashMap<String, Element>());
		
		assertEquals("There should be 2 processors",2,df.getProcessors().size());
		Processor pA = df.getProcessors().get(0);
		Processor pB = df.getProcessors().get(1);
		if (!pB.getLocalName().equals("b_processor")) {
			pB=df.getProcessors().get(0);
			pA=df.getProcessors().get(1);
		}
		assertEquals("There should be 1 precondition",1,pB.getPreconditionList().size());
		Condition con = pB.getPreconditionList().get(0);
		assertSame("the control processor shoudl be a_processor",pA, con.getControl());
	}
	
	@Test
	public void testDataflowProcessor() throws Exception {
		Element element = loadXMLFragment("dataflow_with_unlinked_processor.xml");
		Dataflow df = deserializer.deserializeDataflow(element,new HashMap<String, Element>());
		assertEquals("There should be 1 processor",1,df.getProcessors().size());
		assertEquals("Processor name should be a_processor","a_processor",df.getProcessors().get(0).getLocalName());	
	}
	
	
	@Test
	public void testDataflowDataLinks() throws Exception {
		Element el = loadXMLFragment("dataflow_datalinks.xml");
		Dataflow df = deserializer.deserializeDataflow(el,new HashMap<String, Element>());
		
		assertEquals("There should be 2 processors",2,df.getProcessors().size());
		assertEquals("There should be 2 datalinks",2,df.getLinks().size());
	}
}
