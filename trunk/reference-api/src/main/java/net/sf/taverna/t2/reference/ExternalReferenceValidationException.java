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
package net.sf.taverna.t2.reference;

/**
 * Thrown by setter methods and constructors of ExternalReferenceSPI
 * implementations when fed parameters which cause some kind of format or
 * validation error. These might include badly formed URL or file paths or any
 * other property that fails to validate against some reference type specific
 * scheme.
 * 
 * @author Tom Oinn
 * 
 */
public class ExternalReferenceValidationException extends RuntimeException {

	private static final long serialVersionUID = 3031393671457773057L;

	public ExternalReferenceValidationException() {
		// 
	}

	public ExternalReferenceValidationException(String message) {
		super(message);
	}

	public ExternalReferenceValidationException(Throwable cause) {
		super(cause);
	}

	public ExternalReferenceValidationException(String message, Throwable cause) {
		super(message, cause);
	}

}
