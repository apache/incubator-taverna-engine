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

import java.util.List;

import net.sf.taverna.t2.workflowmodel.health.HealthReport;

/**
 * A HealthReport assocatied with Processors.<br>
 * In particular the behaviour for producing an overall status is specialised.
 * @author Stuart Owen
 *
 * @see ProcessorHealthReport#getStatus()
 */
public class ProcessorHealthReport extends HealthReport {

	public ProcessorHealthReport(String subject,List<HealthReport> activityHealthReports) {
		super(subject,"",Status.OK,activityHealthReports);
		
	}

	/**
	 * the overall status is SEVERE if all sub reports are SEVERE, OK if all are OK, otherwise WARNING.
	 * return 
	 */
	@Override
	public Status getStatus() {
		Status result = super.getStatus();
		int severeCount = 0;
		for (HealthReport report : getSubReports()) {
			if (report.getStatus()!=Status.OK) {
				result = Status.WARNING;
			}
			if (report.getStatus()==Status.SEVERE) severeCount++;
		}
		if (severeCount==getSubReports().size()) result=Status.SEVERE;
		return result;
	}
	
	
}
