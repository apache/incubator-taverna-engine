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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of SetModel based on a HashSet
 * 
 * @author Tom Oinn
 */
public class HashSetModel<ItemType> extends HashSet<ItemType> implements
		SetModel<ItemType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5763277571663880941L;
	// Listeners for set change events
	private List<SetModelChangeListener<ItemType>> changeListeners;

	/**
	 * Default constructor, creates a set model based on a HashSet
	 */
	public HashSetModel() {
		super();
		changeListeners = new ArrayList<SetModelChangeListener<ItemType>>();
	}

	/**
	 * Implements SetModel
	 */
	public synchronized void addSetModelChangeListener(
			SetModelChangeListener<ItemType> listener) {
		changeListeners.add(listener);
	}

	/**
	 * Implements SetModel
	 */
	public synchronized void removeSetModelChangeListener(
			SetModelChangeListener<ItemType> listener) {
		changeListeners.remove(listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void clear() {
		notifyRemoval((Set<Object>) this);
		super.clear();
	}

	@Override
	public synchronized boolean add(ItemType item) {
		if (super.add(item)) {
			notifyAddition(Collections.singleton(item));
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean remove(Object item) {
		if (super.remove(item)) {
			notifyRemoval(Collections.singleton(item));
			return true;
		}
		return false;
	}

	/**
	 * Push addition notification to listeners
	 * 
	 * @param itemsAdded
	 */
	private synchronized void notifyAddition(Set<ItemType> itemsAdded) {
		for (SetModelChangeListener<ItemType> listener : new ArrayList<SetModelChangeListener<ItemType>>(
				changeListeners)) {
			listener.itemsWereAdded(itemsAdded);
		}
	}

	/**
	 * Push removal notification to listeners
	 * 
	 * @param itemsRemoved
	 */
	private synchronized void notifyRemoval(Set<Object> itemsRemoved) {
		for (SetModelChangeListener<ItemType> listener : new ArrayList<SetModelChangeListener<ItemType>>(
				changeListeners)) {
			listener.itemsWereRemoved(itemsRemoved);
		}
	}
}
