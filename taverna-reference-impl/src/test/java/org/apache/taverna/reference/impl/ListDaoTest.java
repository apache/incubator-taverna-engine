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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.taverna.reference.ListDao;
import org.apache.taverna.reference.T2ReferenceType;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class ListDaoTest {
	
	@Before
	public void setup() throws Exception {
		AppContextSetup.setup();
	}
	
	@Test
	public void testStore() throws Exception {
		for (ApplicationContext context : AppContextSetup.contextList){
			ListDao dao = (ListDao)context.getBean("testListDao");
			T2ReferenceImpl r = new T2ReferenceImpl();
			r.setNamespacePart("testNamespace0");
			r.setLocalPart("testLocal0");
			r.setReferenceType(T2ReferenceType.IdentifiedList);
			r.setDepth(0);
			r.setContainsErrors(false);
			T2ReferenceListImpl newList = new T2ReferenceListImpl();
			newList.setTypedId(r);
			dao.store(newList);
			assertNotNull(dao.get(r));	
		}	
	}
	
	/**
	 * Tests that .get returns null when its missing, rather than throw an exception
	 */
	@Test
	public void getMissingItemReturnsNull() {
		for (ApplicationContext context : AppContextSetup.contextList){
			ListDao dao = (ListDao)context.getBean("testListDao");
			T2ReferenceImpl r = new T2ReferenceImpl();
			r.setNamespacePart("testNamespace1");
			r.setLocalPart("testLocal1");
			r.setReferenceType(T2ReferenceType.IdentifiedList);
			r.setDepth(0);
			r.setContainsErrors(false);
			T2ReferenceListImpl newList = new T2ReferenceListImpl();
			newList.setTypedId(r);
			assertNull(dao.get(r));
		}
	}
	
	@Test
	public void testDelete() throws Exception {
		for (ApplicationContext context : AppContextSetup.contextList){
			ListDao dao = (ListDao)context.getBean("testListDao");
			T2ReferenceImpl r = new T2ReferenceImpl();
			r.setNamespacePart("testNamespace2");
			r.setLocalPart("testLocal2");
			r.setReferenceType(T2ReferenceType.IdentifiedList);
			r.setDepth(0);
			r.setContainsErrors(false);
			T2ReferenceListImpl newList = new T2ReferenceListImpl();
			newList.setTypedId(r);
			dao.store(newList);
			assertNotNull(dao.get(r));	
			assertTrue(dao.delete(newList));
			assertNull(dao.get(r));	
		}	
	}

	@Test
	public void testIdentifiedListsForWFRun() throws Exception {
		for (ApplicationContext context : AppContextSetup.contextList){
			ListDao dao = (ListDao)context.getBean("testListDao");
			
			T2ReferenceImpl r1 = new T2ReferenceImpl();
			r1.setReferenceType(T2ReferenceType.IdentifiedList);
			r1.setDepth(0);
			r1.setContainsErrors(true);
			r1.setNamespacePart("wfRunListsTest1");		
			r1.setLocalPart("testLocal1");		
			T2ReferenceListImpl list1 = new T2ReferenceListImpl();		
			list1.setTypedId(r1);	
			dao.store(list1);
			assertNotNull(dao.get(r1));
			
			T2ReferenceImpl r2 = new T2ReferenceImpl();
			r2.setReferenceType(T2ReferenceType.IdentifiedList);
			r2.setDepth(1);
			r2.setContainsErrors(true);
			r2.setNamespacePart("wfRunListsTest1");		
			r2.setLocalPart("testLocal2");		
			T2ReferenceListImpl list2 = new T2ReferenceListImpl();		
			list2.setTypedId(r2);	
			dao.store(list2);
			assertNotNull(dao.get(r2));
			
			T2ReferenceImpl r3 = new T2ReferenceImpl();
			r3.setReferenceType(T2ReferenceType.IdentifiedList);
			r3.setDepth(0);
			r3.setContainsErrors(true);
			r3.setNamespacePart("wfRunListsTest2");		
			r3.setLocalPart("testLocal3");		
			T2ReferenceListImpl list3 = new T2ReferenceListImpl();		
			list3.setTypedId(r3);	
			dao.store(list3);
			assertNotNull(dao.get(r3));
			
			dao.deleteIdentifiedListsForWFRun("wfRunListsTest1");
			
			assertNull(dao.get(r1));			
			assertNull(dao.get(r2));
			assertNotNull(dao.get(r3));	
		}
	}
}
