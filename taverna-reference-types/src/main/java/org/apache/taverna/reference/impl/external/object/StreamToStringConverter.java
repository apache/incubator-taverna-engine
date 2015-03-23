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

import static org.apache.taverna.reference.ReferencedDataNature.TEXT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.taverna.reference.ReferencedDataNature;
import org.apache.taverna.reference.StreamToValueConverterSPI;

/**
 * Build a String from an InputStream
 * 
 * @author Tom Oinn
 */
public class StreamToStringConverter implements
		StreamToValueConverterSPI<String> {
	private static final int END_OF_FILE = -1;
	private static final int CHUNK_SIZE = 4096;
	private static final Charset UTF8 = Charset.forName("UTF-8");

	/**
	 * Reads a text file and returns a string.
	 */
	static String readFile(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		StringBuilder buffer = new StringBuilder();
		char[] chunk = new char[CHUNK_SIZE];
		int character;
		while ((character = br.read(chunk)) != END_OF_FILE)
			buffer.append(chunk, 0, character);
		return buffer.toString();
	}

	@Override
	public Class<String> getPojoClass() {
		return String.class;
	}

	@Override
	public String renderFrom(InputStream stream,
			ReferencedDataNature dataNature, String charset) {
		try {
			if (charset != null && dataNature.equals(TEXT))
				try {
					Charset c = Charset.forName(charset);
					return readFile(new InputStreamReader(stream, c));
				} catch (IllegalArgumentException e1) {
					// Ignore; fallback below is good enough
				}
			return readFile(new InputStreamReader(stream, UTF8));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
