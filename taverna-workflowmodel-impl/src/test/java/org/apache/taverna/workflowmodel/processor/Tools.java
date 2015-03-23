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

package org.apache.taverna.workflowmodel.processor;

import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.InputPort;
import org.apache.taverna.workflowmodel.OutputPort;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.ProcessorInputPort;
import org.apache.taverna.workflowmodel.ProcessorOutputPort;
import org.apache.taverna.workflowmodel.impl.EditsImpl;
import org.apache.taverna.workflowmodel.impl.ProcessorImpl;
import org.apache.taverna.workflowmodel.processor.activity.Activity;

/**
 * Not to be confused with the probably more helpful 
 * {@link org.apache.taverna.workflowmodel.utils.Tools}.
 * 
 * @author Tom Oinn
 * @author Stuart Owen
 */
public class Tools {
	private static EditsImpl edits = new EditsImpl();

	/**
	 * Construct a new {@link Processor} with a single {@link Activity} and
	 * overall processor inputs and outputs mapped to the activity inputs and
	 * outputs. This is intended to be equivalent to the processor creation in
	 * Taverna1 where the concepts of Processor and Activity were somewhat
	 * confused; it also inserts retry, parallelise and failover layers
	 * configured as a Taverna1 process would be.
	 * <p>
	 * Modifies the given activity object, adding the mappings for input and
	 * output port names (these will all be fooport->fooport but they're still
	 * needed)
	 * 
	 * @param activity
	 *            the {@link Activity} to use to build the new processor around
	 * @return An initialised {@link ProcessorImpl}
	 */
	public static Processor buildFromActivity(Activity<?> activity)
			throws EditException {
		Processor processor = edits.createProcessor("");
		edits.getDefaultDispatchStackEdit(processor).doEdit();
		// Add the Activity to the processor
		edits.getAddActivityEdit(processor, activity).doEdit();
		/*
		 * Create processor inputs and outputs corresponding to activity inputs
		 * and outputs and set the mappings in the Activity object.
		 */
		activity.getInputPortMapping().clear();
		activity.getOutputPortMapping().clear();
		for (InputPort ip : activity.getInputPorts()) {
			ProcessorInputPort pip = edits.createProcessorInputPort(processor,
					ip.getName(), ip.getDepth());
			edits.getAddProcessorInputPortEdit(processor, pip).doEdit();
			activity.getInputPortMapping().put(ip.getName(), ip.getName());
		}
		for (OutputPort op : activity.getOutputPorts()) {
			ProcessorOutputPort pop = edits.createProcessorOutputPort(
					processor, op.getName(), op.getDepth(),
					op.getGranularDepth());
			edits.getAddProcessorOutputPortEdit(processor, pop).doEdit();
			activity.getOutputPortMapping().put(op.getName(), op.getName());
		}
		
		return processor;
	}

	private Tools() {
	}
}
