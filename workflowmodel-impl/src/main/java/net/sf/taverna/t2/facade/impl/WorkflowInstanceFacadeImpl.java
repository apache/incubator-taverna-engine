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
package net.sf.taverna.t2.facade.impl;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.taverna.t2.facade.FailureListener;
import net.sf.taverna.t2.facade.ResultListener;
import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.facade.WorkflowRunCancellation;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.TokenOrderException;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.monitor.MonitorManager;
import net.sf.taverna.t2.monitor.MonitorNode;
import net.sf.taverna.t2.monitor.MonitorableProperty;
import net.sf.taverna.t2.provenance.item.DataflowRunComplete;
import net.sf.taverna.t2.provenance.item.WorkflowDataProvenanceItem;
import net.sf.taverna.t2.provenance.item.WorkflowProvenanceItem;
import net.sf.taverna.t2.provenance.reporter.ProvenanceReporter;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.WorkflowRunIdEntity;
import net.sf.taverna.t2.utility.TypedTreeModel;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.DataflowValidationReport;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.EditsRegistry;
import net.sf.taverna.t2.workflowmodel.InvalidDataflowException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorFinishedEvent;
import net.sf.taverna.t2.workflowmodel.impl.ProcessorImpl;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchStack;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.ErrorBounce;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.IntermediateProvenance;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Stop;

import org.apache.log4j.Logger;

/**
 * Implementation of {@link WorkflowInstanceFacade}
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 * @author Ian Dunlop
 * @author Alex Nenadic
 * 
 */
public class WorkflowInstanceFacadeImpl implements WorkflowInstanceFacade {

	private static Logger logger = Logger
			.getLogger(WorkflowInstanceFacadeImpl.class);

	protected static AtomicLong owningProcessId = new AtomicLong(0);

	private InvocationContext context;
	
	// Is workflow currently running
	private boolean isRunning = false;

	public InvocationContext getContext() {
		return context;
	}

	private Dataflow dataflow;
	private FacadeResultListener facadeResultListener;
	// How many processors have finished so far
	private int processorsToComplete;
	private String instanceOwningProcessId;
	private String localName;
	private MonitorManager monitorManager = MonitorManager.getInstance();
	private boolean pushDataCalled = false;
	protected List<FailureListener> failureListeners = Collections.synchronizedList(new ArrayList<FailureListener>());
	protected List<ResultListener> resultListeners = Collections.synchronizedList(new ArrayList<ResultListener>());

	private boolean provEnabled = false;
	
	private WeakHashMap<String, T2Reference> pushedDataMap = new WeakHashMap<String, T2Reference> ();

	// Id of this run
	private String workflowRunId;

	private Timestamp workflowStarted;

	private WorkflowProvenanceItem workflowItem = null;

	private int portsToComplete;

	public WorkflowInstanceFacadeImpl(final Dataflow dataflow,
			InvocationContext context, String parentProcess)
			throws InvalidDataflowException {
		DataflowValidationReport report = dataflow.checkValidity();
		if (!report.isValid()) {
			throw new InvalidDataflowException(dataflow, report);
		}

		this.dataflow = dataflow;
		this.context = context;
		this.portsToComplete = dataflow.getOutputPorts().size();
		this.processorsToComplete = dataflow.getProcessors().size();
		this.localName = "facade" + owningProcessId.getAndIncrement();
		// Set the wf run id
		workflowRunId = UUID.randomUUID().toString();
		if (parentProcess.equals("")) {
			// Top-level workflow
			
			// add top level workflow run so that reference service can generate
			// identifiers linked to our run
			context.addEntity(new WorkflowRunIdEntity(workflowRunId));
			this.instanceOwningProcessId = localName;
			
			// Add this WorkflowInstanceFacade to the map of all workflow run IDs 
			// against the corresponding WorkflowInstanceFacadeS/ - to be used
			// by DependencyActivity's such as API consumer and Beanshell
			workflowRunFacades.put(localName, new WeakReference<WorkflowInstanceFacade>(this));
			// Note that we do not put the IDs for nested workflows, just for the main ones!
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
			workflowItem.setParentId(dataflow.getInternalIdentifier());

			addProvenanceLayerToProcessors(dataflow, workflowItem);
			context.getProvenanceReporter().setSessionID(workflowRunId);

		}
		facadeResultListener = new FacadeResultListener(dataflow, workflowItem);
		
		// Register an observer with each of the processors
		for (Processor processor: dataflow.getProcessors()){			
			String expectedProcessId = instanceOwningProcessId + ":" + dataflow.getLocalName() + ":" + processor.getLocalName();
			ProcessorFinishedObserver observer = new ProcessorFinishedObserver(workflowItem, expectedProcessId);
			((ProcessorImpl) processor).addObserver(observer);
		}
	
	}

	private void addProvenanceLayerToProcessors(Dataflow dataflow2, WorkflowProvenanceItem workflowItem) {
		for (Processor processor : dataflow.getProcessors()) {
			DispatchStack dispatchStack = processor.getDispatchStack();
			List<DispatchLayer<?>> layers = dispatchStack.getLayers();
			boolean provAlreadyAdded = false;
			for (DispatchLayer<?> layer : layers) {
				if (layer instanceof IntermediateProvenance) {
					provAlreadyAdded = true;
				}
			}
			if (provAlreadyAdded) {
				continue;
			}
			for (int j = 0; j < layers.size(); j++) {
				if (! (layers.get(j) instanceof ErrorBounce)) {
					continue;
				}
				DispatchLayer<?> provenance = new IntermediateProvenance();
				IntermediateProvenance intermediateProvenance = (IntermediateProvenance) provenance;
				intermediateProvenance.setWorkflow(workflowItem);
				intermediateProvenance.setReporter(context
						.getProvenanceReporter());

				Edits edits = EditsRegistry.getEdits();
				try {
					edits.getAddDispatchLayerEdit(dispatchStack, provenance,
							j).doEdit();
					break;
				} catch (EditException e) {
					logger.warn("adding provenance layer to dispatch stack failed "
									+ e.toString());
				}

			}
		}
	}

	public void addFailureListener(FailureListener listener) {
		failureListeners.add(listener);
	}

	public void addResultListener(ResultListener listener) {
		synchronized (resultListeners) {
			if (resultListeners.isEmpty()) {
				for (DataflowOutputPort port : dataflow.getOutputPorts()) {
					port.addResultListener(facadeResultListener);
				}
			}
			resultListeners.add(listener); 
		}		
	}

	public void fire() throws IllegalStateException {
		if (pushDataCalled)
			throw new IllegalStateException(
					"Data has already been pushed, fire must be called first!");
		workflowStarted = new Timestamp(System.currentTimeMillis());
		if (provEnabled) {
			workflowItem.setInvocationStarted(workflowStarted);
			context.getProvenanceReporter().addProvenanceItem(workflowItem);
		}
		
		monitorManager.registerNode(this, instanceOwningProcessId.split(":"),				
				new HashSet<MonitorableProperty<?>>());
		dataflow.fire(instanceOwningProcessId, context);
		isRunning = true;		
	}

	public Dataflow getDataflow() {
		return dataflow;
	}

	public TypedTreeModel<MonitorNode> getStateModel() {
		// TODO WorkflowInstanceFacade.getStateModel not yet implemented
		return null;
	}

	public void pushData(WorkflowDataToken token, String portName)
			throws TokenOrderException {
		if (! isRunning) {
			throw new IllegalStateException("fire() has not yet been called, or workflow has been cancelled");
		}
		// TODO: throw TokenOrderException when token stream is violates order
		// constraints.
		for (DataflowInputPort port : dataflow.getInputPorts()) {
			if (portName.equals(port.getName())) {
				if (token.getIndex().length == 0) {
					if (pushedDataMap.containsKey(portName)) {
						throw new IllegalStateException("Already pushed for port " + portName);
					}
					pushedDataMap.put(portName, token.getData());					
				}
				if (provEnabled) {
					WorkflowDataProvenanceItem workflowDataProvenanceItem = new WorkflowDataProvenanceItem();
					workflowDataProvenanceItem.setPortName(portName);
					workflowDataProvenanceItem.setInputPort(true);
					workflowDataProvenanceItem.setData(token.getData());
					workflowDataProvenanceItem.setReferenceService(context.getReferenceService());
					workflowDataProvenanceItem.setParentId(workflowItem.getIdentifier());
					workflowDataProvenanceItem.setWorkflowId(workflowItem.getParentId());
					workflowDataProvenanceItem.setIdentifier(UUID.randomUUID().toString());
					workflowDataProvenanceItem.setParentId(instanceOwningProcessId);
					workflowDataProvenanceItem.setProcessId(instanceOwningProcessId);
					workflowDataProvenanceItem.setIndex(token.getIndex());
					workflowDataProvenanceItem.setFinal(token.isFinal());
					context.getProvenanceReporter().addProvenanceItem(
							workflowDataProvenanceItem);
				}
				port.receiveEvent(token.pushOwningProcess(localName));
			}
		}
		pushDataCalled = true;
	}

	public void removeFailureListener(FailureListener listener) {
		failureListeners.remove(listener);
	}

	public void removeResultListener(ResultListener listener) {
		synchronized (resultListeners) {
			resultListeners.remove(listener);
			if (resultListeners.isEmpty()) {
				for (DataflowOutputPort port : dataflow.getOutputPorts()) {
					port.removeResultListener(facadeResultListener);
				}
			}
		}
	}

	protected class FacadeResultListener implements ResultListener {
		private final WorkflowProvenanceItem workflowItem;

		public FacadeResultListener(Dataflow dataflow,
				WorkflowProvenanceItem workflowItem) {
			this.workflowItem = workflowItem;
			
		}

		public void resultTokenProduced(WorkflowDataToken token, String portName) {
			if (!instanceOwningProcessId.equals(token.getOwningProcess())) {
				return;
			}
			if (provEnabled) {
				WorkflowDataProvenanceItem workflowDataProvenanceItem = new WorkflowDataProvenanceItem();
				workflowDataProvenanceItem.setPortName(portName);
				workflowDataProvenanceItem.setInputPort(false);
				workflowDataProvenanceItem.setData(token.getData());
				workflowDataProvenanceItem.setReferenceService(context.getReferenceService());
				workflowDataProvenanceItem.setParentId(workflowItem.getIdentifier());
				workflowDataProvenanceItem.setWorkflowId(workflowItem.getParentId());
				workflowDataProvenanceItem.setIdentifier(UUID.randomUUID().toString());
				workflowDataProvenanceItem.setParentId(instanceOwningProcessId);
				workflowDataProvenanceItem.setProcessId(instanceOwningProcessId);
				workflowDataProvenanceItem.setIndex(token.getIndex());
				workflowDataProvenanceItem.setFinal(token.isFinal());
				context.getProvenanceReporter().addProvenanceItem(
						workflowDataProvenanceItem);				
			}
			
			if (token.getIndex().length == 0) {
				synchronized (WorkflowInstanceFacadeImpl.this) {
					portsToComplete--;
				}
			}
			ArrayList<ResultListener> copyOfListeners;
			synchronized (resultListeners) {
				copyOfListeners = new ArrayList<ResultListener>(resultListeners);
			}			
			for (ResultListener resultListener : copyOfListeners) {
				try {
					resultListener.resultTokenProduced(
							token.popOwningProcess(), portName);
				} catch (RuntimeException ex) {
					logger.warn("Could not notify result listener "
							+ resultListener, ex);
				}
			}
			checkWorkflowFinished();

		}
	}
	

	/**
	 * An observer of events that occur when a processor finishes with execution.
	 *
	 */
	private class ProcessorFinishedObserver implements Observer<ProcessorFinishedEvent>{

		private WorkflowProvenanceItem workflowItem;
		private final String expectedProcessId;

		public ProcessorFinishedObserver(WorkflowProvenanceItem workflowItem, String expectedProcessId) {
			this.workflowItem = workflowItem;
			this.expectedProcessId = expectedProcessId;
		}

		public void notify(Observable<ProcessorFinishedEvent> sender,
				ProcessorFinishedEvent message) throws Exception {
			if (! message.getOwningProcess().equals(expectedProcessId)) {
				return;
			}
			synchronized(WorkflowInstanceFacadeImpl.this) {
				processorsToComplete--;
			}
			
			// De-register the processor node from the monitor as it has finished
			monitorManager.deregisterNode(message.getOwningProcess());
			
			// De-register this observer from the processor
			message.getProcessor().removeObserver(this);
			
			// All processors have finished => the workflow run has finished
			checkWorkflowFinished();
		}
	}

	private synchronized void checkWorkflowFinished() {
		if (processorsToComplete > 0 || portsToComplete > 0) { 
			return;
		}
		if (processorsToComplete < 0 || portsToComplete < 0) {
			logger.error("Already finished workflow", new IllegalStateException());
			return;
		}
		// De-register the workflow node from the monitor
		monitorManager.deregisterNode(instanceOwningProcessId + ":" + dataflow.getLocalName());

		// De-register this facade node from the monitor - this will effectively
		// tell the monitor that the workflow run has finished
		monitorManager.deregisterNode(instanceOwningProcessId);
		
		
		if (provEnabled) {
			DataflowRunComplete dataflowRunComplete = new DataflowRunComplete();
			dataflowRunComplete.setInvocationEnded(new Timestamp(System.currentTimeMillis()));
			dataflowRunComplete.setParentId(workflowItem
					.getIdentifier());
			dataflowRunComplete.setWorkflowId(workflowItem.getIdentifier());
			dataflowRunComplete
					.setProcessId(instanceOwningProcessId);
			dataflowRunComplete.setIdentifier(UUID.randomUUID()
					.toString());
			context.getProvenanceReporter().addProvenanceItem(
					dataflowRunComplete);
		}
		processorsToComplete = -1;
		portsToComplete = -1;
		
	}


	public WeakHashMap<String, T2Reference> getPushedDataMap() {
		return pushedDataMap;
	}

	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}

	public String getWorkflowRunId() {
		return workflowRunId;
	}
	
	
	public boolean isRunning(){
			return this.isRunning;
	}
	
	public void setIsRunning(boolean isRunning){
		this.isRunning = isRunning;
	}

	public synchronized boolean cancelWorkflowRun() {
		this.isRunning = false;
		boolean result = Stop.cancelWorkflow(getWorkflowRunId());
		if (result) {
			List<FailureListener> copyOfListeners = null;
			synchronized (failureListeners) {
				copyOfListeners = new ArrayList<FailureListener>(failureListeners);
			}			
			for (FailureListener failureListener : copyOfListeners) {
				try {
					failureListener.workflowFailed("Workflow was cancelled", new WorkflowRunCancellation(getWorkflowRunId()));
				} catch (RuntimeException ex) {
					logger.warn("Could not notify failure listener "
							+ failureListener, ex);
				}
			}
			processorsToComplete = 0;
			portsToComplete = 0;
			checkWorkflowFinished();		
		}
		return result;
	}

	public boolean pauseWorkflowRun() {
		return Stop.pauseWorkflow(getWorkflowRunId());
	}

	public boolean resumeWorkflowRun() {
		return Stop.resumeWorkflow(getWorkflowRunId());
	}

}
