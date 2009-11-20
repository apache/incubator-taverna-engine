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
package net.sf.taverna.t2.reference;

/**
 * SPI for components that can convert an arbitrary object to an
 * ExternalReferenceSPI representing the value of that object. Used by
 * implementations of {@link ReferenceService#register(Object, int, boolean)} to
 * map arbitrary objects to ExternalReferenceSPI instances if encountered during
 * the registration process. This SPI is only used if the boolean
 * useConverterSPI parameter is set to true on that method.
 * 
 * @author Tom Oinn
 * 
 */
public interface ValueToReferenceConverterSPI {

	/**
	 * Can this SPI implementation convert the specified object to an
	 * ExternalReferenceSPI? This test should be as lightweight as possible, and
	 * will usually be based on the Class of the object supplied.
	 * 
	 * @param context
	 *            a ReferenceContext to use if required by the plugin, the
	 *            ability to convert should be interpreted in the scope of this
	 *            context. In general the context probably not used by most
	 *            implementations but it's here if required.
	 * 
	 * @return whether this converter is applicable to the specified object
	 */
	public boolean canConvert(Object o, ReferenceContext context);

	/**
	 * Construct and return a new ExternalReferenceSPI implementation which is
	 * in some way equivalent to the supplied object. This is not intended to be
	 * a two-way process necessarily, although the conversion should attempt to
	 * be conservative (so not actually changing the data!).
	 * 
	 * @param context
	 *            a ReferenceContext to use, if required, during construction of
	 *            the new external reference
	 * @return A new instance of ExternalReferenceSPI which references, as far
	 *         as possible, the value represented by the specified object
	 * @throws ValueToReferenceConversionException
	 *             if any problem occurs during the conversion
	 */
	public ExternalReferenceSPI convert(Object o, ReferenceContext context)
			throws ValueToReferenceConversionException;

}
