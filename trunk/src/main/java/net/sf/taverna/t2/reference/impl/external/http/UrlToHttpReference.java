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
package net.sf.taverna.t2.reference.impl.external.http;

import java.net.URL;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceContext;
import net.sf.taverna.t2.reference.ValueToReferenceConversionException;
import net.sf.taverna.t2.reference.ValueToReferenceConverterSPI;

/**
 * Convert a URL with http protocol to a HttpReference reference type
 * 
 * @author Tom Oinn
 * 
 */
public class UrlToHttpReference implements ValueToReferenceConverterSPI {

	/**
	 * Can convert if the object is an instance of java.net.URL and the protocol
	 * is HTTP
	 */
	public boolean canConvert(Object o, ReferenceContext context) {
		if (o instanceof URL) {
			if (((URL) o).getProtocol().equalsIgnoreCase("http")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return a new HttpReference constructed from
	 * <code>((URL)o).toExternalForm()</code>
	 */
	public ExternalReferenceSPI convert(Object o, ReferenceContext context)
			throws ValueToReferenceConversionException {
		HttpReference result = new HttpReference();
		result.setHttpUrlString(((URL) o).toExternalForm());
		return result;
	}

}
