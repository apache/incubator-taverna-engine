package net.sf.taverna.t2.reference.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceSetDao;

import org.junit.Before;
import org.junit.Ignore;
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
	@Ignore
	public void testStore() throws Exception {
		ReferenceSetDao dao = (ReferenceSetDao) context.getBean("testDao");
		T2ReferenceImpl id = new T2ReferenceImpl();
		id.setNamespacePart("testNamespace");		
		id.setLocalPart("testLocal");
		ReferenceSetImpl rs = new ReferenceSetImpl(
				new HashSet<ExternalReferenceSPI>(), id);
		dao.store(rs);
		assertNotNull(dao.get(id));
	}
	
	@Test
	@Ignore
	public void testDelete() throws Exception {
		ReferenceSetDao dao = (ReferenceSetDao) context.getBean("testDao");
		T2ReferenceImpl id = new T2ReferenceImpl();
		id.setNamespacePart("testNamespace");
		id.setLocalPart("testLocal");
		ReferenceSetImpl rs = new ReferenceSetImpl(
				new HashSet<ExternalReferenceSPI>(), id);		
		dao.store(rs);
		assertNotNull(dao.get(id));
		assertTrue(dao.delete(rs));
		assertNull(dao.get(id));
	}
	
	/**
	 * Tests that .get returns null when its missing, rather than throw an exception
	 */
	@Test
	@Ignore
	public void getMissingItemReturnsNull() {
		ReferenceSetDao dao = (ReferenceSetDao) context.getBean("testDao");
		T2ReferenceImpl id = new T2ReferenceImpl();
		id.setNamespacePart("testNamespace");		
		id.setLocalPart("testLocal");
		ReferenceSetImpl rs = new ReferenceSetImpl(
				new HashSet<ExternalReferenceSPI>(), id);
		assertNull(dao.get(id));
		
	}
		
}
