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
package uk.org.taverna.platform.execution.impl.local;

import static java.util.logging.Level.SEVERE;
import static uk.org.taverna.platform.execution.impl.local.T2ReferenceConverter.convertPathToObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.sf.taverna.t2.facade.ResultListener;
import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.TokenOrderException;
import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.monitor.MonitorManager;
import net.sf.taverna.t2.provenance.reporter.ProvenanceReporter;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.InvalidDataflowException;

import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.platform.capability.api.ActivityService;
import org.apache.taverna.platform.capability.api.DispatchLayerService;
import uk.org.taverna.platform.execution.api.AbstractExecution;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.report.ActivityReport;
import uk.org.taverna.platform.report.ProcessorReport;
import uk.org.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * An {@link uk.org.taverna.platform.execution.api.Execution Execution} for
 * executing Taverna workflows on a local Taverna Dataflow Engine.
 * 
 * @author David Withers
 */
public class LocalExecution extends AbstractExecution implements ResultListener {

	private static Logger logger = Logger.getLogger(LocalExecution.class
			.getName());

	private final WorkflowToDataflowMapper mapping;

	private final WorkflowInstanceFacade facade;

	private final LocalExecutionMonitor executionMonitor;

	private final ReferenceService referenceService;

	private final Map<String, DataflowInputPort> inputPorts = new HashMap<String, DataflowInputPort>();

	/**
	 * Constructs an Execution for executing Taverna workflows on a local
	 * Taverna Dataflow Engine.
	 * 
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the
	 *            <code>Workflow</code>s required for execution
	 * @param workflow
	 *            the <code>Workflow</code> to execute
	 * @param profile
	 *            the <code>Profile</code> to use when executing the
	 *            <code>Workflow</code>
	 * @param dataBundle
	 *            the <code>Bundle</code> containing the data values for the
	 *            <code>Workflow</code>
	 * @param referenceService
	 *            the <code>ReferenceService</code> used to register inputs,
	 *            outputs and intermediate values
	 * @throws InvalidWorkflowException
	 *             if the specified workflow is invalid
	 */
	public LocalExecution(WorkflowBundle workflowBundle, Workflow workflow,
			Profile profile, Bundle dataBundle,
			ReferenceService referenceService, Edits edits,
			ActivityService activityService,
			DispatchLayerService dispatchLayerService)
			throws InvalidWorkflowException {
		super(workflowBundle, workflow, profile, dataBundle);
		this.referenceService = referenceService;
		try {
			mapping = new WorkflowToDataflowMapper(workflowBundle, profile,
					edits, activityService, dispatchLayerService);
			Dataflow dataflow = mapping.getDataflow(workflow);
			for (DataflowInputPort dataflowInputPort : dataflow.getInputPorts())
				inputPorts.put(dataflowInputPort.getName(), dataflowInputPort);
			facade = edits.createWorkflowInstanceFacade(dataflow,
					createContext(), "");
			executionMonitor = new LocalExecutionMonitor(getWorkflowReport(),
					getDataBundle(), mapping, facade.getIdentifier());
		} catch (InvalidDataflowException e) {
			throw new InvalidWorkflowException(e);
		}
	}

	@Override
	public void delete() {
		cancel();
	}

	@Override
	public void start() {
		MonitorManager.getInstance().addObserver(executionMonitor);
		/*
		 * have to add a result listener otherwise facade doesn't record when
		 * workflow is finished
		 */
		facade.addResultListener(this);
		facade.fire();
		try {
			if (DataBundles.hasInputs(getDataBundle())) {
				Path inputs = DataBundles.getInputs(getDataBundle());
				for (Entry<String, DataflowInputPort> inputPort : inputPorts
						.entrySet()) {
					String portName = inputPort.getKey();
					Path path = DataBundles.getPort(inputs, portName);
					if (!DataBundles.isMissing(path)) {
						T2Reference identifier = referenceService.register(
								convertPathToObject(path), inputPort.getValue()
										.getDepth(), true, null);
						int[] index = new int[] {};
						WorkflowDataToken token = new WorkflowDataToken("",
								index, identifier, facade.getContext());
						try {
							facade.pushData(token, portName);
						} catch (TokenOrderException e) {
							logger.log(SEVERE, "Unable to push data for input "
									+ portName, e);
						}
					}
				}
			}
		} catch (IOException e) {
			logger.log(SEVERE, "Error getting input data", e);
		}
	}

	@Override
	public void pause() {
		facade.pauseWorkflowRun();
	}

	@Override
	public void resume() {
		facade.resumeWorkflowRun();
	}

	@Override
	public void cancel() {
		facade.cancelWorkflowRun();
		facade.removeResultListener(this);
		MonitorManager.getInstance().removeObserver(executionMonitor);
	}

	@Override
	protected WorkflowReport createWorkflowReport(Workflow workflow) {
		return new WorkflowReport(workflow);
	}

	@Override
	public ProcessorReport createProcessorReport(
			org.apache.taverna.scufl2.api.core.Processor processor) {
		return new LocalProcessorReport(processor);
	}

	@Override
	public ActivityReport createActivityReport(
			org.apache.taverna.scufl2.api.activity.Activity activity) {
		return new ActivityReport(activity);
	}

	private InvocationContext createContext() {
		InvocationContext context = new InvocationContext() {
			private List<Object> entities = Collections
					.synchronizedList(new ArrayList<Object>());

			@Override
			public <T> List<T> getEntities(Class<T> entityType) {
				List<T> entitiesOfType = new ArrayList<>();
				synchronized (entities) {
					for (Object entity : entities)
						if (entityType.isInstance(entity))
							entitiesOfType.add(entityType.cast(entity));
				}
				return entitiesOfType;
			}

			@Override
			public void addEntity(Object entity) {
				entities.add(entity);
			}

			@Override
			public ReferenceService getReferenceService() {
				return referenceService;
			}

			@Override
			public ProvenanceReporter getProvenanceReporter() {
				return null;
			}

		};
		return context;
	}

	@Override
	public void resultTokenProduced(WorkflowDataToken token, String portName) {
	}
}
