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

import static org.apache.taverna.reference.T2ReferenceType.ErrorDocument;

import java.util.List;

import org.apache.taverna.reference.DaoException;
import org.apache.taverna.reference.ErrorDocument;
import org.apache.taverna.reference.ErrorDocumentDao;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.annotations.DeleteIdentifiedOperation;
import org.apache.taverna.reference.annotations.GetIdentifiedOperation;
import org.apache.taverna.reference.annotations.PutIdentifiedOperation;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

/**
 * An implementation of ErrorDocumentDao based on raw hibernate session factory
 * injection and running within a spring managed context through auto-proxy
 * generation. To use this in spring inject a property 'sessionFactory' with
 * either a {@link org.springframework.orm.hibernate3.LocalSessionFactoryBean
 * LocalSessionFactoryBean} or the equivalent class from the T2Platform module
 * to add SPI based implementation discovery and mapping. To use outside of
 * Spring ensure you call the setSessionFactory(..) method before using this
 * (but really, use it from Spring, so much easier).
 * <p>
 * Methods in this Dao require transactional support
 * 
 * @author Tom Oinn
 */
public class TransactionalHibernateErrorDocumentDao implements ErrorDocumentDao {
	private static final String GET_ERRORS_FOR_RUN = "FROM ErrorDocumentImpl WHERE namespacePart = :workflow_run_id";
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Fetch an ErrorDocument list by id
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
	public ErrorDocument get(T2Reference ref) throws DaoException {
		if (ref == null)
			throw new DaoException(
					"Supplied reference is null, can't retrieve.");
		if (!ref.getReferenceType().equals(ErrorDocument))
			throw new DaoException(
					"This dao can only retrieve reference of type T2Reference.ErrorDocument");
		if (!(ref instanceof T2ReferenceImpl))
			throw new DaoException(
					"Reference must be an instance of T2ReferenceImpl");

		try {
			return (ErrorDocumentImpl) sessionFactory.getCurrentSession().get(
					ErrorDocumentImpl.class,
					((T2ReferenceImpl) ref).getCompactForm());
		} catch (Exception ex) {
			throw new DaoException(ex);
		}
	}

	@Override
	@PutIdentifiedOperation
	public void store(ErrorDocument theDocument) throws DaoException {
		if (theDocument.getId() == null)
			throw new DaoException(
					"Supplied error document set has a null ID, allocate "
							+ "an ID before calling the store method in the dao.");
		if (!theDocument.getId().getReferenceType().equals(ErrorDocument))
			throw new DaoException("Strangely the list ID doesn't have type "
					+ "T2ReferenceType.ErrorDocument, something has probably "
					+ "gone badly wrong somewhere earlier!");
		if (!(theDocument instanceof ErrorDocumentImpl))
			throw new DaoException(
					"Supplied ErrorDocument not an instance of ErrorDocumentImpl");

		try {
			sessionFactory.getCurrentSession().save(theDocument);
		} catch (Exception ex) {
			throw new DaoException(ex);
		}
	}

	@Override
	@DeleteIdentifiedOperation
	public boolean delete(ErrorDocument theDocument) throws DaoException {
		if (theDocument.getId() == null)
			throw new DaoException(
					"Supplied error document set has a null ID, allocate "
							+ "an ID before calling the store method in the dao.");
		if (!theDocument.getId().getReferenceType().equals(ErrorDocument))
			throw new DaoException("Strangely the list ID doesn't have type "
					+ "T2ReferenceType.ErrorDocument, something has probably "
					+ "gone badly wrong somewhere earlier!");
		if (!(theDocument instanceof ErrorDocumentImpl))
			throw new DaoException(
					"Supplied ErrorDocument not an instance of ErrorDocumentImpl");

		try {
			sessionFactory.getCurrentSession().delete(theDocument);
			return true;
		} catch (Exception ex) {
			throw new DaoException(ex);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	@DeleteIdentifiedOperation
	public synchronized void deleteErrorDocumentsForWFRun(String workflowRunId)
			throws DaoException {
		try {
			// Select all ErrorDocuments for this wf run
			Query selectQuery = sessionFactory.getCurrentSession().createQuery(
					GET_ERRORS_FOR_RUN);
			selectQuery.setString("workflow_run_id", workflowRunId);
			List<ErrorDocument> errorDocuments = selectQuery.list();
			for (ErrorDocument errorDocument : errorDocuments)
				delete(errorDocument);
		} catch (Exception ex) {
			throw new DaoException(ex);
		}
	}
}
