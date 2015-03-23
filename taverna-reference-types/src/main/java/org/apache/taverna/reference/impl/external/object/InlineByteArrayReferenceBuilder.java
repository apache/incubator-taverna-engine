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

import static org.apache.taverna.reference.impl.external.object.StreamToByteArrayConverter.readFile;

import java.io.IOException;
import java.io.InputStream;

import org.apache.taverna.reference.ExternalReferenceBuilderSPI;
import org.apache.taverna.reference.ExternalReferenceConstructionException;
import org.apache.taverna.reference.ReferenceContext;

/**
 * Build an InlineByteArrayReference from an InputStream
 * 
 * @author Tom Oinn
 * 
 */
public class InlineByteArrayReferenceBuilder implements
		ExternalReferenceBuilderSPI<InlineByteArrayReference> {
	@Override
	public InlineByteArrayReference createReference(InputStream byteStream,
			ReferenceContext context) {
		try {
			byte[] contents = readFile(byteStream);
			InlineByteArrayReference ref = new InlineByteArrayReference();
			ref.setValue(contents);
			return ref;
		} catch (IOException e) {
			throw new ExternalReferenceConstructionException(e);
		}
	}

	@Override
	public float getConstructionCost() {
		return 0.1f;
	}

	@Override
	public Class<InlineByteArrayReference> getReferenceType() {
		return InlineByteArrayReference.class;
	}

	@Override
	public boolean isEnabled(ReferenceContext context) {
		return true;
	}
}
