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

import org.apache.taverna.annotation.HierarchyTraversal;
import org.apache.taverna.workflowmodel.processor.iteration.IterationTypeMismatchException;

/**
 * Superinterface for all classes within the workflow model which consume and
 * emit workflow data tokens.
 * 
 * @author Tom Oinn
 */
public interface TokenProcessingEntity extends NamedWorkflowEntity {
	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	List<? extends EventHandlingInputPort> getInputPorts();

	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	List<? extends EventForwardingOutputPort> getOutputPorts();

	/**
	 * Run a collection level based type check on the token processing entity
	 * 
	 * @return true if the typecheck was successful or false if the check failed
	 *         because there were preconditions missing such as unsatisfied
	 *         input types
	 * @throws IterationTypeMismatchException
	 *             if the typing occurred but didn't match because of an
	 *             iteration mismatch
	 * @throws InvalidDataflowException
	 *             if the entity depended on a dataflow that was not valid
	 */
	boolean doTypeCheck() throws IterationTypeMismatchException,
			InvalidDataflowException;
}
