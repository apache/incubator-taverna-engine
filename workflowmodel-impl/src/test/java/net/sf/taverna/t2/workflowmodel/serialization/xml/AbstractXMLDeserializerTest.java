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
package net.sf.taverna.t2.workflowmodel.serialization.xml;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import net.sf.taverna.t2.workflowmodel.serialization.DummyBean;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

public class AbstractXMLDeserializerTest {
	AbstractXMLDeserializer deserializer = new AbstractXMLDeserializer() {};
	
	@Test
	public void testCreateBeanSimple() throws Exception {
		Element el = new Element(XMLSerializationConstants.CONFIG_BEAN, XMLSerializationConstants.T2_WORKFLOW_NAMESPACE);
		el.setAttribute("encoding","xstream");
		Element elString = new Element("string");
		elString.setText("12345");
		el.addContent(elString);
		
		Object bean = deserializer.createBean(el, XMLDeserializerImpl.class.getClassLoader());
		assertTrue("bean should be a String",bean instanceof String);
		assertEquals("string should equal 12345","12345",((String)bean));
	}
	
	@Test
	public void testCreateBeanComplex() throws Exception {
		String xml="<configBean xmlns='http://taverna.sf.net/2008/xml/t2flow' encoding=\"xstream\"><net.sf.taverna.t2.workflowmodel.serialization.DummyBean><id>1</id><name>bob</name><innerBean><stuff>xyz</stuff></innerBean></net.sf.taverna.t2.workflowmodel.serialization.DummyBean></configBean>";
		Element el = new SAXBuilder().build(new StringReader(xml)).detachRootElement();
		
		Object bean = deserializer.createBean(el, XMLDeserializerImpl.class.getClassLoader());
		assertTrue("bean should be a DummyBean",bean instanceof DummyBean);
		DummyBean dummyBean = (DummyBean)bean;
		
		assertEquals("id should be 1",1,dummyBean.getId());
		assertEquals("namne should be bob","bob",dummyBean.getName());
		assertEquals("stuff should by xyz","xyz",dummyBean.getInnerBean().getStuff());
	}
	
	@Test
	public void testCreateBeanJDomXML() throws Exception {
		String xml="<configBean xmlns='http://taverna.sf.net/2008/xml/t2flow'  encoding=\"jdomxml\"><fred><child1/><child2/></fred></configBean>";
		Element el = new SAXBuilder().build(new StringReader(xml)).detachRootElement();
		
		Object bean = deserializer.createBean(el, XMLDeserializerImpl.class.getClassLoader());
		assertNotNull("There bean should not be null",bean);
		assertTrue("Bean should be an instance of Element",bean instanceof Element);
		el = (Element)bean;
		assertEquals("The root element should be named fred","fred",el.getName());
		assertEquals("There should be 2 child elements",2,el.getChildren().size());
	}

}
