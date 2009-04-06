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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.taverna.t2.facade.FailureListener;
import net.sf.taverna.t2.facade.ResultListener;
import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.TokenOrderException;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.monitor.MonitorManager;
import net.sf.taverna.t2.monitor.MonitorNode;
import net.sf.taverna.t2.monitor.MonitorableProperty;
import net.sf.taverna.t2.provenance.item.DataflowRunComplete;
import net.sf.taverna.t2.provenance.item.WorkflowDataProvenanceItem;
import net.sf.taverna.t2.provenance.item.WorkflowProvenanceItem;
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
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.DispatchStack;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.IntermediateProvenance;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Invoke;

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

	public InvocationContext getContext() {
		return context;
	}

	private Dataflow dataflow;
	private ResultListener facadeResultListener;
	private String instanceOwningProcessId;
	private String localName;
	private MonitorManager monitorManager = MonitorManager.getInstance();
	private boolean pushDataCalled = false;
	protected List<FailureListener> failureListeners = new ArrayList<FailureListener>();
	protected List<ResultListener> resultListeners = new ArrayList<ResultListener>();

	private boolean provEnabled = false;

	public WorkflowInstanceFacadeImpl(final Dataflow dataflow,
			InvocationContext context, String parentProcess)
			throws InvalidDataflowException {
		DataflowValidationReport report = dataflow.checkValidity();
		if (!report.isValid()) {
			throw new InvalidDataflowException(dataflow, report);
		}

		this.dataflow = dataflow;
		this.context = context;
		this.localName = "facade" + owningProcessId.getAndIncrement();
		if (parentProcess.equals("")) {
			this.instanceOwningProcessId = localName;
			// Add this WorkflowInstanceFacade to the map of all workflow run IDs 
			// against the corresponding WorkflowInstanceFacadeS/ 
			WorkflowInstanceFacade.workflowRunFacades.put(localName, this);
			// Note that we do not put the IDs for nested workflows, just for the main ones!
		} else {
			this.instanceOwningProcessId = parentProcess + ":" + localName;
		}

		WorkflowProvenanceItem workflowItem = null;
		if (context.getProvenanceConnector() != null) {

			provEnabled = true;
			workflowItem = new WorkflowProvenanceItem();
			workflowItem.setDataflow(dataflow);
			workflowItem.setProcessId(instanceOwningProcessId);
			workflowItem.setIdentifier(UUID.randomUUID().toString());
			workflowItem.setParentId(dataflow.getInternalIdentier());

			addProvenanceLayerToProcessors(dataflow, workflowItem);
			context.getProvenanceConnector().addProvenanceItem(workflowItem, context);
		}
		facadeResultListener = new FacadeResultListener(dataflow, workflowItem);
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
				if (! (layers.get(j) instanceof Invoke)) {
					continue;
				}
				DispatchLayer<?> provenance = new IntermediateProvenance();
				IntermediateProvenance intermediateProvenance = (IntermediateProvenance) provenance;
				intermediateProvenance.setWorkflow(workflowItem);
				intermediateProvenance.setConnector(context
						.getProvenanceConnector());

				Edits edits = EditsRegistry.getEdits();
				try {
					edits.getAddDispatchLayerEdit(dispatchStack, provenance,
							j - 1).doEdit();
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

	public synchronized void addResultListener(ResultListener listener) {
		if (resultListeners.isEmpty()) {
			for (DataflowOutputPort port : dataflow.getOutputPorts()) {
				port.addResultListener(facadeResultListener);
			}
		}
		resultListeners.add(listener);
	}

	public void fire() throws IllegalStateException {
		if (pushDataCalled)
			throw new IllegalStateException(
					"Data has already been pushed, fire must be called first!");
		monitorManager.registerNode(this, instanceOwningProcessId.split(":"),
				new HashSet<MonitorableProperty<?>>());
		dataflow.fire(instanceOwningProcessId, context);
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
		// TODO: throw TokenOrderException when token stream is violates order
		// constraints.
		for (DataflowInputPort port : dataflow.getInputPorts()) {
			if (portName.equals(port.getName())) {
				port.receiveEvent(token.pushOwningProcess(localName));
			}
		}
		pushDataCalled = true;
	}

	public void removeFailureListener(FailureListener listener) {
		failureListeners.remove(listener);
	}

	public synchronized void removeResultListener(ResultListener listener) {
		resultListeners.remove(listener);
		if (resultListeners.isEmpty()) {
			for (DataflowOutputPort port : dataflow.getOutputPorts()) {
				port.removeResultListener(facadeResultListener);
			}
		}
	}

	protected class FacadeResultListener implements ResultListener {
		private int portsToComplete;
		private final WorkflowProvenanceItem workflowItem;

		public FacadeResultListener(Dataflow dataflow,
				WorkflowProvenanceItem workflowItem) {
			this.workflowItem = workflowItem;
			portsToComplete = dataflow.getOutputPorts().size();
		}

		public void resultTokenProduced(WorkflowDataToken token, String portName) {
			if (!instanceOwningProcessId.equals(token.getOwningProcess())) {
				return;
			}
			if (provEnabled) {
			WorkflowDataProvenanceItem workflowDataProvenanceItem = new WorkflowDataProvenanceItem(
					portName, token.getData(), context.getReferenceService());
				workflowDataProvenanceItem.setParentId(workflowItem.getIdentifier());
				workflowDataProvenanceItem.setIdentifier(UUID.randomUUID().toString());
				workflowDataProvenanceItem.setParentId(instanceOwningProcessId);
				workflowDataProvenanceItem.setProcessId(instanceOwningProcessId);
				context.getProvenanceConnector().addProvenanceItem(
						workflowDataProvenanceItem, context);
			}
			synchronized (this) {
				if (token.getIndex().length == 0) {
					portsToComplete--;
				}
				if (portsToComplete == 0) {
					// Received complete events on all ports, can
					// un-register this node from the monitor
					MonitorManager.getInstance().deregisterNode(
							instanceOwningProcessId.split(":"));
					if (provEnabled) {
						DataflowRunComplete dataflowRunComplete = new DataflowRunComplete();
						dataflowRunComplete.setParentId(workflowItem.getIdentifier());
						dataflowRunComplete
								.setProcessId(instanceOwningProcessId);
						dataflowRunComplete.setIdentifier(UUID.randomUUID().toString());
						context.getProvenanceConnector().addProvenanceItem(
								dataflowRunComplete, context);
					}
				}
			}
			ArrayList<ResultListener> copyOfListeners = new ArrayList<ResultListener>(
					resultListeners);
			for (ResultListener resultListener : copyOfListeners) {
				try {
					resultListener.resultTokenProduced(
							token.popOwningProcess(), portName);
				} catch (RuntimeException ex) {
					logger.warn("Could not notify result listener "
							+ resultListener, ex);
				}
			}

		}
	}

}
