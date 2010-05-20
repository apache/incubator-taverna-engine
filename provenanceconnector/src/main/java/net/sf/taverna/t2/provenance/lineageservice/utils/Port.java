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
 * a Port that has no pName is either a WF input or output, depending on isInput
 * @author Paolo Missier
 */
public class Port {
	
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
	 * @param workflowId the workflowId to set
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
	 * @param name the portName to set
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
	 * @param name the processorName to set
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
	 * @param isInputPort <code>true</code> if the port is an input port,
	 *         <code>false</code> if it is an output port
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
	 * @param depth the depth to set
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
	 * @param resolvedDepth the resolvedDepth to set
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
	 * @param iterationStrategyOrder the iterationStrategyOrder to set
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
