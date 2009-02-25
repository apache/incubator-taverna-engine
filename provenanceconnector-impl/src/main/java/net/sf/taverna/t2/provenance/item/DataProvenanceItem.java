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
import java.util.Set;

import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;

import org.jdom.Element;

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

	/**
	 * Is this {@link ProvenanceItem} for input or output data
	 * 
	 * @return
	 */
	protected abstract boolean isInput();

	public DataProvenanceItem() {

	}

	public DataProvenanceItem(Map<String, T2Reference> dataMap,
			ReferenceService referenceService) {
		super();
		this.dataMap = dataMap;
		this.referenceService = referenceService;
	}

	/**
	 * Returns an Element representing the data item, identfied as either input
	 * or output. References to data are currently resolved to their actual
	 * values
	 */
	public Element getAsXML(ReferenceService referenceService) {
		String name = isInput() ? "inputdata" : "outputdata";
		Element result = new Element(name);
		result.setAttribute("identifier", getIdentifier());
		result.setAttribute("processID", getProcessId());
		result.setAttribute("parent", getParentId());
		for (String port : dataMap.keySet()) {
			Element portElement = new Element("port");
			portElement.setAttribute("name", port);
			portElement.setAttribute("depth", Integer.toString(dataMap
					.get(port).getDepth()));
			result.addContent(portElement);
			portElement.addContent(resolveToElement(dataMap.get(port)));
			Element element = new Element("some_stuff");
			portElement.addContent(element);
		}
		return result;
	}

	public Map<String, T2Reference> getDataMap() {
		return dataMap;
	}

	/**
	 * Given a {@link T2Reference} return all the other {@link T2Reference}s
	 * which it contains as an XML Element.
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
			element.setAttribute("id", reference.toString());
			ReferenceSet referenceSet = referenceService
					.getReferenceSetService().getReferenceSet(reference);
			Set<ExternalReferenceSPI> externalReferences = referenceSet
					.getExternalReferences();
			for (ExternalReferenceSPI externalReference : externalReferences) {
				// FIXME does this make sense? No!! Should get the actual value
				// not what it is (TEXT etc)
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

	public String getAsString() {
		return null;
	}

	/**
	 * A map of port names to data references
	 * 
	 * @param dataMap
	 */
	public void setDataMap(Map<String, T2Reference> dataMap) {
		this.dataMap = dataMap;
	}

	public abstract String getIdentifier();

	public abstract String getParentId();

	public abstract String getProcessId();

}
