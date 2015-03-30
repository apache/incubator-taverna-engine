package org.apache.taverna.provenance.lineageservice.utils;

import java.sql.Blob;

public class WorkflowRun {
	private String workflowRunId;
	/**
	 * this is the workflowId for the TOP LEVEL workflow for this run.
	 * <p>
	 * CHECK
	 */
	private String workflowId;
	private String timestamp;
	private String workflowExternalName;
	private byte[] dataflowBlob;

	public void setWorkflowId(String workflowIdentifier) {
		this.workflowId = workflowIdentifier;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the workflowRunId
	 */
	public String getWorkflowRunId() {
		return workflowRunId;
	}

	/**
	 * @param workflowRunId the workflowRunId to set
	 */
	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}

	/**
	 * @return the workflowExternalName
	 */
	public String getWorkflowExternalName() {
		return workflowExternalName;
	}

	/**
	 * @param workflowExternalName the workflowExternalName to set
	 */
	public void setWorkflowExternalName(String workflowExternalName) {
		this.workflowExternalName = workflowExternalName;
	}
	
	/**
	 * A {@link Blob} object representing the dataflow
	 * @param bs
	 */
	public void setDataflowBlob(byte[] bs) {
		this.dataflowBlob = bs;
	}

	public byte[] getDataflowBlob() {
		return dataflowBlob;
	}
}
