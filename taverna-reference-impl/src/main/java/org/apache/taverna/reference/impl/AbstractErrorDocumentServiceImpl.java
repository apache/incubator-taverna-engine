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

import org.apache.taverna.reference.DaoException;
import org.apache.taverna.reference.ErrorDocument;
import org.apache.taverna.reference.ErrorDocumentDao;
import org.apache.taverna.reference.ErrorDocumentService;
import org.apache.taverna.reference.ErrorDocumentServiceCallback;
import org.apache.taverna.reference.ErrorDocumentServiceException;
import org.apache.taverna.reference.ListServiceException;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.T2ReferenceGenerator;

/**
 * Abstract implementation of ErrorDocumentService, inject with an appropriate
 * ErrorDocumentDao and T2ReferenceGenerator to enable. Contains injectors for
 * id generation and dao along with other bookkeeping, leaving the
 * implementation of the actual service logic to the subclass.
 * 
 * @author Tom Oinn
 */
public abstract class AbstractErrorDocumentServiceImpl extends
		AbstractServiceImpl implements ErrorDocumentService {
	protected ErrorDocumentDao errorDao = null;
	protected T2ReferenceGenerator t2ReferenceGenerator = null;

	/**
	 * Inject the error document data access object.
	 */
	public final void setErrorDao(ErrorDocumentDao dao) {
		errorDao = dao;
	}

	/**
	 * Inject the T2Reference generator used to allocate new IDs when
	 * registering ErrorDocuments
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
	protected final void checkDao() throws ErrorDocumentServiceException {
		if (errorDao == null)
			throw new ErrorDocumentServiceException(
					"ErrorDocumentDao not initialized, error document "
							+ "service operations are not available");
	}

	/**
	 * Check that the t2reference generator is configured
	 * 
	 * @throws ListServiceException
	 *             if the generator is still null
	 */
	protected final void checkGenerator() throws ErrorDocumentServiceException {
		if (t2ReferenceGenerator == null)
			throw new ErrorDocumentServiceException(
					"T2ReferenceGenerator not initialized, error document "
							+ "service operations not available");
	}

	@Override
	public final void getErrorAsynch(final T2Reference id,
			final ErrorDocumentServiceCallback callback)
			throws ErrorDocumentServiceException {
		checkDao();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					ErrorDocument e = errorDao.get(id);
					callback.errorRetrieved(e);
				} catch (DaoException de) {
					callback.errorRetrievalFailed(new ErrorDocumentServiceException(
							de));
				}
			}
		};
		executeRunnable(r);
	}

	@Override
	public final ErrorDocument registerError(String message, int depth,
			ReferenceContext context) throws ErrorDocumentServiceException {
		return registerError(message, (Throwable) null, depth, context);
	}

	@Override
	public final ErrorDocument registerError(Throwable t, int depth,
			ReferenceContext context) throws ErrorDocumentServiceException {
		return registerError("", t, depth, context);
	}
}
