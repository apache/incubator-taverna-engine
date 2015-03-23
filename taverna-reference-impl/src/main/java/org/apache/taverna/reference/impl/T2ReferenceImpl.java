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

import static org.apache.taverna.reference.T2ReferenceType.ErrorDocument;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.T2ReferenceType;
import org.apache.taverna.reference.h3.HibernateComponentClass;

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
 * @author David Withers
 */
public class T2ReferenceImpl implements T2Reference, Serializable, HibernateComponentClass {
	private static Logger logger = Logger.getLogger(T2ReferenceImpl.class);

	private static final long serialVersionUID = 8363330461158750319L;
	private String localPart;
	private String namespacePart;
	private long localMostSigBits, localLeastSigBits;
	private boolean containsErrors = false;
	private T2ReferenceType referenceType = T2ReferenceType.ReferenceSet;
	private int depth = 0;
	private transient URI cachedURI;

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
		if (source instanceof T2ReferenceImpl)
			return (T2ReferenceImpl) source;
		return new T2ReferenceImpl(source);
	}

	/**
	 * Return whether the identified entity either is or contains errors
	 */
	@Override
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
	@Override
	public int getDepth() {
		return this.depth;
	}

	/**
	 * Get the local part of the URI for this reference
	 */
	@Override
	public String getLocalPart() {
		if (localPart == null) {
			UUID localPartUUID = new UUID(localMostSigBits, localLeastSigBits);
			return localPartUUID.toString();
		}
		return localPart;
	}

	/**
	 * Get the namespace part of the URI for this reference
	 */
	@Override
	public String getNamespacePart() {
		return namespacePart;
	}

	/**
	 * Get the type of the entity to which this reference refers
	 */
	@Override
	public T2ReferenceType getReferenceType() {
		return referenceType;
	}

	/**
	 * This method is only ever called from within Hibernate when
	 * re-constructing the identifier component to set the namespace part of the
	 * identifier.
	 */
	public synchronized void setNamespacePart(String namespacePart) {
		this.namespacePart = namespacePart;
	}

	/**
	 * This method is only ever called from within Hibernate when
	 * re-constructing the identifier component to set the local part of the
	 * identifier.
	 */
	public synchronized void setLocalPart(String localPart) {
		try {
			UUID namespacePartUUID = UUID.fromString(localPart);
			localMostSigBits = namespacePartUUID.getMostSignificantBits();
			localLeastSigBits = namespacePartUUID.getLeastSignificantBits();
			this.localPart = null;
		} catch (IllegalArgumentException e) {
			this.localPart = localPart;
		}
	}

	/**
	 * This method is only ever called from within Hibernate when
	 * re-constructing the identifier component to set the depth of the
	 * identifier.
	 */
	public synchronized void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * This method is only ever called from within Hibernate when
	 * re-constructing the identifier component to set the error property of the
	 * identifier.
	 */
	public synchronized void setContainsErrors(boolean containsErrors) {
		this.containsErrors = containsErrors;
	}

	/**
	 * This method is only ever called from within Hibernate when
	 * re-constructing the identifier component to set the reference type
	 * property of the identifier.
	 */
	public synchronized void setReferenceType(T2ReferenceType type) {
		this.referenceType = type;
	}

	/**
	 * By default when printing an identifier we use {@link #toUri()}.
	 * {@link java.net.URI#toASCIIString() toASCIIString()}
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
		if (!getReferenceType().equals(ErrorDocument))
			throw new AssertionError(
					"Attempt to get a deeper reference on something that isn't an error ref");
		if (getDepth() == 0)
			throw new AssertionError(
					"Error identifier already has depth 0, cannot decrease");

		T2ReferenceImpl result = new T2ReferenceImpl();
		result.setContainsErrors(true);
		result.setDepth(getDepth() - 1);
		result.setLocalPart(getLocalPart());
		result.setNamespacePart(getNamespacePart());
		result.setReferenceType(ErrorDocument);
		return result;
	}

	/**
	 * Returns the identifier expressed as a {@link java.net.URI URI},
	 * constructed based on the reference type. For references to ReferenceSet
	 * this is
	 * <code>new URI("t2:ref//" + namespacePart + "?" + localPart)</code>
	 * leading to URIs of the form <code>t2:ref//namespace?local</code>
	 */
	@Override
	public synchronized URI toUri() {
		try {
			if (cachedURI == null)
				switch (referenceType) {
				case ReferenceSet:
					cachedURI = new URI("t2:ref//" + getNamespacePart() + "?"
							+ getLocalPart());
				case IdentifiedList:
					cachedURI = new URI("t2:list//" + getNamespacePart() + "?"
							+ getLocalPart() + "/" + containsErrors + "/"
							+ depth);
				case ErrorDocument:
					cachedURI = new URI("t2:error//" + getNamespacePart() + "?"
							+ getLocalPart() + "/" + depth);
				}
		} catch (URISyntaxException e) {
			logger.error("Unable to create URI", e);
		}
		return cachedURI;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + depth;
		result = 31 * result + (int) (localLeastSigBits ^ (localLeastSigBits >>> 32));
		result = 31 * result + (int) (localMostSigBits ^ (localMostSigBits >>> 32));
		result = 31 * result + ((localPart == null) ? 0 : localPart.hashCode());
		result = 31 * result + ((namespacePart == null) ? 0 : namespacePart.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		T2ReferenceImpl other = (T2ReferenceImpl) obj;
		if (depth != other.depth)
			return false;
		if (localLeastSigBits != other.localLeastSigBits)
			return false;
		if (localMostSigBits != other.localMostSigBits)
			return false;
		if (localPart == null) {
			if (other.localPart != null)
				return false;
		} else if (!localPart.equals(other.localPart))
			return false;
		if (namespacePart == null) {
			if (other.namespacePart != null)
				return false;
		} else if (!namespacePart.equals(other.namespacePart))
			return false;
		return true;
	}

	public synchronized String getCompactForm() {
		return getNamespacePart() + ":" + getLocalPart() + ":" + getDepth();
	}
}
