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

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.processor.activity.ActivityFactory;
import org.apache.taverna.workflowmodel.processor.activity.ActivityInputPort;
import org.apache.taverna.workflowmodel.processor.activity.ActivityOutputPort;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An {@link ActivityFactory} for creating <code>DataflowActivity</code>.
 *
 * @author David Withers
 */
public class DataflowActivityFactory implements ActivityFactory {

	private Edits edits;

	@Override
	public DataflowActivity createActivity() {
		DataflowActivity activity = new DataflowActivity();
		activity.setEdits(edits);
		return activity;
	}

	@Override
	public URI getActivityType() {
		return URI.create(DataflowActivity.URI);
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
		return new HashSet<>();
	}

	@Override
	public Set<ActivityOutputPort> getOutputPorts(JsonNode configuration) {
		return new HashSet<>();
	}

	public void setEdits(Edits edits) {
		this.edits = edits;
	}

}
