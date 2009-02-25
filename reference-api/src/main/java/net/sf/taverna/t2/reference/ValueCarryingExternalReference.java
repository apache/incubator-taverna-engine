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
 * Specialization of ExternalReferenceSPI for reference types which carry a
 * value type internally. Such references can be de-referenced to the specified
 * object type very cheaply. Note that this is not to be used to get an object
 * property of a reference, the returned object must correspond to the value of
 * the referenced data - this means that the HttpUrlReference does not use this
 * to return a java.net.URL, but that the InlineStringReference does use it to
 * return a java.lang.String
 * 
 * @author Tom Oinn
 * 
 */
public interface ValueCarryingExternalReference<T> extends ExternalReferenceSPI {

	/**
	 * Returns the type of the inlined value
	 */
	public Class<T> getValueType();

	/**
	 * Returns the value
	 */
	public T getValue();

}
