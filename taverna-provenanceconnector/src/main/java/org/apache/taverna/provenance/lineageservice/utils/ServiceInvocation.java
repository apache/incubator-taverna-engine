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

public class ServiceInvocation {
	private ProcessorEnactment processorEnactment;
	private String workflowRunId;
	private long invocationNumber;
	private Timestamp invocationStarted;
	private Timestamp invocationEnded;
	private DataBinding inputs;
	private DataBinding outputs;
	private String failureT2Reference;
	private Activity activity;
	private String initiatingDispatchLayer;
	private String finalDispatchLayer;

	public ProcessorEnactment getProcessorEnactment() {
		return processorEnactment;
	}

	public void setProcessorEnactment(ProcessorEnactment processorEnactment) {
		this.processorEnactment = processorEnactment;
	}

	public String getWorkflowRunId() {
		return workflowRunId;
	}

	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}

	public long getInvocationNumber() {
		return invocationNumber;
	}

	public void setInvocationNumber(long invocationNumber) {
		this.invocationNumber = invocationNumber;
	}

	public Timestamp getInvocationStarted() {
		return invocationStarted;
	}

	public void setInvocationStarted(Timestamp invocationStarted) {
		this.invocationStarted = invocationStarted;
	}

	public Timestamp getInvocationEnded() {
		return invocationEnded;
	}

	public void setInvocationEnded(Timestamp invocationEnded) {
		this.invocationEnded = invocationEnded;
	}

	public DataBinding getInputs() {
		return inputs;
	}

	public void setInputs(DataBinding inputs) {
		this.inputs = inputs;
	}

	public DataBinding getOutputs() {
		return outputs;
	}

	public void setOutputs(DataBinding outputs) {
		this.outputs = outputs;
	}

	public String getFailureT2Reference() {
		return failureT2Reference;
	}

	public void setFailureT2Reference(String failureT2Reference) {
		this.failureT2Reference = failureT2Reference;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public String getInitiatingDispatchLayer() {
		return initiatingDispatchLayer;
	}

	public void setInitiatingDispatchLayer(String initiatingDispatchLayer) {
		this.initiatingDispatchLayer = initiatingDispatchLayer;
	}

	public String getFinalDispatchLayer() {
		return finalDispatchLayer;
	}

	public void setFinalDispatchLayer(String finalDispatchLayer) {
		this.finalDispatchLayer = finalDispatchLayer;
	}

	@Override
	public int hashCode() {
		return 31
				* (int) (invocationNumber ^ (invocationNumber >>> 32))
				+ ((processorEnactment == null) ? 0 : processorEnactment
						.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceInvocation other = (ServiceInvocation) obj;
		if (invocationNumber != other.invocationNumber)
			return false;
		if (processorEnactment == null) {
			if (other.processorEnactment != null)
				return false;
		} else if (!processorEnactment.equals(other.processorEnactment))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServiceInvocation [activity=");
		builder.append(activity);
		builder.append(", failureT2Reference=");
		builder.append(failureT2Reference);
		builder.append(", finalDispatchLayer=");
		builder.append(finalDispatchLayer);
		builder.append(", initiatingDispatchLayer=");
		builder.append(initiatingDispatchLayer);
		builder.append(", inputs=");
		builder.append(inputs);
		builder.append(", invocationEnded=");
		builder.append(invocationEnded);
		builder.append(", invocationNumber=");
		builder.append(invocationNumber);
		builder.append(", invocationStarted=");
		builder.append(invocationStarted);
		builder.append(", outputs=");
		builder.append(outputs);
		builder.append(", processorEnactment=");
		builder.append(processorEnactment);
		builder.append(", workflowRunId=");
		builder.append(workflowRunId);
		builder.append("]");
		return builder.toString();
	}
}
