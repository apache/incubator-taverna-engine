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

package org.apache.taverna.provenance.reporter;

import java.util.List;

import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.provenance.item.ProvenanceItem;
import org.apache.taverna.provenance.item.WorkflowProvenanceItem;
import org.apache.taverna.reference.ReferenceService;

public interface ProvenanceReporter {
	/**
	 * Add a {@link ProvenanceItem} to the connector
	 * 
	 * @param provenanceItem
	 * @param invocationContext
	 */
	void addProvenanceItem(ProvenanceItem provenanceItem);

	// FIXME is this reference service really needed since we have the context?
	/**
	 * Tell the connector what {@link ReferenceService} it should use when
	 * trying to dereference data items inside {@link ProvenanceItem}s
	 * 
	 * @param referenceService
	 */
	void setReferenceService(ReferenceService referenceService);

	/**
	 * Get the {@link ReferenceService} in use by this connector
	 * 
	 * @return
	 */
	ReferenceService getReferenceService();

	/**
	 * Get all the {@link ProvenanceItem}s that the connector currently knows
	 * about
	 * 
	 * @return
	 */
	List<ProvenanceItem> getProvenanceCollection();

	/**
	 * Set the {@link InvocationContext} that this reporter should be using
	 * 
	 * @param invocationContext
	 */
	void setInvocationContext(InvocationContext invocationContext);

	/**
	 * Get the {@link InvocationContext} that this reporter should be using if
	 * it needs to dereference any data
	 * 
	 * @return
	 */
	InvocationContext getInvocationContext();

	/**
	 * A unique identifier for this run of provenance, should correspond to the
	 * initial {@link WorkflowProvenanceItem} idenifier that gets sent through
	 * 
	 * @param identifier
	 */
	void setSessionID(String sessionID);

	/**
	 * What is the unique identifier used by this connector
	 * 
	 * @return
	 */
	String getSessionID();
}
