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

import org.apache.taverna.reference.IdentifiedList;
import org.apache.taverna.reference.ListDao;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.WorkflowRunIdEntity;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class ListServiceTest {
	
	private List<ListServiceImpl> serviceList = new ArrayList<ListServiceImpl>();

	@Before
	public void setup() throws Exception {
		
		AppContextSetup.setup();
		
		ListServiceImpl service = null;
		
		for (ApplicationContext context : AppContextSetup.contextList) {
		
			service = new ListServiceImpl();
			service.setListDao((ListDao)context.getBean("testListDao")); 
			service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());	
			serviceList.add(service);
		}
	}
	
	@Test
	public void testDelete() throws Exception {
		ReferenceContextImpl invocationContext = new ReferenceContextImpl();
		invocationContext.addEntity(new WorkflowRunIdEntity("wfRunListsTest"));
		for (ListServiceImpl service : serviceList){
			IdentifiedList<T2Reference> list =service.registerEmptyList(1, invocationContext);
			assertNotNull(service.getList(list.getId()));
			assertTrue(service.delete(list.getId()));
			assertNull(service.getList(list.getId()));
			assertFalse(service.delete(list.getId()));
		}
	}
	
	@Test
	public void testDeleteIdentifiedListsForWFRun() throws Exception {

		for (ListServiceImpl service : serviceList){
			
			String wfRunId1 = "wfRunListsTest1";
			ReferenceContextImpl invocationContext1 = new ReferenceContextImpl();
			invocationContext1.addEntity(new WorkflowRunIdEntity(wfRunId1));
			
			String wfRunId2 = "wfRunListsTest2";
			ReferenceContextImpl invocationContext2 = new ReferenceContextImpl();
			invocationContext2.addEntity(new WorkflowRunIdEntity(wfRunId2));
			
			IdentifiedList<T2Reference> list1 = service.registerEmptyList(2, invocationContext1);
			IdentifiedList<T2Reference> list2 = service.registerEmptyList(1, invocationContext1);
			IdentifiedList<T2Reference> list3 = service.registerEmptyList(1, invocationContext2);

			assertNotNull(service.getList(list1.getId()));
			assertNotNull(service.getList(list2.getId()));
			assertNotNull(service.getList(list3.getId()));

			service.deleteIdentifiedListsForWorkflowRun(wfRunId1);
			
			assertNull(service.getList(list1.getId()));
			assertNull(service.getList(list2.getId()));
			assertNotNull(service.getList(list3.getId()));

		}
	}

}
