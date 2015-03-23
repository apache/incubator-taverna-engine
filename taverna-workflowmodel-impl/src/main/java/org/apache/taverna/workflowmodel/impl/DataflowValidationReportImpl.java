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

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.List;
import java.util.Map;

import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.DataflowValidationReport;
import org.apache.taverna.workflowmodel.TokenProcessingEntity;

/**
 * Simple implementation of the DataflowValidationReport interface
 * 
 * @author Tom Oinn
 */
public class DataflowValidationReportImpl implements DataflowValidationReport {
	private final List<TokenProcessingEntity> failed;
	private final Map<TokenProcessingEntity, DataflowValidationReport> invalidDataflows;
	private final List<DataflowOutputPort> unresolvedOutputs;
	private final List<TokenProcessingEntity> unsatisfied;
	private boolean valid;
	/**
	 * whether a workflow is incomplete (contains no processors and no output
	 * ports), in which case it also must be invalid
	 */
	private boolean isWorkflowIncomplete;

	DataflowValidationReportImpl(
			boolean isValid,
			boolean isWorkflowIncomplete,
			List<TokenProcessingEntity> failedProcessors,
			List<TokenProcessingEntity> unsatisfiedProcessors,
			List<DataflowOutputPort> unresolvedOutputs,
			Map<TokenProcessingEntity, DataflowValidationReport> invalidDataflows) {
		this.valid = isValid;
		this.isWorkflowIncomplete = isWorkflowIncomplete;
		this.invalidDataflows = unmodifiableMap(invalidDataflows);
		this.failed = unmodifiableList(failedProcessors);
		this.unsatisfied = unmodifiableList(unsatisfiedProcessors);
		this.unresolvedOutputs = unmodifiableList(unresolvedOutputs);
	}

	@Override
	public List<? extends TokenProcessingEntity> getFailedEntities() {
		return failed;
	}

	@Override
	public Map<TokenProcessingEntity, DataflowValidationReport> getInvalidDataflows() {
		return invalidDataflows;
	}

	@Override
	public List<? extends DataflowOutputPort> getUnresolvedOutputs() {
		return unresolvedOutputs;
	}

	@Override
	public List<? extends TokenProcessingEntity> getUnsatisfiedEntities() {
		return unsatisfied;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public boolean isWorkflowIncomplete() {
		return isWorkflowIncomplete;
	}
}
