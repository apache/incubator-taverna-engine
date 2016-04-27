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
import org.apache.taverna.reference.ErrorDocument;
import org.apache.taverna.reference.ErrorDocumentDao;
import org.apache.taverna.reference.T2Reference;

/**
 * A trivial in-memory implementation of ErrorDocumentDao for either testing or
 * very lightweight embedded systems. Uses a java Map as the backing store.
 * 
 */
public class InMemoryErrorDocumentDao implements ErrorDocumentDao {
	private Map<T2Reference, ErrorDocument> store;

	public InMemoryErrorDocumentDao() {
		this.store = new ConcurrentHashMap<>();
	}

	@Override
	public synchronized ErrorDocument get(T2Reference reference)
			throws DaoException {
		return store.get(reference);
	}

	@Override
	public synchronized void store(ErrorDocument theDoc) throws DaoException {
		store.put(theDoc.getId(), theDoc);
	}

	@Override
	public synchronized boolean delete(ErrorDocument theDoc)
			throws DaoException {
		return store.remove(theDoc.getId()) != null;
	}

	@Override
	public synchronized void deleteErrorDocumentsForWFRun(String workflowRunId)
			throws DaoException {
		for (T2Reference reference : store.keySet())
			if (reference.getNamespacePart().equals(workflowRunId))
				store.remove(reference);
	}

}
