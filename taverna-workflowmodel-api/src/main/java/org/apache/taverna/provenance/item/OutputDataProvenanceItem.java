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
 * Contains details of port names and the output data they receive. Parent is an
 * {@link IterationProvenanceItem}
 * 
 * @author Paolo Missier
 * @author Stuart Owen
 * @author Ian Dunlop
 */
public class OutputDataProvenanceItem extends DataProvenanceItem {
	private SharedVocabulary eventType = SharedVocabulary.OUTPUTDATA_EVENT_TYPE;

	/**
	 * Used when generating the xml version by the {@link DataProvenanceItem}.
	 * Identifies this {@link DataProvenanceItem} as containing output
	 */
	@Override
	protected boolean isInput() {
		return false;
	}

	@Override
	public SharedVocabulary getEventType() {
		return eventType;
	}
}
