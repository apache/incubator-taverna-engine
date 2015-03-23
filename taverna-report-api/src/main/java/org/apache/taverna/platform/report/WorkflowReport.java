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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.scufl2.api.core.Workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Report about the {@link State} of a {@link Workflow} run.
 *
 * @author David Withers
 */
public class WorkflowReport extends StatusReport<Workflow, ActivityReport> {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(WorkflowReport.class.getName());
	private static final String dateFormatString = "yyyy-MM-dd HH:mm:ss";

	private Set<ProcessorReport> processorReports = new LinkedHashSet<>();
	private Bundle dataBundle;

	public WorkflowReport(Workflow workflow) {
		super(workflow);
	}

	public Set<ProcessorReport> getProcessorReports() {
		return processorReports;
	}

	public void addProcessorReport(ProcessorReport processorReport) {
		processorReports.add(processorReport);
	}

	@JsonIgnore
	public Bundle getDataBundle() {
		return dataBundle;
	}

	public void setDataBundle(Bundle dataBundle) {
		this.dataBundle = dataBundle;
	}

	@Override
	public String toString() {
		DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
		StringBuilder sb = new StringBuilder();
		int max = getLongestName(this, 0);
		spaces(sb, max + 1);
		sb.append("Status    ");
		sb.append("Queued    ");
		sb.append("Started   ");
		sb.append("Complete  ");
		sb.append("Errors    ");
		sb.append("Started             ");
		sb.append("Finished\n");
		sb.append(getSubject().getName());
		spaces(sb, max - getSubject().getName().length() + 1);
		sb.append(getState());
		spaces(sb, 10 - getState().name().length());
		sb.append("-");
		spaces(sb, 9);
		sb.append("-");
		spaces(sb, 9);
		sb.append("-");
		spaces(sb, 9);
		sb.append("-");
		spaces(sb, 9);
		addDates(sb, getStartedDate(), getCompletedDate(), dateFormat);
		for (ProcessorReport processorReport : getProcessorReports())
			addProcessor(sb, max, 0, processorReport, dateFormat);
		return sb.toString();
	}

	private void addProcessor(StringBuilder sb, int max, int level, ProcessorReport processorReport, DateFormat dateFormat) {
		String processorName = processorReport.getSubject().getName();
		spaces(sb, level);
		sb.append(processorName);
		spaces(sb, max - processorName.length() - level + 1);

		State processorState = processorReport.getState();
		sb.append(processorState);
		spaces(sb, 10 - processorState.name().length());

		String jobsQueued = String.valueOf(processorReport.getJobsQueued());
		sb.append(jobsQueued);
		spaces(sb, 10 - jobsQueued.length());

		String jobsStarted = String.valueOf(processorReport.getJobsStarted());
		sb.append(jobsStarted);
		spaces(sb, 10 - jobsStarted.length());

		String jobsCompleted = String.valueOf(processorReport.getJobsCompleted());
		sb.append(jobsCompleted);
		spaces(sb, 10 - jobsCompleted.length());

		String jobsCompletedWithErrors = String.valueOf(processorReport
				.getJobsCompletedWithErrors());
		sb.append(jobsCompletedWithErrors);
		spaces(sb, 10 - jobsCompletedWithErrors.length());

		addDates(sb, processorReport.getStartedDate(), processorReport.getCompletedDate(), dateFormat);

		for (ActivityReport activityReport : processorReport.getActivityReports()) {
			WorkflowReport nestedWorkflowReport = activityReport.getNestedWorkflowReport();
			if (nestedWorkflowReport != null)
				for (ProcessorReport nestedProcessorReport : nestedWorkflowReport.getProcessorReports())
					addProcessor(sb, max, level + 1, nestedProcessorReport, dateFormat);
		}
	}

	private void addDates(StringBuilder sb, Date started, Date stopped, DateFormat dateFormat) {
		if (started != null) {
			sb.append(dateFormat.format(started));
			sb.append(' ');
		} else {
			sb.append('-');
			spaces(sb, dateFormatString.length());
		}
		if (stopped != null)
			sb.append(dateFormat.format(stopped) + "\n");
		else
			sb.append("-\n");
	}

	private int getLongestName(WorkflowReport workflowReport, int level) {
		int result = 0;
		result = Math.max(result, getSubject().getName().length() + level);
		for (ProcessorReport processorReport : workflowReport.getProcessorReports()) {
			result = Math.max(result, processorReport.getSubject().getName().length());
			for (ActivityReport activityReport : processorReport.getActivityReports()) {
				WorkflowReport nestedWorkflowReport = activityReport.getNestedWorkflowReport();
				if (nestedWorkflowReport != null)
					result = Math.max(result, getLongestName(nestedWorkflowReport, level + 1));
			}
		}
		return result;
	}

	private static void spaces(StringBuilder sb, int length) {
		for (int i = 0; i < length; i++)
			sb.append(' ');
	}
}
