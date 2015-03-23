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

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.reference.h3.HibernateMappedEntity;

/**
 * An implementation of ReferenceSet with the additional methods and metadata
 * required by Hibernate3 to allow it to be persisted in a relational store. As
 * with everything else in this package you shouldn't be using this class
 * directly! Instead of this class you should use the registration methods on
 * {@link org.apache.taverna.reference.ReferenceSetService}, implementations of
 * that interface will handle the construction of ReferenceSet implementations
 * (including this one).
 * 
 * @author Tom Oinn
 */
public class ReferenceSetImpl extends AbstractEntityImpl implements
		ReferenceSet, HibernateMappedEntity {
	private Set<ExternalReferenceSPI> externalReferences;
	private Long approximateSizeInBytes = new Long(-1);
	
	/**
	 * Construct a new ReferenceSetImpl with the given set of external
	 * references and identifier.
	 * 
	 * @param references
	 *            the set of ExternalReferenceSPI which this reference set
	 *            should contain initially
	 * @param id
	 *            the T2Reference to use, must be an instance of
	 *            ReferenceSetT2ReferenceImpl so hibernate can make use of it as
	 *            a compound primary key component
	 */
	public ReferenceSetImpl(Set<ExternalReferenceSPI> references,
			T2ReferenceImpl id) {
		setTypedId(id);
		this.externalReferences = references;
		
		//  Should be at least one - otherwise we cannot calculate the data size
		if (externalReferences != null && externalReferences.size() > 0) {
			// Just take the first ExternalReferenceSPI returned
			ExternalReferenceSPI externalReferenceSPI = externalReferences
					.toArray(new ExternalReferenceSPI[0])[0];
			approximateSizeInBytes = externalReferenceSPI
					.getApproximateSizeInBytes();
		}
	}

	/**
	 * Default constructor, used by Hibernate when reconstructing this bean from
	 * the database. If you call this directly from your code you must then call
	 * both {@link #setExternalReferences(Set)} and
	 * {@link #setId(T2ReferenceImpl)} before any use of the reference set. If
	 * you're not writing the reference manager implementation you shouldn't be
	 * using this class anyway.
	 */
	public ReferenceSetImpl() {
		//
	}

	/**
	 * For debugging purposes, prints a summary of the contents and identifier
	 * of this reference set.
	 * 
	 * @return human readable string representation of this object. This is not
	 *         regarded as 'stable' and should not be parsed for any reason!
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getId()).append(" [").append(externalReferences.size())
				.append("]\n");
		for (ExternalReferenceSPI ref : externalReferences)
			sb.append("  ").append(ref).append("\n");
		return sb.toString();

	}

	@Override
	public Set<ExternalReferenceSPI> getExternalReferences() {
		return externalReferences;
	}

	/**
	 * This method is only ever called from within Hibernate, and is used to
	 * initialize the set of external references.
	 */
	public void setExternalReferences(Set<ExternalReferenceSPI> newReferences) {
		this.externalReferences = newReferences;
	}

	public void setApproximateSizeInBytes(Long sizeInBytes) {
		this.approximateSizeInBytes = sizeInBytes;
	}

	@Override
	public Long getApproximateSizeInBytes() {
		return approximateSizeInBytes;
	}
}
