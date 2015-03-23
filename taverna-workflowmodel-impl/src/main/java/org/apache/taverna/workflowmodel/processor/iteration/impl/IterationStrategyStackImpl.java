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

package org.apache.taverna.workflowmodel.processor.iteration.impl;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.IterationInternalEvent;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.WorkflowStructureException;
import org.apache.taverna.workflowmodel.processor.iteration.IterationStrategy;
import org.apache.taverna.workflowmodel.processor.iteration.IterationStrategyStack;
import org.apache.taverna.workflowmodel.processor.iteration.IterationTypeMismatchException;
import org.apache.taverna.workflowmodel.processor.iteration.MissingIterationInputException;

/**
 * Contains an ordered list of IterationStrategyImpl objects. The top of the
 * list is fed data directly, all other nodes are fed complete Job objects and
 * Completion events from the layer above. The bottom layer pushes events onto
 * the processor event queue to be consumed by the dispatch stack.
 * 
 * @author Tom Oinn
 * 
 */
public class IterationStrategyStackImpl implements IterationStrategyStack {
	private List<IterationStrategyImpl> strategies = new ArrayList<>();

	/**
	 * The iteration depth here is calculated by taking first the top iteration
	 * strategy and applying the actual input types to it, then for each
	 * subsequent strategy in the stack using the 'desired cardinality' of the
	 * input nodes for each layer to work out the increase in index array
	 * length.
	 * 
	 * @param inputDepths
	 * @return
	 * @throws IterationTypeMismatchException
	 */
	@Override
	public int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException,
			MissingIterationInputException {
		/*
		 * If there are no iteration strategies or no inputs then by definition
		 * there's no iteration, no wrapping and the depth of wrapping must be
		 * zero
		 */
		if (strategies.isEmpty())
			return 0;
		if (strategies.get(0).inputs.isEmpty())
			return 0;

		IterationStrategyImpl strategy = strategies.get(0);
		int depth = strategy.getIterationDepth(inputDepths);
		for (int index = 1; index < strategies.size(); index++) {
			/*
			 * Construct the input depths for the staged iteration strategies
			 * after the first one by looking at the previous iteration
			 * strategy's desired cardinalities on its input ports.
			 */
			Map<String, Integer> stagedInputDepths = strategy
					.getDesiredCardinalities();
			strategy = strategies.get(index);
			depth += strategy.getIterationDepth(stagedInputDepths);
		}
		return depth;
	}

	public void addStrategy(IterationStrategy is) {
		if (!(is instanceof IterationStrategyImpl))
			throw new WorkflowStructureException(
					"IterationStrategyStackImpl can only hold IterationStrategyImpl objects");
		IterationStrategyImpl isi = (IterationStrategyImpl) is;
		strategies.add(isi);
		isi.setIterationStrategyStack(this);
	}

	public void removeStrategy(IterationStrategy is) {
		if (!(is instanceof IterationStrategyImpl))
			throw new WorkflowStructureException(
					"IterationStrategyStackImpl can only hold IterationStrategyImpl objects");
		IterationStrategyImpl isi = (IterationStrategyImpl) is;
		strategies.remove(isi);
		isi.setIterationStrategyStack(null);
	}

	@Override
	public List<IterationStrategyImpl> getStrategies() {
		return unmodifiableList(this.strategies);
	}

	public void clear() {
		strategies.clear();		
	}
	
	public void receiveData(String inputPortName, String owningProcess,
			int[] indexArray, T2Reference dataReference, InvocationContext context) {
		if (!strategies.isEmpty())
			strategies.get(0).receiveData(inputPortName, owningProcess,
					indexArray, dataReference, context);
	}

	public void receiveCompletion(String inputPortName, String owningProcess,
			int[] completionArray, InvocationContext context) {
		if (!strategies.isEmpty())
			strategies.get(0).receiveCompletion(inputPortName, owningProcess,
					completionArray, context);
	}

	/**
	 * Return the layer below the specified one, or null if there is no lower
	 * layer
	 * 
	 * @return
	 */
	protected IterationStrategyImpl layerBelow(IterationStrategyImpl that) {
		int index = strategies.indexOf(that) + 1;
		if (index == strategies.size())
			return null;
		return strategies.get(index);
	}

	/**
	 * Called by the final iteration strategy to push events onto the
	 * dispatcher's queue
	 * 
	 * @param e
	 */
	protected void receiveEventFromStrategy(IterationInternalEvent<? extends IterationInternalEvent<?>> e) {
		// TODO - push events onto dispatch queue
	}
}
