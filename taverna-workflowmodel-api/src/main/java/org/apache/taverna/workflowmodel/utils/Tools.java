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

package org.apache.taverna.workflowmodel.utils;

import static java.lang.Character.isLetterOrDigit;
import static org.apache.taverna.workflowmodel.utils.AnnotationTools.addAnnotation;
import static org.apache.taverna.workflowmodel.utils.AnnotationTools.getAnnotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.taverna.annotation.annotationbeans.IdentificationAssertion;
import org.apache.taverna.workflowmodel.CompoundEdit;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.EventForwardingOutputPort;
import org.apache.taverna.workflowmodel.EventHandlingInputPort;
import org.apache.taverna.workflowmodel.InputPort;
import org.apache.taverna.workflowmodel.Merge;
import org.apache.taverna.workflowmodel.MergeInputPort;
import org.apache.taverna.workflowmodel.MergeOutputPort;
import org.apache.taverna.workflowmodel.NamedWorkflowEntity;
import org.apache.taverna.workflowmodel.OutputPort;
import org.apache.taverna.workflowmodel.Port;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.ProcessorInputPort;
import org.apache.taverna.workflowmodel.ProcessorOutputPort;
import org.apache.taverna.workflowmodel.TokenProcessingEntity;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;
import org.apache.taverna.workflowmodel.processor.activity.ActivityInputPort;
import org.apache.taverna.workflowmodel.processor.activity.ActivityOutputPort;
import org.apache.taverna.workflowmodel.processor.activity.DisabledActivity;
import org.apache.taverna.workflowmodel.processor.activity.NestedDataflow;

import org.apache.log4j.Logger;

/**
 * Various workflow model tools that can be helpful when constructing a
 * dataflow.
 * <p>
 * Not to be confused with the @deprecated
 * {@link org.apache.taverna.workflowmodel.impl.Tools}
 * 
 * @author David Withers
 * @author Stian Soiland-Reyes
 */
public class Tools {
	private static Logger logger = Logger.getLogger(Tools.class);

	// private static Edits edits = new EditsImpl();

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
	protected static EventHandlingInputPort findEventHandlingInputPort(
			List<Edit<?>> editList, Dataflow dataflow, InputPort inputPort,
			Edits edits) {
		if (inputPort instanceof EventHandlingInputPort)
			return (EventHandlingInputPort) inputPort;
		else if (!(inputPort instanceof ActivityInputPort))
			throw new IllegalArgumentException("Unknown input port type for "
					+ inputPort);

		ActivityInputPort activityInput = (ActivityInputPort) inputPort;
		Collection<Processor> processors = getProcessorsWithActivityInputPort(
				dataflow, activityInput);
		if (processors.isEmpty())
			throw new IllegalArgumentException("Can't find ActivityInputPort "
					+ activityInput.getName() + " in workflow " + dataflow);

		// FIXME: Assumes only one matching processor
		Processor processor = processors.iterator().next();
		Activity<?> activity = null;
		for (Activity<?> checkActivity : processor.getActivityList())
			if (checkActivity.getInputPorts().contains(activityInput)) {
				activity = checkActivity;
				break;
			}
		if (activity == null)
			throw new IllegalArgumentException("Can't find activity for port "
					+ activityInput.getName() + "within processor " + processor);

		ProcessorInputPort input = getProcessorInputPort(processor, activity,
				activityInput);
		if (input != null)
			return input;
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
	protected static EventForwardingOutputPort findEventHandlingOutputPort(
			List<Edit<?>> editList, Dataflow dataflow, OutputPort outputPort,
			Edits edits) {
		if (outputPort instanceof EventForwardingOutputPort)
			return (EventForwardingOutputPort) outputPort;
		else if (!(outputPort instanceof ActivityOutputPort))
			throw new IllegalArgumentException("Unknown output port type for "
					+ outputPort);

		ActivityOutputPort activityOutput = (ActivityOutputPort) outputPort;
		Collection<Processor> processors = getProcessorsWithActivityOutputPort(
				dataflow, activityOutput);
		if (processors.isEmpty())
			throw new IllegalArgumentException("Can't find ActivityOutputPort "
					+ activityOutput.getName() + " in workflow " + dataflow);

		// FIXME: Assumes only one matching processor
		Processor processor = processors.iterator().next();
		Activity<?> activity = null;
		for (Activity<?> checkActivity : processor.getActivityList())
			if (checkActivity.getOutputPorts().contains(activityOutput)) {
				activity = checkActivity;
				break;
			}
		if (activity == null)
			throw new IllegalArgumentException("Can't find activity for port "
					+ activityOutput.getName() + "within processor "
					+ processor);

		ProcessorOutputPort processorOutputPort = Tools.getProcessorOutputPort(
				processor, activity, activityOutput);
		if (processorOutputPort != null)
			return processorOutputPort;

		// port doesn't exist so create a processor port and map it
		String processorPortName = uniquePortName(activityOutput.getName(),
				processor.getOutputPorts());
		processorOutputPort = edits.createProcessorOutputPort(processor,
				processorPortName, activityOutput.getDepth(),
				activityOutput.getGranularDepth());
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
			EventForwardingOutputPort source, EventHandlingInputPort sink,
			Edits edits) {
		Edit<?> edit = null;

		Datalink incomingLink = sink.getIncomingLink();
		if (incomingLink == null) {
			Datalink datalink = edits.createDatalink(source, sink);
			edit = edits.getConnectDatalinkEdit(datalink);
		} else {
			List<Edit<?>> editList = new ArrayList<>();

			Merge merge = null;
			int counter = 0; // counter for merge input port names
			if (incomingLink.getSource() instanceof MergeOutputPort)
				merge = ((MergeOutputPort) incomingLink.getSource()).getMerge();
			else {
				merge = edits.createMerge(dataflow);
				editList.add(edits.getAddMergeEdit(dataflow, merge));
				editList.add(edits.getDisconnectDatalinkEdit(incomingLink));
				MergeInputPort mergeInputPort = edits.createMergeInputPort(
						merge,
						getUniqueMergeInputPortName(merge,
								incomingLink.getSource().getName() + "To"
										+ merge.getLocalName() + "_input",
								counter++), incomingLink.getSink().getDepth());
				editList.add(edits.getAddMergeInputPortEdit(merge,
						mergeInputPort));
				Datalink datalink = edits.createDatalink(
						incomingLink.getSource(), mergeInputPort);
				editList.add(edits.getConnectDatalinkEdit(datalink));
				datalink = edits.createDatalink(merge.getOutputPort(),
						incomingLink.getSink());
				editList.add(edits.getConnectDatalinkEdit(datalink));
			}
			MergeInputPort mergeInputPort = edits.createMergeInputPort(
					merge,
					getUniqueMergeInputPortName(merge, source.getName() + "To"
							+ merge.getLocalName() + "_input", counter),
					sink.getDepth());
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
			OutputPort outputPort, InputPort inputPort, Edits edits) {
		List<Edit<?>> editList = new ArrayList<>();
		EventHandlingInputPort sink = findEventHandlingInputPort(editList,
				dataflow, inputPort, edits);
		EventForwardingOutputPort source = findEventHandlingOutputPort(
				editList, dataflow, outputPort, edits);
		editList.add(getCreateAndConnectDatalinkEdit(dataflow, source, sink,
				edits));
		return new CompoundEdit(editList);
	}

	/**
	 * Find a unique port name given a list of existing ports.
	 * <p>
	 * If needed, the returned port name will be prefixed with an underscore and
	 * a number, starting from 2. (The original being 'number 1')
	 * <p>
	 * Although not strictly needed by Taverna, for added user friendliness the
	 * case of the existing port names are ignored when checking for uniqueness.
	 * 
	 * @see #uniqueProcessorName(String, Dataflow)
	 * 
	 * @param suggestedPortName
	 *            Port name suggested for new port
	 * @param existingPorts
	 *            Collection of existing {@link Port}s
	 * @return A port name unique for the given collection of port
	 */
	public static String uniquePortName(String suggestedPortName,
			Collection<? extends Port> existingPorts) {
		// Make sure we have a unique port name
		Set<String> existingNames = new HashSet<>();
		for (Port existingPort : existingPorts)
			existingNames.add(existingPort.getName().toLowerCase());
		String candidateName = suggestedPortName;
		long counter = 2;
		while (existingNames.contains(candidateName.toLowerCase()))
			candidateName = suggestedPortName + "_" + counter++;
		return candidateName;
	}

	public static Edit<?> getMoveDatalinkSinkEdit(Dataflow dataflow,
			Datalink datalink, EventHandlingInputPort sink, Edits edits) {
		List<Edit<?>> editList = new ArrayList<>();
		editList.add(edits.getDisconnectDatalinkEdit(datalink));
		if (datalink.getSink() instanceof ProcessorInputPort)
			editList.add(getRemoveProcessorInputPortEdit(
					(ProcessorInputPort) datalink.getSink(), edits));
		editList.add(getCreateAndConnectDatalinkEdit(dataflow,
				datalink.getSource(), sink, edits));
		return new CompoundEdit(editList);
	}

	public static Edit<?> getDisconnectDatalinkAndRemovePortsEdit(
			Datalink datalink, Edits edits) {
		List<Edit<?>> editList = new ArrayList<>();
		editList.add(edits.getDisconnectDatalinkEdit(datalink));
		if (datalink.getSource() instanceof ProcessorOutputPort) {
			ProcessorOutputPort processorOutputPort = (ProcessorOutputPort) datalink
					.getSource();
			if (processorOutputPort.getOutgoingLinks().size() == 1)
				editList.add(getRemoveProcessorOutputPortEdit(
						processorOutputPort, edits));
		}
		if (datalink.getSink() instanceof ProcessorInputPort)
			editList.add(getRemoveProcessorInputPortEdit(
					(ProcessorInputPort) datalink.getSink(), edits));
		return new CompoundEdit(editList);
	}

	public static Edit<?> getRemoveProcessorOutputPortEdit(
			ProcessorOutputPort port, Edits edits) {
		List<Edit<?>> editList = new ArrayList<>();
		Processor processor = port.getProcessor();
		editList.add(edits.getRemoveProcessorOutputPortEdit(
				port.getProcessor(), port));
		for (Activity<?> activity : processor.getActivityList())
			editList.add(edits.getRemoveActivityOutputPortMappingEdit(activity,
					port.getName()));
		return new CompoundEdit(editList);
	}

	public static Edit<?> getRemoveProcessorInputPortEdit(
			ProcessorInputPort port, Edits edits) {
		List<Edit<?>> editList = new ArrayList<>();
		Processor processor = port.getProcessor();
		editList.add(edits.getRemoveProcessorInputPortEdit(port.getProcessor(),
				port));
		for (Activity<?> activity : processor.getActivityList())
			editList.add(edits.getRemoveActivityInputPortMappingEdit(activity,
					port.getName()));
		return new CompoundEdit(editList);
	}

	public static Edit<?> getEnableDisabledActivityEdit(Processor processor,
			DisabledActivity disabledActivity, Edits edits) {
		List<Edit<?>> editList = new ArrayList<>();
		Activity<?> brokenActivity = disabledActivity.getActivity();
		try {
			@SuppressWarnings("unchecked")
			Activity<Object> ra = brokenActivity.getClass().newInstance();
			Object lastConfig = disabledActivity.getLastWorkingConfiguration();
			if (lastConfig == null)
				lastConfig = disabledActivity.getActivityConfiguration();
			ra.configure(lastConfig);

			Map<String, String> portMapping = ra.getInputPortMapping();
			Set<String> portNames = new HashSet<>();
			portNames.addAll(portMapping.keySet());
			for (String portName : portNames)
				editList.add(edits.getRemoveActivityInputPortMappingEdit(ra,
						portName));
			portMapping = ra.getOutputPortMapping();
			portNames.clear();
			portNames.addAll(portMapping.keySet());
			for (String portName : portNames)
				editList.add(edits.getRemoveActivityOutputPortMappingEdit(ra,
						portName));

			portMapping = disabledActivity.getInputPortMapping();
			for (String portName : portMapping.keySet())
				editList.add(edits.getAddActivityInputPortMappingEdit(ra,
						portName, portMapping.get(portName)));
			portMapping = disabledActivity.getOutputPortMapping();
			for (String portName : portMapping.keySet())
				editList.add(edits.getAddActivityOutputPortMappingEdit(ra,
						portName, portMapping.get(portName)));

			editList.add(edits.getRemoveActivityEdit(processor,
					disabledActivity));
			editList.add(edits.getAddActivityEdit(processor, ra));
		} catch (ActivityConfigurationException ex) {
			logger.error("Configuration exception ", ex);
			return null;
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
		return new CompoundEdit(editList);
	}

	public static ProcessorInputPort getProcessorInputPort(Processor processor,
			Activity<?> activity, InputPort activityInputPort) {
		ProcessorInputPort result = null;
		for (Entry<String, String> mapEntry : activity.getInputPortMapping()
				.entrySet())
			if (mapEntry.getValue().equals(activityInputPort.getName())) {
				for (ProcessorInputPort processorInputPort : processor
						.getInputPorts())
					if (processorInputPort.getName().equals(mapEntry.getKey())) {
						result = processorInputPort;
						break;
					}
				break;
			}
		return result;
	}

	public static ProcessorOutputPort getProcessorOutputPort(
			Processor processor, Activity<?> activity,
			OutputPort activityOutputPort) {
		ProcessorOutputPort result = null;
		for (Entry<String, String> mapEntry : activity.getOutputPortMapping()
				.entrySet())
			if (mapEntry.getValue().equals(activityOutputPort.getName())) {
				for (ProcessorOutputPort processorOutputPort : processor
						.getOutputPorts())
					if (processorOutputPort.getName().equals(mapEntry.getKey())) {
						result = processorOutputPort;
						break;
					}
				break;
			}
		return result;
	}

	public static ActivityInputPort getActivityInputPort(Activity<?> activity,
			String portName) {
		ActivityInputPort activityInputPort = null;
		for (ActivityInputPort inputPort : activity.getInputPorts())
			if (inputPort.getName().equals(portName)) {
				activityInputPort = inputPort;
				break;
			}
		return activityInputPort;
	}

	public static OutputPort getActivityOutputPort(Activity<?> activity,
			String portName) {
		OutputPort activityOutputPort = null;
		for (OutputPort outputPort : activity.getOutputPorts())
			if (outputPort.getName().equals(portName)) {
				activityOutputPort = outputPort;
				break;
			}
		return activityOutputPort;
	}

	public static String getUniqueMergeInputPortName(Merge merge, String name,
			int count) {
		String uniqueName = name + count;
		for (MergeInputPort mergeInputPort : merge.getInputPorts())
			if (mergeInputPort.getName().equals(uniqueName))
				return getUniqueMergeInputPortName(merge, name, ++count);
		return uniqueName;
	}

	public static Collection<Processor> getProcessorsWithActivity(
			Dataflow dataflow, Activity<?> activity) {
		Set<Processor> processors = new HashSet<>();
		for (Processor processor : dataflow.getProcessors())
			if (processor.getActivityList().contains(activity))
				processors.add(processor);
		return processors;
	}

	public static Collection<Processor> getProcessorsWithActivityInputPort(
			Dataflow dataflow, ActivityInputPort inputPort) {
		Set<Processor> processors = new HashSet<>();
		for (Processor processor : dataflow.getProcessors()) {
			// Does it contain a nested workflow?
			if (containsNestedWorkflow(processor))
				// Get the nested workflow and check all its nested processors
				processors.addAll(getProcessorsWithActivityInputPort(
						getNestedWorkflow(processor), inputPort));

			/*
			 * Check all processor's activities (even if the processor contained
			 * a nested workflow, as its dataflow activity still contains input
			 * and output ports)
			 */
			for (Activity<?> activity : processor.getActivityList())
				if (activity.getInputPorts().contains(inputPort))
					processors.add(processor);
		}
		return processors;
	}

	public static Collection<Processor> getProcessorsWithActivityOutputPort(
			Dataflow dataflow, OutputPort outputPort) {
		Set<Processor> processors = new HashSet<>();
		for (Processor processor : dataflow.getProcessors()) {
			// Does it contain a nested workflow?
			if (containsNestedWorkflow(processor))
				// Get the nested workflow and check all its nested processors
				processors.addAll(getProcessorsWithActivityOutputPort(
						getNestedWorkflow(processor), outputPort));

			/*
			 * Check all processor's activities (even if the processor contained
			 * a nested workflow, as its dataflow activity still contains input
			 * and output ports)
			 */
			for (Activity<?> activity : processor.getActivityList())
				if (activity.getOutputPorts().contains(outputPort))
					processors.add(processor);
		}
		return processors;
	}

	/**
	 * Get the TokenProcessingEntity (Processor, Merge or Dataflow) from the
	 * workflow that contains the given EventForwardingOutputPort. This can be
	 * an output port of a Processor or a Merge or an input port of a Dataflow
	 * that has an internal EventForwardingOutputPort attached to it.
	 * 
	 * @param port
	 * @param workflow
	 * @return
	 */
	public static TokenProcessingEntity getTokenProcessingEntityWithEventForwardingOutputPort(
			EventForwardingOutputPort port, Dataflow workflow) {
		// First check the workflow's inputs
		for (DataflowInputPort input : workflow.getInputPorts())
			if (input.getInternalOutputPort().equals(port))
				return workflow;

		// Check workflow's merges
		for (Merge merge : workflow.getMerges())
			if (merge.getOutputPort().equals(port))
				return merge;

		// Check workflow's processors
		for (Processor processor : workflow.getProcessors()) {
			for (OutputPort output : processor.getOutputPorts())
				if (output.equals(port))
					return processor;

			// If processor contains a nested workflow - descend into it
			if (containsNestedWorkflow(processor)) {
				TokenProcessingEntity entity = getTokenProcessingEntityWithEventForwardingOutputPort(
						port, getNestedWorkflow(processor));
				if (entity != null)
					return entity;
			}
		}

		return null;
	}

	/**
	 * Get the TokenProcessingEntity (Processor, Merge or Dataflow) from the
	 * workflow that contains the given target EventHandlingInputPort. This can
	 * be an input port of a Processor or a Merge or an output port of a
	 * Dataflow that has an internal EventHandlingInputPort attached to it.
	 * 
	 * @param port
	 * @param workflow
	 * @return
	 */
	public static TokenProcessingEntity getTokenProcessingEntityWithEventHandlingInputPort(
			EventHandlingInputPort port, Dataflow workflow) {
		// First check the workflow's outputs
		for (DataflowOutputPort output : workflow.getOutputPorts())
			if (output.getInternalInputPort().equals(port))
				return workflow;

		// Check workflow's merges
		for (Merge merge : workflow.getMerges())
			for (EventHandlingInputPort input : merge.getInputPorts())
				if (input.equals(port))
					return merge;

		// Check workflow's processors
		for (Processor processor : workflow.getProcessors()) {
			for (EventHandlingInputPort output : processor.getInputPorts())
				if (output.equals(port))
					return processor;

			// If processor contains a nested workflow - descend into it
			if (containsNestedWorkflow(processor)) {
				TokenProcessingEntity entity = getTokenProcessingEntityWithEventHandlingInputPort(
						port, getNestedWorkflow(processor));
				if (entity != null)
					return entity;
			}
		}

		return null;
	}

	/**
	 * Returns true if processor contains a nested workflow.
	 */
	public static boolean containsNestedWorkflow(Processor processor) {
		List<?> activities = processor.getActivityList();
		return !activities.isEmpty()
				&& activities.get(0) instanceof NestedDataflow;
	}

	/**
	 * Get the workflow that is nested inside. Only call this if
	 * {@link #containsNestedWorkflow()} returns true.
	 */
	private static Dataflow getNestedWorkflow(Processor processor) {
		return ((NestedDataflow) processor.getActivityList().get(0))
				.getNestedDataflow();
	}

	/**
	 * Find the first processor that contains an activity that has the given
	 * activity input port. See #get
	 * 
	 * @param dataflow
	 * @param targetPort
	 * @return
	 */
	public static Processor getFirstProcessorWithActivityInputPort(
			Dataflow dataflow, ActivityInputPort targetPort) {
		for (Processor processor : getProcessorsWithActivityInputPort(
				dataflow, targetPort))
			return processor;
		return null;
	}

	public static Processor getFirstProcessorWithActivityOutputPort(
			Dataflow dataflow, ActivityOutputPort targetPort) {
		for (Processor processor : getProcessorsWithActivityOutputPort(
				dataflow, targetPort))
			return processor;
		return null;
	}

	/**
	 * Find a unique processor name for the supplied Dataflow, based upon the
	 * preferred name. If needed, an underscore and a numeric suffix is added to
	 * the preferred name, and incremented until it is unique, starting from 2.
	 * (The original being 'number 1')
	 * <p>
	 * Note that this method checks the uniqueness against the names of all
	 * {@link NamedWorkflowEntity}s, including {@link Merge}s.
	 * <p>
	 * Although not strictly needed by Taverna, for added user friendliness the
	 * case of the existing port names are ignored when checking for uniqueness.
	 * 
	 * @param preferredName
	 *            the preferred name for the Processor
	 * @param dataflow
	 *            the dataflow for which the Processor name needs to be unique
	 * @return A unique processor name
	 */
	public static String uniqueProcessorName(String preferredName,
			Dataflow dataflow) {
		Set<String> existingNames = new HashSet<>();
		for (NamedWorkflowEntity entity : dataflow
				.getEntities(NamedWorkflowEntity.class))
			existingNames.add(entity.getLocalName().toLowerCase());
		return uniqueObjectName(preferredName, existingNames);
	}

	/**
	 * Checks that the name does not have any characters that are invalid for a
	 * Taverna name.
	 * 
	 * The name must contain only the chars[A-Za-z_0-9].
	 * 
	 * @param name
	 *            the original name
	 * @return the sanitised name
	 */
	public static String sanitiseName(String name) {
		if (Pattern.matches("\\w++", name) == false) {
			StringBuilder result = new StringBuilder(name.length());
			for (char c : name.toCharArray())
				result.append(isLetterOrDigit(c) || c == '_' ? c : "_");
			return result.toString();
		}
		return name;
	}

	public static String uniqueObjectName(String preferredName,
			Set<String> existingNames) {
		String uniqueName = preferredName;
		long suffix = 2;
		while (existingNames.contains(uniqueName.toLowerCase()))
			uniqueName = preferredName + "_" + suffix++;
		return uniqueName;

	}

	/**
	 * Add the identification of a Dataflow into its identification annotation
	 * chain (if necessary)
	 * 
	 * @return Whether an identification needed to be added
	 */
	public static boolean addDataflowIdentification(Dataflow dataflow,
			String internalId, Edits edits) {
		IdentificationAssertion ia = (IdentificationAssertion) getAnnotation(
				dataflow, IdentificationAssertion.class);
		if (ia != null && ia.getIdentification().equals(internalId))
			return false;
		IdentificationAssertion newIa = new IdentificationAssertion();
		newIa.setIdentification(internalId);
		try {
			addAnnotation(dataflow, newIa, edits).doEdit();
			return true;
		} catch (EditException e) {
			return false;
		}
	}

	/**
	 * Return a path of processors where the last element is this processor and
	 * previous ones are nested processors that contain this one all the way to
	 * the top but excluding the top level workflow as this is only a list of
	 * processors.
	 */
	public static List<Processor> getNestedPathForProcessor(
			Processor processor, Dataflow dataflow) {
		for (Processor proc : dataflow.getProcessors())
			if (proc == processor) { // found it
				List<Processor> list = new ArrayList<>();
				list.add(processor);
				return list;
			} else if (containsNestedWorkflow(proc)) {
				/*
				 * check inside this nested processor
				 */
				List<Processor> nestedList = getNestedPathForProcessor(
						processor, getNestedWorkflow(proc));
				if (nestedList == null)
					// processor not found in this nested workflow
					continue;
				// add this nested processor to the list
				nestedList.add(0, proc);
				return nestedList;
			}
		return null;
	}
}
