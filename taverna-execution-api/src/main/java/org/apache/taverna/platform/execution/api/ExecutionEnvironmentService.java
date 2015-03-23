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

import java.util.Set;

import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * Service for finding <code>ExecutionEnvironment</code>s.
 *
 * @author David Withers
 */
public interface ExecutionEnvironmentService {

	/**
	 * Returns the available <code>ExecutionEnvironment</code>s.
	 *
	 * @return the available <code>ExecutionEnvironment</code>s
	 */
	public Set<ExecutionEnvironment> getExecutionEnvironments();

	/**
	 * Returns the <code>ExecutionEnvironment</code>s that can execute the specified
	 * <code>Profile</code>.
	 *
	 * @param profile
	 *            the <code>Profile</code> to find <code>ExecutionEnvironment</code>s for
	 * @return the <code>ExecutionEnvironment</code>s that can execute a workflow with the specified
	 *         <code>Profile</code>
	 */
	public Set<ExecutionEnvironment> getExecutionEnvironments(Profile profile);

}
