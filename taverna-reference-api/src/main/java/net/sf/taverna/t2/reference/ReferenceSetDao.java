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
package net.sf.taverna.t2.reference;

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
