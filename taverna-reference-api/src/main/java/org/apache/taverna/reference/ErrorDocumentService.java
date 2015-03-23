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

import java.util.Set;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides facilities to register list of T2References, register empty lists at
 * any given depth and to resolve appropriate T2Reference instances back to
 * these lists. Registration operations assign names and lock the list contents
 * as a result. This service operates strictly on T2References, it neither tries
 * to nor is capable of any form of reference resolution, so aspects such as
 * collection traversal are not handled here (these are performed by the top
 * level reference service)
 * 
 * @author Tom Oinn
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public interface ErrorDocumentService {
	/**
	 * Register a new error document.
	 * <p>
	 * The created reference will be related with a workflow run id passed
	 * through ReferenceContext so we can track all data referenced by a
	 * specific run.
	 * 
	 * @param message
	 *            a free text message describing the error, if available. If
	 *            there is no message use the empty string here.
	 * @param t
	 *            a Throwable describing the underlying fault causing this error
	 *            document to be registered, if any. If there is no Throwable
	 *            associated use null.
	 * @param depth
	 *            depth of the error, used when returning an error document
	 *            instead of an identified list.
	 * @return a new ErrorDocument instance, constructed fully and stored in the
	 *         underlying storage system
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	ErrorDocument registerError(String message, Throwable t, int depth,
			ReferenceContext context) throws ErrorDocumentServiceException;

	/**
	 * Equivalent to <code>registerError(message, null, depth, context)</code>.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	ErrorDocument registerError(String message, int depth,
			ReferenceContext context) throws ErrorDocumentServiceException;

	/**
	 * Equivalent to <code>registerError("", t, depth, context)</code>.
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	ErrorDocument registerError(Throwable t, int depth, ReferenceContext context)
			throws ErrorDocumentServiceException;

	/**
	 * Register a new error document.
	 * <p>
	 * The created reference will be related with a workflow run id passed
	 * through ReferenceContext so we can track all data referenced by a
	 * specific run.
	 * 
	 * @param message
	 *            a free text message describing the error, if available. If
	 *            there is no message use the empty string here.
	 * @param errors
	 *            a set of references that contain error documents.
	 * @param depth
	 *            depth of the error, used when returning an error document
	 *            instead of an identified list.
	 * @return a new ErrorDocument instance, constructed fully and stored in the
	 *         underlying storage system
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	ErrorDocument registerError(String message, Set<T2Reference> errors,
			int depth, ReferenceContext context)
			throws ErrorDocumentServiceException;

	/**
	 * Retrieve a previously named and registered ErrorDocument from the backing
	 * store
	 * 
	 * @param id
	 *            identifier of the error document to retrieve
	 * @return an ErrorDocument
	 * @throws ErrorDocumentServiceException
	 *             if anything goes wrong with the retrieval process or if there
	 *             is something wrong with the reference (such as it being of
	 *             the wrong reference type).
	 */
	ErrorDocument getError(T2Reference id) throws ErrorDocumentServiceException;

	/**
	 * Functionality the same as {@link #getError(T2Reference) getError} but in
	 * asynchronous mode, returning immediately and using the supplied callback
	 * to communicate its results.
	 * 
	 * @param id
	 *            a {@link T2Reference} identifying an {@link ErrorDocument} to
	 *            retrieve
	 * @param callback
	 *            a {@link ErrorDocumentServiceCallback} used to convey the
	 *            results of the asynchronous call
	 * @throws ErrorDocumentServiceException
	 *             if the reference set service is not correctly configured.
	 *             Exceptions encountered when performing the asynchronous call
	 *             are not returned here, for obvious reasons, and are instead
	 *             messaged through the callback interface.
	 */
	void getErrorAsynch(T2Reference id, ErrorDocumentServiceCallback callback)
			throws ErrorDocumentServiceException;

	/**
	 * Return the T2Reference for the sole child of an error document
	 * identifier.
	 */
	T2Reference getChild(T2Reference errorId)
			throws ErrorDocumentServiceException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
	boolean delete(T2Reference reference) throws ReferenceServiceException;

	/**
	 * Delete all {@link ErrorDocument}S used by the specific workflow run.
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
	void deleteErrorDocumentsForWorkflowRun(String workflowRunId)
			throws ReferenceServiceException;
}
