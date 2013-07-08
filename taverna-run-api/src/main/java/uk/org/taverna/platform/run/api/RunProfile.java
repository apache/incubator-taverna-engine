package uk.org.taverna.platform.run.api;

import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * A <code>RunProfile</code> specifies the parameters required to run a
 * {@link uk.org.taverna.scufl2.api.core.Workflow}.
 *
 * @author David Withers
 */
public class RunProfile {

	private ExecutionEnvironment executionEnvironment;
	private WorkflowBundle workflowBundle;
	private Bundle inputs;
	private String workflowName;
	private String profileName;

	/**
	 * Constructs a <code>RunProfile</code> that specifies the parameters required to run a
	 * {@link uk.org.taverna.scufl2.api.core.Workflow}. The main <code>Workflow</code> and
	 * <code>Profile</code> from the <code>WorkflowBundle</code> are used.
	 *
	 * @param executionEnvironment
	 *            the {@link ExecutionEnvironment} used to execute the <code>Workflow</code>
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the <code>Workflow</code> to run
	 */
	public RunProfile(ExecutionEnvironment executionEnvironment, WorkflowBundle workflowBundle) {
		this(executionEnvironment, workflowBundle, null, null, null);
	}

	/**
	 * Constructs a <code>RunProfile</code> that specifies the parameters required to run a
	 * {@link uk.org.taverna.scufl2.api.core.Workflow}. The main <code>Workflow</code> and
	 * <code>Profile</code> from the <code>WorkflowBundle</code> are used.
	 *
	 * @param executionEnvironment
	 *            the {@link ExecutionEnvironment} used to execute the <code>Workflow</code>
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the <code>Workflow</code> to run
	 * @param inputs
	 *            the <code>Bundle</code> containing inputs for the <code>Workflow</code>. Can
	 *            be <code>null</code> if there are no inputs
	 */
	public RunProfile(ExecutionEnvironment executionEnvironment, WorkflowBundle workflowBundle,
			Bundle inputs) {
		this(executionEnvironment, workflowBundle, null, null, inputs);
	}

	/**
	 * Constructs a <code>RunProfile</code> that specifies the parameters required to run a
	 * {@link uk.org.taverna.scufl2.api.core.Workflow}.
	 *
	 * @param executionEnvironment
	 *            the {@link ExecutionEnvironment} used to execute the <code>Workflow</code>
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the <code>Workflow</code> to run
	 * @param workflow
	 *            the <code>Workflow</code> to run. If <code>null</code> uses the main
	 *            <code>Workflow</code> from the <code>WorkflowBundle</code>
	 * @param profile
	 *            the {@link uk.org.taverna.scufl2.api.profiles.Profile} to use when running the
	 *            <code>Workflow</code>. If null uses
	 *            the main <code>Profile</code> from the <code>WorkflowBundle</code>
	 * @param inputs
	 *            the <code>Bundle</code> containing inputs for the <code>Workflow</code>. Can
	 *            be <code>null</code> if there are no inputs
	 */
	public RunProfile(ExecutionEnvironment executionEnvironment, WorkflowBundle workflowBundle,
			String workflowName, String profileName, Bundle inputs) {
		this.executionEnvironment = executionEnvironment;
		this.workflowBundle = workflowBundle;
		this.workflowName = workflowName;
		this.profileName = profileName;
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
	 * Sets the <code>WorkflowBundle</code> containing the <code>Workflow</code> to run.
	 *
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the <code>Workflow</code> to run
	 */
	public void setWorkflowBundle(WorkflowBundle workflowBundle) {
		this.workflowBundle = workflowBundle;
	}

	/**
	 * Returns the name of the <code>Workflow</code> to run.
	 * If no <code>Workflow</code> name is set the main <code>Workflow</code> from the
	 * <code>WorkflowBundle</code> will be run.
	 *
	 * @return the <code>Workflow</code> to run
	 */
	public String getWorkflowName() {
		if (workflowName == null && workflowBundle.getMainWorkflow() != null) {
			return workflowBundle.getMainWorkflow().getName();
		}
		return workflowName;
	}

	/**
	 * Sets the name of the <code>Workflow</code> to run.
	 * If no <code>Workflow</code> name is set the main <code>Workflow</code> from the
	 * <code>WorkflowBundle</code> will be run.
	 *
	 * @param workflowName
	 *            the name of the <code>Workflow</code> to run
	 */
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	/**
	 * Returns the name of the <code>Profile</code> to use when running the <code>Workflow</code>.
	 * If no <code>Profile</code> name is set the main <code>Profile</code> from the
	 * <code>WorkflowBundle</code> will be used.
	 *
	 * @return the <code>Profile</code> to use when running the <code>Workflow</code>
	 */
	public String getProfileName() {
		if (profileName == null && workflowBundle.getMainProfile() != null) {
			return workflowBundle.getMainProfile().getName();
		}
		return profileName;
	}

	/**
	 * Sets the name of the <code>Profile</code> to use when running the <code>Workflow</code>.
	 * <p>
	 * If no <code>Profile</code> name is set the main <code>Profile</code> from the
	 * <code>WorkflowBundle</code> will be used.
	 *
	 * @param profileName
	 *            the name of the <code>Profile</code> to use when running the <code>Workflow</code>
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	/**
	 * Returns the <code>Bundle</code> containing the inputs for the <code>Workflow</code>.
	 * <p>
	 * May be <code>null</code> if the <code>Workflow</code> doesn't require any inputs.
	 *
	 * @return the inputs for the <code>Workflow</code>
	 */
	public Bundle getInputs() {
		return inputs;
	}

	/**
	 * Sets the <code>Bundle</code> containing the inputs for the <code>Workflow</code>.
	 *
	 * @param inputs
	 *            the <code>Bundle</code> containing the inputs for the <code>Workflow</code>
	 */
	public void setInputs(Bundle dataBundle) {
		this.inputs = dataBundle;
	}

	/**
	 * Returns the <code>ExecutionEnvironment</code> used to execute the <code>Workflow</code>.
	 *
	 * @return the <code>ExecutionEnvironment</code> used to execute the <code>Workflow</code>
	 */
	public ExecutionEnvironment getExecutionEnvironment() {
		return executionEnvironment;
	}

	/**
	 * Sets the <code>ExecutionEnvironment</code> used to execute the <code>Workflow</code>.
	 *
	 * @param executionEnvironment
	 *            the <code>ExecutionEnvironment</code> used to execute the <code>Workflow</code>
	 */
	public void setExecutionEnvironment(ExecutionEnvironment executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

}
