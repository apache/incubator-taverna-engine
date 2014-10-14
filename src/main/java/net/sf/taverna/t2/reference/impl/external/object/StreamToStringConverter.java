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
package net.sf.taverna.t2.reference.impl.external.object;

import static net.sf.taverna.t2.reference.ReferencedDataNature.TEXT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import net.sf.taverna.t2.reference.ReferencedDataNature;
import net.sf.taverna.t2.reference.StreamToValueConverterSPI;

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
