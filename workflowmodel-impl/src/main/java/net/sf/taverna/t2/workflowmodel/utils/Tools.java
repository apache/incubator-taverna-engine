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
package net.sf.taverna.t2.workflowmodel.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.taverna.t2.workflowmodel.CompoundEdit;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EditsRegistry;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;
import net.sf.taverna.t2.workflowmodel.InputPort;
import net.sf.taverna.t2.workflowmodel.Merge;
import net.sf.taverna.t2.workflowmodel.MergeInputPort;
import net.sf.taverna.t2.workflowmodel.MergeOutputPort;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.Port;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.TokenProcessingEntity;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.NestedDataflow;

public class Tools {

	private static Edits edits = EditsRegistry.getEdits();

	/**
	 * Find (and possibly create) an EventHandlingInputPort.
	 * <p>
	 * If the given inputPort is an instance of {@link EventHandlingInputPort},
	 * it is returned directly. If it is an ActivityInputPort - the owning
	 * processors (found by searching the dataflow) will be searced for a mapped
	 * input port. If this cannot be found, one will be created and mapped. The
	 * edits for this will be added to the editList and needs to be executed by
	 * the caller.
	 * 
	 * @see #findEventHandlingOutputPort(List, Dataflow, OutputPort)
	 * @param editList
	 *            List of {@link Edit}s to append any required edits (yet to be
	 *            performed) to
	 * @param dataflow
	 *            Dataflow containing the processors
	 * @param inputPort
	 *            An EventHandlingInputPort or ActivityInputPort
	 * @return The found or created EventHandlingInputPort
	 */
	@SuppressWarnings("unchecked")
	protected static EventHandlingInputPort findEventHandlingInputPort(
			List<Edit<?>> editList, Dataflow dataflow, InputPort inputPort) {
		if (inputPort instanceof EventHandlingInputPort) {
			return (EventHandlingInputPort) inputPort;
		} else if (!(inputPort instanceof ActivityInputPort)) {
			throw new IllegalArgumentException("Unknown input port type for "
					+ inputPort);
		}
		ActivityInputPort activityInput = (ActivityInputPort) inputPort;
		Collection<Processor> processors = Tools
				.getProcessorsWithActivityInputPort(dataflow, activityInput);
		if (processors.isEmpty()) {
			throw new IllegalArgumentException("Can't find ActivityInputPort "
					+ activityInput.getName() + " in dataflow " + dataflow);
		}
		// FIXME: Assumes only one matching processor
		Processor processor = processors.iterator().next();
		Activity activity = null;
		for (Activity checkActivity : processor.getActivityList()) {
			if (checkActivity.getInputPorts().contains(activityInput)) {
				activity = checkActivity;
				break;
			}
		}
		if (activity == null) {
			throw new IllegalArgumentException("Can't find activity for port "
					+ activityInput.getName() + "within processor " + processor);
		}

		ProcessorInputPort input = Tools.getProcessorInputPort(processor,
				activity, activityInput);
		if (input != null) {
			return input;
		}
		// port doesn't exist so create a processor port and map it
		String processorPortName = uniquePortName(activityInput.getName(),
				processor.getInputPorts());
		ProcessorInputPort processorInputPort = edits.createProcessorInputPort(
				processor, processorPortName, activityInput.getDepth());
		editList.add(edits.getAddProcessorInputPortEdit(processor,
				processorInputPort));
		editList.add(edits.getAddActivityInputPortMappingEdit(activity,
				processorPortName, activityInput.getName()));
		return processorInputPort;
	}

	/**
	 * Find (and possibly create) an EventForwardingOutputPort.
	 * <p>
	 * If the given outputPort is an instance of
	 * {@link EventForwardingOutputPort}, it is returned directly. If it is an
	 * ActivityOutputPort - the owning processors (found by searching the
	 * dataflow) will be searced for a mapped output port. If this cannot be
	 * found, one will be created and mapped. The edits for this will be added
	 * to the editList and needs to be executed by the caller.
	 * 
	 * @see #findEventHandlingInputPort(List, Dataflow, InputPort)
	 * @param editList
	 *            List of {@link Edit}s to append any required edits (yet to be
	 *            performed) to
	 * @param dataflow
	 *            Dataflow containing the processors
	 * @param outputPort
	 *            An EventForwardingOutputPort or ActivityOutputPort
	 * @return The found or created EventForwardingOutputPort
	 */
	@SuppressWarnings("unchecked")
	protected static EventForwardingOutputPort findEventHandlingOutputPort(
			List<Edit<?>> editList, Dataflow dataflow, OutputPort outputPort) {
		if (outputPort instanceof EventForwardingOutputPort) {
			return (EventForwardingOutputPort) outputPort;
		} else if (!(outputPort instanceof ActivityOutputPort)) {
			throw new IllegalArgumentException("Unknown output port type for "
					+ outputPort);
		}
		ActivityOutputPort activityOutput = (ActivityOutputPort) outputPort;
		Collection<Processor> processors = Tools
				.getProcessorsWithActivityOutputPort(dataflow, activityOutput);
		if (processors.isEmpty()) {
			throw new IllegalArgumentException("Can't find ActivityOutputPort "
					+ activityOutput.getName() + " in dataflow " + dataflow);
		}
		// FIXME: Assumes only one matching processor
		Processor processor = processors.iterator().next();
		Activity activity = null;
		for (Activity checkActivity : processor.getActivityList()) {
			if (checkActivity.getOutputPorts().contains(activityOutput)) {
				activity = checkActivity;
				break;
			}
		}
		if (activity == null) {
			throw new IllegalArgumentException("Can't find activity for port "
					+ activityOutput.getName() + "within processor "
					+ processor);
		}

		ProcessorOutputPort processorOutputPort = Tools.getProcessorOutputPort(
				processor, activity, activityOutput);
		if (processorOutputPort != null) {
			return processorOutputPort;
		}
		// port doesn't exist so create a processor port and map it
		String processorPortName = uniquePortName(activityOutput.getName(),
				processor.getOutputPorts());
		processorOutputPort = edits.createProcessorOutputPort(processor,
				processorPortName, activityOutput.getDepth(), activityOutput
						.getGranularDepth());
		editList.add(edits.getAddProcessorOutputPortEdit(processor,
				processorOutputPort));
		editList.add(edits.getAddActivityOutputPortMappingEdit(activity,
				processorPortName, activityOutput.getName()));

		return processorOutputPort;
	}

	/**
	 * Creates an Edit that creates a Datalink between a source and sink port
	 * and connects the Datalink.
	 * 
	 * If the sink port already has a Datalink connected this method checks if a
	 * new Merge is required and creates and connects the required Datalinks.
	 * 
	 * @param dataflow
	 *            the Dataflow to add the Datalink to
	 * @param source
	 *            the source of the Datalink
	 * @param sink
	 *            the source of the Datalink
	 * @return an Edit that creates a Datalink between a source and sink port
	 *         and connects the Datalink
	 */
	public static Edit<?> getCreateAndConnectDatalinkEdit(Dataflow dataflow,
			EventForwardingOutputPort source, EventHandlingInputPort sink) {
		Edit<?> edit = null;

		Datalink incomingLink = sink.getIncomingLink();
		if (incomingLink == null) {
			Datalink datalink = edits.createDatalink(source, sink);
			edit = edits.getConnectDatalinkEdit(datalink);
		} else {
			List<Edit<?>> editList = new ArrayList<Edit<?>>();

			Merge merge = null;
			int counter = 0; // counter for merge input port names
			if (incomingLink.getSource() instanceof MergeOutputPort) {
				merge = ((MergeOutputPort) incomingLink.getSource()).getMerge();
			} else {
				merge = edits.createMerge(dataflow);
				editList.add(edits.getAddMergeEdit(dataflow, merge));
				editList.add(edits.getDisconnectDatalinkEdit(incomingLink));
				MergeInputPort mergeInputPort = edits.createMergeInputPort(
						merge, getUniqueMergeInputPortName(merge, incomingLink
								.getSource().getName()
								+ "To" + merge.getLocalName() + "_input",
								counter++), incomingLink.getSink().getDepth());
				editList.add(edits.getAddMergeInputPortEdit(merge,
						mergeInputPort));
				Datalink datalink = edits.createDatalink(incomingLink
						.getSource(), mergeInputPort);
				editList.add(edits.getConnectDatalinkEdit(datalink));
				datalink = edits.createDatalink(merge.getOutputPort(),
						incomingLink.getSink());
				editList.add(edits.getConnectDatalinkEdit(datalink));
			}
			MergeInputPort mergeInputPort = edits.createMergeInputPort(merge,
					getUniqueMergeInputPortName(merge, source.getName() + "To"
							+ merge.getLocalName() + "_input", counter), sink
							.getDepth());
			editList.add(edits.getAddMergeInputPortEdit(merge, mergeInputPort));
			Datalink datalink = edits.createDatalink(source, mergeInputPort);
			editList.add(edits.getConnectDatalinkEdit(datalink));

			edit = new CompoundEdit(editList);
		}

		return edit;
	}

	/**
	 * Get an {@link Edit} that will link the given output port to the given
	 * input port.
	 * <p>
	 * The output port can be an {@link EventForwardingOutputPort} (such as an
	 * {@link ProcessorOutputPort}, or an {@link ActivityOutputPort}. The input
	 * port can be an {@link EventHandlingInputPort} (such as an
	 * {@link ProcessorInputPort}, or an {@link ActivityInputPort}.
	 * <p>
	 * If an input and/or output port is an activity port, processors in the
	 * given dataflow will be searched for matching mappings, create the
	 * processor port and mapping if needed, before constructing the edits for
	 * adding the datalink.
	 * 
	 * @param dataflow
	 *            Dataflow (indirectly) containing ports
	 * @param outputPort
	 *            An {@link EventForwardingOutputPort} or an
	 *            {@link ActivityOutputPort}
	 * @param inputPort
	 *            An {@link EventHandlingInputPort} or an
	 *            {@link ActivityInputPort}
	 * @return A compound edit for creating and connecting the datalink and any
	 *         neccessary processor ports and mappings
	 */
	public static Edit<?> getCreateAndConnectDatalinkEdit(Dataflow dataflow,
			OutputPort outputPort, InputPort inputPort) {

		List<Edit<?>> editList = new ArrayList<Edit<?>>();
		EventHandlingInputPort sink = findEventHandlingInputPort(editList,
				dataflow, inputPort);
		EventForwardingOutputPort source = findEventHandlingOutputPort(
				editList, dataflow, outputPort);
		editList.add(getCreateAndConnectDatalinkEdit(dataflow, source, sink));
		return new CompoundEdit(editList);
	}

	public static String uniquePortName(String portName,
			Collection<? extends Port> existingPorts) {
		// Make sure we have a unique port name
		Set<String> existingNames = new HashSet<String>();
		for (Port existingPort : existingPorts) {
			existingNames.add(existingPort.getName());
		}
		String candidateName = portName;
		int counter = 2;
		while (existingNames.contains(candidateName)) {
			candidateName = portName + counter++;
		}
		return candidateName;
	}

	public static Edit<?> getMoveDatalinkSinkEdit(Dataflow dataflow,
			Datalink datalink, EventHandlingInputPort sink) {
		List<Edit<?>> editList = new ArrayList<Edit<?>>();
		editList.add(edits.getDisconnectDatalinkEdit(datalink));
		if (datalink.getSink() instanceof ProcessorInputPort) {
			editList
					.add(getRemoveProcessorInputPortEdit((ProcessorInputPort) datalink
							.getSink()));
		}
		editList.add(getCreateAndConnectDatalinkEdit(dataflow, datalink
				.getSource(), sink));
		return new CompoundEdit(editList);
	}

	public static Edit<?> getDisconnectDatalinkAndRemovePortsEdit(
			Datalink datalink) {
		List<Edit<?>> editList = new ArrayList<Edit<?>>();
		editList.add(edits.getDisconnectDatalinkEdit(datalink));
		if (datalink.getSource() instanceof ProcessorOutputPort) {
			ProcessorOutputPort processorOutputPort = (ProcessorOutputPort) datalink
					.getSource();
			if (processorOutputPort.getOutgoingLinks().size() == 1) {
				editList
						.add(getRemoveProcessorOutputPortEdit(processorOutputPort));
			}
		}
		if (datalink.getSink() instanceof ProcessorInputPort) {
			editList
					.add(getRemoveProcessorInputPortEdit((ProcessorInputPort) datalink
							.getSink()));
		}
		return new CompoundEdit(editList);
	}

	public static Edit<?> getRemoveProcessorOutputPortEdit(
			ProcessorOutputPort port) {
		List<Edit<?>> editList = new ArrayList<Edit<?>>();
		Processor processor = port.getProcessor();
		editList.add(edits.getRemoveProcessorOutputPortEdit(
				port.getProcessor(), port));
		for (Activity<?> activity : processor.getActivityList()) {
			editList.add(edits.getRemoveActivityOutputPortMappingEdit(activity,
					port.getName()));
		}
		return new CompoundEdit(editList);
	}

	public static Edit<?> getRemoveProcessorInputPortEdit(
			ProcessorInputPort port) {
		List<Edit<?>> editList = new ArrayList<Edit<?>>();
		Processor processor = port.getProcessor();
		editList.add(edits.getRemoveProcessorInputPortEdit(port.getProcessor(),
				port));
		for (Activity<?> activity : processor.getActivityList()) {
			editList.add(edits.getRemoveActivityInputPortMappingEdit(activity,
					port.getName()));
		}
		return new CompoundEdit(editList);
	}

	public static ProcessorInputPort getProcessorInputPort(Processor processor,
			Activity<?> activity, InputPort activityInputPort) {
		ProcessorInputPort result = null;
		for (Entry<String, String> mapEntry : activity.getInputPortMapping()
				.entrySet()) {
			if (mapEntry.getValue().equals(activityInputPort.getName())) {
				for (ProcessorInputPort processorInputPort : processor
						.getInputPorts()) {
					if (processorInputPort.getName().equals(mapEntry.getKey())) {
						result = processorInputPort;
						break;
					}
				}
				break;
			}
		}
		return result;
	}

	public static ProcessorOutputPort getProcessorOutputPort(
			Processor processor, Activity<?> activity,
			OutputPort activityOutputPort) {
		ProcessorOutputPort result = null;
		for (Entry<String, String> mapEntry : activity.getOutputPortMapping()
				.entrySet()) {
			if (mapEntry.getValue().equals(activityOutputPort.getName())) {
				for (ProcessorOutputPort processorOutputPort : processor
						.getOutputPorts()) {
					if (processorOutputPort.getName().equals(mapEntry.getKey())) {
						result = processorOutputPort;
						break;
					}
				}
				break;
			}
		}
		return result;
	}

	public static ActivityInputPort getActivityInputPort(Activity<?> activity,
			String portName) {
		ActivityInputPort activityInputPort = null;
		for (ActivityInputPort inputPort : activity.getInputPorts()) {
			if (inputPort.getName().equals(portName)) {
				activityInputPort = inputPort;
				break;
			}
		}
		return activityInputPort;
	}

	public static OutputPort getActivityOutputPort(Activity<?> activity,
			String portName) {
		OutputPort activityOutputPort = null;
		for (OutputPort outputPort : activity.getOutputPorts()) {
			if (outputPort.getName().equals(portName)) {
				activityOutputPort = outputPort;
				break;
			}
		}
		return activityOutputPort;
	}

	public static String getUniqueMergeInputPortName(Merge merge, String name,
			int count) {
		String uniqueName = name + count;
		for (MergeInputPort mergeInputPort : merge.getInputPorts()) {
			if (mergeInputPort.getName().equals(uniqueName)) {
				return getUniqueMergeInputPortName(merge, name, ++count);
			}
		}
		return uniqueName;
	}

	public static Collection<Processor> getProcessorsWithActivity(
			Dataflow dataflow, Activity<?> activity) {
		Set<Processor> processors = new HashSet<Processor>();
		for (Processor processor : dataflow.getProcessors()) {
			if (processor.getActivityList().contains(activity)) {
				processors.add(processor);
			}
		}
		return processors;

	}

	public static Collection<Processor> getProcessorsWithActivityInputPort(
			Dataflow dataflow, ActivityInputPort inputPort) {

		Set<Processor> processors = new HashSet<Processor>();
		for (Processor processor : dataflow.getProcessors()) {

			// Does it contain a nested workflow?
			if (containsNestedWorkflow(processor)) {
				// Get the nested workflow and check all its nested processors
				Dataflow nestedWorkflow = ((NestedDataflow) processor
						.getActivityList().get(0)).getNestedDataflow();
				Collection<Processor> nested_processors = getProcessorsWithActivityInputPort(
						nestedWorkflow, inputPort);
				if (!nested_processors.isEmpty())
					processors.addAll(nested_processors);
			}

			// Check all processor's activities (even if the processor contained
			// a nested workflow,
			// as its dataflow activity still contains input and output ports)
			for (Activity<?> activity : processor.getActivityList()) {

				if (activity.getInputPorts().contains(inputPort)) {
					processors.add(processor);
				}
			}
		}
		return processors;
	}

	public static Collection<Processor> getProcessorsWithActivityOutputPort(
			Dataflow dataflow, OutputPort outputPort) {
		Set<Processor> processors = new HashSet<Processor>();
		for (Processor processor : dataflow.getProcessors()) {

			// Does it contain a nested workflow?
			if (containsNestedWorkflow(processor)) {
				// Get the nested workflow and check all its nested processors
				Dataflow nestedWorkflow = ((NestedDataflow) processor
						.getActivityList().get(0)).getNestedDataflow();
				Collection<Processor> nested_processors = getProcessorsWithActivityOutputPort(
						nestedWorkflow, outputPort);
				if (!nested_processors.isEmpty())
					processors.addAll(nested_processors);
			}

			// Check all processor's activities (even if the processor contained
			// a nested workflow,
			// as its dataflow activity still contains input and output ports)
			for (Activity<?> activity : processor.getActivityList()) {

				if (activity.getOutputPorts().contains(outputPort)) {
					processors.add(processor);
				}
			}
		}
		return processors;
	}

	/**
	 * Get the TokenProcessingEntity (Processor, Merge or Dataflow) 
	 * from the workflow that contains the given EventForwardingOutputPort. 
	 * This can be an output port of a Processor or a Merge or an input port of a Dataflow 
	 * that has an internal EventForwardingOutputPort attached to it. 
	 * @param port
	 * @param workflow 
	 * @return
	 */
	public static TokenProcessingEntity getTokenProcessingEntityWithEventForwardingOutputPort(
			EventForwardingOutputPort port, Dataflow workflow) {

		// First check the workflow's inputs
		for (DataflowInputPort input : workflow.getInputPorts()){
			if (input.getInternalOutputPort().equals(port)){
				return workflow;
			}
		}
		
		// Check workflow's merges
		List<? extends Merge> merges = workflow.getMerges();
		for (Merge merge : merges){
			if (merge.getOutputPort().equals(port)){
				return merge;
			}
		}
		
		// Check workflow's processors
		List<? extends Processor> processors = workflow.getProcessors();
		for (Processor processor : processors){
			for (OutputPort output : processor.getOutputPorts()){
				if (output.equals(port)) {
					return processor;
				}
			}

			// If processor contains a nested workflow - descend into it
			if (containsNestedWorkflow(processor)){
				Dataflow nestedWorkflow = ((NestedDataflow) processor.getActivityList().get(0)).getNestedDataflow();
				TokenProcessingEntity entity = getTokenProcessingEntityWithEventForwardingOutputPort(port, nestedWorkflow);
				if (entity != null){
					return entity;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Get the TokenProcessingEntity (Processor, Merge or Dataflow) from 
	 * the workflow that contains the given target EventHandlingInputPort. 
	 * This can be an input port of a Processor or a Merge or an output port of a Dataflow 
	 * that has an internal EventHandlingInputPort attached to it. 
	 * @param port
	 * @param workflow 
	 * @return
	 */
	public static TokenProcessingEntity getTokenProcessingEntityWithEventHandlingInputPort(
			EventHandlingInputPort port, Dataflow workflow) {

		// First check the workflow's outputs
		for (DataflowOutputPort output : workflow.getOutputPorts()){
			if (output.getInternalInputPort().equals(port)){
				return workflow;
			}
		}
		
		// Check workflow's merges
		List<? extends Merge> merges = workflow.getMerges();
		for (Merge merge : merges){
			for (EventHandlingInputPort input : merge.getInputPorts()){
				if (input.equals(port)){
					return merge;
				}
			}
		}
		
		// Check workflow's processors
		List<? extends Processor> processors = workflow.getProcessors();
		for (Processor processor : processors){
			for (EventHandlingInputPort output : processor.getInputPorts()){
				if (output.equals(port)) {
					return processor;
				}
			}

			// If processor contains a nested workflow - descend into it
			if (containsNestedWorkflow(processor)){
				Dataflow nestedWorkflow = ((NestedDataflow) processor.getActivityList().get(0)).getNestedDataflow();
				TokenProcessingEntity entity = getTokenProcessingEntityWithEventHandlingInputPort(port, nestedWorkflow);
				if (entity != null){
					return entity;
				}
			}
		}
		
		return null;
	}

	
	/**
	 * Returns true if processor contains a nested workflow.
	 */
	public static boolean containsNestedWorkflow(Processor processor) {
		return ((!processor.getActivityList().isEmpty()) && processor
				.getActivityList().get(0) instanceof NestedDataflow);
	}

}
