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
import org.apache.taverna.reference.IdentifiedList;
import org.apache.taverna.reference.ListDao;
import org.apache.taverna.reference.T2Reference;

/**
 * A trivial in-memory implementation of ListDao for either testing or very
 * lightweight embedded systems. Uses a java Map as the backing store.
 * 
 * @author Tom Oinn
 */
public class InMemoryListDao implements ListDao {
	private Map<T2Reference, IdentifiedList<T2Reference>> store;

	public InMemoryListDao() {
		this.store = new ConcurrentHashMap<>();
	}

	@Override
	public synchronized IdentifiedList<T2Reference> get(T2Reference reference)
			throws DaoException {
		return store.get(reference);
	}

	@Override
	public synchronized void store(IdentifiedList<T2Reference> theList)
			throws DaoException {
		store.put(theList.getId(), theList);
	}

	@Override
	public boolean delete(IdentifiedList<T2Reference> theList)
			throws DaoException {
		return (store.remove(theList.getId()) != null);
	}

	@Override
	public synchronized void deleteIdentifiedListsForWFRun(String workflowRunId)
			throws DaoException {
		for (T2Reference reference : store.keySet())
			if (reference.getNamespacePart().equals(workflowRunId))
				store.remove(reference);
	}
}
