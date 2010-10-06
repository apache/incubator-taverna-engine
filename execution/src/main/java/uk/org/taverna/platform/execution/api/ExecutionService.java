package uk.org.taverna.platform.execution.api;

import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

public interface ExecutionService {

	/**
	 * Returns the identifier for this execution service.
	 * 
	 * @return the identifier for this execution service
	 */
	public String getID();

	/**
	 * Returns the name of this execution service.
	 * 
	 * @return the name of this execution service
	 */
	public String getName();

	/**
	 * Returns a description of this execution service.
	 * 
	 * @return a description of this execution service
	 */
	public String getDescription();

	/**
	 * Creates a workflow execution and returns its ID.
	 * 
	 * @param workflow
	 *            the workflow to execute
	 * @param profile
	 *            the profile to use to execute the workflow
	 * @param inputs
	 *            the inputs of the workflow
	 * @param referenceService
	 *            the reference service used to register the workflow inputs and
	 *            outputs
	 * @return the ID of the created workflow execution
	 * @throws InvalidWorkflowException
	 */
	public String createExecution(Workflow workflow, Profile profile,
			Map<String, T2Reference> inputs, ReferenceService referenceService)
			throws InvalidWorkflowException;

	/**
	 * Returns the workflow report for the specified execution.
	 * 
	 * @param executionID the ID of the execution
	 * @return the workflow report for this execution
	 */
	public WorkflowReport getWorkflowReport(String executionID) throws InvalidExecutionIdException;

	public void start(String executionID) throws InvalidExecutionIdException;

	public void pause(String executionID) throws InvalidExecutionIdException;

	public void resume(String executionID) throws InvalidExecutionIdException;

	public void cancel(String executionID) throws InvalidExecutionIdException;

}