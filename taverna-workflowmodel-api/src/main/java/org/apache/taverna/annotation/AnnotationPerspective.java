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

import java.util.List;

/**
 * Responsible for the interpretation of an AnnotationChain (which may contain
 * conflicting or disputed information) into a set of AnnotationAssertion
 * instances from that chain which are valid given the chain and some
 * interpretation rule.
 * 
 * @author Tom Oinn
 * 
 */
public interface AnnotationPerspective {

	/**
	 * Evaluate the annotations and their curation events in the specified
	 * chain, resolve conflicts if possible and return the resultant set of
	 * annotations
	 * 
	 * @param chain
	 *            the annotation chain to evaluate
	 * @return the set of annotations which are valid within this chain
	 */
	public List<? extends AnnotationAssertion<?>> getAnnotations(
			AnnotationChain chain);

	/**
	 * Annotation chains may be in a disputed state if there are conflicting
	 * mutually exclusive events within them under the interpretation imposed by
	 * the annotation perspective and the perspective is unable to sensibly
	 * reconcile them. For example, if the perspective is configured to trust
	 * two parties equally and they disagree.
	 * 
	 * @param chain
	 *            the annotation chain to check for conflict
	 * @return true if there are conflicts under the interpretation of this
	 *         annotation perspective
	 */
	public boolean isDisputed(AnnotationChain chain);

}
