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

package org.apache.taverna.workflowmodel;

import static org.apache.taverna.annotation.HierarchyRole.CHILD;

import java.util.List;

import org.apache.taverna.annotation.Annotated;
import org.apache.taverna.annotation.HierarchyTraversal;
import org.apache.taverna.invocation.InvocationContext;

/**
 * Top level definition object for a dataflow workflow. Currently Taverna only
 * supports dataflow workflows, this is equivalent to the Taverna 1 ScuflModel
 * class in role.
 * 
 * @author Tom Oinn
 */
@ControlBoundary
public interface Dataflow extends Annotated<Dataflow>, TokenProcessingEntity,
		WorkflowItem {
	/**
	 * A Dataflow consists of a set of named Processor instances. This method
	 * returns an unmodifiable list of these processors. Equivalent to calling
	 * getEntities(Processor.class).
	 * 
	 * @return list of all processors in the dataflow
	 */
	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	List<? extends Processor> getProcessors();

	/**
	 * Dataflows also contain a set of merge operations, this method returns an
	 * unmodifiable copy of the set. Equivalent to calling
	 * getEntities(Merge.class)
	 */
	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	List<? extends Merge> getMerges();

	/**
	 * Dataflows have a list of input ports. These are the input ports the world
	 * outside the dataflow sees - each one contains an internal output port
	 * which is used to forward events on to entities (mostly processors) within
	 * the dataflow.
	 * 
	 * @return list of dataflow input port instances
	 */
	@Override
	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	List<? extends DataflowInputPort> getInputPorts();

	/**
	 * Get all workflow entities with the specified type restriction, this
	 * allows retrieval of Processor, Merge, a mix of the two or any other
	 * future entity to be added to the workflow model without a significant
	 * change in this part of the API.
	 * 
	 * @return an unmodifiable list of entities of the specified type
	 * @param entityType
	 *            a class of the type specified by the type variable T. All
	 *            entities returned in the list can be cast to this type
	 */
	<T extends NamedWorkflowEntity> List<? extends T> getEntities(
			Class<T> entityType);

	/**
	 * Dataflows have a list of output ports. The output port in a dataflow is
	 * the port visible to the outside world and from which the dataflow emits
	 * events. Each dataflow output port also contains an instance of event
	 * receiving input port which is used by entities within the dataflow to
	 * push events to the corresponding external output port.
	 * 
	 * @return list of dataflow output port instances
	 */
	@Override
	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	List<? extends DataflowOutputPort> getOutputPorts();

	/**
	 * The dataflow is largely defined by the links between processors and other
	 * entities within its scope. This method returns them.
	 * 
	 * @return list of Datalink implementations
	 */
	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	List<? extends Datalink> getLinks();

	/**
	 * Triggers a check for various basic potential problems with the workflow,
	 * in particular ones related to type checking. Returns a report object
	 * containing the state of the workflow and any problems found.
	 * <p>
	 * If the workflow has been set immutable with {@link #setImmutable()},
	 * subsequent calls to this method will return the cached
	 * DataflowValidationReport.
	 * 
	 * @return validation report
	 */
	DataflowValidationReport checkValidity();

	/**
	 * A dataflow with no inputs cannot be driven by the supply of data tokens
	 * as it has nowhere to receive such tokens. This method allows a dataflow
	 * to fire on an empty input set, in this case the owning process identifier
	 * must be passed explicitly to the dataflow. This method then calls the
	 * fire methods of any Processor instances with no input ports.
	 */
	void fire(String owningProcess, InvocationContext context);

	/**
	 * The failure transmitter contains event listeners to be notified of
	 * workflow level failures - these occur when an error bubbles up to the top
	 * of the dispatch stack in a processor and is not handled by conversion to
	 * an error token within the data stream.
	 * <p>
	 * Listeners are messaged after all clean-up has been performed on the
	 * dataflow's internal state and that of any child operations within it,
	 * guaranteeing that no tokens will be generated with the id of the failed
	 * process after the message has been received by the listener
	 */
	FailureTransmitter getFailureTransmitter();

	/**
	 * An identifier that is unique to this dataflow and its current state. The
	 * identifier will change whenever the dataflow is modified.
	 * 
	 * @return a String representing a unique internal identifier.
	 */
	String getIdentifier();

	String recordIdentifier();

	/**
	 * Check if the given input port is connected to anything in the workflow.
	 * 
	 * @param inputPort
	 * @return true if the given workflow input port is connected, false
	 *         otherwise.
	 */
	boolean isInputPortConnected(DataflowInputPort inputPort);

	/**
	 * Mark this dataflow as immutable.
	 * 
	 * Subsequent edits to its ports, links, merges, input and output ports will
	 * throw a RuntimeException like UnsupportedOperationException.
	 * 
	 * This method should be called before executing a Dataflow with
	 * {@link #fire(String, InvocationContext)}, in order to guarantee that
	 * datalinks, port depths etc. don't change while the dataflow is running.
	 * 
	 */
	void setImmutable();
}
