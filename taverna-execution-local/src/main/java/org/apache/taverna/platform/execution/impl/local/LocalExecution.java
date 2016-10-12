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

package org.apache.taverna.platform.execution.impl.local;

import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.taverna.facade.ResultListener;
import org.apache.taverna.facade.WorkflowInstanceFacade;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.TokenOrderException;
import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.monitor.MonitorManager;
import org.apache.taverna.provenance.reporter.ProvenanceReporter;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.InvalidDataflowException;

import org.apache.taverna.robundle.Bundle;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.databundle.DataBundles.ResolveOptions;
import org.apache.taverna.platform.capability.api.ActivityService;
import org.apache.taverna.platform.capability.api.DispatchLayerService;
import org.apache.taverna.platform.execution.api.AbstractExecution;
import org.apache.taverna.platform.execution.api.InvalidWorkflowException;
import org.apache.taverna.platform.report.ActivityReport;
import org.apache.taverna.platform.report.ProcessorReport;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * An {@link org.apache.taverna.platform.execution.api.Execution Execution} for
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
					Path port = DataBundles.getPort(inputs, portName);
					if (!DataBundles.isMissing(port)) {
						T2Reference identifier = referenceService.register(
								DataBundles.resolve(port, ResolveOptions.BYTES), 
								inputPort.getValue()
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
		} catch (IOException|UncheckedIOException e) {
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
