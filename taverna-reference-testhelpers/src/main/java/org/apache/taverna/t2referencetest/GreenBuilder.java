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

package org.apache.taverna.t2referencetest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.taverna.reference.ExternalReferenceBuilderSPI;
import org.apache.taverna.reference.ExternalReferenceConstructionException;
import org.apache.taverna.reference.ReferenceContext;

import org.apache.log4j.Logger;

/**
 * Trivially build a GreenReference from an InputStream, implementing the
 * ExternalReferenceBuilderSPI interface. Used in the augmentation test cases.
 * 
 * @author Tom Oinn
 * 
 */
public class GreenBuilder implements
		ExternalReferenceBuilderSPI<GreenReference> {

	private static Logger logger = Logger
	.getLogger(GreenBuilder.class);

	/**
	 * Construct a new GreenReference from the given input stream, ignoring the
	 * otherwise helpful context as we don't need any resources from it. We
	 * assume UTF-8 encoding as that's what all the test reference types use,
	 * again, with a real example this might have to be a bit smarter!
	 * 
	 * @throws ExternalReferenceConstructionException
	 *             if there are any issues building the new GreenReference
	 *             (which there won't be)
	 */
	@Override
	public GreenReference createReference(InputStream is,
			ReferenceContext context)
			throws ExternalReferenceConstructionException {
		GreenReference newReference = new GreenReference();
		// Read input stream into the 'contents' property of the reference
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		try {
			newReference.setContents(in.readLine());
		} catch (IOException e) {
			throw new ExternalReferenceConstructionException(e);
		} finally {
			try {
				is.close();
				in.close();
			} catch (IOException e) {
				logger.error("Unable to close streams", e);
			}
		}
		return newReference;
	}

	/**
	 * Construction cost fixed at 1.5f
	 * 
	 * @return <code>1.5f</code>
	 */
	@Override
	public float getConstructionCost() {
		return 1.5f;
	}

	/**
	 * @return <code>{@link org.apache.taverna.t2referencetest.GreenReference GreenReference}.class</code>
	 */
	@Override
	public Class<GreenReference> getReferenceType() {
		return GreenReference.class;
	}

	/**
	 * Doesn't use any context resources so is always enabled
	 * 
	 * @return <code>true</code>
	 */
	@Override
	public boolean isEnabled(ReferenceContext arg0) {
		return true;
	}

}
