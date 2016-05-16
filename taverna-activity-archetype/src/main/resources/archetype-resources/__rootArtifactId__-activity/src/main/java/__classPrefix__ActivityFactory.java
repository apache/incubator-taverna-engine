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

import static \${package}.${classPrefix}Activity.*;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.processor.activity.ActivityFactory;
import org.apache.taverna.workflowmodel.processor.activity.ActivityInputPort;
import org.apache.taverna.workflowmodel.processor.activity.ActivityOutputPort;

/**
 * \${classPrefix} <code>ActivityFactory<code>.
 */
public class \${classPrefix}ActivityFactory implements ActivityFactory {

	private Edits edits;

	@Override
	public \${classPrefix}Activity createActivity() {
		return new \${classPrefix}Activity();
	}

	@Override
	public URI getActivityType() {
		return URI.create(\${classPrefix}Activity.ACTIVITY_TYPE);
	}

	@Override
	public JsonNode getActivityConfigurationSchema() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readTree(getClass().getResource("/schema.json"));
		} catch (IOException e) {
			return objectMapper.createObjectNode();
		}
	}

	@Override
	public Set<ActivityInputPort> getInputPorts(JsonNode configuration) {
		Set<ActivityInputPort> inputPorts = new HashSet<>();

		// FIXME: Replace with your input port definitions

		// Hard coded input port, expecting a single String
		inputPorts.add(edits.createActivityInputPort(IN_FIRST_INPUT, 0, true, null, String.class));

		// Optional ports depending on configuration
		if (configuration.get("exampleString").asText().equals("specialCase")) {
			// depth 1, ie. list of binary byte[] arrays
			inputPorts.add(edits.createActivityInputPort(IN_EXTRA_DATA, 1, true, null, byte[].class));
		}

		return inputPorts;
	}

	@Override
	public Set<ActivityOutputPort> getOutputPorts(JsonNode configuration) {
		Set<ActivityOutputPort> outputPorts = new HashSet<>();

		// FIXME: Replace with your output port definitions

		// Optional ports depending on configuration
		if (configuration.get("exampleString").asText().equals("specialCase")) {
			outputPorts.add(edits.createActivityOutputPort(OUT_REPORT, 0, 0));
		}

		// Single value output port (depth 0)
		outputPorts.add(edits.createActivityOutputPort(OUT_SIMPLE_OUTPUT, 0, 0));
		// Output port with list of values (depth 1)
		outputPorts.add(edits.createActivityOutputPort(OUT_MORE_OUTPUTS, 1, 1));

		return outputPorts;
	}

	/**
	 * Sets the edits property.
	 * <p>
	 * This method is used by Spring. The property name must match the property specified
	 * in the Spring context file.
	 *
	 * @param edits the <code>Edits</code> used to create input/output ports
	 */
	public void setEdits(Edits edits) {
		this.edits = edits;
	}

}
