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
package uk.org.taverna.platform.run.impl;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import uk.org.taverna.platform.execution.api.ExecutionService;
import uk.org.taverna.platform.execution.api.InvalidExecutionIdException;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.platform.run.api.RunStateException;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * 
 * 
 * @author David Withers
 */
public class Run {

	private String ID, executionID;

	private Map<String, T2Reference> inputs, outputs;

	private ExecutionService executionService;

	private State state;

	private WorkflowReport workflowReport;

	private final Workflow workflow;

	private final Profile profile;

	private final ReferenceService referenceService;

	public Run(Workflow workflow, Profile profile, Map<String, T2Reference> inputs,
			ReferenceService referenceService, ExecutionService executionService)
			throws InvalidWorkflowException {
		this.workflow = workflow;
		this.profile = profile;
		this.inputs = inputs;
		this.referenceService = referenceService;
		this.executionService = executionService;
		ID = UUID.randomUUID().toString();
		executionID = executionService.createExecution(workflow, profile, inputs, referenceService);
		try {
			workflowReport = executionService.getWorkflowReport(executionID);
		} catch (InvalidExecutionIdException e) {
			// TODO throw an exception
		}
		workflowReport.setCreatedDate(new Date());
		state = State.CREATED;
	}

	public String getID() {
		return ID;
	}

	public WorkflowReport getWorkflowReport() {
		return workflowReport;
	}

	public Map<String, T2Reference> getInputs() {
		return inputs;
	}

	public Map<String, T2Reference> getOutputs() {
		return outputs;
	}

	public void setOutputs(Map<String, T2Reference> outputs) {
		this.outputs = outputs;
	}

	public void start() throws RunStateException, InvalidExecutionIdException {
		synchronized (state) {
			if (state.equals(State.CREATED)) {
				executionService.start(executionID);
				state = State.RUNNING;
				workflowReport.setStartedDate(new Date());
			} else {
				throw new RunStateException("Cannot start a " + state + " run.");
			}

		}
	}

	public void pause() throws RunStateException, InvalidExecutionIdException {
		synchronized (state) {
			if (state.equals(State.RUNNING)) {
				executionService.pause(executionID);
				state = State.PAUSED;
				workflowReport.setPausedDate(new Date());
			} else {
				throw new RunStateException("Cannot pause a " + state + " run.");
			}

		}
	}

	public void resume() throws RunStateException, InvalidExecutionIdException {
		synchronized (state) {
			if (state.equals(State.PAUSED)) {
				executionService.resume(executionID);
				state = State.RUNNING;
				workflowReport.setResumedDate(new Date());
			} else {
				throw new RunStateException("Cannot resume a " + state + " run.");
			}
		}
	}

	public void cancel() throws RunStateException, InvalidExecutionIdException {
		synchronized (state) {
			if (state.equals(State.CANCELLED) || state.equals(State.COMPLETED)
					|| state.equals(State.FAILED)) {
				throw new RunStateException("Cannot cancel a " + state + " run.");
			} else {
				executionService.cancel(executionID);
				state = State.CANCELLED;
				workflowReport.setCancelledDate(new Date());
			}
		}
	}

}
