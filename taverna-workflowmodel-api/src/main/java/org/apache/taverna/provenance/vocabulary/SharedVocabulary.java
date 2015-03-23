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

package org.apache.taverna.provenance.vocabulary;

import org.apache.taverna.provenance.item.ProvenanceItem;

/**
 * Static strings which identify all the {@link ProvenanceItem}s and
 * {@link ProvenanceEventType}s
 * 
 * @author Paolo Missier
 * @author Ian Dunlop
 */
public enum SharedVocabulary {
	DATAFLOW_EVENT_TYPE, PROCESS_EVENT_TYPE, PROVENANCE_EVENT_TYPE, ACTIVITY_EVENT_TYPE, DATA_EVENT_TYPE, ERROR_EVENT_TYPE, INMEMORY_EVENT_TYPE, INPUTDATA_EVENT_TYPE, ITERATION_EVENT_TYPE, OUTPUTDATA_EVENT_TYPE, PROCESSOR_EVENT_TYPE, WEBSERVICE_EVENT_TYPE, WORKFLOW_DATA_EVENT_TYPE, WORKFLOW_EVENT_TYPE, END_WORKFLOW_EVENT_TYPE, INVOCATION_STARTED_EVENT_TYPE;
}
