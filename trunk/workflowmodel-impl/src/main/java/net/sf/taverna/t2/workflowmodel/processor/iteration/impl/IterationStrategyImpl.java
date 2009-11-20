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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.TreeNode;

import net.sf.taverna.t2.invocation.Completion;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.IterationInternalEvent;
import net.sf.taverna.t2.reference.ContextualizedT2Reference;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.WorkflowStructureException;
import net.sf.taverna.t2.workflowmodel.processor.activity.Job;
import net.sf.taverna.t2.workflowmodel.processor.iteration.AbstractIterationStrategyNode;
import net.sf.taverna.t2.workflowmodel.processor.iteration.CrossProduct;
import net.sf.taverna.t2.workflowmodel.processor.iteration.DotProduct;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationStrategy;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationTypeMismatchException;
import net.sf.taverna.t2.workflowmodel.processor.iteration.NamedInputPortNode;
import net.sf.taverna.t2.workflowmodel.processor.iteration.PrefixDotProduct;
import net.sf.taverna.t2.workflowmodel.processor.iteration.TerminalNode;
import net.sf.taverna.t2.workflowmodel.serialization.xml.XMLSerializationConstants;

import org.jdom.Element;

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

	/**
	 * The terminal node is used internally as the root of the iteration
	 * strategy tree, it is responsible for forwarding all events up to the
	 * iteration strategy itself which can then propogate them to the strategy
	 * stack.
	 */
	public class TerminalNodeImpl extends TerminalNode {

		public void receiveCompletion(int inputIndex, Completion completion) {
			if (wrapping) {
				pushEvent(completion.popIndex());
			} else {
				pushEvent(completion);
			}

		}

		public void receiveJob(int inputIndex, Job newJob) {
			if (wrapping) {
				pushEvent(newJob.popIndex());
			} else {
				pushEvent(newJob);
			}
		}

		public void receiveBypassCompletion(Completion completion) {
			pushEvent(completion);
		}

		private void pushEvent(
				IterationInternalEvent<? extends IterationInternalEvent<?>> e) {
			if (stack != null) {
				IterationStrategyImpl below = stack
						.layerBelow(IterationStrategyImpl.this);
				if (below == null) {
					stack.receiveEventFromStrategy(e);
				} else {
					below.receiveEvent(e);
				}
			}
		}

		public int getIterationDepth(Map<String, Integer> inputDepths)
				throws IterationTypeMismatchException {
			if (getChildCount() == 0) {
				return -1;
			} else {
				return getChildAt(0).getIterationDepth(inputDepths);
			}
		}

	}

	public IterationStrategyImpl() {
		inputs = new HashSet<NamedInputPortNode>();
	}

	public TerminalNode getTerminalNode() {
		return terminal;
	}

	/**
	 * Get the XML element defining the state of this iteration strategy
	 * 
	 * @return
	 */
	protected Element asXML() {
		Element strategyElement = new Element("strategy",
				XMLSerializationConstants.T2_WORKFLOW_NAMESPACE);
		if (terminal.getChildCount() > 0) {
			AbstractIterationStrategyNode node = (AbstractIterationStrategyNode) (terminal
					.getChildAt(0));
			strategyElement.addContent(elementForNode(node));
		}
		return strategyElement;
	}

	private static Element elementForNode(AbstractIterationStrategyNode node) {
		Element nodeElement = null;
		if (node instanceof DotProduct) {
			nodeElement = new Element("dot",
					XMLSerializationConstants.T2_WORKFLOW_NAMESPACE);
		} else if (node instanceof CrossProduct) {
			nodeElement = new Element("cross",
					XMLSerializationConstants.T2_WORKFLOW_NAMESPACE);
		} else if (node instanceof PrefixDotProduct) {
			nodeElement = new Element("prefix",
					XMLSerializationConstants.T2_WORKFLOW_NAMESPACE);
		} else if (node instanceof NamedInputPortNode) {
			NamedInputPortNode nipn = (NamedInputPortNode) node;
			nodeElement = new Element("port",
					XMLSerializationConstants.T2_WORKFLOW_NAMESPACE);
			nodeElement.setAttribute("name", nipn.getPortName());
			nodeElement.setAttribute("depth", nipn.getCardinality() + "");
		} else {
			throw new IllegalArgumentException("Unknown node " + node);
		}
		Enumeration<?> children = node.children();
		while (children.hasMoreElements()) {
			TreeNode tn = (TreeNode) children.nextElement();
			nodeElement
					.addContent(elementForNode((AbstractIterationStrategyNode) tn));
		}
		return nodeElement;
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
		if (!strategyElement.getChildren().isEmpty()) {
			for (Element childElement : (List<Element>) strategyElement
					.getChildren()) {
				AbstractIterationStrategyNode node = nodeForElement(childElement);
				node.setParent(terminal);
			}
		}
	}

	private AbstractIterationStrategyNode nodeForElement(Element e) {
		AbstractIterationStrategyNode node = null;
		String eName = e.getName();
		if (eName.equals("dot")) {
			node = new DotProduct();
		} else if (eName.equals("cross")) {
			node = new CrossProduct();
		} else if (eName.equals("prefix")) {
			node = new PrefixDotProduct();
		} else if (eName.equals("port")) {
			String portName = e.getAttributeValue("name");
			int portDepth = Integer.parseInt(e.getAttributeValue("depth"));
			node = new NamedInputPortNode(portName, portDepth);
			addInput((NamedInputPortNode) node);
		}
		for (Object child : e.getChildren()) {
			Element childElement = (Element) child;
			nodeForElement(childElement).setParent(node);
		}
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
	@SuppressWarnings("unchecked")
	// suppressed to avoid jdk1.5 compilation errors caused by the declaration
	// IterationInternalEvent<? extends IterationInternalEvent<?>> e
	protected void receiveEvent(IterationInternalEvent e) {
		// If we ever get this method called we know we're not the top layer in
		// the dispatch stack and that we need to perform wrap / unwrap of data
		// as it comes in. This boolean flag informs the behaviour of the
		// terminal
		// node in the strategy.
		wrapping = true;
		// If this is a Job object then we'll need to split it up and push it
		// through the iteration system to get multiple child jobs followed by a
		// completion event otherwise we can just push the completion event all
		// the way through the system.
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
		}
		// Event was a completion event, push it through unmodified to the
		// terminal node. Intermediate completion events from the split of an
		// input Job into multiple events through data structure traversal are
		// unwrapped but as this one is never wrapped in the first place we need
		// a way to mark it as such, the call to the bypass method achieves this
		else {
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
		Map<String, T2Reference> dataMap = new HashMap<String, T2Reference>();
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
			this.inputs.add(nipn);
		}
	}

	public void removeInput(NamedInputPortNode nipn) {
		synchronized (inputs) {
			this.inputs.remove(nipn);
		}
	}

	public void removeInputByName(String name) {
		synchronized (inputs) {
			NamedInputPortNode removeMe = null;
			for (NamedInputPortNode nipn : inputs) {
				if (nipn.getPortName().equals(name)) {
					removeMe = nipn;
				}
			}
			if (removeMe != null) {
				this.inputs.remove(removeMe);
				removeMe.removeFromParent();
			}
		}
	}

	private NamedInputPortNode nodeForName(String portName)
			throws WorkflowStructureException {
		for (NamedInputPortNode node : inputs) {
			if (node.getPortName().equals(portName)) {
				return node;
			}
		}
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
		} else {
			AbstractIterationStrategyNode node = (AbstractIterationStrategyNode) (terminal
					.getChildAt(0));
			nipn.setParent(node);
		}
	}

	public int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException {
		return getTerminalNode().getIterationDepth(inputDepths);
	}

	public Map<String, Integer> getDesiredCardinalities() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		for (NamedInputPortNode nipn : inputs) {
			result.put(nipn.getPortName(), nipn.getCardinality());
		}
		return result;
	}

}
