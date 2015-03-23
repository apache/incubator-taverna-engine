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

import java.util.Set;

import org.apache.taverna.reference.ErrorDocument;
import org.apache.taverna.reference.ErrorDocumentService;
import org.apache.taverna.reference.ErrorDocumentServiceException;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ReferenceServiceException;
import org.apache.taverna.reference.T2Reference;

/**
 * Implementation of ErrorDocumentService, inject with an appropriate
 * ErrorDocumentDao and T2ReferenceGenerator to enable.
 * 
 * @author Tom Oinn
 */
public class ErrorDocumentServiceImpl extends AbstractErrorDocumentServiceImpl
		implements ErrorDocumentService {
	@Override
	public ErrorDocument getError(T2Reference id)
			throws ErrorDocumentServiceException {
		checkDao();
		try {
			return errorDao.get(id);
		} catch (Throwable t) {
			throw new ErrorDocumentServiceException(t);
		}
	}

	/**
	 * Register the specified error and any child errors (which have the same
	 * namespace and local part but a lower depth, down to depth of zero
	 */
	@Override
	public ErrorDocument registerError(String message, Throwable t, int depth,
			ReferenceContext context) throws ErrorDocumentServiceException {
		checkDao();
		checkGenerator();

		T2Reference ref = t2ReferenceGenerator.nextErrorDocumentReference(
				depth, context);
		T2ReferenceImpl typedId = getAsImpl(ref);

		ErrorDocument docToReturn = null;
		for (; depth >= 0; depth--) {
			ErrorDocumentImpl edi = new ErrorDocumentImpl();
			if (docToReturn == null)
				docToReturn = edi;
			edi.setTypedId(typedId);
			if (message != null)
				edi.setMessage(message);
			else
				edi.setMessage("");
			if (t != null) {
				edi.setExceptionMessage(t.toString());
				for (StackTraceElement ste : t.getStackTrace()) {
					StackTraceElementBeanImpl stebi = new StackTraceElementBeanImpl();
					stebi.setClassName(ste.getClassName());
					stebi.setFileName(ste.getFileName());
					stebi.setLineNumber(ste.getLineNumber());
					stebi.setMethodName(ste.getMethodName());
					edi.stackTrace.add(stebi);
				}
			} else
				edi.setExceptionMessage("");
			try {
				errorDao.store(edi);
			} catch (Throwable t2) {
				throw new ErrorDocumentServiceException(t2);
			}
			if (depth > 0)
				typedId = typedId.getDeeperErrorReference();
		}
		return docToReturn;
	}

	@Override
	public ErrorDocument registerError(String message, Set<T2Reference> errors,
			int depth, ReferenceContext context)
			throws ErrorDocumentServiceException {
		checkDao();
		checkGenerator();

		T2Reference ref = t2ReferenceGenerator.nextErrorDocumentReference(
				depth, context);
		T2ReferenceImpl typedId = T2ReferenceImpl.getAsImpl(ref);

		ErrorDocument docToReturn = null;
		for (; depth >= 0; depth--) {
			ErrorDocumentImpl edi = new ErrorDocumentImpl();
			if (docToReturn == null)
				docToReturn = edi;
			edi.setTypedId(typedId);
			if (message != null)
				edi.setMessage(message);
			else
				edi.setMessage("");
			if (errors != null)
				edi.setErrorReferenceSet(errors);
			edi.setExceptionMessage("");

			try {
				errorDao.store(edi);
			} catch (Throwable t2) {
				throw new ErrorDocumentServiceException(t2);
			}
			if (depth > 0)
				typedId = typedId.getDeeperErrorReference();
		}
		return docToReturn;
	}

	@Override
	public T2Reference getChild(T2Reference errorId)
			throws ErrorDocumentServiceException {
		T2ReferenceImpl refImpl = getAsImpl(errorId);
		try {
			return refImpl.getDeeperErrorReference();
		} catch (Throwable t) {
			throw new ErrorDocumentServiceException(t);
		}
	}

	@Override
	public boolean delete(T2Reference reference)
			throws ReferenceServiceException {
		checkDao();
		ErrorDocument doc = errorDao.get(reference);
		if (doc == null)
			return false;
		return errorDao.delete(doc);
	}

	@Override
	public void deleteErrorDocumentsForWorkflowRun(String workflowRunId)
			throws ReferenceServiceException {
		checkDao();
		errorDao.deleteErrorDocumentsForWFRun(workflowRunId);
	}
}
