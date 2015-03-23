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

package org.apache.taverna.workflowmodel.processor.dispatch.events;

import static org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType.JOB;

import java.util.List;
import java.util.Map;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.ProcessIdentifierException;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.workflowmodel.processor.dispatch.description.DispatchMessageType;

/**
 * An event within the dispatch stack containing a single job's worth of data
 * along with an ordered list of Activity instances.
 * 
 * @author Tom Oinn
 */
public class DispatchJobEvent extends AbstractDispatchEvent<DispatchJobEvent> {
	private Map<String, T2Reference> dataMap;
	private List<? extends Activity<?>> activities;

	/**
	 * Create a new job event, specifying a complete set of input data and a
	 * list of activities which could potentially consume this data
	 * 
	 * @param owningProcess
	 * @param index
	 * @param context
	 * @param data
	 * @param activities
	 */
	public DispatchJobEvent(String owningProcess, int[] index,
			InvocationContext context, Map<String, T2Reference> data,
			List<? extends Activity<?>> activities) {
		super(owningProcess, index, context);
		this.dataMap = data;
		this.activities = activities;
	}

	/**
	 * The actual data carried by this dispatch job event object is in the form
	 * of a map, where the keys of the map are Strings identifying the named
	 * input and the values are Strings containing valid data identifiers within
	 * the context of a visible DataManager object (see CloudOne specification
	 * for further information on the DataManager system)
	 * 
	 * @return Map of name to data reference for this Job
	 */
	public Map<String, T2Reference> getData() {
		return this.dataMap;
	}

	/**
	 * Returns a list of activity instances which can be applied to the data
	 * contained by this job event.
	 * 
	 * @return ordered list of Activity instances
	 */
	public List<? extends Activity<?>> getActivities() {
		return this.activities;
	}

	@Override
	public DispatchJobEvent popOwningProcess()
			throws ProcessIdentifierException {
		return new DispatchJobEvent(popOwner(), index, context, dataMap,
				activities);
	}

	@Override
	public DispatchJobEvent pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException {
		return new DispatchJobEvent(pushOwner(localProcessName), index,
				context, dataMap, activities);
	}

	/**
	 * @return Always a {@link DispatchMessageType#JOB}.
	 */
	@Override
	public DispatchMessageType getMessageType() {
		return JOB;
	}
}
