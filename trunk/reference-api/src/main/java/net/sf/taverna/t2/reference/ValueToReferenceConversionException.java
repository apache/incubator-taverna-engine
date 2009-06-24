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
 * Thrown by instances of ValueToReferenceConvertor when trying to convert an
 * object to an instance of ExternalReferenceSPI if the conversion process fails
 * for some reason.
 * 
 * @author Tom Oinn
 * 
 */
public class ValueToReferenceConversionException extends RuntimeException {

	private static final long serialVersionUID = 3259959719223191820L;

	public ValueToReferenceConversionException() {
		// 
	}

	public ValueToReferenceConversionException(String message) {
		super(message);
	}

	public ValueToReferenceConversionException(Throwable cause) {
		super(cause);
	}

	public ValueToReferenceConversionException(String message, Throwable cause) {
		super(message, cause);
	}

}
