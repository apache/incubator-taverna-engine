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


import java.sql.Blob;

public class WorkflowRun {
	private String workflowRunId;
	/**
	 * this is the workflowId for the TOP LEVEL workflow for this run.
	 * <p>
	 * CHECK
	 */
	private String workflowId;
	private String timestamp;
	private String workflowExternalName;
	private byte[] dataflowBlob;

	public void setWorkflowId(String workflowIdentifier) {
		this.workflowId = workflowIdentifier;
	}

	public String getWorkflowId() {
		return workflowId;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the workflowRunId
	 */
	public String getWorkflowRunId() {
		return workflowRunId;
	}

	/**
	 * @param workflowRunId the workflowRunId to set
	 */
	public void setWorkflowRunId(String workflowRunId) {
		this.workflowRunId = workflowRunId;
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
