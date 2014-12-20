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
package uk.org.taverna.platform.run.impl;

import java.io.File;
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

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.databundle.DataBundles;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.execution.api.ExecutionEnvironmentService;
import uk.org.taverna.platform.execution.api.InvalidExecutionIdException;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.report.ReportListener;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.platform.run.api.InvalidRunIdException;
import uk.org.taverna.platform.run.api.RunProfile;
import uk.org.taverna.platform.run.api.RunProfileException;
import uk.org.taverna.platform.run.api.RunService;
import uk.org.taverna.platform.run.api.RunStateException;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Implementation of the <code>RunService</code>.
 *
 * @author David Withers
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
	public String open(File runFile) throws IOException {
		try {
			String runID = runFile.getName();
			int dot = runID.indexOf('.');
			if (dot > 0)
				runID = runID.substring(0, dot);
			if (!runMap.containsKey(runID)) {
				Bundle bundle = DataBundles.openBundle(runFile.toPath());
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
	public void save(String runID, File runFile) throws InvalidRunIdException, IOException {
		Run run = getRun(runID);
		Bundle dataBundle = run.getDataBundle();
		try {
			DataBundles.closeAndSaveBundle(dataBundle, runFile.toPath());
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
