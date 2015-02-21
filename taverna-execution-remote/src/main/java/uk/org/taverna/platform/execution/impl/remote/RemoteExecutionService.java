/*******************************************************************************
 * Copyright (C) 2011 The University of Manchester
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
package uk.org.taverna.platform.execution.impl.remote;

import java.util.Collections;
import java.util.Set;

import org.apache.taverna.robundle.Bundle;

import uk.org.taverna.platform.execution.api.AbstractExecutionService;
import uk.org.taverna.platform.execution.api.Execution;
import uk.org.taverna.platform.execution.api.ExecutionEnvironment;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * Service for executing Taverna workflows on a Taverna Server.
 *
 * @author David Withers
 */
public class RemoteExecutionService extends AbstractExecutionService {

	public RemoteExecutionService() {
		super(RemoteExecutionService.class.getName(), "Taverna Remote Execution Service",
				"Execution Service for executing Taverna workflows on a Taverna Server");
	}

	@Override
	protected Execution createExecutionImpl(WorkflowBundle workflowBundle, Workflow workflow,
			Profile profile, Bundle inputs)
			throws InvalidWorkflowException {
		return new RemoteExecution(workflowBundle, workflow, profile, inputs);
	}

	@Override
	public Set<ExecutionEnvironment> getExecutionEnvironments() {
		return Collections.<ExecutionEnvironment>emptySet();
	}

}
