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

package org.apache.taverna.platform.report;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.taverna.scufl2.api.core.Processor;

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
