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

import java.util.List;
import java.util.Map;

/**
 * Contains a validation report from a dataflow validation check. Processors are
 * classified as failed, unsatisfied or valid depending on whether they directly
 * fail type validation, cannot be checked due to unsatisfied incoming links or
 * pass respectively.
 * 
 * @author Tom Oinn
 */
public interface DataflowValidationReport {
	/**
	 * Overall validity - if the workflow is valid it can be run, otherwise
	 * there are problems somewhere and a facade can't be created from it.
	 * 
	 * @return whether the workflow is valid (true) or not (false)
	 */
	boolean isValid();

	/**
	 * Whether the workflow is incomplete, i.e. contains no processors and no
	 * connected output ports. For example, it is empty or contains only input
	 * ports. Even though one can technically run such a workflow it should be
	 * prohibited as it does not make any sense. If a workflow is incomplete
	 * {@link DataflowValidationReport#isValid()} should return
	 * <code>false</code>.
	 * 
	 * @return whether the workflow is incomplete or not
	 */
	boolean isWorkflowIncomplete();

	/**
	 * The workflow will be marked as invalid if there are entities with
	 * unlinked input ports or where there are cycles causing the type checking
	 * algorithm to give up. In these cases offending processors or any
	 * ancestors that are affected as a knock on effect will be returned in this
	 * list.
	 * 
	 * @return list of TokenProcessingEntity instances within the Dataflow for
	 *         which it is impossible to determine validity due to missing
	 *         inputs or cyclic dependencies
	 */
	List<? extends TokenProcessingEntity> getUnsatisfiedEntities();

	/**
	 * The workflow will be marked as invalid if any entity fails to type check.
	 * 
	 * @return list of TokenProcessingEntity instances within the Dataflow which
	 *         caused explicit type check failures
	 */
	List<? extends TokenProcessingEntity> getFailedEntities();

	/**
	 * The workflow will be marked as invalid if any of the dataflow output
	 * ports can't be typed based on incoming links. This happens if the port
	 * isn't linked (a common enough issue for new users in previous releases of
	 * Taverna) or if the internal port is linked but the entity it links to
	 * isn't validated.
	 * 
	 * @return a list of DataflowOutputPort implementations which are not typed
	 *         correctly. These will have output depth of -1 indicating an
	 *         unknown depth, they may or may not have a granular depth set but
	 *         if the overall depth is -1 this isn't important as the thing
	 *         won't run anyway.
	 */
	List<? extends DataflowOutputPort> getUnresolvedOutputs();

	/**
	 * An entity will be marked invalid if it depends on a nested dataflow which
	 * itself is invalid. If this is the case the entity will be be present both
	 * in {@link #getFailedEntities()} and can be used as a key with this method
	 * to get the DataflowValidationReport explaining how the nested dataflow
	 * failed.
	 */
	Map<TokenProcessingEntity, DataflowValidationReport> getInvalidDataflows();
}
