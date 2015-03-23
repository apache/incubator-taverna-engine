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

package org.apache.taverna.workflowmodel.impl;

import org.apache.taverna.annotation.AnnotationAssertion;
import org.apache.taverna.annotation.AnnotationBeanSPI;
import org.apache.taverna.annotation.impl.AnnotationAssertionImpl;
import org.apache.taverna.workflowmodel.EditException;

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
