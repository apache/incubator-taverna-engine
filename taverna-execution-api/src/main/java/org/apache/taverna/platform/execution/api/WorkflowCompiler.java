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

package org.apache.taverna.platform.execution.api;

import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;

/**
 * A workflow compilation service converts a workflow (in a
 * {@link WorkflowBundle}) into a dataflow. Most code should ignore this.
 * 
 * @author Donal Fellows
 */
public interface WorkflowCompiler {
	/**
	 * Convert a workflow into a dataflow. May cache.
	 * 
	 * @param workflow
	 *            the workflow to convert; must not be <tt>null</tt>
	 * @return the dataflow, which should not be modified.
	 * @throws InvalidWorkflowException
	 *             If the compilation fails.
	 */
	Dataflow getDataflow(Workflow workflow) throws InvalidWorkflowException;
	
	/**
	 * Convert a workflow bundle into a dataflow. May cache. Only the the
	 * primary workflow is guaranteed to be converted.
	 * 
	 * @param bundle
	 *            the workflow bundle to convert; must not be <tt>null</tt>
	 * @return the dataflow, which should not be modified.
	 * @throws InvalidWorkflowException
	 *             If the compilation fails.
	 */
	Dataflow getDataflow(WorkflowBundle bundle) throws InvalidWorkflowException;
}
