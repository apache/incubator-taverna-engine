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

package org.apache.taverna.invocation;

import org.apache.taverna.reference.T2Reference;

/**
 * A single data token passed between processors in a workflow. This is distinct
 * from the Job in that it contains a single (unnamed) data reference whereas
 * the Job holds a map of arbitrarily many named data references in a bundle.
 * 
 * @author Tom Oinn
 */
public class WorkflowDataToken extends Event<WorkflowDataToken> {
	private T2Reference dataRef;

	/**
	 * Construct a new data token with the specified owning process, conceptual
	 * index array and data reference
	 * 
	 * @param owningProcess
	 * @param index
	 * @param dataRef
	 */
	public WorkflowDataToken(String owningProcess, int[] index,
			T2Reference dataRef, InvocationContext context) {
		super(owningProcess, index, context);
		this.dataRef = dataRef;
	}

	@Override
	public WorkflowDataToken popOwningProcess()
			throws ProcessIdentifierException {
		return new WorkflowDataToken(popOwner(), index, dataRef, context);
	}

	@Override
	public WorkflowDataToken pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException {
		return new WorkflowDataToken(pushOwner(localProcessName), index,
				dataRef, context);
	}

	/**
	 * Return the ID of the data this event represents
	 * 
	 * @return
	 */
	public T2Reference getData() {
		return this.dataRef;
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
		sb.append("Token(").append(owner).append(")[");
		String sep = "";
		for (int idx : index) {
			sb.append(sep).append(idx);
			sep = ",";
		}
		sb.append("]{").append(dataRef).append("}");
		return sb.toString();
	}
}
