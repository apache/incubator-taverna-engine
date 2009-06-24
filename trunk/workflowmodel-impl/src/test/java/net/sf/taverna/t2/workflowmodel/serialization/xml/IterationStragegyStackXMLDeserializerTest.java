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
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategy;

import org.jdom.Element;
import org.junit.Test;

public class IterationStragegyStackXMLDeserializerTest extends DeserializerTestsHelper {
	private IterationStrategyStackXMLDeserializer deserializer = IterationStrategyStackXMLDeserializer.getInstance();
	
	@Test
	public void testCrossProducts() throws Exception {
		Element el = loadXMLFragment("2_port_cross_product.xml");
		Processor p =edits.createProcessor("test");
		
		deserializer.deserializeIterationStrategyStack(el, p.getIterationStrategy());
		assertEquals("There should be 1 strategy",1,p.getIterationStrategy().getStrategies().size());
		
		IterationStrategy strat = p.getIterationStrategy().getStrategies().get(0);
		
		assertNotNull(strat.getDesiredCardinalities());
		assertNotNull(strat.getDesiredCardinalities().get("nested_beanshell_in"));
		assertNotNull(strat.getDesiredCardinalities().get("nested_beanshell_in2"));
		
		assertEquals("cardinality should be 0",Integer.valueOf(0),strat.getDesiredCardinalities().get("nested_beanshell_in"));
		assertEquals("cardinality should be 1",Integer.valueOf(1),strat.getDesiredCardinalities().get("nested_beanshell_in2"));
		
	}
}
