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
package net.sf.taverna.t2.activities.stringconstant;

import java.util.List;

import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.health.HealthCheck;
import net.sf.taverna.t2.workflowmodel.health.HealthChecker;
import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.visit.VisitReport.Status;

public class StringConstantActivityHealthChecker implements HealthChecker<StringConstantActivity> {

	public boolean canVisit(Object subject) {
		return subject!=null && subject instanceof StringConstantActivity;
	}

	public VisitReport visit(StringConstantActivity activity, List<Object> ancestors) {
		Processor p = (Processor) VisitReport.findAncestor(ancestors, Processor.class);
		if (p == null) {
			return null;
		}
		String value = activity.getConfiguration().getValue();
		if (value==null) {
			return new VisitReport(HealthCheck.getInstance(), p,"Null value", HealthCheck.NULL_VALUE, Status.SEVERE);
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
