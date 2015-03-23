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

package org.apache.taverna.reference.impl;

import static org.apache.taverna.reference.T2ReferenceType.ErrorDocument;
import static org.apache.taverna.reference.T2ReferenceType.IdentifiedList;
import static org.apache.taverna.reference.T2ReferenceType.ReferenceSet;

import java.util.List;

import org.apache.taverna.reference.ReferenceContext;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.reference.T2ReferenceGenerator;
import org.apache.taverna.reference.WorkflowRunIdEntity;

/**
 * An abstract class for implementing simple {@link T2ReferenceGenerator}s.
 * 
 * @author Stian Soiland-Reyes
 */
public abstract class AbstractT2ReferenceGenerator implements
		T2ReferenceGenerator {
	public AbstractT2ReferenceGenerator() {
		super();
	}

	private void initReferenceNamespace(T2ReferenceImpl r, ReferenceContext context) {
		if (context == null) {
			// this is not good, just use the default namespace
			r.setNamespacePart(getNamespace());
			return;
		}

		List<WorkflowRunIdEntity> workflowRunIdEntities = context
				.getEntities(WorkflowRunIdEntity.class);
		if (workflowRunIdEntities == null || workflowRunIdEntities.isEmpty()) {
			// this is not good, just use the default namespace
			r.setNamespacePart(getNamespace());
			return;
		}

		// there should be only one wf run id entity
		String workflowRunId = ((WorkflowRunIdEntity) workflowRunIdEntities
				.get(0)).getWorkflowRunId();
		r.setNamespacePart(workflowRunId);
	}

	@Override
	public synchronized T2Reference nextReferenceSetReference(
			ReferenceContext context) {
		T2ReferenceImpl r = new T2ReferenceImpl();
		initReferenceNamespace(r, context);
		r.setLocalPart(getNextLocalPart());
		r.setReferenceType(ReferenceSet);
		r.setDepth(0);
		r.setContainsErrors(false);
		return r;
	}

	/**
	 * Generate a new local part for a new {@link T2Reference reference}. The
	 * local part should be unique within this
	 * {@link T2ReferenceGenerator#getNamespace() namespace}.
	 * 
	 * @return A new, unique local part to identify a new reference.
	 */
	protected abstract String getNextLocalPart();

	@Override
	public T2Reference nextListReference(boolean containsErrors, int listDepth,
			ReferenceContext context) {
		T2ReferenceImpl r = new T2ReferenceImpl();
		initReferenceNamespace(r, context);
		r.setLocalPart(getNextLocalPart());
		r.setReferenceType(IdentifiedList);
		r.setDepth(listDepth);
		r.setContainsErrors(containsErrors);
		return r;
	}

	@Override
	public T2Reference nextErrorDocumentReference(int depth,
			ReferenceContext context) {
		T2ReferenceImpl r = new T2ReferenceImpl();
		initReferenceNamespace(r, context);
		r.setLocalPart(getNextLocalPart());
		r.setReferenceType(ErrorDocument);
		r.setDepth(depth);
		// This is an error document, it contains errors by definition
		r.setContainsErrors(true);
		return r;
	}
}
