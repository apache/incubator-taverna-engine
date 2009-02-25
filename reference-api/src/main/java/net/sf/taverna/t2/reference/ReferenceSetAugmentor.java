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

import java.util.Set;

/**
 * Provides a framework to find and engage appropriate instances of
 * {@link ExternalReferenceTranslatorSPI} and
 * {@link ExternalReferenceBuilderSPI} to build external references from,
 * respectively, other external references and from streams. These are then used
 * to augment the contents of implementations of {@link ReferenceSet} with
 * additional {@link ExternalReferenceSPI} implementations.
 * <p>
 * Methods in this interface throw the runtime exception
 * {@link ReferenceSetAugmentationException} for all problems, other exceptions
 * are wrapped in this type and re-thrown.
 * 
 * @author Tom Oinn
 */
public interface ReferenceSetAugmentor {

	/**
	 * Attempts to modify the supplied ReferenceSet such that it contains an
	 * implementation of at least one of the ExternalReferenceSPI classes
	 * specified. Uses the supplied context if required to build or translate
	 * existing references within the reference set.
	 * 
	 * @param references
	 *            reference set object to augment
	 * @param targetReferenceTypes
	 *            a set of Class objects, this method succeeds if it can create
	 *            an instance of at least one of these pointing to the same data
	 *            as the other external references in the supplied reference set
	 * @param context
	 *            a reference resolution context, potentially required for
	 *            access to the existing references or for creation of the
	 *            augmentations
	 * @return a set of new ExternalReferenceSPI instances such that the union
	 *         of this set with the pre-existing reference set satisfies the
	 *         target reference constraint. It is the responsibility of the
	 *         caller to re-integrate these references into the original
	 *         ReferenceSet if so desired.
	 * @throws ReferenceSetAugmentationException
	 *             if a problem occurs either in configuration of the
	 *             ReferenceSetAugmentor or in the augmentation process itself.
	 *             Any other exception types are wrapped in this and re-thrown.
	 */
	public Set<ExternalReferenceSPI> augmentReferenceSet(ReferenceSet references,
			Set<Class<ExternalReferenceSPI>> targetReferenceTypes,
			ReferenceContext context) throws ReferenceSetAugmentationException;

	/**
	 * As with {@link #augmentReferenceSet(ReferenceSet, Set, ReferenceContext)}
	 * but called in an asynchronous fashion. Returns immediately and uses the
	 * supplied instance of {@link ReferenceSetAugmentorCallback} to provide
	 * either the augmented {@link ReferenceSet} or an exception indicating a
	 * failure in the augmentation process.
	 * 
	 * @param callback
	 *            callback object used to indicate failure or to return the
	 *            modified reference set
	 * @throws ReferenceSetAugmentationException
	 *             if the ReferenceSetAugmentor is missing critical
	 *             configuration. Exceptions that happen during augmentation or
	 *             as a result of a failure to find an appropriate augmentation
	 *             path are signalled by calls to the callback object, this
	 *             method only throws the exception if it can't even try to do
	 *             the augmentation for some reason.
	 */
	public void augmentReferenceSetAsynch(ReferenceSet references,
			Set<Class<ExternalReferenceSPI>> targetReferenceTypes,
			ReferenceContext context, ReferenceSetAugmentorCallback callback)
			throws ReferenceSetAugmentationException;

}
