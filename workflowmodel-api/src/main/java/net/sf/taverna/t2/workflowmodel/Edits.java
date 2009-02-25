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
package net.sf.taverna.t2.workflowmodel;

import java.util.List;

import net.sf.taverna.t2.annotation.Annotated;
import net.sf.taverna.t2.annotation.AnnotationAssertion;
import net.sf.taverna.t2.annotation.AnnotationBeanSPI;
import net.sf.taverna.t2.annotation.AnnotationChain;
import net.sf.taverna.t2.annotation.AnnotationRole;
import net.sf.taverna.t2.annotation.AnnotationSourceSPI;
import net.sf.taverna.t2.annotation.CurationEvent;
import net.sf.taverna.t2.annotation.Person;
import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchStack;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategyStack;

/**
 * Defines the set of all available edit actions over a workflow model. This is
 * the only point at which you can modify any of the entities in the workflow
 * object model, the rest of this API is purely read only.
 * <p>
 * In theory this would be some kind of static interface but Java doesn't have
 * this as a concept so the pattern here will be to discover an appropriate
 * implementation of this interface from whatever version of the implementation
 * package you want to use, instantiate it then use the methods defined here to
 * construct and manipulate the workflow model.
 * 
 * @author Tom Oinn
 * @author Stuart Owen
 * @author David Withers
 * @author Stian Soiland-Reyes
 * 
 */
public interface Edits {

	/**
	 * Build a new Dataflow workflow
	 * 
	 * @return
	 */
	public Dataflow createDataflow();

	/**
	 * Builds a new DataflowInputPort.
	 * 
	 * @param name
	 * @param depth
	 * @param granularDepth
	 * @param dataflow
	 * @return a new DataflowInputPort
	 */
	public DataflowInputPort createDataflowInputPort(String name, int depth,
			int granularDepth, Dataflow dataflow);

	/**
	 * Builds a new DataflowOutputPort.
	 * 
	 * @param name
	 * @param dataflow
	 * @return a new DataflowOutputPort
	 */
	public DataflowOutputPort createDataflowOutputPort(String name,
			Dataflow dataflow);

	/**
	 * Builds a new Datalink with the given source and sink ports
	 * 
	 * @param source
	 *            the source port
	 * @param sink
	 *            the sink port
	 * @return a new Datalink instance
	 */
	public Datalink createDatalink(EventForwardingOutputPort source,
			EventHandlingInputPort sink);

	/**
	 * @param dataflow
	 * @return an instance of Merge
	 * 
	 * @see Merge
	 */
	public Merge createMerge(Dataflow dataflow);

	/**
	 * Builds a new MergeOutputPort.
	 * 
	 * @param merge
	 *            the merge that the port eill be added to
	 * @param name
	 *            the name of the port
	 * @param depth
	 *            the depth of the port
	 * @return a new MergeOutputPort
	 */
	public MergeInputPort createMergeInputPort(Merge merge, String name,
			int depth);

	/**
	 * Builds a new instance of a Processor with the given name. The processor
	 * is setup with a default dispatch stack.
	 * 
	 * @param the
	 *            local name for the processor.
	 */
	public Processor createProcessor(String name);

	/**
	 * Build a new WorkflowInstanceFacade using the supplied Dataflow
	 * 
	 * @param dataflow
	 * @param context
	 * @return an instance of a WorkflowInstanceFacade
	 * @throws InvalidDataflowException
	 *             if the workflow was not valid
	 * 
	 * @see WorkflowInstanceFacade
	 */
	public WorkflowInstanceFacade createWorkflowInstanceFacade(
			Dataflow dataflow, InvocationContext context, String parentProcess)
			throws InvalidDataflowException;

	/**
	 * Add an Activity implementation to the set of activities within a
	 * Processor
	 * 
	 * @param processor
	 *            Processor to add the activity to
	 * @param activity
	 *            Activity to add
	 */
	public Edit<Processor> getAddActivityEdit(Processor processor,
			Activity<?> activity);

	/**
	 * Returns an edit to add an ActivityInputPort to an Activity.
	 * 
	 * @param activity
	 *            activity to add the port to
	 * @param activityInputPort
	 *            the port to add to the activity
	 * @return an edit to add an ActivityInputPort to an Activity
	 */
	public Edit<Activity<?>> getAddActivityInputPortEdit(Activity<?> activity,
			ActivityInputPort activityInputPort);

	/**
	 * Returns an edit to add a ProcessorInputPort to ActivityInputPort mapping
	 * to an Activity.
	 * 
	 * @param activity
	 *            activity to add the port mapping to
	 * @param processorPortName
	 *            the name of the processor port
	 * @param activityPortName
	 *            the name of the activity port
	 * @return an edit to add a ProcessorInputPort to ActivityInputPort mapping
	 *         to an Activity
	 */
	public Edit<Activity<?>> getAddActivityInputPortMappingEdit(
			Activity<?> activity, String processorPortName,
			String activityPortName);

	/**
	 * Returns an edit to add an OutputPort to an Activity.
	 * 
	 * @param activity
	 *            activity to add the port to
	 * @param activityOutputPort
	 *            the port to add to the activity
	 * @return an edit to add an OutputPort to an Activity
	 */
	public Edit<Activity<?>> getAddActivityOutputPortEdit(Activity<?> activity,
			OutputPort activityOutputPort);

	/**
	 * Returns an edit to add a ProcessorOutputPort to OutputPort mapping to an
	 * Activity.
	 * 
	 * @param activity
	 *            activity to add the port mapping to
	 * @param processorPortName
	 *            the name of the processor port
	 * @param activityPortName
	 *            the name of the activity port
	 * @return an edit to add a ProcessorOutputPort to OutputPort mapping to an
	 *         Activity
	 */
	public Edit<Activity<?>> getAddActivityOutputPortMappingEdit(
			Activity<?> activity, String processorPortName,
			String activityPortName);

	/**
	 * Add an {@link AnnotationAssertion} to an {@link AnnotationChain}
	 * 
	 * @param annotationChain
	 * @param annotationAssertion
	 * @return an {@link Edit}able object with undo feature
	 */
	@SuppressWarnings("unchecked")
	public Edit<AnnotationChain> getAddAnnotationAssertionEdit(
			AnnotationChain annotationChain,
			AnnotationAssertion annotationAssertion);

	/**
	 * Add an {@link AnnotationBeanSPI} to an {@link AnnotationAssertion}
	 * 
	 * @param annotationAssertion
	 * @param annotationBean
	 * @return the edit which has do/undo functionality
	 */
	@SuppressWarnings("unchecked")
	public Edit<AnnotationAssertion> getAddAnnotationBean(
			AnnotationAssertion annotationAssertion,
			AnnotationBeanSPI annotationBean);

	/**
	 * Returnes an edit that creates an AnnotationAssertion, adds the
	 * AnnotationAssertion to an AnnotationChain and adds the AnnotationChain to
	 * the Annotated.
	 * 
	 * @param annotated
	 *            the Annotated to add an AnnotationChain to
	 * @param annotation
	 *            the annotation to add to the chain
	 * @return an edit that creates and adds an AnnotationChain to an Annotated
	 */
	public Edit<?> getAddAnnotationChainEdit(Annotated<?> annotated,
			AnnotationBeanSPI annotation);

	@SuppressWarnings("unchecked")
	public Edit<AnnotationAssertion> getAddAnnotationRole(
			AnnotationAssertion annotationAssertion,
			AnnotationRole annotationRole);

	@SuppressWarnings("unchecked")
	public Edit<AnnotationAssertion> getAddAnnotationSource(
			AnnotationAssertion annotationAssertion,
			AnnotationSourceSPI annotationSource);

	@SuppressWarnings("unchecked")
	public Edit<AnnotationAssertion> getAddCreator(
			AnnotationAssertion annotationAssertion, Person person);

	@SuppressWarnings("unchecked")
	public Edit<AnnotationAssertion> getAddCurationEvent(
			AnnotationAssertion annotationAssertion, CurationEvent curationEvent);

	/**
	 * Returns an edit to add a DataflowInputPort to a Dataflow.
	 * 
	 * @param dataflow
	 *            dataflow to add the port to
	 * @param dataflowInputPort
	 *            the port to add to the dataflow
	 * @return an edit to add a DataflowInputPort to a Dataflow
	 */
	public Edit<Dataflow> getAddDataflowInputPortEdit(Dataflow dataflow,
			DataflowInputPort dataflowInputPort);

	/**
	 * Returns an edit to add a DataflowOutputPort to a Dataflow.
	 * 
	 * @param dataflow
	 *            dataflow to add the port to
	 * @param dataflowOutputPort
	 *            the port to add to the dataflow
	 * @return an edit to add a DataflowOutputPort to a Dataflow
	 */
	public Edit<Dataflow> getAddDataflowOutputPortEdit(Dataflow dataflow,
			DataflowOutputPort dataflowOutputPort);

	/**
	 * Returns an edit to change the depth of a DataflowInputPort.
	 * 
	 * @param dataflowInputPort
	 *            the port to change the depth of
	 * @param depth
	 *            the new depth
	 * @return an edit to change the depth of a Dataflow
	 */
	public Edit<DataflowInputPort> getChangeDataflowInputPortDepthEdit(
			DataflowInputPort dataflowInputPort, int depth);

	/**
	 * Returns an edit to change the granular depth of a DataflowInputPort.
	 * 
	 * @param dataflowInputPort
	 *            the port to change the granular depth of
	 * @param granularDepth
	 *            the new granular depth
	 * @return an edit to change the granular depth of a Dataflow
	 */
	public Edit<DataflowInputPort> getChangeDataflowInputPortGranularDepthEdit(
			DataflowInputPort dataflowInputPort, int granularDepth);

	/**
	 * Add a new layer to the specified dispatch stack
	 * 
	 * @param stack
	 *            Stack to add to
	 * @param layer
	 *            New dispatch layer to add
	 * @param position
	 *            Where to add the new layer? 0 is at the top of the stack.
	 */
	public Edit<DispatchStack> getAddDispatchLayerEdit(DispatchStack stack,
			DispatchLayer<?> layer, int position);

	public Edit<Dataflow> getAddMergeEdit(Dataflow dataflow, Merge processor);

	/**
	 * Returns an edit to add a MergeInputPort to a Merge.
	 * 
	 * @param merge
	 *            merge to add the port to
	 * @param mergeInputPort
	 *            the port to add to the merge
	 * @return an edit to add a MergeInputPort to a Merge
	 */
	public Edit<Merge> getAddMergeInputPortEdit(Merge merge,
			MergeInputPort mergeInputPort);

	/**
	 * Provides an edit object responsible for adding a Processor to a Dataflow
	 * 
	 * @param dataflow
	 *            the dataflow to add this processor to
	 * @param processor
	 *            the processor to be added to the dataflow
	 */
	public Edit<Dataflow> getAddProcessorEdit(Dataflow dataflow,
			Processor processor);

	/**
	 * Provides an Edit to add an input port a processor, creating matching
	 * ports in the iteration strategy or strategies as a side effect.
	 * 
	 * @param processor
	 *            processor to add the port to
	 * 
	 * @param port
	 *            the input port to be added
	 */
	public Edit<Processor> getAddProcessorInputPortEdit(Processor processor,
			ProcessorInputPort port);

	/**
	 * Provides an Edit to add a new output port on a processor
	 * 
	 * @param processor
	 *            processor to add the new output port to
	 * 
	 * @param port
	 *            the port to be added
	 */
	public Edit<Processor> getAddProcessorOutputPortEdit(Processor processor,
			ProcessorOutputPort port);

	/**
	 * Returns an Edit that is responsible for configuring an Activity with a
	 * given configuration bean.
	 * 
	 * @see #getConfigureEdit(Configurable, Object)
	 * @param activity
	 * @param configurationBean
	 * @return
	 */
	public <ConfigurationBean> Edit<Activity<?>> getConfigureActivityEdit(
			Activity<ConfigurationBean> activity,
			ConfigurationBean configurationBean);

	/**
	 * Return an Edit that can configure a {@link Configurable} (such as an
	 * {@link Activity} or {@link DispatchLayer} with a given configuration
	 * bean.
	 * 
	 * @param <ConfigurationType>
	 * @param configurable
	 * @param configBean
	 * @return 
	 */
	public <ConfigurationType> Edit<? extends Configurable<ConfigurationType>> getConfigureEdit(
			Configurable<ConfigurationType> configurable,
			ConfigurationType configBean);
	

	/**
	 * Connect a datalink to its source and sink.
	 * 
	 * @param datalink
	 *            the datalink to connect
	 * @return a datalink edit
	 */
	public Edit<Datalink> getConnectDatalinkEdit(Datalink datalink);

	/**
	 * Creates and returns an instance of an Edit<Merge> that is responsible for
	 * generating the links to an from the Merge instance to link together the
	 * source and sink port via the merge instance.
	 * 
	 * @return a new instance of Edit<Merge> constructed from the provided
	 *         parameters.
	 * 
	 * @param merge
	 *            a Merge instance
	 * @param sourcePort
	 *            the source port from which a link is to be created.
	 * @param sinkPort
	 *            the sink port to which the link is to be created.
	 * 
	 * @see Merge
	 */
	public Edit<Merge> getConnectMergedDatalinkEdit(Merge merge,
			EventForwardingOutputPort sourcePort,
			EventHandlingInputPort sinkPort);

	/**
	 * Connect the output port of the specified processor to a target input
	 * port. To connect multiple inputs use this method multiple times with
	 * different targetPort arguments.
	 * 
	 * @param processor
	 *            Processor to link from
	 * @param outputPortName
	 *            Name of the output port within the specified processor to link
	 *            from
	 * @param targetPort
	 *            Input port (specifically an EventHandlingInputPort) to forward
	 *            data events to.
	 */
	public Edit<Processor> getConnectProcessorOutputEdit(Processor processor,
			String outputPortName, EventHandlingInputPort targetPort);

	/**
	 * Create a condition governing execution of the target processor. The
	 * target will not consume jobs from any inputs until all control processors
	 * linked through this edit have completed.
	 * 
	 * @param control
	 *            Processor controlling execution - this must complete before
	 *            the target can start.
	 * @param target
	 *            Processor controlled by this condition.
	 */
	public Edit<OrderedPair<Processor>> getCreateConditionEdit(
			Processor control, Processor target);

	/**
	 * Add an input port to a dataflow.
	 * 
	 * @param dataflow
	 *            dataflow to add the port to
	 * @param portName
	 *            name of the port, unique in the dataflow
	 * @param portDepth
	 *            the conceptual depth of collections consumed by this input
	 *            port
	 * @param granularDepth
	 *            granular depth to copy to the internal output port
	 */
	public Edit<Dataflow> getCreateDataflowInputPortEdit(Dataflow dataflow,
			String portName, int portDepth, int granularDepth);

	/**
	 * Add an output port to a dataflow.
	 * 
	 * @param dataflow
	 *            dataflow to add the port to
	 * @param portName
	 *            name of the port, unique in the dataflow
	 */
	public Edit<Dataflow> getCreateDataflowOutputPortEdit(Dataflow dataflow,
			String portName);

	/**
	 * Provides an edit that setup the default dispatch stack on a raw
	 * processor.
	 * 
	 * @param processor
	 * @return
	 */
	public Edit<Processor> getDefaultDispatchStackEdit(Processor processor);

	/**
	 * Remove a dispatch layer from its dispatch stack
	 * 
	 * @param stack
	 *            The stack from which to remove the layer
	 * @param layer
	 *            The layer to remove
	 */
	public Edit<DispatchStack> getDeleteDispatchLayerEdit(DispatchStack stack,
			DispatchLayer<?> layer);

	/**
	 * Disconnect a datalink from its source and sink.
	 * 
	 * @param datalink
	 *            the datalink to disconnect
	 * @return a datalink edit
	 */
	public Edit<Datalink> getDisconnectDatalinkEdit(Datalink datalink);

	/**
	 * Provides an edit that will configure the processors ports to map to those
	 * of its internal Activity. If there is more than 1 activity then only
	 * first activity is used. If there are zero then an EditException will be
	 * thrown when using the Edit.
	 * 
	 * @param processor
	 * @return
	 */
	public Edit<Processor> getMapProcessorPortsForActivityEdit(
			Processor processor);

	/**
	 * Returns an edit to remove an ActivityInputPort from an Activity.
	 * 
	 * @param activity
	 *            activity to remove the port from
	 * @param activityInputPort
	 *            the port to remove from the activity
	 * @return an edit to remove an ActivityInputPort from an Activity
	 */
	public Edit<Activity<?>> getRemoveActivityInputPortEdit(
			Activity<?> activity, ActivityInputPort activityInputPort);

	/**
	 * Returns an edit to remove a ProcessorInputPort to ActivityInputPort
	 * mapping from an Activity.
	 * 
	 * @param activity
	 *            activity to remove the port mapping from
	 * @param processorPortName
	 *            the name of the processor port to remove from the mapping
	 * @return an edit to remove a ProcessorInputPort to ActivityInputPort
	 *         mapping from an Activity
	 */
	public Edit<Activity<?>> getRemoveActivityInputPortMappingEdit(
			Activity<?> activity, String processorPortName);

	/**
	 * Returns an edit to remove an OutputPort from an Activity.
	 * 
	 * @param activity
	 *            activity to remove the port from
	 * @param activityOutputPort
	 *            the port to remove from the activity
	 * @return an edit to remove an OutputPort from an Activity
	 */
	public Edit<Activity<?>> getRemoveActivityOutputPortEdit(
			Activity<?> activity, OutputPort activityOutputPort);

	/**
	 * Returns an edit to remove a ProcessorOutputPort to OutputPort mapping
	 * from an Activity.
	 * 
	 * @param activity
	 *            activity to remove the port mapping from
	 * @param processorPortName
	 *            the name of the processor port to remove from the mapping
	 * @return an edit to remove a ProcessorOutputPort to OutputPort mapping
	 *         from an Activity
	 */
	public Edit<Activity<?>> getRemoveActivityOutputPortMappingEdit(
			Activity<?> activity, String processorPortName);

	/**
	 * Remove a condition previously applied to the specified pair of Processor
	 * instances
	 * 
	 * @param control
	 *            Processor controlling execution - this must complete before
	 *            the target can start.
	 * @param target
	 *            Processor controlled by this condition.
	 * @return
	 */
	public Edit<OrderedPair<Processor>> getRemoveConditionEdit(
			Processor control, Processor target);

	/**
	 * Returns an edit to remove a DataflowInputPort from a Dataflow.
	 * 
	 * @param dataflow
	 *            the Dataflow to remove this DataflowInputPort from
	 * @param dataflowInputPort
	 *            the DataflowInputPort to be removed from the Dataflow
	 */
	public Edit<Dataflow> getRemoveDataflowInputPortEdit(Dataflow dataflow,
			DataflowInputPort dataflowInputPort);

	/**
	 * Returns an edit to remove a DataflowOutputPort from a Dataflow.
	 * 
	 * @param dataflow
	 *            the Dataflow to remove this DataflowOutputPort from
	 * @param dataflowOutputPort
	 *            the DataflowOutputPort to be removed from the Dataflow
	 */
	public Edit<Dataflow> getRemoveDataflowOutputPortEdit(Dataflow dataflow,
			DataflowOutputPort dataflowOutputPort);

	/**
	 * Returns an edit to remove a Processor from a Dataflow.
	 * 
	 * @param dataflow
	 *            the dataflow to remove the processor from
	 * @param processor
	 *            the processor to be removed from the dataflow
	 */
	public Edit<Dataflow> getRemoveProcessorEdit(Dataflow dataflow,
			Processor processor);

	/**
	 * Removes a Processor input port.
	 * 
	 * @param processor
	 * @param port
	 * @return
	 */
	public Edit<Processor> getRemoveProcessorInputPortEdit(Processor processor,
			ProcessorInputPort port);

	/**
	 * @param processor
	 * @param port
	 * @return
	 */
	public Edit<Processor> getRemoveProcessorOutputPortEdit(
			Processor processor, ProcessorOutputPort port);

	/**
	 * Removes a merge from the dataflow.
	 * 
	 * @param dataflow
	 * @param processor
	 * @return
	 */
	public Edit<Dataflow> getRemoveMergeEdit(Dataflow dataflow, Merge merge);

	/**
	 * Rename a dataflow input port
	 * 
	 * @param dataflowInputPort
	 *            the dataflow input port to rename
	 * @param newName
	 *            the new name, must be unique within the workflow enclosing the
	 *            dataflow input port instance
	 */
	public Edit<DataflowInputPort> getRenameDataflowInputPortEdit(
			DataflowInputPort dataflowInputPort, String newName);

	/**
	 * Rename a dataflow output port
	 * 
	 * @param dataflowOutputPort
	 *            the dataflow output port to rename
	 * @param newName
	 *            the new name, must be unique within the workflow enclosing the
	 *            dataflow output port instance
	 */
	public Edit<DataflowOutputPort> getRenameDataflowOutputPortEdit(
			DataflowOutputPort dataflowOutputPort, String newName);

	/**
	 * Rename a processor
	 * 
	 * @param processor
	 *            the processor to rename
	 * @param newName
	 *            the new name, must be unique within the workflow enclosing the
	 *            processor instance
	 */
	public Edit<Processor> getRenameProcessorEdit(Processor processor,
			String newName);

	/**
	 * Provide an edit that will configure a processors's iteration strategy
	 * stack to the one provided.
	 * 
	 * @param processor
	 *            Processor which iteration stack is to be set
	 * @param iterationStrategyStack
	 *            The new iteration strategy stack
	 * @return An Edit that will set the iteration strategy stack of a processor
	 */
	public Edit<Processor> getSetIterationStrategyStackEdit(
			Processor processor, IterationStrategyStack iterationStrategyStack);

	public Edit<Dataflow> getUpdateDataflowInternalIdentifierEdit(
			Dataflow dataflow, String newId);

	public Edit<Dataflow> getUpdateDataflowNameEdit(Dataflow dataflow,
			String newName);

	/**
	 * Builds an instance of an {@link InputPort} for an Activity.
	 * 
	 * @param portName
	 * @param portDepth
	 * @param allowsLiteralValues
	 *            whether the input port can cope with literal values
	 * @param handledReferenceSchemes
	 *            a list of the reference scheme types that can be legitimately
	 *            pushed into this input port
	 * @param translatedElementClass
	 *            the class desired as result (or elements of collections of
	 *            results) when interpreted by the data facade
	 * @return an instance of InputPort
	 */
	ActivityInputPort createActivityInputPort(
			String portName,
			int portDepth,
			boolean allowsLiteralValues,
			List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes,
			Class<?> translatedElementClass);

	/**
	 * Builds an instance of an {@link OutputPort} for an Activity.
	 * 
	 * @param portName
	 * @param portDepth
	 * @param portGranularDepth
	 * @return an instance of OutputPort
	 */
	OutputPort createActivityOutputPort(String portName, int portDepth,
			int portGranularDepth);

	/**
	 * Creates a new ProcessorInputPort
	 * 
	 * @param processor
	 *            the processor to with the port will be added
	 * @param name
	 * @param depth
	 * @return
	 */
	ProcessorInputPort createProcessorInputPort(Processor processor,
			String name, int depth);

	/**
	 * Creates a new ProcessorOutputPort
	 * 
	 * @param processor
	 * @param name
	 * @param depth
	 * @param granularDepth
	 * @return
	 */
	ProcessorOutputPort createProcessorOutputPort(Processor processor,
			String name, int depth, int granularDepth);

}
