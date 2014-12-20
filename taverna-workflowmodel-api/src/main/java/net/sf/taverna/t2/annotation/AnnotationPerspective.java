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

import java.util.List;

/**
 * Responsible for the interpretation of an AnnotationChain (which may contain
 * conflicting or disputed information) into a set of AnnotationAssertion
 * instances from that chain which are valid given the chain and some
 * interpretation rule.
 * 
 * @author Tom Oinn
 * 
 */
public interface AnnotationPerspective {

	/**
	 * Evaluate the annotations and their curation events in the specified
	 * chain, resolve conflicts if possible and return the resultant set of
	 * annotations
	 * 
	 * @param chain
	 *            the annotation chain to evaluate
	 * @return the set of annotations which are valid within this chain
	 */
	public List<? extends AnnotationAssertion<?>> getAnnotations(
			AnnotationChain chain);

	/**
	 * Annotation chains may be in a disputed state if there are conflicting
	 * mutually exclusive events within them under the interpretation imposed by
	 * the annotation perspective and the perspective is unable to sensibly
	 * reconcile them. For example, if the perspective is configured to trust
	 * two parties equally and they disagree.
	 * 
	 * @param chain
	 *            the annotation chain to check for conflict
	 * @return true if there are conflicts under the interpretation of this
	 *         annotation perspective
	 */
	public boolean isDisputed(AnnotationChain chain);

}
