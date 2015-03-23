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

/**
 * An input port on a Dataflow contains a nested output port within it. This
 * internal output port is used when connecting an edge from the workflow input
 * to a processor or workflow output (which in turn has a nested input port).
 * The workflow ports are therefore effectively pairs of ports with a relay
 * mechanism between the external and internal in the case of the dataflow
 * input.
 * 
 * @author Tom Oinn
 */
public interface DataflowInputPort extends EventHandlingInputPort, DataflowPort {
	/**
	 * Return the internal output port. Output ports have a granular depth
	 * property denoting the finest grained output token they can possibly
	 * produce, this is used to configure downstream filtering input ports. In
	 * this case the finest depth item is determined by the input to the
	 * workflow port and must be explicitly set.
	 * 
	 * @return the internal output port
	 */
	EventForwardingOutputPort getInternalOutputPort();

	/**
	 * Define the finest grained item that will be sent to this input port. As
	 * all data are relayed through to the internal output port this is used to
	 * denote output port granularity as well as to configure any downstream
	 * connected filtering input ports.
	 */
	int getGranularInputDepth();
}
