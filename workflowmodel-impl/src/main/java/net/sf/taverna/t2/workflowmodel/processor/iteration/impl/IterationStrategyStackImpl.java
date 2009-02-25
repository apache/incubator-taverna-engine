/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.workflowmodel.processor.iteration.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.IterationInternalEvent;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.WorkflowStructureException;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategy;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategyStack;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationTypeMismatchException;
import net.sf.taverna.t2.workflowmodel.processor.iteration.MissingIterationInputException;
import net.sf.taverna.t2.workflowmodel.serialization.xml.XMLSerializationConstants;

import org.jdom.Element;

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

	private List<IterationStrategyImpl> strategies = new ArrayList<IterationStrategyImpl>();

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
	public int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException,
			MissingIterationInputException {
		// If there are no iteration strategies or no inputs then by definition
		// there's no iteration, no wrapping and the depth of wrapping must be
		// zero
		if (strategies.isEmpty()) {
			return 0;
		}
		if (strategies.get(0).inputs.isEmpty()) {
			return 0;
		}
		IterationStrategyImpl strategy = strategies.get(0);
		int depth = strategy.getIterationDepth(inputDepths);
		for (int index = 1; index < strategies.size(); index++) {
			// Construct the input depths for the staged iteration strategies
			// after the first one by looking at the previous iteration
			// strategy's desired cardinalities on its input ports.
			Map<String, Integer> stagedInputDepths = strategy
					.getDesiredCardinalities();
			strategy = strategies.get(index);
			depth += strategy.getIterationDepth(stagedInputDepths);
		}
		return depth;
	}

	public void addStrategy(IterationStrategy is) {
		if (is instanceof IterationStrategyImpl) {
			IterationStrategyImpl isi = (IterationStrategyImpl) is;
			strategies.add(isi);
			isi.setIterationStrategyStack(this);
		} else {
			throw new WorkflowStructureException(
					"IterationStrategyStackImpl can only hold IterationStrategyImpl objects");
		}
	}

	public List<IterationStrategyImpl> getStrategies() {
		return Collections.unmodifiableList(this.strategies);
	}

	public void clear() {
		strategies.clear();		
	}
	
	public void receiveData(String inputPortName, String owningProcess,
			int[] indexArray, T2Reference dataReference, InvocationContext context) {
		if (!strategies.isEmpty()) {
			strategies.get(0).receiveData(inputPortName, owningProcess,
					indexArray, dataReference, context);
		}
	}

	public void receiveCompletion(String inputPortName, String owningProcess,
			int[] completionArray, InvocationContext context) {
		if (!strategies.isEmpty()) {
			strategies.get(0).receiveCompletion(inputPortName, owningProcess,
					completionArray, context);
		}
	}

	public Element asXML() {
		Element strategyStackElement = new Element("iteration",XMLSerializationConstants.T2_WORKFLOW_NAMESPACE);
		for (IterationStrategyImpl is : strategies) {
			strategyStackElement.addContent(is.asXML());
		}
		return strategyStackElement;
	}

	public void configureFromElement(Element e) {
		strategies.clear();
		for (Object child : e.getChildren("strategy",XMLSerializationConstants.T2_WORKFLOW_NAMESPACE)) {
			Element strategyElement = (Element) child;
			IterationStrategyImpl strategy = new IterationStrategyImpl();
			strategy.configureFromXML(strategyElement);
			addStrategy(strategy);
		}
	}

	/**
	 * Return the layer below the specified one, or null if there is no lower
	 * layer
	 * 
	 * @return
	 */
	protected IterationStrategyImpl layerBelow(IterationStrategyImpl that) {
		int index = strategies.indexOf(that);
		if (index == (strategies.size() - 1)) {
			return null;
		} else {
			return strategies.get(index + 1);
		}
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
