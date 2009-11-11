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
package net.sf.taverna.t2.workflowmodel.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.sf.taverna.t2.annotation.AbstractAnnotatedThing;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.monitor.MonitorManager;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.DataflowValidationReport;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;
import net.sf.taverna.t2.workflowmodel.FailureTransmitter;
import net.sf.taverna.t2.workflowmodel.InvalidDataflowException;
import net.sf.taverna.t2.workflowmodel.Merge;
import net.sf.taverna.t2.workflowmodel.NamedWorkflowEntity;
import net.sf.taverna.t2.workflowmodel.NamingException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.TokenProcessingEntity;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationTypeMismatchException;

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
		for (Processor existingProcessor : processors
				.toArray(new Processor[] {})) {
			if (existingProcessor.getLocalName().equals(
					processor.getLocalName()))
				throw new NamingException("There already is a processor named:"
						+ processor.getLocalName());
		}
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
		for (Merge existingMerge : merges.toArray(new Merge[] {})) {
			if (existingMerge.getLocalName().equals(merge.getLocalName()))
				throw new NamingException(
						"There already is a merge operation named:"
								+ merge.getLocalName());
		}
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
		for (DataflowInputPort dip : inputs) {
			if (dip.getName().equals(name)) {
				throw new NamingException("Duplicate input port name '" + name
						+ "' in dataflow already.");
			}
		}
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
		for (DataflowInputPort existingInputPort : inputs
				.toArray(new DataflowInputPort[] {})) {
			if (existingInputPort.getName().equals(inputPort.getName()))
				throw new NamingException("There already is a dataflow input port named:"
						+ inputPort.getName());
		}
		if (inputPort.getDataflow() != this) {
			throw new EditException("Port specifies a different dataflow");
		}
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
		DataflowInputPort found = null;
		for (DataflowInputPort dip : inputs) {
			if (dip.getName().equals(name)) {
				found = dip;
				break;
			}
		}
		if (found != null) {
			removeDataflowInputPort(found);
		} else {
			throw new EditException("No such input port '" + name
					+ "' in dataflow.");
		}
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
		if (inputs.contains(dip)) {
			inputs.remove(dip);
		} else {
			throw new EditException(
					"Can't locate the specified input port in dataflow. Input port has name '"
							+ dip.getName() + "'.");
		}
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
		for (DataflowOutputPort dop : outputs) {
			if (dop.getName().equals(name)) {
				throw new NamingException("Duplicate output port name '" + name
						+ "' in dataflow already.");
			}
		}
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
		for (DataflowOutputPort existingOutputPort : outputs
				.toArray(new DataflowOutputPort[] {})) {
			if (existingOutputPort.getName().equals(outputPort.getName()))
				throw new NamingException("There already is a dataflow output port named:"
						+ outputPort.getName());
		}
		if (outputPort.getDataflow() != this) {
			throw new EditException("Port specifies a different dataflow");
		}
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
		DataflowOutputPort found = null;
		for (DataflowOutputPort dop : outputs) {
			if (dop.getName().equals(name)) {
				found = dop;
				break;
			}
		}
		if (found != null) {
			removeDataflowOutputPort(found);
		} else {
			throw new EditException("No such output port '" + name
					+ "' in dataflow.");
		}
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
		if (outputs.contains(dop)) {
			outputs.remove(dop);
		} else {
			throw new EditException(
					"Can't locate the specified output port in dataflow, output port has name '"
							+ dop.getName() + "'.");
		}
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
		BasicEventForwardingOutputPort source = null;
		AbstractEventHandlingInputPort sink = null;

		// Find source port
		String[] split = sourceName.split(":");
		if (split.length == 2) {
			// source is a processor
			// TODO - update to include Merge when it's added
			for (ProcessorImpl pi : processors) {
				if (pi.getLocalName().equals(split[0])) {
					source = pi.getOutputPortWithName(split[1]);
					break;
				}
			}
		} else if (split.length == 1) {
			// source is a workflow input port, or at least the internal output
			// port within it
			for (DataflowInputPortImpl dipi : inputs) {
				if (dipi.getName().equals(split[0])) {
					source = dipi.internalOutput;
					break;
				}
			}
		} else {
			throw new EditException("Invalid source link name '" + sourceName
					+ "'.");
		}
		if (source == null) {
			throw new EditException("Unable to find source port named '"
					+ sourceName + "' in link creation.");
		}

		// Find sink
		split = sinkName.split(":");
		if (split.length == 2) {
			// sink is a processor
			// TODO - update to include Merge when it's added
			for (ProcessorImpl pi : processors) {
				if (pi.getLocalName().equals(split[0])) {
					sink = pi.getInputPortWithName(split[1]);
					break;
				}
			}
		} else if (split.length == 1) {
			// source is a workflow input port, or at least the internal output
			// port within it
			for (DataflowOutputPortImpl dopi : outputs) {
				if (dopi.getName().equals(split[0])) {
					sink = dopi.internalInput;
					break;
				}
			}
		} else {
			throw new EditException("Invalid link sink name '" + sinkName
					+ "'.");
		}
		if (sink == null) {
			throw new EditException("Unable to find sink port named '"
					+ sinkName + "' in link creation");
		}

		// Check whether the sink is already linked
		if (sink.getIncomingLink() != null) {
			throw new EditException("Cannot link to sink port '" + sinkName
					+ "' as it is already linked");
		}

		// Got here so we have both source and sink and the sink isn't already
		// linked from somewhere. If the sink isn't linked we can't have a
		// duplicate link here which would have been the other condition to
		// check for.
		DatalinkImpl link = new DatalinkImpl(source, sink);
		source.addOutgoingLink(link);
		sink.setIncomingLink(link);

		return link;

	}

	/**
	 * Return a copy of the list of dataflow input ports for this dataflow
	 */
	public synchronized List<? extends DataflowInputPort> getInputPorts() {
		return Collections.unmodifiableList(inputs);
	}

	/**
	 * For each processor input, merge input and workflow output get the
	 * incoming link and, if non null, add to a list and return the entire list.
	 */
	public synchronized List<? extends Datalink> getLinks() {
		List<Datalink> result = new ArrayList<Datalink>();
		// All processors have a set of input ports each of which has at most
		// one incoming data link
		for (TokenProcessingEntity p : getEntities(TokenProcessingEntity.class)) {
			for (EventHandlingInputPort pip : p.getInputPorts()) {
				Datalink dl = pip.getIncomingLink();
				if (dl != null) {
					result.add(dl);
				}
			}
		}
		// Workflow outputs have zero or one incoming data link to their
		// internal input port
		for (DataflowOutputPort dop : getOutputPorts()) {
			Datalink dl = dop.getInternalInputPort().getIncomingLink();
			if (dl != null) {
				result.add(dl);
			}
		}

		return result;
	}

	/**
	 * Return the list of all processors within the dataflow
	 */
	public synchronized List<? extends Processor> getProcessors() {
		return getEntities(Processor.class);
	}

	/**
	 * Return the list of all merge operations within the dataflow
	 */
	public synchronized List<? extends Merge> getMerges() {
		return getEntities(Merge.class);
	}

	/**
	 * Return all dataflow output ports
	 */
	public synchronized List<? extends DataflowOutputPort> getOutputPorts() {
		return Collections.unmodifiableList(this.outputs);
	}

	/**
	 * Return the local name of this workflow
	 */
	public String getLocalName() {
		return this.name;
	}

	/**
	 * Run the type check algorithm and return a report on any problems found.
	 * This method must be called prior to actually pushing data through the
	 * dataflow as it sets various properties as a side effect.
	 */
	public synchronized DataflowValidationReport checkValidity() {
		// First things first - nullify the resolved depths in all datalinks
		for (Datalink dl : getLinks()) {
			if (dl instanceof DatalinkImpl) {
				DatalinkImpl dli = (DatalinkImpl) dl;
				dli.setResolvedDepth(-1);
			}
		}
		// Now copy type information from workflow inputs
		for (DataflowInputPort dip : getInputPorts()) {
			for (Datalink dl : dip.getInternalOutputPort().getOutgoingLinks()) {
				if (dl instanceof DatalinkImpl) {
					DatalinkImpl dli = (DatalinkImpl) dl;
					dli.setResolvedDepth(dip.getDepth());
				}
			}
		}
		// Now iteratively attempt to resolve everything else.

		// Firstly take a copy of the processor list, we'll processors from this
		// list as they become either failed or resolved
		List<TokenProcessingEntity> unresolved = new ArrayList<TokenProcessingEntity>(
				getEntities(TokenProcessingEntity.class));

		// Keep a list of processors that have failed, initially empty
		List<TokenProcessingEntity> failed = new ArrayList<TokenProcessingEntity>();

		/**
		 * Is the dataflow valid? The flow is valid if and only if both
		 * unresolved and failed lists are empty at the end. This doesn't
		 * guarantee that the workflow will run, in particular it doesn't
		 * actually check for issues such as unresolved output edges.
		 */

		// Flag to indicate whether we've finished yet, set to true if no
		// changes are made in an iteration
		boolean finished = false;

		Map<TokenProcessingEntity, DataflowValidationReport> invalidDataflows = new HashMap<TokenProcessingEntity, DataflowValidationReport>();
		while (!finished) {
			// We're finished unless something happens later
			finished = true;
			// Keep a list of processors to remove from the unresolved list
			// because they've been resolved properly
			List<TokenProcessingEntity> removeValidated = new ArrayList<TokenProcessingEntity>();
			// Keep another list of those that have failed
			List<TokenProcessingEntity> removeFailed = new ArrayList<TokenProcessingEntity>();
			
			
			for (TokenProcessingEntity p : unresolved) {
				try {
					// true = checked and valid, false = can't check, the
					// exception means the processor was checked but was invalid
					// for some reason
					boolean entityValid = p.doTypeCheck();
					if (entityValid) {
						removeValidated.add(p);
					}
				} catch (IterationTypeMismatchException e) {
					removeFailed.add(p);
				} catch (InvalidDataflowException e) {
					invalidDataflows.put(p, e.getDataflowValidationReport());
					removeFailed.add(p);
				}
			}

			/**
			 * Remove validated and failed items from the pending lists. If
			 * anything was removed because it validated okay then we're not
			 * finished yet and should reset the boolean finished flag
			 */
			for (TokenProcessingEntity p : removeValidated) {
				unresolved.remove(p);
				finished = false;
			}
			for (TokenProcessingEntity p : removeFailed) {
				unresolved.remove(p);
				failed.add(p);
			}

		}

		// At this point we know whether the processors within the workflow
		// validated. If all the processors validated then we're probably okay,
		// but there are a few other problems to check for. Firstly we need to
		// check whether all the dataflow outputs are connected; any unconnected
		// output is by definition a validation failure.
		List<DataflowOutputPort> unresolvedOutputs = new ArrayList<DataflowOutputPort>();
		for (DataflowOutputPortImpl dopi : outputs) {
			Datalink dl = dopi.getInternalInputPort().getIncomingLink();
			// Unset any type information on the output port, we'll set it again
			// later if there's a suitably populated link going into it
			dopi.setDepths(-1, -1);
			if (dl == null) {
				// not linked, this is by definition an unsatisfied link!
				unresolvedOutputs.add(dopi);
			} else if (dl.getResolvedDepth() == -1) {
				// linked but the edge hasn't had its depth resolved, i.e. it
				// links from an unresolved entity
				unresolvedOutputs.add(dopi);
			} else {
				// linked and edge depth is defined, we can therefore populate
				// the granular and real depth of the dataflow output port. Note
				// that this is the only way these values can be populated, you
				// don't define them when creating the ports as they are
				// entirely based on the type check stage.
				int granularDepth = dl.getSource().getGranularDepth();
				int resolvedDepth = dl.getResolvedDepth();
				dopi.setDepths(resolvedDepth, granularDepth);
			}
		}

		// Check if workflow is 'incomplete' - i.e. if it contains no processors and no output ports.
		// This is to prevent empty workflows or ones that contain input ports from being run.
		boolean dataflowIsIncomplete = getProcessors().isEmpty() && getOutputPorts().isEmpty();
		// For a workflow to be valid - workflow must not be 'empty' and lists of problems must all be empty 
		boolean dataflowValid = (!dataflowIsIncomplete) && unresolvedOutputs.isEmpty() && failed.isEmpty()
				&& unresolved.isEmpty();

		// Build and return a new validation report containing the overall state
		// along with lists of failed and unsatisfied processors and unsatisfied
		// output ports
		return new DataflowValidationReportImpl(dataflowValid, dataflowIsIncomplete, failed,
				unresolved, unresolvedOutputs, invalidDataflows);
	}

	/**
	 * Gets all workflow entities of the specified type and returns as an
	 * unmodifiable list of that type
	 */
	public <T extends NamedWorkflowEntity> List<? extends T> getEntities(
			Class<T> entityType) {
		List<T> result = new ArrayList<T>();
		filterAndAdd(processors, result, entityType);
		filterAndAdd(merges, result, entityType);
		return Collections.unmodifiableList(result);
	}

	private <T extends NamedWorkflowEntity> void filterAndAdd(
			Iterable<?> source, List<T> target, Class<T> type) {
		for (Object o : source) {
			if (type.isAssignableFrom(o.getClass())) {
				T targetObject = type.cast(o);
				target.add(targetObject);
			}
		}
	}

	/**
	 * The active process identifiers correspond to current strands of data
	 * running through this dataflow. The keys are process identifiers, the
	 * values are sets of output port names for which final events have been
	 * received.
	 */
	private Map<String, Set<String>> activeProcessIdentifiers = new HashMap<String, Set<String>>();

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
			if (! activeProcessIdentifiers.keySet().contains(owningProcess)) {
				MonitorManager.getInstance().registerNode(this, owningProcess);
				// Message each processor within the dataflow and instruct it to
				// register any properties with the monitor including any
				// processor level properties it can aggregate from its dispatch
				// stack.
				for (ProcessorImpl p : getEntities(ProcessorImpl.class)) {
					p.registerWithMonitor(owningProcess);
					if (p.getInputPorts().isEmpty()) {
						p.fire(owningProcess, context);
					}
				}
				activeProcessIdentifiers.put(owningProcess,
						new HashSet<String>());
				return false;
			}
			return true;
		}
	}
	
	/**
	 * Sets the local name for the dataflow
	 * @param localName
	 */
	public void setLocalName(String localName) {
		name=localName;
	}
	
	@Override
	public String toString() {
		return "Dataflow " + getLocalName() + "[" + getInternalIdentier() + "]"; 
	}

	public void fire(String owningProcess, InvocationContext context) {
		String newOwningProcess = owningProcess + ":" + getLocalName();
		if (tokenReceived(newOwningProcess, context)) {
			// This is not good - should ideally handle it as it means the
			// workflow has been fired when in a state where this wasn't
			// sensible, i.e. already having been fired on this process
			// identifier. For now we'll ignore it (ho hum, release deadline
			// etc!)
		}
		// The code below now happens in the tokenReceived method, we need to
		// fire any processors which don't have dependencies when a new token
		// arrives and we weren't doing that anywhere.
		/**
		 * for (Processor p : getEntities(Processor.class)) { if
		 * (p.getInputPorts().isEmpty()) { p.fire(newOwningProcess, context); } }
		 */
	}

	/**
	 * Called when a token with index array length zero is sent from a dataflow
	 * output port.
	 * 
	 * @param portName
	 */
	public void sentFinalToken(String portName, String owningProcess) {
		synchronized (activeProcessIdentifiers) {
			Set<String> alreadyReceivedPortNames = activeProcessIdentifiers
					.get(owningProcess);
			if (alreadyReceivedPortNames == null) {
				throw new RuntimeException(
						"Workflow's broken in some way, received an output token for process '"
								+ owningProcess + "' that shouldn't exist!");
			}
			if (alreadyReceivedPortNames.contains(portName)) {
				throw new RuntimeException(
						"Received duplicate final events on port name '"
								+ portName + "' for process '" + owningProcess
								+ "', this is not a good thing");
			}

			// No duplicates and the set wasn't null, add this port name to the
			// set of ports which have sent final events.
			alreadyReceivedPortNames.add(portName);

			// Check - if we have no duplicates and the set of output ports
			// which have sent final events in this data thread is the same size
			// as the number of output ports then we've finished and can
			// deregister from the monitor
			if (alreadyReceivedPortNames.size() == getOutputPorts().size()) {

				// request deregistration of processor nodes (arguably we don't
				// need
				// to do this and could just deregister the dataflow node in the
				// tree as that'll by definition detach all the child nodes but
				// this
				// seems cleaner)
				for (Processor p : getEntities(Processor.class)) {
					MonitorManager.getInstance().deregisterNode(
							owningProcess + ":" + p.getLocalName());
				}
				MonitorManager.getInstance().deregisterNode(owningProcess);

				// Remove this entry from the active process map
				activeProcessIdentifiers.remove(owningProcess);

			}
		}

	}

	public FailureTransmitter getFailureTransmitter() {
		throw new UnsupportedOperationException(
				"Not implemented for DataflowImpl yet");
	}

	public boolean doTypeCheck() throws IterationTypeMismatchException {
		throw new UnsupportedOperationException(
				"Not implemented for DataflowImpl yet");
	}
	
	public void refreshInternalIdentifier() {
		internalIdentifier=UUID.randomUUID().toString();
	}

	public String getInternalIdentier() {
		return internalIdentifier;
	}
	
	protected void setInternalIndentifier(String id) {
		this.internalIdentifier=id;
	}

}
