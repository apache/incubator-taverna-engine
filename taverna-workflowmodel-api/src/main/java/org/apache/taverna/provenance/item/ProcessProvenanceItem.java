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

/**
 * Each time a job is received by the dispatch stack one of these will be
 * created. It has a {@link ProcessorProvenanceItem} as its child. Its parent is
 * a {@link WorkflowProvenanceItem} which in turn knows the unique id of the
 * workflow the provenance is being stored for. NOTE: May be superfluous since
 * it essentially mimics the behaviour of its child item but may be more hastle
 * than it is worth to remove it
 * 
 * @author Stuart owen
 * @author Paolo Missier
 * @author Ian Dunlop
 */
public class ProcessProvenanceItem extends AbstractProvenanceItem {
	private String owningProcess;
	private ProcessorProvenanceItem processorProvenanceItem;
	private String facadeID;
	private String dataflowID;
	private SharedVocabulary eventType = SharedVocabulary.PROCESS_EVENT_TYPE;

	/**
	 * As {@link WorkflowInstanceFacade}s are created for a Processor the
	 * details are appended to the owning process identifier. This is in the
	 * form facadeX:dataflowY:ProcessorZ etc. This method returns the facadeX
	 * part.
	 * 
	 * @return
	 */
	public String getFacadeID() {
		return facadeID;
	}

	public void setProcessorProvenanceItem(
			ProcessorProvenanceItem processorProvenanceItem) {
		this.processorProvenanceItem = processorProvenanceItem;
	}

	public ProcessorProvenanceItem getProcessorProvenanceItem() {
		return processorProvenanceItem;
	}

	@Override
	public SharedVocabulary getEventType() {
		return eventType;
	}

	public String getOwningProcess() {
		return owningProcess;
	}

	public void setOwningProcess(String owningProcess) {
		this.owningProcess = owningProcess;
	}

	public void setFacadeID(String facadeID) {
		this.facadeID = facadeID;
	}

	public void setDataflowID(String dataflowID) {
		this.dataflowID = dataflowID;
	}

	public String getDataflowID() {
		return dataflowID;
	}
}
