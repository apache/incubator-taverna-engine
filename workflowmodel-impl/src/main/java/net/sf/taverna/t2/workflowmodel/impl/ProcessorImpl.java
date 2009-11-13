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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.raven.log.Log;
import net.sf.taverna.t2.annotation.AbstractAnnotatedThing;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.IterationInternalEvent;
import net.sf.taverna.t2.lang.observer.MultiCaster;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.monitor.MonitorManager;
import net.sf.taverna.t2.monitor.MonitorableProperty;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.Condition;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowValidationReport;
import net.sf.taverna.t2.workflowmodel.InvalidDataflowException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorFinishedEvent;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.Job;
import net.sf.taverna.t2.workflowmodel.processor.activity.NestedDataflow;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.PropertyContributingDispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.impl.DispatchStackImpl;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationTypeMismatchException;
import net.sf.taverna.t2.workflowmodel.processor.iteration.MissingIterationInputException;
import net.sf.taverna.t2.workflowmodel.processor.iteration.impl.IterationStrategyImpl;
import net.sf.taverna.t2.workflowmodel.processor.iteration.impl.IterationStrategyStackImpl;

/**
 * Implementation of Processor
 * 
 * @author Tom Oinn
 * @author Stuart Owen
 * @author Alex Nenadic
 * 
 */
public final class ProcessorImpl extends AbstractAnnotatedThing<Processor>
		implements Processor{

	protected List<ConditionImpl> conditions = new ArrayList<ConditionImpl>();

	protected List<ConditionImpl> controlledConditions = new ArrayList<ConditionImpl>();

	protected List<ProcessorInputPortImpl> inputPorts = new ArrayList<ProcessorInputPortImpl>();

	protected List<ProcessorOutputPortImpl> outputPorts = new ArrayList<ProcessorOutputPortImpl>();

	protected List<Activity<?>> activityList = new ArrayList<Activity<?>>();

	protected AbstractCrystalizer crystalizer;

	protected DispatchStackImpl dispatchStack;

	protected IterationStrategyStackImpl iterationStack;

	private static int pNameCounter = 0;

	protected String name;

	public transient int resultWrappingDepth = -1;

	protected transient Map<String, Set<MonitorableProperty<?>>> monitorables = new HashMap<String, Set<MonitorableProperty<?>>>();
	
	private static Log logger = Log.getLogger(ProcessorImpl.class);

	private MultiCaster<ProcessorFinishedEvent> processorFinishedMultiCaster = new MultiCaster<ProcessorFinishedEvent>(this);
	
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

	@SuppressWarnings("unchecked")
	protected ProcessorImpl() {

		// Set a default name
		name = "UnnamedProcessor" + (pNameCounter++);

		// Create iteration stack, configure it to send jobs and completion
		// events to the dispatch stack.
		iterationStack = new IterationStrategyStackImpl() {
			@Override
			protected void receiveEventFromStrategy(IterationInternalEvent e) {
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

			public Processor getProcessor() {
				return ProcessorImpl.this;
			}
			
			/**
			 * Called when an event bubbles out of the top of the dispatch
			 * stack. In this case we pass it into the crystalizer.
			 */
			@Override
			protected void pushEvent(IterationInternalEvent e) {
				crystalizer.receiveEvent(e);
			}

			/**
			 * Iterate over all the preconditions and return true if and only if
			 * all are satisfied for the given process identifier.
			 */
			@Override
			protected boolean conditionsSatisfied(String owningProcess) {
				for (Condition c : conditions) {
					if (c.isSatisfied(owningProcess) == false) {
						return false;
					}
				}
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
				if (! controlledConditions.isEmpty()) {
					String enclosingProcess = owningProcess.substring(0,
							owningProcess.lastIndexOf(':'));
					for (ConditionImpl ci : controlledConditions) {
						ci.satisfy(enclosingProcess);
						ci.getTarget().getDispatchStack().satisfyConditions(
								enclosingProcess);
					}
				}
				// Tell whoever is interested that the processor has finished executing
				processorFinishedMultiCaster.notify(new ProcessorFinishedEvent(this.getProcessor(), owningProcess));
			}

			public void receiveMonitorableProperty(MonitorableProperty<?> prop,
					String processID) {
				synchronized (monitorables) {
					Set<MonitorableProperty<?>> props = monitorables
							.get(processID);
					if (props == null) {
						props = new HashSet<MonitorableProperty<?>>();
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
	 * 			 	if the entity depended on a dataflow that was not valid
	 */
	public boolean doTypeCheck() throws IterationTypeMismatchException, InvalidDataflowException {
		
		// Check for any nested dataflows, they should all be valid
		for (Activity<?> activity : getActivityList()) {
			if (activity instanceof NestedDataflow) {
				NestedDataflow nestedDataflowActivity = (NestedDataflow) activity;
				Dataflow nestedDataflow = nestedDataflowActivity.getNestedDataflow();
				DataflowValidationReport validity = nestedDataflow.checkValidity();
				if (! validity.isValid())  {
					throw new InvalidDataflowException(nestedDataflow, validity);
				}
			}	
		}
		
		// Check whether all our input ports have inbound links
		Map<String, Integer> inputDepths = new HashMap<String, Integer>();
		for (ProcessorInputPortImpl input : inputPorts) {
			if (input.getIncomingLink() == null) {
				return false;
			} else {
				if (input.getIncomingLink().getResolvedDepth() == -1) {
					// Incoming link hasn't been resolved yet, can't do this
					// processor at the moment
					return false;
				}
				// Get the conceptual resolved depth of the datalink
				inputDepths.put(input.getName(), input.getIncomingLink()
						.getResolvedDepth());
				// Configure the filter with the finest grained item from the
				// link source
				input.setFilterDepth(input.getIncomingLink().getSource()
						.getGranularDepth());
			}
		}
		// Got here so we have all the inputs, now test whether the iteration
		// strategy typechecks correctly
		try {
			this.resultWrappingDepth = iterationStack
					.getIterationDepth(inputDepths);
			for (BasicEventForwardingOutputPort output : outputPorts) {
				for (DatalinkImpl outgoingLink : output.outgoingLinks) {
					// Set the resolved depth on each output edge
					outgoingLink.setResolvedDepth(this.resultWrappingDepth
							+ output.getDepth());
				}
			}

		} catch (MissingIterationInputException e) {
			// This should never happen as we only get here if we've already
			// checked that all the inputs have been provided. If it does happen
			// we've got some deeper issues.
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
			if (portName.equals(name)) {
				return p;
			}
		}
		return null;
	}

	protected ProcessorOutputPortImpl getOutputPortWithName(String name) {
		for (ProcessorOutputPortImpl p : outputPorts) {
			String portName = p.getName();
			if (portName.equals(name)) {
				return p;
			}
		}
		return null;
	}

	/* Implementations of Processor interface */

	public void fire(String enclosingProcess, InvocationContext context) {
		Job newJob = new Job(enclosingProcess + ":" + this.name, new int[0],
				new HashMap<String, T2Reference>(), context);
		dispatchStack.receiveEvent(newJob);
	}

	public List<? extends Condition> getPreconditionList() {
		return Collections.unmodifiableList(conditions);
	}

	public List<? extends Condition> getControlledPreconditionList() {
		return Collections.unmodifiableList(controlledConditions);
	}

	public DispatchStackImpl getDispatchStack() {
		return dispatchStack;
	}

	public IterationStrategyStackImpl getIterationStrategy() {
		return iterationStack;
	}

	public List<? extends ProcessorInputPort> getInputPorts() {
		return Collections.unmodifiableList(inputPorts);
	}

	public List<? extends ProcessorOutputPort> getOutputPorts() {
		return Collections.unmodifiableList(outputPorts);
	}

	public List<? extends Activity<?>> getActivityList() {
		return Collections.unmodifiableList(activityList);
	}

	protected void setName(String newName) {
		this.name = newName;
	}

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
		// Given the dataflow process identifier, so append local name to get
		// the process identifier that will be applied to incoming data tokens
		String processID = dataflowOwningProcess + ":" + getLocalName();

		// The set of monitorable (and steerable) properties for this processor
		// level monitor node
		Set<MonitorableProperty<?>> properties = new HashSet<MonitorableProperty<?>>();

		// If any dispatch layers implement PropertyContributingDispatchLayer
		// then message them to push their properties into the property store
		// within the dispatch stack. In this case the anonymous inner class
		// implements this by storing them in a protected map within
		// ProcessoImpl from where they can be recovered after the iteration has
		// finished.
		for (DispatchLayer<?> layer : dispatchStack.getLayers()) {
			if (layer instanceof PropertyContributingDispatchLayer) {
				((PropertyContributingDispatchLayer<?>) layer)
						.injectPropertiesFor(processID);
			}
		}
		// All layers have now injected properties into the parent dispatch
		// stack, which has responded by building an entry in the monitorables
		// map in this class. We can pull everything out of it and remove the
		// entry quite safely at this point.
		synchronized (monitorables) {
			Set<MonitorableProperty<?>> layerProps = monitorables
					.get(processID);
			if (layerProps != null) {
				for (MonitorableProperty<?> prop : layerProps) {
					properties.add(prop);
				}
				monitorables.remove(processID);
			}
		}

		// Register the node with the monitor tree, including any aggregated
		// properties from layers.
		MonitorManager.getInstance().registerNode(this,
				dataflowOwningProcess + ":" + getLocalName(), properties);
	}

	public void addObserver(Observer<ProcessorFinishedEvent> observer) {
		processorFinishedMultiCaster.addObserver(observer);
	}

	public List<Observer<ProcessorFinishedEvent>> getObservers() {
		return processorFinishedMultiCaster.getObservers();
	}

	public void removeObserver(Observer<ProcessorFinishedEvent> observer) {
		processorFinishedMultiCaster.removeObserver(observer);
	}
	
	@Override
	public String toString() {
		return "Processor " + getLocalName();
	}
}
