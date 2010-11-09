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

import net.sf.taverna.t2.reference.T2Reference;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;

/**
 * 
 * 
 * @author David Withers
 */
public abstract class WorkflowReport extends StatusReport {

	private Workflow workflow;
	
	private Map<String, T2Reference> outputs;

	private Map<Processor, ProcessorReport> processorReports;

	public WorkflowReport(Workflow workflow) {
		this.workflow = workflow;
		processorReports = new HashMap<Processor, ProcessorReport>();
		for (Processor processor : workflow.getProcessors()) {
			processorReports.put(processor, createProcessorReport(processor, this));
		}
	}

	/**
	 * @return the outputs
	 */
	public Map<String, T2Reference> getOutputs() {
		return outputs;
	}

	/**
	 * @param outputs the outputs to set
	 */
	public void setOutputs(Map<String, T2Reference> outputs) {
		this.outputs = outputs;
	}

	/**
	 * @return the workflow
	 */
	public Workflow getWorkflow() {
		return workflow;
	}

	/**
	 * @return the processorReports
	 */
	public Set<ProcessorReport> getProcessorReports() {
		return new HashSet<ProcessorReport>(processorReports.values());
	}

	public Set<ProcessorReport> getAllProcessorReports() {
		Set<ProcessorReport> allProcessorReports = new HashSet<ProcessorReport>(); 
		for (ProcessorReport processorReport : getProcessorReports()) {
			allProcessorReports.add(processorReport);
			for (ActivityReport activityReport : processorReport.getActivityReports()) {
				WorkflowReport nestedWorkflowReport = activityReport.getNestedWorkflowReport();
				if (nestedWorkflowReport != null) {
					allProcessorReports.addAll(nestedWorkflowReport.getAllProcessorReports());
				}
			}
		}
		return allProcessorReports;
	}

//	public ProcessorReport getProcessorReport(Processor processor) {
//		if (workflow.equals(processor.getParent())) {
//			return processorReports.get(processor);
//		}
//		for (WorkflowReport nestedWorkflowReport : nestedWorkflowReports) {
//			ProcessorReport nestedProcessorReport = nestedWorkflowReport
//					.getProcessorReport(processor);
//			if (nestedProcessorReport != null) {
//				return nestedProcessorReport;
//			}
//		}
//		return null;
//	}
	
	protected abstract ProcessorReport createProcessorReport(Processor processor, WorkflowReport parentReport);

	public String toString() {
		StringBuilder sb = new StringBuilder();
		int max = getLongestName();
		sb.append(spaces(max + 1));
		sb.append("Status    ");
		sb.append("Queued    ");
		sb.append("Started   ");
		sb.append("Complete  ");
		sb.append("Errors\n");
		sb.append(workflow.getName());
		sb.append(spaces(max - workflow.getName().length() +1));
		sb.append(getState() + "\n");
		for (ProcessorReport  processorReport : processorReports.values()) {
			String processorName = processorReport.getProcessor().getName();
			sb.append(processorName);
			sb.append(spaces(max - processorName.length() +1));
			
			State processorState = processorReport.getState();
			sb.append(processorState);
			sb.append(spaces(10 - processorState.name().length()));
			
			String jobsQueued = String.valueOf(processorReport.getJobsQueued());
			sb.append(jobsQueued);
			sb.append(spaces(10 - jobsQueued.length()));
			
			String jobsStarted = String.valueOf(processorReport.getJobsStarted());
			sb.append(jobsStarted);
			sb.append(spaces(10 - jobsStarted.length()));
			
			String jobsCompleted = String.valueOf(processorReport.getJobsCompleted());
			sb.append(jobsCompleted);
			sb.append(spaces(10 - jobsCompleted.length()));
			
			String jobsCompletedWithErrors = String.valueOf(processorReport.getJobsCompletedWithErrors());
			sb.append(jobsCompletedWithErrors);
			sb.append(spaces(10 - jobsCompletedWithErrors.length()));
			
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private int getLongestName() {
		int result = 0;
		result = Math.max(result, workflow.getName().length());
		for (ProcessorReport processorReport : getAllProcessorReports()) {
			result = Math.max(result, processorReport.getProcessor().getName().length());
		}
		return result;
	}
	
	private String spaces(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}
	
}
