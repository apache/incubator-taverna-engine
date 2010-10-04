package uk.org.taverna.platform.run.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import uk.org.taverna.platform.execution.InvalidWorkflowException;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

public interface RunService {

	public List<String> getRuns();

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
	 */
	public String createRun(Workflow workflow, Profile profile, Map<String, T2Reference> inputs,
			ReferenceService referenceService) throws InvalidWorkflowException;

	/**
	 * Starts a run.
	 * 
	 * @param runID
	 *            the ID of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 * @throws RunStateException
	 *             if the run state is not CREATED
	 */
	public void start(String runID) throws InvalidRunIdException, RunStateException;

	/**
	 * Pauses a running run.
	 * 
	 * @param runID
	 *            the ID of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 * @throws RunStateException
	 *             if the run state is not RUNNING
	 */
	public void pause(String runID) throws InvalidRunIdException, RunStateException;

	/**
	 * Resumes a paused run.
	 * 
	 * @param runID
	 *            the ID of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 * @throws RunStateException
	 *             if the run state is not PAUSED
	 */
	public void resume(String runID) throws InvalidRunIdException, RunStateException;

	/**
	 * Cancels a running or paused run.
	 * 
	 * @param runID
	 *            the ID of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 * @throws RunStateException
	 *             if the run state is not RUNNING or PAUSED
	 */
	public void cancel(String runID) throws InvalidRunIdException, RunStateException;

	/**
	 * Returns the current state of the run. A run's state can be CREATED,
	 * RUNNING, COMPLETED, PAUSED or CANCELLED.
	 * 
	 * @param runID
	 *            the ID of the run
	 * @return the current state of the run
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public State getState(String runID) throws InvalidRunIdException;

	public Map<String, T2Reference> getInputs(String runID) throws InvalidRunIdException;

	public Map<String, T2Reference> getOutputs(String runID) throws InvalidRunIdException;

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

	/**
	 * Returns the date that the run was created.
	 * 
	 * @param runID
	 *            the ID of the run
	 * @return the date that the run was created
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public Date getCreatedDate(String runID) throws InvalidRunIdException;

	/**
	 * Returns the date that the run was started. If the run has not been
	 * started then <code>null</code> is returned.
	 * 
	 * @param runID
	 *            the ID of the run
	 * @return the date that the run was started
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public Date getStartedDate(String runID) throws InvalidRunIdException;

	/**
	 * Returns the date that the run was last paused. If the run has never been
	 * paused then <code>null</code> is returned.
	 * 
	 * @param runID
	 *            the ID of the run
	 * @return the date that the run was last paused
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public Date getPausedDate(String runID) throws InvalidRunIdException;

	/**
	 * Returns the date that the run was last resumed. If the run has never been
	 * resumed then <code>null</code> is returned.
	 * 
	 * @param runID
	 *            the ID of the run
	 * @return the date that the run was last resumed
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public Date getResumedDate(String runID) throws InvalidRunIdException;

	/**
	 * Returns the date that the run was canceled. If the run has not been
	 * canceled then <code>null</code> is returned.
	 * 
	 * @param runID
	 *            the ID of the run
	 * @return the date that the run was canceled
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public Date getCancelledDate(String runID) throws InvalidRunIdException;

	/**
	 * Returns the date that the run completed. If the run has not completed then
	 * <code>null</code> is returned.
	 * 
	 * @param runID
	 *            the ID of the run
	 * @return the date the the run completed
	 * @throws InvalidRunIdException
	 *             if the run ID is not valid
	 */
	public Date getCompletedDate(String runID) throws InvalidRunIdException;

}