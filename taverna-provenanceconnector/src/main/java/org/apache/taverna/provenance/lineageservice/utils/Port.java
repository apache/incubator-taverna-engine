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
public class Port {

	@Override
	public String toString() {
		return "Port [identifier=" + identifier + ", isInputPort="
				+ isInputPort + ", portName=" + portName + ", processorName="
				+ processorName + ", workflowId=" + workflowId + "]";
	}

	@Override
	public int hashCode() {
		return 31 + ((identifier == null) ? 0 : identifier.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Port other = (Port) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

	private String identifier;
	private String portName;
	private String processorName;
	private boolean isInputPort;
	private String workflowId;
	private int depth = 0;
	private Integer resolvedDepth = null;
	private int iterationStrategyOrder = 0;
	private String processorId;

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
	 * @return the vName
	 */
	public String getPortName() {
		return portName;
	}

	/**
	 * @param name
	 *            the portName to set
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}

	/**
	 * @return the processorName
	 */
	public String getProcessorName() {
		return processorName;
	}

	/**
	 * @param name
	 *            the processorName to set
	 */
	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	/**
	 * @return <code>true</code> if the port is an input port,
	 *         <code>false</code> if it is an output port
	 */
	public boolean isInputPort() {
		return isInputPort;
	}

	/**
	 * @param isInputPort
	 *            <code>true</code> if the port is an input port,
	 *            <code>false</code> if it is an output port
	 */
	public void setInputPort(boolean isInputPort) {
		this.isInputPort = isInputPort;
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth
	 *            the depth to set
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * @return the resolvedDepth
	 */
	public Integer getResolvedDepth() {
		return resolvedDepth;
	}

	/**
	 * @param resolvedDepth
	 *            the resolvedDepth to set
	 */
	public void setResolvedDepth(Integer resolvedDepth) {
		this.resolvedDepth = resolvedDepth;
	}

	/**
	 * @return <code>true</code> if the {@link #resolvedDepth} has been set
	 */
	public boolean isResolvedDepthSet() {
		return resolvedDepth != null;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the iterationStrategyOrder
	 */
	public int getIterationStrategyOrder() {
		return iterationStrategyOrder;
	}

	/**
	 * @param iterationStrategyOrder
	 *            the iterationStrategyOrder to set
	 */
	public void setIterationStrategyOrder(int iterationStrategyOrder) {
		this.iterationStrategyOrder = iterationStrategyOrder;
	}

	public String getProcessorId() {
		return processorId;
	}

	public void setProcessorId(String processorId) {
		this.processorId = processorId;
	}
}
