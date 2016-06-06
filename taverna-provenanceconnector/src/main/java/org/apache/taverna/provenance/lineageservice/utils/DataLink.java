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

public class DataLink {
	private String workflowId;
	private String sourcePortId;
	private String sourceProcessorName;
	private String sourcePortName;
	private String destinationPortId;
	private String destinationProcessorName;
	private String destinationPortName;

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
	public void setWorkflowId(String workflowRunId) {
		this.workflowId = workflowRunId;
	}

	public String getSourcePortId() {
		return sourcePortId;
	}

	public void setSourcePortId(String sourcePortId) {
		this.sourcePortId = sourcePortId;
	}

	/**
	 * @return the sourceProcessorName
	 */
	public String getSourceProcessorName() {
		return sourceProcessorName;
	}

	/**
	 * @param sourceProcessorName
	 *            the sourceProcessorName to set
	 */
	public void setSourceProcessorName(String sourceProcessorName) {
		this.sourceProcessorName = sourceProcessorName;
	}

	/**
	 * @return the sourcePortName
	 */
	public String getSourcePortName() {
		return sourcePortName;
	}

	/**
	 * @param sourcePortName
	 *            the sourcePortName to set
	 */
	public void setSourcePortName(String sourcePortName) {
		this.sourcePortName = sourcePortName;
	}

	public String getDestinationPortId() {
		return destinationPortId;
	}

	public void setDestinationPortId(String destinationPortId) {
		this.destinationPortId = destinationPortId;
	}

	/**
	 * @return the sourceprocessorNameRef
	 */
	public String getDestinationProcessorName() {
		return destinationProcessorName;
	}

	/**
	 * @param sourceprocessorNameRef
	 *            the sourceprocessorNameRef to set
	 */
	public void setDestinationProcessorName(String destinationProcessorName) {
		this.destinationProcessorName = destinationProcessorName;
	}

	/**
	 * @return the destinationPortName
	 */
	public String getDestinationPortName() {
		return destinationPortName;
	}

	/**
	 * @param destinationPortName
	 *            the destinationPortName to set
	 */
	public void setDestinationPortName(String destinationPortName) {
		this.destinationPortName = destinationPortName;
	}
}
