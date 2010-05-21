/**
 * 
 */
package net.sf.taverna.t2.facade;

/**
 * 
 * A WorkflowRunCancellation is passed to listeners when a workflow run is cancelled.
 * @author alanrw
 *
 */
public class WorkflowRunCancellation extends Throwable {
	
	/**
	 * The id of the workflow run that was cancelled
	 */
	private String cancelledWorkflowRunId;
	
	public WorkflowRunCancellation (String runId) {
		cancelledWorkflowRunId = runId;
	}

	/**
	 * @return the id of the workflow run that was cancelled.
	 */
	public String getCancelledWorkflowRunId() {
		return cancelledWorkflowRunId;
	}
	

}
