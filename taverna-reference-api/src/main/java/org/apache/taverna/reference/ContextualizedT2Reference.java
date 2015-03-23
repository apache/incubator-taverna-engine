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

package org.apache.taverna.reference;

/**
 * Used by the {@link ReferenceService#traverseFrom(T2Reference, int)} when
 * traversing a collection structure. Each contextualized t2reference contains
 * the {@link T2Reference} along with an integer array index representing the
 * position of that reference within the traversal structure. The index
 * [i<sub>0</sub>,i<sub>1</sub>,i<sub>2</sub> ... i<sub>n</sub>] is interpreted
 * such that the reference is located at
 * parent.get(i<sub>0</sub>).get(i<sub>1</sub
 * >).get(i<sub>2</sub>)....get(i<sub>n</sub>). If the index is empty then the
 * T2Reference <em>is</em> the original reference supplied to the
 * {@link ReferenceService#traverseFrom(T2Reference, int) traverseFrom} method.
 * 
 * @author Tom Oinn
 */
public interface ContextualizedT2Reference {
	/**
	 * @return the T2Reference to which the associated index applies.
	 */
	T2Reference getReference();

	/**
	 * @return the index of this T2Reference
	 */
	int[] getIndex();
}
