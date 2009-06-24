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
import net.sf.taverna.t2.reference.Identified;
import net.sf.taverna.t2.reference.ReferenceServiceCacheProvider;
import net.sf.taverna.t2.reference.T2Reference;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * An aspect used to intercept calls to the various data access objects and
 * divert through a write-through cache provider
 * 
 * @author Tom Oinn
 */
public class CacheAspect {

	private ReferenceServiceCacheProvider cacheProvider;

	/**
	 * Return an injected ReferenceServiceCacheProvider
	 */
	private final ReferenceServiceCacheProvider getCacheProvider() {
		return cacheProvider;
	}

	/**
	 * Inject an instance of ReferenceServiceCacheProvider
	 * 
	 * @param cacheProvider
	 *            the cache provider to use
	 */
	public final void setCacheProvider(
			final ReferenceServiceCacheProvider cacheProvider) {
		this.cacheProvider = cacheProvider;
	}

	/**
	 * Handle a 'get by T2Reference' operation on a Dao
	 * 
	 * @param pjp
	 *            the join point representing the ongoing method call to the dao
	 * @return the entity identified by the T2Reference supplied to the method
	 *         to which this advice applies
	 * @throws DaoException
	 *             if anything goes wrong
	 */
	public final Identified getObject(final ProceedingJoinPoint pjp)
			throws DaoException {

		Identified result = null;

		// Get the T2Reference from the argument to the get method
		T2Reference id = (T2Reference) pjp.getArgs()[0];
		if (id != null) {
			result = getCacheProvider().get(id);
			if (result != null) {
				return result;
			}
		}
		// If we miss the cache then call the method as usual
		try {
			result = (Identified) pjp.proceed();
		} catch (Throwable e) {
			if (e instanceof DaoException) {
				throw ((DaoException) e);
			} else {
				throw new DaoException(
						"Unexpected exception type during aspect "
								+ "based invocation", e);
			}
		}

		// Write back to the cache
		if (result != null) {
			getCacheProvider().put(result);
		}

		return result;
	}

	/**
	 * Called around a write or update operation on the backing store, writes
	 * through to the cache after modifying the state of the backing store and
	 * before returning from the dao method
	 * 
	 * @param pjp
	 *            join point representing the ongoing method invocation to cache
	 * @throws DaoException
	 *             if anything goes wrong
	 */
	public void putObject(final ProceedingJoinPoint pjp) throws DaoException {

		// Get the Identified being stored by the method we're advising
		Identified storedObject = (Identified) pjp.getArgs()[0];

		try {
			// Run the store or update method
			pjp.proceed();
		} catch (Throwable e) {
			if (e instanceof DaoException) {
				throw ((DaoException) e);
			} else {
				throw new DaoException(
						"Unexpected exception type during aspect "
								+ "based invocation", e);
			}
		}

		// Assuming the method isn't null and has an identifier (which it will
		// if we haven't thrown an exception before now) write it back to the
		// cache provider
		if (storedObject != null && storedObject.getId() != null) {
			getCacheProvider().put(storedObject);
		}

	}

}
