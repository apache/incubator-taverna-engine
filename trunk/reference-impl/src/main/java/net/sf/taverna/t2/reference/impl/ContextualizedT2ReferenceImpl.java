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

import net.sf.taverna.t2.reference.ContextualizedT2Reference;
import net.sf.taverna.t2.reference.T2Reference;

/**
 * Simple implementation of ContextualizedT2Reference
 * 
 * @author Tom Oinn
 * 
 */
public class ContextualizedT2ReferenceImpl implements ContextualizedT2Reference {

	private T2Reference reference;
	private int[] index;

	public ContextualizedT2ReferenceImpl(T2Reference ref, int[] context) {
		this.reference = ref;
		this.index = context;
	}

	public int[] getIndex() {
		return this.index;
	}

	public T2Reference getReference() {
		return this.reference;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		boolean doneFirst = false;
		for (int i = 0; i < index.length; i++) {
			if (doneFirst) {
				sb.append(",");
			}
			doneFirst = true;
			sb.append(index[i]);
		}
		sb.append("]");
		sb.append(reference.toString());		
		return sb.toString();
	}
	
}
