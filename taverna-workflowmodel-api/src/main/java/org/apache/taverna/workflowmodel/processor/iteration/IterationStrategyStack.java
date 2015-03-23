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

import java.util.List;
import java.util.Map;

import org.apache.taverna.workflowmodel.WorkflowItem;

/**
 * Stack of iteration strategy containers. The stacking behaviour allows for
 * staged implicit iteration where intermediate strategies are used to drill
 * into the collection structure to a certain depth with a final one used to
 * render job objects containing data at the correct depth for the process. This
 * was achieved in Taverna 1 through the combination of nested workflows and
 * 'forcing' processors which could echo and therefore force input types of the
 * workflow to a particular cardinality.
 * 
 * @author Tom Oinn
 */
public interface IterationStrategyStack extends WorkflowItem {
	/**
	 * The iteration strategy stack consists of an ordered list of iteration
	 * strategies.
	 * 
	 * @return An unmodifiable copy of the list containing the iteration
	 *         strategy objects in order, with the strategy at position 0 in the
	 *         list being the one to which data is fed first.
	 */
	List<? extends IterationStrategy> getStrategies();

	/**
	 * Calculate the depth of the iteration strategy stack as a whole given a
	 * set of named inputs and their cardinalities. This depth is the length of
	 * the index array which will be added to any output data, so the resultant
	 * output of each port in the owning processor is the depth of that port as
	 * defined by the activity plus this value.
	 * 
	 * @param inputDepths
	 * @return
	 * @throws IterationTypeMismatchException
	 * @throws MissingIterationInputException
	 */
	int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException,
			MissingIterationInputException;
}
