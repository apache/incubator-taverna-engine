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

import net.sf.taverna.t2.annotation.Annotated;
import net.sf.taverna.t2.annotation.AnnotationAssertion;
import net.sf.taverna.t2.annotation.AnnotationBeanSPI;
import net.sf.taverna.t2.annotation.AnnotationChain;
import net.sf.taverna.t2.annotation.AnnotationRole;
import net.sf.taverna.t2.annotation.AnnotationSourceSPI;
import net.sf.taverna.t2.annotation.CurationEvent;
import net.sf.taverna.t2.annotation.Person;
import net.sf.taverna.t2.annotation.impl.AddAnnotationAssertionEdit;
import net.sf.taverna.t2.annotation.impl.AnnotationAssertionImpl;
import net.sf.taverna.t2.annotation.impl.AnnotationChainImpl;
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
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.impl.ActivityInputPortImpl;
import net.sf.taverna.t2.workflowmodel.processor.activity.impl.ActivityOutputPortImpl;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchStack;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.impl.AddDispatchLayerEdit;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.impl.DeleteDispatchLayerEdit;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategy;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategyStack;
import net.sf.taverna.t2.workflowmodel.processor.iteration.NamedInputPortNode;
import net.sf.taverna.t2.workflowmodel.processor.iteration.impl.IterationStrategyImpl;

/**
 * Implementation of {@link Edits}
 *
 */
public class EditsImpl implements Edits {

	@Override
	public Dataflow createDataflow() {
		return new DataflowImpl();
	}

	@Override
	public Datalink createDatalink(EventForwardingOutputPort source,
			EventHandlingInputPort sink) {
		return new DatalinkImpl(source, sink);
	}

	@Override
	public DataflowInputPort createDataflowInputPort(String name, int depth,
			int granularDepth, Dataflow dataflow) {
		return new DataflowInputPortImpl(name, depth, granularDepth, dataflow);
	}

	@Override
	public DataflowOutputPort createDataflowOutputPort(String name,
			Dataflow dataflow) {
		return new DataflowOutputPortImpl(name, dataflow);
	}

	@Override
	public MergeInputPort createMergeInputPort(Merge merge, String name,
			int depth) {
		if (merge instanceof MergeImpl) {
			return new MergeInputPortImpl((MergeImpl) merge, name, depth);
		} else {
			return null;
		}
	}

	@Override
	public ProcessorOutputPort createProcessorOutputPort(Processor processor,
			String name, int depth, int granularDepth) {
		return new ProcessorOutputPortImpl((ProcessorImpl) processor, name,
				depth, granularDepth);
	}

	@Override
	public ProcessorInputPort createProcessorInputPort(Processor processor,
			String name, int depth) {
		return new ProcessorInputPortImpl((ProcessorImpl) processor, name,
				depth);
	}

	@Override
	public Edit<Dataflow> getAddProcessorEdit(Dataflow dataflow,
			Processor processor) {
		return new AddProcessorEdit(dataflow, processor);
	}

	@Override
	public Edit<Dataflow> getAddMergeEdit(Dataflow dataflow, Merge merge) {
		return new AddMergeEdit(dataflow, merge);
	}

	@Override
	public Edit<DispatchStack> getAddDispatchLayerEdit(DispatchStack stack,
			DispatchLayer<?> layer, int position) {
		return new AddDispatchLayerEdit(stack, layer, position);
	}

	@Override
	public Edit<Processor> getAddActivityEdit(Processor processor,
			Activity<?> activity) {
		return new AddActivityEdit(processor, activity);
	}

	@Override
	public Edit<Processor> getAddProcessorInputPortEdit(Processor processor,
			ProcessorInputPort port) {
		return new AddProcessorInputPortEdit(processor, port);
	}

	@Override
	public Edit<Processor> getAddProcessorOutputPortEdit(Processor processor,
			ProcessorOutputPort port) {
		return new AddProcessorOutputPortEdit(processor, port);
	}

	@Override
	public Edit<Dataflow> getCreateDataflowInputPortEdit(Dataflow dataflow,
			String portName, int portDepth, int granularDepth) {
		return new CreateDataflowInputPortEdit(dataflow, portName, portDepth,
				granularDepth);
	}

	@Override
	public Edit<Dataflow> getCreateDataflowOutputPortEdit(Dataflow dataflow,
			String portName) {
		return new CreateDataflowOutputPortEdit(dataflow, portName);
	}

	@Override
	public Edit<DispatchStack> getDeleteDispatchLayerEdit(DispatchStack stack,
			DispatchLayer<?> layer) {
		return new DeleteDispatchLayerEdit(stack, layer);
	}

	@Override
	public Edit<Merge> getRenameMergeEdit(Merge merge,
			String newName) {
		return new RenameMergeEdit(merge, newName);
	}

	@Override
	public Edit<Processor> getRenameProcessorEdit(Processor processor,
			String newName) {
		return new RenameProcessorEdit(processor, newName);
	}

	@Override
	public Edit<DataflowInputPort> getRenameDataflowInputPortEdit(
			DataflowInputPort dataflowInputPort, String newName) {
		return new RenameDataflowInputPortEdit(dataflowInputPort, newName);
	}

	@Override
	public Edit<DataflowOutputPort> getRenameDataflowOutputPortEdit(
			DataflowOutputPort dataflowOutputPort, String newName) {
		return new RenameDataflowOutputPortEdit(dataflowOutputPort, newName);
	}

	@Override
	public Edit<DataflowInputPort> getChangeDataflowInputPortDepthEdit(
			DataflowInputPort dataflowInputPort, int depth) {
		return new ChangeDataflowInputPortDepthEdit(dataflowInputPort, depth);
	}

	@Override
	public Edit<DataflowInputPort> getChangeDataflowInputPortGranularDepthEdit(
			DataflowInputPort dataflowInputPort, int granularDepth) {
		return new ChangeDataflowInputPortGranularDepthEdit(dataflowInputPort,
				granularDepth);
	}

	@Override
	public Edit<Processor> getConnectProcessorOutputEdit(Processor processor,
			String outputPortName, EventHandlingInputPort targetPort) {
		return new ConnectProcesorOutputEdit(processor, outputPortName,
				targetPort);
	}

	@Override
	public Edit<Datalink> getConnectDatalinkEdit(Datalink datalink) {
		return new ConnectDatalinkEdit(datalink);
	}

	@Override
	public AnnotationChain createAnnotationChain() {
		return new AnnotationChainImpl();
	}


	@Override
	public Edit<AnnotationChain> getAddAnnotationAssertionEdit(
			AnnotationChain annotationChain,
			AnnotationAssertion<?> annotationAssertion) {
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
	@Override
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
	@Override
	public Edit<Merge> getConnectMergedDatalinkEdit(Merge merge,
			EventForwardingOutputPort sourcePort,
			EventHandlingInputPort sinkPort) {
		return new ConnectMergedDatalinkEdit(merge, sourcePort, sinkPort);
	}

	@Override
	public Edit<OrderedPair<Processor>> getCreateConditionEdit(
			Processor control, Processor target) {
		return new CreateConditionEdit(control, target);
	}

	@Override
	public Edit<OrderedPair<Processor>> getRemoveConditionEdit(
			Processor control, Processor target) {
		return new RemoveConditionEdit(control, target);
	}

	@Override
	public Processor createProcessor(String name) {
		ProcessorImpl processor = new ProcessorImpl();
		processor.setName(name);
		return processor;
	}

	@Override
	public IterationStrategy createIterationStrategy() {
		return new IterationStrategyImpl();
	}

	/**
	 * Builds an instance of {@link ActivityInputPortImpl}
	 */
	@Override
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
	@Override
	public ActivityOutputPort createActivityOutputPort(String portName, int portDepth,
			int portGranularDepth) {
		return new ActivityOutputPortImpl(portName, portDepth,
				portGranularDepth);
	}

	@Override
	public WorkflowInstanceFacade createWorkflowInstanceFacade(
			Dataflow dataflow, InvocationContext context, String parentProcess)
			throws InvalidDataflowException {
		return new WorkflowInstanceFacadeImpl(dataflow, context, parentProcess);
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public Edit<AnnotationAssertion<AnnotationBeanSPI>> getAddAnnotationBean(
			AnnotationAssertion annotationAssertion,
			AnnotationBeanSPI annotationBean) {
		return new AddAnnotationBeanEdit(annotationAssertion, annotationBean);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Edit<AnnotationAssertion<AnnotationBeanSPI>> getAddCurationEvent(
			AnnotationAssertion annotationAssertion, CurationEvent curationEvent) {
		return new AddCurationEventEdit(annotationAssertion, curationEvent);
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public Edit<AnnotationAssertion<AnnotationBeanSPI>> getAddAnnotationRole(
			AnnotationAssertion annotationAssertion,
			AnnotationRole annotationRole) {
		return new AddAnnotationRoleEdit(annotationAssertion, annotationRole);
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public Edit<AnnotationAssertion<AnnotationBeanSPI>> getAddAnnotationSource(
			AnnotationAssertion annotationAssertion,
			AnnotationSourceSPI annotationSource) {
		return new AddAnnotationSourceEdit(annotationAssertion,
				annotationSource);
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public Edit<AnnotationAssertion<AnnotationBeanSPI>> getAddCreator(
			AnnotationAssertion annotationAssertion, Person person) {
		return new AddCreatorEdit(annotationAssertion, person);
	}

	@Override
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

	@Override
	public Edit<Dataflow> getUpdateDataflowNameEdit(Dataflow dataflow,
			String newName) {
		return new UpdateDataflowNameEdit(dataflow, newName);
	}

	@Override
	public Edit<Dataflow> getUpdateDataflowInternalIdentifierEdit(
			Dataflow dataflow, String newId) {
		return new UpdateDataflowInternalIdentifierEdit(dataflow, newId);
	}

	@Override
	public Edit<Datalink> getDisconnectDatalinkEdit(Datalink datalink) {
		return new DisconnectDatalinkEdit(datalink);
	}

	@Override
	public Edit<Dataflow> getRemoveDataflowInputPortEdit(Dataflow dataflow,
			DataflowInputPort dataflowInputPort) {
		return new RemoveDataflowInputPortEdit(dataflow, dataflowInputPort);
	}

	@Override
	public Edit<Dataflow> getRemoveDataflowOutputPortEdit(Dataflow dataflow,
			DataflowOutputPort dataflowOutputPort) {
		return new RemoveDataflowOutputPortEdit(dataflow, dataflowOutputPort);
	}

	@Override
	public Edit<Dataflow> getRemoveProcessorEdit(Dataflow dataflow,
			Processor processor) {
		return new RemoveProcessorEdit(dataflow, processor);
	}

	@Override
	public Edit<Dataflow> getRemoveMergeEdit(Dataflow dataflow, Merge merge) {
		return new RemoveMergeEdit(dataflow, merge);
	}

	@Override
	public Edit<Dataflow> getAddDataflowInputPortEdit(Dataflow dataflow,
			DataflowInputPort dataflowInputPort) {
		return new AddDataflowInputPortEdit(dataflow, dataflowInputPort);
	}

	@Override
	public Edit<Dataflow> getAddDataflowOutputPortEdit(Dataflow dataflow,
			DataflowOutputPort dataflowOutputPort) {
		return new AddDataflowOutputPortEdit(dataflow, dataflowOutputPort);
	}

	@Override
	public Edit<Activity<?>> getAddActivityInputPortEdit(Activity<?> activity,
			ActivityInputPort activityInputPort) {
		return new AddActivityInputPortEdit(activity, activityInputPort);
	}

	@Override
	public Edit<Activity<?>> getAddActivityInputPortMappingEdit(
			Activity<?> activity, String processorPortName,
			String activityPortName) {
		return new AddActivityInputPortMappingEdit(activity, processorPortName,
				activityPortName);
	}

	@Override
	public Edit<Activity<?>> getAddActivityOutputPortEdit(Activity<?> activity,
			ActivityOutputPort activityOutputPort) {
		return new AddActivityOutputPortEdit(activity, activityOutputPort);
	}

	@Override
	public Edit<Activity<?>> getAddActivityOutputPortMappingEdit(
			Activity<?> activity, String processorPortName,
			String activityPortName) {
		return new AddActivityOutputPortMappingEdit(activity,
				processorPortName, activityPortName);
	}

	@Override
	public Edit<Activity<?>> getRemoveActivityInputPortEdit(
			Activity<?> activity, ActivityInputPort activityInputPort) {
		return new RemoveActivityInputPortEdit(activity, activityInputPort);
	}

	@Override
	public Edit<Activity<?>> getRemoveActivityInputPortMappingEdit(
			Activity<?> activity, String processorPortName) {
		return new RemoveActivityInputPortMappingEdit(activity,
				processorPortName);
	}

	@Override
	public Edit<Activity<?>> getRemoveActivityOutputPortEdit(
			Activity<?> activity, ActivityOutputPort activityOutputPort) {
		return new RemoveActivityOutputPortEdit(activity, activityOutputPort);
	}

	@Override
	public Edit<Activity<?>> getRemoveActivityOutputPortMappingEdit(
			Activity<?> activity, String processorPortName) {
		return new RemoveActivityOutputPortMappingEdit(activity,
				processorPortName);
	}

	@Override
	public Edit<Merge> getAddMergeInputPortEdit(Merge merge,
			MergeInputPort mergeInputPort) {
		return new AddMergeInputPortEdit(merge, mergeInputPort);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> Edit<Activity<?>> getConfigureActivityEdit(Activity<T> activity,
			T configurationBean) {
		return new ConfigureActivityEdit(activity, configurationBean);
	}

	@Override
	public Edit<Processor> getRemoveProcessorInputPortEdit(Processor processor,
			ProcessorInputPort port) {
		return new RemoveProcessorInputPortEdit(processor, port);
	}

	@Override
	public Edit<Processor> getRemoveProcessorOutputPortEdit(
			Processor processor, ProcessorOutputPort port) {
		return new RemoveProcessorOutputPortEdit(processor, port);
	}

	@Override
	public Edit<Processor> getMapProcessorPortsForActivityEdit(
			Processor processor) {
		return new MapProcessorPortsForActivityEdit(processor);
	}

	@Override
	public Edit<Processor> getDefaultDispatchStackEdit(Processor processor) {
		return new DefaultDispatchStackEdit((ProcessorImpl) processor);
	}

	@Override
	public Edit<Processor> getSetIterationStrategyStackEdit(
			Processor processor, IterationStrategyStack iterationStrategyStack) {
		return new SetIterationStrategyStackEdit(processor,
				iterationStrategyStack);
	}

	@Override
	public <T> Edit<? extends Configurable<T>> getConfigureEdit(
			Configurable<T> configurable, T configBean) {
		return new ConfigureEdit<T>(Configurable.class, configurable,
				configBean);
	}

	@Override
	public Edit<Merge> getReorderMergeInputPortsEdit(Merge merge,
			List<MergeInputPort> reorderedMergeInputPortList) {
		return new ReorderMergeInputPortsEdit(merge, reorderedMergeInputPortList);
	}

	@Override
	public Edit<Processor> getRemoveActivityEdit(Processor processor,
			Activity<?> activity) {
		return new RemoveActivityEdit(processor, activity);
	}

	@Override
	public Edit<IterationStrategyStack> getAddIterationStrategyEdit(
			IterationStrategyStack iterationStrategyStack, IterationStrategy iterationStrategy) {
		return new AddIterationStrategyEdit(iterationStrategyStack, iterationStrategy);
	}

	@Override
	public Edit<IterationStrategy> getAddIterationStrategyInputNodeEdit(
			IterationStrategy iterationStrategy, NamedInputPortNode namedInputPortNode) {
		return new AddIterationStrategyInputPortEdit(iterationStrategy, namedInputPortNode);
	}

	@Override
	public Edit<IterationStrategyStack> getClearIterationStrategyStackEdit(
			IterationStrategyStack iterationStrategyStack) {
		return new ClearIterationStrategyStackEdit(iterationStrategyStack);
	}

}
