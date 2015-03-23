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

import static org.apache.taverna.reference.impl.T2ReferenceImpl.getAsImpl;

import java.util.List;

import org.apache.taverna.reference.DaoException;
import org.apache.taverna.reference.IdentifiedList;
import org.apache.taverna.reference.ListService;
import org.apache.taverna.reference.ListServiceException;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ReferenceServiceException;
import org.apache.taverna.reference.T2Reference;

/**
 * Implementation of ListService, inject with an appropriate ListDao and
 * T2ReferenceGenerator to enable.
 * 
 * @author Tom Oinn
 */
public class ListServiceImpl extends AbstractListServiceImpl implements
		ListService {
	@Override
	public IdentifiedList<T2Reference> getList(T2Reference id)
			throws ListServiceException {
		checkDao();
		try {
			return listDao.get(id);
		} catch (DaoException de) {
			throw new ListServiceException(de);
		}
	}

	@Override
	public IdentifiedList<T2Reference> registerEmptyList(int depth,
			ReferenceContext context) throws ListServiceException {
		if (depth < 1)
			throw new ListServiceException(
					"Can't register empty lists of depth " + depth);
		checkDao();
		checkGenerator();
		T2ReferenceImpl newReference = getAsImpl(t2ReferenceGenerator
				.nextListReference(false, depth, context));
		T2ReferenceListImpl newList = new T2ReferenceListImpl();
		newList.setTypedId(newReference);
		try {
			listDao.store(newList);
			return newList;
		} catch (DaoException de) {
			throw new ListServiceException(de);
		}
	}

	@Override
	public IdentifiedList<T2Reference> registerList(List<T2Reference> items,
			ReferenceContext context) throws ListServiceException {
		checkDao();
		checkGenerator();
		if (items.isEmpty())
			throw new ListServiceException(
					"Can't register an empty list with this method,"
							+ " use the registerEmptyList instead");
		/*
		 * Track whether there are any items in the collection which are or
		 * contain error documents.
		 */
		boolean containsErrors = false;
		// Track depth, ensure that all items have the same depth, fail if not.
		int depth = items.get(0).getDepth();
		if (depth < 0)
			throw new ListServiceException(
					"Can't register list of depth less than 1, but first item "
							+ items.get(0) + " has depth " + depth);
		T2ReferenceListImpl newList = new T2ReferenceListImpl();
		int counter = 0;
		for (T2Reference ref : items) {
			if (ref.getDepth() != depth)
				throw new ListServiceException(
						"Mismatched depths in list registration; reference at index '"
								+ counter + "' has depth " + ref.getDepth()
								+ " but all preceeding items have depth "
								+ depth);
			if (ref.containsErrors())
				// The collection's reference contains errors if any child does
				containsErrors = true;
			newList.add(ref);
			counter++;
		}
		try {
			T2ReferenceImpl newReference = getAsImpl(t2ReferenceGenerator
					.nextListReference(containsErrors, depth + 1, context));
			newList.setTypedId(newReference);
			listDao.store(newList);
			return newList;
		} catch (Throwable t) {
			throw new ListServiceException(t);
		}
	}

	@Override
	public boolean delete(T2Reference reference)
			throws ReferenceServiceException {
		checkDao();
		IdentifiedList<T2Reference> list = listDao.get(reference);
		if (list == null)
			return false;
		return listDao.delete(list);
	}

	@Override
	public void deleteIdentifiedListsForWorkflowRun(String workflowRunId)
			throws ReferenceServiceException {
		checkDao();
		listDao.deleteIdentifiedListsForWFRun(workflowRunId);
	}
}
