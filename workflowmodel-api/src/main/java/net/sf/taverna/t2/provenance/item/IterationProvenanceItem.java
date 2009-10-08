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
package net.sf.taverna.t2.provenance.item;

import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;

/**
 * One of these is created for each iteration inside an enacted activity.
 * Contains both the input and output data and port names contained inside
 * {@link DataProvenanceItem}s. The actual iteration number is contained inside
 * an int array eg [1]
 * 
 * @author Ian Dunlop
 * @author Paolo Missier
 * @author Stuart Owen
 * 
 */
public class IterationProvenanceItem implements ProvenanceItem {
	private int[] iteration;
	private InputDataProvenanceItem inputDataItem;
	private OutputDataProvenanceItem outputDataItem;
	private ErrorProvenanceItem errorItem;
	private String processId;
	private String parentId;
	private String identifier;
	private SharedVocabulary eventType = SharedVocabulary.ITERATION_EVENT_TYPE;
	private String workflowId;

	public IterationProvenanceItem() {
	}

	public void setInputDataItem(InputDataProvenanceItem inputDataItem) {
		this.inputDataItem = inputDataItem;
	}

	public void setOutputDataItem(OutputDataProvenanceItem outputDataItem) {
		this.outputDataItem = outputDataItem;
	}

	public void setErrorItem(ErrorProvenanceItem errorItem) {
		this.errorItem = errorItem;
	}

	public ErrorProvenanceItem getErrorItem() {
		return errorItem;
	}

	public int[] getIteration() {
		return iteration;
	}

	public void setIteration(int[] iteration) {
		this.iteration = iteration;
	}
	
	public InputDataProvenanceItem getInputDataItem() {
		return inputDataItem;
	}

	public OutputDataProvenanceItem getOutputDataItem() {
		return outputDataItem;
	}

	public SharedVocabulary getEventType() {
		return eventType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getProcessId() {
		return processId;
	}
	
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setWorklfowId(String workflowId) {
		this.setWorkflowId(workflowId);
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	public String getWorkflowId() {
		return workflowId;
	}
	

}
