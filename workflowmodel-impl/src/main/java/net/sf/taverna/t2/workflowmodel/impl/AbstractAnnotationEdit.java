/*******************************************************************************
 * Copyright (C) 2007-2014 The University of Manchester   
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
package net.sf.taverna.t2.workflowmodel.impl;

import net.sf.taverna.t2.annotation.AnnotationAssertion;
import net.sf.taverna.t2.annotation.AnnotationBeanSPI;
import net.sf.taverna.t2.annotation.impl.AnnotationAssertionImpl;
import net.sf.taverna.t2.workflowmodel.EditException;

/**
 * Abstraction of an edit acting on a AnnotationAssertion instance. Handles the
 * check to see that the AnnotationAssertion supplied is really an
 * AnnotationAssertionImpl.
 */
abstract class AbstractAnnotationEdit extends
		EditSupport<AnnotationAssertion<AnnotationBeanSPI>> {
	private final AnnotationAssertionImpl annotation;

	protected AbstractAnnotationEdit(AnnotationAssertion<AnnotationBeanSPI> annotation) {
		if (annotation == null)
			throw new RuntimeException(
					"Cannot construct an annotation edit with null annotation");
		if (!(annotation instanceof AnnotationAssertionImpl))
			throw new RuntimeException(
					"Edit cannot be applied to an AnnotationAssertion which isn't an instance of AnnotationAssertionImpl");
		this.annotation = (AnnotationAssertionImpl) annotation;
	}

	@Override
	public final AnnotationAssertion<AnnotationBeanSPI> applyEdit()
			throws EditException {
		synchronized (annotation) {
			doEditAction(annotation);
		}
		return annotation;
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param annotationAssertion
	 *            The AnnotationAssertionImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(
			AnnotationAssertionImpl annotationAssertion) throws EditException;

	@Override
	public final AnnotationAssertion<AnnotationBeanSPI> getSubject() {
		return annotation;
	}
}
