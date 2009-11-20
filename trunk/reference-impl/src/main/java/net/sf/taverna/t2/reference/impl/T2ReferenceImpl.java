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

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;
import net.sf.taverna.t2.reference.h3.HibernateComponentClass;

import org.apache.log4j.Logger;

/**
 * An implementation of T2Reference specific to the ReferenceSetImpl. This is
 * needed because ReferenceSetImpl uses a component based primary key driven
 * from the namespace and local parts of T2Reference. This in turn means we can
 * query hibernate directly with a T2Reference instance in the data access
 * object. Because this is only used as a component (i.e. a value type) we don't
 * need to define a hibernate mapping file for it.
 * 
 * @author Tom Oinn
 * 
 */
public class T2ReferenceImpl implements T2Reference, Serializable,
		HibernateComponentClass {

	private static Logger logger = Logger
	.getLogger(T2ReferenceImpl.class);

	private static final long serialVersionUID = 8363330461158750319L;
	private URI cachedUri = null;
	private String localPart;
	private String namespacePart;
	private boolean containsErrors = false;
	private T2ReferenceType referenceType = T2ReferenceType.ReferenceSet;
	private int depth = 0;

	public T2ReferenceImpl() {
		// Default constructor for Hibernate et al
	}

	/**
	 * Construct a deep copy of the given T2Reference
	 * 
	 * @param source
	 *            T2Reference to copy
	 */
	private T2ReferenceImpl(T2Reference source) {
		super();
		setNamespacePart(source.getNamespacePart());
		setLocalPart(source.getLocalPart());
		setContainsErrors(source.containsErrors());
		setReferenceType(source.getReferenceType());
		setDepth(source.getDepth());
	}

	public static T2ReferenceImpl getAsImpl(T2Reference source) {
		if (source instanceof T2ReferenceImpl) {
			return (T2ReferenceImpl) source;
		} else {
			return new T2ReferenceImpl(source);
		}
	}

	/**
	 * Return whether the identified entity either is or contains errors
	 */
	public boolean containsErrors() {
		return this.containsErrors;
	}

	/**
	 * Property accessor for Hibernate, complies with java bean spec
	 */
	public boolean getContainsErrors() {
		return this.containsErrors;
	}

	/**
	 * Get the depth of the entity referred to by this reference
	 */
	public int getDepth() {
		return this.depth;
	}

	/**
	 * Get the local part of the URI for this reference
	 */
	public String getLocalPart() {
		return this.localPart;
	}

	/**
	 * Get the namespace part of the URI for this reference
	 */
	public String getNamespacePart() {
		return namespacePart;
	}

	/**
	 * Get the type of the entity to which this reference refers
	 */
	public T2ReferenceType getReferenceType() {
		return this.referenceType;
	}

	/**
	 * This method is only ever called from within Hibernate when
	 * re-constructing the identifier component to set the namespace part of the
	 * identifier.
	 */
	public synchronized void setNamespacePart(String namespacePart) {
		this.namespacePart = namespacePart;
		this.hashCode = -1;
		cachedUri = null;
	}

	/**
	 * This method is only ever called from within Hibernate when
	 * re-constructing the identifier component to set the local part of the
	 * identifier.
	 */
	public synchronized void setLocalPart(String localPart) {
		this.localPart = localPart;
		this.hashCode = -1;
		cachedUri = null;
	}

	/**
	 * This method is only ever called from within Hibernate when
	 * re-constructing the identifier component to set the depth of the
	 * identifier.
	 */
	public synchronized void setDepth(int depth) {
		this.depth = depth;
		this.hashCode = -1;
		cachedUri = null;
	}

	/**
	 * This method is only ever called from within Hibernate when
	 * re-constructing the identifier component to set the error property of the
	 * identifier.
	 */
	public synchronized void setContainsErrors(boolean containsErrors) {
		this.containsErrors = containsErrors;
		cachedUri = null;
	}

	/**
	 * This method is only ever called from within Hibernate when
	 * re-constructing the identifier component to set the reference type
	 * property of the identifier.
	 */
	public synchronized void setReferenceType(T2ReferenceType type) {
		this.referenceType = type;
		cachedUri = null;
	}

	/**
	 * By default when printing an identifier we use {@link #toUri()}.{@link java.net.URI#toASCIIString() toASCIIString()}
	 */
	@Override
	public String toString() {
		return toUri().toASCIIString();
	}

	/**
	 * Drill inside an error document reference to get the error one deeper than
	 * this as long as it is at least depth 1.
	 */
	T2ReferenceImpl getDeeperErrorReference() {
		if (getReferenceType().equals(T2ReferenceType.ErrorDocument)) {
			if (getDepth() == 0) {
				throw new AssertionError(
						"Error identifier already has depth 0, cannot decrease");
			}
			T2ReferenceImpl result = new T2ReferenceImpl();
			result.setContainsErrors(true);
			result.setDepth(getDepth() - 1);
			result.setLocalPart(getLocalPart());
			result.setNamespacePart(getNamespacePart());
			result.setReferenceType(T2ReferenceType.ErrorDocument);
			return result;
		}
		throw new AssertionError(
				"Attempt to get a deeper reference on something that isn't an error ref");
	}

	/**
	 * Returns the identifier expressed as a {@link java.net.URI URI},
	 * constructed based on the reference type. For references to ReferenceSet
	 * this is
	 * <code>new URI("t2:ref//" + namespacePart + "?" + localPart)</code>
	 * leading to URIs of the form <code>t2:ref//namespace?local</code>
	 */
	public synchronized URI toUri() {
		if (cachedUri != null) {
			return cachedUri;
		} else if (referenceType.equals(T2ReferenceType.ReferenceSet)) {
			try {
				URI result = new URI("t2:ref//" + namespacePart + "?"
						+ localPart);
				cachedUri = result;
				return result;
			} catch (URISyntaxException e) {
				logger.error("Unable to create URI", e);
				return null;
			}
		} else if (referenceType.equals(T2ReferenceType.IdentifiedList)) {
			try {
				URI result = new URI("t2:list//" + namespacePart + "?"
						+ localPart + "/" + containsErrors + "/" + depth);
				cachedUri = result;
				return result;
			} catch (URISyntaxException e) {
				logger.error("Unable to create URI", e);
				return null;
			}
		} else if (referenceType.equals(T2ReferenceType.ErrorDocument)) {
			try {
				URI result = new URI("t2:error//" + namespacePart + "?"
						+ localPart + "/" + depth);
				cachedUri = result;
				return result;
			} catch (URISyntaxException e) {
				logger.error("Unable to create URI", e);
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Use the equality operator over the URI representation of this bean.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other instanceof T2ReferenceImpl) {
			T2ReferenceImpl otherRef = (T2ReferenceImpl) other;
			if (localPart.equals(otherRef.localPart)
					&& namespacePart.equals(otherRef.namespacePart)
					&& depth == otherRef.depth) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private int hashCode = -1;

	/**
	 * Use hashcode method from the string representation of namespace, local
	 * and depth parts
	 */
	@Override
	public synchronized int hashCode() {
		if (this.hashCode == -1) {
			this.hashCode = getCompactForm().hashCode();
		}
		return this.hashCode;
	}

	private String compactForm = null;

	public synchronized String getCompactForm() {
		if (this.compactForm == null) {
			this.compactForm = getNamespacePart() + ":" + getLocalPart() + ":"
					+ getDepth();
		}
		return this.compactForm;
	}

}
