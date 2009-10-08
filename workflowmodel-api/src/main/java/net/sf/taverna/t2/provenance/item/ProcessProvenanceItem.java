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
 * Each time a job is received by the dispatch stack one of these will be
 * created. It has a {@link ProcessorProvenanceItem} as its child. Its parent is
 * a {@link WorkflowProvenanceItem} which in turn knows the unique id of the
 * workflow the provenance is being stored for. NOTE: May be superfluous since
 * it essentially mimics the behaviour of its child item but may be more hastle
 * than it is worth to remove it
 * 
 * @author Stuart owen
 * @author Paolo Missier
 * @author Ian Dunlop
 * 
 */
public class ProcessProvenanceItem implements ProvenanceItem {
	private String owningProcess;
	private ProcessorProvenanceItem processorProvenanceItem;
	private String parentId;
	private String identifier;
	private String facadeID;
	private String processId;
	private String dataflowID;
	private SharedVocabulary eventType = SharedVocabulary.PROCESS_EVENT_TYPE;
	private String workflowId;

	public ProcessProvenanceItem() {
		
	}

	/**
	 * As {@link WorkflowInstanceFacade}s are created for a Processor the
	 * details are appended to the owning process identifier. This is in the
	 * form facadeX:dataflowY:ProcessorZ etc.  This method returns the facadeX part.
	 * 
	 * @return
	 */
	public String getFacadeID() {
		return facadeID;
	}

	public void setProcessorProvenanceItem(
			ProcessorProvenanceItem processorProvenanceItem) {
		this.processorProvenanceItem = processorProvenanceItem;
	}

	public ProcessorProvenanceItem getProcessorProvenanceItem() {
		return processorProvenanceItem;
	}

	public SharedVocabulary getEventType() {
		return eventType;
	}

	public String getOwningProcess() {
		return owningProcess;
	}

	public void setOwningProcess(String owningProcess) {
		this.owningProcess = owningProcess;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setFacadeID(String facadeID) {
		this.facadeID = facadeID;
	}

	public void setDataflowID(String dataflowID) {
		this.dataflowID = dataflowID;
	}
	
	public String getDataflowID() {
		return dataflowID;
	}
	
	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;	
	}

}
