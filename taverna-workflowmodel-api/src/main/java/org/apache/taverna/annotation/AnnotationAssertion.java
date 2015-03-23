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

/**
 * Represents a single assertion of information, providing access to a bean
 * containing the information in the assertion and one specifying the source of
 * the information contained.
 * 
 * @author Tom Oinn
 * 
 */
public interface AnnotationAssertion<AnnotationBeanType extends AnnotationBeanSPI>
		extends Curateable {

	/**
	 * Each annotation assertion contains a bean specifying the actual
	 * annotation, varying from a simple string for a free text description to
	 * more sophisticated semantic annotations or controlled vocabularies.
	 * 
	 * @return the annotation bean specifying this annotation assertion
	 */
	public AnnotationBeanType getDetail();

	/**
	 * The annotation assertion plays one of several roles within the annotation
	 * chain, either an initial assertion, a refinement of a previous assertion
	 * or a replacement of a previous assertion.
	 * 
	 * @return the annotation role of this annotation
	 */
	public AnnotationRole getRole();

}
