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

package org.apache.taverna.workflowmodel.impl;

import static java.util.Collections.unmodifiableList;
import static org.apache.taverna.workflowmodel.utils.Tools.addDataflowIdentification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.taverna.annotation.AbstractAnnotatedThing;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.monitor.MonitorManager;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.DataflowValidationReport;
import org.apache.taverna.workflowmodel.Datalink;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.EventHandlingInputPort;
import org.apache.taverna.workflowmodel.FailureTransmitter;
import org.apache.taverna.workflowmodel.InvalidDataflowException;
import org.apache.taverna.workflowmodel.Merge;
import org.apache.taverna.workflowmodel.NamedWorkflowEntity;
import org.apache.taverna.workflowmodel.NamingException;
import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.TokenProcessingEntity;
import org.apache.taverna.workflowmodel.processor.iteration.IterationTypeMismatchException;

/**
 * Implementation of Dataflow including implementation of the dataflow level
 * type checker. Other than this the implementation is fairly simple as it's
 * effectively just a container for other things especially the dataflow input
 * and output port implementations.
 * 
 * @author Tom Oinn
 * 
 */
public class DataflowImpl extends AbstractAnnotatedThing<Dataflow> implements
		Dataflow {
	List<ProcessorImpl> processors;
	List<MergeImpl> merges;
	private String name;
	private static int nameIndex = 1;
	private List<DataflowInputPortImpl> inputs;
	private List<DataflowOutputPortImpl> outputs;
	protected String internalIdentifier;
	private DataflowValidationReport validationReport;

    /**
	 * Protected constructor, assigns a default name. To build an instance of
	 * DataflowImpl you should use the appropriate Edit object from the Edits
	 * interface
	 */
	protected DataflowImpl() {
		this.name = "Workflow" + (nameIndex++);
		this.processors = new ArrayList<ProcessorImpl>();
		this.merges = new ArrayList<MergeImpl>();
		this.inputs = new ArrayList<DataflowInputPortImpl>();
		this.outputs = new ArrayList<DataflowOutputPortImpl>();
		refreshInternalIdentifier();
	}

	/**
	 * Adds a processor on the DataFlow.
	 * 
	 * @param processor
	 *            the ProcessorImpl to be added to the Dataflow
	 * @return
	 * @throws NamingException
	 *             if a processor already exists with the same local name
	 */
	protected synchronized void addProcessor(ProcessorImpl processor)
			throws NamingException {
		for (Processor existingProcessor : new ArrayList<>(processors))
			if (existingProcessor.getLocalName().equals(
					processor.getLocalName()))
				throw new NamingException("There already is a processor named:"
						+ processor.getLocalName());
		processors.add(processor);
	}

	protected synchronized void removeProcessor(Processor processor) {
		processors.remove(processor);
	}

	/**
	 * Adds a processor on the DataFlow.
	 * 
	 * @param processor
	 *            the ProcessorImpl to be added to the Dataflow
	 * @return
	 * @throws NamingException
	 *             if a processor already exists with the same local name
	 */
	protected synchronized void addMerge(MergeImpl merge)
			throws NamingException {
		for (Merge existingMerge : new ArrayList<>(merges))
			if (existingMerge.getLocalName().equals(merge.getLocalName()))
				throw new NamingException(
						"There already is a merge operation named:"
								+ merge.getLocalName());
		merges.add(merge);
	}

	protected synchronized void removeMerge(Merge merge) {
		merges.remove(merge);
	}

	/**
	 * Build a new dataflow input port, the granular depth is set for the input
	 * port so it can be copied onto the internal output port
	 * 
	 * @param name
	 *            name of the dataflow input port to build
	 * @param depth
	 *            input depth
	 * @param granularDepth
	 *            granular depth to copy to the internal output port
	 * @throws NamingException
	 *             in the event of a duplicate or invalid name
	 * @return the newly created input port
	 */
	protected synchronized DataflowInputPort createInputPort(String name,
			int depth, int granularDepth) throws NamingException {
		for (DataflowInputPort dip : inputs)
			if (dip.getName().equals(name))
				throw new NamingException(
						"Duplicate workflow input port name '" + name
								+ "' in workflow already.");
		DataflowInputPortImpl dipi = new DataflowInputPortImpl(name, depth,
				granularDepth, this);
		inputs.add(dipi);
		return dipi;
	}

	/**
	 * Adds an input port to the DataFlow.
	 * 
	 * @param inputPort
	 *            the DataflowInputPortImpl to be added to the Dataflow
	 * @throws EditException
	 */
	protected synchronized void addInputPort(DataflowInputPortImpl inputPort)
			throws EditException {
		for (DataflowInputPort existingInputPort : new ArrayList<>(inputs))
			if (existingInputPort.getName().equals(inputPort.getName()))
				throw new NamingException(
						"There already is a workflow input port named:"
								+ inputPort.getName());
		if (inputPort.getDataflow() != this)
			throw new EditException("Port specifies a different workflow");
		inputs.add(inputPort);
	}

	/**
	 * Remove the named dataflow input port
	 * 
	 * @param name
	 *            name of the dataflow input port to remove
	 * @throws EditException
	 *             if the specified port doesn't exist within this dataflow
	 */
	protected synchronized void removeDataflowInputPort(String name)
			throws EditException {
		for (DataflowInputPort dip : inputs)
			if (dip.getName().equals(name)) {
				removeDataflowInputPort(dip);
				return;
			}
		throw new EditException("No such input port '" + name
				+ "' in workflow.");
	}

	/**
	 * Remove the specified input port from this dataflow
	 * 
	 * @param dip
	 *            dataflow input port to remove
	 * @throws EditException
	 *             if the input port isn't in the list of inputs - should never
	 *             happen but you never know.
	 */
	protected synchronized void removeDataflowInputPort(DataflowInputPort dip)
			throws EditException {
		if (!inputs.contains(dip))
			throw new EditException(
					"Can't locate the specified input port in workflow. Input port has name '"
							+ dip.getName() + "'.");
		inputs.remove(dip);
	}

	/**
	 * Create and return a new DataflowOutputPort in this dataflow
	 * 
	 * @param name
	 *            name of the port to create, must be unique within the set of
	 *            output ports for this dataflow
	 * @return the newly created DataflowOutputPort
	 * @throws NamingException
	 *             if the name is invalid or already exists as a name for a
	 *             dataflow output
	 */
	protected synchronized DataflowOutputPort createOutputPort(String name)
			throws NamingException {
		for (DataflowOutputPort dop : outputs)
			if (dop.getName().equals(name))
				throw new NamingException(
						"Duplicate workflow output port name '" + name
								+ "' in workflow already.");
		DataflowOutputPortImpl dopi = new DataflowOutputPortImpl(name, this);
		outputs.add(dopi);
		return dopi;
	}

	/**
	 * Adds an output port to the DataFlow.
	 * 
	 * @param outputPort
	 *            the DataflowOutputPortImpl to be added to the Dataflow
	 * @throws EditException
	 */
	protected synchronized void addOutputPort(DataflowOutputPortImpl outputPort)
			throws EditException {
		for (DataflowOutputPort existingOutputPort : new ArrayList<>(outputs))
			if (existingOutputPort.getName().equals(outputPort.getName()))
				throw new NamingException(
						"There already is a workflow output port named:"
								+ outputPort.getName());
		if (outputPort.getDataflow() != this)
			throw new EditException("Port specifies a different workflow");
		outputs.add(outputPort);
	}

	/**
	 * Remove the named dataflow output port
	 * 
	 * @param name
	 *            name of the dataflow output port to remove
	 * @throws EditException
	 *             if the specified port doesn't exist within this dataflow
	 */
	protected synchronized void removeDataflowOutputPort(String name)
			throws EditException {
		for (DataflowOutputPort dop : outputs)
			if (dop.getName().equals(name)) {
				removeDataflowOutputPort(dop);
				return;
			}
		throw new EditException("No such output port '" + name
				+ "' in workflow.");
	}

	/**
	 * Remove the specified output port from this dataflow
	 * 
	 * @param dop
	 *            dataflow output port to remove
	 * @throws EditException
	 *             if the output port isn't in the list of outputs for this
	 *             dataflow
	 */
	protected synchronized void removeDataflowOutputPort(DataflowOutputPort dop)
			throws EditException {
		if (!outputs.contains(dop))
			throw new EditException(
					"Can't locate the specified output port in workflow, output port has name '"
							+ dop.getName() + "'.");
		outputs.remove(dop);
	}

	/**
	 * Create a new datalink between two entities within the workflow
	 * 
	 * @param sourceName
	 *            interpreted either as the literal name of a dataflow input
	 *            port or the colon seperated name of a
	 *            [processorName|mergeName]:[outputPort]
	 * @param sinkName
	 *            as with sourceName but for processor or merge input ports and
	 *            dataflow output ports
	 * @return the created Datalink
	 * @throws EditException
	 *             if either source or sink isn't found within this dataflow or
	 *             if the link would violate workflow structural constraints in
	 *             an immediate way. This won't catch cycles (see the validation
	 *             methods for that) but will prevent you from having more than
	 *             one link going to an input port.
	 */
	protected synchronized Datalink link(String sourceName, String sinkName)
			throws EditException {
		BasicEventForwardingOutputPort source = findSourcePort(sourceName);
		EventHandlingInputPort sink = findSinkPort(sinkName);

		// Check whether the sink is already linked
		if (sink.getIncomingLink() != null)
			throw new EditException("Cannot link to sink port '" + sinkName
					+ "' as it is already linked");

		/*
		 * Got here so we have both source and sink and the sink isn't already
		 * linked from somewhere. If the sink isn't linked we can't have a
		 * duplicate link here which would have been the other condition to
		 * check for.
		 */

		DatalinkImpl link = new DatalinkImpl(source, sink);
		source.addOutgoingLink(link);
		((AbstractEventHandlingInputPort) sink).setIncomingLink(link);

		return link;
	}

	/* @nonnull */
	private BasicEventForwardingOutputPort findSourcePort(String sourceName)
			throws EditException {
		BasicEventForwardingOutputPort source = null;
		String[] split = sourceName.split(":");
		if (split.length == 2) {
			/* source is a processor */
			// TODO - update to include Merge when it's added
			for (ProcessorImpl pi : processors)
				if (pi.getLocalName().equals(split[0])) {
					source = pi.getOutputPortWithName(split[1]);
					break;
				}
		} else if (split.length == 1) {
			/*
			 * source is a workflow input port, or at least the internal output
			 * port within it
			 */
			for (DataflowInputPortImpl dipi : inputs)
				if (dipi.getName().equals(split[0])) {
					source = dipi.internalOutput;
					break;
				}
		} else
			throw new EditException("Invalid source link name '" + sourceName
					+ "'.");
		if (source == null)
			throw new EditException("Unable to find source port named '"
					+ sourceName + "' in link creation.");
		return source;
	}

	/* @nonnull */
	private EventHandlingInputPort findSinkPort(String sinkName)
			throws EditException {
		EventHandlingInputPort sink = null;
		String[] split;
		split = sinkName.split(":");
		if (split.length == 2) {
			/* sink is a processor */
			// TODO - update to include Merge when it's added
			for (ProcessorImpl pi : processors)
				if (pi.getLocalName().equals(split[0])) {
					sink = pi.getInputPortWithName(split[1]);
					break;
				}
		} else if (split.length == 1) {
			/*
			 * source is a workflow input port, or at least the internal output
			 * port within it
			 */
			for (DataflowOutputPortImpl dopi : outputs)
				if (dopi.getName().equals(split[0])) {
					sink = dopi.getInternalInputPort();
					break;
				}
		} else
			throw new EditException("Invalid link sink name '" + sinkName
					+ "'.");
		if (sink == null)
			throw new EditException("Unable to find sink port named '"
					+ sinkName + "' in link creation");
		return sink;
	}
	
	/**
	 * Return a copy of the list of dataflow input ports for this dataflow
	 */
	@Override
	public synchronized List<? extends DataflowInputPort> getInputPorts() {
		return unmodifiableList(inputs);
	}

	/**
	 * For each processor input, merge input and workflow output get the
	 * incoming link and, if non null, add to a list and return the entire list.
	 */
	@Override
	public synchronized List<? extends Datalink> getLinks() {
		List<Datalink> result = new ArrayList<>();
		/*
		 * All processors have a set of input ports each of which has at most
		 * one incoming data link
		 */
		for (TokenProcessingEntity p : getEntities(TokenProcessingEntity.class))
			for (EventHandlingInputPort pip : p.getInputPorts()) {
				Datalink dl = pip.getIncomingLink();
				if (dl != null)
					result.add(dl);
			}
		/*
		 * Workflow outputs have zero or one incoming data link to their
		 * internal input port
		 */
		for (DataflowOutputPort dop : getOutputPorts()) {
			Datalink dl = dop.getInternalInputPort().getIncomingLink();
			if (dl != null)
				result.add(dl);
		}

		return result;
	}

	/**
	 * Return the list of all processors within the dataflow
	 */
	@Override
	public synchronized List<? extends Processor> getProcessors() {
		return getEntities(Processor.class);
	}

	/**
	 * Return the list of all merge operations within the dataflow
	 */
	@Override
	public synchronized List<? extends Merge> getMerges() {
		return getEntities(Merge.class);
	}

	/**
	 * Return all dataflow output ports
	 */
	@Override
	public synchronized List<? extends DataflowOutputPort> getOutputPorts() {
		return unmodifiableList(outputs);
	}

	/**
	 * Return the local name of this workflow
	 */
	@Override
	public String getLocalName() {
		return this.name;
	}

	/**
	 * Run the type check algorithm and return a report on any problems found.
	 * This method must be called prior to actually pushing data through the
	 * dataflow as it sets various properties as a side effect.
	 * 
	 * If the workflow has been set immutable with {@link #setImmutable()},
	 * subsequent calls to this method will return the cached
	 * DataflowValidationReport.
	 * 
	 */
	@Override
	public DataflowValidationReport checkValidity() {
		if (!immutable)
			// Don't store it!
			return checkValidityImpl();
		if (validationReport == null)
			validationReport = checkValidityImpl();
		return validationReport;
	}

	/**
	 * Works out whether a dataflow is valid. <strong>This includes working out
	 * the real depths of output ports.</strong>
	 */
	public synchronized DataflowValidationReport checkValidityImpl() {
		// First things first - nullify the resolved depths in all datalinks
		for (Datalink dl : getLinks())
			if (dl instanceof DatalinkImpl)
				((DatalinkImpl) dl).setResolvedDepth(-1);
		// Now copy type information from workflow inputs
		for (DataflowInputPort dip : getInputPorts())
			for (Datalink dl : dip.getInternalOutputPort().getOutgoingLinks())
				if (dl instanceof DatalinkImpl)
					((DatalinkImpl) dl).setResolvedDepth(dip.getDepth());

		/*
		 * ==================================================================
		 * Now iteratively attempt to resolve everything else.
		 * ==================================================================
		 */

		/*
		 * Firstly take a copy of the processor list, we'll processors from this
		 * list as they become either failed or resolved
		 */
		List<TokenProcessingEntity> unresolved = new ArrayList<>(
				getEntities(TokenProcessingEntity.class));

		// Keep a list of processors that have failed, initially empty
		List<TokenProcessingEntity> failed = new ArrayList<>();

		/**
		 * Is the dataflow valid? The flow is valid if and only if both
		 * unresolved and failed lists are empty at the end. This doesn't
		 * guarantee that the workflow will run, in particular it doesn't
		 * actually check for issues such as unresolved output edges.
		 */

		// Flag to indicate whether we've finished yet, set to true if no
		// changes are made in an iteration
		boolean finished = false;

		Map<TokenProcessingEntity, DataflowValidationReport> invalidDataflows = new HashMap<>();
		while (!finished) {
			// We're finished unless something happens later
			finished = true;
			// Keep a list of processors to remove from the unresolved list
			// because they've been resolved properly
			List<TokenProcessingEntity> removeValidated = new ArrayList<>();
			// Keep another list of those that have failed
			List<TokenProcessingEntity> removeFailed = new ArrayList<>();

			for (TokenProcessingEntity p : unresolved)
				try {
					/*
					 * true = checked and valid, false = can't check, the
					 * exception means the processor was checked but was invalid
					 * for some reason
					 */

					if (p.doTypeCheck()) {
						removeValidated.add(p);
						/*
						 * At least one thing validated; we will need to run the
						 * check loop at least once more.
						 */
						finished = false;
					}
				} catch (IterationTypeMismatchException e) {
					removeFailed.add(p);
				} catch (InvalidDataflowException e) {
					invalidDataflows.put(p, e.getDataflowValidationReport());
					removeFailed.add(p);
				}

			/*
			 * Remove validated and failed items from the pending lists.
			 */
			unresolved.removeAll(removeValidated);
			unresolved.removeAll(removeFailed);
			failed.addAll(removeFailed);
		}

		/*
		 * At this point we know whether the processors within the workflow
		 * validated. If all the processors validated then we're probably okay,
		 * but there are a few other problems to check for. Firstly we need to
		 * check whether all the dataflow outputs are connected; any unconnected
		 * output is by definition a validation failure.
		 */
		List<DataflowOutputPort> unresolvedOutputs = new ArrayList<>();
		for (DataflowOutputPortImpl dopi : outputs) {
			Datalink dl = dopi.getInternalInputPort().getIncomingLink();
			/*
			 * Unset any type information on the output port, we'll set it again
			 * later if there's a suitably populated link going into it
			 */
			dopi.setDepths(-1, -1);
			if (dl == null)
				// not linked, this is by definition an unsatisfied link!
				unresolvedOutputs.add(dopi);
			else if (dl.getResolvedDepth() == -1)
				/*
				 * linked but the edge hasn't had its depth resolved, i.e. it
				 * links from an unresolved entity
				 */
				unresolvedOutputs.add(dopi);
			else {
				/*
				 * linked and edge depth is defined, we can therefore populate
				 * the granular and real depth of the dataflow output port. Note
				 * that this is the only way these values can be populated, you
				 * don't define them when creating the ports as they are
				 * entirely based on the type check stage.
				 */

				int granularDepth = dl.getSource().getGranularDepth();
				int resolvedDepth = dl.getResolvedDepth();
				dopi.setDepths(resolvedDepth, granularDepth);
			}
		}

		/*
		 * Check if workflow is 'incomplete' - i.e. if it contains no processors
		 * and no output ports. This is to prevent empty workflows or ones that
		 * contain input ports from being run.
		 */

		boolean dataflowIsIncomplete = getProcessors().isEmpty()
				&& getOutputPorts().isEmpty();

		/*
		 * For a workflow to be valid - workflow must not be 'empty' and lists
		 * of problems must all be empty
		 */

		boolean dataflowValid = (!dataflowIsIncomplete)
				&& unresolvedOutputs.isEmpty() && failed.isEmpty()
				&& unresolved.isEmpty();

		/*
		 * Build and return a new validation report containing the overall state
		 * along with lists of failed and unsatisfied processors and unsatisfied
		 * output ports
		 */

		return new DataflowValidationReportImpl(dataflowValid,
				dataflowIsIncomplete, failed, unresolved, unresolvedOutputs,
				invalidDataflows);
	}

	/**
	 * Gets all workflow entities of the specified type and returns as an
	 * unmodifiable list of that type
	 */
	@Override
	public <T extends NamedWorkflowEntity> List<? extends T> getEntities(
			Class<T> entityType) {
		List<T> result = new ArrayList<T>();
		filterAndAdd(processors, result, entityType);
		filterAndAdd(merges, result, entityType);
		return unmodifiableList(result);
	}

	private <T extends NamedWorkflowEntity> void filterAndAdd(
			Iterable<?> source, List<T> target, Class<T> type) {
		for (Object o : source)
			if (type.isAssignableFrom(o.getClass()))
				target.add(type.cast(o));
	}

	/**
	 * The active process identifiers correspond to current strands of data
	 * running through this dataflow.
	 */
	private Set<String> activeProcessIdentifiers = new HashSet<>();
	private volatile boolean immutable;

	/**
	 * Called when a token is received or the dataflow is fired, checks to see
	 * whether the process identifier is already known (in which case we assume
	 * it's been registered and can ignore it) or registers it with the monitor
	 * along with all child entities. The method is called with the ID of the
	 * new process, that is to say the ID of the token with ':'getLocalName()
	 * appended.
	 * 
	 * @param owningProcess
	 * 
	 * @return true if the owning process specified was already in the active
	 *         process identifier set, false otherwise
	 */
	protected boolean tokenReceived(String owningProcess,
			InvocationContext context) {
		synchronized (activeProcessIdentifiers) {
			if (activeProcessIdentifiers.contains(owningProcess))
				return true;
			MonitorManager.getInstance().registerNode(this, owningProcess);

			/*
			 * Message each processor within the dataflow and instruct it to
			 * register any properties with the monitor including any processor
			 * level properties it can aggregate from its dispatch stack.
			 */

			for (ProcessorImpl p : getEntities(ProcessorImpl.class)) {
				p.registerWithMonitor(owningProcess);
				if (p.getInputPorts().isEmpty())
					p.fire(owningProcess, context);
			}
			activeProcessIdentifiers.add(owningProcess);
			return false;
		}
	}

	/**
	 * Sets the local name for the dataflow
	 * 
	 * @param localName
	 */
	public void setLocalName(String localName) {
		if (immutable)
			throw new UnsupportedOperationException("Dataflow is immutable");
		name = localName;
	}

	@Override
	public String toString() {
		return "Dataflow " + getLocalName() + "[" + getIdentifier() + "]";
	}

	@Override
	public void fire(String owningProcess, InvocationContext context) {
		String newOwningProcess = owningProcess + ":" + getLocalName();
		if (tokenReceived(newOwningProcess, context)) {
			/*
			 * This is not good - should ideally handle it as it means the
			 * workflow has been fired when in a state where this wasn't
			 * sensible, i.e. already having been fired on this process
			 * identifier. For now we'll ignore it (ho hum, release deadline
			 * etc!)
			 */
		}
		/*
		 * The code below now happens in the tokenReceived method, we need to
		 * fire any processors which don't have dependencies when a new token
		 * arrives and we weren't doing that anywhere.
		 */
		/**
		 * for (Processor p : getEntities(Processor.class)) { if
		 * (p.getInputPorts().isEmpty()) { p.fire(newOwningProcess, context); }
		 * }
		 */
	}

	@Override
	public FailureTransmitter getFailureTransmitter() {
		throw new UnsupportedOperationException(
				"Not implemented for DataflowImpl yet");
	}

	@Override
	public boolean doTypeCheck() throws IterationTypeMismatchException {
		throw new UnsupportedOperationException(
				"Not implemented for DataflowImpl yet");
	}

	public void refreshInternalIdentifier() {
		setIdentifier(UUID.randomUUID().toString());
	}

	@Override
	public String getIdentifier() {
		return internalIdentifier;
	}

	@Override
	public String recordIdentifier() {
		addDataflowIdentification(this, internalIdentifier, new EditsImpl());
		return internalIdentifier;
	}

	void setIdentifier(String id) {
		if (immutable)
			throw new UnsupportedOperationException("Dataflow is immutable");
		this.internalIdentifier = id;
	}

	@Override
	public boolean isInputPortConnected(DataflowInputPort inputPort) {
		for (Datalink link : getLinks())
			if (link.getSource().equals(inputPort.getInternalOutputPort()))
				return true;
		return false;
	}

	@Override
	public synchronized void setImmutable() {
		if (immutable)
			return;
		processors = unmodifiableList(processors);
		merges = unmodifiableList(merges);
		outputs = unmodifiableList(outputs);
		inputs = unmodifiableList(inputs);
		immutable = true;
	}
}
