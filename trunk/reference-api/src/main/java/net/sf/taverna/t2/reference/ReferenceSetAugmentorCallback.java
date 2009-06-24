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
 * Callback interface used when augmenting a ReferenceSet in an asynchronous
 * fashion through
 * {@link ReferenceSetAugmentor#augmentReferenceSetAsynch(ReferenceSet, Set, ReferenceContext, ReferenceSetAugmentorCallback) augmentReferenceSetAsynch}
 * in {@link ReferenceSetAugmentor}.
 * 
 * @author Tom Oinn
 * 
 */
public interface ReferenceSetAugmentorCallback {

	/**
	 * Called when the augmentation has succeeded
	 * 
	 * @param newReferences
	 *            a set of ExternalReferenceSPI instances created during the
	 *            augmentation process. It is the responsibility of the caller
	 *            to re-integrate these back into the ReferenceSet used in the
	 *            translation
	 */
	public void augmentationCompleted(Set<ExternalReferenceSPI> newReferences);

	/**
	 * Called when the augmentation has failed for some reason
	 * 
	 * @param cause
	 *            a {@link ReferenceSetAugmentationException} object describing
	 *            the failure.
	 */
	public void augmentationFailed(ReferenceSetAugmentationException cause);

}
