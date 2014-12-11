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
package uk.org.taverna.platform.execution.api;

import java.util.Set;

import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Service for executing Taverna workflows. There may be several <code>ExecutionService</code>s
 * available that offer different execution environments, e.g. one <code>ExecutionService</code> may
 * execute workflows on a remote server while another executes workflows on the local machine.
 *
 * @author David Withers
 */
public interface ExecutionService {

	/**
	 * Returns the identifier for this ExecutionService.
	 *
	 * @return the identifier for this ExecutionService
	 */
	public String getID();

	/**
	 * Returns the name of this ExecutionService.
	 *
	 * @return the name of this ExecutionService
	 */
	public String getName();

	/**
	 * Returns a description of this ExecutionService.
	 *
	 * @return a description of this ExecutionService
	 */
	public String getDescription();

	/**
	 * Returns the ExecutionEnvironments available for this ExecutionService.
	 *
	 * @return the ExecutionEnvironments available for this ExecutionService
	 */
	public Set<ExecutionEnvironment> getExecutionEnvironments();

	/**
	 * Creates a workflow execution and returns its ID.
	 *
	 * @param executionEnvironment
	 *            the {@link ExecutionEnvironment} used to execute the
	 *            <code>Workflow</code>
	 * @param workflowBundle
	 *            the <code>WorkflowBundle</code> containing the workflows required for execution
	 * @param workflow
	 *            the workflow to execute
	 * @param profile
	 *            the profile to use when executing the workflow
	 * @param dataBundle
	 *            the <code>Bundle</code> containing the data values for the <code>Workflow</code>
	 * @return the ID of the created workflow execution
	 * @throws InvalidWorkflowException
	 */
	public String createExecution(ExecutionEnvironment executionEnvironment, WorkflowBundle workflowBundle, Workflow workflow, Profile profile,
			Bundle dataBundle)
			throws InvalidWorkflowException;

	/**
	 * Returns the workflow report for the specified execution.
	 *
	 * @param executionID
	 *            the ID of the execution
	 * @return the workflow report for this execution
	 */
	public WorkflowReport getWorkflowReport(String executionID) throws InvalidExecutionIdException;

	/**
	 * Deletes the execution of a workflow.
	 *
	 * @param executionID
	 *            the ID of the execution to delete
	 * @throws InvalidExecutionIdException
	 *             if the execution ID is not valid
	 */
	public void delete(String executionID) throws InvalidExecutionIdException;

	/**
	 * Starts the execution of a workflow.
	 *
	 * @param executionID
	 *            the ID of the execution to start
	 * @throws InvalidExecutionIdException
	 *             if the execution ID is not valid
	 */
	public void start(String executionID) throws InvalidExecutionIdException;

	/**
	 * Pauses the execution of a workflow.
	 *
	 * @param executionID
	 *            the ID of the execution to pause
	 * @throws InvalidExecutionIdException
	 *             if the execution ID is not valid
	 */
	public void pause(String executionID) throws InvalidExecutionIdException;

	/**
	 * Resumes the execution of a paused workflow.
	 *
	 * @param executionID
	 *            the ID of the execution to resume
	 * @throws InvalidExecutionIdException
	 *             if the execution ID is not valid
	 */
	public void resume(String executionID) throws InvalidExecutionIdException;

	/**
	 * Cancels the execution of a workflow.
	 *
	 * @param executionID
	 *            the ID of the execution to cancel
	 * @throws InvalidExecutionIdException
	 *             if the execution ID is not valid
	 */
	public void cancel(String executionID) throws InvalidExecutionIdException;

}