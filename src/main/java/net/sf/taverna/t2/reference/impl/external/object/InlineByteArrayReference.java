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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import net.sf.taverna.t2.reference.AbstractExternalReference;
import net.sf.taverna.t2.reference.DereferenceException;
import net.sf.taverna.t2.reference.ReferenceContext;
import net.sf.taverna.t2.reference.ValueCarryingExternalReference;

/**
 * A reference implementation that inlines an array of bytes. Rather
 * unpleasantly this currently exposes the byte array to Hibernate through a
 * textual value, as Derby allows long textual values but not long binary ones
 * (yuck). As it uses a fixed character set (UTF-8) to store and load I believe
 * this doesn't break things.
 * 
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

	public byte[] getValue() {
		return bytes;
	}

	public Class<byte[]> getValueType() {
		return byte[].class;
	}

	public InputStream openStream(ReferenceContext context)
			throws DereferenceException {
		return new ByteArrayInputStream(bytes);
	}

	private static Charset charset = Charset.forName("UTF-8");

	public String getContents() {
		try {
			return new String(Base64.encodeBase64(bytes), charset.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setContents(String contentsAsString) {
		try {
			this.bytes = Base64.decodeBase64(contentsAsString.getBytes(charset.toString()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
