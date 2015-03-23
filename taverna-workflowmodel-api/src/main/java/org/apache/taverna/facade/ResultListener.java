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

import org.apache.taverna.invocation.WorkflowDataToken;

/**
 * Implement and use with the WorkflowInstanceFacade to listen for data
 * production events from the underlying workflow instance
 * 
 * @author Tom Oinn
 */
public interface ResultListener {
	/**
	 * Called when a new result token is produced by the workflow instance.
	 * 
	 * @param token
	 *            the WorkflowDataToken containing the result.
	 * @param portName
	 *            The name of the output port on the workflow from which this
	 *            token is produced, this now folds in the owning process which
	 *            was part of the signature for this method
	 */
	void resultTokenProduced(WorkflowDataToken token, String portName);
}
