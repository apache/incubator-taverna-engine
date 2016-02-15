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

import java.io.IOException;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.platform.execution.api.ExecutionEnvironment;
import org.apache.taverna.platform.execution.api.ExecutionEnvironmentService;
import org.apache.taverna.platform.execution.api.InvalidExecutionIdException;
import org.apache.taverna.platform.execution.api.InvalidWorkflowException;
import org.apache.taverna.platform.report.ReportListener;
import org.apache.taverna.platform.report.State;
import org.apache.taverna.platform.report.WorkflowReport;
import org.apache.taverna.platform.run.api.InvalidRunIdException;
import org.apache.taverna.platform.run.api.RunProfile;
import org.apache.taverna.platform.run.api.RunProfileException;
import org.apache.taverna.platform.run.api.RunService;
import org.apache.taverna.platform.run.api.RunStateException;
import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * Implementation of the <code>RunService</code>.
 *
 */
public class RunServiceImpl implements RunService {
	private static final Logger logger = Logger.getLogger(RunServiceImpl.class.getName());
	private static SimpleDateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd_HHmmss");

	private final Map<String, Run> runMap;
	private ExecutionEnvironmentService executionEnvironmentService;
	private EventAdmin eventAdmin;

	public RunServiceImpl() {
		runMap = new TreeMap<>();
	}

	@Override
	public Set<ExecutionEnvironment> getExecutionEnvironments() {
		return executionEnvironmentService.getExecutionEnvironments();
	}

	@Override
	public Set<ExecutionEnvironment> getExecutionEnvironments(WorkflowBundle workflowBundle) {
		return getExecutionEnvironments(workflowBundle.getMainProfile());
	}

	@Override
	public Set<ExecutionEnvironment> getExecutionEnvironments(Profile profile) {
		return executionEnvironmentService.getExecutionEnvironments(profile);
	}

	@Override
	public List<String> getRuns() {
		return new ArrayList<>(runMap.keySet());
	}

	@Override
	public String createRun(RunProfile runProfile) throws InvalidWorkflowException, RunProfileException {
		Run run = new Run(runProfile);
		run.getWorkflowReport().addReportListener(new RunReportListener(run.getID()));
		runMap.put(run.getID(), run);
		postEvent(RUN_CREATED, run.getID());
		return run.getID();
	}

	@Override
	public String open(Path runFile) throws IOException {
		try {
			String runID = runFile.getFileName().toString();
			int dot = runID.indexOf('.');
			if (dot > 0)
				runID = runID.substring(0, dot);
			if (!runMap.containsKey(runID)) {
				Bundle bundle = DataBundles.openBundle(runFile);
				Run run = new Run(runID, bundle);
				runMap.put(run.getID(), run);
			}
			postEvent(RUN_OPENED, runID);
			return runID;
		} catch (ReaderException | ParseException e) {
			throw new IOException("Error opening file " + runFile, e);
		}
	}

	@Override
	public void close(String runID) throws InvalidRunIdException, InvalidExecutionIdException {
		Run run = getRun(runID);
		try {
			Bundle dataBundle = run.getDataBundle();
			DataBundles.closeBundle(dataBundle);
		} catch (IOException | ClosedFileSystemException e) {
			logger.log(Level.WARNING, "Error closing data bundle for run " + runID, e);
		}
		runMap.remove(runID);
		postEvent(RUN_CLOSED, runID);
	}

	@Override
	public void save(String runID, Path runFile) throws InvalidRunIdException, IOException {
		Run run = getRun(runID);
		Bundle dataBundle = run.getDataBundle();
		try {
			DataBundles.closeAndSaveBundle(dataBundle, runFile);
		} catch (InvalidPathException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void delete(String runID) throws InvalidRunIdException, InvalidExecutionIdException {
		Run run = getRun(runID);
		run.delete();
		Bundle dataBundle = run.getDataBundle();
		try {
			DataBundles.closeBundle(dataBundle);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Error closing data bundle for run " + runID, e);
		}
		runMap.remove(runID);
		postEvent(RUN_DELETED, runID);
	}

	@Override
	public void start(String runID) throws InvalidRunIdException, RunStateException, InvalidExecutionIdException {
		getRun(runID).start();
		postEvent(RUN_STARTED, runID);
	}

	@Override
	public void pause(String runID) throws InvalidRunIdException, RunStateException, InvalidExecutionIdException {
		getRun(runID).pause();
		postEvent(RUN_PAUSED, runID);
	}

	@Override
	public void resume(String runID) throws InvalidRunIdException, RunStateException, InvalidExecutionIdException {
		getRun(runID).resume();
		postEvent(RUN_RESUMED, runID);
	}

	@Override
	public void cancel(String runID) throws InvalidRunIdException, RunStateException, InvalidExecutionIdException {
		getRun(runID).cancel();
		postEvent(RUN_STOPPED, runID);
	}

	@Override
	public State getState(String runID) throws InvalidRunIdException {
		return getRun(runID).getState();
	}

	@Override
	public Bundle getDataBundle(String runID) throws InvalidRunIdException {
		return getRun(runID).getDataBundle();
	}

	@Override
	public WorkflowReport getWorkflowReport(String runID) throws InvalidRunIdException {
		return getRun(runID).getWorkflowReport();
	}

	@Override
	public Workflow getWorkflow(String runID) throws InvalidRunIdException {
		return getRun(runID).getWorkflow();
	}

	@Override
	public Profile getProfile(String runID) throws InvalidRunIdException {
		return getRun(runID).getProfile();
	}

	@Override
	public String getRunName(String runID) throws InvalidRunIdException {
		WorkflowReport workflowReport = getWorkflowReport(runID);
		return workflowReport.getSubject().getName() + "_" + ISO_8601.format(workflowReport.getCreatedDate());
	}

	private Run getRun(String runID) throws InvalidRunIdException {
		Run run = runMap.get(runID);
		if (run == null)
			throw new InvalidRunIdException("Run ID " + runID + " is not valid");
		return run;
	}

	private void postEvent(String topic, String runId) {
		HashMap<String, String> properties = new HashMap<>();
		properties.put("RUN_ID", runId);
		Event event = new Event(topic, properties);
		eventAdmin.postEvent(event);
	}

	public void setExecutionEnvironmentService(ExecutionEnvironmentService executionEnvironmentService) {
		this.executionEnvironmentService = executionEnvironmentService;
	}

	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	public void setWorkflowBundleIO(WorkflowBundleIO workflowBundleIO) {
		DataBundles.setWfBundleIO(workflowBundleIO);
	}

	private class RunReportListener implements ReportListener {
		private final String runId;

		public RunReportListener(String runId) {
			this.runId = runId;
		}

		@Override
		public void outputAdded(Path path, String portName, int[] index) {
		}

		@Override
		public void stateChanged(State oldState, State newState) {
			switch (newState) {
			case COMPLETED:
			case FAILED:
				postEvent(RUN_STOPPED, runId);
			default:
				break;
			}
		}
	}
}
