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
