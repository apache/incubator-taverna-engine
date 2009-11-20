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
import net.sf.taverna.t2.annotation.AnnotationAssertionImpl;
import net.sf.taverna.t2.annotation.AnnotationBeanSPI;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;

@SuppressWarnings("unchecked")
public class AddAnnotationBeanEdit implements Edit<AnnotationAssertion> {

	private AnnotationAssertion annotationAssertion;
	private AnnotationBeanSPI annotationBean;
	private boolean applied;

	public AddAnnotationBeanEdit(AnnotationAssertion annotationAssertion,
			AnnotationBeanSPI annotationBean) {
				this.annotationAssertion = annotationAssertion;
				this.annotationBean = annotationBean;
	}

	public AnnotationAssertion doEdit() throws EditException {
			if (applied) {
				throw new EditException("Edit has already been applied");
			}
			if (!(annotationAssertion instanceof AnnotationAssertionImpl)) {
				throw new EditException(
						"Object being edited must be instance of AnnotationAssertionImpl");
			}

			try {
				synchronized (annotationAssertion) {
					((AnnotationAssertionImpl) annotationAssertion).setAnnotationBean(annotationBean);
					applied = true;
					return this.annotationAssertion;
				}
			} catch (Exception e) {
				applied = false;
				throw new EditException("There was a problem with the edit", e);
			}
	}

	public Object getSubject() {
		return annotationAssertion;
	}

	public boolean isApplied() {
		return applied;
	}

	public void undo() {
		if (!applied) {
			throw new RuntimeException(
					"Attempt to undo edit that was never applied");
		}
		((AnnotationAssertionImpl) annotationAssertion).removeAnnotationBean();
		applied = false;
	}

}
