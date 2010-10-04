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
package uk.org.taverna.platform.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.taverna.t2.activities.beanshell.BeanshellActivity;
import net.sf.taverna.t2.activities.beanshell.BeanshellActivityConfigurationBean;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EditsRegistry;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;
import net.sf.taverna.t2.workflowmodel.InputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.iteration.NamedInputPortNode;
import net.sf.taverna.t2.workflowmodel.processor.iteration.impl.IterationStrategyImpl;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.activity.InputActivityPort;
import uk.org.taverna.scufl2.api.activity.OutputActivityPort;
import uk.org.taverna.scufl2.api.common.Configurable;
import uk.org.taverna.scufl2.api.common.ConfigurableProperty;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.ToBeDecided;
import uk.org.taverna.scufl2.api.configurations.ConfigurablePropertyConfiguration;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.RunAfterCondition;
import uk.org.taverna.scufl2.api.core.StartCondition;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.port.Port;
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Translates a scufl2 {@link Workflow} into a {@link Dataflow}.
 * 
 * @author David Withers
 */
public class WorkflowToDataflowMapper {

	private static Edits edits = EditsRegistry.getEdits();

	private Map<Port, EventHandlingInputPort> inputPorts;

	private Map<Port, EventForwardingOutputPort> outputPorts;

	private Map<Workflow, Dataflow> workflowToDataflow;

	private Map<Dataflow, Workflow> dataflowToWorkflow;

	private Map<Processor, net.sf.taverna.t2.workflowmodel.Processor> workflowToDataflowProcessors;

	private Map<net.sf.taverna.t2.workflowmodel.Processor, Processor> dataflowToWorkflowProcessors;

	private Map<Activity, net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?>> workflowToDataflowActivities;

	private Map<net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?>, Activity> dataflowToWorkflowActivities;
	
	private final Workflow workflow;

	private final Profile profile;

	private final Dataflow dataflow;


	public WorkflowToDataflowMapper(Workflow workflow, Profile profile) throws InvalidWorkflowException {
		this.workflow = workflow;
		this.profile = profile;
		workflowToDataflow = new HashMap<Workflow, Dataflow>();
		dataflowToWorkflow = new HashMap<Dataflow, Workflow>();
		inputPorts = new IdentityHashMap<Port, EventHandlingInputPort>();
		outputPorts = new IdentityHashMap<Port, EventForwardingOutputPort>();
		workflowToDataflowProcessors = new IdentityHashMap<Processor, net.sf.taverna.t2.workflowmodel.Processor>();
		dataflowToWorkflowProcessors = new HashMap<net.sf.taverna.t2.workflowmodel.Processor, Processor>();
		workflowToDataflowActivities = new IdentityHashMap<Activity, net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?>>();
		dataflowToWorkflowActivities = new HashMap<net.sf.taverna.t2.workflowmodel.processor.activity.Activity<?>, Activity>();
		try {
			dataflow = createDataflow();
		} catch (EditException e) {
			throw new InvalidWorkflowException(e);
		}
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public Dataflow getDataflow() {
		return dataflow;
	}

	public Workflow getWorkflow(Dataflow datafow) {
		return dataflowToWorkflow.get(datafow);
	}

	public Dataflow getDataflow(Workflow workflow) {
		return workflowToDataflow.get(workflow);
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

	protected Dataflow createDataflow() throws EditException {
		// create the dataflow
		Dataflow dataflow = edits.createDataflow();
		// set the dataflow name
		edits.getUpdateDataflowNameEdit(dataflow, new String(workflow.getName())).doEdit();
		// map the workflow
		workflowToDataflow.put(workflow, dataflow);
		dataflowToWorkflow.put(dataflow, workflow);

		addInputPorts(dataflow);
		
		addOutputPorts(dataflow);
		
		addProcessors(dataflow);

		addDataLinks();
		
		// add start conditions
		for (Entry<Processor, net.sf.taverna.t2.workflowmodel.Processor> entry : workflowToDataflowProcessors
				.entrySet()) {
			Processor processor = entry.getKey();
			net.sf.taverna.t2.workflowmodel.Processor dataflowProcessor = entry.getValue();
			for (StartCondition startCondition : processor.getStartConditions()) {
				if (startCondition instanceof RunAfterCondition) {
					RunAfterCondition runAfterCondition = (RunAfterCondition) startCondition;
					Processor controllingProcessor = runAfterCondition.getControllingProcessor();
					edits.getCreateConditionEdit(
							workflowToDataflowProcessors.get(controllingProcessor),
							dataflowProcessor).doEdit();
				}
			}
		}

		addActivities();

		return dataflow;
	}

	private void addProcessors(Dataflow dataflow) throws EditException {
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
			//add output ports
			for (OutputProcessorPort outputProcessorPort : processor.getOutputPorts()) {
				ProcessorOutputPort processorOutputPort = edits.createProcessorOutputPort(
						dataflowProcessor, outputProcessorPort.getName(),
						outputProcessorPort.getDepth(), outputProcessorPort.getGranularDepth());
				edits.getAddProcessorOutputPortEdit(dataflowProcessor, processorOutputPort)
						.doEdit();
				outputPorts.put(outputProcessorPort, processorOutputPort);
			}

			// add dispatch stack
			ToBeDecided dispatchStack = processor.getDispatchStack();
			// TODO dispatchStack not finished so add default for now
			edits.getDefaultDispatchStackEdit(dataflowProcessor).doEdit();

			addDefaultIterationStrategy(dataflowProcessor);

			// add iteration strategy
			// List<IterationStrategy> iterationStrategyStack =
			// processor.getIterationStrategyStack();
			// for (IterationStrategy iterationStrategy : iterationStrategyStack) {
			// iterationStrategy.
			// }
		}
	}

	private void addActivities() throws EditException {
		for (ProcessorBinding processorBinding : profile.getProcessorBindings()) {
			net.sf.taverna.t2.workflowmodel.Processor processor = workflowToDataflowProcessors.get(processorBinding.getBoundProcessor());
			Activity activity = processorBinding.getBoundActivity();
			// TODO decide how to map activities properly
			if (activity.getType().getName().endsWith("beanshell")) {
				BeanshellActivity beanshellActivity = new BeanshellActivity();
				BeanshellActivityConfigurationBean configurationBean = new BeanshellActivityConfigurationBean();
				NamedSet<ConfigurableProperty> configurableProperties = activity.getConfigurableProperties();
				for (ConfigurableProperty configurableProperty : configurableProperties) {
					if (configurableProperty.getName().endsWith("script")) {
						configurationBean.setScript((String) getValue(activity, configurableProperty));
					}
				}
				List<ActivityInputPortDefinitionBean> inputPortDefinitionBeans = new ArrayList<ActivityInputPortDefinitionBean>();
				for (InputActivityPort inputActivityPort : activity.getInputPorts()) {
					ActivityInputPortDefinitionBean inputPortDefinitionBean = new ActivityInputPortDefinitionBean();
					inputPortDefinitionBean.setName(inputActivityPort.getName());
					inputPortDefinitionBean.setDepth(/*inputActivityPort.getDepth()*/0);
					inputPortDefinitionBeans.add(inputPortDefinitionBean);
				}
				configurationBean.setInputPortDefinitions(inputPortDefinitionBeans);
				List<ActivityOutputPortDefinitionBean> outputPortDefinitionBeans = new ArrayList<ActivityOutputPortDefinitionBean>();
				for (OutputActivityPort outputActivityPort : activity.getOutputPorts()) {
					ActivityOutputPortDefinitionBean outputPortDefinitionBean = new ActivityOutputPortDefinitionBean();
					outputPortDefinitionBean.setName(outputActivityPort.getName());
					outputPortDefinitionBean.setDepth(/*outputActivityPort.getDepth()*/processor.getLocalName().equals("ListGenerator")?1:0);
					outputPortDefinitionBeans.add(outputPortDefinitionBean);
				}
				configurationBean.setOutputPortDefinitions(outputPortDefinitionBeans);
				edits.getConfigureActivityEdit(beanshellActivity, configurationBean).doEdit();
				edits.getAddActivityEdit(processor, beanshellActivity).doEdit();
				for (ProcessorInputPortBinding processorInputPortBinding : processorBinding.getInputPortBindings()) {
					EventHandlingInputPort inputPort = inputPorts.get(processorInputPortBinding.getBoundProcessorPort());
					edits.getAddActivityInputPortMappingEdit(beanshellActivity, inputPort.getName(), inputPort.getName()).doEdit();
				}
				for (ProcessorOutputPortBinding processorOutputPortBinding : processorBinding.getOutputPortBindings()) {
					EventForwardingOutputPort outputPort = outputPorts.get(processorOutputPortBinding.getBoundProcessorPort());
					edits.getAddActivityOutputPortMappingEdit(beanshellActivity, outputPort.getName(), outputPort.getName()).doEdit();
				}
//				System.out.println(beanshellActivity);
//				System.out.println(beanshellActivity.getInputPorts());
//				System.out.println(beanshellActivity.getOutputPorts());
//				System.out.println(beanshellActivity.getInputPortMapping());
//				System.out.println(beanshellActivity.getOutputPortMapping());
//				System.out.println(beanshellActivity.getConfiguration().getScript());
//				System.out.println();
			} else {
				throw new RuntimeException("Unknown activity : " + activity.getType().getName());
			}
		}
	}

	private Object getValue(Configurable configurable, ConfigurableProperty configurableProperty) {
		NamedSet<Configuration> configurations = profile.getConfigurations();
		for (Configuration configuration : configurations) {
			if (configuration.getConfigured().equals(configurable)) {
				Set<ConfigurablePropertyConfiguration> configurablePropertyConfigurations = configuration.getConfigurablePropertyConfigurations();
				for (ConfigurablePropertyConfiguration configurablePropertyConfiguration : configurablePropertyConfigurations) {
					if (configurablePropertyConfiguration.getConfiguredProperty().equals(configurableProperty)) {
						return configurablePropertyConfiguration.getValue();
					}
				}
			}
		}
		return null;
	}

	private void addDataLinks() throws EditException {
		for (DataLink dataLink : workflow.getDatalinks()) {
			ReceiverPort receiverPort = dataLink.getReceiverPort();
			SenderPort senderPort = dataLink.getSenderPort();
			EventForwardingOutputPort source = outputPorts.get(senderPort);
			EventHandlingInputPort sink = inputPorts.get(receiverPort);
			Datalink datalink = edits.createDatalink(source, sink);
			edits.getConnectDatalinkEdit(datalink).doEdit();
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

	private void addDefaultIterationStrategy(
			net.sf.taverna.t2.workflowmodel.Processor dataflowProcessor) {
		IterationStrategyImpl iterationStrategy = (IterationStrategyImpl) dataflowProcessor
				.getIterationStrategy().getStrategies().get(0);
		for (InputPort inputPort : dataflowProcessor.getInputPorts()) {
			NamedInputPortNode inputPortNode = new NamedInputPortNode(inputPort.getName(),
					inputPort.getDepth());
			iterationStrategy.connectDefault(inputPortNode);
		}
	}

}
