package net.sf.taverna.t2.reference.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ListDao;
import net.sf.taverna.t2.reference.T2Reference;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ListServiceTest {
	
	private ApplicationContext context;
	private ListServiceImpl service;

	@Before
	public void setup() throws Exception {
		context = new ClassPathXmlApplicationContext(
				"vanillaHibernateAppContext.xml");
		service = new ListServiceImpl();
		service.setListDao((ListDao)context.getBean("testListDao"));
		service.setT2ReferenceGenerator(new SimpleT2ReferenceGenerator());		
	}
	
	@Test
	public void delete() throws Exception {
		IdentifiedList<T2Reference> list =service.registerEmptyList(1);
		assertNotNull(service.getList(list.getId()));
		assertTrue(service.delete(list.getId()));
		assertNull(service.getList(list.getId()));
		assertFalse(service.delete(list.getId()));
	}

}
