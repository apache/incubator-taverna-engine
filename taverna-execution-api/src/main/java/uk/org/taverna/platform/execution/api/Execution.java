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

import org.purl.wf4ever.robundle.Bundle;

import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Interface for a single execution of a Taverna workflow.
 *
 * @author David Withers
 */
public interface Execution {

	/**
	 * Returns the identifier for this execution.
	 *
	 * @return the identifier for this execution
	 */
	public abstract String getID();

	/**
	 * Returns the <code>WorkflowBundle</code> containing the <code>Workflow</code>s required for execution.
	 *
	 * @return the <code>WorkflowBundle</code> containing the <code>Workflow</code>s required for execution
	 */
	public abstract WorkflowBundle getWorkflowBundle();

	/**
	 * Returns the <code>Bundle</code> containing the data values for the <code>Workflow</code>.
	 *
	 * @return the <code>Bundle</code> containing the data values for the <code>Workflow</code>
	 */
	public abstract Bundle getDataBundle();

	/**
	 * Returns the <code>Workflow</code> to execute.
	 *
	 * @return the <code>Workflow</code> to execute
	 */
	public abstract Workflow getWorkflow();

	/**
	 * Returns the <code>Profile</code> to use when executing the <code>Workflow</code>.
	 *
	 * @return the <code>Profile</code> to use when executing the <code>Workflow</code>
	 */
	public abstract Profile getProfile();

	/**
	 * Returns the <code>WorkflowReport</code> for the execution.
	 *
	 * @return the <code>WorkflowReport</code> for the execution
	 */
	public abstract WorkflowReport getWorkflowReport();

	/**
	 * Deletes the execution.
	 */
	public abstract void delete();

	/**
	 * Starts the execution.
	 */
	public abstract void start();

	/**
	 * Pauses the execution.
	 */
	public abstract void pause();

	/**
	 * Resumes a paused execution.
	 */
	public abstract void resume();

	/**
	 * Cancels the execution.
	 */
	public abstract void cancel();

}