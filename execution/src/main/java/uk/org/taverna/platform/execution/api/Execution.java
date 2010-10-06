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

import java.util.Map;
import java.util.UUID;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * 
 * @author David Withers
 */
public abstract class Execution {

	private final String ID;
	private final Workflow workflow;
	private final Profile profile;
	private final Map<String, T2Reference> inputs;
	private final ReferenceService referenceService;
	private final WorkflowReport workflowReport;

	public Execution(Workflow workflow, Profile profile,
			Map<String, T2Reference> inputs, ReferenceService referenceService) {
		this.workflow = workflow;
		this.profile = profile;
		this.inputs = inputs;
		this.referenceService = referenceService;
		ID = UUID.randomUUID().toString();
		workflowReport = createWorkflowReport(workflow);
	}

	protected abstract WorkflowReport createWorkflowReport(Workflow workflow);

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