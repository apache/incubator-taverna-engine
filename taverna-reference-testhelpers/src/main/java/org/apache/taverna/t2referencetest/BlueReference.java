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

package org.apache.taverna.t2referencetest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.taverna.reference.AbstractExternalReference;
import org.apache.taverna.reference.DereferenceException;
import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ReferencedDataNature;

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
	@Override
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

	@Override
	public Long getApproximateSizeInBytes() {
		return new Long(contents.getBytes().length);
	}

	@Override
	public ExternalReferenceSPI clone() throws CloneNotSupportedException {
		return new BlueReference(this.getContents());
	}

}
