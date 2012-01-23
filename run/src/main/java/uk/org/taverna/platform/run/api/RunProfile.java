package uk.org.taverna.platform.run.api;

import java.util.Map;

import uk.org.taverna.platform.data.Data;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
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

	private ExecutionEnvironment executionEnvironment;
	private WorkflowBundle workflowBundle;
	private Workflow workflow;
	private Profile profile;
	private Map<String, Data> inputs;

	/**
	 * Constructs a <code>RunProfile</code> that specifies the parameters
	 * required to run a {@link Workflow}. The main <code>Workflow</code> and
	 * <code>Profile</code> from the <code>WorkflowBundle</code> are used.
	 *
	 * @param executionEnvironment
	 *            the {@link ExecutionEnvironment} used to execute the
	 *            <code>Workflow</code>
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the
	 *            <code>Workflow</code> to run
	 */
	public RunProfile(ExecutionEnvironment executionEnvironment, WorkflowBundle workflowBundle) {
		this(executionEnvironment, workflowBundle, null, null, null);
	}

	/**
	 * Constructs a <code>RunProfile</code> that specifies the parameters
	 * required to run a {@link Workflow}. The main <code>Workflow</code> and
	 * <code>Profile</code> from the <code>WorkflowBundle</code> are used.
	 *
	 * @param executionEnvironment
	 *            the {@link ExecutionEnvironment} used to execute the
	 *            <code>Workflow</code>
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the
	 *            <code>Workflow</code> to run
	 * @param inputs
	 *            the inputs for the <code>Workflow</code>. Can be
	 *            <code>null</code> if there are no inputs
	 */
	public RunProfile(ExecutionEnvironment executionEnvironment, WorkflowBundle workflowBundle,
			Map<String, Data> inputs) {
		this(executionEnvironment, workflowBundle, null, null, inputs);
	}

	/**
	 * Constructs a <code>RunProfile</code> that specifies the parameters
	 * required to run a {@link Workflow}.
	 *
	 * @param executionEnvironment
	 *            the {@link ExecutionEnvironment} used to execute the
	 *            <code>Workflow</code>
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
	 */
	public RunProfile(ExecutionEnvironment executionEnvironment, WorkflowBundle workflowBundle, Workflow workflow,
			Profile profile, Map<String, Data> inputs) {
		this.executionEnvironment = executionEnvironment;
		this.workflowBundle = workflowBundle;
		this.workflow = workflow;
		this.profile = profile;
		this.inputs = inputs;
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
			return workflowBundle.getMainWorkflow();
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
		if (profile == null) {
			return workflowBundle.getMainProfile();
		}
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
	public Map<String, Data> getInputs() {
		return inputs;
	}

	/**
	 * Sets the inputs for the <code>Workflow</code>.
	 *
	 * @param inputs
	 *            the inputs for the <code>Workflow</code>
	 */
	public void setInputs(Map<String, Data> inputs) {
		this.inputs = inputs;
	}

	/**
	 * Returns the <code>ExecutionEnvironment</code> used to execute the <code>Workflow</code>.
	 *
	 * @return the  <code>ExecutionEnvironment</code> used to execute the <code>Workflow</code>
	 */
	public ExecutionEnvironment getExecutionEnvironment() {
		return executionEnvironment;
	}

	/**
	 * Sets the <code>ExecutionEnvironment</code> used to execute the <code>Workflow</code>.
	 *
	 * @param executionEnvironment the <code>ExecutionEnvironment</code> used to execute the <code>Workflow</code>
	 */
	public void setExecutionEnvironment(ExecutionEnvironment executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

}
