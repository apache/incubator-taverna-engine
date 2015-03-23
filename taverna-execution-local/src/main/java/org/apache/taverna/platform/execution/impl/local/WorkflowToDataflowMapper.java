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

package org.apache.taverna.platform.execution.impl.local;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.EventForwardingOutputPort;
import org.apache.taverna.workflowmodel.EventHandlingInputPort;
import org.apache.taverna.workflowmodel.Merge;
import org.apache.taverna.workflowmodel.MergeInputPort;
import org.apache.taverna.workflowmodel.ProcessorInputPort;
import org.apache.taverna.workflowmodel.ProcessorOutputPort;
import org.apache.taverna.workflowmodel.processor.activity.ActivityInputPort;
import org.apache.taverna.workflowmodel.processor.activity.ActivityOutputPort;
import org.apache.taverna.workflowmodel.processor.activity.NestedDataflow;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchStack;
import org.apache.taverna.workflowmodel.processor.iteration.IterationStrategy;
import org.apache.taverna.workflowmodel.processor.iteration.NamedInputPortNode;
import org.apache.taverna.platform.capability.api.ActivityConfigurationException;
import org.apache.taverna.platform.capability.api.ActivityNotFoundException;
import org.apache.taverna.platform.capability.api.ActivityService;
import org.apache.taverna.platform.capability.api.DispatchLayerConfigurationException;
import org.apache.taverna.platform.capability.api.DispatchLayerNotFoundException;
import org.apache.taverna.platform.capability.api.DispatchLayerService;
import org.apache.taverna.platform.execution.api.InvalidWorkflowException;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.BlockingControlLink;
import org.apache.taverna.scufl2.api.core.ControlLink;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.DotProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyNode;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;
import org.apache.taverna.scufl2.api.iterationstrategy.PortNode;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.Port;
import org.apache.taverna.scufl2.api.port.ReceiverPort;
import org.apache.taverna.scufl2.api.port.SenderPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Translates a scufl2 {@link Workflow} into a {@link Dataflow}.
 * 
 * @author David Withers
 */
public class WorkflowToDataflowMapper {
	private static final URI NESTED_WORKFLOW_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/nested-workflow");

	private Edits edits;
	private final Scufl2Tools scufl2Tools = new Scufl2Tools();
	private final Map<Port, EventHandlingInputPort> inputPorts;
	private final Map<Port, EventForwardingOutputPort> outputPorts;
	private final Map<Port, Merge> merges;
	private final Map<Workflow, Dataflow> workflowToDataflow;
	private final Map<Dataflow, Workflow> dataflowToWorkflow;
	private final Map<Processor, org.apache.taverna.workflowmodel.Processor> workflowToDataflowProcessors;
	private final Map<org.apache.taverna.workflowmodel.Processor, Processor> dataflowToWorkflowProcessors;
	private final Map<Activity, org.apache.taverna.workflowmodel.processor.activity.Activity<?>> workflowToDataflowActivities;
	private final Map<org.apache.taverna.workflowmodel.processor.activity.Activity<?>, Activity> dataflowToWorkflowActivities;
	@SuppressWarnings("unused")
	private final WorkflowBundle workflowBundle;
	private final Profile profile;
	private final ActivityService activityService;
	private final DispatchLayerService dispatchLayerService;

	public WorkflowToDataflowMapper(WorkflowBundle workflowBundle,
			Profile profile, Edits edits, ActivityService activityService,
			DispatchLayerService dispatchLayerService) {
		this.workflowBundle = workflowBundle;
		this.profile = profile;
		this.edits = edits;
		this.activityService = activityService;
		this.dispatchLayerService = dispatchLayerService;
		inputPorts = new IdentityHashMap<>();
		outputPorts = new IdentityHashMap<>();
		merges = new IdentityHashMap<>();
		workflowToDataflow = new IdentityHashMap<>();
		dataflowToWorkflow = new HashMap<>();
		workflowToDataflowProcessors = new IdentityHashMap<>();
		dataflowToWorkflowProcessors = new HashMap<>();
		workflowToDataflowActivities = new IdentityHashMap<>();
		dataflowToWorkflowActivities = new HashMap<>();
	}

	public Workflow getWorkflow(Dataflow dataflow) {
		return dataflowToWorkflow.get(dataflow);
	}

	public Dataflow getDataflow(Workflow workflow)
			throws InvalidWorkflowException {
		if (!workflowToDataflow.containsKey(workflow)) {
			try {
				Dataflow dataflow = createDataflow(workflow);
				workflowToDataflow.put(workflow, dataflow);
				dataflowToWorkflow.put(dataflow, workflow);
			} catch (EditException | ActivityConfigurationException
					| DispatchLayerConfigurationException
					| ActivityNotFoundException
					| DispatchLayerNotFoundException e) {
				throw new InvalidWorkflowException(e);
			}
		}
		return workflowToDataflow.get(workflow);
	}

	public Processor getWorkflowProcessor(
			org.apache.taverna.workflowmodel.Processor dataflowProcessor) {
		return dataflowToWorkflowProcessors.get(dataflowProcessor);
	}

	public org.apache.taverna.workflowmodel.Processor getDataflowProcessor(
			Processor workflowProcessor) {
		return workflowToDataflowProcessors.get(workflowProcessor);
	}

	public Activity getWorkflowActivity(
			org.apache.taverna.workflowmodel.processor.activity.Activity<?> dataflowActiviy) {
		return dataflowToWorkflowActivities.get(dataflowActiviy);
	}

	public org.apache.taverna.workflowmodel.processor.activity.Activity<?> getDataflowActivity(
			Activity workflowActivity) {
		return workflowToDataflowActivities.get(workflowActivity);
	}

	protected Dataflow createDataflow(Workflow workflow) throws EditException,
			ActivityNotFoundException, ActivityConfigurationException,
			InvalidWorkflowException, DispatchLayerNotFoundException,
			DispatchLayerConfigurationException {
		// create the dataflow
		Dataflow dataflow = edits.createDataflow();
		// set the dataflow name
		edits.getUpdateDataflowNameEdit(dataflow, workflow.getName()).doEdit();

		addInputPorts(workflow, dataflow);
		addOutputPorts(workflow, dataflow);
		addProcessors(workflow, dataflow);
		addDataLinks(workflow, dataflow);
		addControlLinks(workflow);

		return dataflow;
	}

	private void addProcessors(Workflow workflow, Dataflow dataflow)
			throws EditException, ActivityNotFoundException,
			ActivityConfigurationException, InvalidWorkflowException,
			DispatchLayerNotFoundException, DispatchLayerConfigurationException {
		for (Processor processor : workflow.getProcessors()) {
			org.apache.taverna.workflowmodel.Processor dataflowProcessor = edits
					.createProcessor(processor.getName());
			edits.getAddProcessorEdit(dataflow, dataflowProcessor).doEdit();
			// map the processor
			workflowToDataflowProcessors.put(processor, dataflowProcessor);
			dataflowToWorkflowProcessors.put(dataflowProcessor, processor);
			// add input ports
			for (InputProcessorPort inputProcessorPort : processor
					.getInputPorts()) {
				if (inputProcessorPort.getDatalinksTo().isEmpty())
					continue;
				ProcessorInputPort processorInputPort = edits
						.createProcessorInputPort(dataflowProcessor,
								inputProcessorPort.getName(),
								inputProcessorPort.getDepth());
				edits.getAddProcessorInputPortEdit(dataflowProcessor,
						processorInputPort).doEdit();
				inputPorts.put(inputProcessorPort, processorInputPort);
			}
			// add output ports
			for (OutputProcessorPort outputProcessorPort : processor
					.getOutputPorts()) {
				ProcessorOutputPort processorOutputPort = edits
						.createProcessorOutputPort(dataflowProcessor,
								outputProcessorPort.getName(),
								outputProcessorPort.getDepth(),
								outputProcessorPort.getGranularDepth());
				edits.getAddProcessorOutputPortEdit(dataflowProcessor,
						processorOutputPort).doEdit();
				outputPorts.put(outputProcessorPort, processorOutputPort);
			}

			// add dispatch stack
			addDispatchStack(processor, dataflowProcessor);

			addIterationStrategy(processor, dataflowProcessor);

			// add bound activities
			for (ProcessorBinding processorBinding : scufl2Tools
					.processorBindingsForProcessor(processor, profile))
				addActivity(processorBinding);
		}
	}

	private void addDispatchStack(Processor processor,
			org.apache.taverna.workflowmodel.Processor dataflowProcessor)
			throws DispatchLayerNotFoundException,
			DispatchLayerConfigurationException, EditException {
		DispatchStack dispatchStack = dataflowProcessor.getDispatchStack();

		JsonNode json = null;
		try {
			json = processor.getConfiguration(profile).getJson();
		} catch (IndexOutOfBoundsException e) {
			// no configuration for processor
		}

		int layer = 0;
		addDispatchLayer(
				dispatchStack,
				URI.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Parallelize"),
				layer++, json == null ? null : json.get("parallelize"));
		addDispatchLayer(
				dispatchStack,
				URI.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/ErrorBounce"),
				layer++, null);
		addDispatchLayer(
				dispatchStack,
				URI.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Failover"),
				layer++, null);
		addDispatchLayer(
				dispatchStack,
				URI.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Retry"),
				layer++, json == null ? null : json.get("retry"));
		addDispatchLayer(
				dispatchStack,
				URI.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Stop"),
				layer++, null);
		addDispatchLayer(
				dispatchStack,
				URI.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Invoke"),
				layer++, null);

	}

	private void addDispatchLayer(DispatchStack dispatchStack,
			URI dispatchLayerType, int layer, JsonNode json)
			throws DispatchLayerConfigurationException,
			DispatchLayerNotFoundException, EditException {
		// create the dispatch layer
		DispatchLayer<?> dispatchLayer = dispatchLayerService
				.createDispatchLayer(dispatchLayerType, json);
		// add the dispatch layer to the dispatch layer stack
		edits.getAddDispatchLayerEdit(dispatchStack, dispatchLayer, layer)
				.doEdit();
	}

	private void addIterationStrategy(Processor processor,
			org.apache.taverna.workflowmodel.Processor dataflowProcessor)
			throws EditException, InvalidWorkflowException {
		// get the iteration strategy from the processor
		org.apache.taverna.workflowmodel.processor.iteration.IterationStrategyStack dataflowIterationStrategyStack = dataflowProcessor
				.getIterationStrategy();
		// clear the iteration strategy
		edits.getClearIterationStrategyStackEdit(dataflowIterationStrategyStack)
				.doEdit();
		IterationStrategyStack iterationStrategyStack = processor
				.getIterationStrategyStack();
		for (IterationStrategyTopNode iterationStrategyTopNode : iterationStrategyStack) {
			// create iteration strategy
			IterationStrategy dataflowIterationStrategy = edits
					.createIterationStrategy();
			// add iteration strategy to the stack
			edits.getAddIterationStrategyEdit(dataflowIterationStrategyStack,
					dataflowIterationStrategy).doEdit();
			// add the node to the iteration strategy
			addIterationStrategyNode(dataflowIterationStrategy,
					dataflowIterationStrategy.getTerminalNode(),
					iterationStrategyTopNode);
		}
	}

	private void addIterationStrategyNode(
			IterationStrategy dataflowIterationStrategy,
			org.apache.taverna.workflowmodel.processor.iteration.IterationStrategyNode dataflowIterationStrategyNode,
			IterationStrategyNode iterationStrategyNode) throws EditException,
			InvalidWorkflowException {
		org.apache.taverna.workflowmodel.processor.iteration.IterationStrategyNode childDataflowIterationStrategyNode = null;
		if (iterationStrategyNode instanceof CrossProduct) {
			CrossProduct crossProduct = (CrossProduct) iterationStrategyNode;
			childDataflowIterationStrategyNode = new org.apache.taverna.workflowmodel.processor.iteration.CrossProduct();
			for (IterationStrategyNode iterationStrategyNode2 : crossProduct)
				addIterationStrategyNode(dataflowIterationStrategy,
						childDataflowIterationStrategyNode,
						iterationStrategyNode2);
		} else if (iterationStrategyNode instanceof DotProduct) {
			DotProduct dotProduct = (DotProduct) iterationStrategyNode;
			childDataflowIterationStrategyNode = new org.apache.taverna.workflowmodel.processor.iteration.DotProduct();
			for (IterationStrategyNode iterationStrategyNode2 : dotProduct)
				addIterationStrategyNode(dataflowIterationStrategy,
						childDataflowIterationStrategyNode,
						iterationStrategyNode2);
		} else if (iterationStrategyNode instanceof PortNode) {
			PortNode portNode = (PortNode) iterationStrategyNode;
			Integer desiredDepth = portNode.getDesiredDepth();
			if (desiredDepth == null)
				desiredDepth = portNode.getInputProcessorPort().getDepth();
			NamedInputPortNode namedInputPortNode = new NamedInputPortNode(
					portNode.getInputProcessorPort().getName(), desiredDepth);
			edits.getAddIterationStrategyInputNodeEdit(
					dataflowIterationStrategy, namedInputPortNode).doEdit();
			childDataflowIterationStrategyNode = namedInputPortNode;
		} else {
			throw new InvalidWorkflowException(
					"Unknown IterationStrategyNode type : "
							+ iterationStrategyNode.getClass().getName());
		}
		childDataflowIterationStrategyNode
				.setParent(dataflowIterationStrategyNode);
	}

	private void addActivity(ProcessorBinding processorBinding)
			throws EditException, ActivityNotFoundException,
			ActivityConfigurationException, InvalidWorkflowException {
		org.apache.taverna.workflowmodel.Processor processor = workflowToDataflowProcessors
				.get(processorBinding.getBoundProcessor());
		Activity scufl2Activity = processorBinding.getBoundActivity();
		URI activityType = scufl2Activity.getType();
		if (!activityService.activityExists(activityType))
			throw new ActivityNotFoundException("No activity exists for "
					+ activityType);
		Configuration configuration = scufl2Activity.getConfiguration();

		// create the activity
		org.apache.taverna.workflowmodel.processor.activity.Activity<?> activity = activityService
				.createActivity(activityType, configuration.getJson());
		// check if we have a nested workflow
		if (activityType.equals(NESTED_WORKFLOW_URI)) {
			if (activity instanceof NestedDataflow) {
				Workflow nestedWorkflow = scufl2Tools
						.nestedWorkflowForProcessor(
								processorBinding.getBoundProcessor(), profile);
				((NestedDataflow) activity)
						.setNestedDataflow(getDataflow(nestedWorkflow));
			} else
				throw new ActivityConfigurationException(
						"Activity is not an instance of NestedDataflow");
		}

		// add the activity to the processor
		edits.getAddActivityEdit(processor, activity).doEdit();

		// add input ports
		for (InputActivityPort inputActivityPort : scufl2Activity
				.getInputPorts()) {
			ActivityInputPort activityInputPort = edits
					.createActivityInputPort(
							inputActivityPort.getName(),
							inputActivityPort.getDepth(),
							false,
							new ArrayList<Class<? extends ExternalReferenceSPI>>(),
							String.class);
			edits.getAddActivityInputPortEdit(activity, activityInputPort)
					.doEdit();
		}
		// add output ports
		for (OutputActivityPort outputActivityPort : scufl2Activity
				.getOutputPorts()) {
			ActivityOutputPort activitytOutputPort = edits
					.createActivityOutputPort(outputActivityPort.getName(),
							outputActivityPort.getDepth(),
							outputActivityPort.getGranularDepth());
			edits.getAddActivityOutputPortEdit(activity, activitytOutputPort)
					.doEdit();
		}
		// map input ports
		for (ProcessorInputPortBinding portBinding : processorBinding
				.getInputPortBindings()) {
			InputProcessorPort processorPort = portBinding
					.getBoundProcessorPort();
			InputActivityPort activityPort = portBinding.getBoundActivityPort();
			edits.getAddActivityInputPortMappingEdit(activity,
					processorPort.getName(), activityPort.getName()).doEdit();
		}
		// map output ports
		for (ProcessorOutputPortBinding portBinding : processorBinding
				.getOutputPortBindings()) {
			OutputProcessorPort processorPort = portBinding
					.getBoundProcessorPort();
			OutputActivityPort activityPort = portBinding
					.getBoundActivityPort();
			edits.getAddActivityOutputPortMappingEdit(activity,
					processorPort.getName(), activityPort.getName()).doEdit();
		}
		workflowToDataflowActivities.put(scufl2Activity, activity);
		dataflowToWorkflowActivities.put(activity, scufl2Activity);
	}

	private void addDataLinks(Workflow workflow, Dataflow dataflow)
			throws EditException {
		for (DataLink dataLink : workflow.getDataLinks()) {
			ReceiverPort receiverPort = dataLink.getSendsTo();
			SenderPort senderPort = dataLink.getReceivesFrom();
			EventForwardingOutputPort source = outputPorts.get(senderPort);
			EventHandlingInputPort sink = inputPorts.get(receiverPort);
			Integer mergePosition = dataLink.getMergePosition();
			if (mergePosition != null) {
				if (!merges.containsKey(receiverPort)) {
					Merge merge = edits.createMerge(dataflow);
					edits.getAddMergeEdit(dataflow, merge).doEdit();
					merges.put(receiverPort, merge);
				}
				Merge merge = merges.get(receiverPort);
				// create merge input port
				MergeInputPort mergeInputPort = edits.createMergeInputPort(
						merge, "input" + mergePosition, sink.getDepth());
				// add it to the correct position in the merge
				@SuppressWarnings("unchecked")
				List<MergeInputPort> mergeInputPorts = (List<MergeInputPort>) merge
						.getInputPorts();
				if (mergePosition > mergeInputPorts.size())
					mergeInputPorts.add(mergeInputPort);
				else
					mergeInputPorts.add(mergePosition, mergeInputPort);
				// connect a datalink into the merge
				Datalink datalinkIn = edits.createDatalink(source,
						mergeInputPort);
				edits.getConnectDatalinkEdit(datalinkIn).doEdit();
				// check if the merge output has been connected
				EventForwardingOutputPort mergeOutputPort = merge
						.getOutputPort();
				if (mergeOutputPort.getOutgoingLinks().isEmpty()) {
					Datalink datalinkOut = edits.createDatalink(
							merge.getOutputPort(), sink);
					edits.getConnectDatalinkEdit(datalinkOut).doEdit();
				} else if (mergeOutputPort.getOutgoingLinks().size() == 1) {
					if (mergeOutputPort.getOutgoingLinks().iterator().next()
							.getSink() != sink)
						throw new EditException(
								"Cannot add a different sinkPort to a Merge that already has one defined");
				} else
					throw new EditException(
							"The merge instance cannot have more that 1 outgoing Datalink");
			} else {
				Datalink datalink = edits.createDatalink(source, sink);
				edits.getConnectDatalinkEdit(datalink).doEdit();
			}
		}
	}

	private void addControlLinks(Workflow workflow) throws EditException {
		for (ControlLink controlLink : workflow.getControlLinks()) {
			if (controlLink instanceof BlockingControlLink) {
				BlockingControlLink blockingControlLink = (BlockingControlLink) controlLink;
				Processor untilFinished = blockingControlLink
						.getUntilFinished();
				Processor block = blockingControlLink.getBlock();
				edits.getCreateConditionEdit(
						workflowToDataflowProcessors.get(untilFinished),
						workflowToDataflowProcessors.get(block)).doEdit();
			}
		}
	}

	private void addOutputPorts(Workflow workflow, Dataflow dataflow)
			throws EditException {
		for (OutputWorkflowPort outputWorkflowPort : workflow.getOutputPorts()) {
			DataflowOutputPort dataflowOutputPort = edits
					.createDataflowOutputPort(outputWorkflowPort.getName(),
							dataflow);
			edits.getAddDataflowOutputPortEdit(dataflow, dataflowOutputPort)
					.doEdit();
			inputPorts.put(outputWorkflowPort,
					dataflowOutputPort.getInternalInputPort());
		}
	}

	private void addInputPorts(Workflow workflow, Dataflow dataflow)
			throws EditException {
		for (InputWorkflowPort inputWorkflowPort : workflow.getInputPorts()) {
			DataflowInputPort dataflowInputPort = edits
					.createDataflowInputPort(inputWorkflowPort.getName(),
							inputWorkflowPort.getDepth(),
							inputWorkflowPort.getDepth(), dataflow);
			edits.getAddDataflowInputPortEdit(dataflow, dataflowInputPort)
					.doEdit();
			outputPorts.put(inputWorkflowPort,
					dataflowInputPort.getInternalOutputPort());
		}
	}
}
