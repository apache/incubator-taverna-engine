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

package org.apache.taverna.activities.dataflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.activities.testutils.ActivityInvoker;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.impl.EditsImpl;
import org.apache.taverna.workflowmodel.processor.activity.impl.ActivityInputPortImpl;
import org.apache.taverna.workflowmodel.processor.activity.impl.ActivityOutputPortImpl;

import org.junit.Before;
import org.junit.Test;

/**
 * Dataflow Activity Tests
 *
 * @author David Withers
 */
public class DataflowActivityTest {

	private Dataflow dataflow;

	private DataflowActivity activity;

	@Before
	public void setUp() throws Exception {
		activity = new DataflowActivity();
		Edits edits = new EditsImpl();
		activity.setEdits(edits);
		dataflow = edits.createDataflow();
		edits.getCreateDataflowInputPortEdit(dataflow, "input", 0, 0).doEdit();
		edits.getCreateDataflowOutputPortEdit(dataflow, "output").doEdit();
		Datalink datalink = edits.createDatalink(dataflow.getInputPorts().get(0)
				.getInternalOutputPort(), dataflow.getOutputPorts().get(0).getInternalInputPort());
		edits.getConnectDatalinkEdit(datalink).doEdit();
	}

	@Test
	public void testConfigureDataflowActivityConfigurationBean() throws Exception {
		activity.setNestedDataflow(dataflow);
		assertEquals(dataflow, activity.getNestedDataflow());

		Edits edits = new EditsImpl();
		dataflow = edits.createDataflow();
		edits.getAddActivityInputPortEdit(activity, new ActivityInputPortImpl("input", 0)).doEdit();
		edits.getAddActivityOutputPortEdit(activity, new ActivityOutputPortImpl("output", 0, 0))
				.doEdit();

		assertEquals(1, activity.getInputPorts().size());
		assertEquals("input", activity.getInputPorts().iterator().next().getName());
		assertEquals(1, activity.getOutputPorts().size());
		assertEquals("output", activity.getOutputPorts().iterator().next().getName());

		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("input", "aString");
		Map<String, Class<?>> expectedOutputs = new HashMap<String, Class<?>>();
		expectedOutputs.put("output", String.class);

		Map<String, Object> outputs = ActivityInvoker.invokeAsyncActivity(activity, inputs,
				expectedOutputs);
		assertTrue("there should be an output named output", outputs.containsKey("output"));
		assertEquals("output should have the value aString", "aString", outputs.get("output"));
	}

	@Test
	public void testGetConfiguration() {
		assertNull("freshly created activity should not contain configuration",
				activity.getConfiguration());
	}

}
