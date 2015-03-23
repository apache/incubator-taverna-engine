/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.annotation;

import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;

/**
 * Convenient abstract superclass for annotated things, manages edits.
 * Subclasses of this must implement the Annotated interface with their own
 * interface type as the parameter, so for example Processor subclasses would
 * implement Annotated&lt;Processor&gt;
 * 
 * @author Tom Oinn
 * @author Alan R Williams
 */
public abstract class AbstractAnnotatedThing<T> implements Annotated<T> {
	private Set<AnnotationChain> annotations = new HashSet<>();

	/**
	 * Return the set of annotations bound to this annotated object, the set
	 * returned is an unmodifiable copy of the internal annotation set, if you
	 * need to modify the annotations you should use the get methods for Edit
	 * objects to do so.
	 * 
	 * @see org.apache.taverna.annotation.Annotated#getAnnotations()
	 */
	@Override
	public final Set<AnnotationChain> getAnnotations() {
		return unmodifiableSet(annotations);
	}

	/**
	 * Set the annotation chains associated with this annotated object. This is
	 * only needed for deserialization and could almost certainly be done in a
	 * better way.
	 * 
	 * @param annotations
	 */
	@Override
	public final void setAnnotations(Set<AnnotationChain> annotations) {
		this.annotations = annotations;
	}

	/**
	 * Superclass of edits to remove, add and replace annotations on instances
	 * of the enclosing AbstractAnnotatedThing class
	 * 
	 * @author Tom
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

		@Override
		@SuppressWarnings("unchecked")
		public final TargetType doEdit() throws EditException {
			synchronized (subject) {
				if (applied)
					throw new EditException("Edit already applied!");
				doEditAction(subject);
				this.applied = true;
				return (TargetType) subject;
			}
		}

		protected abstract void doEditAction(AbstractAnnotatedThing<?> subject)
				throws EditException;

		protected abstract void undoEditAction(AbstractAnnotatedThing<?> subject);

		@Override
		@SuppressWarnings("unchecked")
		public final TargetType getSubject() {
			return (TargetType) subject;
		}

		@Override
		public final boolean isApplied() {
			return this.applied;
		}

		@Override
		public final void undo() {
			synchronized (subject) {
				if (!applied)
					throw new RuntimeException(
							"Attempt to undo edit that was never applied");
				undoEditAction(subject);
				applied = false;
			}
		}
	}

	/**
	 * @see net.sf.taverna.t2.annotation.Annotated#getAddAnnotationEdit(net.sf.taverna.t2.annotation.WorkflowAnnotation)
	 */
	@Override
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
	@Override
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
