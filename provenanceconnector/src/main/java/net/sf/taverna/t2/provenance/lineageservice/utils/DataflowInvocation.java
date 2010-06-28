package net.sf.taverna.t2.provenance.lineageservice.utils;

import java.sql.Timestamp;

public class DataflowInvocation {
	private String dataflowInvocationId;	
	private String workflowId;
	private String workflowRunId;
	private String parentProcessorEnactmentId;
	private String inputsDataBindingId;
	private String outputsDataBindingId;
	private Timestamp invocationEnded;
	private Timestamp invocationStarted;
	private boolean completed;	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((dataflowInvocationId == null) ? 0 : dataflowInvocationId
						.hashCode());
		return result;
	}
	@Override
	public String toString() {
		return "DataflowInvocation [dataflowInvocationId="
				+ dataflowInvocationId + ", parentProcessorEnactmentId="
				+ parentProcessorEnactmentId + ", workflowRunId="
				+ workflowRunId + "]";
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataflowInvocation other = (DataflowInvocation) obj;
		if (dataflowInvocationId == null) {
			if (other.dataflowInvocationId != null)
				return false;
		} else if (!dataflowInvocationId.equals(other.dataflowInvocationId))
			return false;
		return true;
	}
	public String getDataflowInvocationId() {
		return dataflowInvocationId;
	}
	public void setDataflowInvocationId(String dataflowInvocationId) {
		this.dataflowInvocationId = dataflowInvocationId;
	}
	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public String getWorkflowRunId() {
		return workflowRunId;
	}
	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}
	public String getParentProcessorEnactmentId() {
		return parentProcessorEnactmentId;
	}
	public void setParentProcessorEnactmentId(String parentProcessorEnactmentId) {
		this.parentProcessorEnactmentId = parentProcessorEnactmentId;
	}
	public String getInputsDataBindingId() {
		return inputsDataBindingId;
	}
	public void setInputsDataBindingId(String inputsDataBindingId) {
		this.inputsDataBindingId = inputsDataBindingId;
	}
	public String getOutputsDataBindingId() {
		return outputsDataBindingId;
	}
	public void setOutputsDataBindingId(String outputsDataBindingId) {
		this.outputsDataBindingId = outputsDataBindingId;
	}
	public Timestamp getInvocationEnded() {
		return invocationEnded;
	}
	public void setInvocationEnded(Timestamp invocationEnded) {
		this.invocationEnded = invocationEnded;
	}
	public Timestamp getInvocationStarted() {
		return invocationStarted;
	}
	public void setInvocationStarted(Timestamp invocationStarted) {
		this.invocationStarted = invocationStarted;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;		
	}
	public boolean getCompleted() {
		return completed;
	}

}
