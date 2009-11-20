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
 * Used to store some enactment information during a workflow run
 * 
 * @author Ian Dunlop
 * 
 */
public interface ProvenanceItem {

	/**
	 * What type of information does the item contain. The
	 * {@link SharedVocabulary} can be used to identify it
	 * 
	 * @return
	 */
	public SharedVocabulary getEventType();

	/**
	 * The unique identifier for this item
	 * 
	 * @return
	 */
	public String getIdentifier();

	/**
	 * A unique id for this event. Any children would use this as their parentId
	 * 
	 * @param identifier
	 */
	public void setIdentifier(String identifier);

	/**
	 * The workflow model id that is supplied during enactment eg
	 * facade0:dataflow2:processor1
	 * 
	 * @param processId
	 */
	public void setProcessId(String processId);

	/**
	 * Get the enactor supplie identifier
	 * 
	 * @return
	 */
	public String getProcessId();

	/**
	 * The parent of this provenance Item. The model is
	 * WorkflowProvenanceItem>ProcessProvenanceItem
	 * >ProcessorProvenanceItem>ActivityProvenanceITem
	 * >IterationProvenanceItem>DataProvenanceItem
	 * 
	 * Additionally there is a WorkflowDataProvenanceItem that is sent when the
	 * facade receives a completion event and a ErrorProvenanceItem when things
	 * go wrong
	 * 
	 * @param parentId
	 */
	public void setParentId(String parentId);

	/**
	 * Who is the parent of this item?
	 * 
	 * @return
	 */
	public String getParentId();
	
	/**
	 * The uuid that belongs to the actual dataflow
	 * @param workflowId
	 */
	public void setWorkflowId(String workflowId);
	
	/**
	 * The uuid that belongs to the actual dataflow
	 * @return a string representation of a uuid
	 */
	public String getWorkflowId();

}