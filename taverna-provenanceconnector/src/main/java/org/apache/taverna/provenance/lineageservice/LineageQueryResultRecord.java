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
package org.apache.taverna.provenance.lineageservice;

/**
 *
 * This Java bean holds a single provenance record, i.e., the finest element of
 * a provenance graph that is stored in the provenance DB. Essentially this
 * represents one data element (value) flowing through a port (vname) of a
 * processor (pname), in the context of one run (wfInstance) of a workflow
 * (wfname). The record may include an <b>iteration</b> vector, used when the
 * same processor receives multiple values on the same port, as part of
 * iterative processing. When the value belongs to a collection (a nested list),
 * the <b>collIdRef</b> field contains a reference to that collection.
 *
 * @author Paolo Missier
 *
 */
public class LineageQueryResultRecord {
	private String workflowId;
	private String processorName;
	private String portName;
	private String workflowRunId;
	private String iteration;
	private String value;     // atomic or XML-formatted collection -- this is actually a reference to the value...
	private String collIdRef;
	private String parentCollIDRef;
	private String resolvedValue;
	private String type;  // one of referenceSet, referenceSetCollection
	boolean printResolvedValue;
	boolean isInput;
	boolean isCollection;

	@Override
	public String toString() {
		if (isCollection) {
			return "COLLECTION: proc " + getProcessorName() + " var "
					+ getPortName() + " " + " iteration: " + getIteration()
					+ " value: " + getValue() + " collection id: "
					+ getCollectionT2Reference() + " parent collection: "
					+ getParentCollIDRef();
		} else if (printResolvedValue)
			return "workflow " + getworkflowId() + " proc "
					+ getProcessorName() + " var " + getPortName() + " "
					+ " iteration: " + getIteration() + " value: " + getValue()
					+ " collection id: " + getCollectionT2Reference()
					+ " resolvedValue: " + getResolvedValue();
		else
			return "workflow " + getworkflowId() + " proc "
					+ getProcessorName() + " var " + getPortName() + " "
					+ " iteration: " + getIteration() + " collection id: "
					+ getCollectionT2Reference() + " value: " + getValue();
	}

	/**
	 * @return the pname
	 */
	public String getProcessorName() {
		return processorName;
	}
	/**
	 * @param pname the pname to set
	 */
	public void setProcessorName(String pname) {
		this.processorName = pname;
	}
	/**
	 * @return the vname
	 */
	public String getPortName() {
		return portName;
	}
	/**
	 * @param vname the vname to set
	 */
	public void setPortName(String vname) {
		this.portName = vname;
	}
	/**
	 * @return the workflowRun
	 */
	public String getWorkflowRunId() {
		return workflowRunId;
	}
	/**
	 * @param workflowRun the workflowRun to set
	 */
	public void setWorkflowRunId(String workflowRun) {
		this.workflowRunId = workflowRun;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the iteration
	 */
	public String getIteration() {
		return iteration;
	}
	/**
	 * @param iteration the iteration to set
	 */
	public void setIteration(String iteration) {
		this.iteration = iteration;
	}
	/**
	 * @return the resolvedValue
	 */
	public String getResolvedValue() {
		return resolvedValue;
	}
	/**
	 * @param resolvedValue the resolvedValue to set
	 */
	public void setResolvedValue(String resolvedValue) {
		this.resolvedValue = resolvedValue;
	}


	public void setPrintResolvedValue(boolean printResolvedValue) {
		this.printResolvedValue = printResolvedValue;
	}


	/**
	 * @return the isInput
	 */
	public boolean isInputPort() {
		return isInput;
	}


	/**
	 * @param isInput the isInput to set
	 */
	public void setIsInputPort(boolean isInput) {
		this.isInput = isInput;
	}


	/**
	 * @return the collIdRef
	 */
	public String getCollectionT2Reference() {
		return collIdRef;
	}


	/**
	 * @param collIdRef the collIdRef to set
	 */
	public void setCollectionT2Reference(String collIdRef) {
		this.collIdRef = collIdRef;
	}


	/**
	 * @return the isCollection
	 */
	public boolean isCollection() {
		return isCollection;
	}


	/**
	 * @param isCollection the isCollection to set
	 */
	public void setCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}


	/**
	 * @return the parentCollIDRef
	 */
	public String getParentCollIDRef() {
		return parentCollIDRef;
	}


	/**
	 * @param parentCollIDRef the parentCollIDRef to set
	 */
	public void setParentCollIDRef(String parentCollIDRef) {
		this.parentCollIDRef = parentCollIDRef;
	}


	/**
	 * @return the workflowId
	 */
	public String getworkflowId() {
		return workflowId;
	}


	/**
	 * @param workflowId the workflowId to set
	 */
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

}
