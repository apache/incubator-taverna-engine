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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import uk.org.taverna.scufl2.api.core.Processor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Report about the {@link State} of a {@link Processor} invocation.
 *
 * @author David Withers
 */
@JsonPropertyOrder({ "subject", "parent", "state", "createdDate",
        "startedDate", "pausedDate", "pausedDates", "resumedDate",
        "resumedDates", "cancelledDate", "failedDate", "completedDate",
        "jobsQueued", "jobsStarted", "jobsCompleted",
        "jobsCompletedWithErrors", "invocations", "activityReports"})
public abstract class ProcessorReport extends StatusReport<Processor, WorkflowReport> {

	private Set<ActivityReport> activityReports = new LinkedHashSet<>();

	private SortedMap<String, Object> properties = new TreeMap<>();

	/**
	 * Constructs a new <code>ProcessorReport</code>.
	 *
	 * @param processor
	 */
	public ProcessorReport(Processor processor) {
		super(processor);
	}

	public void addActivityReport(ActivityReport activityReport) {
		activityReports.add(activityReport);
	}

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

	@JsonIgnore 
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
