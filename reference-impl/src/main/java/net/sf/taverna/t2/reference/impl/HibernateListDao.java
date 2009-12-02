/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.reference.impl;

import java.util.List;

import net.sf.taverna.t2.reference.DaoException;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ListDao;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;
import net.sf.taverna.t2.reference.annotations.DeleteIdentifiedOperation;
import net.sf.taverna.t2.reference.annotations.GetIdentifiedOperation;
import net.sf.taverna.t2.reference.annotations.PutIdentifiedOperation;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * An implementation of ListDao based on Spring's HibernateDaoSupport. To use
 * this in spring inject a property 'sessionFactory' with either a
 * {@link org.springframework.orm.hibernate3.LocalSessionFactoryBean LocalSessionFactoryBean}
 * or the equivalent class from the T2Platform module to add SPI based
 * implementation discovery and mapping. To use outside of Spring ensure you
 * call the setSessionFactory(..) method before using this (but really, use it
 * from Spring, so much easier).
 * 
 * @author Tom Oinn
 * 
 */
public class HibernateListDao extends HibernateDaoSupport implements ListDao {

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
	@GetIdentifiedOperation
	public IdentifiedList<T2Reference> get(T2Reference ref) throws DaoException {
		if (ref == null) {
			throw new DaoException(
					"Supplied reference is null, can't retrieve.");
		} else if (ref.getReferenceType()
				.equals(T2ReferenceType.IdentifiedList) == false) {
			throw new DaoException(
					"This dao can only retrieve reference of type T2Reference.IdentifiedList");
		}
		if (ref instanceof T2ReferenceImpl) {
			try {
				return (T2ReferenceListImpl) getHibernateTemplate().get(
						T2ReferenceListImpl.class,
						((T2ReferenceImpl) ref).getCompactForm());
			} catch (Exception ex) {
				throw new DaoException(ex);
			}
		} else {
			throw new DaoException(
					"Reference must be an instance of T2ReferenceImpl");
		}
	}

	@PutIdentifiedOperation
	public void store(IdentifiedList<T2Reference> theList) throws DaoException {
		if (theList.getId() == null) {
			throw new DaoException("Supplied list set has a null ID, allocate "
					+ "an ID before calling the store method in the dao.");
		} else if (theList.getId().getReferenceType().equals(
				T2ReferenceType.IdentifiedList) == false) {
			throw new DaoException("Strangely the list ID doesn't have type "
					+ "T2ReferenceType.IdentifiedList, something has probably "
					+ "gone badly wrong somewhere earlier!");
		}
		if (theList instanceof T2ReferenceListImpl) {
			try {
				getHibernateTemplate().save(theList);
			} catch (Exception ex) {
				throw new DaoException(ex);
			}
		} else {
			throw new DaoException(
					"Supplied identifier list not an instance of T2ReferenceList");
		}
	}

	public boolean delete(
			IdentifiedList<T2Reference> theList) throws DaoException {
		if (theList.getId() == null) {
			throw new DaoException("Supplied list set has a null ID, allocate "
					+ "an ID before calling the store method in the dao.");
		} else if (theList.getId().getReferenceType().equals(
				T2ReferenceType.IdentifiedList) == false) {
			throw new DaoException("Strangely the list ID doesn't have type "
					+ "T2ReferenceType.IdentifiedList, something has probably "
					+ "gone badly wrong somewhere earlier!");
		}
		if (theList instanceof T2ReferenceListImpl) {
			try {
				getHibernateTemplate().delete(theList);
			} catch (Exception ex) {
				throw new DaoException(ex);
			}
		} else {
			throw new DaoException(
					"Supplied identifier list not an instance of T2ReferenceList");
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@DeleteIdentifiedOperation
	public synchronized void deleteIdentifiedListsForWFRun(String workflowRunId) throws DaoException {	
		try{
			// Select all T2Reference lists for this wf run
			Session session = getSession();
			Query selectQuery= session.createQuery("FROM T2ReferenceListImpl WHERE namespacePart=:workflow_run_id");
			selectQuery.setString("workflow_run_id", workflowRunId);
			List<IdentifiedList<T2Reference>> identifiedLists = selectQuery.list();
			session.close(); // need to close before we do delete otherwise hibernate complains that two sessions are accessing collection
			getHibernateTemplate().deleteAll(identifiedLists);
		}
		catch(Exception ex){
			throw new DaoException(ex);
		}
		
	}
}
