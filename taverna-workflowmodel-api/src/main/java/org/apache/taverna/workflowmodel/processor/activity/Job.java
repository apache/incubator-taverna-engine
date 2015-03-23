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

package org.apache.taverna.workflowmodel.processor.activity;

import java.util.Map;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.IterationInternalEvent;
import org.apache.taverna.invocation.ProcessIdentifierException;
import org.apache.taverna.reference.T2Reference;

/**
 * Contains a (possibly partial) job description. A job is the smallest entity
 * that can be enacted by the invocation layer of the dispatch stack within a
 * processor. Jobs are partial jobs if the set of keys in the data map is not
 * identical to the set of named input ports on the processor within which the
 * job is used. These objects are used internally within the processor to stage
 * data during iteration and within the dispatch stack, they do not appear
 * within the workflow itself.
 * 
 * @author Tom Oinn
 */
public class Job extends IterationInternalEvent<Job> {
	private Map<String, T2Reference> dataMap;

	/**
	 * Push the index array onto the owning process name and return the new Job
	 * object. Does not modify this object, the method creates a new Job with
	 * the modified index array and owning process
	 * 
	 * @return
	 */
	@Override
	public Job pushIndex() {
		return new Job(getPushedOwningProcess(), new int[] {}, dataMap, context);
	}

	/**
	 * Pull the index array previous pushed to the owning process name and
	 * prepend it to the current index array
	 */
	@Override
	public Job popIndex() {
		return new Job(owner.substring(0, owner.lastIndexOf(':')),
				getPoppedIndex(), dataMap, context);
	}

	/**
	 * The actual data carried by this (partial) Job object is in the form of a
	 * map, where the keys of the map are Strings identifying the named input
	 * and the values are Strings containing valid data identifiers within the
	 * context of a visible DataManager object (see CloudOne specification for
	 * further information on the DataManager system)
	 * 
	 * @return Map of name to data reference for this Job
	 */
	public Map<String, T2Reference> getData() {
		return this.dataMap;
	}

	/**
	 * Create a new Job object with the specified owning process (colon
	 * separated 'list' of process identifiers), index array and data map
	 * 
	 * @param owner
	 * @param index
	 * @param data
	 */
	public Job(String owner, int[] index, Map<String, T2Reference> data,
			InvocationContext context) {
		super(owner, index, context);
		this.dataMap = data;
	}

	/**
	 * Show the owner, index array and data map in textual form for debugging
	 * and any other purpose. Jobs appear in the form :
	 * 
	 * <pre>
	 * Job(Process1)[2,0]{Input2=dataID4,Input1=dataID3}
	 * </pre>
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Job(").append(owner).append(")[");
		String sep = "";
		for (int i : index) {
			sb.append(sep).append(i);
			sep = ",";
		}
		sb.append("]{");
		sep = "";
		for (String key : dataMap.keySet()) {
			sb.append(sep).append(key).append("=").append(dataMap.get(key));
			sep = ",";
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public Job popOwningProcess() throws ProcessIdentifierException {
		return new Job(popOwner(), index, dataMap, context);
	}

	@Override
	public Job pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException {
		return new Job(pushOwner(localProcessName), index, dataMap, context);
	}
}
