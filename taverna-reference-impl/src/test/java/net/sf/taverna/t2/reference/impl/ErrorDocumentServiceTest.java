package net.sf.taverna.t2.reference.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ErrorDocumentDao;
import net.sf.taverna.t2.reference.WorkflowRunIdEntity;

import org.junit.Before;
import org.junit.Test;

public class ErrorDocumentServiceTest {
	
	private List<ErrorDocumentServiceImpl> serviceList = new ArrayList<ErrorDocumentServiceImpl>();
	
	@Before
	public void setup() throws Exception {
		
		AppContextSetup.setup();
		
		ErrorDocumentServiceImpl service = null;

			service = new ErrorDocumentServiceImpl();
			service.setErrorDao((ErrorDocumentDao)AppContextSetup.contextList.get(0).getBean("testErrorDao")); // hiberate
			service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());
			serviceList.add(service);	
		
			service = new ErrorDocumentServiceImpl();
			service.setErrorDao((ErrorDocumentDao)AppContextSetup.contextList.get(1).getBean("testErrorDao")); // in memory
			service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());
			serviceList.add(service);
		
			service = new ErrorDocumentServiceImpl();
			service.setErrorDao((ErrorDocumentDao)AppContextSetup.contextList.get(2).getBean("testErrorDao")); // transactional hibernate
			service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());
			serviceList.add(service);
				
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
