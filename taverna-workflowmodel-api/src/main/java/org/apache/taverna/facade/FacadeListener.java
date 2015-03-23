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

package org.apache.taverna.facade;

import org.apache.taverna.facade.WorkflowInstanceFacade.State;

/**
 * Used to communicate a failure of the overall workflow to interested parties.
 * 
 * @author Tom Oinn
 */
public interface FacadeListener {
	/**
	 * Called if the workflow fails in a critical and fundamental way. Most
	 * internal failures of individual process instances will not trigger this,
	 * being handled either by the per processor dispatch stack through retry,
	 * failover etc or by being converted into error tokens and injected
	 * directly into the data stream. This therefore denotes a catastrophic and
	 * unrecoverable problem.
	 * 
	 * @param message
	 *            Description of what happened
	 * @param t
	 *            The cause of the failure
	 */
	void workflowFailed(WorkflowInstanceFacade facade, String message,
			Throwable t);

	void stateChange(WorkflowInstanceFacade facade, State oldState,
			State newState);
}
