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

import org.apache.taverna.reference.ErrorDocumentDao;
import org.apache.taverna.reference.T2ReferenceType;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class ErrorDocumentDaoTest {
	
	@Before
	public void setup() throws Exception {
		AppContextSetup.setup();
	}
	
	@Test
	public void testStore() throws Exception {
		for (ApplicationContext context : AppContextSetup.contextList){
			
			ErrorDocumentDao dao = (ErrorDocumentDao) context.getBean("testErrorDao");
			ErrorDocumentImpl doc = new ErrorDocumentImpl();
			T2ReferenceImpl id = new T2ReferenceImpl();
			id.setReferenceType(T2ReferenceType.ErrorDocument);
			id.setDepth(0);
			id.setContainsErrors(true);
			id.setNamespacePart("testNamespace0");		
			id.setLocalPart("testLocal0");
			
			doc.setExceptionMessage("An exception");		
			
			T2ReferenceImpl typedId = T2ReferenceImpl.getAsImpl(id);
			
			doc.setTypedId(typedId);
			
			dao.store(doc);
			assertNotNull(dao.get(id));	
		}
	}
	
	/**
	 * Tests that .get returns null when its missing, rather than throw an exception
	 */
	@Test
	public void getMissingItemReturnsNull() {
		for (ApplicationContext context : AppContextSetup.contextList){
			ErrorDocumentDao dao = (ErrorDocumentDao) context.getBean("testErrorDao");
			ErrorDocumentImpl doc = new ErrorDocumentImpl();
			T2ReferenceImpl id = new T2ReferenceImpl();
			id.setReferenceType(T2ReferenceType.ErrorDocument);
			id.setDepth(0);
			id.setContainsErrors(true);
			id.setNamespacePart("testNamespace1");		
			id.setLocalPart("testLocal1");
			
			doc.setExceptionMessage("An exception");		
			
			T2ReferenceImpl typedId = T2ReferenceImpl.getAsImpl(id);
			
			doc.setTypedId(typedId);
			assertNull(dao.get(id));	
		}
	}
	
	@Test
	public void testDelete() throws Exception {
		for (ApplicationContext context : AppContextSetup.contextList){
			
			ErrorDocumentDao dao = (ErrorDocumentDao) context.getBean("testErrorDao");
			ErrorDocumentImpl doc = new ErrorDocumentImpl();
			T2ReferenceImpl id = new T2ReferenceImpl();
			id.setReferenceType(T2ReferenceType.ErrorDocument);
			id.setDepth(0);
			id.setContainsErrors(true);
			id.setNamespacePart("testNamespace2");		
			id.setLocalPart("testLocal2");
			
			doc.setExceptionMessage("An exception");		
			
			T2ReferenceImpl typedId = T2ReferenceImpl.getAsImpl(id);
			
			doc.setTypedId(typedId);
			
			dao.store(doc);
			assertNotNull(dao.get(id));
			
			assertTrue(dao.delete(doc));		
			assertNull(dao.get(id));	
		}

	}

	@Test
	public void testDeleteErrorDocumentsForWFRun() throws Exception {
		for (ApplicationContext context : AppContextSetup.contextList){
			
			ErrorDocumentDao dao = (ErrorDocumentDao) context.getBean("testErrorDao");
			
			ErrorDocumentImpl doc1 = new ErrorDocumentImpl();	
			T2ReferenceImpl id1 = new T2ReferenceImpl();
			id1.setReferenceType(T2ReferenceType.ErrorDocument);
			id1.setDepth(0);
			id1.setContainsErrors(true);
			id1.setNamespacePart("wfRunErrorDocTest1");		
			id1.setLocalPart("testLocal1");		
			doc1.setExceptionMessage("An exception 1");			
			T2ReferenceImpl typedId1 = T2ReferenceImpl.getAsImpl(id1);		
			doc1.setTypedId(typedId1);	
			dao.store(doc1);
			assertNotNull(dao.get(id1));
			
			ErrorDocumentImpl doc2 = new ErrorDocumentImpl();	
			T2ReferenceImpl id2 = new T2ReferenceImpl();
			id2.setReferenceType(T2ReferenceType.ErrorDocument);
			id2.setDepth(0);
			id2.setContainsErrors(true);
			id2.setNamespacePart("wfRunErrorDocTest1");		
			id2.setLocalPart("testLocal2");		
			doc2.setExceptionMessage("An exception 2");			
			T2ReferenceImpl typedId2 = T2ReferenceImpl.getAsImpl(id2);		
			doc2.setTypedId(typedId2);	
			dao.store(doc2);
			assertNotNull(dao.get(id2));
			
			ErrorDocumentImpl doc3 = new ErrorDocumentImpl();	
			T2ReferenceImpl id3 = new T2ReferenceImpl();
			id3.setReferenceType(T2ReferenceType.ErrorDocument);
			id3.setDepth(0);
			id3.setContainsErrors(true);
			id3.setNamespacePart("wfRunErrorDocTest2");		
			id3.setLocalPart("testLocal3");		
			doc3.setExceptionMessage("An exception 3");			
			T2ReferenceImpl typedId3 = T2ReferenceImpl.getAsImpl(id3);		
			doc3.setTypedId(typedId3);	
			dao.store(doc3);
			assertNotNull(dao.get(id3));
			
			dao.deleteErrorDocumentsForWFRun("wfRunErrorDocTest1");
			
			assertNull(dao.get(id1));			
			assertNull(dao.get(id2));
			assertNotNull(dao.get(id3));	
		}

	}

}
