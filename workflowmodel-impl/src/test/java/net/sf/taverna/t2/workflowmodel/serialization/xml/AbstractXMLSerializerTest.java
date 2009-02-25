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
import net.sf.taverna.t2.workflowmodel.serialization.DummyBean;
import net.sf.taverna.t2.workflowmodel.serialization.InnerBean;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;



public class AbstractXMLSerializerTest {
	AbstractXMLSerializer serializer = new AbstractXMLSerializer() {};
	
	@Test
	public void testBeanAsElementComplex() throws Exception {
		DummyBean bean = new DummyBean();
		InnerBean bean2 = new InnerBean();
		bean.setName("name");
		bean.setId(5);
		bean2.setStuff("stuff");
		bean.setInnerBean(bean2);
		Element el = serializer.beanAsElement(bean);
		assertEquals("root should be configBean","configBean",el.getName());
		assertEquals("the type should be xstream","xstream",el.getAttribute("encoding").getValue());
		assertEquals("there should be 1 DummyBean child",1,el.getChildren("net.sf.taverna.t2.workflowmodel.serialization.DummyBean").size());
		Element beanElement = el.getChild("net.sf.taverna.t2.workflowmodel.serialization.DummyBean");
		assertNotNull("there should be a child id",beanElement.getChild("id"));
		assertEquals("id child should have value 5","5",beanElement.getChild("id").getText());
		assertNotNull("there should be a child name",beanElement.getChild("name"));
		assertEquals("name child should have text name","name",beanElement.getChild("name").getText());
		assertNotNull("there should be a child innerBean",beanElement.getChild("innerBean"));
		Element innerBeanElement = beanElement.getChild("innerBean");
		assertNotNull("innerBean should have a child stuff",innerBeanElement.getChild("stuff"));
		assertEquals("stuff child should have text stuff","stuff",innerBeanElement.getChild("stuff").getText());
		
	}
	
	@Test
	public void testElementBean() throws Exception {
		Element person = new Element("person");
		person.addContent(new Element("name"));
		person.getChild("name").setText("fred smith");
		
		Element el = serializer.beanAsElement(person);
		assertEquals("root should be configBean","configBean",el.getName());
		assertEquals("the type should be jdomxml","jdomxml",el.getAttribute("encoding").getValue());
		assertEquals("there should be 1 person",1,el.getChildren("person").size());
		
		Element person2=el.getChild("person");
		
		assertEquals("XML for person should match",elementToString(person),elementToString(person2));
		
	}
	
	@Test
	public void testBeanAsElementSimple() throws Exception {
		String helloWorld="hello world";
		Element el = serializer.beanAsElement(helloWorld);
		assertEquals("root should be configBean","configBean",el.getName());
		assertEquals("the type should be xstream","xstream",el.getAttribute("encoding").getValue());
		assertEquals("there should be 1 string",1,el.getChildren("string").size());
		String innerXML=elementToString(el.getChild("string"));
		assertEquals("Unexpected xml for the string","<string>hello world</string>",innerXML);
	}
	
	private String elementToString(Element element) {
		return new XMLOutputter().outputString(element);
	}

}
