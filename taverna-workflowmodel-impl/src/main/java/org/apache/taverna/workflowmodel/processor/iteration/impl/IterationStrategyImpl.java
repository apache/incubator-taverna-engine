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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.invocation.Completion;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.IterationInternalEvent;
import org.apache.taverna.reference.ContextualizedT2Reference;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.WorkflowStructureException;
import org.apache.taverna.workflowmodel.processor.activity.Job;
import org.apache.taverna.workflowmodel.processor.iteration.AbstractIterationStrategyNode;
import org.apache.taverna.workflowmodel.processor.iteration.CrossProduct;
import org.apache.taverna.workflowmodel.processor.iteration.DotProduct;
import org.apache.taverna.workflowmodel.processor.iteration.IterationStrategy;
import org.apache.taverna.workflowmodel.processor.iteration.IterationTypeMismatchException;
import org.apache.taverna.workflowmodel.processor.iteration.NamedInputPortNode;
import org.apache.taverna.workflowmodel.processor.iteration.PrefixDotProduct;
import org.apache.taverna.workflowmodel.processor.iteration.TerminalNode;
import org.jdom2.Element;

/**
 * A single layer of iteration strategy, consuming individual named inputs and
 * combining these into Job objects to be consumed by the dispatch stack
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 * 
 */
public class IterationStrategyImpl implements IterationStrategy {
	Set<NamedInputPortNode> inputs;
	private boolean wrapping = false;
	protected IterationStrategyStackImpl stack = null;
	private TerminalNodeImpl terminal = new TerminalNodeImpl();

	private void pushEvent(IterationInternalEvent<?> e) {
		if (stack == null)
			return;
		IterationStrategyImpl below = stack
				.layerBelow(IterationStrategyImpl.this);
		if (below == null)
			stack.receiveEventFromStrategy(e);
		else
			below.receiveEvent(e);
	}

	/**
	 * The terminal node is used internally as the root of the iteration
	 * strategy tree, it is responsible for forwarding all events up to the
	 * iteration strategy itself which can then propogate them to the strategy
	 * stack.
	 */
	@SuppressWarnings("serial")
	public class TerminalNodeImpl extends TerminalNode {
		private IterationInternalEvent<?> unwrap(IterationInternalEvent<?> completion) {
			return (wrapping ? completion.popIndex() : completion);
		}

		@Override
		public void receiveCompletion(int inputIndex, Completion completion) {
			pushEvent(unwrap(completion));
		}

		@Override
		public void receiveJob(int inputIndex, Job newJob) {
			pushEvent(unwrap(newJob));
		}

		public void receiveBypassCompletion(Completion completion) {
			pushEvent(completion);
		}

		@Override
		public int getIterationDepth(Map<String, Integer> inputDepths)
				throws IterationTypeMismatchException {
			if (getChildCount() == 0)
				return -1;
			return getChildAt(0).getIterationDepth(inputDepths);
		}
	}

	public IterationStrategyImpl() {
		inputs = new HashSet<>();
	}

	@Override
	public TerminalNode getTerminalNode() {
		return terminal;
	}

	/**
	 * Configure from an XML element
	 * 
	 * @param strategyElement
	 */
	@SuppressWarnings("unchecked")
	protected void configureFromXML(Element strategyElement) {
		inputs.clear();
		terminal.clear();
		if (!strategyElement.getChildren().isEmpty())
			for (Element childElement : (List<Element>) strategyElement
					.getChildren())
				nodeForElement(childElement).setParent(terminal);
	}

	private AbstractIterationStrategyNode nodeForElement(Element e) {
		AbstractIterationStrategyNode node = null;
		String eName = e.getName();
		if (eName.equals("dot"))
			node = new DotProduct();
		else if (eName.equals("cross"))
			node = new CrossProduct();
		else if (eName.equals("prefix"))
			node = new PrefixDotProduct();
		else if (eName.equals("port")) {
			NamedInputPortNode nipn = new NamedInputPortNode(
					e.getAttributeValue("name"), Integer.parseInt(e
							.getAttributeValue("depth")));
			node = nipn;
			addInput(nipn);
		}
		for (Object child : e.getChildren())
			nodeForElement((Element) child).setParent(node);
		return node;
	}

	/**
	 * Receive a single job from an upstream IterationStrategyImpl in the stack.
	 * This job will have one or more data parts where the cardinality doesn't
	 * match that defined by the NamedInputPortNode and will need to be split up
	 * appropriately
	 * 
	 * @param j
	 */
	protected void receiveEvent(IterationInternalEvent<?> e) {
		/*
		 * If we ever get this method called we know we're not the top layer in
		 * the dispatch stack and that we need to perform wrap / unwrap of data
		 * as it comes in. This boolean flag informs the behaviour of the
		 * terminal node in the strategy.
		 */
		wrapping = true;
		/*
		 * If this is a Job object then we'll need to split it up and push it
		 * through the iteration system to get multiple child jobs followed by a
		 * completion event otherwise we can just push the completion event all
		 * the way through the system.
		 */
		if (e instanceof Job) {
			Job j = ((Job) e).pushIndex();
			// Now have to split this job up into a number of distinct events!
			String owningProcess = j.getOwningProcess();
			for (String portName : j.getData().keySet()) {
				T2Reference dataRef = j.getData().get(portName);
				ReferenceService rs = e.getContext().getReferenceService();
				NamedInputPortNode ipn = nodeForName(portName);
				int desiredDepth = ipn.getCardinality();
				Iterator<ContextualizedT2Reference> ids = rs.traverseFrom(
						dataRef, desiredDepth);
				while (ids.hasNext()) {
					ContextualizedT2Reference ci = ids.next();
					int[] indexArray = ci.getIndex();
					T2Reference childDataRef = ci.getReference();
					receiveData(portName, owningProcess, indexArray,
							childDataRef, e.getContext());
				}
				receiveCompletion(portName, owningProcess, new int[] {}, e
						.getContext());
			}
		} else {
			/*
			 * Event was a completion event, push it through unmodified to the
			 * terminal node. Intermediate completion events from the split of
			 * an input Job into multiple events through data structure
			 * traversal are unwrapped but as this one is never wrapped in the
			 * first place we need a way to mark it as such, the call to the
			 * bypass method achieves this
			 */
			terminal.receiveBypassCompletion((Completion) e);
		}
	}

	/**
	 * Receive a single data event from an upstream process. This method is only
	 * ever called on the first layer in the IterationStrategyStackImpl, other
	 * layers are passed entire Job objects
	 * 
	 * @param inputPortName
	 * @param owningProcess
	 * @param indexArray
	 * @param dataReference
	 * @throws WorkflowStructureException
	 */
	public void receiveData(String inputPortName, String owningProcess,
			int[] indexArray, T2Reference dataReference,
			InvocationContext context) throws WorkflowStructureException {
		Map<String, T2Reference> dataMap = new HashMap<>();
		dataMap.put(inputPortName, dataReference);
		Job newJob = new Job(owningProcess, indexArray, dataMap, context);
		nodeForName(inputPortName).receiveJob(0, newJob);
	}

	public void receiveCompletion(String inputPortName, String owningProcess,
			int[] completionArray, InvocationContext context)
			throws WorkflowStructureException {
		nodeForName(inputPortName).receiveCompletion(0,
				new Completion(owningProcess, completionArray, context));
	}

	public void addInput(NamedInputPortNode nipn) {
		synchronized (inputs) {
			inputs.add(nipn);
		}
	}

	public void removeInput(NamedInputPortNode nipn) {
		synchronized (inputs) {
			inputs.remove(nipn);
		}
	}

	public void removeInputByName(String name) {
		synchronized (inputs) {
			NamedInputPortNode removeMe = null;
			for (NamedInputPortNode nipn : inputs)
				if (nipn.getPortName().equals(name))
					removeMe = nipn;
			if (removeMe != null) {
				inputs.remove(removeMe);
				removeMe.removeFromParent();
			}
		}
	}

	private NamedInputPortNode nodeForName(String portName)
			throws WorkflowStructureException {
		for (NamedInputPortNode node : inputs)
			if (node.getPortName().equals(portName))
				return node;
		throw new WorkflowStructureException("No port found with name '"
				+ portName + "'");
	}

	public void setIterationStrategyStack(IterationStrategyStackImpl stack) {
		this.stack = stack;
	}

	/**
	 * Connect up a new named input port node to the first child of the terminal
	 * node. If the terminal node doesn't have any children then create a new
	 * cross product node, connect it to the terminal and connect the new input
	 * port node to the cross product (saneish default behaviour)
	 * 
	 * @param nipn
	 */
	public synchronized void connectDefault(NamedInputPortNode nipn) {
		if (terminal.getChildCount() == 0) {
			CrossProduct cp = new CrossProduct();
			cp.setParent(terminal);
			nipn.setParent(cp);
		} else
			nipn.setParent((AbstractIterationStrategyNode) terminal
					.getChildAt(0));
	}

	@Override
	public int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException {
		return getTerminalNode().getIterationDepth(inputDepths);
	}

	@Override
	public Map<String, Integer> getDesiredCardinalities() {
		Map<String, Integer> result = new HashMap<>();
		for (NamedInputPortNode nipn : inputs)
			result.put(nipn.getPortName(), nipn.getCardinality());
		return result;
	}

	@Override
	public void normalize() {
		boolean finished = false;
		do {
			finished = true;
			@SuppressWarnings("unchecked")
			Enumeration<AbstractIterationStrategyNode> e = getTerminalNode()
					.breadthFirstEnumeration();
			while (e.hasMoreElements() && finished == true) {
				AbstractIterationStrategyNode n = e.nextElement();
				AbstractIterationStrategyNode parent = (AbstractIterationStrategyNode) n
						.getParent();
				// Check whether anything needs doing

				// Check for collation nodes with no children
				if (!(n instanceof NamedInputPortNode) && parent != null
						&& n.getChildCount() == 0) {
					// Remove the node from its parent and set finished to false
					parent.remove(n);
					finished = false;
				} else if (!(n.isLeaf()) && parent != null
						&& n.getChildCount() == 1) {
					/*
					 * Is a collation node with a single child, and therefore
					 * pointless. Replace it with the child node
					 */
					AbstractIterationStrategyNode child = (AbstractIterationStrategyNode) n
							.getChildAt(0);
					// Find the index of the collation node in its parent
					int oldIndex = parent.getIndex(n);
					parent.remove(n);
					parent.insert(child, oldIndex);
					finished = false;
				}
			}
		} while (!finished);
	}
}
