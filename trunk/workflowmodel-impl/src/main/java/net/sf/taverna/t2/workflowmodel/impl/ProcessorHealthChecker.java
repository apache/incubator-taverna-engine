/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.workflowmodel.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.health.HealthChecker;
import net.sf.taverna.t2.workflowmodel.health.HealthCheckerFactory;
import net.sf.taverna.t2.workflowmodel.health.HealthReport;
import net.sf.taverna.t2.workflowmodel.health.HealthReport.Status;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * A Health Checker associated with Processors. This iterates over the processor activities
 * invoking each HealthChecker available for each Activity to generate an overal ProcessorHealthReport
 * @author Stuart Owen
 *
 */
public class ProcessorHealthChecker implements HealthChecker<Processor> {

	public boolean canHandle(Object subject) {
		return subject!=null && subject instanceof Processor;
	}

	@SuppressWarnings("unchecked")
	public HealthReport checkHealth(Processor subject) {
		List<HealthReport> activityReports = new ArrayList<HealthReport>();
		for (Activity<?> a : subject.getActivityList()) {
			List<HealthChecker<?>> checkers = HealthCheckerFactory
					.getInstance().getHealthCheckersForObject(a);
			if (checkers.size() > 0) {
				List<HealthReport> reports = new ArrayList<HealthReport>();
				for (HealthChecker checker : checkers) {
					reports.add(checker.checkHealth(a));
				}
				if (reports.size() == 1) {
					activityReports.add(reports.get(0));
				} else {
					activityReports.add(new HealthReport("Activity tests...", "",
							Status.OK, reports));
				}
			}
		}
		HealthReport processorHealthReport = new ProcessorHealthReport(
				subject.getLocalName() + " Processor", activityReports);
		return processorHealthReport;
	}

}
