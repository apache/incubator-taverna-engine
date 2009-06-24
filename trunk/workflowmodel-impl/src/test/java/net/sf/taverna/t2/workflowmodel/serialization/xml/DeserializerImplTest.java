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
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Before;
import org.junit.Test;

public class DeserializerImplTest extends DeserializerTestsHelper {

	private XMLDeserializerImpl deserializer = new XMLDeserializerImpl();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDeserialize() throws Exception {
		
			Element element = new Element("workflow",Namespace.getNamespace("http://taverna.sf.net/2008/xml/t2flow"));
			Element innerDataflow = loadXMLFragment("empty_dataflow_with_ports.xml");
			innerDataflow.setAttribute("role","top");
			element.addContent(innerDataflow);
			Dataflow df = deserializer.deserializeDataflow(element);
			
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
	
	
	

}
