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

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.Merge;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.impl.EditsImpl;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

public class DatalinksXMLSerializerTest implements XMLSerializationConstants {
	DatalinksXMLSerializer serializer = DatalinksXMLSerializer.getInstance();
	private Edits edits = new EditsImpl();

	private String elementToString(Element element) {
		return new XMLOutputter().outputString(element);
	}

	@Test
	public void testDatalinks() throws Exception {
		Processor p = edits.createProcessor("top");
		Processor p2 = edits.createProcessor("bottom");
		ProcessorInputPort iPort = edits.createProcessorInputPort(p2, "input", 0);
		ProcessorOutputPort oPort = edits.createProcessorOutputPort(p, "output", 0, 0);
		edits.getAddProcessorInputPortEdit(p2, iPort).doEdit();
		edits.getAddProcessorOutputPortEdit(p, oPort).doEdit();
		Datalink link = edits.createDatalink(p.getOutputPorts().get(0), p2
				.getInputPorts().get(0));
		List<Datalink> links = new ArrayList<Datalink>();
		links.add(link);

		Element el = serializer.datalinksToXML(links);

		assertEquals("Root name should be datalinks", "datalinks", el.getName());
		assertEquals("there should be 1 child named datalink", 1, el
				.getChildren("datalink", T2_WORKFLOW_NAMESPACE).size());

	}

	@Test
	public void testDatalinkMerge() throws Exception {
		Dataflow df = edits.createDataflow();
		Processor p = edits.createProcessor("top");
		Processor p2 = edits.createProcessor("bottom");
		edits.getAddProcessorEdit(df, p).doEdit();
		edits.getAddProcessorEdit(df, p2).doEdit();
		ProcessorInputPort iPort = edits.createProcessorInputPort(p2, "input", 0);
		ProcessorOutputPort oPort = edits.createProcessorOutputPort(p, "output", 0, 0);
		edits.getAddProcessorInputPortEdit(p2, iPort).doEdit();
		edits.getAddProcessorOutputPortEdit(p, oPort).doEdit();
		Merge m = edits.createMerge(df);
		edits.getAddMergeEdit(df, m).doEdit();

		edits.getConnectMergedDatalinkEdit(m, p.getOutputPorts().get(0),
				p2.getInputPorts().get(0)).doEdit();

		Element el = serializer.datalinksToXML(df.getLinks());

		assertEquals("root element should be datalinks", "datalinks", el
				.getName());
		assertEquals("there should be 1 child datalink", 1, el.getChildren(
				"datalink", T2_WORKFLOW_NAMESPACE).size());

		String xml = elementToString(el);
		String expected = "<datalinks xmlns=\""
				+ T2_WORKFLOW_NAMESPACE.getURI()
				+ "\"><datalink><sink type=\"merge\"><processor>bottom</processor><port>input</port></sink><source type=\"processor\"><processor>top</processor><port>output</port></source></datalink></datalinks>";
		assertEquals("Unexpected xml generated", expected, xml);
	}

	@Test
	public void testLinkedDataflowInputPort() throws Exception {
		Dataflow df = edits.createDataflow();
		edits.getCreateDataflowInputPortEdit(df, "dataflow_in", 0, 0).doEdit();
		Processor p = edits.createProcessor("p");
		ProcessorInputPort port = edits.createProcessorInputPort(p,"p_in",0);
		edits.getAddProcessorInputPortEdit(p, port).doEdit();
		Datalink link = edits.createDatalink(df.getInputPorts().get(0)
				.getInternalOutputPort(), p.getInputPorts().get(0));
		edits.getConnectDatalinkEdit(link).doEdit();
		List<Datalink> links = new ArrayList<Datalink>();
		links.add(link);

		Element el = serializer.datalinksToXML(links);
		
		assertEquals("Root name should be datalinks", "datalinks", el.getName());
		assertEquals("there should be 1 child named datalink", 1, el
				.getChildren("datalink", T2_WORKFLOW_NAMESPACE).size());
		Element elLink = el.getChild("datalink", T2_WORKFLOW_NAMESPACE);
		assertEquals("There should be 1 sink", 1, elLink.getChildren("sink",
				T2_WORKFLOW_NAMESPACE).size());
		assertEquals("There should be 1 source", 1, elLink.getChildren(
				"source", T2_WORKFLOW_NAMESPACE).size());

		Element elSink = elLink.getChild("sink", T2_WORKFLOW_NAMESPACE);
		assertEquals("type should be processor", "processor", elSink
				.getAttribute("type").getValue());
		assertEquals("processor name should be p", "p", elSink.getChildText(
				"processor", T2_WORKFLOW_NAMESPACE));
		assertEquals("port name should be p_in", "p_in", elSink.getChildText(
				"port", T2_WORKFLOW_NAMESPACE));

		Element elSource = elLink.getChild("source", T2_WORKFLOW_NAMESPACE);
		assertEquals("type should be dataflow", "dataflow", elSource
				.getAttribute("type").getValue());
		assertEquals("port name should be dataflow_in", "dataflow_in", elSource
				.getChildText("port", T2_WORKFLOW_NAMESPACE));

	}

	@Test
	public void testLinkedDataflowOutputPort() throws Exception {
		Dataflow df = edits.createDataflow();
		edits.getCreateDataflowOutputPortEdit(df, "dataflow_out").doEdit();
		Processor p = edits.createProcessor("p");
		ProcessorOutputPort port = edits.createProcessorOutputPort(p, "p_out", 0, 0);
		edits.getAddProcessorOutputPortEdit(p, port).doEdit();
		Datalink link = edits.createDatalink(p.getOutputPorts().get(0), df
				.getOutputPorts().get(0).getInternalInputPort());
		edits.getConnectDatalinkEdit(link).doEdit();
		List<Datalink> links = new ArrayList<Datalink>();
		links.add(link);

		Element el = serializer.datalinksToXML(links);
		
		assertEquals("Root name should be datalinks", "datalinks", el.getName());
		assertEquals("there should be 1 child named datalink", 1, el
				.getChildren("datalink", T2_WORKFLOW_NAMESPACE).size());
		Element elLink = el.getChild("datalink", T2_WORKFLOW_NAMESPACE);
		assertEquals("There should be 1 sink", 1, elLink.getChildren("sink",
				T2_WORKFLOW_NAMESPACE).size());
		assertEquals("There should be 1 source", 1, elLink.getChildren(
				"source", T2_WORKFLOW_NAMESPACE).size());

		Element elSink = elLink.getChild("sink", T2_WORKFLOW_NAMESPACE);
		assertEquals("type should be dataflow", "dataflow", elSink
				.getAttribute("type").getValue());
		assertEquals("port name should be dataflow_out", "dataflow_out", elSink
				.getChildText("port", T2_WORKFLOW_NAMESPACE));

		Element elSource = elLink.getChild("source", T2_WORKFLOW_NAMESPACE);
		assertEquals("type should be processor", "processor", elSource
				.getAttribute("type").getValue());
		assertEquals("processor name should be p", "p", elSource.getChildText(
				"processor", T2_WORKFLOW_NAMESPACE));
		assertEquals("port name should be p_in", "p_out", elSource
				.getChildText("port", T2_WORKFLOW_NAMESPACE));
	}
	
	@Test
	public void testDatalink() throws Exception {
		Processor p = edits.createProcessor("top");
		Processor p2 = edits.createProcessor("bottom");
		ProcessorInputPort iPort = edits.createProcessorInputPort(p2, "input", 0);
		edits.getAddProcessorInputPortEdit(p2, iPort).doEdit();
		ProcessorOutputPort oPort = edits.createProcessorOutputPort(p, "output", 0, 0);
		edits.getAddProcessorOutputPortEdit(p, oPort).doEdit();
		Datalink link = edits.createDatalink(p.getOutputPorts().get(0), p2.getInputPorts().get(0));
		edits.getConnectDatalinkEdit(link).doEdit();
		
		Element el = serializer.datalinkToXML(link);
		
		assertEquals("root name should be datalink","datalink",el.getName());
		assertEquals("there should be 1 child called source",1,el.getChildren("source",T2_WORKFLOW_NAMESPACE).size());
		assertEquals("there should be 1 child called sink",1,el.getChildren("sink",T2_WORKFLOW_NAMESPACE).size());
		Element sink=el.getChild("sink",T2_WORKFLOW_NAMESPACE);
		Element source=el.getChild("source",T2_WORKFLOW_NAMESPACE);
		
		assertEquals("source processor should be called 'top'","top",source.getChild("processor",T2_WORKFLOW_NAMESPACE).getText());
		assertEquals("sink processor should be called 'bottom'","bottom",sink.getChild("processor",T2_WORKFLOW_NAMESPACE).getText());
		
		assertEquals("source port should be called 'output'","output",source.getChild("port",T2_WORKFLOW_NAMESPACE).getText());
		assertEquals("sink port should be called 'input'","input",sink.getChild("port",T2_WORKFLOW_NAMESPACE).getText());
	}
}
