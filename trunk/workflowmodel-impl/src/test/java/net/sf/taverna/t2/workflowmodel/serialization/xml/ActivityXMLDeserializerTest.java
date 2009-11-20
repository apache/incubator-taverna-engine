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

import java.util.HashMap;

import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.serialization.DummyActivity;

import org.jdom.Element;
import org.junit.Test;



public class ActivityXMLDeserializerTest extends DeserializerTestsHelper {
		ActivityXMLDeserializer deserializer = ActivityXMLDeserializer.getInstance();

		@Test
		public void testActivityDeserialization() throws Exception {
			Element el = loadXMLFragment("activity.xml");
			Activity<?> activity = deserializer.deserializeActivity(el,new HashMap<String,Element>());
			
			assertNotNull("The activity should not be NULL",activity);
			assertTrue("should be a DummyActivity",activity instanceof DummyActivity);
			assertTrue("bean should be an Integer",activity.getConfiguration() instanceof Integer);
			assertEquals("bean should equal 5",5,((Integer)activity.getConfiguration()).intValue());
			
			assertEquals("there should be 1 input port mapping",1,activity.getInputPortMapping().size());
			assertEquals("there should be 1 output port mapping",1,activity.getOutputPortMapping().size());
			
			assertEquals("input in is mapped to in","in",activity.getInputPortMapping().get("in"));
			assertEquals("output out is mapped to out","out",activity.getOutputPortMapping().get("out"));
		}

}
