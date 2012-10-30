package uk.org.taverna.platform.run.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.org.taverna.platform.data.api.Data;
import uk.org.taverna.platform.data.api.DataLocation;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.execution.api.InvalidExecutionIdException;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Service for managing runs of Taverna workflows.
 *
 * @author David Withers
 */
public interface RunService {

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
	 * @param workflow
	 *            the workflow to run
	 * @param inputs
	 *            the workflow inputs
	 * @return the run ID
	 * @throws InvalidWorkflowException
	 * @throws RunProfileException
	 */
	public String createRun(RunProfile runProfile) throws InvalidWorkflowException,
			RunProfileException;

	/**
	 * Returns the list of runs that this service is managing.
	 *
	 * @return the list of runs that this service is managing
	 */
	public List<String> getRuns();

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
	 * Returns the inputs of the run. May be null if there are no inputs.
	 *
	 * @param runID
	 *            the ID of the run
	 * @return the inputs of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public Map<String, DataLocation> getInputs(String runID) throws InvalidRunIdException;

	/**
	 * Returns the outputs of the run. May be null if there are no outputs.
	 *
	 * @param runID
	 *            the ID of the run
	 * @return the outputs of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public Map<String, DataLocation> getOutputs(String runID) throws InvalidRunIdException;

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

}