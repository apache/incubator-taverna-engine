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

import org.apache.taverna.reference.ExternalReferenceTranslatorSPI;
import org.apache.taverna.reference.ReferenceContext;

public class GreenToRed implements
		ExternalReferenceTranslatorSPI<GreenReference, RedReference> {

	@Override
	public RedReference createReference(GreenReference ref,
			ReferenceContext context) {
		RedReference newReference = new RedReference();
		newReference.setContents(ref.getContents());
		// Insert a two second pause to simulate reference translation and to
		// test the behaviour of multiple concurrent translations
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ie) {
			System.out
					.println("Translation thread was interrupted, probably something wrong.");
		}
		return newReference;
	}

	@Override
	public Class<GreenReference> getSourceReferenceType() {
		return GreenReference.class;
	}

	@Override
	public Class<RedReference> getTargetReferenceType() {
		return RedReference.class;
	}

	@Override
	public float getTranslationCost() {
		return 0.4f;
	}

	@Override
	public boolean isEnabled(ReferenceContext arg0) {
		return true;
	}

}
