/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package org.apache.taverna.provenance.lineageservice.utils;

/**
 * @author Paolo Missier
 * 
 */
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
