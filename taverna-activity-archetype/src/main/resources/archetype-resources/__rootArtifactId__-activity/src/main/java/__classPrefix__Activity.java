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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivity;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivityCallback;

/**
 * \${classPrefix} <code>Activity<code>.
 */
public class \${classPrefix}Activity extends AbstractAsynchronousActivity<JsonNode>
		implements AsynchronousActivity<JsonNode> {

	public static final String ACTIVITY_TYPE = "http://example.com/2013/activity/\${rootArtifactId}";

	/*
	 * Best practice: Keep port names as constants to avoid misspelling. This
	 * would not apply if port names are looked up dynamically from the service
	 * operation, like done for WSDL services.
	 */
	public static final String IN_FIRST_INPUT = "firstInput";
	public static final String IN_EXTRA_DATA = "extraData";
	public static final String OUT_MORE_OUTPUTS = "moreOutputs";
	public static final String OUT_SIMPLE_OUTPUT = "simpleOutput";
	public static final String OUT_REPORT = "report";

	private JsonNode configuration;

	@Override
	public void configure(JsonNode configuration) throws ActivityConfigurationException {

		// Any pre-config sanity checks
		if (configuration.get("exampleString").asText().equals("invalidExample")) {
			throw new ActivityConfigurationException(
					"Example string can't be 'invalidExample'");
		}
		// Store for getConfiguration(), but you could also make
		// getConfiguration() return a new bean from other sources
		this.configuration = configuration;

		// OPTIONAL:
		// Do any server-side lookups and configuration, like resolving WSDLs

		// myClient = new MyClient(configuration.get("exampleUri").asText());
		// this.service = myClient.getService(configuration.get("exampleString").asText());

	}

	@Override
	public JsonNode getConfiguration() {
		return configuration;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void executeAsynch(final Map<String, T2Reference> inputs,
			final AsynchronousActivityCallback callback) {
		// Don't execute service directly now, request to be run ask to be run
		// from thread pool and return asynchronously
		callback.requestRun(new Runnable() {

			public void run() {
				InvocationContext context = callback
						.getContext();
				ReferenceService referenceService = context
						.getReferenceService();
				// Resolve inputs
				String firstInput = (String) referenceService.renderIdentifier(inputs.get(IN_FIRST_INPUT),
						String.class, context);

				// Support our configuration-dependendent input
				boolean optionalPorts = configuration.get("exampleString").asText().equals("specialCase");

				List<byte[]> special = null;
				// We'll also allow IN_EXTRA_DATA to be optionally not provided
				if (optionalPorts && inputs.containsKey(IN_EXTRA_DATA)) {
					// Resolve as a list of byte[]
					special = (List<byte[]>) referenceService.renderIdentifier(
							inputs.get(IN_EXTRA_DATA), byte[].class, context);
				}


				// TODO: Do the actual service invocation
//				try {
//					results = this.service.invoke(firstInput, special)
//				} catch (ServiceException ex) {
//					callback.fail("Could not invoke \${classPrefix} service " + configBean.getExampleUri(),
//							ex);
//					// Make sure we don't call callback.receiveResult later
//					return;
//				}

				// Register outputs
				Map<String, T2Reference> outputs = new HashMap<>();
				String simpleValue = "simple";
				T2Reference simpleRef = referenceService.register(simpleValue, 0, true, context);
				outputs.put(OUT_SIMPLE_OUTPUT, simpleRef);

				// For list outputs, only need to register the top level list
				List<String> moreValues = new ArrayList<>();
				moreValues.add("Value 1");
				moreValues.add("Value 2");
				T2Reference moreRef = referenceService.register(moreValues, 1, true, context);
				outputs.put(OUT_MORE_OUTPUTS, moreRef);

				if (optionalPorts) {
					// Populate our optional output port
					// NOTE: Need to return output values for all defined output ports
					String report = "Everything OK";
					outputs.put(OUT_REPORT, referenceService.register(report,
							0, true, context));
				}

				// return map of output data, with empty index array as this is
				// the only and final result (this index parameter is used if
				// pipelining output)
				callback.receiveResult(outputs, new int[0]);
			}
		});
	}

}
