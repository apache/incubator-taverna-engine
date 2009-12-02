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

import java.util.HashSet;
import java.util.Set;

import net.sf.taverna.t2.reference.DaoException;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceContext;
import net.sf.taverna.t2.reference.ReferenceServiceException;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferenceSetAugmentationException;
import net.sf.taverna.t2.reference.ReferenceSetService;
import net.sf.taverna.t2.reference.ReferenceSetServiceException;
import net.sf.taverna.t2.reference.T2Reference;

/**
 * Implementation of ReferenceSetService, inject with an appropriate
 * ReferenceSetDao to enable. Implements translation functionality as long as an
 * appropriate ReferenceSetAugmentor implementation is injected.
 * 
 * @author Tom Oinn
 * 
 */
public class ReferenceSetServiceImpl extends AbstractReferenceSetServiceImpl
		implements ReferenceSetService {

	/**
	 * {@inheritDoc}
	 */
	public ReferenceSet getReferenceSet(T2Reference id)
			throws ReferenceSetServiceException {
		checkDao();
		try {
			return referenceSetDao.get(id);
		} catch (DaoException de) {
			throw new ReferenceSetServiceException(de);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ReferenceSet getReferenceSetWithAugmentation(T2Reference id,
			Set<Class<ExternalReferenceSPI>> ensureTypes,
			ReferenceContext context) throws ReferenceSetServiceException {
		checkDao();
		checkAugmentor();
		if (context == null) {
			context = new EmptyReferenceContext();
		}
		// Obtain the reference set

		try {
			// Synchronize on the reference set, should ensure that we don't
			// have multiple concurrent translations assuming that Hibernate
			// retrieves the same entity each time. To work around this
			// potentially not being the case we can synchronize on the
			// stringified form of the identifier.
			synchronized (id.toString()) {
				ReferenceSet rs = getReferenceSet(id);
				Set<ExternalReferenceSPI> newReferences = referenceSetAugmentor
						.augmentReferenceSet(rs, ensureTypes, context);
				if (newReferences.isEmpty() == false) {
					// Write back changes to the store if we got here, this can
					// potentially throw an unsupported operation exception in
					// which
					// case we have to fail the augmentation.
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

	/**
	 * {@inheritDoc}
	 */
	public ReferenceSet registerReferenceSet(
			Set<ExternalReferenceSPI> references, ReferenceContext context)
			throws ReferenceSetServiceException {
		checkDao();
		checkGenerator();
		ReferenceSetImpl rsi = new ReferenceSetImpl();
		rsi
				.setExternalReferences(new HashSet<ExternalReferenceSPI>(
						references));
		T2Reference id = t2ReferenceGenerator.nextReferenceSetReference(context);
		rsi.setTypedId(T2ReferenceImpl.getAsImpl(id));
		try {
			referenceSetDao.store(rsi);
			return rsi;
		} catch (DaoException de) {
			throw new ReferenceSetServiceException(de);
		}
	}

	public boolean delete(T2Reference reference)
			throws ReferenceServiceException {
		checkDao();
		ReferenceSet set=referenceSetDao.get(reference);
		if (set==null) return false;
		return referenceSetDao.delete(set);
	}

	public void deleteReferenceSetsForWorkflowRun(String workflowRunId)
			throws ReferenceServiceException {
		checkDao();
		referenceSetDao.deleteReferenceSetsForWFRun(workflowRunId);
	}
}
