package uk.org.taverna.platform.execution.impl;

import uk.org.taverna.platform.report.ProcessorReport;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;

public class DataflowWorkflowReport extends WorkflowReport {
	
	public DataflowWorkflowReport(Workflow workflow) {
		super(workflow);
	}

	@Override
	protected ProcessorReport createProcessorReport(Processor processor, WorkflowReport parentReport) {
		return new DataflowProcessorReport(processor, parentReport);
	}
	
}
