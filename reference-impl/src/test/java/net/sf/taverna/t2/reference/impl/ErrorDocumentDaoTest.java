package net.sf.taverna.t2.reference.impl;

import static org.junit.Assert.*;
import net.sf.taverna.t2.reference.ErrorDocumentDao;
import net.sf.taverna.t2.reference.ReferenceSetDao;
import net.sf.taverna.t2.reference.T2ReferenceType;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ErrorDocumentDaoTest {
	
	private ApplicationContext context;

	@Before
	public void setup() throws Exception {
		context = new ClassPathXmlApplicationContext(
				"vanillaHibernateAppContext.xml");
	}
	
	@Test
	public void store() throws Exception {
		ErrorDocumentDao dao = (ErrorDocumentDao) context.getBean("testErrorDao");
		ErrorDocumentImpl doc = new ErrorDocumentImpl();
		T2ReferenceImpl id = new T2ReferenceImpl();
		id.setReferenceType(T2ReferenceType.ErrorDocument);
		id.setDepth(0);
		id.setContainsErrors(true);
		id.setNamespacePart("testNamespace");		
		id.setLocalPart("testLocal");
		
		doc.setExceptionMessage("An exception");		
		
		T2ReferenceImpl typedId = T2ReferenceImpl.getAsImpl(id);
		
		doc.setTypedId(typedId);
		
		dao.store(doc);
		assertNotNull(dao.get(doc.getId()));	
	}
	
	@Test
	public void delete() throws Exception {
		ErrorDocumentDao dao = (ErrorDocumentDao) context.getBean("testErrorDao");
		ErrorDocumentImpl doc = new ErrorDocumentImpl();
		T2ReferenceImpl id = new T2ReferenceImpl();
		id.setReferenceType(T2ReferenceType.ErrorDocument);
		id.setDepth(0);
		id.setContainsErrors(true);
		id.setNamespacePart("testNamespace");		
		id.setLocalPart("testLocal");
		
		doc.setExceptionMessage("An exception");		
		
		T2ReferenceImpl typedId = T2ReferenceImpl.getAsImpl(id);
		
		doc.setTypedId(typedId);
		
		dao.store(doc);
		assertNotNull(dao.get(doc.getId()));
		
		assertTrue(dao.delete(doc));		
		assertNull(dao.get(id));	
	}

}
