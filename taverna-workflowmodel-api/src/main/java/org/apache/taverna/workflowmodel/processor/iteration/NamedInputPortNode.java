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

package org.apache.taverna.workflowmodel.processor.iteration;

import java.util.Map;

import org.apache.taverna.invocation.Completion;
import org.apache.taverna.workflowmodel.processor.activity.Job;

/**
 * Acts as the input to a stage within the iteration strategy, passes all jobs
 * straight through. NamedInputPortNode objects are, as the name suggests,
 * named. These names correspond to the names of abstract input ports on the
 * Processor object to which the iteration strategy belongs.
 * 
 * @author Tom Oinn
 */
@SuppressWarnings("serial")
public class NamedInputPortNode extends AbstractIterationStrategyNode {
	private String portName;
	private int desiredCardinality;

	public NamedInputPortNode(String name, int cardinality) {
		super();
		this.portName = name;
		this.desiredCardinality = cardinality;
	}

	/**
	 * If this node receives a job it will always be pushed without modification
	 * up to the parent
	 */
	@Override
	public void receiveJob(int inputIndex, Job newJob) {
		pushJob(newJob);
	}

	/**
	 * Completion events are passed straight through the same as jobs
	 */
	@Override
	public void receiveCompletion(int inputIndex, Completion completion) {
		pushCompletion(completion);
	}

	/**
	 * Each node maps to a single named input port within the processor
	 */
	public String getPortName() {
		return this.portName;
	}

	/**
	 * Each node defines the level of collection depth for that input port
	 */
	public int getCardinality() {
		return this.desiredCardinality;
	}

	/**
	 * These nodes correspond to inputs to the iteration strategy and are always
	 * leaf nodes as a result.
	 * 
	 * @override
	 */
	@Override
	public boolean isLeaf() {
		return true;
	}

	/**
	 * These nodes can never have children
	 * 
	 * @override
	 */
	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	/**
	 * Iteration depth is the difference between the supplied input depth and
	 * the desired one. If the desired depth is greater then wrapping will
	 * happen and the iteration depth will be zero (rather than a negative!)
	 */
	@Override
	public int getIterationDepth(Map<String, Integer> inputDepths) {
		int myInputDepth = inputDepths.get(portName);
		int depthMismatch = myInputDepth - desiredCardinality;
		return (depthMismatch > 0 ? depthMismatch : 0);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getPortName() + "("
				+ getCardinality() + ")";
	}
}
