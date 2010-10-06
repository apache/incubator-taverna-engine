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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import uk.org.taverna.platform.execution.api.InvalidExecutionIdException;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.report.State;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.platform.run.api.InvalidRunIdException;
import uk.org.taverna.platform.run.api.RunService;
import uk.org.taverna.platform.run.api.RunStateException;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * 
 * 
 * @author David Withers
 */
public class RunServiceImpl implements RunService {

	private final List<String> runs;

	private final Map<String, Run> runMap;

	public RunServiceImpl() {
		runs = new ArrayList<String>();
		runMap = new HashMap<String, Run>();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.run.RunService#getRuns()
	 */
	@Override
	public List<String> getRuns() {
		return runs;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.run.RunService#createRun(uk.org.taverna.scufl2.api.core.Workflow, uk.org.taverna.scufl2.api.profiles.Profile, java.util.Map, net.sf.taverna.t2.reference.ReferenceService)
	 */
	@Override
	public String createRun(Workflow workflow, Profile profile, Map<String, T2Reference> inputs, ReferenceService referenceService) throws InvalidWorkflowException {
		Run run = new Run(workflow, profile, inputs, referenceService, null);
		runMap.put(run.getID(), run);
		runs.add(run.getID());
		return run.getID();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.run.RunService#start(java.lang.String)
	 */
	@Override
	public void start(String runID) throws InvalidRunIdException, RunStateException, InvalidExecutionIdException {
		getRun(runID).start();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.run.RunService#pause(java.lang.String)
	 */
	@Override
	public void pause(String runID) throws InvalidRunIdException, RunStateException, InvalidExecutionIdException {
		getRun(runID).pause();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.run.RunService#resume(java.lang.String)
	 */
	@Override
	public void resume(String runID) throws InvalidRunIdException, RunStateException, InvalidExecutionIdException {
		getRun(runID).resume();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.run.RunService#cancel(java.lang.String)
	 */
	@Override
	public void cancel(String runID) throws InvalidRunIdException, RunStateException, InvalidExecutionIdException {
		getRun(runID).cancel();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.run.RunService#getState(java.lang.String)
	 */
	@Override
	public State getState(String runID) throws InvalidRunIdException {
		return getWorkflowReport(runID).getState();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.run.RunService#getInputs(java.lang.String)
	 */
	@Override
	public Map<String, T2Reference> getInputs(String runID) throws InvalidRunIdException {
		return getRun(runID).getInputs();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.run.RunService#getOutputs(java.lang.String)
	 */
	@Override
	public Map<String, T2Reference> getOutputs(String runID) throws InvalidRunIdException {
		return getRun(runID).getOutputs();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.run.RunService#getWorkflowReport(java.lang.String)
	 */
	@Override
	public WorkflowReport getWorkflowReport(String runID) throws InvalidRunIdException {
		return getRun(runID).getWorkflowReport();
	}
	
	private Run getRun(String runID) throws InvalidRunIdException {
		Run run = runMap.get(runID);
		if (run == null) {
			throw new InvalidRunIdException("Run ID " + runID + " is not valid");
		}
		return run;
	}

}
