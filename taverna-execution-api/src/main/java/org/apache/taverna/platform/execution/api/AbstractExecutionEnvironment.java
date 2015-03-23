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

import java.net.URI;

/**
 * A common super type for concrete implementations of <code>ExecutionEnvironment</code>s.
 *
 * @author David Withers
 */
public abstract class AbstractExecutionEnvironment implements ExecutionEnvironment {
	private final String ID;
	private final String name;
	private final String description;
	private final ExecutionService executionService;

	public AbstractExecutionEnvironment(String ID, String name, String description,
			ExecutionService executionService) {
		this.ID = ID;
		this.name = name;
		this.description = description;
		this.executionService = executionService;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public ExecutionService getExecutionService() {
		return executionService;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ID + "\n");
		sb.append(name + "\n");
		sb.append(description + "\n");
		sb.append("Activities : \n");
		for (URI uri : getActivityTypes())
			sb.append("  " + uri + "\n");
		sb.append("Dispatch Layers : \n");
		for (URI uri : getDispatchLayerTypes())
			sb.append("  " + uri + "\n");
		return sb.toString();
	}

}
