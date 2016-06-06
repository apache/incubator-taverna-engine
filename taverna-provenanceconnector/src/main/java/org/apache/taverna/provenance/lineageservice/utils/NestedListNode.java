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

public class NestedListNode {
	private String collectionT2Reference;
	private String parentCollectionT2Reference;
	private String workflowRunId;
	private String processorName;
	private String portName;
	private String iteration;

	/**
	 * @return the collectionT2Reference
	 */
	public String getCollectionT2Reference() {
		return collectionT2Reference;
	}

	/**
	 * @param collectionT2Reference
	 *            the collectionT2Reference to set
	 */
	public void setCollectionT2Reference(String collectionT2Reference) {
		this.collectionT2Reference = collectionT2Reference;
	}

	/**
	 * @return the parentCollIdRef
	 */
	public String getParentCollIdRef() {
		return parentCollectionT2Reference;
	}

	/**
	 * @param parentCollIdRef
	 *            the parentCollIdRef to set
	 */
	public void setParentCollIdRef(String parentCollIdRef) {
		this.parentCollectionT2Reference = parentCollIdRef;
	}

	/**
	 * @return the workflowRunId
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
	 * @return the processorNameRef
	 */
	public String getProcessorName() {
		return processorName;
	}

	/**
	 * @param nameRef
	 *            the processorNameRef to set
	 */
	public void setProcessorName(String nameRef) {
		processorName = nameRef;
	}

	/**
	 * @return the portName
	 */
	public String getPortName() {
		return portName;
	}

	/**
	 * @param portName
	 *            the portName to set
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}

	/**
	 * @return the iteration
	 */
	public String getIteration() {
		return iteration;
	}

	/**
	 * @param iteration
	 *            the iteration to set
	 */
	public void setIteration(String iteration) {
		this.iteration = iteration;
	}
}
