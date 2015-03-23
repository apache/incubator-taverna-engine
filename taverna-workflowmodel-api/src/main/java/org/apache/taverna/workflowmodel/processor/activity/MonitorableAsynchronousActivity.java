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

package org.apache.taverna.workflowmodel.processor.activity;

import java.util.Map;
import java.util.Set;

import org.apache.taverna.monitor.MonitorableProperty;
import org.apache.taverna.reference.T2Reference;

/**
 * An extension of AsynchronousActivity with the additional stipulation that
 * implementing classes must return a set of monitorable properties for the
 * activity invocation instance when invoked. This allows for deep state
 * management, where the monitor state extends out from the workflow engine into
 * the remote resources themselves and is dependant on the resource proxied by
 * the activity implementation providing this information.
 * 
 * @author Tom Oinn
 */
public interface MonitorableAsynchronousActivity<ConfigType> extends
		AsynchronousActivity<ConfigType> {
	/**
	 * This has the same invocation semantics as
	 * {@link AsynchronousActivity}<code>.executeAsynch</code> and all
	 * implementations should also implement that method, with the difference
	 * that this one returns immediately with a set of monitorable properties
	 * which represent monitorable or steerable state within the invocation
	 * itself.
	 * 
	 * @param data
	 * @param callback
	 * @return a set of monitorable properties representing internal state of
	 *         the invoked resource
	 */
	Set<MonitorableProperty<?>> executeAsynchWithMonitoring(
			Map<String, T2Reference> data, AsynchronousActivityCallback callback);
}
