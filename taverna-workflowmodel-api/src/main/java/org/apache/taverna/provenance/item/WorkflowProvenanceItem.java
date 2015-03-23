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

import java.sql.Timestamp;

import org.apache.taverna.facade.WorkflowInstanceFacade;
import org.apache.taverna.provenance.vocabulary.SharedVocabulary;
import org.apache.taverna.workflowmodel.Dataflow;

/**
 * The first {@link ProvenanceItem} that the {@link ProvenanceConnector} will
 * receive for a workflow run. Contains the {@link Dataflow} itself as well as
 * the process id for the {@link WorkflowInstanceFacade} (facadeX:dataflowY).
 * Its child is a {@link ProcessProvenanceItem} and parent is the UUID of the
 * {@link Dataflow} itself
 * 
 * @author Ian Dunlop
 * @author Paolo Missier
 * @author Stuart Owen
 */
public class WorkflowProvenanceItem extends AbstractProvenanceItem {
	private Dataflow dataflow;
	private SharedVocabulary eventType = SharedVocabulary.WORKFLOW_EVENT_TYPE;
	private int[] index;
	private boolean isFinal;

	private Timestamp invocationStarted;

	public Timestamp getInvocationStarted() {
		return invocationStarted;
	}

	public WorkflowProvenanceItem() {
	}

	public Dataflow getDataflow() {
		return dataflow;
	}

	public void setDataflow(Dataflow dataflow) {
		this.dataflow = dataflow;
	}

	@Override
	public SharedVocabulary getEventType() {
		return eventType;
	}

	/**
	 * @return the index
	 */
	public int[] getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int[] index) {
		this.index = index;
	}

	/**
	 * @return the isFinal
	 */
	public boolean isFinal() {
		return isFinal;
	}

	/**
	 * @param isFinal
	 *            the isFinal to set
	 */
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public void setInvocationStarted(Timestamp invocationStarted) {
		this.invocationStarted = invocationStarted;
	}
}
