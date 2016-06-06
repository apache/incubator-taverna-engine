/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.provenance.lineageservice.utils;

import java.sql.Timestamp;

public class ProcessorEnactment {
	private Timestamp enactmentEnded;
	private Timestamp enactmentStarted;
	private String finalOutputsDataBindingId;
	private String initialInputsDataBindingId;
	private String iteration;
	private String parentProcessorEnactmentId;
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

	public String getParentProcessorEnactmentId() {
		return parentProcessorEnactmentId;
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

	public void setParentProcessorEnactmentId(String parentProcessorEnactmentId) {
		this.parentProcessorEnactmentId = parentProcessorEnactmentId;
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
		builder.append(parentProcessorEnactmentId);
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
