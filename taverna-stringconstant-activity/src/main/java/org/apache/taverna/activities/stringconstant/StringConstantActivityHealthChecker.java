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

package org.apache.taverna.activities.stringconstant;

import java.util.List;

import org.apache.taverna.workflowmodel.Processor;
import org.apache.taverna.workflowmodel.health.HealthCheck;
import org.apache.taverna.workflowmodel.health.HealthChecker;
import org.apache.taverna.visit.VisitReport;
import org.apache.taverna.visit.VisitReport.Status;

public class StringConstantActivityHealthChecker implements HealthChecker<StringConstantActivity> {

	public boolean canVisit(Object subject) {
		return subject!=null && subject instanceof StringConstantActivity;
	}

	public VisitReport visit(StringConstantActivity activity, List<Object> ancestors) {
		Processor p = (Processor) VisitReport.findAncestor(ancestors, Processor.class);
		if (p == null) {
			return null;
		}
		String value = activity.getConfiguration().get("string").asText();
		if (value==null) {
			return new VisitReport(HealthCheck.getInstance(), p,"No value", HealthCheck.NULL_VALUE, Status.SEVERE);
		}
		if ("Add your own value here".equals(value)) {
			VisitReport vr = new VisitReport(HealthCheck.getInstance(), p, "Default value", HealthCheck.DEFAULT_VALUE, Status.WARNING);
			vr.setProperty("value", value);
			return vr;
		}
		return new VisitReport(HealthCheck.getInstance(), p, "StringConstant is OK", HealthCheck.NO_PROBLEM, Status.OK);
	}

	public boolean isTimeConsuming() {
		return false;
	}

}
