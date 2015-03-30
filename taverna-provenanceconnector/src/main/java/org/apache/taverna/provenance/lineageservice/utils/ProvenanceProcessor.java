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
