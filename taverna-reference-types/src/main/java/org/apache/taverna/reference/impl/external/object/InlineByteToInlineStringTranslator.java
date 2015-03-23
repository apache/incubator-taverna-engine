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

import java.io.UnsupportedEncodingException;

import org.apache.taverna.reference.ExternalReferenceConstructionException;
import org.apache.taverna.reference.ExternalReferenceTranslatorSPI;
import org.apache.taverna.reference.ReferenceContext;

public class InlineByteToInlineStringTranslator
		implements
		ExternalReferenceTranslatorSPI<InlineByteArrayReference, InlineStringReference> {
	@Override
	public InlineStringReference createReference(
			InlineByteArrayReference sourceReference, ReferenceContext context) {
		String contents;
		try {
			String charset = sourceReference.getCharset();
			if (charset == null)
				// usual fallback:
				charset = "UTF-8";
			contents = new String(sourceReference.getValue(), charset);
		} catch (UnsupportedEncodingException e) {
			String msg = "Unknown character set "
					+ sourceReference.getCharset();
			throw new ExternalReferenceConstructionException(msg, e);
		}
		InlineStringReference ref = new InlineStringReference();
		ref.setContents(contents);
		return ref;
	}

	@Override
	public Class<InlineByteArrayReference> getSourceReferenceType() {
		return InlineByteArrayReference.class;
	}

	@Override
	public Class<InlineStringReference> getTargetReferenceType() {
		return InlineStringReference.class;
	}

	@Override
	public boolean isEnabled(ReferenceContext context) {
		return true;
	}

	@Override
	public float getTranslationCost() {
		return 0.001f;
	}
}
