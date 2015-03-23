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
 * Each Processor inside a workflow will have one of these for each provenance
 * run. Its parent is a {@link ProcessProvenanceItem} and child is an
 * {@link ActivityProvenanceItem}. In theory there could be more than one
 * {@link ActivityProvenanceItem} per processor to cope with failover etc
 * 
 * @author Ian Dunlop
 * @author Stuart Owen
 * @author Paolo Missier
 */
public class ProcessorProvenanceItem extends AbstractProvenanceItem {
	private ActivityProvenanceItem activityProvenanceItem;
	private String identifier;
	private SharedVocabulary eventType = SharedVocabulary.PROCESSOR_EVENT_TYPE;

	public void setActivityProvenanceItem(
			ActivityProvenanceItem activityProvenanceItem) {
		this.activityProvenanceItem = activityProvenanceItem;
	}

	public ActivityProvenanceItem getActivityProvenanceItem() {
		return activityProvenanceItem;
	}

	public String getProcessorID() {
		return identifier;
	}

	@Override
	public SharedVocabulary getEventType() {
		return eventType;
	}
}
