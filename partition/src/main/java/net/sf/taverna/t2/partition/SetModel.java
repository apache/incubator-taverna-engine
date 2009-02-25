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
package net.sf.taverna.t2.partition;

import java.util.Set;

/**
 * Extension of the java Set interface with the addition of change listener
 * support. Intended to be plugged into the RootPartition class so the partition
 * is synchronized with the set membership.
 * 
 * @author Tom Oinn
 * 
 * @param <ItemType>
 *            the parameterised type of the set
 */
public interface SetModel<ItemType> extends Set<ItemType> {

	/**
	 * Add a listener to be notified of change events on the set's membership
	 * 
	 * @param listener
	 */
	public void addSetModelChangeListener(
			SetModelChangeListener<ItemType> listener);

	/**
	 * Remove a previously registered change listener
	 * 
	 * @param listener
	 */
	public void removeSetModelChangeListener(
			SetModelChangeListener<ItemType> listener);

}
