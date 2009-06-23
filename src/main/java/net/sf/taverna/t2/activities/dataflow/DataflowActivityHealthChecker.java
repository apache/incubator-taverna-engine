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
package net.sf.taverna.t2.activities.dataflow;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.health.HealthChecker;
import net.sf.taverna.t2.workflowmodel.health.HealthCheckerFactory;
import net.sf.taverna.t2.workflowmodel.health.HealthReport;
import net.sf.taverna.t2.workflowmodel.health.HealthReport.Status;

public class DataflowActivityHealthChecker implements HealthChecker<DataflowActivity> {

	public boolean canHandle(Object subject) {
		return subject!=null && subject instanceof DataflowActivity;
	}

	@SuppressWarnings("unchecked")
	public HealthReport checkHealth(DataflowActivity activity) {
		Dataflow dataflow = activity.getConfiguration();
		Status status = Status.OK;
		String message = "Everything seems fine";
		List<HealthReport> subReports = new ArrayList<HealthReport>();
		for (Processor processor : dataflow.getProcessors()) {
			for (HealthChecker checker : HealthCheckerFactory.getInstance().getHealthCheckersForObject(processor)) {
				HealthReport subReport = checker.checkHealth(processor);
				if (subReport.getStatus().equals(Status.WARNING)) {
					if (status.equals(Status.OK)) {
						status = Status.WARNING;
						message = "Some warnings reported";
					}
				} else if (subReport.getStatus().equals(Status.SEVERE)) {
					status = Status.SEVERE;
					message = "We have a problem";
				}
				subReports.add(subReport);
			}
			
		}
		return new HealthReport("Dataflow Activity", message, status, subReports);
	}

}
