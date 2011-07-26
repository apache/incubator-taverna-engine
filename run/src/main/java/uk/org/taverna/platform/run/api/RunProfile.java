package uk.org.taverna.platform.run.api;

import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * A <code>RunProfile</code> specifies the parameters required to run a
 * {@link Workflow}.
 *
 * @author David Withers
 */
public class RunProfile {

	private WorkflowBundle workflowBundle;
	private Workflow workflow;
	private Profile profile;
	private Map<String, T2Reference> inputs;
	private ReferenceService referenceService;
	private ExecutionService executionService;

	/**
	 * Constructs a <code>RunProfile</code> that specifies the parameters
	 * required to run a {@link Workflow}. The main <code>Workflow</code> and
	 * <code>Profile</code> from the <code>WorkflowBundle</code> are used.
	 *
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the
	 *            <code>Workflow</code> to run
	 * @param referenceService
	 *            the {@link ReferenceService} used to register the
	 *            <code>Workflow</code> inputs and outputs
	 * @param executionService
	 *            the {@link ExecutionService} used to execute the
	 *            <code>Workflow</code>
	 */
	public RunProfile(WorkflowBundle workflowBundle,
			ReferenceService referenceService, ExecutionService executionService) {
		this(workflowBundle, null, null, null, referenceService,
				executionService);
	}

	/**
	 * Constructs a <code>RunProfile</code> that specifies the parameters
	 * required to run a {@link Workflow}. The main <code>Workflow</code> and
	 * <code>Profile</code> from the <code>WorkflowBundle</code> are used.
	 *
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the
	 *            <code>Workflow</code> to run
	 * @param inputs
	 *            the inputs for the <code>Workflow</code>. Can be
	 *            <code>null</code> if there are no inputs
	 * @param referenceService
	 *            the {@link ReferenceService} used to register the
	 *            <code>Workflow</code> inputs and outputs
	 * @param executionService
	 *            the {@link ExecutionService} used to execute the
	 *            <code>Workflow</code>
	 */
	public RunProfile(WorkflowBundle workflowBundle,
			Map<String, T2Reference> inputs, ReferenceService referenceService,
			ExecutionService executionService) {
		this(workflowBundle, null, null, inputs, referenceService,
				executionService);
	}

	/**
	 * Constructs a <code>RunProfile</code> that specifies the parameters
	 * required to run a {@link Workflow}.
	 *
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the
	 *            <code>Workflow</code> to run
	 * @param workflow
	 *            the <code>Workflow</code> to run. If <code>null</code> uses
	 *            the main <code>Workflow</code> from the
	 *            <code>WorkflowBundle</code>
	 * @param profile
	 *            the {@link Profile} to use when running the
	 *            <code>Workflow</code>. If null uses the main
	 *            <code>Profile</code> from the <code>WorkflowBundle</code>
	 * @param inputs
	 *            the inputs for the <code>Workflow</code>. Can be
	 *            <code>null</code> if there are no inputs
	 * @param referenceService
	 *            the {@link ReferenceService} used to register the
	 *            <code>Workflow</code> inputs and outputs
	 * @param executionService
	 *            the {@link ExecutionService} used to execute the
	 *            <code>Workflow</code>
	 */
	public RunProfile(WorkflowBundle workflowBundle, Workflow workflow,
			Profile profile, Map<String, T2Reference> inputs,
			ReferenceService referenceService, ExecutionService executionService) {
		this.workflowBundle = workflowBundle;
		this.workflow = workflow;
		this.profile = profile;
		this.inputs = inputs;
		this.referenceService = referenceService;
		this.executionService = executionService;
	}

	/**
	 * Returns the <code>WorkflowBundle</code>.
	 *
	 * @return the <code>WorkflowBundle</code>
	 */
	public WorkflowBundle getWorkflowBundle() {
		return workflowBundle;
	}

	/**
	 * Sets the <code>WorkflowBundle</code> containing the <code>Workflow</code>
	 * to run.
	 *
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the
	 *            <code>Workflow</code> to run
	 */
	public void setWorkflowBundle(WorkflowBundle workflowBundle) {
		this.workflowBundle = workflowBundle;
	}

	/**
	 * Returns the <code>Workflow</code> to run.
	 *
	 * If no <code>Workflow</code> is set the main <code>Workflow</code> from
	 * the <code>WorkflowBundle</code> will be run.
	 *
	 * @return the <code>Workflow</code> to run
	 */
	public Workflow getWorkflow() {
		if (workflow == null) {
			workflowBundle.getMainWorkflow();
		}
		return workflow;
	}

	/**
	 * Sets the <code>Workflow</code> to run.
	 *
	 * If no <code>Workflow</code> is set the main <code>Workflow</code> from
	 * the <code>WorkflowBundle</code> will be run.
	 *
	 * @param workflow
	 *            the workflow to run
	 */
	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	/**
	 * Returns the <code>Profile</code> to use when running the
	 * <code>Workflow</code>.
	 *
	 * If no <code>Profile</code> is set the main <code>Profile</code> from the
	 * <code>WorkflowBundle</code> will be used.
	 *
	 * @return the <code>Profile</code> to use when running the
	 *         <code>Workflow</code>
	 */
	public Profile getProfile() {
		return profile;
	}

	/**
	 * Sets the <code>Profile</code> to use when running the
	 * <code>Workflow</code>.
	 *
	 * If no <code>Profile</code> is set the main <code>Profile</code> from the
	 * <code>WorkflowBundle</code> will be used.
	 *
	 * @param profile
	 *            the <code>Profile</code> to use when running the
	 *            <code>Workflow</code>
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * Returns the inputs for the <code>Workflow</code>.
	 *
	 * May be <code>null</code> if the <code>Workflow</code> doesn't require any
	 * inputs.
	 *
	 * @return the inputs for the <code>Workflow</code>
	 */
	public Map<String, T2Reference> getInputs() {
		return inputs;
	}

	/**
	 * Sets the inputs for the <code>Workflow</code>.
	 *
	 * @param inputs
	 *            the inputs for the <code>Workflow</code>
	 */
	public void setInputs(Map<String, T2Reference> inputs) {
		this.inputs = inputs;
	}

	/**
	 * Returns the <code>ReferenceService</code> used to register the inputs.
	 *
	 * This <code>ReferenceService</code> will also be used to register any
	 * outputs and intermediate values produced by the <code>Workflow</code>
	 * run.
	 *
	 * @return the <code>ReferenceService</code> used to register the inputs
	 */
	public ReferenceService getReferenceService() {
		return referenceService;
	}

	/**
	 * Sets the <code>ReferenceService</code> used to register the inputs.
	 *
	 * This <code>ReferenceService</code> will also be used to register any
	 * outputs and intermediate values produced by the <code>Workflow</code>
	 * run.
	 *
	 * @param referenceService
	 *            the <code>ReferenceService</code> used to register the inputs
	 */
	public void setReferenceService(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}

	/**
	 * The <code>ExecutionService</code> used to execute the
	 * <code>Workflow</code>.
	 *
	 * @return the <code>ExecutionService</code> used to execute the
	 *         <code>Workflow</code>
	 */
	public ExecutionService getExecutionService() {
		return executionService;
	}

	/**
	 * Sets the <code>ExecutionService</code> used to execute the
	 * <code>Workflow</code>.
	 *
	 * @param executionService
	 *            the <code>ExecutionService</code> used to execute the
	 *            <code>Workflow</code>
	 */
	public void setExecutionService(ExecutionService executionService) {
		this.executionService = executionService;
	}

}
