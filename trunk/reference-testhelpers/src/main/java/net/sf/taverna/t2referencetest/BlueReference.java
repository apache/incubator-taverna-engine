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
package net.sf.taverna.t2referencetest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.sf.taverna.t2.reference.AbstractExternalReference;
import net.sf.taverna.t2.reference.DereferenceException;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceContext;
import net.sf.taverna.t2.reference.ReferencedDataNature;

/**
 * BlueReferences carry their data as an internal String and have a resolution
 * cost of 1.0f whatever the value of that string.
 * 
 * @author Tom Oinn
 * 
 */
public class BlueReference extends AbstractExternalReference implements
		ExternalReferenceSPI {

	// Hold the 'value' of this reference, probably the simplest backing store
	// possible for an ExternalReferenceSPI implementation :)
	private String contents;

	public BlueReference() {
		//
	}

	public BlueReference(String contents) {
		this.contents = contents;
	}

	/**
	 * Set the 'value' of this reference as a string. It's not really a
	 * reference type in any true sense of the word, but it'll do for testing
	 * the augmentation system. This method is really here so you can configure
	 * test beans from spring.
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}

	/**
	 * Get the 'value' of this reference as a string, really just returns the
	 * internal string representation.
	 */
	public String getContents() {
		return this.contents;
	}

	/**
	 * Fakes a de-reference operation, returning a byte stream over the string
	 * data.
	 */
	public InputStream openStream(ReferenceContext arg0) {
		try {
			return new ByteArrayInputStream(this.contents
					.getBytes(getCharset()));
		} catch (UnsupportedEncodingException e) {
			throw new DereferenceException(e);
		}
	}

	/**
	 * Default resolution cost of 1.0f whatever the contents
	 */
	@Override
	public float getResolutionCost() {
		return 1.0f;
	}

	/**
	 * Data nature set to 'ReferencedDataNature.TEXT'
	 */
	@Override
	public ReferencedDataNature getDataNature() {
		return ReferencedDataNature.TEXT;
	}

	/**
	 * Character encoding set to 'UTF-8'
	 */
	@Override
	public String getCharset() {
		return "UTF-8";
	}

	/**
	 * String representation for testing, returns <code>blue{CONTENTS}</code>
	 */
	@Override
	public String toString() {
		return "blue{" + contents + "}";
	}

}
