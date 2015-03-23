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
 * Specifies the role of an AnnotationAssertion within an AnnotationChain
 * 
 * @author Tom Oinn
 */
public enum AnnotationRole {

	/**
	 * The information assertion is the first in the chain (if this is applied
	 * to an annotation that isn't the earliest in its chain it should be
	 * treated as a validation failure)
	 */
	INITIAL_ASSERTION,

	/**
	 * The information assertion was added to the chain to refine the existing
	 * annotation assertion or assertions, such as cases where a generic
	 * description exists which can be specialized in a particular instance but
	 * where the original more generic form is still correct
	 */
	REFINEMENT,

	/**
	 * The information assertion was added to the chain in order to override an
	 * earlier information assertion which was regarded as incorrect.
	 */
	REPLACEMENT;

}
