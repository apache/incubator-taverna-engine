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

package org.apache.taverna.reference;

import java.util.Set;

/**
 * Callback interface used when augmenting a ReferenceSet in an asynchronous
 * fashion through
 * {@link ReferenceSetAugmentor#augmentReferenceSetAsynch(ReferenceSet, Set, ReferenceContext, ReferenceSetAugmentorCallback)
 * augmentReferenceSetAsynch} in {@link ReferenceSetAugmentor}.
 * 
 * @author Tom Oinn
 */
public interface ReferenceSetAugmentorCallback {
	/**
	 * Called when the augmentation has succeeded
	 * 
	 * @param newReferences
	 *            a set of ExternalReferenceSPI instances created during the
	 *            augmentation process. It is the responsibility of the caller
	 *            to re-integrate these back into the ReferenceSet used in the
	 *            translation
	 */
	void augmentationCompleted(Set<ExternalReferenceSPI> newReferences);

	/**
	 * Called when the augmentation has failed for some reason
	 * 
	 * @param cause
	 *            a {@link ReferenceSetAugmentationException} object describing
	 *            the failure.
	 */
	void augmentationFailed(ReferenceSetAugmentationException cause);
}
