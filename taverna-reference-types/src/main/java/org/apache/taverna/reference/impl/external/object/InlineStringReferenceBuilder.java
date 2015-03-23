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

import static org.apache.taverna.reference.impl.external.object.StreamToStringConverter.readFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.taverna.reference.ExternalReferenceBuilderSPI;
import org.apache.taverna.reference.ExternalReferenceConstructionException;
import org.apache.taverna.reference.ReferenceContext;

/**
 * Build an InlineStringReference from an InputStream
 * 
 * @author Tom Oinn
 */
public class InlineStringReferenceBuilder implements
		ExternalReferenceBuilderSPI<InlineStringReference> {
	private static final Charset UTF8 = Charset.forName("UTF-8");
	
	@Override
	public InlineStringReference createReference(InputStream byteStream,
			ReferenceContext context) {
		try {
			/*
			 * UTF8 is a slightly saner default than system default for most
			 * bytestreams
			 */
			String contents = readFile(new BufferedReader(
					new InputStreamReader(byteStream, UTF8)));
			InlineStringReference ref = new InlineStringReference();
			ref.setContents(contents);
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
	public Class<InlineStringReference> getReferenceType() {
		return InlineStringReference.class;
	}

	@Override
	public boolean isEnabled(ReferenceContext context) {
		return true;
	}
}
