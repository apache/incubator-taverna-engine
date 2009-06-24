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

import java.util.List;

/**
 * An identified list is a list which is identified by a T2Reference. Lists are
 * immutable once named - if getId() returns a non null value all list methods
 * modifying the underlying list data will throw IllegalStateException. In the
 * reference management API this list sub-interface is used to represent both
 * collections of identifiers (i.e. 'raw' stored lists) and more fully resolved
 * structures where the types in the list can be reference sets, error documents
 * and other lists of such. The ListDao interface uses only the 'raw' form
 * consisting of flat lists of identifiers.
 * <p>
 * The IdentifiedList has a unique T2Reference associated with it. If this is
 * null the contents of the list may be modified, otherwise all modification
 * operations throw IllegalStateException. Lists in T2, once named, are
 * immutable.
 * 
 * @author Tom Oinn
 * 
 * @param <T>
 */
public interface IdentifiedList<T> extends List<T>, Identified {

}
