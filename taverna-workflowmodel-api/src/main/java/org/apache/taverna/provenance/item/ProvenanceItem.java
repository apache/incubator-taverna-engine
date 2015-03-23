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
 * Used to store some enactment information during a workflow run
 * 
 * @see AbstractProvenanceItem
 * @author Ian Dunlop
 */
public interface ProvenanceItem {
	/**
	 * What type of information does the item contain. The
	 * {@link SharedVocabulary} can be used to identify it
	 * 
	 * @return
	 */
	SharedVocabulary getEventType();

	/**
	 * The unique identifier for this item
	 * 
	 * @return
	 */
	String getIdentifier();

	/**
	 * A unique id for this event. Any children would use this as their parentId
	 * 
	 * @param identifier
	 */
	void setIdentifier(String identifier);

	/**
	 * The workflow model id that is supplied during enactment eg
	 * facade0:dataflow2:processor1
	 * 
	 * @param processId
	 */
	void setProcessId(String processId);

	/**
	 * Get the enactor supplie identifier
	 * 
	 * @return
	 */
	String getProcessId();

	/**
	 * The parent of this provenance Item. The model is
	 * WorkflowProvenanceItem>ProcessProvenanceItem
	 * >ProcessorProvenanceItem>ActivityProvenanceITem
	 * >IterationProvenanceItem>DataProvenanceItem
	 * 
	 * Additionally there is a WorkflowDataProvenanceItem that is sent when the
	 * facade receives a completion event and a ErrorProvenanceItem when things
	 * go wrong
	 * 
	 * @param parentId
	 */
	void setParentId(String parentId);

	/**
	 * Who is the parent of this item?
	 * 
	 * @return
	 */
	String getParentId();

	/**
	 * The uuid that belongs to the actual dataflow
	 * 
	 * @param workflowId
	 */
	void setWorkflowId(String workflowId);

	/**
	 * The uuid that belongs to the actual dataflow
	 * 
	 * @return a string representation of a uuid
	 */
	String getWorkflowId();
}