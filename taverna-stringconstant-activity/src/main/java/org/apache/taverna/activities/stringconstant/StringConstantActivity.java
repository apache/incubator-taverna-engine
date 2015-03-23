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

import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.annotation.annotationbeans.MimeType;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.ReferenceServiceException;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;
import org.apache.taverna.workflowmodel.processor.activity.ActivityOutputPort;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivityCallback;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An Activity that holds a constant string value.
 * <p>
 * It is automatically configured to have no input ports and only one output port named
 * <em>value</em>.
 *
 * @author Stuart Owen
 * @author David Withers
 */
public class StringConstantActivity extends AbstractAsynchronousActivity<JsonNode> {

	public static final String URI = "http://ns.taverna.org.uk/2010/activity/constant";

	private static final Logger logger = Logger.getLogger(StringConstantActivity.class);

	private String value;

	private JsonNode json;

	@Override
	public void configure(JsonNode json) throws ActivityConfigurationException {
		this.json = json;
		this.value = json.get("string").asText();
//		if (outputPorts.size() == 0) {
//			addOutput("value", 0, "text/plain");
//		}
	}

	public String getStringValue() {
		return json.get("string").asText();
	}

	@Override
	public JsonNode getConfiguration() {
		return json;
	}

	@Override
	public void executeAsynch(final Map<String, T2Reference> data,
			final AsynchronousActivityCallback callback) {
		callback.requestRun(new Runnable() {

			@Override
			public void run() {
				ReferenceService referenceService = callback.getContext().getReferenceService();
				try {
					T2Reference id = referenceService.register(value, 0, true,
							callback.getContext());
					Map<String, T2Reference> outputData = new HashMap<String, T2Reference>();
					outputData.put("value", id);
					callback.receiveResult(outputData, new int[0]);
				} catch (ReferenceServiceException e) {
					callback.fail(e.getMessage(), e);
				}
			}

		});

	}

//	protected void addOutput(String portName, int portDepth, String type) {
//		ActivityOutputPort port = edits.createActivityOutputPort(portName, portDepth, portDepth);
//		MimeType mimeType = new MimeType();
//		mimeType.setText(type);
//		try {
//			edits.getAddAnnotationChainEdit(port, mimeType).doEdit();
//		} catch (EditException e) {
//			logger.debug("Error adding MimeType annotation to port", e);
//		}
//		outputPorts.add(port);
//	}

	public String getExtraDescription() {
		if (value.length() > 60) {
			return value.substring(0, 60 - 3) + "...";
		}
		return value;
	}

}
