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

import static org.apache.taverna.reference.T2ReferenceType.IdentifiedList;

import java.util.List;

import org.apache.taverna.reference.DaoException;
import org.apache.taverna.reference.IdentifiedList;
import org.apache.taverna.reference.ListDao;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.annotations.DeleteIdentifiedOperation;
import org.apache.taverna.reference.annotations.GetIdentifiedOperation;
import org.apache.taverna.reference.annotations.PutIdentifiedOperation;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * An implementation of ListDao based on Spring's HibernateDaoSupport. To use
 * this in spring inject a property 'sessionFactory' with either a
 * {@link org.springframework.orm.hibernate3.LocalSessionFactoryBean
 * LocalSessionFactoryBean} or the equivalent class from the T2Platform module
 * to add SPI based implementation discovery and mapping. To use outside of
 * Spring ensure you call the setSessionFactory(..) method before using this
 * (but really, use it from Spring, so much easier).
 * 
 * @author Tom Oinn
 */
public class HibernateListDao extends HibernateDaoSupport implements ListDao {
	private static final String GET_LISTS_FOR_RUN = "FROM T2ReferenceListImpl WHERE namespacePart = :workflow_run_id";

	/**
	 * Fetch a t2reference list by id
	 * 
	 * @param ref
	 *            the T2Reference to fetch
	 * @return a retrieved identified list of T2 references
	 * @throws DaoException
	 *             if the supplied reference is of the wrong type or if
	 *             something goes wrong fetching the data or connecting to the
	 *             database
	 */
	@Override
	@GetIdentifiedOperation
	public IdentifiedList<T2Reference> get(T2Reference ref) throws DaoException {
		if (ref == null)
			throw new DaoException(
					"Supplied reference is null, can't retrieve.");
		if (!ref.getReferenceType().equals(IdentifiedList))
			throw new DaoException(
					"This dao can only retrieve reference of type T2Reference.IdentifiedList");
		if (!(ref instanceof T2ReferenceImpl))
			throw new DaoException(
					"Reference must be an instance of T2ReferenceImpl");

		try {
			return (T2ReferenceListImpl) getHibernateTemplate().get(
					T2ReferenceListImpl.class,
					((T2ReferenceImpl) ref).getCompactForm());
		} catch (Exception ex) {
			throw new DaoException(ex);
		}
	}

	@Override
	@PutIdentifiedOperation
	public void store(IdentifiedList<T2Reference> theList) throws DaoException {
		if (theList.getId() == null)
			throw new DaoException("Supplied list set has a null ID, allocate "
					+ "an ID before calling the store method in the dao.");
		if (!theList.getId().getReferenceType().equals(IdentifiedList))
			throw new DaoException("Strangely the list ID doesn't have type "
					+ "T2ReferenceType.IdentifiedList, something has probably "
					+ "gone badly wrong somewhere earlier!");
		if (!(theList instanceof T2ReferenceListImpl))
			throw new DaoException(
					"Supplied identifier list not an instance of T2ReferenceList");

		try {
			getHibernateTemplate().save(theList);
		} catch (Exception ex) {
			throw new DaoException(ex);
		}
	}

	@Override
	public boolean delete(IdentifiedList<T2Reference> theList)
			throws DaoException {
		if (theList.getId() == null)
			throw new DaoException("Supplied list set has a null ID, allocate "
					+ "an ID before calling the store method in the dao.");
		if (!theList.getId().getReferenceType().equals(IdentifiedList))
			throw new DaoException("Strangely the list ID doesn't have type "
					+ "T2ReferenceType.IdentifiedList, something has probably "
					+ "gone badly wrong somewhere earlier!");
		if (!(theList instanceof T2ReferenceListImpl))
			throw new DaoException(
					"Supplied identifier list not an instance of T2ReferenceList");

		try {
			getHibernateTemplate().delete(theList);
			return true;
		} catch (Exception ex) {
			throw new DaoException(ex);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	@DeleteIdentifiedOperation
	public synchronized void deleteIdentifiedListsForWFRun(String workflowRunId)
			throws DaoException {
		try {
			// Select all T2Reference lists for this wf run
			Session session = getSession();
			Query selectQuery = session.createQuery(GET_LISTS_FOR_RUN);
			selectQuery.setString("workflow_run_id", workflowRunId);
			List<IdentifiedList<T2Reference>> identifiedLists = selectQuery
					.list();
			session.close();
			/*
			 * need to close before we do delete otherwise hibernate complains
			 * that two sessions are accessing collection
			 */
			getHibernateTemplate().deleteAll(identifiedLists);
		} catch (Exception ex) {
			throw new DaoException(ex);
		}
	}
}
