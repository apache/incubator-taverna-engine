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

import org.jdom.Element;

/**
 * One of these is created for each iteration inside an enacted activity.
 * Contains both the input and output data and port names contained inside
 * {@link DataProvenanceItem}s. The actual iteration number is contained inside
 * an int array eg [1]
 * 
 * @author Ian Dunlop
 * @author Paolo Missier
 * @author Stuart Owen
 * 
 */
public class IterationProvenanceItem implements ProvenanceItem {
	private int[] iteration;
	private InputDataProvenanceItem inputDataItem;
	private OutputDataProvenanceItem outputDataItem;
	private ErrorProvenanceItem errorItem;
	private String processId;
	private String parentId;
	private String identifier;

	public IterationProvenanceItem(int[] iteration) {
		super();
		this.iteration = iteration;
	}

	public void setInputDataItem(InputDataProvenanceItem inputDataItem) {
		this.inputDataItem = inputDataItem;
	}

	public void setOutputDataItem(OutputDataProvenanceItem outputDataItem) {
		this.outputDataItem = outputDataItem;
	}

	public void setErrorItem(ErrorProvenanceItem errorItem) {
		this.errorItem = errorItem;
	}

	public Element getAsXML(ReferenceService referenceService) {
		Element result = new Element("iteration");
		result.setAttribute("id", iterationToString());
		result.setAttribute("identifier", this.identifier);
		result.setAttribute("processID", this.processId);
		result.setAttribute("parent", this.parentId);
		if (inputDataItem != null)
			result.addContent(inputDataItem.getAsXML(referenceService));
		if (outputDataItem != null)
			result.addContent(outputDataItem.getAsXML(referenceService));
		if (errorItem != null)
			result.addContent(errorItem.getAsXML(referenceService));
		return result;
	}

	private String iterationToString() {
		String result = "[";
		for (int i = 0; i < iteration.length; i++) {
			result += iteration[i];
			if (i < (iteration.length - 1))
				result += ",";
		}
		result += "]";
		return result;
	}

	public String getAsString() {
		return null;
	}

	public int[] getIteration() {
		return iteration;
	}

	public InputDataProvenanceItem getInputDataItem() {
		return inputDataItem;
	}

	public OutputDataProvenanceItem getOutputDataItem() {
		return outputDataItem;
	}

	public String getEventType() {
		return SharedVocabulary.ITERATION_EVENT_TYPE;
	}

	public ErrorProvenanceItem getErrorItem() {
		return errorItem;
	}

	public void setIteration(int[] iteration) {
		this.iteration = iteration;
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
		// TODO Auto-generated method stub

	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
		// TODO Auto-generated method stub

	}

	public String getProcessId() {
		// TODO Auto-generated method stub
		return processId;
	}

}
