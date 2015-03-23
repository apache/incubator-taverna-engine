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

package org.apache.taverna.platform.run.impl;

import static java.util.logging.Level.WARNING;
import static org.apache.taverna.platform.report.State.CANCELLED;
import static org.apache.taverna.platform.report.State.COMPLETED;
import static org.apache.taverna.platform.report.State.CREATED;
import static org.apache.taverna.platform.report.State.FAILED;
import static org.apache.taverna.platform.report.State.PAUSED;
import static org.apache.taverna.platform.report.State.RUNNING;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.manifest.Manifest;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.platform.execution.api.ExecutionEnvironment;
import org.apache.taverna.platform.execution.api.InvalidExecutionIdException;
import org.apache.taverna.platform.execution.api.InvalidWorkflowException;
import org.apache.taverna.platform.report.State;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.platform.run.api.RunProfile;
import org.apache.taverna.platform.run.api.RunProfileException;
import org.apache.taverna.platform.run.api.RunStateException;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * A single run of a {@link Workflow}.
 * 
 * @author David Withers
 */
public class Run {
	private static final WorkflowReportJSON workflowReportJson = new WorkflowReportJSON();
	private static final Logger logger = Logger.getLogger(Run.class.getName());

	private final String ID, executionID;
	private final ExecutionEnvironment executionEnvironment;
	private final WorkflowReport workflowReport;
	private final WorkflowBundle workflowBundle;
	private final Bundle dataBundle;
	private final Workflow workflow;
	private final Profile profile;

	/**
	 * Constructs a <code>Run</code> from the specified <code>RunProfile</code>.
	 * 
	 * @param runProfile
	 *            the profile to create a <code>Run</code> from
	 * @throws InvalidWorkflowException
	 *             if the <code>Workflow</code> specified by the
	 *             <code>RunProfile</code> is not valid
	 * @throws RunProfileException
	 *             if the <code>RunProfile</code> does not contain the correct
	 *             information to run a <code>Workflow</code>
	 */
	public Run(RunProfile runProfile) throws InvalidWorkflowException,
			RunProfileException {
		if (runProfile.getWorkflowBundle() == null) {
			String message = "No WorkflowBundle specified in the RunProfile";
			logger.warning(message);
			throw new RunProfileException(message);
		}
		workflowBundle = runProfile.getWorkflowBundle();
		if (runProfile.getWorkflowName() == null) {
			if (workflowBundle.getMainWorkflow() == null) {
				String message = "No Workflow specified in either the RunProfile or the WorkflowBundle";
				logger.warning(message);
				throw new RunProfileException(message);
			}
			logger.info("No Workflow specified - using the main Workflow from the WorkflowBundle");
			workflow = workflowBundle.getMainWorkflow();
		} else {
			workflow = workflowBundle.getWorkflows().getByName(
					runProfile.getWorkflowName());
		}
		if (runProfile.getProfileName() == null) {
			if (workflowBundle.getMainProfile() == null) {
				String message = "No Profile specified in either the RunProfile or the WorkflowBundle";
				logger.warning(message);
				throw new RunProfileException(message);
			}
			logger.info("No Profile specified - using the main Profile from the WorkflowBundle");
			profile = workflowBundle.getMainProfile();
		} else {
			profile = workflowBundle.getProfiles().getByName(
					runProfile.getProfileName());
		}
		if (runProfile.getDataBundle() == null) {
			String message = "No DataBundle specified in the RunProfile";
			logger.warning(message);
			throw new RunProfileException(message);
		}
		dataBundle = runProfile.getDataBundle();
		try {
			DataBundles.setWorkflowBundle(dataBundle, workflowBundle);
		} catch (IOException e) {
			String message = "Could not save workflow bundle to data bundle";
			logger.log(WARNING, message, e);
			throw new InvalidWorkflowException(message, e);
		}
		if (runProfile.getExecutionEnvironment() == null) {
			String message = "No ExecutionEnvironment specified in the RunProfile";
			logger.warning(message);
			throw new RunProfileException(message);
		}
		executionEnvironment = runProfile.getExecutionEnvironment();

		ID = UUID.randomUUID().toString();
		executionID = executionEnvironment.getExecutionService()
				.createExecution(executionEnvironment, workflowBundle,
						workflow, profile, dataBundle);
		try {
			workflowReport = executionEnvironment.getExecutionService()
					.getWorkflowReport(executionID);
		} catch (InvalidExecutionIdException e) {
			String message = "Error while creating a execution on the "
					+ executionEnvironment.getName();
			logger.severe(message);
			throw new RuntimeException(message, e);
		}
	}

	public Run(String id, Bundle bundle) throws IOException, ReaderException,
			ParseException {
		this.ID = id;
		executionID = null;
		executionEnvironment = null;
		workflowReport = workflowReportJson.load(bundle);
		workflowBundle = DataBundles.getWorkflowBundle(bundle);
		dataBundle = bundle;
		workflow = workflowBundle.getMainWorkflow();
		profile = workflowBundle.getMainProfile();
	}

	/**
	 * Returns the identifier for this <code>Run</code>.
	 * 
	 * @return the identifier for this <code>Run</code>
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Returns the current {@link State} of the <code>Run</code>.
	 * 
	 * A <code>Run</code>'s state can be CREATED, RUNNING, COMPLETED, PAUSED,
	 * CANCELLED or FAILED.
	 * 
	 * @return the current <code>State</code> of the <code>Run</code>
	 */
	public State getState() {
		return workflowReport.getState();
	}

	/**
	 * Returns the <code>Bundle</code> containing the data values of the run.
	 * <p>
	 * 
	 * @return the <code>Bundle</code> containing the data values for the
	 *         <code>Workflow</code>
	 */
	public Bundle getDataBundle() {
		if (getWorkflowReport() != null)
			// Save the workflow report
			try {
				workflowReportJson.save(getWorkflowReport(), dataBundle);
			} catch (IOException e) {
				logger.log(WARNING,
						"Can't save workflow report to data bundle", e);
			}
		// Update manifest
		try {
			Manifest manifest = new Manifest(dataBundle);
			manifest.populateFromBundle();
			manifest.writeAsJsonLD();
		} catch (IOException e) {
			logger.log(WARNING, "Can't add manifest to data bundle", e);
		}
		return dataBundle;
	}

	/**
	 * Returns the status report for the run.
	 * 
	 * @return the status report for the run
	 */
	public WorkflowReport getWorkflowReport() {
		return workflowReport;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public Profile getProfile() {
		return profile;
	}

	/**
	 * Deletes a run.
	 * 
	 * @throws InvalidExecutionIdException
	 */
	public void delete() throws InvalidExecutionIdException {
		synchronized (workflowReport) {
			executionEnvironment.getExecutionService().delete(executionID);
		}
	}

	public void start() throws RunStateException, InvalidExecutionIdException {
		synchronized (workflowReport) {
			State state = workflowReport.getState();
			if (!state.equals(CREATED))
				throw new RunStateException("Cannot start a " + state + " run.");
			executionEnvironment.getExecutionService().start(executionID);
		}
	}

	public void pause() throws RunStateException, InvalidExecutionIdException {
		synchronized (workflowReport) {
			State state = workflowReport.getState();
			if (!state.equals(RUNNING))
				throw new RunStateException("Cannot pause a " + state + " run.");
			executionEnvironment.getExecutionService().pause(executionID);
			workflowReport.setPausedDate(new Date());
		}
	}

	public void resume() throws RunStateException, InvalidExecutionIdException {
		synchronized (workflowReport) {
			State state = workflowReport.getState();
			if (!state.equals(PAUSED))
				throw new RunStateException("Cannot resume a " + state
						+ " run.");
			executionEnvironment.getExecutionService().resume(executionID);
			workflowReport.setResumedDate(new Date());
		}
	}

	public void cancel() throws RunStateException, InvalidExecutionIdException {
		synchronized (workflowReport) {
			State state = workflowReport.getState();
			if (state.equals(CANCELLED) || state.equals(COMPLETED)
					|| state.equals(FAILED))
				throw new RunStateException("Cannot cancel a " + state
						+ " run.");
			executionEnvironment.getExecutionService().cancel(executionID);
			workflowReport.setCancelledDate(new Date());
		}
	}
}
