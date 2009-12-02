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

import org.springframework.transaction.annotation.Propagation;
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
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void store(IdentifiedList<T2Reference> theList) throws DaoException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IdentifiedList<T2Reference> get(T2Reference reference)
			throws DaoException;
	
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
	public boolean delete(IdentifiedList<T2Reference> theList)
			throws DaoException;
	
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
	public void deleteIdentifiedListsForWFRun(String workflowRunId) throws DaoException;

}
