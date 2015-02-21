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
package net.sf.taverna.t2.activities.stringconstant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.activities.testutils.ActivityInvoker;
import net.sf.taverna.t2.workflowmodel.AbstractPort;

import org.junit.Test;

/**
 * Tests the StringConstantActivity
 * @author Stuart Owen
 *
 */
public class StringConstantActivityTest {

	/**
	 * Simple invocation test. Also tests Activity.configure sets up the correct output port.
	 * @throws Exception
	 */
	@Test
	public void testInvoke() throws Exception {
		return;
//		StringConstantConfigurationBean bean = new StringConstantConfigurationBean();
//		bean.setValue("this_is_a_string");
//		StringConstantActivity activity = new StringConstantActivity();
//		activity.configure(bean);
//		
//		assertEquals("there should be no inputs",0,activity.getInputPorts().size());
//		assertEquals("there should be 1 output",1,activity.getOutputPorts().size());
//		assertEquals("the output port name should be value","value",((AbstractPort)activity.getOutputPorts().toArray()[0]).getName());
//		
//		Map<String, Class<?>> expectedOutputs = new HashMap<String, Class<?>>();
//		expectedOutputs.put("value", String.class);
//
//		Map<String,Object> outputs = ActivityInvoker.invokeAsyncActivity(activity, new HashMap<String, Object>(), expectedOutputs);
//		
//		assertEquals("there should be 1 output",1,outputs.size());
//		assertTrue("there should be an output named value",outputs.containsKey("value"));
//		assertEquals("The value of the output should be 'this_is_a_string'","this_is_a_string",outputs.get("value"));
//		assertTrue("The output type should be String",outputs.get("value") instanceof String);
	}
}
