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
package net.sf.taverna.t2.workflowmodel.health;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a summary of an individual health check.
 * <br>
 * A number of health checks are performed on a dataflow and its internal elements providing a sanity check before
 * invoking the dataflow itself. It can check such things as whether a service endpoint is accessible or whether a script parses.
 * <br>
 * A HealthReport can contain additional nested HealthReports to an unlimited depth, allowing multiple recursive tests to be performed as
 * part of an overall health check. If a HealthReport contains subreports then its overall status is taken as the most SEVERE of all its sub reports and itself.
 * 
 * @author Stuart Owen
 * @author David Withers
 *
 */
public class HealthReport {
	
	/**
	 * Enumeration of the possible status's in increasing severity: OK, WARNING,SEVERE 
	 */
	public enum Status {OK,WARNING,SEVERE};

	private String message;
	private Status status;
	private String subject;
	private List<HealthReport> subReports = new ArrayList<HealthReport>();
	
	/**
	 * Constructs the Health Report. The sub reports default to an empty list.
	 * @param subject - a String representing the thing being tested.
	 * @param message - a summary of the result of the test.
	 * @param status - the overall Status.
	 */
	public HealthReport(String subject, String message, Status status) {
		this(subject,message,status,new ArrayList<HealthReport>());
	}
	
	/**
	 * Constructs the HealthReport
	 * 
	 * @param subject - a String representing the thing being tested.
	 * @param message - a summary of the result of the test.
	 * @param status - the overall Status.
	 * @param subReports - a List of sub reports.
	 */
	public HealthReport(String subject, String message,Status status,  List<HealthReport> subReports) {
		this.subject=subject;
		this.status=status;
		this.message=message;
		this.subReports=subReports;
	}
	
	/**
	 * @return a message summarising the report
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the message
	 * @param message a message summarising the report
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Determines the overall Status. This is the most severe status of this report and all its sub reports.
	 * @return the overall status
	 */
	public Status getStatus() {
		Status result = status;
		for (HealthReport report : subReports) {
			if (report.getStatus().compareTo(result)>0) result=report.getStatus();
		}
		return result;
	}
	
	/**
	 * Sets the status of this report. Be aware that the overall status of this report may also be affected by its sub reports if they have a more severe Status.
	 * @param status
	 * @see #getStatus
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
	/**
	 * @return a String representing the subject of this health report
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * @param subject a String representing the subject of this health report
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * Provides a list of sub reports. This list defaults an empty list, so it is safe to add new reports through this method.
	 * @return a list of sub reports associated with this HealthReport
	 */
	public List<HealthReport> getSubReports() {
		return subReports;
	}
	/**
	 * Replaces the List of sub reports with those provided.
	 * @param subReports a list of sub reports
	 */
	public void setSubReports(List<HealthReport> subReports) {
		this.subReports = subReports;
	}
}
