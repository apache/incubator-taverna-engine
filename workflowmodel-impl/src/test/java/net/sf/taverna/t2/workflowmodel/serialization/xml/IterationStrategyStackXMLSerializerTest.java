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
import net.sf.taverna.t2.workflowmodel.processor.iteration.DotProduct;
import net.sf.taverna.t2.workflowmodel.processor.iteration.NamedInputPortNode;
import net.sf.taverna.t2.workflowmodel.processor.iteration.impl.IterationStrategyImpl;
import net.sf.taverna.t2.workflowmodel.processor.iteration.impl.IterationStrategyStackImpl;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;



public class IterationStrategyStackXMLSerializerTest implements XMLSerializationConstants {
	IterationStrategyStackXMLSerializer serializer = IterationStrategyStackXMLSerializer.getInstance();
	
	@Test
	public void testIterationStrategyStack() throws Exception {
		NamedInputPortNode nipn1 = new NamedInputPortNode("a", 0);
		NamedInputPortNode nipn2 = new NamedInputPortNode("b", 0);
		IterationStrategyImpl strat = new IterationStrategyImpl();
		
		DotProduct dp = new DotProduct();
		nipn1.setParent(dp);
		nipn2.setParent(dp);
		dp.setParent(strat.getTerminalNode());
		IterationStrategyImpl is = new IterationStrategyImpl();
		is.addInput(nipn1);
		is.addInput(nipn2);
		
		IterationStrategyStackImpl stack = new IterationStrategyStackImpl();
		stack.addStrategy(strat);
		
		Element el = serializer.iterationStrategyStackToXML(stack);
		
		assertEquals("root name should be iterationStrategyStack","iterationStrategyStack",el.getName());
		assertEquals("child name should be iteration","iteration",el.getChild("iteration",T2_WORKFLOW_NAMESPACE).getName());
		Element iteration=el.getChild("iteration",T2_WORKFLOW_NAMESPACE);
		assertEquals("there should be 1 child named strategy",1,iteration.getChildren("strategy",T2_WORKFLOW_NAMESPACE).size());
		Element strategy=iteration.getChild("strategy",T2_WORKFLOW_NAMESPACE);
		assertEquals("there should be 1 child named dot",1,strategy.getChildren("dot",T2_WORKFLOW_NAMESPACE).size());
		assertEquals("there should be no child named cross",0,strategy.getChildren("cross",T2_WORKFLOW_NAMESPACE).size());
		Element dot=strategy.getChild("dot",T2_WORKFLOW_NAMESPACE);
		dot.setNamespace(null);
		for (Object child : dot.getChildren()) {
			((Element)child).setNamespace(null);
		}
		assertEquals("wrong xml for dot","<dot><port name=\"a\" depth=\"0\" /><port name=\"b\" depth=\"0\" /></dot>",elementToString(dot));
		
	}
	
	private String elementToString(Element element) {
		return new XMLOutputter().outputString(element);
	}

	

}
