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

import java.util.Set;

/**
 * A set of ExternalReferenceSPI instances, all of which point to the same (byte
 * equivalent) data. The set is identified by a T2Reference. This interface is
 * read-only, as are most of the interfaces in this package. Rather than
 * modifying properties of the reference set directly the client code should use
 * the reference manager functionality.
 * <p>
 * It is technically okay, but rather unhelpful, to have a ReferenceSet with no
 * ExternalReferenceSPI implementations. In general this is a sign that
 * something has gone wrong somewhere as the reference set will not be
 * resolvable in any way, but it would still retain its unique identifier so
 * there may be occasions where this is the desired behaviour.
 * 
 * @author Tom Oinn
 * 
 */
public interface ReferenceSet extends Identified {

	/**
	 * The reference set contains a set of ExternalReferenceSPI instances, all
	 * of which point to byte equivalent data.
	 * 
	 * @return the set of references to external data
	 */
	public Set<ExternalReferenceSPI> getExternalReferences();

}
