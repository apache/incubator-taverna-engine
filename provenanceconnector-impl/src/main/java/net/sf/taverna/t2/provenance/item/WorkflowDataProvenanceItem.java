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

import java.util.Set;

import net.sf.taverna.t2.facade.WorkflowInstanceFacade;
import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;

import org.jdom.Element;

/**
 * When the {@link WorkflowInstanceFacade} for a processor receives a data token
 * one of these is created. This is especially important for data which flows
 * straight through a facade without going into the dispatch stack (a rare event
 * but it can happen)
 * 
 * @author Ian Dunlop
 * 
 */
public class WorkflowDataProvenanceItem implements ProvenanceItem {

	private String identifier;
	private String parentId;
	private String processId;
	private ReferenceService referenceService;
	/** The output port name that the data is for */
	private final String portName;
	/** A reference to the data token received in the facade */
	private final T2Reference data;

	public WorkflowDataProvenanceItem(String portName, T2Reference data,
			ReferenceService referenceService) {
		this.portName = portName;
		this.data = data;
		this.referenceService = referenceService;
	}

	public String getAsString() {
		return null;
	}

	public Element getAsXML(ReferenceService referenceService) {
		Element result = new Element("workflowdata");
		result.setAttribute("identifier", getIdentifier());
		result.setAttribute("processID", getProcessId());
		result.setAttribute("parent", getParentId());

		Element portElement = new Element("port");
		portElement.setAttribute("name", portName);
		portElement.setAttribute("data", data.toString());
		result.addContent(portElement);
		//don't know the depth for these type of results
//		portElement.addContent(resolveToElement(data));
		return result;
	}

	public String getEventType() {
		return SharedVocabulary.WORKFLOW_DATA_EVENT_TYPE;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getParentId() {
		return parentId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	/**
	 * Given a {@link T2Reference} return all the other {@link T2Reference}s
	 * which it contains as an XML Element
	 * 
	 * @param entityIdentifier
	 * @return
	 * @throws NotFoundException
	 * @throws RetrievalException
	 */
	private org.jdom.Element resolveToElement(T2Reference reference) {

		org.jdom.Element element = new org.jdom.Element("resolvedReference");
		if (reference.getReferenceType().equals(T2ReferenceType.ErrorDocument)) {
			ErrorDocument error = referenceService.getErrorDocumentService()
					.getError(reference);

			element.setName("error");
			element.setAttribute("id", reference.toString());
			org.jdom.Element messageElement = new org.jdom.Element("message");
			messageElement.addContent(error.getExceptionMessage());
			element.addContent(messageElement);
		} else if (reference.getReferenceType().equals(
				T2ReferenceType.ReferenceSet)) {
			element.setName("referenceSet");
			System.out.println("reference is: " + reference.toString());
			element.setAttribute("id", reference.toString());
			if (referenceService == null) {
				System.out.println("Ref service is null");
			}
			ReferenceSet referenceSet = referenceService
					.getReferenceSetService().getReferenceSet(reference);
			Set<ExternalReferenceSPI> externalReferences = referenceSet
					.getExternalReferences();
			for (ExternalReferenceSPI externalReference : externalReferences) {
				// FIXME does this make sense? NO, should contain the actual
				// data not the type it is (eg TEXT)
				org.jdom.Element refElement = new org.jdom.Element("reference");
				refElement.addContent(externalReference.getDataNature()
						.toString());
				element.addContent(refElement);
			}

		} else if (reference.getReferenceType().equals(
				T2ReferenceType.IdentifiedList)) {
			IdentifiedList<T2Reference> list = referenceService
					.getListService().getList(reference);

			element.setName("list");
			element.setAttribute("id", reference.toString());
			for (T2Reference ref : list) {
				element.addContent(resolveToElement(ref));
			}
		} else {
			// throw something (maybe a tantrum)
		}
		return element;
	}

}
