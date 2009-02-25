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

import net.sf.taverna.t2.provenance.connector.ProvenanceConnector;
import net.sf.taverna.t2.provenance.item.ProvenanceItem;
import net.sf.taverna.t2.provenance.vocabulary.SharedVocabulary;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchErrorType;

import org.jdom.Element;

/**
 * When an error is received in the dispatch stack, one of these is created and
 * sent across to the {@link ProvenanceConnector}. Parent is an
 * {@link IterationProvenanceItem}
 * 
 * @author Ian Dunlop
 * @author Stuart Owen
 * @author Paolo Missier
 * 
 */
public class ErrorProvenanceItem implements ProvenanceItem {

	private Throwable cause;
	private String message;
	private DispatchErrorType errorType;
	private String processId;
	private String parentId;
	private String identifier;

	public ErrorProvenanceItem(Throwable cause, String message,
			DispatchErrorType errorType, String processId) {
		super();
		this.cause = cause;
		this.message = message;
		this.errorType = errorType;
		this.processId = processId;
	}

	public Element getAsXML(ReferenceService referenceService) {
		Element result = new Element("error");
		result.setAttribute("message", message);
		result.setAttribute("type", errorType.toString());
		Element causeElement = new Element("cause");
		String st = "";
		for (StackTraceElement trace : cause.getStackTrace()) {
			st += trace.toString();
			st += "\n";
		}
		causeElement.setText(st);
		result.addContent(causeElement);
		return result;
	}

	public String getAsString() {
		return null;
	}

	public String getEventType() {
		return SharedVocabulary.ERROR_EVENT_TYPE;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public DispatchErrorType getErrorType() {
		return errorType;
	}

	public void setErrorType(DispatchErrorType errorType) {
		this.errorType = errorType;
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
