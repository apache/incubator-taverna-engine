/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.activities.stringconstant;


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
