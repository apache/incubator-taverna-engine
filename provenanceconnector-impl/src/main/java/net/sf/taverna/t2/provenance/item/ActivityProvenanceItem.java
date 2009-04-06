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

import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import org.jdom.Element;

/**
 * Contains details for an enacted Activity. Parent is a
 * {@link ProcessorProvenanceItem}. Children are {@link IterationProvenanceItem}
 * s. There could be multiple {@link ActivityProvenanceItem}s for each
 * {@link ProcessorProvenanceItem}
 * 
 * @author Ian Dunlop
 * @author Stuart Owen
 * @author Paolo Missier
 * 
 */
public class ActivityProvenanceItem implements ProvenanceItem {

	private Activity<?> activity;
	private IterationProvenanceItem iterationProvenanceItem;
	private String processId;
	private String identifier;
	private String parentId;

	public ActivityProvenanceItem(Activity<?> activity) {
	super();
		this.activity = activity;
	}

	public Element getAsXML(ReferenceService referenceService) {
		Element result = new Element("activity");
		result.setAttribute("id", getActivityID());
		result.setAttribute("identifier", this.identifier);
		result.setAttribute("processID", this.processId);
		result.setAttribute("parent", this.parentId);
		
//		 if (iterationProvenanceItem!=null)
//		 result.addContent(iterationProvenanceItem.getAsXML(referenceService));
		return result;
	}

	private String getActivityID() {
		return activity.getClass().getSimpleName();
	}

	public void setIterationProvenanceItem(
			IterationProvenanceItem iterationProvenanceItem) {
		this.iterationProvenanceItem = iterationProvenanceItem;
	}

	public IterationProvenanceItem getIterationProvenanceItem() {
		return iterationProvenanceItem;
	}

	public String getAsString() {
		return null;
	}

	public String getEventType() {
		return SharedVocabulary.ACTIVITY_EVENT_TYPE;
	}

	public Activity<?> getActivity() {
		return activity;
	}

	public void setActivity(Activity<?> activity) {
		this.activity = activity;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getParentId() {
		// TODO Auto-generated method stub
		return parentId;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;

	}

	public void setParentId(String parentId) {
		this.parentId = parentId;

	}

	public String getProcessId() {
		// TODO Auto-generated method stub
		return processId;
	}

}
