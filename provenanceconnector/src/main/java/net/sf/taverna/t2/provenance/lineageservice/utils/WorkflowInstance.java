package net.sf.taverna.t2.provenance.lineageservice.utils;

public class WorkflowInstance {

        private String instanceID;
	
	private String wfnameRef;
	
	private String timestamp;

	public void setIdentifier(String identifier) {
		this.instanceID = identifier;
	}

	public String getIdentifier() {
		return instanceID;
	}

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
	

}
