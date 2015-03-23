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

package org.apache.taverna.annotation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.annotation.AnnotationAssertion;
import org.apache.taverna.annotation.AnnotationChain;

public class AnnotationChainImpl implements AnnotationChain{
	private List<AnnotationAssertion<?>> annotationAssertions = new ArrayList<>();

	@Override
	public List<AnnotationAssertion<?>> getAssertions() {
		return new ArrayList<>(annotationAssertions);
	}
	
	/**
	 * Add an annotation to the chain Added because without the edits stuff how
	 * else can we do it?
	 * 
	 * @param annotationAssertion
	 */
	public void addAnnotationAssertion(AnnotationAssertion<?> annotationAssertion) {
		annotationAssertions.add(annotationAssertion);
	}
	
	public void removeAnnotationAssertion(AnnotationAssertion<?> annotationAssertion) {
		annotationAssertions.remove(annotationAssertion);
	}
}
