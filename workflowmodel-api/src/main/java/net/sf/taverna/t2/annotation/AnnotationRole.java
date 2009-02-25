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
package net.sf.taverna.t2.annotation;

/**
 * Specifies the role of an AnnotationAssertion within an AnnotationChain
 * 
 * @author Tom Oinn
 */
public enum AnnotationRole {

	/**
	 * The information assertion is the first in the chain (if this is applied
	 * to an annotation that isn't the earliest in its chain it should be
	 * treated as a validation failure)
	 */
	INITIAL_ASSERTION,

	/**
	 * The information assertion was added to the chain to refine the existing
	 * annotation assertion or assertions, such as cases where a generic
	 * description exists which can be specialized in a particular instance but
	 * where the original more generic form is still correct
	 */
	REFINEMENT,

	/**
	 * The information assertion was added to the chain in order to override an
	 * earlier information assertion which was regarded as incorrect.
	 */
	REPLACEMENT;

}
