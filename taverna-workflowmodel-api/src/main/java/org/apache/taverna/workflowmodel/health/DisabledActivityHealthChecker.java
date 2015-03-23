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

package org.apache.taverna.workflowmodel.health;

import static org.apache.taverna.visit.VisitReport.Status.SEVERE;
import static org.apache.taverna.workflowmodel.health.HealthCheck.DISABLED;

import java.util.List;

import org.apache.taverna.visit.VisitReport;
import org.apache.taverna.workflowmodel.processor.activity.DisabledActivity;

/**
 * Check on the health of a DisabledActivity
 * 
 * @author alanrw
 * 
 */
public class DisabledActivityHealthChecker implements
		HealthChecker<DisabledActivity> {

	/**
	 * The visitor can visit DisabledActivitys.
	 */
	@Override
	public boolean canVisit(Object o) {
		return ((o != null) && (o instanceof DisabledActivity));
	}

	/**
	 * The check is not time consuming as it simply constructs a VisitReport
	 */
	@Override
	public boolean isTimeConsuming() {
		return false;
	}

	/**
	 * The result of the visit is simply a VisitReport to state that the service
	 * is not available.
	 */
	@Override
	public VisitReport visit(DisabledActivity o, List<Object> ancestry) {
		return new VisitReport(HealthCheck.getInstance(), o,
				"Service is not available", DISABLED, SEVERE);
	}
}
