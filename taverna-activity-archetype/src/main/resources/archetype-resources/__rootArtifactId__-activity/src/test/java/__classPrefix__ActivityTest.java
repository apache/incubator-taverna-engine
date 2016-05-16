##    Licensed to the Apache Software Foundation (ASF) under one or more
##    contributor license agreements.  See the NOTICE file distributed with
##    this work for additional information regarding copyright ownership.
##    The ASF licenses this file to You under the Apache License, Version 2.0
##    (the "License"); you may not use this file except in compliance with
##    the License.  You may obtain a copy of the License at
##
##    http://www.apache.org/licenses/LICENSE-2.0
##
##    Unless required by applicable law or agreed to in writing, software
##    distributed under the License is distributed on an "AS IS" BASIS,
##    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
##    See the License for the specific language governing permissions and
##    limitations under the License.
##
## Note: Above Velocity comment should NOT be included in generated
## code from the archetype
package \${package};

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.taverna.activities.testutils.ActivityInvoker;
import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;
import org.apache.taverna.workflowmodel.processor.activity.ActivityInputPort;
import org.apache.taverna.workflowmodel.processor.activity.ActivityOutputPort;

import org.junit.Before;
import org.junit.Test;

public class \${classPrefix}ActivityTest {

	private ObjectNode configuration;

	private \${classPrefix}Activity activity = new ${classPrefix}Activity();

	@Before
	public void makeConfiguration() throws Exception {
		configuration = JsonNodeFactory.instance.objectNode();
		configuration.put("exampleString", "something");
		configuration.put("exampleUri", "http://localhost:8080/myEndPoint");
	}

	@Test
	public void configureActivity() throws Exception {
		activity.configure(configuration);
		assertTrue(configuration.equals(activity.getConfiguration()));
	}

	@Test(expected = ActivityConfigurationException.class)
	public void invalidConfiguration() throws ActivityConfigurationException {
		ObjectNode invalidBean = JsonNodeFactory.instance.objectNode();
		invalidBean.put("exampleString", "invalidExample");
		// Should throw ActivityConfigurationException
		activity.configure(invalidBean);
	}

	@Test
	public void executeAsynch() throws Exception {
		activity.configure(configuration);

		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("firstInput", "hello");

		Map<String, Class<?>> expectedOutputTypes = new HashMap<String, Class<?>>();
		expectedOutputTypes.put("simpleOutput", String.class);
		expectedOutputTypes.put("moreOutputs", String.class);

		Map<String, Object> outputs = ActivityInvoker.invokeAsyncActivity(
				activity, inputs, expectedOutputTypes);

		assertEquals("Unexpected outputs", 2, outputs.size());
		assertEquals("simple", outputs.get("simpleOutput"));
		assertEquals(Arrays.asList("Value 1", "Value 2"), outputs
				.get("moreOutputs"));

	}

}
