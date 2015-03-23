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

import static org.springframework.transaction.annotation.Propagation.REQUIRED;
import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

import org.springframework.transaction.annotation.Transactional;

/**
 * Data access object handling ErrorDocument instances.
 * 
 * @author Tom Oinn
 */
public interface ErrorDocumentDao {
	/**
	 * Store a named ErrorDocument to the database.
	 * 
	 * @param errorDoc
	 *            error document to store
	 * @throws DaoException
	 *             if any exception is thrown when connecting to the underlying
	 *             store or when storing the error document
	 */
	@Transactional(propagation = REQUIRED, readOnly = false)
	void store(ErrorDocument errorDoc) throws DaoException;

	/**
	 * Retrieves a named and populated ErrorDocument
	 * 
	 * @param reference
	 *            id of the error document to retrieve
	 * @return a previously stored ErrorDocument instance
	 * @throws DaoException
	 *             if any exception is thrown when connecting to the underlying
	 *             data store or when attempting retrieval of the error document
	 */
	@Transactional(propagation = SUPPORTS, readOnly = true)
	ErrorDocument get(T2Reference reference) throws DaoException;

	@Transactional(propagation = SUPPORTS, readOnly = false)
	boolean delete(ErrorDocument errorDoc) throws DaoException;

	@Transactional(propagation = SUPPORTS, readOnly = false)
	void deleteErrorDocumentsForWFRun(String workflowRunId) throws DaoException;
}
