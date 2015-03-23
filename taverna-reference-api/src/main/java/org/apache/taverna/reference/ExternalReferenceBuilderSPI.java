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

import java.io.InputStream;

/**
 * Constructs an ExternalReferenceSPI instance from a byte stream. Used by the
 * {@link ReferenceSetAugmentor} when there isn't a direct reference to
 * reference translation path available for a desired target type, but available
 * for external use wherever this functionality is needed.
 * <p>
 * Where an underlying resource is required this is extracted from the supplied
 * ReferenceContext, this implies that all methods in implementations of this
 * interface should be thread safe, allowing multiple concurrent threads
 * cleanly. For SPI purposes implementations should be java beans with default
 * constructors.
 * 
 * @author Tom Oinn
 */
public interface ExternalReferenceBuilderSPI<TargetType extends ExternalReferenceSPI> {
	/**
	 * Given a stream of bytes, build the appropriate target
	 * ExternalReferenceSPI implementation which would de-reference to the value
	 * of that stream and return it.
	 * 
	 * @param byteStream
	 *            the bytestream to read target from.
	 * @param context
	 *            a reference resolution context, needed potentially to
	 *            construct the new ExternalReferenceSchemeSPI, especially in
	 *            cases where the context contains security agents giving access
	 *            to a remote data staging system *
	 * @throws ExternalReferenceConstructionException
	 *             if an error occurs instantiating the new reference.
	 * @return the newly constructed ExternalReferenceSPI instance.
	 */
	TargetType createReference(InputStream byteStream, ReferenceContext context);

	/**
	 * Expose the type of the ExternalReferenceSPI that this builder can
	 * construct
	 * 
	 * @return the class of ExternalReferenceSPI returned by the create
	 *         reference methods.
	 */
	Class<TargetType> getReferenceType();

	/**
	 * Because the reference builder may rely on facilities provided to it
	 * through the context this method is available to check whether these
	 * facilities are sufficient.
	 * 
	 * @param context
	 *            the reference context that will be used to construct new
	 *            references
	 * @return whether the context contains necessary resources for the
	 *         reference construction process
	 */
	boolean isEnabled(ReferenceContext context);

	/**
	 * Return an approximate complexity cost of the reference construction. In
	 * general we can't make any guarantees about this because the complexity of
	 * the construction depends on more than just the type involved - it can
	 * depend on local configuration, network location relative to the data
	 * stores referenced and in some cases on the data themselves. For now
	 * though we assign an approximation, the default value is 1.0f and lower
	 * values represent less costly operations.
	 */
	float getConstructionCost();
}
