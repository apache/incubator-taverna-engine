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
 * Data Access Object interface for {@link ReferenceSet}. Used by the
 * {@link ReferenceSetService} to store and retrieve implementations of
 * reference set to and from the database. Client code should use the reference
 * set service rather than using this Dao directly.
 * <p>
 * All methods throw DaoException, and nothing else. Where a deeper error is
 * propagated it is wrapped in a DaoException and passed on to the caller.
 * 
 * @author Tom Oinn
 */
public interface ReferenceSetDao {
	/**
	 * Store the specified new reference set
	 * 
	 * @param rs
	 *            a reference set, must not already exist in the database.
	 * @throws DaoException
	 *             if the entry already exists in the database or some other
	 *             database related problem occurs
	 */
	@Transactional(propagation = REQUIRED, readOnly = false)
	void store(ReferenceSet rs) throws DaoException;

	/**
	 * Update a pre-existing entry in the database
	 * 
	 * @param rs
	 *            the reference set to update. This must already exist in the
	 *            database
	 * @throws DaoException
	 */
	@Transactional(propagation = REQUIRED, readOnly = false)
	void update(ReferenceSet rs) throws DaoException;

	/**
	 * Fetch a reference set by id
	 * 
	 * @param ref
	 *            the T2Reference to fetch
	 * @return a retrieved ReferenceSet
	 * @throws DaoException
	 *             if the supplied reference is of the wrong type or if
	 *             something goes wrong fetching the data or connecting to the
	 *             database
	 */
	@Transactional(propagation = SUPPORTS, readOnly = true)
	ReferenceSet get(T2Reference ref) throws DaoException;

	@Transactional(propagation = SUPPORTS, readOnly = false)
	boolean delete(ReferenceSet rs) throws DaoException;

	@Transactional(propagation = SUPPORTS, readOnly = false)
	void deleteReferenceSetsForWFRun(String workflowRunId) throws DaoException;
}
