package net.sf.taverna.t2.reference.impl;

import java.util.HashSet;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceSetDao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ReferenceSetDaoTest {

	private ApplicationContext context;

	@Before
	public void setup() throws Exception {
		context = new ClassPathXmlApplicationContext(
				"vanillaHibernateAppContext.xml");
	}

	@Test
	public void testStore() throws Exception {
		ReferenceSetDao dao = (ReferenceSetDao) context.getBean("testDao");
		T2ReferenceImpl id = new T2ReferenceImpl();
		id.setNamespacePart("testNamespace");		
		id.setLocalPart("testLocal");
		ReferenceSetImpl rs = new ReferenceSetImpl(
				new HashSet<ExternalReferenceSPI>(), id);
		dao.store(rs);
		Assert.assertNotNull(dao.get(id));
	}
	
	@Test
	public void testDelete() throws Exception {
		ReferenceSetDao dao = (ReferenceSetDao) context.getBean("testDao");
		T2ReferenceImpl id = new T2ReferenceImpl();
		id.setNamespacePart("testNamespace");
		id.setLocalPart("testLocal");
		ReferenceSetImpl rs = new ReferenceSetImpl(
				new HashSet<ExternalReferenceSPI>(), id);		
		dao.store(rs);
		dao.delete(rs);
		Assert.assertNull(dao.get(id));
	}
}
