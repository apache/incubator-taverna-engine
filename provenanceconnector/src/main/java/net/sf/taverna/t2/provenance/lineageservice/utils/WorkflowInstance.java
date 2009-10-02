package net.sf.taverna.t2.provenance.lineageservice.utils;

public class WorkflowInstance {

        private String instanceID;
	
	private String wfnameRef;
	
	private String timestamp;



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
	

}
