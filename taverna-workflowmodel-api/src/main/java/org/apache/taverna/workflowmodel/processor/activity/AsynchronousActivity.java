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

import org.apache.taverna.reference.T2Reference;

/**
 * A concrete invokable activity with an asynchronous invocation API and no
 * knowledge of invocation context. This is the most common concrete activity
 * type in Taverna 2, it has no knowledge of any enclosing iteration or other
 * handling process. The activity may stream results in the sense that it can
 * use the AsynchronousActivityCallback object to push multiple results followed
 * by a completion event. If a completion event is received by the callback
 * before any data events the callback will insert a data event containing empty
 * collections of the appropriate depth.
 * 
 * @param <ConfigurationType>
 *            the ConfigurationType associated with the Activity.
 * @author Tom Oinn
 */
public interface AsynchronousActivity<ConfigurationType> extends
		Activity<ConfigurationType> {
	/**
	 * Invoke the activity in an asynchronous manner. The activity uses the
	 * specified ActivityCallback object to push results, errors and completion
	 * events back to the dispatch stack.
	 */
	void executeAsynch(Map<String, T2Reference> data,
			AsynchronousActivityCallback callback);
}
