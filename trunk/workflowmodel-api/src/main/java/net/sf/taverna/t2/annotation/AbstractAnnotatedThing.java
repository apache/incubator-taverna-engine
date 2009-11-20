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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;

/**
 * Convenient abstract superclass for annotated things, manages edits.
 * Subclasses of this must implement the Annotated interface with their own
 * interface type as the parameter, so for example Processor subclasses would
 * implement Annotated&lt;Processor&gt;
 * 
 * @author Tom Oinn
 * @author Alan R Williams
 * 
 */
public abstract class AbstractAnnotatedThing<T> implements Annotated<T> {

	private Set<AnnotationChain> annotations = new HashSet<AnnotationChain>();

	/**
	 * Return the set of annotations bound to this annotated object, the set
	 * returned is an unmodifiable copy of the internal annotation set, if you
	 * need to modify the annotations you should use the get methods for Edit
	 * objects to do so.
	 * 
	 * @see net.sf.taverna.t2.annotation.Annotated#getAnnotations()
	 */
	public final Set<AnnotationChain> getAnnotations() {
		return Collections.unmodifiableSet(annotations);
	}

	/**
	 * Set the annotation chains associated with this annotated object. This is
	 * only needed for deserialization and could almost certainly be done in a
	 * better way.
	 * 
	 * @param annotations
	 */
	public final void setAnnotations(Set<AnnotationChain> annotations) {
		this.annotations = annotations;
	}

	/**
	 * Superclass of edits to remove, add and replace annotations on instances
	 * of the enclosing AbstractAnnotatedThing class
	 * 
	 * @author Tom
	 * 
	 * @param <TargetType>
	 */
	private static abstract class AbstractAnnotationEdit<TargetType> implements
			Edit<TargetType> {

		private AbstractAnnotatedThing<TargetType> subject;

		private boolean applied = false;

		protected AbstractAnnotationEdit(
				AbstractAnnotatedThing<TargetType> subject) {
			this.subject = subject;
		}

		@SuppressWarnings("unchecked")
		public final TargetType doEdit() throws EditException {
			synchronized (subject) {
				if (applied) {
					throw new EditException("Edit already applied!");
				}
				doEditAction(subject);
				this.applied = true;
				return (TargetType) subject;
			}
		}

		protected abstract void doEditAction(AbstractAnnotatedThing<?> subject)
				throws EditException;

		protected abstract void undoEditAction(AbstractAnnotatedThing<?> subject);

		@SuppressWarnings("unchecked")
		public final TargetType getSubject() {
			return (TargetType) subject;
		}

		public final boolean isApplied() {
			return this.applied;
		}

		public final void undo() {
			synchronized (subject) {
				if (!applied) {
					throw new RuntimeException(
							"Attempt to undo edit that was never applied");
				}
				undoEditAction(subject);
				applied = false;
			}
		}

	}

	/**
	 * @see net.sf.taverna.t2.annotation.Annotated#getAddAnnotationEdit(net.sf.taverna.t2.annotation.WorkflowAnnotation)
	 */
	public final Edit<T> getAddAnnotationEdit(
			final AnnotationChain newAnnotation) {
		return new AbstractAnnotationEdit<T>(this) {
			@Override
			protected void doEditAction(AbstractAnnotatedThing<?> subject)
					throws EditException {
				annotations.add(newAnnotation);
			}

			@Override
			protected void undoEditAction(AbstractAnnotatedThing<?> subject) {
				annotations.remove(newAnnotation);
			}
		};
	}

	/**
	 * @see net.sf.taverna.t2.annotation.Annotated#getRemoveAnnotationEdit(net.sf.taverna.t2.annotation.WorkflowAnnotation)
	 */
	public final Edit<T> getRemoveAnnotationEdit(
			final AnnotationChain annotationToRemove) {
		return new AbstractAnnotationEdit<T>(this) {
			@Override
			protected void doEditAction(AbstractAnnotatedThing<?> subject)
					throws EditException {
				annotations.remove(annotationToRemove);
			}

			@Override
			protected void undoEditAction(AbstractAnnotatedThing<?> subject) {
				annotations.add(annotationToRemove);
			}
		};
	}

}
