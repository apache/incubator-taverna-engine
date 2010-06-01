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
package net.sf.taverna.t2.provenance.lineageservice.utils;


/**
 * @author paolo
 * 
 */

public class DataLink {
	private

	String workflowId;
	String sourcePortId;
	String sourceProcessorName;
	String sourcePortName;
	String destinationPortId;
	String destinationProcessorName;
	String destinationPortName;

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
