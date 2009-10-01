package net.sf.taverna.t2.provenance.lineageservice.utils;


public class Collection {
	private String identifier;
	
	private String parentIdentifier;
	
	private String workflowIdentifier;
	
	private String processorName;
	
	private String varName;
	
	private String iteration;
	
	private String collId;
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getParentIdentifier() {
		return parentIdentifier;
	}

	public void setParentIdentifier(String parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}

	public String getWorkflowIdentifier() {
		return workflowIdentifier;
	}

	public void setWorkflowIdentifier(String workflowIdentifier) {
		this.workflowIdentifier = workflowIdentifier;
	}

	public String getProcessorName() {
		return processorName;
	}

	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getIteration() {
		return iteration;
	}

	public void setIteration(String iteration) {
		this.iteration = iteration;
	}

	public void setCollId(String collId) {
		this.collId = collId;
	}

	public String getCollId() {
		return collId;
	}

}
