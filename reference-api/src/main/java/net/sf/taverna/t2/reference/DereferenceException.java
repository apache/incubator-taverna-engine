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
 * Thrown when a problem occurs during de-reference of an ExternalReferenceSPI
 * implementation. This include operations which implicitly de-reference the
 * reference such as those infering character set or data natures.
 * 
 * @author Tom Oinn
 * 
 */
public class DereferenceException extends RuntimeException {

	private static final long serialVersionUID = 8054381613840005541L;

	public DereferenceException() {
		// 
	}

	public DereferenceException(String message) {
		super(message);
	}

	public DereferenceException(Throwable cause) {
		super(cause);
	}

	public DereferenceException(String message, Throwable cause) {
		super(message, cause);
	}

}
