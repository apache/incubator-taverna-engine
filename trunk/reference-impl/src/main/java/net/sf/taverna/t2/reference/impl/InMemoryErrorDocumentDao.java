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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.taverna.t2.reference.DaoException;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ErrorDocumentDao;
import net.sf.taverna.t2.reference.T2Reference;

/**
 * A trivial in-memory implementation of ErrorDocumentDao for either testing or
 * very lightweight embedded systems. Uses a java Map as the backing store.
 * 
 * @author Tom Oinn
 * 
 */
public class InMemoryErrorDocumentDao implements ErrorDocumentDao {

	private Map<T2Reference, ErrorDocument> store;

	public InMemoryErrorDocumentDao() {
		this.store = new ConcurrentHashMap<T2Reference, ErrorDocument>();
	}

	public synchronized ErrorDocument get(T2Reference reference)
			throws DaoException {
		if (store.containsKey(reference)) {
			return store.get(reference);
		} else {
			throw new DaoException("Key " + reference + " not found in store");
		}
	}

	public synchronized void store(ErrorDocument theDoc) throws DaoException {
		store.put(theDoc.getId(), theDoc);
	}

}
