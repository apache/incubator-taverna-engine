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

import net.sf.taverna.t2.reference.DaoException;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferenceSetDao;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;
import net.sf.taverna.t2.reference.annotations.DeleteIdentifiedOperation;
import net.sf.taverna.t2.reference.annotations.GetIdentifiedOperation;
import net.sf.taverna.t2.reference.annotations.PutIdentifiedOperation;

import org.hibernate.SessionFactory;

/**
 * An implementation of ReferenceSetDao based on raw hibernate session factory
 * injection and running within a spring managed context through auto-proxy
 * generation. To use this in spring inject a property 'sessionFactory' with
 * either a
 * {@link org.springframework.orm.hibernate3.LocalSessionFactoryBean LocalSessionFactoryBean}
 * or the equivalent class from the T2Platform module to add SPI based
 * implementation discovery and mapping. To use outside of Spring ensure you
 * call the setSessionFactory(..) method before using this (but really, use it
 * from Spring, so much easier).
 * <p>
 * Methods in this Dao require transactional support
 * 
 * @author Tom Oinn
 * 
 */
public class TransactionalHibernateReferenceSetDao implements ReferenceSetDao {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Store the specified new reference set
	 * 
	 * @param rs
	 *            a reference set, must not already exist in the database.
	 * @throws DaoException
	 *             if the entry already exists in the database, if the supplied
	 *             reference set isn't an instance of ReferenceSetImpl or if
	 *             something else goes wrong connecting to the database
	 */
	@PutIdentifiedOperation
	public void store(ReferenceSet rs) throws DaoException {
		if (rs.getId() == null) {
			throw new DaoException(
					"Supplied reference set has a null ID, allocate "
							+ "an ID before calling the store method in the dao.");
		} else if (rs.getId().getReferenceType().equals(
				T2ReferenceType.ReferenceSet) == false) {
			throw new DaoException(
					"Strangely the reference set ID doesn't have type "
							+ "T2ReferenceType.ReferenceSet, something has probably "
							+ "gone badly wrong somewhere earlier!");
		}
		if (rs instanceof ReferenceSetImpl) {
			try {
				sessionFactory.getCurrentSession().save(rs);
			} catch (Exception ex) {
				throw new DaoException(ex);
			}
		} else {
			throw new DaoException(
					"Supplied reference set not an instance of ReferenceSetImpl");
		}
	}

	/**
	 * Update a pre-existing entry in the database
	 * 
	 * @param rs
	 *            the reference set to update. This must already exist in the
	 *            database
	 * @throws DaoException
	 */
	@PutIdentifiedOperation
	public void update(ReferenceSet rs) throws DaoException {
		if (rs.getId() == null) {
			throw new DaoException(
					"Supplied reference set has a null ID, allocate "
							+ "an ID before calling the store method in the dao.");
		} else if (rs.getId().getReferenceType().equals(
				T2ReferenceType.ReferenceSet) == false) {
			throw new DaoException(
					"Strangely the reference set ID doesn't have type "
							+ "T2ReferenceType.ReferenceSet, something has probably "
							+ "gone badly wrong somewhere earlier!");
		}
		if (rs instanceof ReferenceSetImpl) {
			try {
				sessionFactory.getCurrentSession().update(rs);
			} catch (Exception ex) {
				throw new DaoException(ex);
			}
		} else {
			throw new DaoException(
					"Supplied reference set not an instance of ReferenceSetImpl");
		}
	}

	/**
	 * Fetch a reference set by id
	 * 
	 * @param ref
	 *            the ReferenceSetT2ReferenceImpl to fetch
	 * @return a retrieved ReferenceSetImpl
	 * @throws DaoException
	 *             if the supplied reference is of the wrong type or if
	 *             something goes wrong fetching the data or connecting to the
	 *             database
	 */
	@GetIdentifiedOperation
	public ReferenceSetImpl get(T2Reference ref) throws DaoException {
		if (ref == null) {
			throw new DaoException(
					"Supplied reference is null, can't retrieve.");
		} else if (ref.getReferenceType().equals(T2ReferenceType.ReferenceSet) == false) {
			throw new DaoException(
					"This dao can only retrieve reference of type T2Reference.ReferenceSet");
		}
		if (ref instanceof T2ReferenceImpl) {
			try {
				return (ReferenceSetImpl) sessionFactory.getCurrentSession()
						.get(ReferenceSetImpl.class,
								((T2ReferenceImpl) ref).getCompactForm());
			} catch (Exception ex) {
				throw new DaoException(ex);
			}
		} else {
			throw new DaoException(
					"Reference must be an instance of T2ReferenceImpl");
		}
	}

	@DeleteIdentifiedOperation
	public boolean delete(ReferenceSet rs) throws DaoException {
		if (rs.getId() == null) {
			throw new DaoException(
					"Supplied reference set has a null ID, allocate "
							+ "an ID before calling the store method in the dao.");
		} else if (rs.getId().getReferenceType().equals(
				T2ReferenceType.ReferenceSet) == false) {
			throw new DaoException(
					"Strangely the reference set ID doesn't have type "
							+ "T2ReferenceType.ReferenceSet, something has probably "
							+ "gone badly wrong somewhere earlier!");
		}
		if (rs instanceof ReferenceSetImpl) {
			try {
				sessionFactory.getCurrentSession().delete(rs);
			} catch (Exception ex) {
				throw new DaoException(ex);
			}
		} else {
			throw new DaoException(
					"Supplied reference set not an instance of ReferenceSetImpl");
		}
		return true;
	}
}
