/*******************************************************************************
 * Copyright (C) 2010 The University of Manchester   
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
package uk.org.taverna.platform.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.org.taverna.scufl2.api.core.Processor;

/**
 * 
 * @author David Withers
 */
public abstract class ProcessorReport extends StatusReport {

	private final Processor processor;

	private final WorkflowReport parentReport;

	private Set<ActivityReport> activityReports = new HashSet<ActivityReport>();

	private Map<String, Object> properties = new HashMap<String, Object>();

	public ProcessorReport(Processor processor, WorkflowReport parentReport) {
		this.processor = processor;
		this.parentReport = parentReport;
	}

	/**
	 * @return the processor
	 */
	public Processor getProcessor() {
		return processor;
	}

	/**
	 * @return the parentReport
	 */
	public WorkflowReport getParentReport() {
		return parentReport;
	}

	public void addActivityReport(ActivityReport processorReport) {
		activityReports.add(processorReport);
	}
	
	/**
	 * @return the activityReports
	 */
	public Set<ActivityReport> getActivityReports() {
		return activityReports;
	}

	/**
	 * Returns the number of jobs queued by the processor.
	 * 
	 * @return the number of jobs queued by the processor
	 */
	public abstract int getJobsQueued();

	/**
	 * Returns the number of jobs that the processor has started processing.
	 * 
	 * @return the number of jobs that the processor has started processing
	 */
	public abstract int getJobsStarted();

	/**
	 * Returns the number of jobs that the processor has completed.
	 * 
	 * @return the number of jobs that the processor has completed
	 */
	public abstract int getJobsCompleted();

	/**
	 * Returns the number of jobs that completed with an error.
	 * 
	 * @return the number of jobs that completed with an error
	 */
	public abstract int getJobsCompletedWithErrors();

	public Set<String> getPropertyKeys() {
		return new HashSet<String>(properties.keySet());
	}

	public Object getProperty(String key) {
		return properties.get(key);
	}

	public void setProperty(String key, Object value) {
		synchronized (properties) {
			// if (properties.containsKey(key)) {
			properties.put(key, value);
			// }
		}
	}

}
