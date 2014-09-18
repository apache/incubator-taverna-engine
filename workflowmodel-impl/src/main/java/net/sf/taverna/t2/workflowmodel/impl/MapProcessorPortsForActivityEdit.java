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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.workflowmodel.CompoundEdit;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityOutputPort;

class MapProcessorPortsForActivityEdit extends EditSupport<Processor> {
	private final ProcessorImpl processor;
	CompoundEdit compoundEdit = null;

	public MapProcessorPortsForActivityEdit(Processor processor) {
		this.processor = (ProcessorImpl)processor;
	}
	
	@Override
	public Processor applyEdit() throws EditException {
		EditsImpl editsImpl = new EditsImpl();
		List<Edit<?>> edits = new ArrayList<>();
		Activity<?> a = processor.getActivityList().get(0);
		
		List<? extends ProcessorInputPort> inputPortsForRemoval = determineInputPortsForRemoval(processor,a);
		List<? extends ProcessorOutputPort> outputPortsForRemoval = determineOutputPortsForRemoval(processor,a);
		Map<? extends ProcessorInputPort, ActivityInputPort> changedInputPorts = determineChangedInputPorts(processor,a);
		Map<? extends ProcessorOutputPort, ActivityOutputPort> changedOutputPorts = determineChangedOutputPorts(processor,a);
		
		for (ProcessorInputPort ip : inputPortsForRemoval) {
			if (ip.getIncomingLink() != null)
				edits.add(editsImpl.getDisconnectDatalinkEdit(ip
						.getIncomingLink()));
			edits.add(editsImpl.getRemoveProcessorInputPortEdit(processor, ip));
			if (a.getInputPortMapping().containsKey(ip.getName()))
				edits.add(editsImpl.getRemoveActivityInputPortMappingEdit(a, ip
						.getName()));
		}
		
		for (ProcessorOutputPort op : outputPortsForRemoval) {
			if (!op.getOutgoingLinks().isEmpty())
				for (Datalink link : op.getOutgoingLinks())
					edits.add(editsImpl.getDisconnectDatalinkEdit(link));
			edits.add(editsImpl.getRemoveProcessorOutputPortEdit(processor, op));
			if (a.getOutputPortMapping().containsKey(op.getName()))
				edits.add(editsImpl.getRemoveActivityOutputPortMappingEdit(a, op
						.getName()));
		}

		for (ProcessorInputPort ip : changedInputPorts.keySet()) {
			Datalink incomingLink = ip.getIncomingLink();
			if (incomingLink != null)
				edits.add(editsImpl.getDisconnectDatalinkEdit(incomingLink));
			edits.add(editsImpl.getRemoveProcessorInputPortEdit(processor, ip));
			
			if (incomingLink != null) {
				ActivityInputPort aip = changedInputPorts.get(ip);
				ProcessorInputPort pip = new ProcessorInputPortImpl(processor,
						aip.getName(), aip.getDepth());
				edits.add(editsImpl
						.getAddProcessorInputPortEdit(processor, pip));
				edits.add(editsImpl.getConnectDatalinkEdit(new DatalinkImpl(
						incomingLink.getSource(), pip)));
			}
		}
		
		for (ProcessorOutputPort op : changedOutputPorts.keySet()) {
			Set<? extends Datalink> outgoingLinks = op.getOutgoingLinks();
			for (Datalink link : outgoingLinks)
				edits.add(editsImpl.getDisconnectDatalinkEdit(link));
			edits.add(editsImpl.getRemoveProcessorOutputPortEdit(processor, op));
			
			if (!outgoingLinks.isEmpty()) {
				ActivityOutputPort aop = changedOutputPorts.get(op);
				ProcessorOutputPort pop = new ProcessorOutputPortImpl(
						processor, aop.getName(), aop.getDepth(),
						aop.getGranularDepth());
				edits.add(editsImpl.getAddProcessorOutputPortEdit(processor,
						pop));
				for (Datalink link : outgoingLinks)
					edits.add(editsImpl
							.getConnectDatalinkEdit(new DatalinkImpl(pop, link
									.getSink())));
			}
		}

		compoundEdit = new CompoundEdit(edits);
		compoundEdit.doEdit();
		return processor;
	}

	@Override
	public Object getSubject() {
		return processor;
	}

	private List<ProcessorInputPort> determineInputPortsForRemoval(Processor p,
			Activity<?> a) {
		List<ProcessorInputPort> result = new ArrayList<>();
		processorPorts: for (ProcessorInputPort pPort : p.getInputPorts()) {
			for (ActivityInputPort aPort : a.getInputPorts())
				if (aPort.getName().equals(pPort.getName()))
					continue processorPorts;
			result.add(pPort);
		}
		return result;
	}
	
	private List<ProcessorOutputPort> determineOutputPortsForRemoval(
			Processor p, Activity<?> a) {
		List<ProcessorOutputPort> result = new ArrayList<>();
		processorPorts: for (ProcessorOutputPort pPort : p.getOutputPorts()) {
			for (OutputPort aPort : a.getOutputPorts())
				if (aPort.getName().equals(pPort.getName()))
					continue processorPorts;
			result.add(pPort);
		}
		return result;
	}
	
	private Map<? extends ProcessorInputPort, ActivityInputPort> determineChangedInputPorts(
			Processor p, Activity<?> a) {
		Map<ProcessorInputPort, ActivityInputPort> result = new HashMap<>();
		for (ProcessorInputPort pPort : p.getInputPorts())
			for (ActivityInputPort aPort : a.getInputPorts())
				if (aPort.getName().equals(pPort.getName())) {
					if (pPort.getDepth() != aPort.getDepth())
						result.put(pPort, aPort);
					break;
				}
		return result;
	}
	
	private Map<? extends ProcessorOutputPort, ActivityOutputPort> determineChangedOutputPorts(
			Processor p, Activity<?> a) {
		Map<ProcessorOutputPort, ActivityOutputPort> result = new HashMap<>();
		for (ProcessorOutputPort pPort : p.getOutputPorts())
			for (OutputPort aPort : a.getOutputPorts())
				if (aPort.getName().equals(pPort.getName())) {
					if ((pPort.getDepth() != aPort.getDepth())
							|| (pPort.getGranularDepth() != aPort
									.getGranularDepth()))
						result.put(pPort, (ActivityOutputPort) aPort);
					break;
				}
		return result;
	}
}
