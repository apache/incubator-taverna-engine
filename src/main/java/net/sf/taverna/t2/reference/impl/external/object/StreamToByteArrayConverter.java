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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.taverna.t2.reference.StreamToValueConverterSPI;

/**
 * Build a byte[] from an InputStream
 * 
 * @author Tom Oinn
 * 
 */
public class StreamToByteArrayConverter implements
		StreamToValueConverterSPI<byte[]> {

	private static final int CHUNK_SIZE = 4096;

	static byte[] readFile(InputStream reader) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[CHUNK_SIZE];
		int len;
		while ((len = reader.read(buf)) > 0) {
			bos.write(buf, 0, len);
		}
		return bos.toByteArray();
	}

	public Class<byte[]> getPojoClass() {
		return byte[].class;
	}

	public byte[] renderFrom(InputStream stream) {
		try {
			return readFile(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
