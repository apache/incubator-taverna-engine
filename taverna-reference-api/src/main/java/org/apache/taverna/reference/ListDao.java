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
 * Data access object handling NamedLists of T2Reference instances.
 * 
 * @author Tom Oinn
 */
public interface ListDao {
	/**
	 * Store a named and populated IdentifiedList of T2Reference to the
	 * database.
	 * 
	 * @param theList
	 *            list to store
	 * @throws DaoException
	 *             if any exception is thrown when connecting to the underlying
	 *             store or when storing the list
	 */
	@Transactional(propagation = REQUIRED, readOnly = false)
	void store(IdentifiedList<T2Reference> theList) throws DaoException;

	/**
	 * Retrieves a named and populated IdentifiedList of T2Reference from the
	 * database by T2Reference
	 * 
	 * @param reference
	 *            id of the list to retrieve
	 * @return a previously stored list of T2References
	 * @throws DaoException
	 *             if any exception is thrown when connecting to the underlying
	 *             data store or when attempting retrieval of the list
	 */
	@Transactional(propagation = SUPPORTS, readOnly = true)
	IdentifiedList<T2Reference> get(T2Reference reference) throws DaoException;

	@Transactional(propagation = SUPPORTS, readOnly = false)
	boolean delete(IdentifiedList<T2Reference> theList) throws DaoException;

	@Transactional(propagation = SUPPORTS, readOnly = false)
	void deleteIdentifiedListsForWFRun(String workflowRunId)
			throws DaoException;
}
