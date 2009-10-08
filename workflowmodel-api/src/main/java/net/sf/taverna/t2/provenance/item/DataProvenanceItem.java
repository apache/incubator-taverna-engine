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

import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;

/**
 * Contains references to data which a workflow has used or created. Parent is
 * an {@link IterationProvenanceItem}
 * 
 * @author Ian Dunlop
 * @auhor Stuart Owen
 * @author Paolo Missier
 * 
 */
public abstract class DataProvenanceItem implements ProvenanceItem {
	/** A map of port name to data reference */
	private Map<String, T2Reference> dataMap;
	private ReferenceService referenceService;
	private String workflowId;

	/**
	 * Is this {@link ProvenanceItem} for input or output data
	 * 
	 * @return
	 */
	protected abstract boolean isInput();

	public DataProvenanceItem() {

	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.provenance.item.DataProvenanceItemInterface#getDataMap()
	 */
	public Map<String, T2Reference> getDataMap() {
		return dataMap;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.provenance.item.DataProvenanceItemInterface#setDataMap(java.util.Map)
	 */
	public void setDataMap(Map<String, T2Reference> dataMap) {
		this.dataMap = dataMap;
	}

	public abstract String getIdentifier();

	public abstract String getParentId();

	public abstract String getProcessId();

	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}

	public ReferenceService getReferenceService() {
		return referenceService;
	}
	
	public String getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;	
	}

}
