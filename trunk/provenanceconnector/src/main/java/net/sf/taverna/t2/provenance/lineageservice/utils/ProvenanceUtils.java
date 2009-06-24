package net.sf.taverna.t2.provenance.lineageservice.utils;

import java.util.Set;

import net.sf.taverna.t2.provenance.item.DataProvenanceItem;
import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;

import org.jdom.Element;

public class ProvenanceUtils {
	
	public static String iterationToString(int[] iteration) {
		String result = "[";
		for (int i = 0; i < iteration.length; i++) {
			result += iteration[i];
			if (i < (iteration.length - 1))
				result += ",";
		}
		result += "]";
		return result;
	}
	
	/**
	 * Returns an Element representing the data item, identfied as either input
	 * or output. References to data are currently resolved to their actual
	 * values
	 */
	public static Element getDataItemAsXML(DataProvenanceItem provenanceItem) {
		String name;
		if (provenanceItem.getEventType().equals(SharedVocabulary.INPUTDATA_EVENT_TYPE)) {
			name = "inputdata";
		} else {
			name = "outputdata";
		}
		Element result = new Element(name);
		result.setAttribute("identifier", provenanceItem.getIdentifier());
		result.setAttribute("processID", provenanceItem.getProcessId());
		result.setAttribute("parent", provenanceItem.getParentId());
		for (String port : provenanceItem.getDataMap().keySet()) {
			Element portElement = new Element("port");
			portElement.setAttribute("name", port);
			portElement.setAttribute("depth", Integer.toString(provenanceItem.getDataMap()
					.get(port).getDepth()));
			result.addContent(portElement);
			portElement.addContent(resolveToElement(provenanceItem.getDataMap().get(port), provenanceItem.getReferenceService()));
			Element element = new Element("some_stuff");
			portElement.addContent(element);
		}
		return result;
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
	private static org.jdom.Element resolveToElement(T2Reference reference, ReferenceService referenceService) {

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
				element.addContent(resolveToElement(ref, referenceService));
			}
		} else {
			// throw something (maybe a tantrum)
		}
		return element;
	}

}
