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
import org.apache.taverna.workflowmodel.processor.activity.Activity;

/**
 * Contains details for an enacted Activity. Parent is a
 * {@link ProcessorProvenanceItem}. Children are {@link IterationProvenanceItem}
 * s. There could be multiple {@link ActivityProvenanceItem}s for each
 * {@link ProcessorProvenanceItem}
 * 
 * @author Ian Dunlop
 * @author Stuart Owen
 * @author Paolo Missier
 */
public class ActivityProvenanceItem extends AbstractProvenanceItem implements ProvenanceItem  {
	private Activity<?> activity;
	private IterationProvenanceItem iterationProvenanceItem;
	
	public void setIterationProvenanceItem(
			IterationProvenanceItem iterationProvenanceItem) {
		this.iterationProvenanceItem = iterationProvenanceItem;
	}

	public IterationProvenanceItem getIterationProvenanceItem() {
		return iterationProvenanceItem;
	}

	@Override
	public SharedVocabulary getEventType() {
		return SharedVocabulary.ACTIVITY_EVENT_TYPE;
	}

	public Activity<?> getActivity() {
		return activity;
	}

	public void setActivity(Activity<?> activity) {
		this.activity = activity;
	}

}
