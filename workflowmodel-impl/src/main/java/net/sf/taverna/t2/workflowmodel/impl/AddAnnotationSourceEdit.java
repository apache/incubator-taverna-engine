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
package net.sf.taverna.t2.workflowmodel.impl;

import net.sf.taverna.t2.annotation.AnnotationAssertion;
import net.sf.taverna.t2.annotation.AnnotationBeanSPI;
import net.sf.taverna.t2.annotation.AnnotationSourceSPI;
import net.sf.taverna.t2.annotation.impl.AnnotationAssertionImpl;
import net.sf.taverna.t2.workflowmodel.EditException;

class AddAnnotationSourceEdit extends
		EditSupport<AnnotationAssertion<AnnotationBeanSPI>> {
	private AnnotationAssertionImpl annotationAssertion;
	private AnnotationSourceSPI annotationSource;

	public AddAnnotationSourceEdit(AnnotationAssertion<?> annotationAssertion,
			AnnotationSourceSPI annotationSource) {
		if (!(annotationAssertion instanceof AnnotationAssertionImpl))
			throw new RuntimeException(
					"Object being edited must be instance of AnnotationAssertionImpl");
		this.annotationAssertion = (AnnotationAssertionImpl) annotationAssertion;
		this.annotationSource = annotationSource;
	}

	@Override
	public AnnotationAssertion<AnnotationBeanSPI> applyEdit()
			throws EditException {
		synchronized (annotationAssertion) {
			annotationAssertion.setAnnotationSource(annotationSource);
			return this.annotationAssertion;
		}
	}

	@Override
	public Object getSubject() {
		return annotationAssertion;
	}
}
