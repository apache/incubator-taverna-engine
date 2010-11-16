package uk.org.taverna.platform.execution.impl.local;

import uk.org.taverna.platform.report.ActivityReport;
import uk.org.taverna.platform.report.ProcessorReport;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;

public class LocalWorkflowReport extends WorkflowReport {
	
	public LocalWorkflowReport(Workflow workflow) {
		super(workflow);
	}

	@Override
	public ProcessorReport createProcessorReport(Processor processor, WorkflowReport parentReport) {
		return new LocalProcessorReport(processor, parentReport);
	}
	
	@Override
	public ActivityReport createActivityReport(Activity activity, ProcessorReport parentReport) {
		return new ActivityReport(activity, parentReport);
	}
	
}
