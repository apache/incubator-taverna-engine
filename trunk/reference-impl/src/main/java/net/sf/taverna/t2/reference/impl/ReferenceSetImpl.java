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

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.h3.HibernateMappedEntity;

/**
 * An implementation of ReferenceSet with the additional methods and metadata
 * required by Hibernate3 to allow it to be persisted in a relational store. As
 * with everything else in this package you shouldn't be using this class
 * directly! Instead of this class you should use the registration methods on
 * {@link net.sf.taverna.t2.reference.ReferenceSetService}, implementations of
 * that interface will handle the construction of ReferenceSet implementations
 * (including this one).
 * 
 * @author Tom Oinn
 * 
 */
public class ReferenceSetImpl extends AbstractEntityImpl implements
		ReferenceSet, HibernateMappedEntity {

	private Set<ExternalReferenceSPI> externalReferences;
	
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
		StringBuffer sb = new StringBuffer();
		sb.append(getId() + " [" + externalReferences.size() + "]\n");

		for (ExternalReferenceSPI ref : externalReferences) {
			sb.append("  " + ref.toString() + "\n");
		}
		return sb.toString();

	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ExternalReferenceSPI> getExternalReferences() {
		return this.externalReferences;
	}

	/**
	 * This method is only ever called from within Hibernate, and is used to
	 * initialize the set of external references.
	 */
	public void setExternalReferences(Set<ExternalReferenceSPI> newReferences) {
		this.externalReferences = newReferences;
	}

}
