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

import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.facade.FacadeListener;
import org.apache.taverna.facade.ResultListener;
import org.apache.taverna.facade.WorkflowInstanceFacade;
import org.apache.taverna.facade.WorkflowInstanceFacade.State;
import org.apache.taverna.invocation.TokenOrderException;
import org.apache.taverna.invocation.WorkflowDataToken;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.InvalidDataflowException;
import org.apache.taverna.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivityCallback;
import org.apache.taverna.workflowmodel.processor.activity.NestedDataflow;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An Activity providing nested Dataflow functionality.
 *
 * @author David Withers
 */
public class DataflowActivity extends AbstractAsynchronousActivity<JsonNode> implements NestedDataflow {

	public static final String URI = "http://ns.taverna.org.uk/2010/activity/nested-workflow";

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DataflowActivity.class);

	private Dataflow dataflow;

	private JsonNode json;

	@Override
	public void configure(JsonNode json) throws ActivityConfigurationException {
		this.json = json;
//		dataflow.checkValidity();
//		buildInputPorts();
//		buildOutputPorts();
	}

	@Override
	public JsonNode getConfiguration() {
		return json;
	}

	@Override
	public void executeAsynch(final Map<String, T2Reference> data,
			final AsynchronousActivityCallback callback) {
		callback.requestRun(new Runnable() {
			
			Map<String, T2Reference> outputData = new HashMap<String, T2Reference>();

			public void run() {

				final WorkflowInstanceFacade facade;
				try {
					facade = getEdits().createWorkflowInstanceFacade(dataflow, callback.getContext(),
							callback.getParentProcessIdentifier());
				} catch (InvalidDataflowException ex) {
					callback.fail("Invalid workflow", ex);
					return;
				}

				final ResultListener rl = new ResultListener() {


					public void resultTokenProduced(WorkflowDataToken dataToken, String port) {
						if (dataToken.getIndex().length == 0) {
							outputData.put(port, dataToken.getData());
						}
					}
				};
				
				final FacadeListener fl = new FacadeListener() {

					@Override
					public void workflowFailed(WorkflowInstanceFacade facade,
							String message, Throwable t) {
						callback.fail(message, t);
					}

					@Override
					public void stateChange(WorkflowInstanceFacade facade,
							State oldState, State newState) {
						if (newState == State.completed) {
							facade.removeResultListener(rl);
							facade.removeFacadeListener(this);
							callback.receiveResult(outputData, new int[]{});
						}
					}
					
				};
				
				facade.addResultListener(rl);
				facade.addFacadeListener(fl);

				facade.fire();

				for (Map.Entry<String, T2Reference> entry : data.entrySet()) {
					try {
						WorkflowDataToken token = new WorkflowDataToken(callback
								.getParentProcessIdentifier(), new int[] {}, entry.getValue(),
								callback.getContext());
						facade.pushData(token, entry.getKey());
					} catch (TokenOrderException e) {
						callback.fail("Failed to push data into facade", e);
					}
				}

			}

		});
	}

//	private void buildInputPorts() throws ActivityConfigurationException {
//		inputPorts.clear();
//		for (DataflowInputPort dataflowInputPort : dataflow.getInputPorts()) {
//			addInput(dataflowInputPort.getName(), dataflowInputPort.getDepth(), true,
//					new ArrayList<Class<? extends ExternalReferenceSPI>>(), null);
//		}
//	}

//	private void buildOutputPorts() throws ActivityConfigurationException {
//		outputPorts.clear();
//		// granular depth same as depth - no streaming of results
//		for (DataflowOutputPort dataflowOutputPort : dataflow.getOutputPorts()) {
//			addOutput(dataflowOutputPort.getName(), dataflowOutputPort.getDepth(),
//					dataflowOutputPort.getDepth());
//		}
//	}

	public Dataflow getNestedDataflow() {
		return dataflow;
	}

	@Override
	public void setNestedDataflow(Dataflow dataflow) {
		this.dataflow = dataflow;
	}

}
