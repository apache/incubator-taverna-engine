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
import static org.junit.Assert.assertNotNull;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.impl.EditsImpl;

import org.jdom.Element;
import org.junit.Test;



public class ProcessorXMLSerializerTest implements XMLSerializationConstants {
	Edits edits = new EditsImpl();
	
	ProcessorXMLSerializer serializer = ProcessorXMLSerializer.getInstance();
	
	@Test
	public void testProcessorSerialization() throws Exception {
		Processor p = edits.createProcessor("fred");
		ProcessorInputPort iPort = edits.createProcessorInputPort(p, "input", 0);
		ProcessorOutputPort oPort = edits.createProcessorOutputPort(p, "output", 1, 0);
		edits.getAddProcessorInputPortEdit(p, iPort).doEdit();
		edits.getAddProcessorOutputPortEdit(p, oPort).doEdit();
		
		Element el = serializer.processorToXML(p);

		
		assertNotNull("Element should not be null",el);
		
		assertEquals("root element should be processor","processor",el.getName());
		Element name=el.getChild("name",T2_WORKFLOW_NAMESPACE);
		assertNotNull("There should be a child called name",name);
		assertEquals("name should be fred","fred",name.getText());
		
		assertEquals("there should be an annotations child (even if its empty)",1,el.getChildren("annotations",T2_WORKFLOW_NAMESPACE).size());
		assertEquals("there should be an activities child (even if its empty)",1,el.getChildren("activities",T2_WORKFLOW_NAMESPACE).size());
		assertEquals("there should be an dispatch statck child (even if its empty)",1,el.getChildren("dispatchStack",T2_WORKFLOW_NAMESPACE).size());
		assertEquals("there should be an iteration strategy stack child (even if its empty)",1,el.getChildren("iterationStrategyStack",T2_WORKFLOW_NAMESPACE).size());
		assertEquals("there should be an input ports child (even if its empty)",1,el.getChildren("inputPorts",T2_WORKFLOW_NAMESPACE).size());
		Element inputPorts = el.getChild("inputPorts",T2_WORKFLOW_NAMESPACE);
		assertEquals("there should be 1 port element",1,inputPorts.getChildren("port",T2_WORKFLOW_NAMESPACE).size());
		Element port = inputPorts.getChild("port",T2_WORKFLOW_NAMESPACE);
		assertEquals("name should be input","input",port.getChild("name",T2_WORKFLOW_NAMESPACE).getText());
		assertEquals("depth should be 0","0",port.getChild("depth",T2_WORKFLOW_NAMESPACE).getText());
		Element outputPorts = el.getChild("outputPorts",T2_WORKFLOW_NAMESPACE);
		assertEquals("there should be an output ports child (even if its empty)",1,el.getChildren("outputPorts",T2_WORKFLOW_NAMESPACE).size());
		port = outputPorts.getChild("port",T2_WORKFLOW_NAMESPACE);
		assertEquals("name should be output","output",port.getChild("name",T2_WORKFLOW_NAMESPACE).getText());
		assertEquals("depth should be 1","1",port.getChild("depth",T2_WORKFLOW_NAMESPACE).getText());
		assertEquals("granularDepth should be 0","0",port.getChild("granularDepth",T2_WORKFLOW_NAMESPACE).getText());
	}

}
