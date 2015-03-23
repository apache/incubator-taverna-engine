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

import org.apache.taverna.workflowmodel.WorkflowItem;

public interface IterationStrategy extends WorkflowItem {
	/**
	 * The iteration strategy results in a set of job objects with a particular
	 * job index. This method returns the length of that index array when the
	 * specified input types are used. Input types are defined in terms of name
	 * and integer pairs where the name is the name of a NamedInputPortNode in
	 * the iteration strategy and the integer is the depth of the input data
	 * collection (i.e. item depth + index array length for that item which
	 * should be a constant).
	 * 
	 * @param inputDepths
	 *            map of port names to input collection depth
	 * @return the length of the index array which will be generated for each
	 *         resultant job object.
	 */
	int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException;

	/**
	 * Return a map of port name -> desired cardinality for this iteration
	 * strategy
	 */
	Map<String, Integer> getDesiredCardinalities();

	TerminalNode getTerminalNode();

	void normalize();
}
