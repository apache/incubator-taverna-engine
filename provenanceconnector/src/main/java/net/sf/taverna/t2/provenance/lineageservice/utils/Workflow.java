package net.sf.taverna.t2.provenance.lineageservice.utils;

public class Workflow {

	private String workflowId;
	private String parentWorkflowId;
	private String externalName;
	
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setParentWorkflowId(String parentIdentifier) {
		this.parentWorkflowId = parentIdentifier;
	}

	public String getParentWorkflowId() {
		return parentWorkflowId;
	}

	/**
	 * @return the externalName
	 */
	public String getExternalName() {
		return externalName;
	}

	/**
	 * @param externalName the externalName to set
	 */
	public void setExternalName(String externalName) {
		this.externalName = externalName;
	}
	
}
