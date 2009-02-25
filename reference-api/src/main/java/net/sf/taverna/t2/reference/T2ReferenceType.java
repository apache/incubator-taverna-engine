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
 * The T2Reference interface is used to identify several different kinds of
 * information, namely ReferenceSet, IdentifiedList and ErrorDocument. Because
 * the top level reference service needs to determine which sub-service to
 * delegate to when resolving references we carry this information in each
 * T2Reference in the form of one of these enumerated types.
 * 
 * @author Tom Oinn
 * 
 */
public enum T2ReferenceType {

	/**
	 * A reference to a ReferenceSet
	 */
	ReferenceSet,
	
	/**
	 * A reference to an IdentifiedList of other T2References
	 */
	IdentifiedList,
	
	/**
	 * A reference to an ErrorDocument
	 */
	ErrorDocument;

}
