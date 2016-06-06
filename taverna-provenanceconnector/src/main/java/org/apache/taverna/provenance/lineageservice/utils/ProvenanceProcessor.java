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

/**
 * a Port that has no pName is either a WF input or output, depending on isInput
 *
 * @author Paolo Missier
 */
public class ProvenanceProcessor {

	public static final String DATAFLOW_ACTIVITY = "net.sf.taverna.t2.activities.dataflow.DataflowActivity";

	private String identifier;
	private String processorName;
	private String workflowId;
	private String firstActivityClassName;
	private boolean isTopLevelProcessor;

	public ProvenanceProcessor() {
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PROCESSOR: ****").append("\nworkflow: " + getWorkflowId())
				.append("\nprocessor name: " + getProcessorName())
				.append("\ntype: " + getFirstActivityClassName());

		return sb.toString();
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

	/**
	 * @return The fully qualified classname for the first activity in this
	 *         processor, or {@link #DATAFLOW_ACTIVITY} if this is a virtual
	 *         processor representing the workflow itself.
	 */
	public String getFirstActivityClassName() {
		return firstActivityClassName;
	}

	/**
	 * @param firstActivityClassName
	 *            The fully qualified classname for the first activity in this
	 *            processor, or {@link #DATAFLOW_ACTIVITY} if this is a virtual
	 *            processor representing the workflow itself.
	 */
	public void setFirstActivityClassName(String firstActivityClassName) {
		this.firstActivityClassName = firstActivityClassName;
	}

	/**
	 * @return the processorName
	 */
	public String getProcessorName() {
		return processorName;
	}

	/**
	 * @param processorName
	 *            the processorName to set
	 */
	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setTopLevelProcessor(boolean isTopLevelProcessor) {
		this.isTopLevelProcessor = isTopLevelProcessor;
	}

	public boolean isTopLevelProcessor() {
		return isTopLevelProcessor;
	}
}
