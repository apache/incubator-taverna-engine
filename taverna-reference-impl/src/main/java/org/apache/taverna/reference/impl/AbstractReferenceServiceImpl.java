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

import java.util.List;
import java.util.Set;

import org.apache.taverna.reference.ErrorDocumentService;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ListService;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.ReferenceServiceException;
import org.apache.taverna.reference.ReferenceServiceResolutionCallback;
import org.apache.taverna.reference.ReferenceSetService;
import org.apache.taverna.reference.StreamToValueConverterSPI;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.ValueToReferenceConverterSPI;

/**
 * Implementation of ReferenceService, inject with ReferenceSetService,
 * ErrorDocumentService and ListService to enable. Inject with an instance
 * registry of ValueToReferenceConvertorSPI to enable on the fly registration of
 * otherwise illegal object types. This class contains the basic injection
 * functionality and the getters for the sub-services, mostly to isolate these
 * mundane bits of code from the more interesting actual implementation of the
 * reference service logic.
 * 
 * @author Tom Oinn
 */
public abstract class AbstractReferenceServiceImpl extends AbstractServiceImpl
		implements ReferenceService {
	protected ErrorDocumentService errorDocumentService = null;
	protected ReferenceSetService referenceSetService = null;
	protected ListService listService = null;
	protected List<ValueToReferenceConverterSPI> converters = null;
	@SuppressWarnings("rawtypes")
	protected List<StreamToValueConverterSPI> valueBuilders = null;

	/**
	 * Inject value to reference convertor SPI
	 */
	public final void setConverters(
			List<ValueToReferenceConverterSPI> converters) {
		this.converters = converters;
	}

	/**
	 * Inject stream to value converter SPI
	 */
	@SuppressWarnings("rawtypes")
	public final void setValueBuilders(
			List<StreamToValueConverterSPI> valueBuilders) {
		this.valueBuilders = valueBuilders;
	}

	/**
	 * Inject error document service
	 */
	public final void setErrorDocumentService(ErrorDocumentService eds) {
		this.errorDocumentService = eds;
	}

	/**
	 * Inject reference set service
	 */
	public final void setReferenceSetService(ReferenceSetService rss) {
		this.referenceSetService = rss;
	}

	/**
	 * Inject list service
	 */
	public final void setListService(ListService ls) {
		this.listService = ls;
	}

	/**
	 * Throw a ReferenceServiceException if methods in ReferenceService are
	 * called without the necessary sub-services configured.
	 */
	protected final void checkServices() throws ReferenceServiceException {
		if (errorDocumentService == null)
			throw new ReferenceServiceException(
					"Reference service must be configued with an "
							+ "instance of ErrorDocumentService to function");
		if (referenceSetService == null)
			throw new ReferenceServiceException(
					"Reference service must be configued with an "
							+ "instance of ReferenceSetService to function");
		if (listService == null)
			throw new ReferenceServiceException(
					"Reference service must be configued with an "
							+ "instance of ListService to function");
	}

	/**
	 * Check whether the converter registry has been defined, throw a
	 * ReferenceServiceException if not
	 */
	protected final void checkConverterRegistry()
			throws ReferenceServiceException {
		if (converters == null)
			throw new ReferenceServiceException(
					"Reference service must be configued with an "
							+ "instance registry of ValueToReferenceConvertorSPI "
							+ "to enable on the fly mapping of arbitrary objects "
							+ "during compound registration");
	}

	@Override
	public final ErrorDocumentService getErrorDocumentService() {
		checkServices();
		return this.errorDocumentService;
	}

	@Override
	public final ListService getListService() {
		checkServices();
		return this.listService;
	}

	@Override
	public final ReferenceSetService getReferenceSetService() {
		checkServices();
		return this.referenceSetService;
	}

	/**
	 * Wraps the synchronous form, using the executeRunnable method to schedule
	 * it.
	 */
	@Override
	public void resolveIdentifierAsynch(final T2Reference id,
			final Set<Class<ExternalReferenceSPI>> ensureTypes,
			final ReferenceContext context,
			final ReferenceServiceResolutionCallback callback)
			throws ReferenceServiceException {
		checkServices();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					callback.identifierResolved(resolveIdentifier(id,
							ensureTypes, context));
				} catch (ReferenceServiceException rse) {
					callback.resolutionFailed(rse);
				}
			}
		};
		executeRunnable(r);
	}
}
