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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.taverna.reference.ReferencedDataNature;
import org.apache.taverna.reference.StreamToValueConverterSPI;

/**
 * Build a byte[] from an InputStream
 * 
 * @author Tom Oinn
 */
public class StreamToByteArrayConverter implements
		StreamToValueConverterSPI<byte[]> {
	private static final int CHUNK_SIZE = 4096;

	static byte[] readFile(InputStream reader) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[CHUNK_SIZE];
		int len;
		while ((len = reader.read(buf)) > 0)
			bos.write(buf, 0, len);
		return bos.toByteArray();
	}

	@Override
	public Class<byte[]> getPojoClass() {
		return byte[].class;
	}

	@Override
	public byte[] renderFrom(InputStream stream,
			ReferencedDataNature dataNature, String charset) {
		try {
			return readFile(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
