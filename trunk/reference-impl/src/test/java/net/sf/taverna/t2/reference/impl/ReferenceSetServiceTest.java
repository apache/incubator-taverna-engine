package net.sf.taverna.t2.reference.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferenceSetDao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ReferenceSetServiceTest {
	
	private ApplicationContext context;
	private ReferenceSetServiceImpl service;

	@Before
	public void setup() throws Exception {
		context = new ClassPathXmlApplicationContext(
				"vanillaHibernateAppContext.xml");
		service = new ReferenceSetServiceImpl();
		service.setReferenceSetDao((ReferenceSetDao)context.getBean("testDao"));
		service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());		
	}
	
	@Test
	public void delete() throws Exception {
		ReferenceSet set = service.registerReferenceSet(new HashSet());
		assertNotNull(service.getReferenceSet(set.getId()));
		assertTrue(service.delete(set.getId()));
		assertNull(service.getReferenceSet(set.getId()));
		assertFalse(service.delete(set.getId()));
	}

}
