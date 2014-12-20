package net.sf.taverna.t2.reference.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ListDao;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.WorkflowRunIdEntity;

import org.junit.Before;
import org.junit.Test;

public class ListServiceTest {
	
	private List<ListServiceImpl> serviceList = new ArrayList<ListServiceImpl>();

	@Before
	public void setup() throws Exception {
		
		AppContextSetup.setup();
		
		ListServiceImpl service = null;
		
			service = new ListServiceImpl();
			service.setListDao((ListDao)AppContextSetup.contextList.get(0).getBean("testListDao")); // hibernate
			service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());	
			serviceList.add(service);
		
			service = new ListServiceImpl();
			service.setListDao((ListDao)AppContextSetup.contextList.get(1).getBean("testListDao")); // in memory
			service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());	
			serviceList.add(service);

		
			service = new ListServiceImpl();
			service.setListDao((ListDao)AppContextSetup.contextList.get(2).getBean("testListDao")); // transactional hibernate
			service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());	
			serviceList.add(service);
		
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
