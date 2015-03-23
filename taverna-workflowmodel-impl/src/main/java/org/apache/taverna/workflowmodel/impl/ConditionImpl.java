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

import static java.lang.Boolean.TRUE;

import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.annotation.AbstractAnnotatedThing;
import org.apache.taverna.workflowmodel.Condition;

class ConditionImpl extends AbstractAnnotatedThing<Condition> implements Condition {
	private ProcessorImpl control, target;
	private Map<String, Boolean> stateMap = new HashMap<>();

	protected ConditionImpl(ProcessorImpl control, ProcessorImpl target) {
		this.control = control;
		this.target = target;
	}

	@Override
	public ProcessorImpl getControl() {
		return this.control;
	}

	@Override
	public ProcessorImpl getTarget() {
		return this.target;
	}

	@Override
	public boolean isSatisfied(String owningProcess) {
		if (!stateMap.containsKey(owningProcess))
			return false;
		return stateMap.get(owningProcess);
	}

	protected void satisfy(String owningProcess) {
		stateMap.put(owningProcess, TRUE);
		// TODO - poke target processor here
	}
}
