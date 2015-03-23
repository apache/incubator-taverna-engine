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

import java.util.Set;

import org.apache.taverna.reference.DaoException;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.reference.ReferenceSetAugmentor;
import org.apache.taverna.reference.ReferenceSetDao;
import org.apache.taverna.reference.ReferenceSetService;
import org.apache.taverna.reference.ReferenceSetServiceCallback;
import org.apache.taverna.reference.ReferenceSetServiceException;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.T2ReferenceGenerator;

/**
 * Abstract implementation of ReferenceSetService, inject with an appropriate
 * ReferenceSetDao to enable. Implements translation functionality as long as an
 * appropriate ReferenceSetAugmentor implementation is injected. Contains
 * injectors for id generation and dao along with other bookkeeping, leaving the
 * implementation of the actual service logic to the subclass.
 * 
 * @author Tom Oinn
 */
public abstract class AbstractReferenceSetServiceImpl extends
		AbstractServiceImpl implements ReferenceSetService {
	protected ReferenceSetDao referenceSetDao = null;
	protected T2ReferenceGenerator t2ReferenceGenerator = null;
	protected ReferenceSetAugmentor referenceSetAugmentor = null;

	/**
	 * Inject the reference set data access object.
	 */
	public final void setReferenceSetDao(ReferenceSetDao dao) {
		this.referenceSetDao = dao;
	}

	/**
	 * Inject the T2Reference generator used to allocate new IDs when
	 * registering sets of ExternalReferenceSPI
	 */
	public final void setT2ReferenceGenerator(T2ReferenceGenerator t2rg) {
		this.t2ReferenceGenerator = t2rg;
	}

	/**
	 * Inject the ReferenceSetAugmentor used to translate or construct new
	 * ExternalReferenceSPI instances within a ReferenceSet
	 */
	public final void setReferenceSetAugmentor(ReferenceSetAugmentor rse) {
		this.referenceSetAugmentor = rse;
	}

	/**
	 * Check that the reference set dao is configured
	 * 
	 * @throws ReferenceSetServiceException
	 *             if the dao is still null
	 */
	protected final void checkDao() throws ReferenceSetServiceException {
		if (referenceSetDao == null)
			throw new ReferenceSetServiceException(
					"ReferenceSetDao not initialized, reference set "
							+ "service operations are not available");
	}

	/**
	 * Check that the t2reference generator is configured
	 * 
	 * @throws ReferenceSetServiceException
	 *             if the generator is still null
	 */
	protected final void checkGenerator() throws ReferenceSetServiceException {
		if (t2ReferenceGenerator == null)
			throw new ReferenceSetServiceException(
					"T2ReferenceGenerator not initialized, reference "
							+ "set service operations not available");
	}

	/**
	 * Check that the reference set augmentor is configured
	 * 
	 * @throws ReferenceSetServiceException
	 *             if the reference set augmentor is still null
	 */
	protected final void checkAugmentor() throws ReferenceSetServiceException {
		if (referenceSetAugmentor == null)
			throw new ReferenceSetServiceException(
					"ReferenceSetAugmentor not initialized, reference "
							+ "set service operations not available");
	}

	@Override
	public final void getReferenceSetAsynch(final T2Reference id,
			final ReferenceSetServiceCallback callback)
			throws ReferenceSetServiceException {
		checkDao();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					ReferenceSet rs = referenceSetDao.get(id);
					callback.referenceSetRetrieved(rs);
				} catch (DaoException de) {
					callback.referenceSetRetrievalFailed(new ReferenceSetServiceException(
							de));
				}
			}
		};
		executeRunnable(r);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void getReferenceSetWithAugmentationAsynch(
			final T2Reference id,
			final Set<Class<ExternalReferenceSPI>> ensureTypes,
			final ReferenceContext context,
			final ReferenceSetServiceCallback callback)
			throws ReferenceSetServiceException {
		checkDao();
		checkAugmentor();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					callback.referenceSetRetrieved(getReferenceSetWithAugmentation(
							id, ensureTypes, context));
				} catch (ReferenceSetServiceException rsse) {
					callback.referenceSetRetrievalFailed(rsse);
				}
			}
		};
		executeRunnable(r);
	}

}
