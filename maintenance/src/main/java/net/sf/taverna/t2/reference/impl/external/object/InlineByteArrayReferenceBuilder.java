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

import java.io.IOException;
import java.io.InputStream;

import net.sf.taverna.t2.reference.ExternalReferenceBuilderSPI;
import net.sf.taverna.t2.reference.ExternalReferenceConstructionException;
import net.sf.taverna.t2.reference.ReferenceContext;

/**
 * Build an InlineByteArrayReference from an InputStream
 * 
 * @author Tom Oinn
 * 
 */
public class InlineByteArrayReferenceBuilder implements
		ExternalReferenceBuilderSPI<InlineByteArrayReference> {

	public InlineByteArrayReference createReference(InputStream byteStream,
			ReferenceContext context) {
		try {
			byte[] contents = StreamToByteArrayConverter.readFile(byteStream);
			InlineByteArrayReference ref = new InlineByteArrayReference();
			ref.setValue(contents);
			return ref;
		} catch (IOException e) {
			throw new ExternalReferenceConstructionException(e);
		}
	}

	public float getConstructionCost() {
		return 0.1f;
	}

	public Class<InlineByteArrayReference> getReferenceType() {
		return InlineByteArrayReference.class;
	}

	public boolean isEnabled(ReferenceContext context) {
		return true;
	}

}
