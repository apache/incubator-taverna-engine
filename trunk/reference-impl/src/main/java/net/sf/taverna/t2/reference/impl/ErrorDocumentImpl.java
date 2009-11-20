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
package net.sf.taverna.t2.reference.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.StackTraceElementBean;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.h3.HibernateMappedEntity;

/**
 * Simple bean implementation of ErrorDocument
 * 
 * @author Tom Oinn
 * 
 */
public class ErrorDocumentImpl extends AbstractEntityImpl implements
		ErrorDocument, HibernateMappedEntity {

	private String exceptionMessage = "";
	private String message = "";
	List<StackTraceElementBean> stackTrace;
	Set<T2Reference> errorReferences = new HashSet<T2Reference>();
	
	public ErrorDocumentImpl() {
		this.stackTrace = new ArrayList<StackTraceElementBean>();
	}

	public String getExceptionMessage() {
		return this.exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * From interface, not used by hibernate internally
	 */
	public List<StackTraceElementBean> getStackTraceStrings() {
		return this.stackTrace;
	}

	/**
	 * Used by Hibernate to bodge around problems with interface types in the
	 * API
	 */
	@SuppressWarnings("unchecked")
	public void setStackTraceList(List newList) {
		this.stackTrace = newList;
	}

	/**
	 * Used by Hibernate to bodge around problems with interface types in the
	 * API
	 */
	@SuppressWarnings("unchecked")
	public List getStackTraceList() {
		return this.stackTrace;
	}

	public Set<T2Reference> getErrorReferences() {
		return errorReferences;
	}

	/**
	 * Used by Hibernate to bodge around problems with interface types in the
	 * API
	 */
	@SuppressWarnings("unchecked")
	public void setErrorReferenceSet(Set errorReferenceSet) {
		this.errorReferences = errorReferenceSet;
	}
	
	/**
	 * Used by Hibernate to bodge around problems with interface types in the
	 * API
	 */
	@SuppressWarnings("unchecked")
	public Set getErrorReferenceSet() {
		return this.errorReferences;
	}

}
