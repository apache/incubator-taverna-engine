/*******************************************************************************
 * Copyright (C) 2007-2008 The University of Manchester   
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ListService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.InputPort;
import net.sf.taverna.t2.workflowmodel.Merge;
import net.sf.taverna.t2.workflowmodel.MergeInputPort;
import net.sf.taverna.t2.workflowmodel.WorkflowStructureException;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationTypeMismatchException;

import org.apache.log4j.Logger;

/**
 * Implementation of {@link Merge}
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 *
 */
public class MergeImpl implements Merge {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(MergeImpl.class);
	
	private List<MergeInputPortImpl> inputs = new ArrayList<MergeInputPortImpl>();

	private String name;

	private BasicEventForwardingOutputPort output;
	
	private Map<String, List<T2Reference>> partialOutputsByProcess = new HashMap<String, List<T2Reference>>();

	public MergeImpl(String mergeName) {
		super();
		this.name = mergeName;
		this.output = new MergeOutputPortImpl(this, name+"_output", 0, 0);
	}

	public String getLocalName() {
		return this.name;
	}

	/**
	 * Adds a new input port to the internal list of ports.
	 * 
	 * @param inputPort
	 *            the MergeInputPortImpl
	 */
	public void addInputPort(MergeInputPortImpl inputPort) {
		inputs.add(inputPort);
	}

	/**
	 * Removes an input port from the internal list of ports.
	 * 
	 * @param inputPort
	 */
	public void removeInputPort(MergeInputPortImpl inputPort) {
		inputs.remove(inputPort);
	}

	public List<? extends MergeInputPort> getInputPorts() {
		return inputs;
	}

	public EventForwardingOutputPort getOutputPort() {
		return this.output;
	}

	/**
	 * Return the index of the port with the specified name, or -1 if the port
	 * can't be found (this is a bad thing!)
	 * 
	 * @param portName
	 * @return
	 */
	private int inputPortNameToIndex(String portName) {
		int i = 0;
		for (InputPort ip : inputs) {
			if (ip.getName().equals(portName)) {
				return i;
			}
			i++;
		}
		return -1; // FIXME: as the javadoc states, this is a bad thing!
	}
	

	protected void receiveEvent(WorkflowDataToken token, String portName) {
		List<T2Reference> outputList;
		String owningProcess = token.getOwningProcess();
		synchronized (partialOutputsByProcess) {
			outputList = partialOutputsByProcess.get(owningProcess);
			if (outputList == null) {
				int numPorts = getInputPorts().size();
				outputList = new ArrayList<T2Reference>(Collections.nCopies(numPorts, (T2Reference)null));
				partialOutputsByProcess.put(owningProcess, outputList);
			}
		}
		int portIndex = inputPortNameToIndex(portName);
		if (portIndex == -1) {
			throw new WorkflowStructureException(
					"Received event on unknown port " + portName);
		}
		int[] currentIndex = token.getIndex();
		int[] newIndex = new int[currentIndex.length + 1];
		newIndex[0] = portIndex;
		for (int i = 0; i < currentIndex.length; i++) {
			newIndex[i + 1] = currentIndex[i];
		}
		InvocationContext context = token.getContext();
		output.sendEvent(new WorkflowDataToken(owningProcess,
				newIndex, token.getData(), context));
		if (token.getIndex().length == 0) {
			// Add to completion list
			synchronized (outputList) {
				if (outputList.size() <= portIndex) {
					// Ports changed after initiating running as our list is
					// smaller than portIndex
					throw new WorkflowStructureException(
							"Unexpected addition of output port " + portName
									+ " at " + portIndex);
				}
				if (outputList.get(portIndex) != null) {
					throw new WorkflowStructureException(
							"Already received completion for port " + portName
									+ " " + outputList.get(portIndex));
				}
				outputList.set(portIndex, token.getData());
				if (!outputList.contains(null)) {
					// We're finished, let's register and send out the list
					ListService listService = context.getReferenceService()
							.getListService();
					IdentifiedList<T2Reference> registeredList = listService
							.registerList(outputList);
					WorkflowDataToken workflowDataToken = new WorkflowDataToken(
							owningProcess, new int[0], registeredList.getId(),
							context);
					synchronized (partialOutputsByProcess) {
						partialOutputsByProcess.remove(owningProcess);
					}
					output.sendEvent(workflowDataToken);
				}
			}
		}
	}

	/**
	 * There is only ever a single output from a merge node but the token
	 * processing entity interface defines a list, in this case it always
	 * contains exactly one item.
	 */
	public List<? extends EventForwardingOutputPort> getOutputPorts() {
		List<EventForwardingOutputPort> result = new ArrayList<EventForwardingOutputPort>();
		result.add(output);
		return result;
	}

	public boolean doTypeCheck() throws IterationTypeMismatchException {
		if (inputs.size() == 0) {
			// Arguable, but technically a merge with no inputs is valid, it may
			// make more sense to throw an exception here though as it has no
			// actual meaning.
			return true;
		}
		// Return false if we have unbound input ports or bound ports where the
		// resolved depth hasn't been calculated yet
		for (MergeInputPort ip : inputs) {
			if (ip.getIncomingLink() == null
					|| ip.getIncomingLink().getResolvedDepth() == -1) {
				return false;
			}
		}
		// Got all input ports, now scan for input depths
		int inputDepth = inputs.get(0).getIncomingLink().getResolvedDepth();
		for (MergeInputPort ip : inputs) {
			if (ip.getIncomingLink().getResolvedDepth() != inputDepth) {
				throw new IterationTypeMismatchException();
			}
		}
		// Got to here so all the input resolved depths match, push depth+1 to
		// all outgoing links and return true
		for (DatalinkImpl dli : output.outgoingLinks) {
			dli.setResolvedDepth(inputDepth+1);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public void reorderInputPorts(
			List<? extends MergeInputPort> reorderedInputPortList) {
		// Just set the inputs to the already reordered list of ports
		inputs = (List<MergeInputPortImpl>) reorderedInputPortList;	
	}
}
