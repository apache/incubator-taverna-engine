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

import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;
import net.sf.taverna.t2.workflowmodel.Dataflow;

import org.apache.log4j.Logger;

/**
 * The first {@link ProvenanceItem} that the {@link ProvenanceConnector} will
 * receive for a workflow run. Contains the {@link Dataflow} itself as well as
 * the process id for the {@link WorkflowInstanceFacade} (facadeX:dataflowY).
 * Its child is a {@link ProcessProvenanceItem} and parent is the UUID of the
 * {@link Dataflow} itself
 * 
 * @author Ian Dunlop
 * @author Paolo Missier
 * @author Stuart Owen
 * 
 */
public class WorkflowProvenanceItem implements ProvenanceItem {

	private static Logger logger = Logger
			.getLogger(WorkflowProvenanceItem.class);

	private Dataflow dataflow;
	private String processId;
	private String parentId;
	private String identifier;
	private SharedVocabulary eventType  = SharedVocabulary.WORKFLOW_EVENT_TYPE;
	private int[] index;
	private boolean isFinal;
	
	public WorkflowProvenanceItem() {
		
	}

	public Dataflow getDataflow() {
		return dataflow;
	}

	public void setDataflow(Dataflow dataflow) {
		this.dataflow = dataflow;
	}

	public SharedVocabulary getEventType() {
		return eventType;
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

	/**
	 * @return the index
	 */
	public int[] getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int[] index) {
		this.index = index;
	}

	/**
	 * @return the isFinal
	 */
	public boolean isFinal() {
		return isFinal;
	}

	/**
	 * @param isFinal the isFinal to set
	 */
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}
}
