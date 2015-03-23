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

import static org.apache.taverna.reference.impl.T2ReferenceImpl.getAsImpl;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.taverna.reference.DaoException;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ReferenceServiceException;
import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.reference.ReferenceSetAugmentationException;
import org.apache.taverna.reference.ReferenceSetService;
import org.apache.taverna.reference.ReferenceSetServiceException;
import org.apache.taverna.reference.T2Reference;

/**
 * Implementation of ReferenceSetService, inject with an appropriate
 * ReferenceSetDao to enable. Implements translation functionality as long as an
 * appropriate ReferenceSetAugmentor implementation is injected.
 * 
 * @author Tom Oinn
 */
public class ReferenceSetServiceImpl extends AbstractReferenceSetServiceImpl
		implements ReferenceSetService {
	@Override
	public ReferenceSet getReferenceSet(T2Reference id)
			throws ReferenceSetServiceException {
		checkDao();
		try {
			return referenceSetDao.get(id);
		} catch (DaoException de) {
			throw new ReferenceSetServiceException(de);
		}
	}

	private Map<URI,Object> locks = new WeakHashMap<>();

	private Object getLock(T2Reference id) {
		URI uri = id.toUri();
		synchronized (locks) {
			Object lock = locks.get(uri);
			if (lock == null) {
				lock = new Object();
				locks.put(uri, lock);
			}
			return lock;
		}
	}

	@Override
	public ReferenceSet getReferenceSetWithAugmentation(T2Reference id,
			Set<Class<ExternalReferenceSPI>> ensureTypes,
			ReferenceContext context) throws ReferenceSetServiceException {
		checkDao();
		checkAugmentor();
		if (context == null)
			context = new EmptyReferenceContext();
		// Obtain the reference set

		try {
			/*
			 * Synchronize on the reference set, should ensure that we don't
			 * have multiple concurrent translations assuming that Hibernate
			 * retrieves the same entity each time. Except we have to
			 * synchronize on the reference, and in fact we have to synchronize
			 * on the URI form.
			 */
			synchronized (getLock(id)) {
				ReferenceSet rs = getReferenceSet(id);
				Set<ExternalReferenceSPI> newReferences = referenceSetAugmentor
						.augmentReferenceSet(rs, ensureTypes, context);
				if (!newReferences.isEmpty()) {
					/*
					 * Write back changes to the store if we got here, this can
					 * potentially throw an unsupported operation exception in
					 * which case we have to fail the augmentation.
					 */
					try {
						rs.getExternalReferences().addAll(newReferences);
					} catch (RuntimeException re) {
						throw new ReferenceSetAugmentationException(
								"Can't add new references back into existing reference set instance");
					}
					referenceSetDao.update(rs);
				}
				return rs;
			}
		} catch (ReferenceSetAugmentationException rsae) {
			throw new ReferenceSetServiceException(rsae);
		}
	}

	@Override
	public ReferenceSet registerReferenceSet(
			Set<ExternalReferenceSPI> references, ReferenceContext context)
			throws ReferenceSetServiceException {
		checkDao();
		checkGenerator();
		
		ReferenceSetImpl rsi = new ReferenceSetImpl(new HashSet<>(references),
				getAsImpl(t2ReferenceGenerator
						.nextReferenceSetReference(context)));

		try {
			referenceSetDao.store(rsi);
			return rsi;
		} catch (DaoException de) {
			throw new ReferenceSetServiceException(de);
		}
	}

	@Override
	public boolean delete(T2Reference reference)
			throws ReferenceServiceException {
		checkDao();
		ReferenceSet set = referenceSetDao.get(reference);
		if (set == null)
			return false;
		return referenceSetDao.delete(set);
	}

	@Override
	public void deleteReferenceSetsForWorkflowRun(String workflowRunId)
			throws ReferenceServiceException {
		checkDao();
		referenceSetDao.deleteReferenceSetsForWFRun(workflowRunId);
	}
}
