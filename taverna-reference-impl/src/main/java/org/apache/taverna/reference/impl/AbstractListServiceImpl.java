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

import org.apache.taverna.reference.ListDao;
import org.apache.taverna.reference.ListService;
import org.apache.taverna.reference.ListServiceCallback;
import org.apache.taverna.reference.ListServiceException;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.T2ReferenceGenerator;

/**
 * Abstract implementation of ListService, inject with an appropriate ListDao
 * and T2ReferenceGenerator to enable. Contains injectors for id generation and
 * dao along with other bookkeeping, leaving the implementation of the actual
 * service logic to the subclass.
 * 
 * @author Tom Oinn
 * 
 */
public abstract class AbstractListServiceImpl extends AbstractServiceImpl
		implements ListService {
	protected ListDao listDao = null;
	protected T2ReferenceGenerator t2ReferenceGenerator = null;

	/**
	 * Inject the list data access object.
	 */
	public final void setListDao(ListDao dao) {
		listDao = dao;
	}

	/**
	 * Inject the T2Reference generator used to allocate new IDs when
	 * registering lists of T2Reference
	 */
	public final void setT2ReferenceGenerator(T2ReferenceGenerator t2rg) {
		t2ReferenceGenerator = t2rg;
	}

	/**
	 * Check that the list dao is configured
	 * 
	 * @throws ListServiceException
	 *             if the dao is still null
	 */
	protected final void checkDao() throws ListServiceException {
		if (listDao == null)
			throw new ListServiceException("ListDao not initialized, list "
					+ "service operations are not available");
	}

	/**
	 * Check that the t2reference generator is configured
	 * 
	 * @throws ListServiceException
	 *             if the generator is still null
	 */
	protected final void checkGenerator() throws ListServiceException {
		if (t2ReferenceGenerator == null)
			throw new ListServiceException(
					"T2ReferenceGenerator not initialized, list "
							+ "service operations not available");
	}

	@Override
	public final void getListAsynch(final T2Reference id,
			final ListServiceCallback callback) throws ListServiceException {
		checkDao();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					callback.listRetrieved(getList(id));
				} catch (ListServiceException lse) {
					callback.listRetrievalFailed(lse);
				}
			}
		};
		executeRunnable(r);
	}
}
