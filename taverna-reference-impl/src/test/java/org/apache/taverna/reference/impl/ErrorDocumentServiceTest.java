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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.reference.ErrorDocument;
import org.apache.taverna.reference.ErrorDocumentDao;
import org.apache.taverna.reference.WorkflowRunIdEntity;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class ErrorDocumentServiceTest {
	
	private List<ErrorDocumentServiceImpl> serviceList = new ArrayList<ErrorDocumentServiceImpl>();
	
	@Before
	public void setup() throws Exception {
		
		AppContextSetup.setup();
		
		ErrorDocumentServiceImpl service = null;

		for (ApplicationContext context : AppContextSetup.contextList) {
			service = new ErrorDocumentServiceImpl();
			service.setErrorDao((ErrorDocumentDao) context.getBean("testErrorDao"));
			service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());
			serviceList.add(service);	
		}
				
	}
	
	@Test
	public void testDelete() throws Exception {
		ReferenceContextImpl invocationContext = new ReferenceContextImpl();
		invocationContext.addEntity(new WorkflowRunIdEntity("wfRunErrorDocTest"));
		for (ErrorDocumentServiceImpl service : serviceList){
			ErrorDocument doc = service.registerError("Fred", 0, invocationContext);
			assertNotNull(service.getError(doc.getId()));
			assertTrue(service.delete(doc.getId()));
			assertNull(service.getError(doc.getId()));
			assertFalse(service.delete(doc.getId()));
		}
	}

	@Test
	public void testDeleteErrorDocumentsForWFRun() throws Exception {

		for (ErrorDocumentServiceImpl service : serviceList){
			
			String wfRunId1 = "wfRunErrorDocTest1";
			ReferenceContextImpl invocationContext1 = new ReferenceContextImpl();
			invocationContext1.addEntity(new WorkflowRunIdEntity(wfRunId1));
			
			String wfRunId2 = "wfRunErrorDocTest2";
			ReferenceContextImpl invocationContext2 = new ReferenceContextImpl();
			invocationContext2.addEntity(new WorkflowRunIdEntity(wfRunId2));
			
			ErrorDocument doc1 = service.registerError("Fred1", 0, invocationContext1);
			ErrorDocument doc2 = service.registerError("Fred2", 0, invocationContext1);
			ErrorDocument doc3 = service.registerError("Fred3", 0, invocationContext2);

			assertNotNull(service.getError(doc1.getId()));
			assertNotNull(service.getError(doc2.getId()));
			assertNotNull(service.getError(doc3.getId()));

			service.deleteErrorDocumentsForWorkflowRun(wfRunId1);
			
			assertNull(service.getError(doc1.getId()));
			assertNull(service.getError(doc2.getId()));
			assertNotNull(service.getError(doc3.getId()));

		}
	}
}
