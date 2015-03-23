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

package org.apache.taverna.workflowmodel.processor.dispatch.description;

/**
 * Describes the effect of a message on the state of a dispatch layer, used by
 * DispatchLayerReaction. If no message causes any of these actions the layer
 * can be described as state free.
 * 
 * @author Tom Oinn
 */
public enum DispatchLayerStateEffect {
	/**
	 * The message causes a state object within the dispatch layer to be created
	 * keyed on the process identifier and index
	 */
	CREATE_LOCAL_STATE,

	/**
	 * The message causes the removal of a state object within the dispatch
	 * layer, the layer to be removed is keyed on process identifier and index
	 * of the message
	 */
	REMOVE_LOCAL_STATE,

	/**
	 * The message causes the modification of a previously stored state object
	 * within the dispatch layer, the state object modified is keyed on process
	 * identifier and index of the message.
	 */
	UPDATE_LOCAL_STATE,

	/**
	 * The message causes a state object within the dispatch layer to be created
	 * keyed only on the process identifier and not on the index of the message.
	 */
	CREATE_PROCESS_STATE,

	/**
	 * The message causes a state object to be removed from the dispatch layer,
	 * the state object is identified only by the process identifier
	 */
	REMOVE_PROCESS_STATE,

	/**
	 * The message causes a state object to be modified, the state object is
	 * identified by process identifier only
	 */
	UPDATE_PROCESS_STATE,

	/**
	 * The message causes global level state to be modified within the dispatch
	 * layer
	 */
	UPDATE_GLOBAL_STATE,

	/**
	 * The message has no effect on state. This value is used when specifying
	 * that a message might cause effect or it might not, the interpretation of
	 * the various reaction annotations is that exactly one of the state effects
	 * will take place, so if the state side effect array isn't empty you have
	 * to insert this one to specify that it's possible that no state change
	 * will occur
	 */
	NO_EFFECT;
}
