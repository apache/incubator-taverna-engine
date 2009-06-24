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
	private String errorType;
	private String processId;
	private String parentId;
	private String identifier;
	private SharedVocabulary eventType = SharedVocabulary.ERROR_EVENT_TYPE;

	public ErrorProvenanceItem() {
	}

	public SharedVocabulary getEventType() {
		return eventType;
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

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getParentId() {
		return parentId;
	}


	public void setParentId(String parentId) {
		this.parentId = parentId;

	}

	public String getProcessId() {
		return processId;
	}

}
