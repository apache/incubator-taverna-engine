package net.sf.taverna.t2.reference.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ErrorDocumentDao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ErrorDocumentServiceTest {
	
	private ApplicationContext context;
	private ErrorDocumentServiceImpl service;

	@Before
	public void setup() throws Exception {
		context = new ClassPathXmlApplicationContext(
				"vanillaHibernateAppContext.xml");
		service = new ErrorDocumentServiceImpl();
		service.setErrorDao((ErrorDocumentDao)context.getBean("testErrorDao"));
		service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());
		
	}
	
	@Test
	public void delete() throws Exception {
		ErrorDocument doc = service.registerError("Fred", 0);
		assertNotNull(service.getError(doc.getId()));
		assertTrue(service.delete(doc.getId()));
		assertNull(service.getError(doc.getId()));
		assertFalse(service.delete(doc.getId()));
	}

}
