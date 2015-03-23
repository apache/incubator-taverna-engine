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

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.annotation.AbstractAnnotatedThing;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.IterationInternalEvent;
import org.apache.taverna.lang.observer.MultiCaster;
import org.apache.taverna.lang.observer.Observer;
import org.apache.taverna.monitor.MonitorManager;
import org.apache.taverna.monitor.MonitorableProperty;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.Condition;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowValidationReport;
import org.apache.taverna.workflowmodel.InvalidDataflowException;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.ProcessorFinishedEvent;
import org.apache.taverna.workflowmodel.ProcessorInputPort;
import org.apache.taverna.workflowmodel.ProcessorOutputPort;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.activity.Job;
import org.apache.taverna.workflowmodel.processor.activity.NestedDataflow;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.PropertyContributingDispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.impl.DispatchStackImpl;
import org.apache.taverna.workflowmodel.processor.iteration.IterationTypeMismatchException;
import org.apache.taverna.workflowmodel.processor.iteration.MissingIterationInputException;
import org.apache.taverna.workflowmodel.processor.iteration.impl.IterationStrategyImpl;
import org.apache.taverna.workflowmodel.processor.iteration.impl.IterationStrategyStackImpl;

import org.apache.log4j.Logger;

/**
 * Implementation of Processor
 * 
 * @author Tom Oinn
 * @author Stuart Owen
 * @author Alex Nenadic
 * 
 */
public final class ProcessorImpl extends AbstractAnnotatedThing<Processor>
		implements Processor {
	private static int pNameCounter = 0;
	private static Logger logger = Logger.getLogger(ProcessorImpl.class);

	protected List<ConditionImpl> conditions = new ArrayList<>();
	protected List<ConditionImpl> controlledConditions = new ArrayList<>();
	protected List<ProcessorInputPortImpl> inputPorts = new ArrayList<>();
	protected List<ProcessorOutputPortImpl> outputPorts = new ArrayList<>();
	protected List<Activity<?>> activityList = new ArrayList<>();
	protected AbstractCrystalizer crystalizer;
	protected DispatchStackImpl dispatchStack;
	protected IterationStrategyStackImpl iterationStack;
	protected String name;
	public transient int resultWrappingDepth = -1;
	protected transient Map<String, Set<MonitorableProperty<?>>> monitorables = new HashMap<>();
	private MultiCaster<ProcessorFinishedEvent> processorFinishedMultiCaster = new MultiCaster<>(
			this);

	/**
	 * <p>
	 * Create a new processor implementation with default blank iteration
	 * strategy and dispatch stack.
	 * </p>
	 * <p>
	 * This constructor is protected to enforce that an instance can only be
	 * created via the {@link EditsImpl#createProcessor(String)} method.
	 * </p>
	 */

	protected ProcessorImpl() {
		// Set a default name
		name = "UnnamedProcessor" + (pNameCounter++);

		/*
		 * Create iteration stack, configure it to send jobs and completion
		 * events to the dispatch stack.
		 */
		iterationStack = new IterationStrategyStackImpl() {
			@Override
			protected void receiveEventFromStrategy(IterationInternalEvent<?> e) {
				dispatchStack.receiveEvent(e);
			}
		};
		iterationStack.addStrategy(new IterationStrategyImpl());

		// Configure dispatch stack to push output events to the crystalizer
		dispatchStack = new DispatchStackImpl() {
			@Override
			protected String getProcessName() {
				return ProcessorImpl.this.name;
			}

			@Override
			public Processor getProcessor() {
				return ProcessorImpl.this;
			}

			/**
			 * Called when an event bubbles out of the top of the dispatch
			 * stack. In this case we pass it into the crystalizer.
			 */
			@Override
			protected void pushEvent(IterationInternalEvent<?> e) {
				crystalizer.receiveEvent(e);
			}

			/**
			 * Iterate over all the preconditions and return true if and only if
			 * all are satisfied for the given process identifier.
			 */
			@Override
			protected boolean conditionsSatisfied(String owningProcess) {
				for (Condition c : conditions)
					if (c.isSatisfied(owningProcess) == false)
						return false;
				return true;
			}

			@Override
			protected List<? extends Activity<?>> getActivities() {
				return ProcessorImpl.this.getActivityList();
			}

			/**
			 * We've finished here, set the satisfied property on any controlled
			 * condition objects to true and notify the targets.
			 */
			@Override
			protected void finishedWith(String owningProcess) {
				if (!controlledConditions.isEmpty()) {
					String enclosingProcess = owningProcess.substring(0,
							owningProcess.lastIndexOf(':'));
					for (ConditionImpl ci : controlledConditions) {
						ci.satisfy(enclosingProcess);
						ci.getTarget().getDispatchStack()
								.satisfyConditions(enclosingProcess);
					}
				}
				/*
				 * Tell whoever is interested that the processor has finished
				 * executing
				 */
				processorFinishedMultiCaster.notify(new ProcessorFinishedEvent(
						this.getProcessor(), owningProcess));
			}

			@Override
			public void receiveMonitorableProperty(MonitorableProperty<?> prop,
					String processID) {
				synchronized (monitorables) {
					Set<MonitorableProperty<?>> props = monitorables
							.get(processID);
					if (props == null) {
						props = new HashSet<>();
						monitorables.put(processID, props);
					}
					props.add(prop);
				}
			}
		};

		// Configure crystalizer to send realized events to the output ports
		crystalizer = new ProcessorCrystalizerImpl(this);
	}

	/**
	 * When called this method configures input port filters and the
	 * crystalizer, pushing cardinality information into outgoing datalinks.
	 * 
	 * @return true if the typecheck was successful or false if the check failed
	 *         because there were preconditions missing such as unsatisfied
	 *         input types
	 * @throws IterationTypeMismatchException
	 *             if the typing occured but didn't match because of an
	 *             iteration mismatch
	 * @throws InvalidDataflowException
	 *             if the entity depended on a dataflow that was not valid
	 */
	@Override
	public boolean doTypeCheck() throws IterationTypeMismatchException,
			InvalidDataflowException {
		// Check for any nested dataflows, they should all be valid
		for (Activity<?> activity : getActivityList())
			if (activity instanceof NestedDataflow) {
				NestedDataflow nestedDataflowActivity = (NestedDataflow) activity;
				Dataflow nestedDataflow = nestedDataflowActivity
						.getNestedDataflow();
				DataflowValidationReport validity = nestedDataflow
						.checkValidity();
				if (!validity.isValid())
					throw new InvalidDataflowException(nestedDataflow, validity);
			}

		/*
		 * Check whether all our input ports have inbound links
		 */

		Map<String, Integer> inputDepths = new HashMap<>();
		for (ProcessorInputPortImpl input : inputPorts) {
			if (input.getIncomingLink() == null)
				return false;
			if (input.getIncomingLink().getResolvedDepth() == -1)
				/*
				 * Incoming link hasn't been resolved yet, can't do this
				 * processor at the moment
				 */
				return false;

			// Get the conceptual resolved depth of the datalink
			inputDepths.put(input.getName(), input.getIncomingLink()
					.getResolvedDepth());
			/*
			 * Configure the filter with the finest grained item from the link
			 * source
			 */
			input.setFilterDepth(input.getIncomingLink().getSource()
					.getGranularDepth());
		}

		/*
		 * Got here so we have all the inputs, now test whether the iteration
		 * strategy typechecks correctly
		 */

		try {
			this.resultWrappingDepth = iterationStack
					.getIterationDepth(inputDepths);
			for (BasicEventForwardingOutputPort output : outputPorts)
				for (DatalinkImpl outgoingLink : output.outgoingLinks)
					// Set the resolved depth on each output edge
					outgoingLink.setResolvedDepth(this.resultWrappingDepth
							+ output.getDepth());
		} catch (MissingIterationInputException e) {
			/*
			 * This should never happen as we only get here if we've already
			 * checked that all the inputs have been provided. If it does happen
			 * we've got some deeper issues.
			 */
			logger.error(e);
			return false;
		}

		// If we get to here everything has been configured appropriately
		return true;
	}

	/* Utility methods */

	protected ProcessorInputPortImpl getInputPortWithName(String name) {
		for (ProcessorInputPortImpl p : inputPorts) {
			String portName = p.getName();
			if (portName.equals(name))
				return p;
		}
		return null;
	}

	protected ProcessorOutputPortImpl getOutputPortWithName(String name) {
		for (ProcessorOutputPortImpl p : outputPorts) {
			String portName = p.getName();
			if (portName.equals(name))
				return p;
		}
		return null;
	}

	/* Implementations of Processor interface */

	@Override
	public void fire(String enclosingProcess, InvocationContext context) {
		Job newJob = new Job(enclosingProcess + ":" + this.name, new int[0],
				new HashMap<String, T2Reference>(), context);
		dispatchStack.receiveEvent(newJob);
	}

	@Override
	public List<? extends Condition> getPreconditionList() {
		return unmodifiableList(conditions);
	}

	@Override
	public List<? extends Condition> getControlledPreconditionList() {
		return unmodifiableList(controlledConditions);
	}

	@Override
	public DispatchStackImpl getDispatchStack() {
		return dispatchStack;
	}

	@Override
	public IterationStrategyStackImpl getIterationStrategy() {
		return iterationStack;
	}

	@Override
	public List<? extends ProcessorInputPort> getInputPorts() {
		return unmodifiableList(inputPorts);
	}

	@Override
	public List<? extends ProcessorOutputPort> getOutputPorts() {
		return unmodifiableList(outputPorts);
	}

	@Override
	public List<? extends Activity<?>> getActivityList() {
		return unmodifiableList(activityList);
	}

	protected void setName(String newName) {
		this.name = newName;
	}

	@Override
	public String getLocalName() {
		return this.name;
	}

	/**
	 * Called by the DataflowImpl containing this processor requesting that it
	 * register itself with the monitor tree under the specified process
	 * identifier.
	 * 
	 * @param dataflowOwningProcess
	 *            the process identifier of the parent dataflow, the processor
	 *            must register with this as the base path plus the local name
	 */
	void registerWithMonitor(String dataflowOwningProcess) {
		/*
		 * Given the dataflow process identifier, so append local name to get
		 * the process identifier that will be applied to incoming data tokens
		 */
		String processID = dataflowOwningProcess + ":" + getLocalName();

		/*
		 * The set of monitorable (and steerable) properties for this processor
		 * level monitor node
		 */
		Set<MonitorableProperty<?>> properties = new HashSet<>();

		/*
		 * If any dispatch layers implement PropertyContributingDispatchLayer
		 * then message them to push their properties into the property store
		 * within the dispatch stack. In this case the anonymous inner class
		 * implements this by storing them in a protected map within
		 * ProcessoImpl from where they can be recovered after the iteration has
		 * finished.
		 */
		for (DispatchLayer<?> layer : dispatchStack.getLayers())
			if (layer instanceof PropertyContributingDispatchLayer)
				((PropertyContributingDispatchLayer<?>) layer)
						.injectPropertiesFor(processID);
		/*
		 * All layers have now injected properties into the parent dispatch
		 * stack, which has responded by building an entry in the monitorables
		 * map in this class. We can pull everything out of it and remove the
		 * entry quite safely at this point.
		 */
		synchronized (monitorables) {
			Set<MonitorableProperty<?>> layerProps = monitorables
					.get(processID);
			if (layerProps != null) {
				for (MonitorableProperty<?> prop : layerProps)
					properties.add(prop);
				monitorables.remove(processID);
			}
		}

		/*
		 * Register the node with the monitor tree, including any aggregated
		 * properties from layers.
		 */
		MonitorManager.getInstance().registerNode(this,
				dataflowOwningProcess + ":" + getLocalName(), properties);
	}

	@Override
	public void addObserver(Observer<ProcessorFinishedEvent> observer) {
		processorFinishedMultiCaster.addObserver(observer);
	}

	@Override
	public List<Observer<ProcessorFinishedEvent>> getObservers() {
		return processorFinishedMultiCaster.getObservers();
	}

	@Override
	public void removeObserver(Observer<ProcessorFinishedEvent> observer) {
		processorFinishedMultiCaster.removeObserver(observer);
	}

	@Override
	public String toString() {
		return "Processor " + getLocalName();
	}
}
