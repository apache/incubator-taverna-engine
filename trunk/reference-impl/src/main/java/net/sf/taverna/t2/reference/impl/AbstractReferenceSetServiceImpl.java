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

import java.util.Set;

import net.sf.taverna.t2.reference.DaoException;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceContext;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferenceSetAugmentor;
import net.sf.taverna.t2.reference.ReferenceSetDao;
import net.sf.taverna.t2.reference.ReferenceSetService;
import net.sf.taverna.t2.reference.ReferenceSetServiceCallback;
import net.sf.taverna.t2.reference.ReferenceSetServiceException;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceGenerator;

/**
 * Abstract implementation of ReferenceSetService, inject with an appropriate
 * ReferenceSetDao to enable. Implements translation functionality as long as an
 * appropriate ReferenceSetAugmentor implementation is injected. Contains
 * injectors for id generation and dao along with other bookkeeping, leaving the
 * implementation of the actual service logic to the subclass.
 * 
 * @author Tom Oinn
 * 
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
		if (referenceSetDao == null) {
			throw new ReferenceSetServiceException(
					"ReferenceSetDao not initialized, reference set "
							+ "service operations are not available");
		}
	}

	/**
	 * Check that the t2reference generator is configured
	 * 
	 * @throws ReferenceSetServiceException
	 *             if the generator is still null
	 */
	protected final void checkGenerator() throws ReferenceSetServiceException {
		if (t2ReferenceGenerator == null) {
			throw new ReferenceSetServiceException(
					"T2ReferenceGenerator not initialized, reference "
							+ "set service operations not available");
		}
	}

	/**
	 * Check that the reference set augmentor is configured
	 * 
	 * @throws ReferenceSetServiceException
	 *             if the reference set augmentor is still null
	 */
	protected final void checkAugmentor() throws ReferenceSetServiceException {
		if (referenceSetAugmentor == null) {
			throw new ReferenceSetServiceException(
					"ReferenceSetAugmentor not initialized, reference "
							+ "set service operations not available");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final void getReferenceSetAsynch(final T2Reference id,
			final ReferenceSetServiceCallback callback)
			throws ReferenceSetServiceException {
		checkDao();
		Runnable r = new Runnable() {
			public void run() {
				try {
					ReferenceSet rs = referenceSetDao.get(id);
					callback.referenceSetRetrieved(rs);
				} catch (DaoException de) {
					callback
							.referenceSetRetrievalFailed(new ReferenceSetServiceException(
									de));
				}
			}
		};
		executeRunnable(r);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void getReferenceSetWithAugmentationAsynch(
			final T2Reference id,
			final Set<Class<ExternalReferenceSPI>> ensureTypes,
			final ReferenceContext context,
			final ReferenceSetServiceCallback callback)
			throws ReferenceSetServiceException {
		checkDao();
		checkAugmentor();
		Runnable r = new Runnable() {
			public void run() {
				try {
					callback
							.referenceSetRetrieved(getReferenceSetWithAugmentation(
									id, ensureTypes, context));

				} catch (ReferenceSetServiceException rsse) {
					callback.referenceSetRetrievalFailed(rsse);
				}
			}
		};
		executeRunnable(r);
	}

}
