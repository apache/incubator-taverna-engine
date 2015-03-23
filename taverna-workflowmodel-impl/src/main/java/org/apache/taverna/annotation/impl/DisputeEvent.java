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

package org.apache.taverna.annotation.impl;

import org.apache.taverna.annotation.Curateable;
import org.apache.taverna.annotation.CurationEvent;
import org.apache.taverna.annotation.CurationEventType;

public class DisputeEvent implements CurationEvent<DisputeEventDetails> {
	private DisputeEventDetails disputeEventDetails;
	private CurationEventType curationEventType;
	private Curateable targetEvent;

	public DisputeEvent() {
	}

	public DisputeEvent(DisputeEventDetails disputeEventDetails,
			CurationEventType curationEventType, Curateable targetEvent) {
		this.disputeEventDetails = disputeEventDetails;
		this.curationEventType = curationEventType;
		this.targetEvent = targetEvent;
	}

	@Override
	public DisputeEventDetails getDetail() {
		return disputeEventDetails;
	}

	@Override
	public Curateable getTarget() {
		return targetEvent;
	}

	@Override
	public CurationEventType getType() {
		return curationEventType;
	}

	public void setDisputeEventDetails(DisputeEventDetails disputeEventDetails) {
//		if (disputeEventDetails != null)
//			throw new RuntimeException("Dispute event details have already been set");
		this.disputeEventDetails = disputeEventDetails;
	}

	public void setCurationEventType(CurationEventType curationEventType) {
//		if (curationEventType != null)
//			throw new RuntimeException("Curation event details have already been set");
		this.curationEventType = curationEventType;
	}

	public void setTargetEvent(Curateable targetEvent) {
//		if (targetEvent!= null)
//			throw new RuntimeException("Target event details have already been set");
		this.targetEvent = targetEvent;
	}
}
