package net.sf.taverna.t2.provenance.lineageservice.utils;

import java.sql.Timestamp;

public class ProcessorEnactment {

	private Timestamp enactmentEnded;
	private Timestamp enactmentStarted;
	private String finalOutputsDataBindingId;
	private String initialInputsDataBindingId;
	private String iteration;
	private String parentProcessEnactmentId;
	private String processEnactmentId;
	private String processIdentifier;
	private String processorId;
	private String workflowRunId;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessorEnactment other = (ProcessorEnactment) obj;
		if (processEnactmentId == null) {
			if (other.processEnactmentId != null)
				return false;
		} else if (!processEnactmentId.equals(other.processEnactmentId))
			return false;
		return true;
	}

	public Timestamp getEnactmentEnded() {
		return enactmentEnded;
	}

	public Timestamp getEnactmentStarted() {
		return enactmentStarted;
	}

	public String getFinalOutputsDataBindingId() {
		return finalOutputsDataBindingId;
	}

	public String getInitialInputsDataBindingId() {
		return initialInputsDataBindingId;
	}

	public String getIteration() {
		return iteration;
	}

	public String getParentProcessEnactmentId() {
		return parentProcessEnactmentId;
	}

	public String getProcessEnactmentId() {
		return processEnactmentId;
	}

	public String getProcessIdentifier() {
		return processIdentifier;
	}

	public String getProcessorId() {
		return processorId;
	}

	public String getWorkflowRunId() {
		return workflowRunId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((processEnactmentId == null) ? 0 : processEnactmentId
						.hashCode());
		return result;
	}

	public void setEnactmentEnded(Timestamp enactmentEnded) {
		this.enactmentEnded = enactmentEnded;
	}

	public void setEnactmentStarted(Timestamp enactmentStarted) {
		this.enactmentStarted = enactmentStarted;
	}

	public void setFinalOutputsDataBindingId(String finalOutputsDataBindingId) {
		this.finalOutputsDataBindingId = finalOutputsDataBindingId;
	}

	public void setInitialInputsDataBindingId(String initialInputsDataBindingId) {
		this.initialInputsDataBindingId = initialInputsDataBindingId;
	}

	public void setIteration(String iteration) {
		this.iteration = iteration;
	}

	public void setParentProcessEnactmentId(
			String parentProcessEnactmentId) {
		this.parentProcessEnactmentId = parentProcessEnactmentId;
	}

	public void setProcessEnactmentId(String processEnactmentId) {
		this.processEnactmentId = processEnactmentId;
	}

	public void setProcessIdentifier(String processIdentifier) {
		this.processIdentifier = processIdentifier;
	}

	public void setProcessorId(String processorId) {
		this.processorId = processorId;
	}

	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessorEnactment [enactmentEnded=");
		builder.append(enactmentEnded);
		builder.append(", enactmentStarted=");
		builder.append(enactmentStarted);
		builder.append(", finalOutputs=");
		builder.append(finalOutputsDataBindingId);
		builder.append(", initialInputs=");
		builder.append(initialInputsDataBindingId);
		builder.append(", iteration=");
		builder.append(iteration);
		builder.append(", parentProcessEnactmentId=");
		builder.append(parentProcessEnactmentId);
		builder.append(", processEnactmentId=");
		builder.append(processEnactmentId);
		builder.append(", processIdentifier=");
		builder.append(processIdentifier);
		builder.append(", processorId=");
		builder.append(processorId);
		builder.append(", workflowRunId=");
		builder.append(workflowRunId);
		builder.append("]");
		return builder.toString();
	}
}
