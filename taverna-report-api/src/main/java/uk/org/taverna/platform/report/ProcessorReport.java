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
 * @author Stian Soiland-Reyes
 */
@JsonPropertyOrder({ "subject", "parent", "state", "createdDate",
        "startedDate", "pausedDate", "pausedDates", "resumedDate",
        "resumedDates", "cancelledDate", "failedDate", "completedDate",
        "jobsQueued", "jobsStarted", "jobsCompleted",
        "jobsCompletedWithErrors", "invocations", "activityReports"})
public class ProcessorReport extends StatusReport<Processor, WorkflowReport> {
	private Set<ActivityReport> activityReports = new LinkedHashSet<>();
	private int jobsCompleted;
    private int jobsCompletedWithErrors;
    private int jobsQueued;
    private int jobsStarted;
    private SortedMap<String, Object> properties = new TreeMap<>();

    /**
	 * Constructs a new <code>ProcessorReport</code>.
	 *
	 * @param processor The processor to report on
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
	 * Returns the number of jobs that the processor has completed.
	 *
	 * @return the number of jobs that the processor has completed
	 */
	public int getJobsCompleted() {
	    return jobsCompleted;
	}

	/**
	 * Returns the number of jobs that completed with an error.
	 *
	 * @return the number of jobs that completed with an error
	 */
	public int getJobsCompletedWithErrors() {
	    return jobsCompletedWithErrors;
	}

	/**
	 * Returns the number of jobs queued by the processor.
	 *
	 * @return the number of jobs queued by the processor
	 */
	public int getJobsQueued() {
        return jobsQueued;
    }

	/**
	 * Returns the number of jobs that the processor has started processing.
	 *
	 * @return the number of jobs that the processor has started processing
	 */
	public int getJobsStarted() {
	    return jobsStarted;
	}

	public Object getProperty(String key) {
		return properties.get(key);
	}

	@JsonIgnore 
	public Set<String> getPropertyKeys() {
		return new HashSet<>(properties.keySet());
	}

	/**
     * Set the number of completed jobs.
     * 
     * @param jobsCompleted the number of jobs that the processor has completed.
     */
    public void setJobsCompleted(int jobsCompleted) {
        this.jobsCompleted = jobsCompleted;
    }

	/**
     * Set the number of jobs that have completed, but with errors.
     * 
     * @param jobsCompletedWithErrors the number of jobs that completed with errors
     */
    public void setJobsCompletedWithErrors(int jobsCompletedWithErrors) {
        this.jobsCompletedWithErrors = jobsCompletedWithErrors;
    }

	/**
     * Set the number of queued jobs.
     * 
     * @param jobsQueued the number of jobs queued by the processor
     */
    public void setJobsQueued(int jobsQueued) {
        this.jobsQueued = jobsQueued;
    }

	/**
     * Set the number of started jobs.
     * 
     * @param jobsStarted the number of jobs that the processor has started processing
     */
    public void setJobsStarted(int jobsStarted) {
        this.jobsStarted = jobsStarted;
    }

    /**
     * Set an additional property
     * 
     * @param key
     * @param value
     */
	public void setProperty(String key, Object value) {
		synchronized (properties) {
			// if (properties.containsKey(key)) {
			properties.put(key, value);
			// }
		}
	}
}
