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

package org.apache.taverna.workflowmodel.processor.dispatch.events;

/**
 * A simple enumeration of possible failure classes, used to determine whether
 * fault handling dispatch layers should attempt to handle a given failure
 * message.
 * 
 * @author Tom Oinn
 */
public enum DispatchErrorType {
	/**
	 * Indicates that the failure to invoke the activity was due to invalid
	 * input data, in this case there is no point in trying to invoke the
	 * activity again with the same data as it will always fail. Fault handling
	 * layers such as retry should pass this error type through directly; layers
	 * such as failover handlers should handle it as the input data may be
	 * applicable to other activities within the processor.
	 */
	DATA,

	/**
	 * Indicates that the failure was related to the invocation of the resource
	 * rather than the input data, and that an identical invocation at a later
	 * time may succeed.
	 */
	INVOCATION,

	/**
	 * Indicates that the failure was due to missing or incorrect authentication
	 * credentials and that retrying the activity invocation without modifying
	 * the credential set is pointless.
	 */
	AUTHENTICATION;
}
