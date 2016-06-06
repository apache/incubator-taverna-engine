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

public class ProcessorBinding {
	private String identifier;

	private String processorName;
	private String workflowRunId;
	private String workflowId;
	private String firstActivityClassName;
	private String iterationVector;

	@Override
	public String toString() {
		return "ProcessorBinding [firstActivityClassName="
				+ firstActivityClassName + ", identifier=" + identifier
				+ ", iterationVector=" + iterationVector + ", processorName="
				+ processorName + ", workflowId=" + workflowId
				+ ", workflowRunId=" + workflowRunId + "]";
	}

	/**
	 * @return the processorNameRef
	 */
	public String getProcessorName() {
		return processorName;
	}

	/**
	 * @param nameRef
	 *            the processorNameRef to set
	 */
	public void setProcessorName(String processorNameRef) {
		this.processorName = processorNameRef;
	}

	/**
	 * @return the execIDRef
	 */
	public String getWorkflowRunId() {
		return workflowRunId;
	}

	/**
	 * @param workflowRunId
	 *            the workflowRunId to set
	 */
	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
	}

	/**
	 * @return the actName
	 */
	public String getFirstActivityClassName() {
		return firstActivityClassName;
	}

	/**
	 * @param actName
	 *            the actName to set
	 */
	public void setFirstActivityClassName(String actName) {
		this.firstActivityClassName = actName;
	}

	/**
	 * @return the iteration
	 */
	public String getIterationVector() {
		return iterationVector;
	}

	/**
	 * @param iterationVector
	 *            the iteration to set
	 */
	public void setIterationVector(String iterationVector) {
		this.iterationVector = iterationVector;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the workflowId
	 */
	public String getWorkflowId() {
		return workflowId;
	}

	/**
	 * @param workflowId
	 *            the workflowId to set
	 */
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
}
