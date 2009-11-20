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

import java.io.InputStream;

/**
 * Constructs an ExternalReferenceSPI instance from a byte stream. Used by the
 * {@link ReferenceSetAugmentor} when there isn't a direct reference to
 * reference translation path available for a desired target type, but available
 * for external use wherever this functionality is needed.
 * <p>
 * Where an underlying resource is required this is extracted from the supplied
 * ReferenceContext, this implies that all methods in implementations of this
 * interface should be thread safe, allowing multiple concurrent threads
 * cleanly. For SPI purposes implementations should be java beans with default
 * constructors.
 * 
 * @author Tom Oinn
 */
public interface ExternalReferenceBuilderSPI<TargetType extends ExternalReferenceSPI> {

	/**
	 * Given a stream of bytes, build the appropriate target
	 * ExternalReferenceSPI implementation which would de-reference to the value
	 * of that stream and return it.
	 * 
	 * @param existingReferences
	 *            the references to be used as sources for the translation. In
	 *            general the implementation will use one of these references.
	 * @param context
	 *            a reference resolution context, needed potentially to
	 *            construct the new ExternalReferenceSchemeSPI, especially in
	 *            cases where the context contains security agents giving access
	 *            to a remote data staging system *
	 * @throws ExternalReferenceConstructionException
	 *             if an error occurs instantiating the new reference.
	 * @return the newly constructed ExternalReferenceSPI instance.
	 */
	public TargetType createReference(InputStream byteStream,
			ReferenceContext context);

	/**
	 * Expose the type of the ExternalReferenceSPI that this builder can
	 * construct
	 * 
	 * @return the class of ExternalReferenceSPI returned by the create
	 *         reference methods.
	 */
	public Class<TargetType> getReferenceType();

	/**
	 * Because the reference builder may rely on facilities provided to it
	 * through the context this method is available to check whether these
	 * facilities are sufficient.
	 * 
	 * @param context
	 *            the reference context that will be used to construct new
	 *            references
	 * @return whether the context contains necessary resources for the
	 *         reference construction process
	 */
	public boolean isEnabled(ReferenceContext context);

	/**
	 * Return an approximate complexity cost of the reference construction. In
	 * general we can't make any guarantees about this because the complexity of
	 * the construction depends on more than just the type involved - it can
	 * depend on local configuration, network location relative to the data
	 * stores referenced and in some cases on the data themselves. For now
	 * though we assign an approximation, the default value is 1.0f and lower
	 * values represent less costly operations.
	 */
	public float getConstructionCost();

}
