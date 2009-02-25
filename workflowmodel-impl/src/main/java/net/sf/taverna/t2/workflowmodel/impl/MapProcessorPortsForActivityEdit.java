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
import java.util.List;

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

public class MapProcessorPortsForActivityEdit implements Edit<Processor> {

	private final ProcessorImpl processor;
	CompoundEdit compoundEdit = null;
	

	public MapProcessorPortsForActivityEdit(Processor processor) {
		this.processor = (ProcessorImpl)processor;
	}
	
	public Processor doEdit() throws EditException {
		EditsImpl editsImpl = new EditsImpl();
		List<Edit<?>> edits = new ArrayList<Edit<?>>();
		Activity<?> a = processor.getActivityList().get(0);
		
		List<? extends ProcessorInputPort> inputPortsForRemoval = determineInputPortsForRemoval(processor,a);
		List<? extends ProcessorOutputPort> outputPortsForRemoval = determineOutputPortsForRemoval(processor,a);
//		List<ActivityInputPort> changedInputPorts = determineChangedInputPorts(processor,a);
//		List<OutputPort> changedOutputPorts = determineChangedOutputPorts(processor,a);
//		List<ActivityInputPort> newInputPorts = determineNewInputPorts(processor,a);
//		List<OutputPort> newOutputPorts = determineNewOutputPorts(processor,a);
		
		for (ProcessorInputPort ip : inputPortsForRemoval) {
			if (ip.getIncomingLink()!=null) {
				edits.add(editsImpl.getDisconnectDatalinkEdit(ip.getIncomingLink()));
			}
			edits.add(editsImpl.getRemoveProcessorInputPortEdit(processor, ip));
			if (a.getInputPortMapping().containsKey(ip.getName())) {
				edits.add(new RemoveActivityInputPortMappingEdit(a,ip.getName()));
			}
		}
		
		for (ProcessorOutputPort op : outputPortsForRemoval) {
			if (op.getOutgoingLinks().size()>0) {
				for (Datalink link : op.getOutgoingLinks())
				edits.add(editsImpl.getDisconnectDatalinkEdit(link));
			}
			edits.add(editsImpl.getRemoveProcessorOutputPortEdit(processor, op));
			if (a.getOutputPortMapping().containsKey(op.getName())) {
				edits.add(new RemoveActivityOutputPortMappingEdit(a,op.getName()));
			}
		}
		
//		for (ActivityInputPort ip : changedInputPorts) {
//			ProcessorInputPort pPort = processor.getInputPortWithName(ip.getName());
//			edits.add(new ChangeProcessorInputPortDepthEdit(pPort,ip.getDepth()));
//		}
//		
//		for (OutputPort op : changedOutputPorts) {
//			ProcessorOutputPort pPort = processor.getOutputPortWithName(op.getName());
//			if (pPort.getDepth() != op.getDepth()) {
//				edits.add(new ChangeProcessorOutputPortDepthEdit(pPort,op.getDepth()));
//			}
//			if (pPort.getGranularDepth() != op.getGranularDepth()) {
//				edits.add(new ChangeProcessorOutputPortGranularDepthEdit(pPort,op.getGranularDepth()));
//			}
//		}
		
//		for (ActivityInputPort ip : newInputPorts) {
//			ProcessorInputPort processorInputPort = editsImpl.createProcessorInputPort(processor, ip.getName(), ip.getDepth());
//			edits.add(editsImpl.getAddProcessorInputPortEdit(processor, processorInputPort));
//			edits.add(new AddActivityInputPortMapping(a,ip.getName(),ip.getName()));
//		}
//		
//		for (OutputPort op : newOutputPorts) {
//			ProcessorOutputPort processorOutputPort = editsImpl.createProcessorOutputPort(processor, op.getName(), op.getDepth(), op.getGranularDepth());
//			edits.add(editsImpl.getAddProcessorOutputPortEdit(processor, processorOutputPort));
//			edits.add(new AddActivityOutputPortMapping(a,op.getName(),op.getName()));
//		}
		
		compoundEdit = new CompoundEdit(edits);
		compoundEdit.doEdit();
		return processor;
	}

	public Object getSubject() {
		return processor;
	}

	public boolean isApplied() {
		return (compoundEdit!=null && compoundEdit.isApplied());
	}

	public void undo() {
		compoundEdit.undo();
	}

	private List<ProcessorInputPort> determineInputPortsForRemoval(Processor p,Activity<?>a) {
		
		List<ProcessorInputPort> result = new ArrayList<ProcessorInputPort>();
		for (ProcessorInputPort pPort : p.getInputPorts()) {
			boolean found=false;
			for (ActivityInputPort aPort : a.getInputPorts()) {
				if (aPort.getName().equals(pPort.getName())) {
					if (pPort.getDepth() == aPort.getDepth()) {
						found=true;
					}
					break;
				}
			}
			if (!found) {
				result.add(pPort);
			}
		}
		return result;
	}
	
	private List<ProcessorOutputPort> determineOutputPortsForRemoval(Processor p,Activity<?>a) {
		List<ProcessorOutputPort> result = new ArrayList<ProcessorOutputPort>();
		for (ProcessorOutputPort pPort : p.getOutputPorts()) {
			boolean found=false;
			for (OutputPort aPort : a.getOutputPorts()) {
				if (aPort.getName().equals(pPort.getName())) {
					if (pPort.getDepth() == aPort.getDepth() && pPort.getGranularDepth() == aPort.getGranularDepth()) {
						found=true;
					}
					break;
				}
			}
			if (!found) {
				result.add(pPort);
			}
		}
		return result;
	}
	
//	private List<ActivityInputPort> determineChangedInputPorts(ProcessorImpl p,Activity<?>a) {
//		
//		List<ActivityInputPort> result = new ArrayList<ActivityInputPort>();
//		for (ActivityInputPort aPort : a.getInputPorts()) {
//			ProcessorInputPort pPort = p.getInputPortWithName(aPort.getName());
//			
//			if (pPort!=null && pPort.getDepth()!=aPort.getDepth()) {
//				result.add(aPort);
//			}
//		}
//		return result;
//	}
//	
//	private List<OutputPort> determineChangedOutputPorts(ProcessorImpl p,Activity<?>a) {
//		List<OutputPort> result = new ArrayList<OutputPort>();
//		for (OutputPort aPort : a.getOutputPorts()) {
//			ProcessorOutputPort pPort = p.getOutputPortWithName(aPort.getName());
//			
//			if (pPort!=null && (pPort.getDepth()!=aPort.getDepth() || pPort.getGranularDepth()!=aPort.getGranularDepth())) {
//				result.add(aPort);
//			}
//		}
//		return result;
//	}
	
//	private List<ActivityInputPort> determineNewInputPorts(ProcessorImpl p,Activity<?> a) {
//		List<ActivityInputPort> result = new ArrayList<ActivityInputPort>();
//		for (ActivityInputPort aPort : a.getInputPorts()) {
//			ProcessorInputPort pPort = p.getInputPortWithName(aPort.getName());
//			
//			if (pPort==null) {
//				result.add(aPort);
//			}
//		}
//		
//		return result;
//	}
//	
//	private List<OutputPort> determineNewOutputPorts(ProcessorImpl p,Activity<?> a) {
//		List<OutputPort> result = new ArrayList<OutputPort>();
//		for (OutputPort aPort : a.getOutputPorts()) {
//			ProcessorOutputPort pPort = p.getOutputPortWithName(aPort.getName());
//			
//			if (pPort==null) {
//				result.add(aPort);
//			}
//		}
//		
//		return result;
//	}
}
