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
 * Used by the {@link ReferenceService#traverseFrom(T2Reference, int)} when
 * traversing a collection structure. Each contextualized t2reference contains
 * the {@link T2Reference} along with an integer array index representing the
 * position of that reference within the traversal structure. The index [i<sub>0</sub>,i<sub>1</sub>,i<sub>2</sub>
 * ... i<sub>n</sub>] is interpreted such that the reference is located at
 * parent.get(i<sub>0</sub>).get(i<sub>1</sub>).get(i<sub>2</sub>)....get(i<sub>n</sub>).
 * If the index is empty then the T2Reference <em>is</em> the original
 * reference supplied to the {@link ReferenceService#traverseFrom(T2Reference, int) traverseFrom} method.
 * 
 * @author Tom Oinn
 * 
 */
public interface ContextualizedT2Reference {

	/**
	 * @return the T2Reference to which the associated index applies.
	 */
	public T2Reference getReference();
	
	/**
	 * @return the index of this T2Reference
	 */
	public int[] getIndex();
	
}
