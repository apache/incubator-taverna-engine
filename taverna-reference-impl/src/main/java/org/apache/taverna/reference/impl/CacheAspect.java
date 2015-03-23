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

import org.apache.taverna.reference.DaoException;
import org.apache.taverna.reference.Identified;
import org.apache.taverna.reference.ReferenceServiceCacheProvider;
import org.apache.taverna.reference.T2Reference;

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
			if (result != null)
				return result;
		}
		// If we miss the cache then call the method as usual
		try {
			result = (Identified) pjp.proceed();
		} catch (DaoException e) {
			throw e;
		} catch (Throwable e) {
			throw new DaoException("Unexpected exception type during aspect "
					+ "based invocation", e);
		}

		// Write back to the cache
		if (result != null)
			getCacheProvider().put(result);

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
		} catch (DaoException e) {
			throw e;
		} catch (Throwable e) {
			throw new DaoException("Unexpected exception type during aspect "
					+ "based invocation", e);
		}

		/*
		 * Assuming the method isn't null and has an identifier (which it will
		 * if we haven't thrown an exception before now) write it back to the
		 * cache provider
		 */
		if (storedObject != null && storedObject.getId() != null)
			getCacheProvider().put(storedObject);
	}
}
