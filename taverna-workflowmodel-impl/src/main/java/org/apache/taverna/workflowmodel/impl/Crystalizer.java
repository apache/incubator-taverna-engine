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

package org.apache.taverna.workflowmodel.impl;

import org.apache.taverna.invocation.Completion;
import org.apache.taverna.invocation.IterationInternalEvent;
import org.apache.taverna.workflowmodel.processor.activity.Job;

/**
 * Recieves Job and Completion events and emits Jobs unaltered. Completion
 * events additionally cause registration of lists for each key in the datamap
 * of the jobs at immediate child locations in the index structure. These list
 * identifiers are sent in place of the Completion events.
 * <p>
 * State for a given process ID is purged when a final completion event is
 * received so there is no need for an explicit cache purge operation in the
 * public API (although for termination of partially complete workflows it may
 * be sensible for subclasses to provide one)
 * 
 * @author Tom Oinn
 */
public interface Crystalizer {
	/**
	 * Receive a Job or Completion, Jobs are emitted unaltered and cached,
	 * Completion events trigger registration of a corresponding list - this may
	 * be recursive in nature if the completion event's index implies nested
	 * lists which have not been registered.
	 */
	void receiveEvent(
			IterationInternalEvent<? extends IterationInternalEvent<?>> event);

	/**
	 * This method is called when a new Job has been handled by the
	 * AbstractCrystalizer, either by direct passthrough or by list
	 * registration.
	 * 
	 */
	void jobCreated(Job outputJob);

	/**
	 * Called whenever a completion not corresponding to a node in the cache is
	 * generated. In many cases this is an indication of an error state, the
	 * processor implementation should ensure that completion events are only
	 * sent to the crystalizer if there has been at least one data event with a
	 * lower depth on the same path.
	 */
	void completionCreated(Completion completion);
}
