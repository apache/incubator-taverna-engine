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

package org.apache.taverna.reference.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.reference.Identified;
import org.apache.taverna.reference.ReferenceServiceCacheProvider;
import org.apache.taverna.reference.T2Reference;

import org.apache.log4j.Logger;

/**
 * Completely naive cache provider that just stores everything in a map. This
 * <em>will</em> run out of memory as it makes no attempt to evict old items,
 * it's really just here as a test!
 * 
 * @author Tom Oinn
 */
public class SimpleCacheProviderImpl implements ReferenceServiceCacheProvider {
	private final Logger log = Logger.getLogger(SimpleCacheProviderImpl.class);
	private Map<T2Reference, Identified> cache = new HashMap<>();

	@Override
	public Identified get(T2Reference id) {
		if (log.isDebugEnabled())
			log.debug("Get " + id.toString() + " (" + cache.containsKey(id)
					+ ")");
		return cache.get(id);
	}

	@Override
	public void put(Identified i) {
		if (log.isDebugEnabled())
			log.debug("Put " + i.getId().toString());
		cache.put(i.getId(), i);
	}
}
