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

import java.util.List;
import java.util.Map;

import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.DataflowValidationReport;
import org.apache.taverna.workflowmodel.TokenProcessingEntity;

public class DummyValidationReport implements DataflowValidationReport {
	private final boolean valid;

	public DummyValidationReport(boolean valid) {
		this.valid = valid;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public List<? extends TokenProcessingEntity> getUnsatisfiedEntities() {
		return null;
	}

	@Override
	public List<? extends DataflowOutputPort> getUnresolvedOutputs() {
		return null;
	}

	@Override
	public List<? extends TokenProcessingEntity> getFailedEntities() {
		return null;
	}

	@Override
	public Map<TokenProcessingEntity, DataflowValidationReport> getInvalidDataflows() {
		return null;
	}

	@Override
	public boolean isWorkflowIncomplete() {
		return false;
	}
}
