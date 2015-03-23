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

import java.util.HashSet;

import org.apache.taverna.reference.ExternalReferenceSPI;
import org.apache.taverna.reference.ListDao;
import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.reference.ReferenceSetDao;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.T2ReferenceType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Tests initialization of the Derby database and Hibernate ORM system
 * 
 * @author Tom Oinn
 */
public class DatabaseSetupTest {

	@Before
	public void setup() throws Exception {
		AppContextSetup.setup();
	}
	
	
	@Test
	public void testListStorage() {
			ApplicationContext context = AppContextSetup.contextList.get(0); // Hibernate context
			ListDao o = (ListDao) context.getBean("testListDao");
			T2ReferenceImpl listReference = new T2ReferenceImpl();
			listReference.setContainsErrors(false);
			listReference.setDepth(1);
			listReference.setLocalPart("list1");
			listReference.setNamespacePart("testNamespace");
			listReference.setReferenceType(T2ReferenceType.IdentifiedList);

			T2ReferenceListImpl l = new T2ReferenceListImpl();

			T2ReferenceImpl itemId1 = new T2ReferenceImpl();
			itemId1.setNamespacePart("testNamespace");
			itemId1.setLocalPart("item1");
			T2ReferenceImpl itemId2 = new T2ReferenceImpl();
			itemId2.setNamespacePart("testNamespace");
			itemId2.setLocalPart("item2");

			l.add(itemId1);
			l.add(itemId2);

			l.setTypedId(listReference);

			System.out.println(l);

			o.store(l);

			T2ReferenceImpl listReference2 = new T2ReferenceImpl();
			listReference2.setContainsErrors(false);
			listReference2.setDepth(1);
			listReference2.setLocalPart("list1");
			listReference2.setNamespacePart("testNamespace");
			listReference2.setReferenceType(T2ReferenceType.IdentifiedList);

			System.out.println(o.get(listReference2));

	}

	@SuppressWarnings("serial")
	@Test
	public void testDatabaseReadWriteWithoutPlugins() {
			ApplicationContext context = AppContextSetup.contextList.get(0); // Hibernate context
			ReferenceSetDao o = (ReferenceSetDao) context
					.getBean("testDao");
			T2ReferenceImpl id = new T2ReferenceImpl();
			id.setNamespacePart("testNamespace");
			id.setLocalPart("testLocal");
			ReferenceSetImpl rs = new ReferenceSetImpl(
					new HashSet<ExternalReferenceSPI>(), id);
			o.store(rs);
			
			
			// Retrieve with a new instance of an anonymous subclass of
			// ReferenceSetT2ReferenceImpl, just to check that hibernate can cope
			// with this. It can, but this *must* be a subclass of the registered
			// component type, which means we need to modify the component type to
			// be the fully generic T2Reference with all fields accessed via
			// properties.
			T2Reference newReference = new T2ReferenceImpl() {

				@Override
				public boolean containsErrors() {
					return false;
				}

				@Override
				public int getDepth() {
					return 0;
				}

				@Override
				public String getLocalPart() {
					return "testLocal";
				}

				@Override
				public String getNamespacePart() {
					return "testNamespace";
				}

			};

			
			ReferenceSet returnedset = o.get(newReference);
			Assert.assertNotNull(returnedset.getId());	

	}

}
