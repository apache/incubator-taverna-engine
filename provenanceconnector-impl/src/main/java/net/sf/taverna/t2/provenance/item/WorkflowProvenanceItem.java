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
import net.sf.taverna.t2.provenance.connector.ProvenanceConnector;
import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.serialization.SerializationException;
import net.sf.taverna.t2.workflowmodel.serialization.xml.XMLSerializer;
import net.sf.taverna.t2.workflowmodel.serialization.xml.XMLSerializerRegistry;

import org.apache.log4j.Logger;
import org.jdom.Element;

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

	public Dataflow getDataflow() {
		return dataflow;
	}

	public void setDataflow(Dataflow dataflow) {
		this.dataflow = dataflow;
	}

	public WorkflowProvenanceItem() {
	}

	public String getAsString() {
		return null;
	}

	public Element getAsXML(ReferenceService referenceService) {

		Element result = new Element("workflowItem");

		result.setAttribute("identifier", this.identifier);

		Element serializeDataflow = null;
		try {
			XMLSerializerRegistry instance = XMLSerializerRegistry
					.getInstance();
			XMLSerializer serializer = instance.getSerializer();
			serializeDataflow = serializer.serializeDataflow(this.dataflow);
		} catch (SerializationException e) {
			logger
					.warn("Workflow Provenance Item had a problem serializing the dataflow: "
							+ e.toString());
		}
		result.addContent(serializeDataflow);
		return result;
	}

	public String getEventType() {
		return SharedVocabulary.WORKFLOW_EVENT_TYPE;
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

}
