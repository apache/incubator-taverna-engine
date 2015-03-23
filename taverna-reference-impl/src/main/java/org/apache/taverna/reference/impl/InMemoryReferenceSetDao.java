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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.taverna.reference.DaoException;
import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.reference.ReferenceSetDao;
import org.apache.taverna.reference.T2Reference;

/**
 * A trivial in-memory implementation of ReferenceSetDao for either testing or
 * very lightweight embedded systems. Uses a java Map as the backing store.
 * 
 * @author Tom Oinn
 */
public class InMemoryReferenceSetDao implements ReferenceSetDao {
	private Map<T2Reference, ReferenceSet> store;

	public InMemoryReferenceSetDao() {
		this.store = new ConcurrentHashMap<>();
	}

	@Override
	public synchronized ReferenceSet get(T2Reference reference)
			throws DaoException {
		return store.get(reference);
	}

	@Override
	public synchronized void store(ReferenceSet refSet) throws DaoException {
		store.put(refSet.getId(), refSet);
	}

	@Override
	public synchronized void update(ReferenceSet refSet) throws DaoException {
		store.put(refSet.getId(), refSet);
	}

	@Override
	public synchronized boolean delete(ReferenceSet refSet) throws DaoException {
		return store.remove(refSet.getId()) != null;
	}

	@Override
	public synchronized void deleteReferenceSetsForWFRun(String workflowRunId)
			throws DaoException {
		for (T2Reference reference : store.keySet())
			if (reference.getNamespacePart().equals(workflowRunId))
				store.remove(reference);
	}
}
