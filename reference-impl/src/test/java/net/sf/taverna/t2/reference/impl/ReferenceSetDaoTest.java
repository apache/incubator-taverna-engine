package net.sf.taverna.t2.reference.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ReferenceSetDao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class ReferenceSetDaoTest {

	@Before
	public void setup() throws Exception {

		AppContextSetup.setup();
	}

	@Test
	public void testStore() throws Exception {
		for (ApplicationContext context : AppContextSetup.contextList){
			ReferenceSetDao dao = (ReferenceSetDao) context.getBean("testDao");
			T2ReferenceImpl id = new T2ReferenceImpl();
			id.setNamespacePart("testNamespace0");		
			id.setLocalPart("testLocal0");
			ReferenceSetImpl rs = new ReferenceSetImpl(
					new HashSet<ExternalReferenceSPI>(), id);
			dao.store(rs);
			assertNotNull(dao.get(id));
		}
	}
	
	@Test
	public void testDelete() throws Exception {
		for (ApplicationContext context : AppContextSetup.contextList){
			ReferenceSetDao dao = (ReferenceSetDao) context.getBean("testDao");
			T2ReferenceImpl id = new T2ReferenceImpl();
			id.setNamespacePart("testNamespace1");
			id.setLocalPart("testLocal1");
			ReferenceSetImpl rs = new ReferenceSetImpl(
					new HashSet<ExternalReferenceSPI>(), id);		
			dao.store(rs);
			assertNotNull(dao.get(id));
			assertTrue(dao.delete(rs));
			assertNull(dao.get(id));
		}
	}
	
	@Test
	public void testDeleteRerefenceSetsForWFRun() throws Exception {
		for (ApplicationContext context : AppContextSetup.contextList){
			ReferenceSetDao dao = (ReferenceSetDao) context.getBean("testDao");
			
			T2ReferenceImpl id1 = new T2ReferenceImpl();
			id1.setNamespacePart("wfRunRefSetTest1");
			id1.setLocalPart("testLocal1");
			ReferenceSetImpl rs1 = new ReferenceSetImpl(
					new HashSet<ExternalReferenceSPI>(), id1);		
			dao.store(rs1);
			assertNotNull(dao.get(id1));
			
			T2ReferenceImpl id2 = new T2ReferenceImpl();
			id2.setNamespacePart("wfRunRefSetTest1");
			id2.setLocalPart("testLocal2");
			ReferenceSetImpl rs2 = new ReferenceSetImpl(
					new HashSet<ExternalReferenceSPI>(), id2);		
			dao.store(rs2);
			assertNotNull(dao.get(id2));
			
			T2ReferenceImpl id3 = new T2ReferenceImpl();
			id3.setNamespacePart("wfRunRefSetTest2");
			id3.setLocalPart("testLocal3");
			ReferenceSetImpl rs3 = new ReferenceSetImpl(
					new HashSet<ExternalReferenceSPI>(), id3);		
			dao.store(rs3);
			assertNotNull(dao.get(id3));
			
			dao.deleteReferenceSetsForWFRun("wfRunRefSetTest1");
			
			assertNull(dao.get(id1));			
			assertNull(dao.get(id2));
			assertNotNull(dao.get(id3));	
		}
	}
	
	/**
	 * Tests that .get returns null when its missing, rather than throw an exception
	 */
	@Test
	public void getMissingItemReturnsNull() {
		for (ApplicationContext context : AppContextSetup.contextList){
			ReferenceSetDao dao = (ReferenceSetDao) context.getBean("testDao");
			T2ReferenceImpl id = new T2ReferenceImpl();
			id.setNamespacePart("testNamespace2");		
			id.setLocalPart("testLocal2");
			assertNull(dao.get(id));
		}
	}
		
}
