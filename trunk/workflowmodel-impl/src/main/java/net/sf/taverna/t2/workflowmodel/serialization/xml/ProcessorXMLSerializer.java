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

import java.io.IOException;

import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchStack;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategyStack;

import org.jdom.Element;
import org.jdom.JDOMException;

public class ProcessorXMLSerializer extends AbstractXMLSerializer {
	private static ProcessorXMLSerializer instance = new ProcessorXMLSerializer();

	private ProcessorXMLSerializer() {

	}

	public static ProcessorXMLSerializer getInstance() {
		return instance;
	}

	public Element processorToXML(Processor processor) throws IOException,
			JDOMException {

		Element result = new Element(PROCESSOR, T2_WORKFLOW_NAMESPACE);
		Element nameElement = new Element(NAME, T2_WORKFLOW_NAMESPACE);
		nameElement.setText(processor.getLocalName());
		result.addContent(nameElement);

		// input and output ports
		Element inputPorts = processorInputPortsToXML(processor);
		Element outputPorts = processorOutputPortsToXML(processor);
		result.addContent(inputPorts);
		result.addContent(outputPorts);

		// annotations
		result.addContent(annotationsToXML(processor));

		// list of activities
		Element activities = new Element(ACTIVITIES, T2_WORKFLOW_NAMESPACE);
		for (Activity<?> activity : processor.getActivityList()) {
			activities.addContent(activityToXML(activity));
		}
		result.addContent(activities);

		// dispatch stack
		result.addContent(dispatchStackToXML(processor.getDispatchStack()));

		// iteration strategy
		result.addContent(iterationStrategyStackToXML(processor
				.getIterationStrategy()));

		return result;
	}

	protected Element dispatchStackToXML(DispatchStack stack)
			throws IOException, JDOMException {
		return DispatchStackXMLSerializer.getInstance().dispatchStackToXML(
				stack);
	}

	protected Element iterationStrategyStackToXML(
			IterationStrategyStack strategyStack) {
		return IterationStrategyStackXMLSerializer.getInstance()
				.iterationStrategyStackToXML(strategyStack);
	}

	protected Element activityToXML(Activity<?> activity) throws JDOMException,
			IOException {
		return ActivityXMLSerializer.getInstance().activityToXML(activity);
	}
	
	private Element processorOutputPortsToXML(Processor processor) {
		Element outputPorts = new Element(PROCESSOR_OUTPUT_PORTS,
				T2_WORKFLOW_NAMESPACE);
		for (ProcessorOutputPort port : processor.getOutputPorts()) {
			Element portElement = new Element(PROCESSOR_PORT,
					T2_WORKFLOW_NAMESPACE);
			Element name = new Element(NAME, T2_WORKFLOW_NAMESPACE);
			Element depth = new Element(DEPTH, T2_WORKFLOW_NAMESPACE);
			Element granularDepth = new Element(GRANULAR_DEPTH,
					T2_WORKFLOW_NAMESPACE);
			name.setText(port.getName());
			depth.setText(String.valueOf(port.getDepth()));
			granularDepth.setText(String.valueOf(port.getGranularDepth()));
			portElement.addContent(name);
			portElement.addContent(depth);
			portElement.addContent(granularDepth);
			outputPorts.addContent(portElement);
		}
		return outputPorts;
	}

	private Element processorInputPortsToXML(Processor processor) {
		Element inputPorts = new Element(PROCESSOR_INPUT_PORTS,
				T2_WORKFLOW_NAMESPACE);
		for (ProcessorInputPort port : processor.getInputPorts()) {
			Element portElement = new Element(PROCESSOR_PORT,
					T2_WORKFLOW_NAMESPACE);
			Element name = new Element(NAME, T2_WORKFLOW_NAMESPACE);
			Element depth = new Element(DEPTH, T2_WORKFLOW_NAMESPACE);
			name.setText(port.getName());
			depth.setText(String.valueOf(port.getDepth()));
			portElement.addContent(name);
			portElement.addContent(depth);
			inputPorts.addContent(portElement);
		}
		return inputPorts;
	}
}
