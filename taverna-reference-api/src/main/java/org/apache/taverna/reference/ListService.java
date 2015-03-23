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

import java.util.List;

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
@Transactional(propagation = SUPPORTS, readOnly = true)
public interface ListService {
	/**
	 * Register a new list of T2References. The depth of the list will be
	 * calculated based on the depth of the references within it - if these are
	 * not uniform the list won't be created (all children of a list in T2 must
	 * have the same depth as their siblings). Provided this constraint is
	 * satisfied the list is named and stored in the backing store. The returned
	 * list is at this point immutable, operations modifying it either directly
	 * or through the ListIterator will fail with an IllegalStateException.
	 * Implementations should copy the input list rather than keeping a
	 * reference to it to preserve this property.
	 * <p>
	 * The created references will be related with a workflow run id passed
	 * through ReferenceContext so we can track all data referenced by a
	 * specific run.
	 * 
	 * @param items
	 *            the T2Reference instances to store as a list.
	 * @return a new IdentifiedList of T2Reference instances allocated with a
	 *         T2Reference itself as the unique name and cached by the backing
	 *         store.
	 * @throws ListServiceException
	 *             if there is a problem either with the specified list of
	 *             references or with the storage subsystem.
	 */
	@Transactional(propagation = REQUIRED, readOnly = false)
	IdentifiedList<T2Reference> registerList(List<T2Reference> items,
			ReferenceContext context) throws ListServiceException;

	/**
	 * Register a new empty list with the specified depth. This is needed
	 * because in the case of empty lists we can't calculate the depth from the
	 * list items (what with there not being any!), but the depth property is
	 * critical for the T2 iteration and collection management system in the
	 * enactor - we need to know that this is an empty list that
	 * <em>would have</em> contained lists, for example.
	 * <p>
	 * The created references will be related with a workflow run id passed
	 * through ReferenceContext so we can track all data referenced by a
	 * specific run.
	 * 
	 * @param depth
	 *            the depth of the empty list, must be >=1
	 * @return a new empty IdentifiedList allocated with a T2Reference itself as
	 *         the unique name and cached by the backing store.
	 * @throws ListServiceException
	 *             if there is a problem with the storage subsystem or if called
	 *             with an invalid depth argument
	 */
	@Transactional(propagation = REQUIRED, readOnly = false)
	IdentifiedList<T2Reference> registerEmptyList(int depth,
			ReferenceContext context) throws ListServiceException;

	/**
	 * Retrieve a previously named and registered list of T2Reference instances
	 * identified by the specified T2Reference (which must be of type
	 * T2ReferenceType.IdentifiedList)
	 * 
	 * @param id
	 *            identifier of the list of reference to retrieve
	 * @return an IdentifiedList of T2References. Note that because this list is
	 *         named it is effectively immutable, if you want to modify the list
	 *         you have to create and register a new list, you cannot modify the
	 *         returned value of this directly. This is why there is no update
	 *         method in the service or dao for reference lists.
	 * @throws ListServiceException
	 *             if anything goes wrong with the retrieval process or if there
	 *             is something wrong with the reference (such as it being of
	 *             the wrong reference type).
	 */
	IdentifiedList<T2Reference> getList(T2Reference id)
			throws ListServiceException;

	/**
	 * Functionality the same as {@link #getList(T2Reference) getList} but in
	 * asynchronous mode, returning immediately and using the supplied callback
	 * to communicate its results.
	 * 
	 * @param id
	 *            a {@link T2Reference} identifying an {@link IdentifiedList} to
	 *            retrieve
	 * @param callback
	 *            a {@link ListServiceCallback} used to convey the results of
	 *            the asynchronous call
	 * @throws ListServiceException
	 *             if the reference set service is not correctly configured.
	 *             Exceptions encountered when performing the asynchronous call
	 *             are not returned here, for obvious reasons, and are instead
	 *             messaged through the callback interface.
	 */
	void getListAsynch(T2Reference id, ListServiceCallback callback)
			throws ListServiceException;

	@Transactional(propagation = SUPPORTS, readOnly = false)
	boolean delete(T2Reference reference) throws ReferenceServiceException;

	/**
	 * Delete all {@link IdentifiedList}S used by the specific workflow run.
	 */
	@Transactional(propagation = SUPPORTS, readOnly = false)
	void deleteIdentifiedListsForWorkflowRun(String workflowRunId)
			throws ReferenceServiceException;
}
