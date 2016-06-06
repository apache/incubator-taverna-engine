/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.provenance.lineageservice.utils;

import static net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary.INPUTDATA_EVENT_TYPE;

import java.util.Set;
import java.util.regex.Pattern;

import net.sf.taverna.t2.provenance.item.DataProvenanceItem;
import org.apache.taverna.reference.ErrorDocument;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.IdentifiedList;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.reference.T2Reference;

import org.jdom.Element;
import org.tupeloproject.kernel.NotFoundException;

public class ProvenanceUtils {
	public static Pattern parentProcessPattern = Pattern.compile("^(.*):?[^:]+:[^:]+$");

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
		if (provenanceItem.getEventType().equals(INPUTDATA_EVENT_TYPE)) {
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
			portElement.setAttribute(
					"depth",
					Integer.toString(provenanceItem.getDataMap().get(port)
							.getDepth()));
			result.addContent(portElement);
			portElement.addContent(resolveToElement(provenanceItem.getDataMap()
					.get(port), provenanceItem.getReferenceService()));
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
	private static org.jdom.Element resolveToElement(T2Reference reference,
			ReferenceService referenceService) {
		Element element = new Element("resolvedReference");
		switch (reference.getReferenceType()) {
		case ErrorDocument:
			ErrorDocument error = referenceService.getErrorDocumentService()
					.getError(reference);

			element.setName("error");
			element.setAttribute("id", reference.toString());
			Element messageElement = new Element("message");
			messageElement.addContent(error.getExceptionMessage());
			element.addContent(messageElement);
			break;
		case ReferenceSet:
			element.setName("referenceSet");
			element.setAttribute("id", reference.toString());
			ReferenceSet referenceSet = referenceService
					.getReferenceSetService().getReferenceSet(reference);
			Set<ExternalReferenceSPI> externalReferences = referenceSet
					.getExternalReferences();
			for (ExternalReferenceSPI externalReference : externalReferences) {
				// FIXME does this make sense? No!! Should get the actual value
				// not what it is (TEXT etc)
				Element refElement = new Element("reference");
				refElement.addContent(externalReference.getDataNature()
						.toString());
				element.addContent(refElement);
			}
			break;
		case IdentifiedList:
			IdentifiedList<T2Reference> list = referenceService
					.getListService().getList(reference);

			element.setName("list");
			element.setAttribute("id", reference.toString());
			for (T2Reference ref : list)
				element.addContent(resolveToElement(ref, referenceService));
			break;
		default:
			// throw something (maybe a tantrum)
		}
		return element;
	}

	public static String parentProcess(String processId, int levels) {
		if (levels < 1)
			return processId;
		int lastColon = processId.lastIndexOf(":");
		if (lastColon == -1)
			return null;
		return parentProcess(processId.substring(0, lastColon), --levels);
	}

}
