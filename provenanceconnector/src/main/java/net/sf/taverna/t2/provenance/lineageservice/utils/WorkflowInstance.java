package net.sf.taverna.t2.provenance.lineageservice.utils;

import java.sql.Blob;

public class WorkflowInstance {

	private String instanceID;
	private String wfnameRef;  // this is the wfnameRef for the TOP LEVEL workflow for this run  CHECK
	private String timestamp;
	private String workflowExternalName;
	private byte[] dataflowBlob;

	public void setWorkflowIdentifier(String workflowIdentifier) {
		this.wfnameRef = workflowIdentifier;
	}

	public String getWorkflowIdentifier() {
		return wfnameRef;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the instanceID
	 */
	public String getInstanceID() {
		return instanceID;
	}

	/**
	 * @param instanceID the instanceID to set
	 */
	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
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
