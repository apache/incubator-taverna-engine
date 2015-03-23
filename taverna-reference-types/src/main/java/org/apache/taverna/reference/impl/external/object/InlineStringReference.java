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

package org.apache.taverna.reference.impl.external.object;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.taverna.reference.AbstractExternalReference;
import org.apache.taverna.reference.DereferenceException;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ReferencedDataNature;
import org.apache.taverna.reference.ValueCarryingExternalReference;

/**
 * Contains and references a String value
 * 
 * @author Tom Oinn
 */
public class InlineStringReference extends AbstractExternalReference implements
		ValueCarryingExternalReference<String> {
	/**
	 * Hold the 'value' of this reference, probably the simplest backing store
	 * possible for an ExternalReferenceSPI implementation :)
	 */
	private String contents;
	private transient Long length;

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
			return new ByteArrayInputStream(contents.getBytes(getCharset()));
		} catch (UnsupportedEncodingException e) {
			throw new DereferenceException(e);
		}
	}

	/**
	 * Default resolution cost of 0.0f whatever the contents
	 */
	@Override
	public float getResolutionCost() {
		return 0.0f;
	}

	/**
	 * Data nature set to 'ReferencedDataNature.TEXT'
	 */
	@Override
	public ReferencedDataNature getDataNature() {
		return ReferencedDataNature.TEXT;
	}

	/**
	 * Character encoding set to 'UTF-8' by default
	 */
	@Override
	public String getCharset() {
		return "UTF-8";
	}

	/**
	 * String representation for testing, returns <code>string{CONTENTS}</code>
	 */
	@Override
	public String toString() {
		return "string{" + contents + "}";
	}

	@Override
	public String getValue() {
		return getContents();
	}

	@Override
	public Class<String> getValueType() {
		return String.class;
	}

	@Override
	public Long getApproximateSizeInBytes() {
		if (length == null)
			length = new Long(contents.getBytes().length);
		return length;
	}

	@Override
	public InlineStringReference clone() {
		InlineStringReference result = new InlineStringReference();
		result.setContents(this.getContents());
		return result;
	}
}
