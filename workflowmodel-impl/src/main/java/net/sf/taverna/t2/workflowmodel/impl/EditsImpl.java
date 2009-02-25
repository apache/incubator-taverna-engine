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

import net.sf.taverna.t2.annotation.AddAnnotationAssertionEdit;
import net.sf.taverna.t2.annotation.Annotated;
import net.sf.taverna.t2.annotation.AnnotationAssertion;
import net.sf.taverna.t2.annotation.AnnotationAssertionImpl;
import net.sf.taverna.t2.annotation.AnnotationBeanSPI;
import net.sf.taverna.t2.annotation.AnnotationChain;
import net.sf.taverna.t2.annotation.AnnotationChainImpl;
import net.sf.taverna.t2.annotation.AnnotationRole;
import net.sf.taverna.t2.annotation.AnnotationSourceSPI;
import net.sf.taverna.t2.annotation.CurationEvent;
import net.sf.taverna.t2.annotation.Person;
import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.facade.impl.WorkflowInstanceFacadeImpl;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.workflowmodel.CompoundEdit;
import net.sf.taverna.t2.workflowmodel.Configurable;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;
import net.sf.taverna.t2.workflowmodel.InvalidDataflowException;
import net.sf.taverna.t2.workflowmodel.Merge;
import net.sf.taverna.t2.workflowmodel.MergeInputPort;
import net.sf.taverna.t2.workflowmodel.OrderedPair;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.impl.ActivityInputPortImpl;
import net.sf.taverna.t2.workflowmodel.processor.activity.impl.ActivityOutputPortImpl;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchStack;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.impl.AddDispatchLayerEdit;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.impl.DeleteDispatchLayerEdit;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategyStack;

/**
 * Implementation of {@link Edits}
 * 
 */
public class EditsImpl implements Edits {

	public Dataflow createDataflow() {
		return new DataflowImpl();
	}

	public Datalink createDatalink(EventForwardingOutputPort source,
			EventHandlingInputPort sink) {
		return new DatalinkImpl(source, sink);
	}

	public DataflowInputPort createDataflowInputPort(String name, int depth,
			int granularDepth, Dataflow dataflow) {
		return new DataflowInputPortImpl(name, depth, granularDepth, dataflow);
	}

	public DataflowOutputPort createDataflowOutputPort(String name,
			Dataflow dataflow) {
		return new DataflowOutputPortImpl(name, dataflow);
	}

	public MergeInputPort createMergeInputPort(Merge merge, String name,
			int depth) {
		if (merge instanceof MergeImpl) {
			return new MergeInputPortImpl((MergeImpl) merge, name, depth);
		} else {
			return null;
		}
	}

	public ProcessorOutputPort createProcessorOutputPort(Processor processor,
			String name, int depth, int granularDepth) {
		return new ProcessorOutputPortImpl((ProcessorImpl) processor, name,
				depth, granularDepth);
	}

	public ProcessorInputPort createProcessorInputPort(Processor processor,
			String name, int depth) {
		return new ProcessorInputPortImpl((ProcessorImpl) processor, name,
				depth);
	}

	public Edit<Dataflow> getAddProcessorEdit(Dataflow dataflow,
			Processor processor) {
		return new AddProcessorEdit(dataflow, processor);
	}

	public Edit<Dataflow> getAddMergeEdit(Dataflow dataflow, Merge merge) {
		return new AddMergeEdit(dataflow, merge);
	}

	public Edit<DispatchStack> getAddDispatchLayerEdit(DispatchStack stack,
			DispatchLayer<?> layer, int position) {
		return new AddDispatchLayerEdit(stack, layer, position);
	}

	public Edit<Processor> getAddActivityEdit(Processor processor,
			Activity<?> activity) {
		return new AddActivityEdit(processor, activity);
	}

	public Edit<Processor> getAddProcessorInputPortEdit(Processor processor,
			ProcessorInputPort port) {
		return new AddProcessorInputPortEdit(processor, port);
	}

	public Edit<Processor> getAddProcessorOutputPortEdit(Processor processor,
			ProcessorOutputPort port) {
		return new AddProcessorOutputPortEdit(processor, port);
	}

	public Edit<Dataflow> getCreateDataflowInputPortEdit(Dataflow dataflow,
			String portName, int portDepth, int granularDepth) {
		return new CreateDataflowInputPortEdit(dataflow, portName, portDepth,
				granularDepth);
	}

	public Edit<Dataflow> getCreateDataflowOutputPortEdit(Dataflow dataflow,
			String portName) {
		return new CreateDataflowOutputPortEdit(dataflow, portName);
	}

	public Edit<DispatchStack> getDeleteDispatchLayerEdit(DispatchStack stack,
			DispatchLayer<?> layer) {
		return new DeleteDispatchLayerEdit(stack, layer);
	}

	public Edit<Processor> getRenameProcessorEdit(Processor processor,
			String newName) {
		return new RenameProcessorEdit(processor, newName);
	}

	public Edit<DataflowInputPort> getRenameDataflowInputPortEdit(
			DataflowInputPort dataflowInputPort, String newName) {
		return new RenameDataflowInputPortEdit(dataflowInputPort, newName);
	}

	public Edit<DataflowOutputPort> getRenameDataflowOutputPortEdit(
			DataflowOutputPort dataflowOutputPort, String newName) {
		return new RenameDataflowOutputPortEdit(dataflowOutputPort, newName);
	}

	public Edit<DataflowInputPort> getChangeDataflowInputPortDepthEdit(
			DataflowInputPort dataflowInputPort, int depth) {
		return new ChangeDataflowInputPortDepthEdit(dataflowInputPort, depth);
	}

	public Edit<DataflowInputPort> getChangeDataflowInputPortGranularDepthEdit(
			DataflowInputPort dataflowInputPort, int granularDepth) {
		return new ChangeDataflowInputPortGranularDepthEdit(dataflowInputPort,
				granularDepth);
	}

	public Edit<Processor> getConnectProcessorOutputEdit(Processor processor,
			String outputPortName, EventHandlingInputPort targetPort) {
		return new ConnectProcesorOutputEdit(processor, outputPortName,
				targetPort);
	}

	public Edit<Datalink> getConnectDatalinkEdit(Datalink datalink) {
		return new ConnectDatalinkEdit(datalink);
	}

	@SuppressWarnings("unchecked")
	public Edit<AnnotationChain> getAddAnnotationAssertionEdit(
			AnnotationChain annotationChain,
			AnnotationAssertion annotationAssertion) {
		return new AddAnnotationAssertionEdit(annotationChain,
				annotationAssertion);
	}

	/**
	 * Creates a MergeImpl instance. Merge names are generated as 'Merge0',
	 * 'Merge1', 'Merge2', etc. The next merge to be added always gets the name
	 * as the previous merge in the list with its index incremented by one. If a
	 * merge is deleted, that is not taken into account when generating merges'
	 * names.
	 */
	public Merge createMerge(Dataflow dataflow) {

		String mergeName;

		// Get all merges for a workflow
		List<? extends Merge> merges = (List<? extends Merge>) dataflow
				.getMerges();

		if (merges.isEmpty()) {
			mergeName = "Merge0"; // the first merge to be added to the list
		} else {
			String lastMergeName = merges.get(merges.size() - 1).getLocalName();
			// Get the index of the last Merge
			int lastMergeIndex = Integer.parseInt(lastMergeName.substring(5));
			mergeName = "Merge" + String.valueOf((lastMergeIndex + 1));
		}

		return new MergeImpl(mergeName);
	}

	/**
	 * @return a new instance of ConnectMergedDatalinkEdit constructed from the
	 *         provided parameters.
	 * 
	 * @param merge
	 *            a Merge instance
	 * @param sourcePort
	 *            the source port from which a link is to be created.
	 * @param sinkPort
	 *            the sink port to which the link is to be created.
	 */
	public Edit<Merge> getConnectMergedDatalinkEdit(Merge merge,
			EventForwardingOutputPort sourcePort,
			EventHandlingInputPort sinkPort) {
		return new ConnectMergedDatalinkEdit(merge, sourcePort, sinkPort);
	}

	public Edit<OrderedPair<Processor>> getCreateConditionEdit(
			Processor control, Processor target) {
		return new CreateConditionEdit(control, target);
	}

	public Edit<OrderedPair<Processor>> getRemoveConditionEdit(
			Processor control, Processor target) {
		return new RemoveConditionEdit(control, target);
	}

	public Processor createProcessor(String name) {
		ProcessorImpl processor = new ProcessorImpl();
		processor.setName(name);
		return processor;
	}

	/**
	 * Builds an instance of {@link ActivityInputPortImpl}
	 */
	public ActivityInputPort createActivityInputPort(
			String portName,
			int portDepth,
			boolean allowsLiteralValues,
			List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes,
			Class<?> translatedElementClass) {
		return new ActivityInputPortImpl(portName, portDepth,
				allowsLiteralValues, handledReferenceSchemes,
				translatedElementClass);
	}

	/**
	 * Builds an instance of {@link ActivityOutputPortImpl}
	 */
	public OutputPort createActivityOutputPort(String portName, int portDepth,
			int portGranularDepth) {
		return new ActivityOutputPortImpl(portName, portDepth,
				portGranularDepth);
	}

	public WorkflowInstanceFacade createWorkflowInstanceFacade(
			Dataflow dataflow, InvocationContext context, String parentProcess)
			throws InvalidDataflowException {
		return new WorkflowInstanceFacadeImpl(dataflow, context, parentProcess);
	}

	@SuppressWarnings("unchecked")
	public Edit<AnnotationAssertion> getAddAnnotationBean(
			AnnotationAssertion annotationAssertion,
			AnnotationBeanSPI annotationBean) {
		return new AddAnnotationBeanEdit(annotationAssertion, annotationBean);
	}

	@SuppressWarnings("unchecked")
	public Edit<AnnotationAssertion> getAddCurationEvent(
			AnnotationAssertion annotationAssertion, CurationEvent curationEvent) {
		return new AddCurationEventEdit(annotationAssertion, curationEvent);
	}

	@SuppressWarnings("unchecked")
	public Edit<AnnotationAssertion> getAddAnnotationRole(
			AnnotationAssertion annotationAssertion,
			AnnotationRole annotationRole) {
		return new AddAnnotationRoleEdit(annotationAssertion, annotationRole);
	}

	@SuppressWarnings("unchecked")
	public Edit<AnnotationAssertion> getAddAnnotationSource(
			AnnotationAssertion annotationAssertion,
			AnnotationSourceSPI annotationSource) {
		return new AddAnnotationSourceEdit(annotationAssertion,
				annotationSource);
	}

	@SuppressWarnings("unchecked")
	public Edit<AnnotationAssertion> getAddCreator(
			AnnotationAssertion annotationAssertion, Person person) {
		return new AddCreatorEdit(annotationAssertion, person);
	}

	public Edit<?> getAddAnnotationChainEdit(Annotated<?> annotated,
			AnnotationBeanSPI annotation) {
		List<Edit<?>> editList = new ArrayList<Edit<?>>();

		AnnotationAssertion<?> annotationAssertion = new AnnotationAssertionImpl();
		editList.add(getAddAnnotationBean(annotationAssertion, annotation));

		AnnotationChain annotationChain = new AnnotationChainImpl();
		editList.add(getAddAnnotationAssertionEdit(annotationChain,
				annotationAssertion));

		editList.add(annotated.getAddAnnotationEdit(annotationChain));

		return new CompoundEdit(editList);
	}

	public Edit<Dataflow> getUpdateDataflowNameEdit(Dataflow dataflow,
			String newName) {
		return new UpdateDataflowNameEdit(dataflow, newName);
	}

	public Edit<Dataflow> getUpdateDataflowInternalIdentifierEdit(
			Dataflow dataflow, String newId) {
		return new UpdateDataflowInternalIdentifierEdit(dataflow, newId);
	}

	public Edit<Datalink> getDisconnectDatalinkEdit(Datalink datalink) {
		return new DisconnectDatalinkEdit(datalink);
	}

	public Edit<Dataflow> getRemoveDataflowInputPortEdit(Dataflow dataflow,
			DataflowInputPort dataflowInputPort) {
		return new RemoveDataflowInputPortEdit(dataflow, dataflowInputPort);
	}

	public Edit<Dataflow> getRemoveDataflowOutputPortEdit(Dataflow dataflow,
			DataflowOutputPort dataflowOutputPort) {
		return new RemoveDataflowOutputPortEdit(dataflow, dataflowOutputPort);
	}

	public Edit<Dataflow> getRemoveProcessorEdit(Dataflow dataflow,
			Processor processor) {
		return new RemoveProcessorEdit(dataflow, processor);
	}

	public Edit<Dataflow> getRemoveMergeEdit(Dataflow dataflow, Merge merge) {
		return new RemoveMergeEdit(dataflow, merge);
	}

	public Edit<Dataflow> getAddDataflowInputPortEdit(Dataflow dataflow,
			DataflowInputPort dataflowInputPort) {
		return new AddDataflowInputPortEdit(dataflow, dataflowInputPort);
	}

	public Edit<Dataflow> getAddDataflowOutputPortEdit(Dataflow dataflow,
			DataflowOutputPort dataflowOutputPort) {
		return new AddDataflowOutputPortEdit(dataflow, dataflowOutputPort);
	}

	public Edit<Activity<?>> getAddActivityInputPortEdit(Activity<?> activity,
			ActivityInputPort activityInputPort) {
		return new AddActivityInputPortEdit(activity, activityInputPort);
	}

	public Edit<Activity<?>> getAddActivityInputPortMappingEdit(
			Activity<?> activity, String processorPortName,
			String activityPortName) {
		return new AddActivityInputPortMappingEdit(activity, processorPortName,
				activityPortName);
	}

	public Edit<Activity<?>> getAddActivityOutputPortEdit(Activity<?> activity,
			OutputPort activityOutputPort) {
		return new AddActivityOutputPortEdit(activity, activityOutputPort);
	}

	public Edit<Activity<?>> getAddActivityOutputPortMappingEdit(
			Activity<?> activity, String processorPortName,
			String activityPortName) {
		return new AddActivityOutputPortMappingEdit(activity,
				processorPortName, activityPortName);
	}

	public Edit<Activity<?>> getRemoveActivityInputPortEdit(
			Activity<?> activity, ActivityInputPort activityInputPort) {
		return new RemoveActivityInputPortEdit(activity, activityInputPort);
	}

	public Edit<Activity<?>> getRemoveActivityInputPortMappingEdit(
			Activity<?> activity, String processorPortName) {
		return new RemoveActivityInputPortMappingEdit(activity,
				processorPortName);
	}

	public Edit<Activity<?>> getRemoveActivityOutputPortEdit(
			Activity<?> activity, OutputPort activityOutputPort) {
		return new RemoveActivityOutputPortEdit(activity, activityOutputPort);
	}

	public Edit<Activity<?>> getRemoveActivityOutputPortMappingEdit(
			Activity<?> activity, String processorPortName) {
		return new RemoveActivityOutputPortMappingEdit(activity,
				processorPortName);
	}

	public Edit<Merge> getAddMergeInputPortEdit(Merge merge,
			MergeInputPort mergeInputPort) {
		return new AddMergeInputPortEdit(merge, mergeInputPort);
	}

	public <ConfigurationBean> Edit<Activity<?>> getConfigureActivityEdit(
			Activity<ConfigurationBean> activity,
			ConfigurationBean configurationBean) {
		return new ConfigureActivityEdit(activity, configurationBean);
	}

	public Edit<Processor> getRemoveProcessorInputPortEdit(Processor processor,
			ProcessorInputPort port) {
		return new RemoveProcessorInputPortEdit(processor, port);
	}

	public Edit<Processor> getRemoveProcessorOutputPortEdit(
			Processor processor, ProcessorOutputPort port) {
		return new RemoveProcessorOutputPortEdit(processor, port);
	}

	public Edit<Processor> getMapProcessorPortsForActivityEdit(
			Processor processor) {
		return new MapProcessorPortsForActivityEdit(processor);
	}

	public Edit<Processor> getDefaultDispatchStackEdit(Processor processor) {
		return new DefaultDispatchStackEdit(processor);
	}

	public Edit<Processor> getSetIterationStrategyStackEdit(
			Processor processor, IterationStrategyStack iterationStrategyStack) {
		return new SetIterationStrategyStackEdit(processor,
				iterationStrategyStack);
	}

	public <ConfigurationType> Edit<? extends Configurable<ConfigurationType>> getConfigureEdit(
			Configurable<ConfigurationType> configurable,
			ConfigurationType configBean) {
		return new ConfigureEdit<Configurable<ConfigurationType>, Configurable<ConfigurationType>>(
				Configurable.class, configurable, configBean);
	}

}
