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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.serialization.DeserializationException;

import org.jdom.Element;

public class DataflowXMLDeserializer extends AbstractXMLDeserializer {
	private static DataflowXMLDeserializer instance = new DataflowXMLDeserializer();

	private DataflowXMLDeserializer() {

	}

	public static DataflowXMLDeserializer getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public Dataflow deserializeDataflow(Element element,Map<String,Element> innerDataflowElements) throws EditException, DeserializationException, ActivityConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Dataflow df = edits.createDataflow();
		
		String name = element.getChildText(NAME,T2_WORKFLOW_NAMESPACE);
		String id = element.getAttributeValue(DATAFLOW_ID);
		edits.getUpdateDataflowNameEdit(df, name).doEdit();
		edits.getUpdateDataflowInternalIdentifierEdit(df, id).doEdit();
		
		Element inputPorts = element.getChild(DATAFLOW_INPUT_PORTS,T2_WORKFLOW_NAMESPACE);
		Element outputPorts = element.getChild(DATAFLOW_OUTPUT_PORTS,T2_WORKFLOW_NAMESPACE);
		
		//dataflow ports
		addDataflowPorts(df,inputPorts,outputPorts);
		
		Map<String,Processor> createdProcessors = new HashMap<String, Processor>();
		//processors
		Element processorsElement = element.getChild(PROCESSORS,T2_WORKFLOW_NAMESPACE);
		for(Element procElement : (List<Element>)processorsElement.getChildren(PROCESSOR,T2_WORKFLOW_NAMESPACE)) {
			Processor p = ProcessorXMLDeserializer.getInstance().deserializeProcessor(procElement,innerDataflowElements);
			createdProcessors.put(p.getLocalName(),p);
			edits.getAddProcessorEdit(df, p).doEdit();
		}
		
		//conditions
		Element conditions = element.getChild(CONDITIONS,T2_WORKFLOW_NAMESPACE);
		ConditionXMLDeserializer.getInstance().buildConditions(df,conditions,createdProcessors);		
		
		//datalinks
		Element datalinks = element.getChild(DATALINKS,T2_WORKFLOW_NAMESPACE);
		DatalinksXMLDeserializer.getInstance().buildDatalinks(df,createdProcessors,datalinks);
		
		//annotations
		annotationsFromXml(df, element, df.getClass().getClassLoader());
		return df;
	}

	@SuppressWarnings("unchecked")
	private void addDataflowPorts(Dataflow df, Element inputPortsElement,Element outputPortsElement) throws EditException {
		for (Element port : (List<Element>)inputPortsElement.getChildren(DATAFLOW_PORT,T2_WORKFLOW_NAMESPACE)) {
			String name=port.getChildText(NAME,T2_WORKFLOW_NAMESPACE);
			int portDepth = Integer.valueOf(port.getChildText(DEPTH,T2_WORKFLOW_NAMESPACE));
			int granularDepth = Integer.valueOf(port.getChildText(GRANULAR_DEPTH,T2_WORKFLOW_NAMESPACE));
			edits.getCreateDataflowInputPortEdit(df, name, portDepth, granularDepth).doEdit();
		}
		
		for (Element port : (List<Element>)outputPortsElement.getChildren(DATAFLOW_PORT,T2_WORKFLOW_NAMESPACE)) {
			String name=port.getChildText(NAME,T2_WORKFLOW_NAMESPACE);
			edits.getCreateDataflowOutputPortEdit(df, name).doEdit();
		}
	}
}
