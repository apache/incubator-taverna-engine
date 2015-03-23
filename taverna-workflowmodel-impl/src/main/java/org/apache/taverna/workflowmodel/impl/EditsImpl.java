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

import static java.lang.Integer.parseInt;
import static org.apache.taverna.workflowmodel.utils.Tools.getUniqueMergeInputPortName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.annotation.Annotated;
import org.apache.taverna.annotation.AnnotationAssertion;
import org.apache.taverna.annotation.AnnotationBeanSPI;
import org.apache.taverna.annotation.AnnotationChain;
import org.apache.taverna.annotation.AnnotationRole;
import org.apache.taverna.annotation.AnnotationSourceSPI;
import org.apache.taverna.annotation.CurationEvent;
import org.apache.taverna.annotation.Person;
import org.apache.taverna.annotation.impl.AnnotationAssertionImpl;
import org.apache.taverna.annotation.impl.AnnotationChainImpl;
import org.apache.taverna.facade.WorkflowInstanceFacade;
import org.apache.taverna.facade.impl.WorkflowInstanceFacadeImpl;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.workflowmodel.CompoundEdit;
import org.apache.taverna.workflowmodel.Condition;
import org.apache.taverna.workflowmodel.Configurable;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.EventForwardingOutputPort;
import org.apache.taverna.workflowmodel.EventHandlingInputPort;
import org.apache.taverna.workflowmodel.InvalidDataflowException;
import org.apache.taverna.workflowmodel.Merge;
import org.apache.taverna.workflowmodel.MergeInputPort;
import org.apache.taverna.workflowmodel.OrderedPair;
import org.apache.taverna.workflowmodel.OutputPort;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.ProcessorInputPort;
import org.apache.taverna.workflowmodel.ProcessorOutputPort;
import org.apache.taverna.workflowmodel.processor.activity.AbstractActivity;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityInputPort;
import org.apache.taverna.workflowmodel.processor.activity.ActivityOutputPort;
import org.apache.taverna.workflowmodel.processor.activity.impl.ActivityInputPortImpl;
import org.apache.taverna.workflowmodel.processor.activity.impl.ActivityOutputPortImpl;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchStack;
import org.apache.taverna.workflowmodel.processor.dispatch.impl.AbstractDispatchLayerEdit;
import org.apache.taverna.workflowmodel.processor.dispatch.impl.DispatchStackImpl;
import org.apache.taverna.workflowmodel.processor.dispatch.layers.ErrorBounce;
import org.apache.taverna.workflowmodel.processor.dispatch.layers.Failover;
import org.apache.taverna.workflowmodel.processor.dispatch.layers.Invoke;
import org.apache.taverna.workflowmodel.processor.dispatch.layers.Parallelize;
import org.apache.taverna.workflowmodel.processor.dispatch.layers.Retry;
import org.apache.taverna.workflowmodel.processor.dispatch.layers.Stop;
import org.apache.taverna.workflowmodel.processor.iteration.IterationStrategy;
import org.apache.taverna.workflowmodel.processor.iteration.IterationStrategyStack;
import org.apache.taverna.workflowmodel.processor.iteration.NamedInputPortNode;
import org.apache.taverna.workflowmodel.processor.iteration.impl.IterationStrategyImpl;
import org.apache.taverna.workflowmodel.processor.iteration.impl.IterationStrategyStackImpl;

/**
 * Implementation of {@link Edits}
 * @author Donal Fellows (et al)
 */
public class EditsImpl implements Edits {
	// ----------------------------------------------------------------
	// Basic factory methods

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
		if (merge instanceof MergeImpl)
			return new MergeInputPortImpl((MergeImpl) merge, name, depth);
		return null;
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
	public AnnotationChain createAnnotationChain() {
		return new AnnotationChainImpl();
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

		// Work out what the name of the merge should be.
		List<? extends Merge> merges = dataflow.getMerges();
		if (merges.isEmpty())
			mergeName = "Merge0"; // the first merge to be added to the list
		else
			mergeName = "Merge"
					+ String.valueOf(parseInt(merges.get(merges.size() - 1)
							.getLocalName().substring(5)) + 1);

		return new MergeImpl(mergeName);
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

	// ----------------------------------------------------------------
	// Edits (structured transformation) factory methods

	@Override
	public Edit<Dataflow> getAddProcessorEdit(Dataflow dataflow,
			Processor processor) {
		if (!(processor instanceof ProcessorImpl))
			throw new RuntimeException(
					"The Processor is of the wrong implmentation,"
							+ " it should be of type ProcessorImpl");
		final ProcessorImpl p = (ProcessorImpl) processor;
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow)
					throws EditException {
				dataflow.addProcessor(p);
			}
		};
	}

	@Override
	public Edit<Dataflow> getAddMergeEdit(Dataflow dataflow, Merge merge) {
		if (!(merge instanceof MergeImpl))
			throw new RuntimeException(
					"The Merge is of the wrong implmentation, "
							+ "it should be of type MergeImpl");
		final MergeImpl m = (MergeImpl) merge;
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow)
					throws EditException {
				dataflow.addMerge(m);
			}
		};
	}

	@Override
	public Edit<DispatchStack> getAddDispatchLayerEdit(DispatchStack stack,
			final DispatchLayer<?> layer, final int position) {
		return new AbstractDispatchLayerEdit(stack) {
			@Override
			protected void doEditAction(DispatchStackImpl stack) throws EditException {
				stack.addLayer(layer, position);
			}

			@Override
			protected void undoEditAction(DispatchStackImpl stack) {
				stack.removeLayer(layer);
			}
		};
	}

	@Override
	public Edit<Processor> getAddActivityEdit(Processor processor,
			final Activity<?> activity) {
		return new AbstractProcessorEdit(processor) {
			@Override
			protected void doEditAction(ProcessorImpl processor)
					throws EditException {
				List<Activity<?>> activities = processor.activityList;
				if (activities.contains(activity))
					throw new EditException(
							"Cannot add a duplicate activity to processor");
				activities.add(activity);
			}
		};
	}

	@Override
	public Edit<Processor> getAddProcessorInputPortEdit(Processor processor,
			final ProcessorInputPort port) {
		return new AbstractProcessorEdit(processor) {
			@Override
			protected void doEditAction(ProcessorImpl processor)
					throws EditException {
				/*
				 * Add a new InputPort object to the processor and also create
				 * an appropriate NamedInputPortNode in any iteration
				 * strategies. By default set the desired drill depth on each
				 * iteration strategy node to the same as the input port, so
				 * this won't automatically trigger iteration staging unless the
				 * depth is altered on the iteration strategy itself.)
				 */
				if (processor.getInputPortWithName(port.getName()) != null)
					throw new EditException(
							"Attempt to create duplicate input port with name '"
									+ port.getName() + "'");
				processor.inputPorts.add((ProcessorInputPortImpl) port);
				for (IterationStrategyImpl is : processor.iterationStack
						.getStrategies()) {
					NamedInputPortNode nipn = new NamedInputPortNode(
							port.getName(), port.getDepth());
					is.addInput(nipn);
					is.connectDefault(nipn);
				}
			}
		};
	}

	@Override
	public Edit<Processor> getAddProcessorOutputPortEdit(Processor processor,
			final ProcessorOutputPort port) {
		return new AbstractProcessorEdit(processor) {
			@Override
			protected void doEditAction(ProcessorImpl processor)
					throws EditException {
				if (processor.getOutputPortWithName(port.getName()) != null)
					throw new EditException("Duplicate output port name");
				processor.outputPorts.add((ProcessorOutputPortImpl) port);
			}
		};
	}

	@Override
	public Edit<Dataflow> getCreateDataflowInputPortEdit(Dataflow dataflow,
			final String portName, final int portDepth, final int granularDepth) {
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow)
					throws EditException {
				dataflow.createInputPort(portName, portDepth, granularDepth);
			}
		};
	}

	@Override
	public Edit<Dataflow> getCreateDataflowOutputPortEdit(Dataflow dataflow,
			final String portName) {
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow)
					throws EditException {
				dataflow.createOutputPort(portName);
			}
		};
	}

	@Override
	public Edit<DispatchStack> getDeleteDispatchLayerEdit(DispatchStack stack,
			final DispatchLayer<?> layer) {
		return new AbstractDispatchLayerEdit(stack) {
			private int index;

			@Override
			protected void doEditAction(DispatchStackImpl stack) {
				index = stack.removeLayer(layer);
			}

			@Override
			protected void undoEditAction(DispatchStackImpl stack) {
				stack.addLayer(layer, index);
			}
		};
	}

	@Override
	public Edit<Merge> getRenameMergeEdit(Merge merge, final String newName) {
		return new AbstractMergeEdit(merge) {
			@Override
			protected void doEditAction(MergeImpl merge) {
				merge.setName(newName);
			}
		};
	}

	@Override
	public Edit<Processor> getRenameProcessorEdit(Processor processor,
			final String newName) {
		return new AbstractProcessorEdit(processor) {
			@Override
			protected void doEditAction(ProcessorImpl processor) {
				processor.setName(newName);
			}
		};
	}

	@Override
	public Edit<DataflowInputPort> getRenameDataflowInputPortEdit(
			DataflowInputPort dataflowInputPort, final String newName) {
		return new AbstractDataflowInputPortEdit(dataflowInputPort) {
			@Override
			protected void doEditAction(DataflowInputPortImpl inputPort) {
				inputPort.setName(newName);
			}
		};
	}

	@Override
	public Edit<DataflowOutputPort> getRenameDataflowOutputPortEdit(
			DataflowOutputPort dataflowOutputPort, final String newName) {
		return new AbstractDataflowOutputPortEdit(dataflowOutputPort) {
			@Override
			protected void doEditAction(DataflowOutputPortImpl outputPort) {
				outputPort.setName(newName);
			}
		};
	}

	@Override
	public Edit<DataflowInputPort> getChangeDataflowInputPortDepthEdit(
			DataflowInputPort dataflowInputPort, final int depth) {
		return new AbstractDataflowInputPortEdit(dataflowInputPort) {
			@Override
			protected void doEditAction(DataflowInputPortImpl port) {
				port.setDepth(depth);
			}
		};
	}

	@Override
	public Edit<DataflowInputPort> getChangeDataflowInputPortGranularDepthEdit(
			DataflowInputPort dataflowInputPort, final int granularDepth) {
		return new AbstractDataflowInputPortEdit(dataflowInputPort) {
			@Override
			protected void doEditAction(DataflowInputPortImpl port) {
				port.setGranularDepth(granularDepth);
			}
		};
	}

	@Override
	public Edit<Processor> getConnectProcessorOutputEdit(Processor processor,
			final String outputPortName, final EventHandlingInputPort targetPort) {
		return new AbstractProcessorEdit(processor) {
			@Override
			protected void doEditAction(ProcessorImpl processor) throws EditException {
				for (BasicEventForwardingOutputPort popi : processor.outputPorts)
					if (popi.getName().equals(outputPortName)) {
						addOutgoingLink(popi);
						return;
					}
				throw new EditException("Cannot locate output port with name '"
						+ outputPortName + "'");
			}

			private void addOutgoingLink(BasicEventForwardingOutputPort popi) {
				DatalinkImpl newLink = new DatalinkImpl(popi, targetPort);
				popi.addOutgoingLink(newLink);
				if (targetPort instanceof AbstractEventHandlingInputPort)
					((AbstractEventHandlingInputPort) targetPort)
							.setIncomingLink(newLink);
			}
		};
	}

	@Override
	public Edit<Datalink> getConnectDatalinkEdit(Datalink datalink) {
		return new AbstractDatalinkEdit(datalink) {
			@Override
			protected void doEditAction(DatalinkImpl datalink) throws EditException {
				EventForwardingOutputPort source = datalink.getSource();
				EventHandlingInputPort sink = datalink.getSink();
				if (source instanceof BasicEventForwardingOutputPort)
					((BasicEventForwardingOutputPort) source).addOutgoingLink(datalink);
				if (sink instanceof AbstractEventHandlingInputPort)
					((AbstractEventHandlingInputPort) sink).setIncomingLink(datalink);
			}
		};
	}

	@Override
	public Edit<AnnotationChain> getAddAnnotationAssertionEdit(
			AnnotationChain annotationChain,
			final AnnotationAssertion<?> annotationAssertion) {
		if (!(annotationChain instanceof AnnotationChainImpl))
			throw new RuntimeException(
					"Object being edited must be instance of AnnotationChainImpl");
		final AnnotationChainImpl chain = (AnnotationChainImpl) annotationChain;
		return new EditSupport<AnnotationChain>() {
			@Override
			public AnnotationChain applyEdit() {
				synchronized (chain) {
					chain.addAnnotationAssertion(annotationAssertion);
				}
				return chain;
			}

			@Override
			public Object getSubject() {
				return chain;
			}
		};
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
			final EventForwardingOutputPort sourcePort,
			final EventHandlingInputPort sinkPort) {
		if (sourcePort == null)
			throw new RuntimeException("The sourceport cannot be null");
		if (sinkPort == null)
			throw new RuntimeException("The sinkport cannot be null");
		return new AbstractMergeEdit(merge) {
			private boolean needToCreateDatalink(MergeImpl mergeImpl)
					throws EditException {
				Collection<? extends Datalink> outgoing = mergeImpl.getOutputPort()
						.getOutgoingLinks();
				if (outgoing.size() == 0) {
					return true;
				} else if (outgoing.size() != 1)
					throw new EditException(
							"The merge instance cannot have more that 1 outgoing Datalink");
				if (outgoing.iterator().next().getSink() != sinkPort)
					throw new EditException(
							"Cannot add a different sinkPort to a Merge that already has one defined");
				return false;
			}

			@Override
			protected void doEditAction(MergeImpl merge) throws EditException {
				boolean linkOut = needToCreateDatalink(merge);
				String name = getUniqueMergeInputPortName(merge,
						sourcePort.getName() + "To" + merge.getLocalName()
								+ "_input", 0);
				MergeInputPortImpl mergeInputPort = new MergeInputPortImpl(
						merge, name, sinkPort.getDepth());
				merge.addInputPort(mergeInputPort);
				getConnectDatalinkEdit(
						createDatalink(sourcePort, mergeInputPort)).doEdit();
				if (linkOut)
					getConnectDatalinkEdit(
							createDatalink(merge.getOutputPort(), sinkPort))
							.doEdit();
			}
		};
	}

	@Override
	public Edit<OrderedPair<Processor>> getCreateConditionEdit(
			Processor control, Processor target) {
		return new AbstractBinaryProcessorEdit(control, target) {
			@Override
			protected void doEditAction(ProcessorImpl control,
					ProcessorImpl target) throws EditException {
				ConditionImpl condition = new ConditionImpl(control, target);
				// Check for duplicates
				for (Condition c : control.controlledConditions)
					if (c.getTarget() == target)
						throw new EditException(
								"Attempt to create duplicate control link");
				control.controlledConditions.add(condition);
				target.conditions.add(condition);
			}
		};
	}

	@Override
	public Edit<OrderedPair<Processor>> getRemoveConditionEdit(
			Processor control, Processor target) {
		return new AbstractBinaryProcessorEdit(control, target) {
			@Override
			protected void doEditAction(ProcessorImpl control, ProcessorImpl target)
					throws EditException {
				for (ConditionImpl c : control.controlledConditions)
					if (c.getTarget() == target) {
						control.controlledConditions.remove(c);
						target.conditions.remove(c);
						return;
					}
				throw new EditException(
						"Can't remove a control link as it doesn't exist");
			}
		};
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Edit<AnnotationAssertion<AnnotationBeanSPI>> getAddAnnotationBean(
			AnnotationAssertion annotationAssertion,
			final AnnotationBeanSPI bean) {
		return new AbstractAnnotationEdit(annotationAssertion) {
			@Override
			protected void doEditAction(AnnotationAssertionImpl assertion)
					throws EditException {
				assertion.setAnnotationBean(bean);
			}
		};
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Edit<AnnotationAssertion<AnnotationBeanSPI>> getAddCurationEvent(
			AnnotationAssertion annotationAssertion,
			final CurationEvent curationEvent) {
		return new AbstractAnnotationEdit(annotationAssertion) {
			@Override
			protected void doEditAction(AnnotationAssertionImpl assertion)
					throws EditException {
				assertion.addCurationEvent(curationEvent);
			}
		};
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Edit<AnnotationAssertion<AnnotationBeanSPI>> getAddAnnotationRole(
			AnnotationAssertion annotationAssertion,
			final AnnotationRole role) {
		return new AbstractAnnotationEdit(annotationAssertion) {
			@Override
			protected void doEditAction(AnnotationAssertionImpl assertion)
					throws EditException {
				assertion.setAnnotationRole(role);
			}
		};
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Edit<AnnotationAssertion<AnnotationBeanSPI>> getAddAnnotationSource(
			AnnotationAssertion annotationAssertion,
			final AnnotationSourceSPI source) {
		return new AbstractAnnotationEdit(annotationAssertion) {
			@Override
			protected void doEditAction(AnnotationAssertionImpl assertion)
					throws EditException {
				assertion.setAnnotationSource(source);
			}
		};
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Edit<AnnotationAssertion<AnnotationBeanSPI>> getAddCreator(
			AnnotationAssertion annotationAssertion, final Person person) {
		return new AbstractAnnotationEdit(annotationAssertion) {
			@Override
			protected void doEditAction(AnnotationAssertionImpl assertion)
					throws EditException {
				assertion.addCreator(person);
			}
		};
	}

	@Override
	public Edit<?> getAddAnnotationChainEdit(Annotated<?> annotated,
			AnnotationBeanSPI annotation) {
		List<Edit<?>> editList = new ArrayList<>();

		AnnotationAssertion<?> assertion = new AnnotationAssertionImpl();
		AnnotationChain chain = new AnnotationChainImpl();
		editList.add(getAddAnnotationBean(assertion, annotation));
		editList.add(getAddAnnotationAssertionEdit(chain, assertion));
		editList.add(annotated.getAddAnnotationEdit(chain));

		return new CompoundEdit(editList);
	}

	@Override
	public Edit<Dataflow> getUpdateDataflowNameEdit(Dataflow dataflow,
			final String newName) {
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow) {
				dataflow.setLocalName(newName);
			}
		};
	}

	@Override
	public Edit<Dataflow> getUpdateDataflowInternalIdentifierEdit(
			Dataflow dataflow, final String newId) {
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow) {
				dataflow.setIdentifier(newId);
			}
		};
	}

	@Override
	public Edit<Datalink> getDisconnectDatalinkEdit(Datalink datalink) {
		return new AbstractDatalinkEdit(datalink) {
			@Override
			protected void doEditAction(DatalinkImpl datalink) throws EditException {
				EventForwardingOutputPort source = datalink.getSource();
				EventHandlingInputPort sink = datalink.getSink();

				if (source instanceof BasicEventForwardingOutputPort)
					((BasicEventForwardingOutputPort) source)
							.removeOutgoingLink(datalink);

				if (sink instanceof AbstractEventHandlingInputPort)
					((AbstractEventHandlingInputPort) sink).setIncomingLink(null);
				if (sink instanceof MergeInputPortImpl) {
					MergeInputPortImpl mip = (MergeInputPortImpl) sink;
					((MergeImpl) mip.getMerge()).removeInputPort(mip);
				}
			}
		};
	}

	@Override
	public Edit<Dataflow> getRemoveDataflowInputPortEdit(Dataflow dataflow,
			final DataflowInputPort dataflowInputPort) {
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow) throws EditException {
				dataflow.removeDataflowInputPort(dataflowInputPort);
			}
		};
	}

	@Override
	public Edit<Dataflow> getRemoveDataflowOutputPortEdit(Dataflow dataflow,
			final DataflowOutputPort dataflowOutputPort) {
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow) throws EditException {
				dataflow.removeDataflowOutputPort(dataflowOutputPort);
			}
		};
	}

	@Override
	public Edit<Dataflow> getRemoveProcessorEdit(Dataflow dataflow,
			final Processor processor) {
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow) {
				dataflow.removeProcessor(processor);
			}
		};
	}

	@Override
	public Edit<Dataflow> getRemoveMergeEdit(Dataflow dataflow, final Merge merge) {
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow) {
				dataflow.removeMerge(merge);
			}
		};
	}

	@Override
	public Edit<Dataflow> getAddDataflowInputPortEdit(Dataflow dataflow,
			DataflowInputPort dataflowInputPort) {
		if (!(dataflowInputPort instanceof DataflowInputPortImpl))
			throw new RuntimeException(
					"The DataflowInputPort is of the wrong implmentation, "
							+ "it should be of type DataflowInputPortImpl");
		final DataflowInputPortImpl port = (DataflowInputPortImpl) dataflowInputPort;
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow)
					throws EditException {
				dataflow.addInputPort(port);
			}
		};
	}

	@Override
	public Edit<Dataflow> getAddDataflowOutputPortEdit(Dataflow dataflow,
			final DataflowOutputPort dataflowOutputPort) {
		if (!(dataflowOutputPort instanceof DataflowOutputPortImpl))
			throw new RuntimeException(
					"The DataflowOutputPort is of the wrong implmentation, "
							+ "it should be of type DataflowOutputPortImpl");
		return new AbstractDataflowEdit(dataflow) {
			@Override
			protected void doEditAction(DataflowImpl dataflow)
					throws EditException {
				dataflow.addOutputPort((DataflowOutputPortImpl) dataflowOutputPort);
			}
		};
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Edit<Activity<?>> getAddActivityInputPortEdit(Activity<?> activity,
			final ActivityInputPort activityInputPort) {
		return new AbstractActivityEdit(activity) {
			@Override
			protected void doEditAction(AbstractActivity activity) {
				activity.getInputPorts().add(activityInputPort);
			}
		};
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Edit<Activity<?>> getAddActivityInputPortMappingEdit(
			Activity<?> activity, final String processorPortName,
			final String activityPortName) {
		return new AbstractActivityEdit(activity) {
			@Override
			protected void doEditAction(AbstractActivity activity)
					throws EditException {
				if (activity.getInputPortMapping().containsKey(
						processorPortName))
					throw new EditException(
							"The output mapping for processor name:"
									+ processorPortName + " already exists");
				/*
				 * Note javadoc of getOutputPortMapping - the mapping is
				 * processorPort -> activityPort -- opposite of the
				 * outputPortMapping
				 */
				activity.getInputPortMapping().put(processorPortName,
						activityPortName);
			}
		};
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Edit<Activity<?>> getAddActivityOutputPortEdit(Activity<?> activity,
			final ActivityOutputPort activityOutputPort) {
		return new AbstractActivityEdit(activity) {
			@Override
			protected void doEditAction(AbstractActivity activity) {
				activity.getOutputPorts().add(activityOutputPort);
			}
		};
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Edit<Activity<?>> getAddActivityOutputPortMappingEdit(
			Activity<?> activity, final String processorPortName,
			final String activityPortName) {
		return new AbstractActivityEdit(activity) {
			@Override
			protected void doEditAction(AbstractActivity activity)
					throws EditException {
				Map<String, String> opm = activity.getOutputPortMapping();
				if (opm.containsKey(activityPortName))
					throw new EditException("The mapping starting with:"
							+ activityPortName + " already exists");
				/*
				 * Note javadoc of getOutputPortMapping - the mapping is
				 * activityPort -> processorPort -- opposite of the
				 * outputPortMapping
				 */
				opm.put(activityPortName, processorPortName);
			}
		};
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Edit<Activity<?>> getRemoveActivityInputPortEdit(
			Activity<?> activity, final ActivityInputPort activityInputPort) {
		return new AbstractActivityEdit(activity) {
			@Override
			protected void doEditAction(AbstractActivity activity)
					throws EditException {
				activity.getInputPorts().remove(activityInputPort);
			}
		};
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Edit<Activity<?>> getRemoveActivityInputPortMappingEdit(
			Activity<?> activity, final String processorPortName) {
		return new AbstractActivityEdit(activity) {
			@Override
			protected void doEditAction(AbstractActivity activity)
					throws EditException {
				if (!activity.getInputPortMapping().containsKey(processorPortName))
					throw new EditException(
							"The input port mapping for the processor port name:"
									+ processorPortName + " doesn't exist");
				activity.getInputPortMapping().remove(processorPortName);
			}
		};
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Edit<Activity<?>> getRemoveActivityOutputPortEdit(
			Activity<?> activity, final ActivityOutputPort activityOutputPort) {
		return new AbstractActivityEdit(activity) {
			@Override
			protected void doEditAction(AbstractActivity activity)
					throws EditException {
				activity.getOutputPorts().remove(activityOutputPort);
			}
		};
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Edit<Activity<?>> getRemoveActivityOutputPortMappingEdit(
			Activity<?> activity, final String processorPortName) {
		return new AbstractActivityEdit(activity) {
			@Override
			protected void doEditAction(AbstractActivity activity)
					throws EditException {
				if (!activity.getOutputPortMapping().containsKey(processorPortName))
					throw new EditException(
							"The output port mapping for the processor port name:"
									+ processorPortName + " doesn't exist");
				activity.getOutputPortMapping().remove(processorPortName);
			}
		};
	}

	@Override
	public Edit<Merge> getAddMergeInputPortEdit(Merge merge,
			MergeInputPort mergeInputPort) {
		if (!(mergeInputPort instanceof MergeInputPortImpl))
			throw new RuntimeException(
					"The MergeInputPort is of the wrong implmentation,"
							+ " it should be of type MergeInputPortImpl");
		final MergeInputPortImpl port = (MergeInputPortImpl) mergeInputPort;
		return new AbstractMergeEdit(merge) {
			@Override
			protected void doEditAction(MergeImpl mergeImpl) {
				mergeImpl.addInputPort(port);
			}
		};
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> Edit<Activity<?>> getConfigureActivityEdit(Activity<T> activity,
			T configurationBean) {
		return new ConfigureEdit(Activity.class, activity, configurationBean);
	}

	@Override
	public Edit<Processor> getRemoveProcessorInputPortEdit(Processor processor,
			final ProcessorInputPort port) {
		return new AbstractProcessorEdit(processor) {
			@Override
			protected void doEditAction(ProcessorImpl processor) throws EditException {
				if (processor.getInputPortWithName(port.getName()) == null)
					throw new EditException("The processor doesn't have a port named:"
							+ port.getName());
				for (IterationStrategyImpl is : processor.iterationStack
						.getStrategies())
					is.removeInputByName(port.getName());
				processor.inputPorts.remove(port);
			}
		};
	}

	@Override
	public Edit<Processor> getRemoveProcessorOutputPortEdit(
			Processor processor, final ProcessorOutputPort port) {
		return new AbstractProcessorEdit(processor) {
			@Override
			protected void doEditAction(ProcessorImpl processor) throws EditException {
				if (processor.getOutputPortWithName(port.getName()) == null)
					throw new EditException("The processor doesn't have a port named:"
							+ port.getName());
				processor.outputPorts.remove(port);
			}
		};
	}

	@Override
	public Edit<Processor> getMapProcessorPortsForActivityEdit(
			final Processor processor) {
		return new EditSupport<Processor>() {
			@Override
			public Processor applyEdit() throws EditException {
				List<Edit<?>> edits = new ArrayList<>();
				Activity<?> a = processor.getActivityList().get(0);

				List<ProcessorInputPort> inputPortsForRemoval = determineInputPortsForRemoval(
						processor, a);
				List<ProcessorOutputPort> outputPortsForRemoval = determineOutputPortsForRemoval(
						processor, a);
				Map<ProcessorInputPort, ActivityInputPort> changedInputPorts = determineChangedInputPorts(
						processor, a);
				Map<ProcessorOutputPort, ActivityOutputPort> changedOutputPorts = determineChangedOutputPorts(
						processor, a);

				for (ProcessorInputPort ip : inputPortsForRemoval) {
					if (ip.getIncomingLink() != null)
						edits.add(getDisconnectDatalinkEdit(ip
								.getIncomingLink()));
					edits.add(getRemoveProcessorInputPortEdit(processor, ip));
					if (a.getInputPortMapping().containsKey(ip.getName()))
						edits.add(getRemoveActivityInputPortMappingEdit(a, ip
								.getName()));
				}
				
				for (ProcessorOutputPort op : outputPortsForRemoval) {
					if (!op.getOutgoingLinks().isEmpty())
						for (Datalink link : op.getOutgoingLinks())
							edits.add(getDisconnectDatalinkEdit(link));
					edits.add(getRemoveProcessorOutputPortEdit(processor, op));
					if (a.getOutputPortMapping().containsKey(op.getName()))
						edits.add(getRemoveActivityOutputPortMappingEdit(a, op
								.getName()));
				}

				for (ProcessorInputPort ip : changedInputPorts.keySet()) {
					Datalink incomingLink = ip.getIncomingLink();
					if (incomingLink != null)
						edits.add(getDisconnectDatalinkEdit(incomingLink));
					edits.add(getRemoveProcessorInputPortEdit(processor, ip));
					
					if (incomingLink != null) {
						ActivityInputPort aip = changedInputPorts.get(ip);
						ProcessorInputPort pip = createProcessorInputPort(processor,
								aip.getName(), aip.getDepth());
						edits.add(getAddProcessorInputPortEdit(processor, pip));
						edits.add(getConnectDatalinkEdit(new DatalinkImpl(
								incomingLink.getSource(), pip)));
					}
				}
				
				for (ProcessorOutputPort op : changedOutputPorts.keySet()) {
					Set<? extends Datalink> outgoingLinks = op.getOutgoingLinks();
					for (Datalink link : outgoingLinks)
						edits.add(getDisconnectDatalinkEdit(link));
					edits.add(getRemoveProcessorOutputPortEdit(processor, op));
					
					if (!outgoingLinks.isEmpty()) {
						ActivityOutputPort aop = changedOutputPorts.get(op);
						ProcessorOutputPort pop = createProcessorOutputPort(
								processor, aop.getName(), aop.getDepth(),
								aop.getGranularDepth());
						edits.add(getAddProcessorOutputPortEdit(processor,
								pop));
						for (Datalink link : outgoingLinks)
							edits.add(getConnectDatalinkEdit(createDatalink(
									pop, link.getSink())));
					}
				}

				new CompoundEdit(edits).doEdit();
				return processor;
			}

			@Override
			public Object getSubject() {
				return processor;
			}

			private List<ProcessorInputPort> determineInputPortsForRemoval(Processor p,
					Activity<?> a) {
				List<ProcessorInputPort> result = new ArrayList<>();
				processorPorts: for (ProcessorInputPort pPort : p.getInputPorts()) {
					for (ActivityInputPort aPort : a.getInputPorts())
						if (aPort.getName().equals(pPort.getName()))
							continue processorPorts;
					result.add(pPort);
				}
				return result;
			}
			
			private List<ProcessorOutputPort> determineOutputPortsForRemoval(
					Processor p, Activity<?> a) {
				List<ProcessorOutputPort> result = new ArrayList<>();
				processorPorts: for (ProcessorOutputPort pPort : p.getOutputPorts()) {
					for (OutputPort aPort : a.getOutputPorts())
						if (aPort.getName().equals(pPort.getName()))
							continue processorPorts;
					result.add(pPort);
				}
				return result;
			}
			
			private Map<ProcessorInputPort, ActivityInputPort> determineChangedInputPorts(
					Processor p, Activity<?> a) {
				Map<ProcessorInputPort, ActivityInputPort> result = new HashMap<>();
				for (ProcessorInputPort pPort : p.getInputPorts())
					for (ActivityInputPort aPort : a.getInputPorts())
						if (aPort.getName().equals(pPort.getName())) {
							if (pPort.getDepth() != aPort.getDepth())
								result.put(pPort, aPort);
							break;
						}
				return result;
			}
			
			private Map<ProcessorOutputPort, ActivityOutputPort> determineChangedOutputPorts(
					Processor p, Activity<?> a) {
				Map<ProcessorOutputPort, ActivityOutputPort> result = new HashMap<>();
				for (ProcessorOutputPort pPort : p.getOutputPorts())
					for (OutputPort aPort : a.getOutputPorts())
						if (aPort.getName().equals(pPort.getName())) {
							if ((pPort.getDepth() != aPort.getDepth())
									|| (pPort.getGranularDepth() != aPort
											.getGranularDepth()))
								result.put(pPort, (ActivityOutputPort) aPort);
							break;
						}
				return result;
			}
		};
	}

	private static final int DEFAULT_MAX_JOBS = 1;

	@Override
	public Edit<Processor> getDefaultDispatchStackEdit(Processor processor) {
		DispatchStackImpl stack = ((ProcessorImpl) processor)
				.getDispatchStack();
		// Top level parallelise layer
		int layer = 0;
		List<Edit<?>> edits = new ArrayList<>();
		edits.add(getAddDispatchLayerEdit(stack, new Parallelize(DEFAULT_MAX_JOBS),
				layer++));
		edits.add(getAddDispatchLayerEdit(stack, new ErrorBounce(), layer++));
		edits.add(getAddDispatchLayerEdit(stack, new Failover(), layer++));
		edits.add(getAddDispatchLayerEdit(stack, new Retry(), layer++));
		edits.add(getAddDispatchLayerEdit(stack, new Stop(), layer++));
		edits.add(getAddDispatchLayerEdit(stack, new Invoke(), layer++));

		final Edit<?> compoundEdit = new CompoundEdit(edits);
		return new AbstractProcessorEdit(processor) {
			@Override
			protected void doEditAction(ProcessorImpl processor)
					throws EditException {
				compoundEdit.doEdit();
			}
		};
	}

	@Override
	public Edit<Processor> getSetIterationStrategyStackEdit(
			Processor processor,
			final IterationStrategyStack iterationStrategyStack) {
		if (!(iterationStrategyStack instanceof IterationStrategyStackImpl))
			throw new RuntimeException(
					"Unknown implementation of iteration strategy "
							+ iterationStrategyStack);
		return new AbstractProcessorEdit(processor) {
			@Override
			protected void doEditAction(ProcessorImpl processor)
					throws EditException {
				processor.iterationStack = (IterationStrategyStackImpl) iterationStrategyStack;
			}
		};
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Edit<? extends Configurable<T>> getConfigureEdit(
			Configurable<T> configurable, T configBean) {
		return new ConfigureEdit(Configurable.class, configurable, configBean);
	}

	@Override
	public Edit<Merge> getReorderMergeInputPortsEdit(Merge merge,
			final List<MergeInputPort> reorderedMergeInputPortList) {
		return new AbstractMergeEdit(merge) {
			@Override
			protected void doEditAction(MergeImpl mergeImpl) {
				mergeImpl.reorderInputPorts(reorderedMergeInputPortList);
			}
		};
	}

	@Override
	public Edit<Processor> getRemoveActivityEdit(Processor processor,
			final Activity<?> activity) {
		return new AbstractProcessorEdit(processor) {
			@Override
			protected void doEditAction(ProcessorImpl processor) {
				processor.activityList.remove(activity);
			}
		};
	}

	@Override
	public Edit<IterationStrategyStack> getAddIterationStrategyEdit(
			IterationStrategyStack iterationStrategyStack,
			final IterationStrategy strategy) {
		if (!(iterationStrategyStack instanceof IterationStrategyStackImpl))
			throw new RuntimeException(
					"Object being edited must be instance of IterationStrategyStackImpl");
		final IterationStrategyStackImpl stack = (IterationStrategyStackImpl) iterationStrategyStack;
		return new EditSupport<IterationStrategyStack>() {
			@Override
			public IterationStrategyStack applyEdit() {
				stack.addStrategy(strategy);
				return stack;
			}

			@Override
			public IterationStrategyStack getSubject() {
				return stack;
			}
		};
	}

	@Override
	public Edit<IterationStrategy> getAddIterationStrategyInputNodeEdit(
			IterationStrategy iterationStrategy,
			final NamedInputPortNode namedInputPortNode) {
		if (!(iterationStrategy instanceof IterationStrategyImpl))
			throw new RuntimeException(
					"Object being edited must be instance of IterationStrategyImpl");
		final IterationStrategyImpl strategy = (IterationStrategyImpl) iterationStrategy;
		return new EditSupport<IterationStrategy>() {
			@Override
			public IterationStrategy applyEdit() throws EditException {
				strategy.addInput(namedInputPortNode);
				return strategy;
			}

			@Override
			public IterationStrategy getSubject() {
				return strategy;
			}
		};
	}

	@Override
	public Edit<IterationStrategyStack> getClearIterationStrategyStackEdit(
			final IterationStrategyStack iterationStrategyStack) {
		if (!(iterationStrategyStack instanceof IterationStrategyStackImpl))
			throw new RuntimeException(
					"Object being edited must be instance of IterationStrategyStackImpl");
		return new EditSupport<IterationStrategyStack>() {
			@Override
			public IterationStrategyStack applyEdit() throws EditException {
				((IterationStrategyStackImpl) iterationStrategyStack).clear();
				return iterationStrategyStack;
			}

			@Override
			public IterationStrategyStack getSubject() {
				return iterationStrategyStack;
			}
		};
	}
}
