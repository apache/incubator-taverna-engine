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

package org.apache.taverna.invocation.impl;

import static java.util.Collections.synchronizedList;

import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.provenance.reporter.ProvenanceReporter;
import org.apache.taverna.reference.ReferenceService;

public class InvocationContextImpl implements InvocationContext {
	private final ReferenceService referenceService;
	private final ProvenanceReporter provenanceReporter;
	private List<Object> entities = synchronizedList(new ArrayList<Object>());

	public InvocationContextImpl(ReferenceService referenceService,
			ProvenanceReporter provenanceReporter) {
		this.referenceService = referenceService;
		this.provenanceReporter = provenanceReporter;
	}

	@Override
	public ReferenceService getReferenceService() {
		return referenceService;
	}

	@Override
	public ProvenanceReporter getProvenanceReporter() {
		return provenanceReporter;
	}

	@Override
	public <T extends Object> List<T> getEntities(Class<T> entityType) {
		List<T> entitiesOfType = new ArrayList<>();
		synchronized (entities) {
			for (Object entity : entities)
				if (entityType.isInstance(entity))
					entitiesOfType.add(entityType.cast(entity));
		}
		return entitiesOfType;
	}

	@Override
	public void addEntity(Object entity) {
		entities.add(entity);
	}

	public void removeEntity(Object entity) {
		entities.remove(entity);
	}
}
