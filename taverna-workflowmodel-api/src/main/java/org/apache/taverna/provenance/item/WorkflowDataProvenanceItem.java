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
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;

/**
 * When the {@link WorkflowInstanceFacade} for a processor receives a data token
 * one of these is created. This is especially important for data which flows
 * straight through a facade without going into the dispatch stack (a rare event
 * but it can happen)
 * 
 * @author Ian Dunlop
 */
public class WorkflowDataProvenanceItem extends AbstractProvenanceItem {
	private ReferenceService referenceService;
	/** The port name that the data is for */
	private String portName;
	/** A reference to the data token received in the facade */
	private T2Reference data;
	private SharedVocabulary eventType = SharedVocabulary.WORKFLOW_DATA_EVENT_TYPE;
	private boolean isFinal;
	private int[] index;
	private boolean isInputPort;

	public boolean isInputPort() {
		return isInputPort;
	}

	public void setInputPort(boolean isInputPort) {
		this.isInputPort = isInputPort;
	}

	public WorkflowDataProvenanceItem() {
	}

	@Override
	public SharedVocabulary getEventType() {
		return eventType;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getPortName() {
		return portName;
	}

	public void setData(T2Reference data) {
		this.data = data;
	}

	public T2Reference getData() {
		return data;
	}

	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}

	public ReferenceService getReferenceService() {
		return referenceService;
	}

	public void setIndex(int[] index) {
		this.index = index;
	}

	public int[] getIndex() {
		return index;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isFinal() {
		return isFinal;
	}
}
