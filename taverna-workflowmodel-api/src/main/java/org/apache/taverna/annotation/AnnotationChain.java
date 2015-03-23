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
 * A fact about an annotated entity is expressed in terms of an annotation
 * chain. The annotation chain contains one or more information assertions in a
 * list ordered by the creation date of each assertion. Annotation chains are
 * then interpreted by an AnnotationPerspective which is responsible for
 * reasoning over the information in the chain and extracting the set of
 * information assertions that are valid according to the rules in the
 * particular AnnotationPerspective.
 * 
 * @author Tom Oinn
 * 
 */
public interface AnnotationChain {

	/**
	 * Returns the ordered list of AnnotationAssertions. This is the 'raw' set
	 * of annotations in creation order - this order is not necessarily the
	 * order they were curated, and may include refuted or otherwise wrong
	 * annotations. Consumers of this API are recommended to use an
	 * AnnotationPerspective to resolve any such conflicts appropriately.
	 * 
	 * @return read only copy of the ordered list of AnnotationAssertion
	 *         instances
	 */
	List<AnnotationAssertion<?>> getAssertions();

}
