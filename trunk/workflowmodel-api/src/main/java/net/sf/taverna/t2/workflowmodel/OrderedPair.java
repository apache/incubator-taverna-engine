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
package net.sf.taverna.t2.workflowmodel;

/**
 * A simple generic class to hold a pair of same type objects. Used by various
 * Edit implementations that operate on pairs of Processors amongst other
 * things.
 * 
 * @author Tom Oinn
 * 
 * @param <T>
 *            Type of the pair of contained objects
 */
public class OrderedPair<T> {

	private T a, b;

	/**
	 * Build a new ordered pair with the specified objects.
	 * 
	 * @throws RuntimeException
	 *             if either a or b are null
	 * @param a
	 * @param b
	 */
	public OrderedPair(T a, T b) {
		if (a == null || b == null) {
			throw new RuntimeException(
					"Cannot construct ordered pair with null arguments");
		}
		this.a = a;
		this.b = b;
	}

	/**
	 * Return object a
	 */
	public T getA() {
		return this.a;
	}

	/**
	 * Return object b
	 */
	public T getB() {
		return this.b;
	}

	/**
	 * A pair of objects (a,b) is equal to another pair (c,d) if and only if a,
	 * b, c and d are all the same type and the condition (a.equals(c) &
	 * b.equals(d)) is true.
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof OrderedPair) {
			OrderedPair<?> op = (OrderedPair<?>) other;
			return (a.equals(op.getA()) && b.equals(op.getB()));
		} else {
			return false;
		}
	}

}
