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
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void store(ErrorDocument errorDoc) throws DaoException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ErrorDocument get(T2Reference reference) throws DaoException;
	
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
	public boolean delete(ErrorDocument errorDoc) throws DaoException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = false)
	public void deleteErrorDocumentsForWFRun(String workflowRunId) throws DaoException;
}
