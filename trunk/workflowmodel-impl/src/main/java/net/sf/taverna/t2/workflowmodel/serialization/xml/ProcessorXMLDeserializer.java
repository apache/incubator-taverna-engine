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

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.serialization.DeserializationException;

import org.jdom.Element;

public class ProcessorXMLDeserializer extends AbstractXMLDeserializer {
	private static ProcessorXMLDeserializer instance = new ProcessorXMLDeserializer();

	private ProcessorXMLDeserializer() {

	}

	public static ProcessorXMLDeserializer getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public Processor deserializeProcessor(Element el,Map<String,Element>innerDataflowElements) throws EditException, ActivityConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException, DeserializationException {
		String name=el.getChildText(NAME,T2_WORKFLOW_NAMESPACE);
		Processor result=edits.createProcessor(name);
		
		//activities
		Element activities=el.getChild(ACTIVITIES,T2_WORKFLOW_NAMESPACE);
		for (Element activity : (List<Element>)activities.getChildren(ACTIVITY,T2_WORKFLOW_NAMESPACE)) {
			Activity<?> a = ActivityXMLDeserializer.getInstance().deserializeActivity(activity,innerDataflowElements);
			edits.getAddActivityEdit(result, a).doEdit();
		}
		
		//ports
		Element inputPorts = el.getChild(PROCESSOR_INPUT_PORTS,T2_WORKFLOW_NAMESPACE);
		Element outputPorts = el.getChild(PROCESSOR_OUTPUT_PORTS,T2_WORKFLOW_NAMESPACE);
		
		for (Element inputPort : (List<Element>)inputPorts.getChildren(PROCESSOR_PORT,T2_WORKFLOW_NAMESPACE)) {
			String portName=inputPort.getChildText(NAME,T2_WORKFLOW_NAMESPACE);
			int portDepth = Integer.valueOf(inputPort.getChildText(DEPTH,T2_WORKFLOW_NAMESPACE));
			ProcessorInputPort port = edits.createProcessorInputPort(result, portName, portDepth);
			edits.getAddProcessorInputPortEdit(result, port).doEdit();
		}
		
		for (Element outputPort : (List<Element>)outputPorts.getChildren(PROCESSOR_PORT,T2_WORKFLOW_NAMESPACE)) {
			String portName=outputPort.getChildText(NAME,T2_WORKFLOW_NAMESPACE);
			int portDepth = Integer.valueOf(outputPort.getChildText(DEPTH,T2_WORKFLOW_NAMESPACE));
			int granularDepth = Integer.valueOf(outputPort.getChildText(GRANULAR_DEPTH,T2_WORKFLOW_NAMESPACE));
			ProcessorOutputPort port = edits.createProcessorOutputPort(result, portName, portDepth, granularDepth);
			edits.getAddProcessorOutputPortEdit(result,port).doEdit();
		}
		
		//TODO: annotations
		
		//Dispatch stack
		Element dispatchStack = el.getChild(DISPATCH_STACK,T2_WORKFLOW_NAMESPACE);
		DispatchStackXMLDeserializer.getInstance().deserializeDispatchStack(result, dispatchStack);
		
		
		//Iteration strategy
		Element iterationStrategyStack = el.getChild(ITERATION_STRATEGY_STACK,T2_WORKFLOW_NAMESPACE);
		IterationStrategyStackXMLDeserializer.getInstance().deserializeIterationStrategyStack(iterationStrategyStack, result.getIterationStrategy());
		
		return result;
		
	}
}
