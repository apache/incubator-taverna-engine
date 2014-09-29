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
 * A simple interface to be implemented by data access object cache providers,
 * intended to be used to inject cache implementations through AoP
 * 
 * @author Tom Oinn
 */
public interface ReferenceServiceCacheProvider {
	/**
	 * Called after an {@link Identified} has been written to the backing store,
	 * either for the first time or after modification. In our model
	 * {@link ReferenceSet} is the only {@link Identified} that is modifiable,
	 * specifically only by the addition of {@link ExternalReferenceSPI}
	 * instances to its reference set.
	 * 
	 * @param i
	 *            the Identified written to the backing store
	 */
	void put(Identified i);

	/**
	 * Called before an attempt is made to retrieve an item from the backing
	 * store
	 * 
	 * @param id
	 *            the T2Reference of the item to retrieve
	 * @return a cached item with matching {@link T2Reference}, or <tt>null</tt>
	 *         if the cache does not contain that item
	 */
	Identified get(T2Reference id);
}
