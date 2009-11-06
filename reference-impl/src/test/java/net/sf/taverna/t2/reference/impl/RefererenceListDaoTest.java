package net.sf.taverna.t2.reference.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;

import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ListDao;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RefererenceListDaoTest {
	
	private ApplicationContext context;

	@Before
	public void setup() throws Exception {
		context = new ClassPathXmlApplicationContext(
				"vanillaHibernateAppContext.xml");
	}
	
	@Test
	public void store() throws Exception {
		ListDao dao = (ListDao)context.getBean("testListDao");
		T2ReferenceImpl r = new T2ReferenceImpl();
		r.setNamespacePart("testNamespace");
		r.setLocalPart("testLocal");
		r.setReferenceType(T2ReferenceType.IdentifiedList);
		r.setDepth(0);
		r.setContainsErrors(false);
		T2ReferenceListImpl newList = new T2ReferenceListImpl();
		newList.setTypedId(r);
		dao.store(newList);
		assertNotNull(dao.get(r));		
	}
	
	@Test
	public void delete() throws Exception {
		ListDao dao = (ListDao)context.getBean("testListDao");
		T2ReferenceImpl r = new T2ReferenceImpl();
		r.setNamespacePart("testNamespace");
		r.setLocalPart("testLocal");
		r.setReferenceType(T2ReferenceType.IdentifiedList);
		r.setDepth(0);
		r.setContainsErrors(false);
		T2ReferenceListImpl newList = new T2ReferenceListImpl();
		newList.setTypedId(r);
		dao.store(newList);
		assertNotNull(dao.get(r));	
		assertTrue(dao.delete(newList));
		assertNull(dao.get(r));		
	}

}
