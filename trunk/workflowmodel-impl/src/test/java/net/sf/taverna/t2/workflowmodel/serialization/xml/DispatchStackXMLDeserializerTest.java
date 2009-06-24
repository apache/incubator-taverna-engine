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
import static org.junit.Assert.assertTrue;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.ErrorBounce;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Failover;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Invoke;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Parallelize;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.layers.Retry;

import org.jdom.Element;
import org.junit.Test;


public class DispatchStackXMLDeserializerTest extends DeserializerTestsHelper {
	DispatchStackXMLDeserializer deserializer = DispatchStackXMLDeserializer.getInstance();
	
	@Test
	public void testDispatchStack() throws Exception {
		Element el = loadXMLFragment("dispatchStack.xml");
		Processor p = edits.createProcessor("p");
		deserializer.deserializeDispatchStack(p, el);
		assertEquals("there should be 5 layers",5,p.getDispatchStack().getLayers().size());
		assertTrue("first layer should be parallelize, but was "+p.getDispatchStack().getLayers().get(0),p.getDispatchStack().getLayers().get(0) instanceof Parallelize);
		assertTrue("2nd layer should be ErrorBounce, but was "+p.getDispatchStack().getLayers().get(1),p.getDispatchStack().getLayers().get(1) instanceof ErrorBounce);
		assertTrue("3rd layer should be Failover, but was "+p.getDispatchStack().getLayers().get(2),p.getDispatchStack().getLayers().get(2) instanceof Failover);
		assertTrue("4th layer should be Retry, but was "+p.getDispatchStack().getLayers().get(3),p.getDispatchStack().getLayers().get(3) instanceof Retry);
		assertTrue("5th layer should be Invoke, but was "+p.getDispatchStack().getLayers().get(4),p.getDispatchStack().getLayers().get(4) instanceof Invoke);
	}

}
