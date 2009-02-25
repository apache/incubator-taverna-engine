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
package net.sf.taverna.t2.reference.impl;

import java.util.List;

import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.h3.HibernateMappedEntity;

/**
 * Simple extension of
 * <code>{@link IdentifiedArrayList IdentifiedArrayList&lt;T2Reference&gt;}</code>
 * exposing get and set methods for the list contents so we can map it in
 * hibernate.
 * 
 * @author Tom Oinn
 * 
 */
public class T2ReferenceListImpl extends IdentifiedArrayList<T2Reference>
		implements HibernateMappedEntity {

	public T2ReferenceListImpl() {
		super();
	}

	/**
	 * This is only called from Hibernate, outside of test code, so is
	 * relatively safe to leave unchecked.
	 */
	@SuppressWarnings("unchecked")
	public List getListContents() {
		return this.listDelegate;
	}

	/**
	 * This is only called from Hibernate, outside of test code, so is
	 * relatively safe to leave unchecked.
	 */
	@SuppressWarnings("unchecked")
	public void setListContents(List newList) {
		this.listDelegate = newList;
	}

	/**
	 * Print the contents of this list for vaguely human readable debug
	 * porpoises.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getId().toString() + "\n");
		int counter = 0;
		for (T2Reference ref : listDelegate) {
			sb.append("  " + (++counter) + ") " + ref.toString() + "\n");
		}
		return sb.toString();
	}

}
