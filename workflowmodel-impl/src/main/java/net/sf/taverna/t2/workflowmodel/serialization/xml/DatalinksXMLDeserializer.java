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

import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;
import net.sf.taverna.t2.workflowmodel.Merge;
import net.sf.taverna.t2.workflowmodel.MergeOutputPort;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.serialization.DeserializationException;

import org.jdom.Element;

public class DatalinksXMLDeserializer extends AbstractXMLDeserializer {
	private static DatalinksXMLDeserializer instance = new DatalinksXMLDeserializer();

	private DatalinksXMLDeserializer() {

	}

	public static DatalinksXMLDeserializer getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public void buildDatalinks(Dataflow dataflow,
			Map<String, Processor> createdProcessors, Element datalinks)
			throws DeserializationException, EditException {
		for (Element datalink : (List<Element>) datalinks.getChildren(DATALINK,
				T2_WORKFLOW_NAMESPACE)) {
			Element sink = datalink.getChild(SINK, T2_WORKFLOW_NAMESPACE);
			Element source = datalink.getChild(SOURCE, T2_WORKFLOW_NAMESPACE);
			if (sink == null)
				throw new DeserializationException(
						"No sink defined for datalink:"
								+ elementToString(datalink));
			if (source == null)
				throw new DeserializationException(
						"No source defined for datalink:"
								+ elementToString(datalink));
			String sinkType = sink.getAttributeValue(DATALINK_TYPE);

			EventForwardingOutputPort sourcePort = determineLinkSourcePort(
					source, dataflow, createdProcessors);
			EventHandlingInputPort sinkPort = determineLinkSinkPort(sink,
					dataflow, createdProcessors);

			if (sourcePort == null)
				throw new DeserializationException(
						"Unable to determine source port for:"
								+ elementToString(datalink));
			if (sinkPort == null)
				throw new DeserializationException(
						"Unable to determine sink port for:"
								+ elementToString(datalink));
			if (sinkType.equals(DATALINK_TYPES.MERGE.toString())) {
				Merge merge;
				if (sinkPort.getIncomingLink() == null) {
					merge = edits.createMerge(dataflow);
					edits.getAddMergeEdit(dataflow, merge).doEdit();
				} else {
					if (sinkPort.getIncomingLink().getSource() instanceof MergeOutputPort) {
						merge = ((MergeOutputPort) sinkPort.getIncomingLink()
								.getSource()).getMerge();
					} else {
						throw new DeserializationException(
								"There was a merge port execpted to be connected to "
										+ sinkPort);
					}
				}
				if (merge == null)
					throw new DeserializationException(
							"Unable to find or create Merge for "
									+ elementToString(datalink));
				try {
					edits.getConnectMergedDatalinkEdit(merge, sourcePort, sinkPort)
					.doEdit();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			} else {
				Datalink link = edits.createDatalink(sourcePort, sinkPort);
				edits.getConnectDatalinkEdit(link).doEdit();
			}
		}

	}

	private EventForwardingOutputPort determineLinkSourcePort(Element source,
			Dataflow dataflow, Map<String, Processor> createdProcessors)
			throws DeserializationException, EditException {
		EventForwardingOutputPort result = null;
		String sourceType = source.getAttributeValue(DATALINK_TYPE);
		String portName = source.getChildText(PORT, T2_WORKFLOW_NAMESPACE);
		if (sourceType.equals(DATALINK_TYPES.PROCESSOR.toString())) {
			String processorName = source.getChildText(PROCESSOR,
					T2_WORKFLOW_NAMESPACE);
			result = findProcessorOutputPort(createdProcessors, portName,
					processorName);
		} else if (sourceType.equals(DATALINK_TYPES.DATAFLOW.toString())) {
			for (DataflowInputPort port : dataflow.getInputPorts()) {
				if (port.getName().equals(portName)) {
					result = port.getInternalOutputPort();
					break;
				}
			}
		} else if (sourceType.equals(DATALINK_TYPES.MERGE.toString())) {
			throw new DeserializationException(
					"The source type is marked as merge for:"
							+ elementToString(source) + " but should never be");
		} else {
			throw new DeserializationException(
					"Unable to recognise datalink type:" + sourceType);
		}
		return result;
	}

	private EventHandlingInputPort determineLinkSinkPort(Element sink,
			Dataflow dataflow, Map<String, Processor> createdProcessors)
			throws DeserializationException, EditException {
		EventHandlingInputPort result = null;
		String sinkType = sink.getAttributeValue(DATALINK_TYPE);
		String portName = sink.getChildText(PORT, T2_WORKFLOW_NAMESPACE);
		if (sinkType.equals(DATALINK_TYPES.PROCESSOR.toString())) {
			String processorName = sink.getChildText(PROCESSOR,
					T2_WORKFLOW_NAMESPACE);
			result = findProcessorInputPort(createdProcessors, portName,
					processorName);
		} else if (sinkType.equals(DATALINK_TYPES.DATAFLOW.toString())) {
			for (DataflowOutputPort port : dataflow.getOutputPorts()) {
				if (port.getName().equals(portName)) {
					result = port.getInternalInputPort();
					break;
				}
			}
		} else if (sinkType.equals(DATALINK_TYPES.MERGE.toString())) {
			String processorName = sink.getChildText(PROCESSOR,
					T2_WORKFLOW_NAMESPACE);
			String processorPort = sink.getChildText(PROCESSOR_PORT,
					T2_WORKFLOW_NAMESPACE);
			EventHandlingInputPort processorInputPort = findProcessorInputPort(
					createdProcessors, processorPort, processorName);
			result = processorInputPort;
		} else {
			throw new DeserializationException(
					"Unable to recognise datalink sink type:" + sinkType);
		}
		return result;
	}

	private EventHandlingInputPort findProcessorInputPort(
			Map<String, Processor> createdProcessors, String portName,
			String processorName) throws DeserializationException {
		EventHandlingInputPort result = null;
		Processor p = createdProcessors.get(processorName);
		if (p == null)
			throw new DeserializationException(
					"Unable to find processor named:" + processorName);
		for (ProcessorInputPort port : p.getInputPorts()) {
			if (port.getName().equals(portName)) {
				result = port;
				break;
			}
		}
		return result;
	}

	private EventForwardingOutputPort findProcessorOutputPort(
			Map<String, Processor> createdProcessors, String portName,
			String processorName) throws DeserializationException {
		EventForwardingOutputPort result = null;
		Processor p = createdProcessors.get(processorName);
		if (p == null)
			throw new DeserializationException(
					"Unable to find processor named:" + processorName);
		for (ProcessorOutputPort port : p.getOutputPorts()) {
			if (port.getName().equals(portName)) {
				result = port;
				break;
			}
		}
		return result;
	}
}
