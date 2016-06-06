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

public class PortBinding {
	private String workflowId;
	private String portName;
	private String workflowRunId;
	private String value;
	private String collIDRef;
	private int positionInColl;
	private String processorName;
	private String valueType;
	private String reference;
	private String iteration;
	private String resolvedValue;
	private String portId;
	private Boolean isInputPort;

	public PortBinding() {
	}

	public PortBinding(PortBinding vb) {
		workflowId = vb.workflowId;
		portName = vb.portName;
		workflowRunId = vb.workflowRunId;
		value = vb.value;
		collIDRef = vb.collIDRef;
		positionInColl = vb.positionInColl;
		processorName = vb.processorName;
		valueType = vb.valueType;
		reference = vb.reference;
		iteration = vb.iteration;
		resolvedValue = vb.resolvedValue;
		portId = vb.portId;
		isInputPort = vb.isInputPort;
	}

	public String getPortId() {
		return portId;
	}

	@Override
	public String toString() {
		return "PortBinding [workflowId=" + workflowId + ", portName="
				+ portName + ", workflowRunId=" + workflowRunId + ", value="
				+ value + ", collIDRef=" + collIDRef + ", positionInColl="
				+ positionInColl + ", processorName=" + processorName
				+ ", valueType=" + valueType + ", reference=" + reference
				+ ", iteration=" + iteration + ", resolvedValue="
				+ resolvedValue + ", portId=" + portId + ", isInputPort="
				+ isInputPort + "]";
	}

	/**
	 * @return the positionInColl
	 */
	public int getPositionInColl() {
		return positionInColl;
	}

	/**
	 * @param positionInColl
	 *            the positionInColl to set
	 */
	public void setPositionInColl(int positionInColl) {
		this.positionInColl = positionInColl;
	}

	/**
	 * @return the valueType
	 */
	public String getValueType() {
		return valueType;
	}

	/**
	 * @param valueType
	 *            the valueType to set
	 */
	public void setValueType(String valueType) {
		this.valueType = valueType;
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

	/**
	 * @return the collIDRef
	 */
	public String getCollIDRef() {
		return collIDRef;
	}

	/**
	 * @param collIDRef
	 *            the collIDRef to set
	 */
	public void setCollIDRef(String collIDRef) {
		this.collIDRef = collIDRef;
	}

	/**
	 * @return the iteration
	 */
	public String getIteration() {
		return iteration;
	}

	/**
	 * @param iterationVector
	 *            the iteration to set
	 */
	public void setIteration(String iterationVector) {
		this.iteration = iterationVector;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the ref
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param ref
	 *            the ref to set
	 */
	public void setReference(String ref) {
		this.reference = ref;
	}

	/**
	 * @return the resolvedValue
	 */
	public String getResolvedValue() {
		return resolvedValue;
	}

	/**
	 * @param resolvedValue
	 *            the resolvedValue to set
	 */
	public void setResolvedValue(String resolvedValue) {
		this.resolvedValue = resolvedValue;
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

	public void setPortId(String portId) {
		this.portId = portId;

	}

	public void setIsInputPort(boolean isInputPort) {
		this.setInputPort(isInputPort);
	}

	public void setInputPort(boolean isInputPort) {
		this.isInputPort = isInputPort;
	}

	public Boolean isInputPort() {
		return isInputPort;
	}
}
