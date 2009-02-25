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
import java.util.List;

import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.serialization.SerializationException;

import org.jdom.Element;
import org.jdom.JDOMException;

public class DataflowXMLSerializer extends AbstractXMLSerializer {
	private static DataflowXMLSerializer instance = new DataflowXMLSerializer();

	private DataflowXMLSerializer() {

	}

	public static DataflowXMLSerializer getInstance() {
		return instance;
	}

	protected Element serializeDataflow(Dataflow df)
			throws SerializationException {
		Element result = new Element(DATAFLOW, T2_WORKFLOW_NAMESPACE);
		result.setAttribute(DATAFLOW_ID,df.getInternalIdentier());
		try {
			
			Element name=new Element(NAME,T2_WORKFLOW_NAMESPACE);
			name.setText(df.getLocalName());
			result.addContent(name);

			// do dataflow inputs and outputs
			result.addContent(dataflowInputPorts(df.getInputPorts()));
			result.addContent(dataflowOutputPorts(df.getOutputPorts()));
			// do processors
			Element processors = new Element(PROCESSORS, T2_WORKFLOW_NAMESPACE);

			for (Processor processor : df.getProcessors()) {
				processors.addContent(processorToXML(processor));
			}
			result.addContent(processors);

			// do conditions
			result.addContent(conditionsToXML(df.getProcessors()));
			// do datalinks
			result.addContent(DatalinksXMLSerializer.getInstance().datalinksToXML(df.getLinks()));
			// do annotations
			result.addContent(annotationsToXML(df));
			
		}
		// FIXME: improve error reporting
		catch (JDOMException jdomException) {
			throw new SerializationException(
					"There was a problem generating the XML for the dataflow: " + jdomException.toString(),
					jdomException);
		} catch (IOException ioException) {
			throw new SerializationException(
					"There was a problem generating the XML for the dataflow: " + ioException.toString(),
					ioException);
		}
		return result;
	}

	private Element conditionsToXML(List<? extends Processor> processors) {
		return ConditionXMLSerializer.getInstance().conditionsToXML(processors);
	}

	private Element processorToXML(Processor processor) throws IOException,
			JDOMException {
		return ProcessorXMLSerializer.getInstance().processorToXML(processor);
	}

	protected Element dataflowInputPorts(
			List<? extends DataflowInputPort> inputPorts) {
		Element result = new Element(DATAFLOW_INPUT_PORTS,
				T2_WORKFLOW_NAMESPACE);
		for (DataflowInputPort port : inputPorts) {
			Element portElement = new Element(DATAFLOW_PORT,
					T2_WORKFLOW_NAMESPACE);
			Element name = new Element(NAME, T2_WORKFLOW_NAMESPACE);
			Element depth = new Element(DEPTH, T2_WORKFLOW_NAMESPACE);
			Element granularDepth = new Element(GRANULAR_DEPTH,
					T2_WORKFLOW_NAMESPACE);

			name.setText(port.getName());
			depth.setText(String.valueOf(port.getDepth()));
			granularDepth.setText(String.valueOf(port.getGranularInputDepth()));

			portElement.addContent(name);
			portElement.addContent(depth);
			portElement.addContent(granularDepth);
			result.addContent(portElement);
		}
		return result;
	}

	protected Element dataflowOutputPorts(
			List<? extends DataflowOutputPort> outputPorts) {
		Element result = new Element(DATAFLOW_OUTPUT_PORTS,
				T2_WORKFLOW_NAMESPACE);
		for (DataflowOutputPort port : outputPorts) {
			Element portElement = new Element(DATAFLOW_PORT,
					T2_WORKFLOW_NAMESPACE);
			Element name = new Element(NAME, T2_WORKFLOW_NAMESPACE);
			name.setText(port.getName());

			portElement.addContent(name);
			result.addContent(portElement);
		}
		return result;

	}
}
