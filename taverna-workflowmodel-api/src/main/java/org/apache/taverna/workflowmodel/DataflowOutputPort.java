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

import org.apache.taverna.facade.ResultListener;

/**
 * Output port of a DataFlow, exposes an internal EventHandlingInputPort into
 * which the internal workflow logic pushes data to be exposed outside the
 * workflow boundary.
 * 
 */
public interface DataflowOutputPort extends EventForwardingOutputPort,
		DataflowPort {
	/**
	 * Get the internal input port for this workflow output
	 * 
	 * @return port into which the workflow can push data for this output
	 */
	EventHandlingInputPort getInternalInputPort();

	/**
	 * Add a ResultListener, capable of listening to results being received by
	 * the output port
	 * 
	 * @param listener
	 *            the ResultListener
	 * 
	 * @see ResultListener
	 */
	void addResultListener(ResultListener listener);

	/**
	 * Remove a ResultListener
	 * 
	 * @param listener
	 *            the ResultListener
	 * 
	 * @see ResultListener
	 */
	void removeResultListener(ResultListener listener);
}
