package uk.org.taverna.platform.run.api;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.execution.api.InvalidExecutionIdException;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Service for managing runs of Taverna workflows.
 *
 * @author David Withers
 */
public interface RunService {

	public static final String EVENT_TOPIC_ROOT = "uk/org/taverna/platform/run/RunService/";
	public static final String RUN_CREATED = EVENT_TOPIC_ROOT + "RUN_CREATED";
	public static final String RUN_DELETED = EVENT_TOPIC_ROOT + "RUN_DELETED";
	public static final String RUN_STARTED = EVENT_TOPIC_ROOT + "RUN_STARTED";
	public static final String RUN_STOPPED = EVENT_TOPIC_ROOT + "RUN_STOPPED";
	public static final String RUN_PAUSED = EVENT_TOPIC_ROOT + "RUN_PAUSED";
	public static final String RUN_RESUMED = EVENT_TOPIC_ROOT + "RUN_RESUMED";

	/**
	 * Returns the available <code>ExecutionEnvironment</code>s.
	 *
	 * @return the available <code>ExecutionEnvironment</code>s
	 */
	public Set<ExecutionEnvironment> getExecutionEnvironments();

	/**
	 * Returns the <code>ExecutionEnvironment</code>s that can execute the specified
	 * <code>WorkflowBundle</code> using its default <code>Profile</code>.
	 *
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> to find <code>ExecutionEnvironment</code>s for
	 * @return the <code>ExecutionEnvironment</code>s that can execute the specified
	 *         <code>WorkflowBundle</code>
	 */
	public Set<ExecutionEnvironment> getExecutionEnvironments(WorkflowBundle workflowBundle);

	/**
	 * Returns the <code>ExecutionEnvironment</code>s that can execute the specified
	 * <code>Profile</code>.
	 *
	 * @param profile
	 *            the <code>Profile</code> to find <code>ExecutionEnvironment</code>s for
	 * @return the <code>ExecutionEnvironment</code>s that can execute the specified
	 *         <code>Profile</code>
	 */
	public Set<ExecutionEnvironment> getExecutionEnvironments(Profile profile);

	/**
	 * Creates a new run and returns the ID for the run.
	 *
	 * To start the run use the {@link #start(String)} method.
	 *
	 * @param runProfile
	 *            the workflow to run
	 * @return the run ID
	 * @throws InvalidWorkflowException
	 * @throws RunProfileException
	 */
	public String createRun(RunProfile runProfile) throws InvalidWorkflowException,
			RunProfileException;

	/**
	 * Returns the list of runs that this service is managing.
	 * <p>
	 * If there are no runs this method returns an empty list.
	 *
	 * @return the list of runs that this service is managing
	 */
	public List<String> getRuns();

	/**
	 * Opens a run and returns the ID for the run.
	 *
	 * @param runFile
	 *            the workflow run to open
	 * @return the run ID
	 * @throws InvalidWorkflowException
	 * @throws RunProfileException
	 */
	public String open(File runFile) throws IOException;

	/**
	 * Closes a run.
	 *
	 * @param runID
	 *            the ID of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 * @throws InvalidExecutionIdException
	 */
	public void close(String runID) throws InvalidRunIdException, InvalidExecutionIdException;

	/**
	 * Saves a run.
	 *
	 * @param runID
	 *            the ID of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 * @throws InvalidExecutionIdException
	 */
	public void save(String runID) throws InvalidRunIdException, InvalidExecutionIdException;

	/**
	 * Deletes a run.
	 *
	 * @param runID
	 *            the ID of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 * @throws InvalidExecutionIdException
	 */
	public void delete(String runID) throws InvalidRunIdException, InvalidExecutionIdException;

	/**
	 * Starts a run.
	 *
	 * @param runID
	 *            the ID of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 * @throws RunStateException
	 *             if the run state is not CREATED
	 * @throws InvalidExecutionIdException
	 */
	public void start(String runID) throws InvalidRunIdException, RunStateException,
			InvalidExecutionIdException;

	/**
	 * Pauses a running run.
	 *
	 * @param runID
	 *            the ID of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 * @throws RunStateException
	 *             if the run state is not RUNNING
	 * @throws InvalidExecutionIdException
	 */
	public void pause(String runID) throws InvalidRunIdException, RunStateException,
			InvalidExecutionIdException;

	/**
	 * Resumes a paused run.
	 *
	 * @param runID
	 *            the ID of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 * @throws RunStateException
	 *             if the run state is not PAUSED
	 * @throws InvalidExecutionIdException
	 */
	public void resume(String runID) throws InvalidRunIdException, RunStateException,
			InvalidExecutionIdException;

	/**
	 * Cancels a running or paused run.
	 *
	 * @param runID
	 *            the ID of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 * @throws RunStateException
	 *             if the run state is not RUNNING or PAUSED
	 * @throws InvalidExecutionIdException
	 */
	public void cancel(String runID) throws InvalidRunIdException, RunStateException,
			InvalidExecutionIdException;

	/**
	 * Returns the current state of the run.
	 *
	 * A run's state can be CREATED, RUNNING, COMPLETED, PAUSED, CANCELLED or FAILED.
	 *
	 * @param runID
	 *            the ID of the run
	 * @return the current state of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public State getState(String runID) throws InvalidRunIdException;

	/**
	 * Returns the <code>Bundle</code> containing the inputs of the run.
	 * <p>
	 * May be null if there are no inputs.
	 *
	 * @param runID
	 *            the ID of the run
	 * @return the <code>Databundle</code> containing the inputs of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public Bundle getInputs(String runID) throws InvalidRunIdException;

	/**
	 * Returns the <code>Bundle</code> containing the outputs of the run.
	 * <p>
	 * May be null if there are no outputs.
	 *
	 * @param runID
	 *            the ID of the run
	 * @return the <code>Databundle</code> containing the outputs of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public Bundle getOutputs(String runID) throws InvalidRunIdException;

	/**
	 * Returns the status report for the run.
	 *
	 * @param runID
	 *            the ID of the run
	 * @return the status report for the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public WorkflowReport getWorkflowReport(String runID) throws InvalidRunIdException;

	public Workflow getWorkflow(String runID) throws InvalidRunIdException;

	public Profile getProfile(String runID) throws InvalidRunIdException;

}