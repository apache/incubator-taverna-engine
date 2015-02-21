package uk.org.taverna.platform.execution.api;

import net.sf.taverna.t2.workflowmodel.Dataflow;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;

/**
 * A workflow compilation service converts a workflow (in a
 * {@link WorkflowBundle}) into a dataflow. Most code should ignore this.
 * 
 * @author Donal Fellows
 */
public interface WorkflowCompiler {
	/**
	 * Convert a workflow into a dataflow. May cache.
	 * 
	 * @param workflow
	 *            the workflow to convert; must not be <tt>null</tt>
	 * @return the dataflow, which should not be modified.
	 * @throws InvalidWorkflowException
	 *             If the compilation fails.
	 */
	Dataflow getDataflow(Workflow workflow) throws InvalidWorkflowException;
	
	/**
	 * Convert a workflow bundle into a dataflow. May cache. Only the the
	 * primary workflow is guaranteed to be converted.
	 * 
	 * @param bundle
	 *            the workflow bundle to convert; must not be <tt>null</tt>
	 * @return the dataflow, which should not be modified.
	 * @throws InvalidWorkflowException
	 *             If the compilation fails.
	 */
	Dataflow getDataflow(WorkflowBundle bundle) throws InvalidWorkflowException;
}
