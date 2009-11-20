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

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.sf.taverna.t2.reference.DaoException;
import net.sf.taverna.t2.reference.Identified;
import net.sf.taverna.t2.reference.T2Reference;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * An aspect used to intercept calls to the various data access objects and
 * execute data writes on a thread limited executer with an unbounded blocking queue.
 * 
 * @author David Withers
 */
public class WriteQueueAspect {

	private Map<T2Reference, Identified> store = new ConcurrentHashMap<T2Reference, Identified>();
	
	private Map<T2Reference, SoftReference<Identified>> cache = new ConcurrentHashMap<T2Reference, SoftReference<Identified>>();

	private ThreadPoolExecutor executer;
	
	public WriteQueueAspect() {
		this(5);
	}
	
	public WriteQueueAspect(int threads) {
		executer = new ThreadPoolExecutor(threads, threads, 60,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), Executors
						.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
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
			// try the cache
			SoftReference<Identified> ref = cache.get(id);
			if (ref != null) {
				result = ref.get();
			}
			if (result == null) {
				// not in the cache, check if it's still in the write queue
				result = store.get(id);				
			}
		}
		// If we miss the cache then call the method as usual
		if (result == null) {
			try {
				result = (Identified) pjp.proceed();
				if (result != null) {
					cache.put(id, new SoftReference<Identified>(result));
				}
			} catch (Throwable e) {
				if (e instanceof DaoException) {
					throw ((DaoException) e);
				} else {
					throw new DaoException(
							"Unexpected exception type during aspect "
							+ "based invocation", e);
				}
			}
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
		final Identified storedObject = (Identified) pjp.getArgs()[0];

		Runnable task = new Runnable() {

			public void run() {
				try {
					// Run the store or update method
					pjp.proceed();
					store.remove(storedObject.getId());
				} catch (Throwable e) {
					if (e instanceof DaoException) {
						throw ((DaoException) e);
					} else {
						throw new DaoException(
								"Unexpected exception type during aspect "
										+ "based invocation", e);
					}
				}
			}
			
		};
		
		cache.put(storedObject.getId(), new SoftReference<Identified>(storedObject));
		store.put(storedObject.getId(), storedObject);
		executer.execute(task);
	}
	
	public int cacheSize() {
		return executer.getActiveCount() + executer.getQueue().size();
	}
	
}
