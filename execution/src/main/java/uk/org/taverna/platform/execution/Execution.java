package uk.org.taverna.platform.execution;

import java.util.Map;
import java.util.UUID;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

public abstract class Execution {

	private final String ID;
	private final Workflow workflow;
	private final Profile profile;
	private final Map<String, T2Reference> inputs;
	private final ReferenceService referenceService;
	private final WorkflowReport workflowReport;

	public Execution(Workflow workflow, Profile profile, Map<String, T2Reference> inputs, ReferenceService referenceService) {
		this.workflow = workflow;
		this.profile = profile;
		this.inputs = inputs;
		this.referenceService = referenceService;
		ID = UUID.randomUUID().toString();
		workflowReport = createWorkflowReport(workflow);
	}

	protected WorkflowReport createWorkflowReport(Workflow workflow) {
		return new WorkflowReport(workflow);
	}

	public String getID() {
		return ID;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public Profile getProfile() {
		return profile;
	}

	public Map<String, T2Reference> getInputs() {
		return inputs;
	}

	public ReferenceService getReferenceService() {
		return referenceService;
	}
	
	public WorkflowReport getWorkflowReport() {
		return workflowReport;
	}

	public abstract void start();

	public abstract void pause();

	public abstract void resume();

	public abstract void cancel();

}