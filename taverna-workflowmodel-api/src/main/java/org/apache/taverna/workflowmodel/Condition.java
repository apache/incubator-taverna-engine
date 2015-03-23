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

import org.apache.taverna.annotation.Annotated;

/**
 * Defines the base interface for a condition which must be satisfied before a
 * processor can commence invocation. Conditions are expressed in terms of a
 * relationship between a controlling and a target processor where the target
 * processor may not commence invocation until all conditions for which it is a
 * target are satisfied in the context of a particular owning process
 * identifier.
 * 
 * @author Tom Oinn
 */
public interface Condition extends Annotated<Condition>, WorkflowItem {
	/**
	 * @return the Processor constrained by this condition
	 */
	Processor getControl();

	/**
	 * @return the Processor acting as the controller for this condition
	 */
	Processor getTarget();

	/**
	 * @param owningProcess
	 *            the context in which the condition is to be evaluated
	 * @return whether the condition is satisfied
	 */
	boolean isSatisfied(String owningProcess);
}
