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

import java.nio.charset.Charset;

import org.apache.taverna.reference.ExternalReferenceTranslatorSPI;
import org.apache.taverna.reference.ReferenceContext;

public class InlineStringToInlineByteTranslator implements
		ExternalReferenceTranslatorSPI<InlineStringReference, InlineByteArrayReference> {
	private static final Charset UTF8 = Charset.forName("UTF-8");
	
	@Override
	public InlineByteArrayReference createReference(
			InlineStringReference sourceReference, ReferenceContext context) {
		byte[] bytes = sourceReference.getValue().getBytes(UTF8);
		InlineByteArrayReference ref = new InlineByteArrayReference();
		ref.setValue(bytes);	
		return ref;
	}

	@Override
	public Class<InlineStringReference> getSourceReferenceType() {
		return InlineStringReference.class;
	}

	@Override
	public Class<InlineByteArrayReference> getTargetReferenceType() {
		return InlineByteArrayReference.class;
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
