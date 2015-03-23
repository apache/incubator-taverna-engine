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

package org.apache.taverna.facade.impl;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.synchronizedList;
import static java.util.UUID.randomUUID;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.taverna.facade.FacadeListener;
import org.apache.taverna.facade.ResultListener;
import org.apache.taverna.facade.WorkflowInstanceFacade;
import org.apache.taverna.facade.WorkflowRunCancellation;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.TokenOrderException;
import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;
import org.apache.taverna.monitor.MonitorManager;
import org.apache.taverna.monitor.MonitorNode;
import org.apache.taverna.monitor.MonitorableProperty;
import org.apache.taverna.monitor.NoSuchPropertyException;
import org.apache.taverna.provenance.item.DataflowRunComplete;
import org.apache.taverna.provenance.item.WorkflowDataProvenanceItem;
import org.apache.taverna.provenance.item.WorkflowProvenanceItem;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.WorkflowRunIdEntity;
import org.apache.taverna.utility.TypedTreeModel;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.DataflowValidationReport;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.InvalidDataflowException;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.ProcessorFinishedEvent;
import org.apache.taverna.workflowmodel.impl.EditsImpl;
import org.apache.taverna.workflowmodel.impl.ProcessorImpl;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchLayer;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchStack;
import org.apache.taverna.workflowmodel.processor.dispatch.layers.ErrorBounce;
import org.apache.taverna.workflowmodel.processor.dispatch.layers.IntermediateProvenance;
import org.apache.taverna.workflowmodel.processor.dispatch.layers.Parallelize;
import org.apache.taverna.workflowmodel.processor.dispatch.layers.Stop;

import org.apache.log4j.Logger;

/**
 * Implementation of {@link WorkflowInstanceFacade}
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 * @author Ian Dunlop
 * @author Alex Nenadic
 */
public class WorkflowInstanceFacadeImpl implements WorkflowInstanceFacade {
	private static Logger logger = Logger
			.getLogger(WorkflowInstanceFacadeImpl.class);

	protected static AtomicLong owningProcessId = new AtomicLong(0);
	private InvocationContext context;	
	private State state = State.prepared;
	public Date stateLastModified = new Date();

	@Override
	public InvocationContext getContext() {
		return context;
	}

	private Dataflow dataflow;
	private FacadeResultListener facadeResultListener;
	/** How many processors have finished so far */
	private int processorsToComplete;
	private String instanceOwningProcessId;
	private String localName;
	private MonitorManager monitorManager = MonitorManager.getInstance();
	protected List<FacadeListener> facadeListeners = synchronizedList(new ArrayList<FacadeListener>());
	protected List<ResultListener> resultListeners = synchronizedList(new ArrayList<ResultListener>());
	private boolean provEnabled = false;
	private WeakHashMap<String, T2Reference> pushedDataMap = new WeakHashMap<>();
	/** Id of this run */
	private String workflowRunId;
	private Timestamp workflowStarted;
	private WorkflowProvenanceItem workflowItem = null;
	private int portsToComplete;
	
	private enum WorkflowInstanceFacadeChange {
		CANCELLATION, PORT_DECREMENT, PROCESSOR_DECREMENT
	};

	public WorkflowInstanceFacadeImpl(Dataflow dataflow,
			InvocationContext context, String parentProcess)
			throws InvalidDataflowException {
		if (dataflow == null) {
			logger.error("Dataflow is null");
			throw new IllegalArgumentException("Dataflow is null");
		}
		DataflowValidationReport report = dataflow.checkValidity();
		if (!report.isValid())
			throw new InvalidDataflowException(dataflow, report);

		this.dataflow = dataflow;
		this.context = context;
		this.portsToComplete = dataflow.getOutputPorts().size();
		this.processorsToComplete = dataflow.getProcessors().size();
		this.localName = "facade" + owningProcessId.getAndIncrement();
		// Set the wf run id
		workflowRunId = UUID.randomUUID().toString();
		if (parentProcess.isEmpty()) {
			// Top-level workflow
			
			/*
			 * add top level workflow run so that reference service can generate
			 * identifiers linked to our run
			 */
			context.addEntity(new WorkflowRunIdEntity(workflowRunId));
			this.instanceOwningProcessId = localName;
			
			/*
			 * Add this WorkflowInstanceFacade to the map of all workflow run
			 * IDs against the corresponding WorkflowInstanceFacadeS/ - to be
			 * used by DependencyActivity's such as API consumer and Beanshell
			 */
			workflowRunFacades.put(localName, new WeakReference<WorkflowInstanceFacade>(this));
			/*
			 * Note that we do not put the IDs for nested workflows, just for
			 * the main ones!
			 */
		} else {
			// Nested workflow
			this.instanceOwningProcessId = parentProcess + ":" + localName;
		}		
				
		if (context.getProvenanceReporter() != null) {
			provEnabled = true;
			workflowItem = new WorkflowProvenanceItem();
			workflowItem.setDataflow(dataflow);
			workflowItem.setProcessId(instanceOwningProcessId);
			workflowItem.setIdentifier(workflowRunId);
			workflowItem.setParentId(dataflow.getIdentifier());
			workflowItem.setWorkflowId(dataflow.getIdentifier());
			
			/*
			 * FIXME: workflowItem is local to each instanceOwningProcessId, but
			 * might be added to the Processor within different concurrent runs.
			 * (T3-930)
			 */
			addProvenanceLayerToProcessors(workflowItem);
			context.getProvenanceReporter().setSessionID(workflowRunId);
		}
		facadeResultListener = new FacadeResultListener(dataflow, workflowItem);
		
		// Register an observer with each of the processors
		for (Processor processor : dataflow.getProcessors()) {
			String expectedProcessId = instanceOwningProcessId + ":"
					+ dataflow.getLocalName() + ":" + processor.getLocalName();
			ProcessorFinishedObserver observer = new ProcessorFinishedObserver(
					workflowItem, expectedProcessId);
			((ProcessorImpl) processor).addObserver(observer);
		}
	}

	private void addProvenanceLayerToProcessors(WorkflowProvenanceItem workflowItem) {
		// TODO Shouldn't we use a bean for this? 
		Edits edits = new EditsImpl();
		for (Processor processor : dataflow.getProcessors())
			/*
			 * Synchronized per processor as we might be modifying its dispatch
			 * stack (fixes T3-929)
			 */
		    synchronized (processor) {               
		        DispatchStack dispatchStack = processor.getDispatchStack();
    			List<DispatchLayer<?>> layers = dispatchStack.getLayers();
    			if (isProvenanceAlreadyAdded(layers))
    				continue;
    			IntermediateProvenance provenance = new IntermediateProvenance();
				provenance.setWorkflow(workflowItem);
				provenance.setReporter(context.getProvenanceReporter());

				try {
					edits.getAddDispatchLayerEdit(dispatchStack, provenance,
					        provenancePosition(layers)).doEdit();
					break;
				} catch (EditException e) {
					logger.warn("adding provenance layer to dispatch stack failed "
									+ e.toString());
				}
		    }
	}

    private boolean isProvenanceAlreadyAdded(List<DispatchLayer<?>> layers) {
        for (DispatchLayer<?> layer : layers)
        	if (layer instanceof IntermediateProvenance)
        		return true;
        return false;
    }

    private int provenancePosition(List<DispatchLayer<?>> layers) {
        int position=0; // fallback - beginning of list
        for (int i = 0; i < layers.size(); i++) {
            DispatchLayer<?> layer = layers.get(i);
        	if (layer instanceof Parallelize)
        	    // Below Parallelize (should be there!)
        	    position = i+1;
        	else if (layer instanceof ErrorBounce)
        	    // and inserted just above ErrorBounce (if it's there)
        		position = i;
        }
        return position;
    }

	@Override
	public void addFacadeListener(FacadeListener listener) {
		facadeListeners.add(listener);
	}

	@Override
	public void addResultListener(ResultListener listener) {
		synchronized (resultListeners) {
			if (resultListeners.isEmpty())
				for (DataflowOutputPort port : dataflow.getOutputPorts())
					port.addResultListener(facadeResultListener);
			resultListeners.add(listener); 
		}		
	}

	@Override
	public synchronized void fire() throws IllegalStateException {
		if (getState().equals(State.running))
			throw new IllegalStateException("Workflow is already running!");
		workflowStarted = new Timestamp(currentTimeMillis());
		setState(State.running);
		if (provEnabled) {
			workflowItem.setInvocationStarted(workflowStarted);
			context.getProvenanceReporter().addProvenanceItem(workflowItem);
		}
		
		HashSet<MonitorableProperty<?>> properties = new HashSet<>();
		properties.add(new StateProperty());
		monitorManager.registerNode(this, instanceOwningProcessId.split(":"),				
				properties);
		dataflow.fire(instanceOwningProcessId, context);		
	}

	public final class StateProperty implements MonitorableProperty<State> {
		@Override
		public Date getLastModified() {
			return stateLastModified;
		}

		@Override
		public String[] getName() {
			return new String[] { "facade", "state" };
		}

		@Override
		public State getValue() throws NoSuchPropertyException {
			return getState();
		}
	}
	
	@Override
	public Dataflow getDataflow() {
		return dataflow;
	}

	@Override
	public TypedTreeModel<MonitorNode> getStateModel() {
		// TODO WorkflowInstanceFacade.getStateModel not yet implemented
		return null;
	}

	@Override
	public void pushData(WorkflowDataToken token, String portName)
			throws TokenOrderException {
		State currentState = getState();
		if (! currentState.equals(State.running))
			throw new IllegalStateException(
					"Can't push data, current state is not running, but "
							+ currentState);
		/*
		 * TODO: throw TokenOrderException when token stream is violates order
		 * constraints.
		 */
		for (DataflowInputPort port : dataflow.getInputPorts()) {
			if (!portName.equals(port.getName()))
				continue;
			if (token.getIndex().length == 0) {
				if (pushedDataMap.containsKey(portName))
					throw new IllegalStateException("Already pushed for port " + portName);
				pushedDataMap.put(portName, token.getData());					
			}
			if (provEnabled) {
				WorkflowDataProvenanceItem provItem = new WorkflowDataProvenanceItem();
				provItem.setPortName(portName);
				provItem.setInputPort(true);
				provItem.setData(token.getData());
				provItem.setReferenceService(context.getReferenceService());
				provItem.setParentId(workflowItem.getIdentifier());
				provItem.setWorkflowId(workflowItem.getParentId());
				provItem.setIdentifier(randomUUID().toString());
				provItem.setParentId(instanceOwningProcessId);
				provItem.setProcessId(instanceOwningProcessId);
				provItem.setIndex(token.getIndex());
				provItem.setFinal(token.isFinal());
				context.getProvenanceReporter().addProvenanceItem(provItem);
			}
			port.receiveEvent(token.pushOwningProcess(localName));
		}
	}

	@Override
	public void removeFacadeListener(FacadeListener listener) {
		facadeListeners.remove(listener);
	}

	private <T> ArrayList<T> copyList(List<T> listenerList) {
		synchronized (listenerList) {
			return new ArrayList<>(listenerList);
		}
	}

	@Override
	public void removeResultListener(ResultListener listener) {
		synchronized (resultListeners) {
			resultListeners.remove(listener);
			if (resultListeners.isEmpty())
				for (DataflowOutputPort port : dataflow.getOutputPorts())
					port.removeResultListener(facadeResultListener);
		}
	}

	protected class FacadeResultListener implements ResultListener {
		private final WorkflowProvenanceItem workflowItem;

		public FacadeResultListener(Dataflow dataflow,
				WorkflowProvenanceItem workflowItem) {
			this.workflowItem = workflowItem;
		}

		@Override
		public void resultTokenProduced(WorkflowDataToken token, String portName) {			
			if (!instanceOwningProcessId.equals(token.getOwningProcess()))
				return;
			if (getState().equals(State.cancelled))
				// Throw the token away
				return;

			if (provEnabled) {
				WorkflowDataProvenanceItem provItem = new WorkflowDataProvenanceItem();
				provItem.setPortName(portName);
				provItem.setInputPort(false);
				provItem.setData(token.getData());
				provItem.setReferenceService(context.getReferenceService());
				provItem.setParentId(workflowItem.getIdentifier());
				provItem.setWorkflowId(workflowItem.getParentId());
				provItem.setIdentifier(randomUUID().toString());
				provItem.setParentId(instanceOwningProcessId);
				provItem.setProcessId(instanceOwningProcessId);
				provItem.setIndex(token.getIndex());
				provItem.setFinal(token.isFinal());
				context.getProvenanceReporter().addProvenanceItem(provItem);
			}
			
			for (ResultListener resultListener : copyList(resultListeners))
				try {
					resultListener.resultTokenProduced(
							token.popOwningProcess(), portName);
				} catch (RuntimeException ex) {
					logger.warn("Could not notify result listener "
							+ resultListener, ex);
				}
			if (token.getIndex().length == 0)
				checkWorkflowFinished(WorkflowInstanceFacadeChange.PORT_DECREMENT);
		}
	}
	

	/**
	 * An observer of events that occur when a processor finishes with execution.
	 *
	 */
	private class ProcessorFinishedObserver implements Observer<ProcessorFinishedEvent>{
		private final String expectedProcessId;

		public ProcessorFinishedObserver(WorkflowProvenanceItem workflowItem, String expectedProcessId) {
			this.expectedProcessId = expectedProcessId;
		}

		@Override
		public void notify(Observable<ProcessorFinishedEvent> sender,
				ProcessorFinishedEvent message) throws Exception {
			if (! message.getOwningProcess().equals(expectedProcessId))
				return;
			
			// De-register the processor node from the monitor as it has finished
			monitorManager.deregisterNode(message.getOwningProcess());
			
			// De-register this observer from the processor
			message.getProcessor().removeObserver(this);
			
			// All processors have finished => the workflow run has finished
			checkWorkflowFinished(WorkflowInstanceFacadeChange.PROCESSOR_DECREMENT);
		}
	}

	private void applyChange(WorkflowInstanceFacadeChange change) {
		switch (change) {
		case CANCELLATION:
			processorsToComplete = 0;
			portsToComplete = 0;
			break;
		case PORT_DECREMENT:
			portsToComplete--;
			break;
		case PROCESSOR_DECREMENT:
			processorsToComplete--;
			break;
		}
	}
	protected void checkWorkflowFinished(WorkflowInstanceFacadeChange change) {
		synchronized (this) {
			applyChange(change);
			if (getState().equals(State.cancelled) && processorsToComplete < 0) {
				logger.error("Already cancelled workflow run "
						+ instanceOwningProcessId);
				return;
			}
			if (getState().equals(State.completed)) {
				logger.error("Already finished workflow run "
						+ instanceOwningProcessId, new IllegalStateException());
				return;
			}
			if (processorsToComplete > 0 || portsToComplete > 0)
				// Not yet finished
				return;
			if (processorsToComplete < 0 || portsToComplete < 0) {
				logger.error("Already finished workflow run "
						+ instanceOwningProcessId, new IllegalStateException());
				return;
			}
			if (!getState().equals(State.cancelled))
				setState(State.completed);
			processorsToComplete = -1;
			portsToComplete = -1;
		}	
		// De-register the workflow node from the monitor
		monitorManager.deregisterNode(instanceOwningProcessId + ":" + dataflow.getLocalName());

		/*
		 * De-register this facade node from the monitor - this will effectively
		 * tell the monitor that the workflow run has finished
		 */
		monitorManager.deregisterNode(instanceOwningProcessId);

		if (provEnabled) {
			DataflowRunComplete provItem = new DataflowRunComplete();
			provItem.setInvocationEnded(new Timestamp(currentTimeMillis()));
			provItem.setParentId(workflowItem.getIdentifier());
			provItem.setWorkflowId(workflowItem.getParentId());
			provItem.setProcessId(instanceOwningProcessId);
			provItem.setIdentifier(randomUUID().toString());
			provItem.setState(getState());
			context.getProvenanceReporter().addProvenanceItem(provItem);
		}
	}

	@Override
	public WeakHashMap<String, T2Reference> getPushedDataMap() {
		return pushedDataMap;
	}

	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}

	@Override
	public String getWorkflowRunId() {
		return workflowRunId;
	}
	
	@Override
	public synchronized State getState() {
		return state;
	}
	
	public synchronized void setState(State newState) throws IllegalStateException {
		State oldState = state;
		if (newState.equals(state))
			return;
		switch (newState) {
		case running:
			switch (state) {
			case prepared:
			case paused:
				stateLastModified = new Date();
				state = newState;
				notifyFacadeListeners(oldState, newState);
				return;
			default:
				throw new IllegalStateException("Can't change state from "
						+ state + " to " + newState);
			}
		case paused:
			switch (state) {
			case running:
				stateLastModified = new Date();
				state = newState;
				notifyFacadeListeners(oldState, newState);
				return;
			default:
				throw new IllegalStateException("Can't change state from "
						+ state + " to " + newState);
			}
		case completed:
			switch (state) {
			case running:
				stateLastModified = new Date();
				state = newState;
				notifyFacadeListeners(oldState, newState);
				return;
			case cancelled:
				// Keep as cancelled
				return;
			default:
				throw new IllegalStateException("Can't change state from "
						+ state + " to " + newState);
			}
		case cancelled:
			switch (state) {
			case completed:
				throw new IllegalStateException("Can't change state from "
						+ state + " to " + newState);
			default:
				stateLastModified = new Date();
				state = newState;
				notifyFacadeListeners(oldState, newState);
				return;
			}
		default:
			throw new IllegalStateException("Can't change state from " + state  + " to " + newState);		
		}		
	}

	private void notifyFacadeListeners(State oldState, State newState) {
		for (FacadeListener facadeListener : copyList(facadeListeners))
			try {
				facadeListener.stateChange(this, oldState, newState);
			} catch (RuntimeException ex) {
				logger.warn("Could not notify facade listener "
						+ facadeListener, ex);
			}
	}

	@Override
	public synchronized boolean cancelWorkflowRun() {
		if (getState().equals(State.completed))
			return false;
		boolean result = Stop.cancelWorkflow(getWorkflowRunId());
		if (result) {
			setState(State.cancelled);
			logger.info("Cancelled workflow runId=" + getWorkflowRunId()
					+ " processId=" + instanceOwningProcessId);
			for (FacadeListener facadeListener : copyList(facadeListeners))
				try {
					facadeListener.workflowFailed(this, "Workflow was cancelled",
							new WorkflowRunCancellation(getWorkflowRunId()));
				} catch (RuntimeException ex) {
					logger.warn("Could not notify failure listener "
							+ facadeListener, ex);
				}
			checkWorkflowFinished(WorkflowInstanceFacadeChange.CANCELLATION);		
		}
		return result;
	}

	@Override
	public boolean pauseWorkflowRun() {
		setState(State.paused);
		if (Stop.pauseWorkflow(getWorkflowRunId())) {
			logger.info("Paused workflow runId=" + getWorkflowRunId()
					+ " processId=" + instanceOwningProcessId);
			return true;
		}
		return false;
	}

	@Override
	public boolean resumeWorkflowRun() {
		setState(State.running);
		if (Stop.resumeWorkflow(getWorkflowRunId())) {
			logger.info("Resumed paused workflow runId=" + getWorkflowRunId()
					+ " processId=" + instanceOwningProcessId);
			return true;
		}
		return false;
	}

	@Override
	public String getIdentifier() {
		return localName;
	}
}
