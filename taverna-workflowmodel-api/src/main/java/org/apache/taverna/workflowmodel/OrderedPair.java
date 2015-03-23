/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.workflowmodel;

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
		if (a == null || b == null)
			throw new RuntimeException(
					"Cannot construct ordered pair with null arguments");
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
		if (!(other instanceof OrderedPair))
			return false;
		OrderedPair<?> op = (OrderedPair<?>) other;
		return (a.equals(op.getA()) && b.equals(op.getB()));
	}

	@Override
	public int hashCode() {
		int aHash = a.hashCode();
		int bHash = b.hashCode();
		return (aHash << 16) | (aHash >> 16) | bHash;
	}
}
