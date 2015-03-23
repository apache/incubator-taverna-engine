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

package org.apache.taverna.workflowmodel.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.annotation.Annotated;
import org.apache.taverna.annotation.AnnotationAssertion;
import org.apache.taverna.annotation.AnnotationBeanSPI;
import org.apache.taverna.annotation.AnnotationChain;
import org.apache.taverna.annotation.AppliesTo;
import org.apache.taverna.annotation.annotationbeans.AbstractTextualValueAssertion;
import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;

import org.apache.log4j.Logger;

public class AnnotationTools {
	private static Logger logger = Logger.getLogger(AnnotationTools.class);
	// private Iterable<Class<?>> annotationBeanRegistry;

	public static Edit<?> addAnnotation(Annotated<?> annotated,
			AnnotationBeanSPI a, Edits edits) {
		return edits.getAddAnnotationChainEdit(annotated, a);
	}

	public static AnnotationBeanSPI getAnnotation(Annotated<?> annotated,
			Class<?> annotationClass) {
		AnnotationBeanSPI result = null;
		Date latestDate = null;
		for (AnnotationChain chain : annotated.getAnnotations())
			for (AnnotationAssertion<?> assertion : chain.getAssertions()) {
				AnnotationBeanSPI detail = assertion.getDetail();
				if (annotationClass.isInstance(detail)) {
					Date assertionDate = assertion.getCreationDate();
					if ((latestDate == null)
							|| latestDate.before(assertionDate)) {
						result = detail;
						latestDate = assertionDate;
					}
				}
			}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> List<Class<? extends T>> getAnnotationBeanClasses(
			List<AnnotationBeanSPI> annotations, Class<T> superClass) {
		List<Class<? extends T>> results = new ArrayList<>();
		for (AnnotationBeanSPI annotation : annotations) {
			Class<? extends AnnotationBeanSPI> annotationBeanClass = annotation
					.getClass();
			if (superClass.isAssignableFrom(annotationBeanClass))
				results.add((Class<? extends T>) annotationBeanClass);
		}
		return results;
	}

	public List<Class<?>> getAnnotatingClasses(
			List<AnnotationBeanSPI> annotations, Annotated<?> annotated) {
		List<Class<?>> result = new ArrayList<>();
		for (Class<? extends AbstractTextualValueAssertion> c : getAnnotationBeanClasses(
				annotations, AbstractTextualValueAssertion.class)) {
			AppliesTo appliesToAnnotation = (AppliesTo) c
					.getAnnotation(AppliesTo.class);
			if (appliesToAnnotation == null)
				continue;
			for (Class<?> target : appliesToAnnotation.targetObjectType())
				if (target.isInstance(annotated))
					result.add(c);
		}
		return result;
	}

	public static Edit<?> setAnnotationString(Annotated<?> annotated,
			Class<?> c, String value, Edits edits) {
		AbstractTextualValueAssertion a = null;
		try {
			logger.info("Setting " + c.getCanonicalName() + " to " + value);
			a = (AbstractTextualValueAssertion) c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
		a.setText(value);
		return addAnnotation(annotated, a, edits);
	}

	public static String getAnnotationString(Annotated<?> annotated,
			Class<?> annotationClass, String missingValue) {
		AbstractTextualValueAssertion a = (AbstractTextualValueAssertion) getAnnotation(
				annotated, annotationClass);
		if (a == null)
			return missingValue;
		return a.getText();
	}

	/**
	 * Remove out of date annotations unless many of that class are allowed, or
	 * it is explicitly not pruned
	 */
	@SuppressWarnings("rawtypes")
	public static void pruneAnnotations(Annotated<?> annotated, Edits edits) {
		Map<Class<? extends AnnotationBeanSPI>, AnnotationAssertion> remainder = new HashMap<>();
		Set<AnnotationChain> newChains = new HashSet<AnnotationChain>();
		for (AnnotationChain chain : annotated.getAnnotations()) {
			AnnotationChain newChain = edits.createAnnotationChain();
			for (AnnotationAssertion assertion : chain.getAssertions()) {
				AnnotationBeanSPI annotation = assertion.getDetail();
				Class<? extends AnnotationBeanSPI> annotationClass = annotation
						.getClass();
				AppliesTo appliesToAnnotation = (AppliesTo) annotationClass
						.getAnnotation(AppliesTo.class);
				if ((appliesToAnnotation == null) || appliesToAnnotation.many()
						|| !appliesToAnnotation.pruned())
					try {
						edits.getAddAnnotationAssertionEdit(newChain, assertion)
								.doEdit();
					} catch (EditException e) {
						logger.error("Error while pruning annotations", e);
					}
				else if (remainder.containsKey(annotationClass)) {
					AnnotationAssertion currentAssertion = remainder
							.get(annotationClass);
					if (assertion.getCreationDate().compareTo(
							currentAssertion.getCreationDate()) > 0)
						remainder.put(annotationClass, assertion);
				} else
					remainder.put(annotationClass, assertion);
			}
			if (!newChain.getAssertions().isEmpty())
				newChains.add(newChain);
		}
		for (AnnotationAssertion assertion : remainder.values()) {
			AnnotationChain newChain = edits.createAnnotationChain();
			try {
				edits.getAddAnnotationAssertionEdit(newChain, assertion)
						.doEdit();
			} catch (EditException e) {
				logger.error("Error while pruning annotations", e);
			}
			newChains.add(newChain);
		}
		annotated.setAnnotations(newChains);
	}
}
