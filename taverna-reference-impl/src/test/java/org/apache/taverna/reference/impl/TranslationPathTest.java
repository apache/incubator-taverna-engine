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

package org.apache.taverna.reference.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.t2referencetest.DummyReferenceSet;
import org.apache.taverna.t2referencetest.GreenBuilder;
import org.apache.taverna.t2referencetest.GreenReference;
import org.apache.taverna.t2referencetest.GreenToRed;
import org.apache.taverna.t2referencetest.RedReference;

import org.junit.Test;

public class TranslationPathTest {

	protected TranslationPath path  = new TranslationPath();
	
	@Test
	public void doTranslationWithTranslator() throws Exception {
		ReferenceContext context = new EmptyReferenceContext();
		ReferenceSet rs = new DummyReferenceSet(new GreenReference("green"));		
		path.getTranslators().add(new GreenToRed());
		Set<ExternalReferenceSPI> set = path.doTranslation(rs, context);		
		assertEquals(1, set.size());
		assertTrue(set.iterator().next() instanceof RedReference);
	}
	
	@Test
	public void doTranslationByReadingStream() throws Exception {
		ReferenceContext context = new EmptyReferenceContext();
		path.setSourceReference(new RedReference("red"));
		ReferenceSet rs = new DummyReferenceSet(path.getSourceReference());
		path.setInitialBuilder(new GreenBuilder());
		//augmentor.path.translators.add(new DummyTranslator());
		Set<ExternalReferenceSPI> set = path.doTranslation(rs, context);		
		assertEquals(1, set.size());
		assertTrue(set.iterator().next() instanceof GreenReference);
	}

	
}
