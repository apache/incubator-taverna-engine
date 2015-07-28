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
import java.util.HashSet;
import java.util.List;

import org.apache.taverna.reference.ReferenceSet;
import org.apache.taverna.reference.ReferenceSetDao;
import org.apache.taverna.reference.WorkflowRunIdEntity;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class ReferenceSetServiceTest {
	
	private List<ReferenceSetServiceImpl> serviceList = new ArrayList<ReferenceSetServiceImpl>();

	@Before
	public void setup() throws Exception {

		AppContextSetup.setup();

		ReferenceSetServiceImpl service = null;
		
		for (ApplicationContext context : AppContextSetup.contextList) {

			service = new ReferenceSetServiceImpl();
			service.setReferenceSetDao((ReferenceSetDao)context.getBean("testDao"));
			service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());	
			serviceList.add(service);
		}
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testDelete() throws Exception {
		ReferenceContextImpl invocationContext = new ReferenceContextImpl();
		invocationContext.addEntity(new WorkflowRunIdEntity("wfRunRefSetTest0"));
		for (ReferenceSetServiceImpl service : serviceList){
			ReferenceSet set = service.registerReferenceSet(new HashSet(), invocationContext);
			assertNotNull(service.getReferenceSet(set.getId()));
			assertTrue(service.delete(set.getId()));
			assertNull(service.getReferenceSet(set.getId()));
			assertFalse(service.delete(set.getId()));
		}
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testDeleteReferenceSetsForWFRun() throws Exception {

		for (ReferenceSetServiceImpl service : serviceList){
			
			String wfRunId1 = "wfRunRefSetTest1";
			ReferenceContextImpl invocationContext1 = new ReferenceContextImpl();
			invocationContext1.addEntity(new WorkflowRunIdEntity(wfRunId1));
			
			String wfRunId2 = "wfRunRefSetTest2";
			ReferenceContextImpl invocationContext2 = new ReferenceContextImpl();
			invocationContext2.addEntity(new WorkflowRunIdEntity(wfRunId2));
			
			ReferenceSet set1 = service.registerReferenceSet(new HashSet(), invocationContext1);
			ReferenceSet set2 = service.registerReferenceSet(new HashSet(), invocationContext1);
			ReferenceSet set3 = service.registerReferenceSet(new HashSet(), invocationContext2);

			assertNotNull(service.getReferenceSet(set1.getId()));
			assertNotNull(service.getReferenceSet(set2.getId()));
			assertNotNull(service.getReferenceSet(set3.getId()));

			service.deleteReferenceSetsForWorkflowRun(wfRunId1);
			
			assertNull(service.getReferenceSet(set1.getId()));
			assertNull(service.getReferenceSet(set2.getId()));
			assertNotNull(service.getReferenceSet(set3.getId()));

		}
	}

}
