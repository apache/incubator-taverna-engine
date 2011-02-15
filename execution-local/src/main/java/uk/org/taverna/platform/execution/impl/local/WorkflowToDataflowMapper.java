/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester   
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
package uk.org.taverna.platform.execution.impl.local;

import java.net.URI;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.workflowmodel.Configurable;
import net.sf.taverna.t2.workflowmodel.ConfigurationException;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;
import net.sf.taverna.t2.workflowmodel.Merge;
import net.sf.taverna.t2.workflowmodel.MergeInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import uk.org.taverna.platform.activity.ActivityConfigurationException;
import uk.org.taverna.platform.activity.ActivityNotFoundException;
import uk.org.taverna.platform.activity.ActivityService;
import uk.org.taverna.platform.dispatch.DispatchLayerConfigurationException;
import uk.org.taverna.platform.dispatch.DispatchLayerNotFoundException;
import uk.org.taverna.platform.dispatch.DispatchLayerService;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.BlockingControlLink;
import uk.org.taverna.scufl2.api.core.ControlLink;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStack;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.port.Port;
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyNotFoundException;

/**
 * Translates a scufl2 {@link Workflow} into a {@link Dataflow}.
 * 
 * @author David Withers
 */
public class WorkflowToDataflowMapper {

	private Edits edits;

	private final Scufl2Tools scufl2Tools = new Scufl2Tools();

	private final URITools uriTools = new URITools();

	private final Map<Port, EventHandlingInputPort> inputPorts;

	private final Map<Port, EventForwardingOutputPort> outputPorts;

	private final Map<Port, Merge> merges;

	private final Map<Processor, net.sf.taverna.t2.workflowmodel.Processor> workflowToDataflowProcessors;

	private final Map<net.sf.taverna.t2.workflowmodel.Processor, Processor> dataflowToWorkflowProcessors;

	private final Map<Activity, net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?>> workflowToDataflowActivities;

	private final Map<net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?>, Activity> dataflowToWorkflowActivities;

	private final WorkflowBundle workflowBundle;

	private final Workflow workflow;

	private final Profile profile;

	private final Dataflow dataflow;

	private final ActivityService activityService;

	private final DispatchLayerService dispatchLayerService;

	public WorkflowToDataflowMapper(WorkflowBundle workflowBundle, Workflow workflow,
			Profile profile, Edits edits, ActivityService activityService, DispatchLayerService dispatchLayerService)
			throws InvalidWorkflowException {
		this.workflowBundle = workflowBundle;
		this.workflow = workflow;
		this.profile = profile;
		this.edits = edits;
		this.activityService = activityService;
		this.dispatchLayerService = dispatchLayerService;
		inputPorts = new IdentityHashMap<Port, EventHandlingInputPort>();
		outputPorts = new IdentityHashMap<Port, EventForwardingOutputPort>();
		merges = new IdentityHashMap<Port, Merge>();
		workflowToDataflowProcessors = new IdentityHashMap<Processor, net.sf.taverna.t2.workflowmodel.Processor>();
		dataflowToWorkflowProcessors = new HashMap<net.sf.taverna.t2.workflowmodel.Processor, Processor>();
		workflowToDataflowActivities = new IdentityHashMap<Activity, net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?>>();
		dataflowToWorkflowActivities = new HashMap<net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?>, Activity>();
		try {
			dataflow = createDataflow();
		} catch (EditException e) {
			throw new InvalidWorkflowException(e);
		} catch (ActivityNotFoundException e) {
			throw new InvalidWorkflowException(e);
		} catch (ActivityConfigurationException e) {
			throw new InvalidWorkflowException(e);
		} catch (PropertyNotFoundException e) {
			throw new InvalidWorkflowException(e);
		} catch (DispatchLayerNotFoundException e) {
			throw new InvalidWorkflowException(e);
		} catch (DispatchLayerConfigurationException e) {
			throw new InvalidWorkflowException(e);
		}
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public Dataflow getDataflow() {
		return dataflow;
	}

	public Processor getWorkflowProcessor(
			net.sf.taverna.t2.workflowmodel.Processor dataflowProcessor) {
		return dataflowToWorkflowProcessors.get(dataflowProcessor);
	}

	public net.sf.taverna.t2.workflowmodel.Processor getDataflowProcessor(
			Processor workflowProcessor) {
		return workflowToDataflowProcessors.get(workflowProcessor);
	}

	public Activity getWorkflowActivity(
			net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?> dataflowActiviy) {
		return dataflowToWorkflowActivities.get(dataflowActiviy);
	}

	public net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?> getDataflowActivity(
			Activity workflowActivity) {
		return workflowToDataflowActivities.get(workflowActivity);
	}

	protected Dataflow createDataflow() throws EditException, ActivityNotFoundException,
			ActivityConfigurationException, PropertyNotFoundException, InvalidWorkflowException, DispatchLayerNotFoundException, DispatchLayerConfigurationException {
		// create the dataflow
		Dataflow dataflow = edits.createDataflow();
		// set the dataflow name
		edits.getUpdateDataflowNameEdit(dataflow, new String(workflow.getName())).doEdit();

		addInputPorts(dataflow);

		addOutputPorts(dataflow);

		addProcessors(dataflow);

		addDataLinks();

		addControlLinks();

		return dataflow;
	}

	private void addProcessors(Dataflow dataflow) throws EditException, PropertyNotFoundException,
			ActivityNotFoundException, ActivityConfigurationException, InvalidWorkflowException, DispatchLayerNotFoundException, DispatchLayerConfigurationException {
		for (Processor processor : workflow.getProcessors()) {
			net.sf.taverna.t2.workflowmodel.Processor dataflowProcessor = edits
					.createProcessor(processor.getName());
			edits.getAddProcessorEdit(dataflow, dataflowProcessor).doEdit();
			// map the processor
			workflowToDataflowProcessors.put(processor, dataflowProcessor);
			dataflowToWorkflowProcessors.put(dataflowProcessor, processor);
			// add input ports
			for (InputProcessorPort inputProcessorPort : processor.getInputPorts()) {
				ProcessorInputPort processorInputPort = edits.createProcessorInputPort(
						dataflowProcessor, inputProcessorPort.getName(),
						inputProcessorPort.getDepth());
				edits.getAddProcessorInputPortEdit(dataflowProcessor, processorInputPort).doEdit();
				inputPorts.put(inputProcessorPort, processorInputPort);
			}
			// add output ports
			for (OutputProcessorPort outputProcessorPort : processor.getOutputPorts()) {
				ProcessorOutputPort processorOutputPort = edits.createProcessorOutputPort(
						dataflowProcessor, outputProcessorPort.getName(),
						outputProcessorPort.getDepth(), outputProcessorPort.getGranularDepth());
				edits.getAddProcessorOutputPortEdit(dataflowProcessor, processorOutputPort)
						.doEdit();
				outputPorts.put(outputProcessorPort, processorOutputPort);
			}

			// add dispatch stack
			 net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchStack dataflowDispatchStack = dataflowProcessor.getDispatchStack();
			 DispatchStack dispatchStack = processor.getDispatchStack();
			 for (int layer = 0; layer < dispatchStack.size(); layer++) {
				 URI uri = dispatchStack.get(layer).getConfigurableType();
				 edits.getAddDispatchLayerEdit(dataflowDispatchStack, dispatchLayerService.createDispatchLayer(uri, null), layer).doEdit();
			 }

			// addDefaultIterationStrategy(dataflowProcessor);

			// add iteration strategy
			// List<IterationStrategy> iterationStrategyStack =
			// processor.getIterationStrategyStack();
			// for (IterationStrategy iterationStrategy : iterationStrategyStack) {
			// iterationStrategy.
			// }

			// add bound activities
			List<ProcessorBinding> processorBindings = scufl2Tools.processorBindingsForProcessor(
					processor, profile);
			for (ProcessorBinding processorBinding : processorBindings) {
				addActivity(processorBinding);
			}
		}
	}

	private void addActivity(ProcessorBinding processorBinding) throws EditException,
			ActivityNotFoundException, PropertyNotFoundException, ActivityConfigurationException,
			InvalidWorkflowException {
		net.sf.taverna.t2.workflowmodel.Processor processor = workflowToDataflowProcessors
				.get(processorBinding.getBoundProcessor());
		Activity scufl2Activity = processorBinding.getBoundActivity();
		URI activityType = scufl2Activity.getConfigurableType();
		Configuration configuration = scufl2Tools.configurationFor(scufl2Activity, profile);

		// create the activity
		net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?> activity = null;
		// check if we have a nested workflow
		if (activityType.equals(URI.create("http://ns.taverna.org.uk/2010/activity/dataflow"))) {
			activity = activityService.createActivity(activityType, null);
			URI dataflowURI = configuration.getPropertyResource().getResourceURI();
			Workflow workflow = (Workflow) uriTools.resolveUri(dataflowURI, workflowBundle);
			WorkflowToDataflowMapper mapper = new WorkflowToDataflowMapper(workflowBundle,
					workflow, profile, edits, activityService, dispatchLayerService);
			Dataflow dataflow = mapper.getDataflow();
			try {
				((Configurable) activity).configure(dataflow);
			} catch (ConfigurationException e) {
				throw new ActivityConfigurationException(e);
			}
		} else {
			activity = activityService.createActivity(activityType, configuration);
		}

		// add the activity to the processor
		edits.getAddActivityEdit(processor, activity).doEdit();

		// map input ports
		for (ProcessorInputPortBinding portBinding : processorBinding.getInputPortBindings()) {
			InputProcessorPort processorPort = portBinding.getBoundProcessorPort();
			InputActivityPort activityPort = portBinding.getBoundActivityPort();
			edits.getAddActivityInputPortMappingEdit(activity, processorPort.getName(),
					activityPort.getName()).doEdit();
		}
		// map output ports
		for (ProcessorOutputPortBinding portBinding : processorBinding.getOutputPortBindings()) {
			OutputProcessorPort processorPort = portBinding.getBoundProcessorPort();
			OutputActivityPort activityPort = portBinding.getBoundActivityPort();
			edits.getAddActivityOutputPortMappingEdit(activity, processorPort.getName(),
					activityPort.getName()).doEdit();
		}
		workflowToDataflowActivities.put(scufl2Activity, activity);
		dataflowToWorkflowActivities.put(activity, scufl2Activity);
	}

	private void addDataLinks() throws EditException {
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
				MergeInputPort mergeInputPort = edits.createMergeInputPort(merge, "input"
						+ mergePosition, sink.getDepth());
				// add it to the correct position in the merge
				List<MergeInputPort> mergeInputPorts = (List<MergeInputPort>) merge.getInputPorts();
				if (mergePosition > mergeInputPorts.size()) {
					mergeInputPorts.add(mergeInputPort);
				} else {
					mergeInputPorts.add(mergePosition, mergeInputPort);
				}
				// connect a datalink into the merge
				Datalink datalinkIn = edits.createDatalink(source, mergeInputPort);
				edits.getConnectDatalinkEdit(datalinkIn).doEdit();
				// check if the merge output has been connected
				EventForwardingOutputPort mergeOutputPort = merge.getOutputPort();
				if (mergeOutputPort.getOutgoingLinks().size() == 0) {
					Datalink datalinkOut = edits.createDatalink(merge.getOutputPort(), sink);
					edits.getConnectDatalinkEdit(datalinkOut).doEdit();
				} else if (mergeOutputPort.getOutgoingLinks().size() == 1) {
					if (mergeOutputPort.getOutgoingLinks().iterator().next().getSink() != sink) {
						throw new EditException(
								"Cannot add a different sinkPort to a Merge that already has one defined");
					}
				} else {
					throw new EditException(
							"The merge instance cannot have more that 1 outgoing Datalink");
				}
			} else {
				Datalink datalink = edits.createDatalink(source, sink);
				edits.getConnectDatalinkEdit(datalink).doEdit();
			}
		}
	}

	private void addControlLinks() throws EditException {
		for (ControlLink controlLink : workflow.getControlLinks()) {
			if (controlLink instanceof BlockingControlLink) {
				BlockingControlLink blockingControlLink = (BlockingControlLink) controlLink;
				Processor untilFinished = blockingControlLink.getUntilFinished();
				Processor block = blockingControlLink.getBlock();
				edits.getCreateConditionEdit(workflowToDataflowProcessors.get(untilFinished),
						workflowToDataflowProcessors.get(block)).doEdit();
			}
		}
	}

	private void addOutputPorts(Dataflow dataflow) throws EditException {
		for (OutputWorkflowPort outputWorkflowPort : workflow.getOutputPorts()) {
			DataflowOutputPort dataflowOutputPort = edits.createDataflowOutputPort(
					outputWorkflowPort.getName(), dataflow);
			edits.getAddDataflowOutputPortEdit(dataflow, dataflowOutputPort).doEdit();
			inputPorts.put(outputWorkflowPort, dataflowOutputPort.getInternalInputPort());
		}
	}

	private void addInputPorts(Dataflow dataflow) throws EditException {
		for (InputWorkflowPort inputWorkflowPort : workflow.getInputPorts()) {
			DataflowInputPort dataflowInputPort = edits.createDataflowInputPort(
					inputWorkflowPort.getName(), inputWorkflowPort.getDepth(),
					inputWorkflowPort.getDepth(), dataflow);
			edits.getAddDataflowInputPortEdit(dataflow, dataflowInputPort).doEdit();
			outputPorts.put(inputWorkflowPort, dataflowInputPort.getInternalOutputPort());
		}
	}

	// private void addDefaultIterationStrategy(
	// net.sf.taverna.t2.workflowmodel.Processor dataflowProcessor) {
	// IterationStrategyImpl iterationStrategy = (IterationStrategyImpl) dataflowProcessor
	// .getIterationStrategy().getStrategies().get(0);
	// for (InputPort inputPort : dataflowProcessor.getInputPorts()) {
	// NamedInputPortNode inputPortNode = new NamedInputPortNode(inputPort.getName(),
	// inputPort.getDepth());
	// iterationStrategy.connectDefault(inputPortNode);
	// }
	// }

}
