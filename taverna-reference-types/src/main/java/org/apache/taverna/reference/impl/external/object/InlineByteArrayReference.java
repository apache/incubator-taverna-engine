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

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.taverna.reference.AbstractExternalReference;
import org.apache.taverna.reference.DereferenceException;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ValueCarryingExternalReference;

/**
 * A reference implementation that inlines an array of bytes. Rather
 * unpleasantly this currently exposes the byte array to Hibernate through a
 * textual value, as Derby allows long textual values but not long binary ones
 * (yuck). As it uses a fixed character set (UTF-8) to store and load I believe
 * this doesn't break things.
 * <p>
 * Unfortunately this does break things (binaries get corrupted) so I've added
 * base64 encoding of the value as a workaround.
 * 
 * @author Tom Oinn
 * @author David Withers
 */
public class InlineByteArrayReference extends AbstractExternalReference
		implements ValueCarryingExternalReference<byte[]> {
	private byte[] bytes = new byte[0];

	public void setValue(byte[] newBytes) {
		this.bytes = newBytes;
	}

	@Override
	public byte[] getValue() {
		return bytes;
	}

	@Override
	public Class<byte[]> getValueType() {
		return byte[].class;
	}

	@Override
	public InputStream openStream(ReferenceContext context)
			throws DereferenceException {
		return new ByteArrayInputStream(bytes);
	}

	private static final Charset charset = Charset.forName("UTF-8");

	public String getContents() {
		return new String(encodeBase64(bytes), charset);
	}

	public void setContents(String contentsAsString) {
		this.bytes = decodeBase64(contentsAsString.getBytes(charset));
	}

	@Override
	public Long getApproximateSizeInBytes() {
		return new Long(bytes.length);
	}

	@Override
	public InlineByteArrayReference clone() {
		InlineByteArrayReference result = new InlineByteArrayReference();
		result.setValue(this.getValue().clone());
		return result;
	}
}
