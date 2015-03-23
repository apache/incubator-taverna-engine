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

package org.apache.taverna.provenance.item;

import org.apache.taverna.provenance.vocabulary.SharedVocabulary;

public abstract class AbstractProvenanceItem implements ProvenanceItem {
	@Override
	public abstract SharedVocabulary getEventType();

	private String identifier, parentId, processId, workflowId;

	@Override
	public final String getIdentifier() {
		return identifier;
	}

	@Override
	public int hashCode() {
		return 31 + (identifier == null ? 0 : identifier.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractProvenanceItem other = (AbstractProvenanceItem) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

	@Override
	public final void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public final String getParentId() {
		return parentId;
	}

	@Override
	public final void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public final String getProcessId() {
		return processId;
	}

	@Override
	public final void setProcessId(String processId) {
		this.processId = processId;
	}

	@Override
	public final String getWorkflowId() {
		return workflowId;
	}

	@Override
	public final void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	
	@Override
	public String toString() {
		return getEventType() + " id:" + getIdentifier() + " parent:" + getParentId();
	}
}
