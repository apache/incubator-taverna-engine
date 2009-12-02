/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.reference.impl;

import java.util.HashSet;

import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.reference.ListDao;
import net.sf.taverna.t2.reference.ReferenceSet;
import net.sf.taverna.t2.reference.ReferenceSetDao;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.reference.T2ReferenceType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * Tests initialization of the Derby database and Hibernate ORM system
 * 
 * @author Tom Oinn
 */
public class DatabaseSetupTest {

	@Before
	public void setup() throws Exception {
		AppContextSetup.setup();
	}
	
	
	@Test
	public void testListStorage() {
			ApplicationContext context = AppContextSetup.contextList.get(0); // Hibernate context
			ListDao o = (ListDao) context.getBean("testListDao");
			T2ReferenceImpl listReference = new T2ReferenceImpl();
			listReference.setContainsErrors(false);
			listReference.setDepth(1);
			listReference.setLocalPart("list1");
			listReference.setNamespacePart("testNamespace");
			listReference.setReferenceType(T2ReferenceType.IdentifiedList);

			T2ReferenceListImpl l = new T2ReferenceListImpl();

			T2ReferenceImpl itemId1 = new T2ReferenceImpl();
			itemId1.setNamespacePart("testNamespace");
			itemId1.setLocalPart("item1");
			T2ReferenceImpl itemId2 = new T2ReferenceImpl();
			itemId2.setNamespacePart("testNamespace");
			itemId2.setLocalPart("item2");

			l.add(itemId1);
			l.add(itemId2);

			l.setTypedId(listReference);

			System.out.println(l);

			o.store(l);

			T2ReferenceImpl listReference2 = new T2ReferenceImpl();
			listReference2.setContainsErrors(false);
			listReference2.setDepth(1);
			listReference2.setLocalPart("list1");
			listReference2.setNamespacePart("testNamespace");
			listReference2.setReferenceType(T2ReferenceType.IdentifiedList);

			System.out.println(o.get(listReference2));

	}

	@SuppressWarnings("serial")
	@Test
	public void testDatabaseReadWriteWithoutPlugins() {
			ApplicationContext context = AppContextSetup.contextList.get(0); // Hibernate context
			ReferenceSetDao o = (ReferenceSetDao) context
					.getBean("testDao");
			T2ReferenceImpl id = new T2ReferenceImpl();
			id.setNamespacePart("testNamespace");
			id.setLocalPart("testLocal");
			ReferenceSetImpl rs = new ReferenceSetImpl(
					new HashSet<ExternalReferenceSPI>(), id);
			o.store(rs);
			
			
			// Retrieve with a new instance of an anonymous subclass of
			// ReferenceSetT2ReferenceImpl, just to check that hibernate can cope
			// with this. It can, but this *must* be a subclass of the registered
			// component type, which means we need to modify the component type to
			// be the fully generic T2Reference with all fields accessed via
			// properties.
			T2Reference newReference = new T2ReferenceImpl() {

				public boolean containsErrors() {
					return false;
				}

				public int getDepth() {
					return 0;
				}

				public String getLocalPart() {
					return "testLocal";
				}

				public String getNamespacePart() {
					return "testNamespace";
				}

			};

			
			ReferenceSet returnedset = o.get(newReference);
			Assert.assertNotNull(returnedset.getId());	

	}

}
