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

package org.apache.taverna.monitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.taverna.lang.observer.MultiCaster;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;
import org.apache.taverna.monitor.MonitorManager.MonitorMessage;

/**
 * Manages a list of monitors implementations that get notified to register and
 * deregister nodes (ie. {@link{org.apache.taverna.monitor.MonitorNode}s), in
 * addition to the addition of {@link MonitorableProperty monitorable
 * properties} of such nodes.
 * <p>
 * Nodes are identified by their owning process, which is an array of strings,
 * for instance ["dataflow2", "processor5", "fish"]
 * <p>
 * To notify the (by default 0) monitors, use any of the
 * {@link #addPropertiesToNode(String[], Set)},
 * {@link #registerNode(Object, String[], Set)}, {@link #deregisterNode(String)}
 * methods and variants.
 * <p>
 * To register a monitor, use {@link #addObserver(Observer)}.
 * 
 * @author Stian Soiland-Reyes
 */
public class MonitorManager implements Observable<MonitorMessage> {
	private static MonitorManager instance;

	/**
	 * Get the MonitorManager singleton instance.
	 * 
	 * @return The MonitorManager singleton
	 */
	public synchronized static MonitorManager getInstance() {
		if (instance == null)
			setInstance(new MonitorManager());
		return instance;
	}

	/**
	 * Set the MonitorManager singleton instance. Only to be used by overriding
	 * super classes at initialisation time.
	 * 
	 * @param instance
	 *            MonitorManager singleton to be returned by
	 *            {@link #getInstance()}.
	 */
	protected synchronized static void setInstance(MonitorManager instance) {
		MonitorManager.instance = instance;
	}

	protected MultiCaster<MonitorMessage> multiCaster = new MultiCaster<>(this);

	/**
	 * Protected constructor, use singleton access
	 * {@link MonitorManager#getInstance()} instead.
	 * 
	 */
	protected MonitorManager() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addObserver(Observer<MonitorMessage> observer) {
		multiCaster.addObserver(observer);
	}

	/**
	 * Push new property get / set methods into the specified node. This is used
	 * for monitor-able activities where we have to create the node in the state
	 * tree before we have the set of monitor-able properties for it.
	 * 
	 * @param owningProcess
	 *            the node path to add properties, this must already be
	 *            registered in the state model, if not then this method fails
	 *            silently and does nothing.
	 * @param newProperties
	 *            the set of properties to add to the specified node in the
	 *            state model
	 */
	public void addPropertiesToNode(String[] owningProcess,
			Set<MonitorableProperty<?>> newProperties) {
		multiCaster.notify(new AddPropertiesMessage(owningProcess,
				newProperties));
	}

	/**
	 * Remove the specified node from the monitor. This must be called when the
	 * final data or completion event leaves a boundary of control. The monitor
	 * is free to delay the actual removal of state, in which case the node may
	 * choose to throw exceptions when properties are accessed. The monitor
	 * should eventually release all references to work-flow objects and process
	 * identifiers - typically this is used to allow a UI a few seconds delay on
	 * de-registration so that very fast register / de-register cycles are
	 * visible to a user.
	 * <p>
	 * The specified process identifier must exist within the monitor.
	 * 
	 * @param owningProcess
	 *            the identifier of the node to remove as a :-separated string
	 */
	public void deregisterNode(String owningProcessIdentifier) {
		deregisterNode(owningProcessIdentifier.split(":"));
	}

	/**
	 * Remove the specified node from the monitor. This must be called when the
	 * final data or completion event leaves a boundary of control. The monitor
	 * is free to delay the actual removal of state, in which case the node may
	 * choose to throw exceptions when properties are accessed. The monitor
	 * should eventually release all references to work-flow objects and process
	 * identifiers - typically this is used to allow a UI a few seconds delay on
	 * de-registration so that very fast register / de-register cycles are
	 * visible to a user.
	 * <p>
	 * The specified process identifier must exist within the monitor.
	 * 
	 * @param owningProcess
	 *            the identifier of the node to remove.
	 */
	public void deregisterNode(String[] owningProcess) {
		multiCaster.notify(new DeregisterNodeMessage(owningProcess));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Observer<MonitorMessage>> getObservers() {
		return multiCaster.getObservers();
	}

	/**
	 * Register a new node with this monitor. New nodes can only be registered
	 * when a new process identifier is allocated to data corresponding to entry
	 * to a boundary of control in the data-flow. For cases where extensions
	 * such as dispatch layers wish to augment existing state the plug-in point
	 * is responsible for the aggregation of appropriate properties.
	 * <p>
	 * The process identifier must not already exist within the state tree, if
	 * it does it will be ignored. Similarly the parent of the process
	 * identifier must exist, you cannot specify orphan nodes with no parent.
	 * 
	 * @param workflowObject
	 *            an object within the work-flow model which has received the
	 *            data stream and caused this node to be created. This may or
	 *            may not be a top level work-flow entity such as a processor or
	 *            data-flow. It may be empty in which case null can be used but
	 *            this is not recommended (there's almost always something you
	 *            can put here, in general the code to create a new node is
	 *            contained within something that's just received a data stream
	 *            so a default approach would be to use the 'this'
	 *            meta-variable)
	 * @param owningProcess
	 *            the :-separated process identifier as a string which has been
	 *            assigned to data moving through the workflow object. The list
	 *            of IDs must contain the new identifier as the last element.
	 */
	public void registerNode(Object workflowObject,
			String owningProcessIdentifier) {
		registerNode(workflowObject, owningProcessIdentifier.split(":"), null);
	}

	/**
	 * Register a new node with this monitor. New nodes can only be registered
	 * when a new process identifier is allocated to data corresponding to entry
	 * to a boundary of control in the data-flow. For cases where extensions
	 * such as dispatch layers wish to augment existing state the plug-in point
	 * is responsible for the aggregation of appropriate properties.
	 * <p>
	 * The process identifier must not already exist within the state tree, if
	 * it does it will be ignored. Similarly the parent of the process
	 * identifier must exist, you cannot specify orphan nodes with no parent.
	 * 
	 * @param workflowObject
	 *            an object within the work-flow model which has received the
	 *            data stream and caused this node to be created. This may or
	 *            may not be a top level work-flow entity such as a processor or
	 *            data-flow. It may be empty in which case null can be used but
	 *            this is not recommended (there's almost always something you
	 *            can put here, in general the code to create a new node is
	 *            contained within something that's just received a data stream
	 *            so a default approach would be to use the 'this'
	 *            meta-variable)
	 * @param owningProcess
	 *            the :-separated process identifier as a string which has been
	 *            assigned to data moving through the workflow object. The list
	 *            of IDs must contain the new identifier as the last element. *
	 * @param properties
	 *            the set of monitor-able, and potentially steer-able,
	 *            properties which can be monitored from this node. Properties
	 *            may cache, may become invalid and may make no guarantee about
	 *            timely updates.
	 */
	public void registerNode(Object workflowObject,
			String owningProcessIdentifier,
			Set<MonitorableProperty<?>> properties) {
		registerNode(workflowObject, owningProcessIdentifier.split(":"),
				properties);
	}

	/**
	 * Register a new node with this monitor. New nodes can only be registered
	 * when a new process identifier is allocated to data corresponding to entry
	 * to a boundary of control in the data-flow. For cases where extensions
	 * such as dispatch layers wish to augment existing state the plug-in point
	 * is responsible for the aggregation of appropriate properties.
	 * <p>
	 * The process identifier must not already exist within the state tree, if
	 * it does it will be ignored. Similarly the parent of the process
	 * identifier must exist, you cannot specify orphan nodes with no parent.
	 * 
	 * @param workflowObject
	 *            an object within the work-flow model which has received the
	 *            data stream and caused this node to be created. This may or
	 *            may not be a top level work-flow entity such as a processor or
	 *            data-flow. It may be empty in which case null can be used but
	 *            this is not recommended (there's almost always something you
	 *            can put here, in general the code to create a new node is
	 *            contained within something that's just received a data stream
	 *            so a default approach would be to use the 'this'
	 *            meta-variable)
	 * @param owningProcess
	 *            the process identifier which has been assigned to data moving
	 *            through the work-flow object. The list of IDs must contain the
	 *            new identifier as the last element.
	 */
	public void registerNode(Object workflowObject, String[] owningProcess) {
		registerNode(workflowObject, owningProcess, null);
	}

	/**
	 * Register a new node with this monitor. New nodes can only be registered
	 * when a new process identifier is allocated to data corresponding to entry
	 * to a boundary of control in the data-flow. For cases where extensions
	 * such as dispatch layers wish to augment existing state the plug-in point
	 * is responsible for the aggregation of appropriate properties.
	 * <p>
	 * The process identifier must not already exist within the state tree, if
	 * it does it will be ignored. Similarly the parent of the process
	 * identifier must exist, you cannot specify orphan nodes with no parent.
	 * 
	 * @param workflowObject
	 *            an object within the work-flow model which has received the
	 *            data stream and caused this node to be created. This may or
	 *            may not be a top level work-flow entity such as a processor or
	 *            data-flow. It may be empty in which case null can be used but
	 *            this is not recommended (there's almost always something you
	 *            can put here, in general the code to create a new node is
	 *            contained within something that's just received a data stream
	 *            so a default approach would be to use the 'this'
	 *            meta-variable)
	 * @param owningProcess
	 *            the process identifier which has been assigned to data moving
	 *            through the work-flow object. The list of IDs must contain the
	 *            new identifier as the last element.
	 * @param properties
	 *            the set of monitor-able, and potentially steer-able,
	 *            properties which can be monitored from this node. Properties
	 *            may cache, may become invalid and may make no guarantee about
	 *            timely updates.
	 */
	public void registerNode(Object workflowObject, String[] owningProcess,
			Set<MonitorableProperty<?>> properties) {
		if (properties == null)
			properties = new HashSet<>();
		multiCaster.notify(new RegisterNodeMessage(workflowObject,
				owningProcess, properties));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeObserver(Observer<MonitorMessage> observer) {
		multiCaster.removeObserver(observer);
	}

	/**
	 * Message indicating that {@link #getNewProperties() new properties} have
	 * been added to a node identified by given {@link #getOwningProcess()
	 * owning process}.
	 * 
	 */
	public class AddPropertiesMessage extends MonitorMessage {
		private final Set<MonitorableProperty<?>> newProperties;

		public AddPropertiesMessage(String[] owningProcess,
				Set<MonitorableProperty<?>> newProperties) {
			super(owningProcess);
			this.newProperties = newProperties;
		}

		public Set<MonitorableProperty<?>> getNewProperties() {
			return newProperties;
		}
	}

	/**
	 * Message indicating that the node of the given {@link #getOwningProcess()
	 * owning process} is to be deregistered.
	 * 
	 */
	public class DeregisterNodeMessage extends MonitorMessage {
		public DeregisterNodeMessage(String[] owningProcess) {
			super(owningProcess);
		}
	}

	/**
	 * Common abstract superclass for all monitor messages. Identifies the
	 * {@link #getOwningProcess() owning process}.
	 * 
	 */
	public abstract class MonitorMessage {
		private final String[] owningProcess;

		public MonitorMessage(String[] owningProcess) {
			this.owningProcess = owningProcess;
		}

		public String[] getOwningProcess() {
			return owningProcess;
		}
	}

	/**
	 * Message indicating that the node of the given {@link #getOwningProcess()
	 * owning process} is to be registered. The node might have
	 * {@link #getProperties() a set of monitorable properties} and a
	 * {@link #getWorkflowObject workflow object}.
	 */
	public class RegisterNodeMessage extends MonitorMessage {
		private final Set<MonitorableProperty<?>> properties;
		private final Object workflowObject;

		public RegisterNodeMessage(Object workflowObject,
				String[] owningProcess, Set<MonitorableProperty<?>> properties) {
			super(owningProcess);
			this.workflowObject = workflowObject;
			this.properties = properties;
		}

		public Set<MonitorableProperty<?>> getProperties() {
			return properties;
		}

		public Object getWorkflowObject() {
			return workflowObject;
		}
	}
}
