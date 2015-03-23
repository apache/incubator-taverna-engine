/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.workflowmodel.impl;

import static java.lang.System.arraycopy;
import static java.util.Collections.nCopies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.reference.IdentifiedList;
import org.apache.taverna.reference.ListService;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.EventForwardingOutputPort;
import org.apache.taverna.workflowmodel.InputPort;
import org.apache.taverna.workflowmodel.Merge;
import org.apache.taverna.workflowmodel.MergeInputPort;
import org.apache.taverna.workflowmodel.WorkflowStructureException;
import org.apache.taverna.workflowmodel.processor.iteration.IterationTypeMismatchException;

import org.apache.log4j.Logger;

/**
 * Implementation of {@link Merge}
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 *
 */
class MergeImpl implements Merge {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(MergeImpl.class);
	
	private List<MergeInputPortImpl> inputs = new ArrayList<>();
	private String name;
	private BasicEventForwardingOutputPort output;
	private Map<String, List<T2Reference>> partialOutputsByProcess = new HashMap<>();

	public MergeImpl(String mergeName) {
		super();
		this.name = mergeName;
		this.output = new MergeOutputPortImpl(this, name + "_output", 0, 0);
	}

	@Override
	public String getLocalName() {
		return this.name;
	}
	
	protected void setName(String name) {
		this.name = name;
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

	@Override
	public List<? extends MergeInputPort> getInputPorts() {
		return inputs;
	}

	@Override
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
			if (ip.getName().equals(portName))
				return i;
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
				outputList = new ArrayList<>(nCopies(numPorts, (T2Reference) null));
				partialOutputsByProcess.put(owningProcess, outputList);
			}
		}
		int portIndex = inputPortNameToIndex(portName);
		if (portIndex == -1)
			throw new WorkflowStructureException(
					"Received event on unknown port " + portName);
		int[] currentIndex = token.getIndex();
		int[] newIndex = new int[currentIndex.length + 1];
		newIndex[0] = portIndex;
		arraycopy(currentIndex, 0, newIndex, 1, currentIndex.length);
		InvocationContext context = token.getContext();
		output.sendEvent(new WorkflowDataToken(owningProcess,
				newIndex, token.getData(), context));
		if (token.getIndex().length == 0)
			// Add to completion list
			synchronized (outputList) {
				if (outputList.size() <= portIndex)
					// Ports changed after initiating running as our list is
					// smaller than portIndex
					throw new WorkflowStructureException(
							"Unexpected addition of output port " + portName
									+ " at " + portIndex);
				if (outputList.get(portIndex) != null)
					throw new WorkflowStructureException(
							"Already received completion for port " + portName
									+ " " + outputList.get(portIndex));

				outputList.set(portIndex, token.getData());
				if (!outputList.contains(null)) {
					// We're finished, let's register and send out the list
					ListService listService = context.getReferenceService()
							.getListService();
					IdentifiedList<T2Reference> registeredList = listService
							.registerList(outputList, context);
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

	/**
	 * There is only ever a single output from a merge node but the token
	 * processing entity interface defines a list, in this case it always
	 * contains exactly one item.
	 */
	@Override
	public List<? extends EventForwardingOutputPort> getOutputPorts() {
		List<EventForwardingOutputPort> result = new ArrayList<>();
		result.add(output);
		return result;
	}

	@Override
	public boolean doTypeCheck() throws IterationTypeMismatchException {
		if (inputs.size() == 0)
			/*
			 * Arguable, but technically a merge with no inputs is valid, it may
			 * make more sense to throw an exception here though as it has no
			 * actual meaning.
			 */
			return true;
		/*
		 * Return false if we have unbound input ports or bound ports where the
		 * resolved depth hasn't been calculated yet
		 */
		for (MergeInputPort ip : inputs)
			if (ip.getIncomingLink() == null
					|| ip.getIncomingLink().getResolvedDepth() == -1)
				return false;

		// Got all input ports, now scan for input depths
		int inputDepth = inputs.get(0).getIncomingLink().getResolvedDepth();
		for (MergeInputPort ip : inputs)
			if (ip.getIncomingLink().getResolvedDepth() != inputDepth)
				throw new IterationTypeMismatchException();

		// Set the granular depth to be the input depth as this will be the granularity of the output
		output.setGranularDepth(inputDepth);
		/*
		 * Got to here so all the input resolved depths match, push depth+1 to
		 * all outgoing links and return true
		 */
		for (DatalinkImpl dli : output.outgoingLinks)
			dli.setResolvedDepth(inputDepth+1);
		return true;
	}

	@SuppressWarnings("unchecked")
	public void reorderInputPorts(
			List<? extends MergeInputPort> reorderedInputPortList) {
		// Just set the inputs to the already reordered list of ports
		inputs = (List<MergeInputPortImpl>) reorderedInputPortList;	
	}
	
	@Override
	public String toString() {
		return "Merge " + getLocalName();
	}
}
