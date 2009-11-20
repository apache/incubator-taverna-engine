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
 * A fact about an annotated entity is expressed in terms of an annotation
 * chain. The annotation chain contains one or more information assertions in a
 * list ordered by the creation date of each assertion. Annotation chains are
 * then interpreted by an AnnotationPerspective which is responsible for
 * reasoning over the information in the chain and extracting the set of
 * information assertions that are valid according to the rules in the
 * particular AnnotationPerspective.
 * 
 * @author Tom Oinn
 * 
 */
public interface AnnotationChain {

	/**
	 * Returns the ordered list of AnnotationAssertions. This is the 'raw' set
	 * of annotations in creation order - this order is not necessarily the
	 * order they were curated, and may include refuted or otherwise wrong
	 * annotations. Consumers of this API are recommended to use an
	 * AnnotationPerspective to resolve any such conflicts appropriately.
	 * 
	 * @return read only copy of the ordered list of AnnotationAssertion
	 *         instances
	 */
	List<AnnotationAssertion<?>> getAssertions();

}
