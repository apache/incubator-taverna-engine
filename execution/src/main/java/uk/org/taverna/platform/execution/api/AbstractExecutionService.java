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

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * 
 * 
 * @author David Withers
 */
public abstract class AbstractExecutionService implements ExecutionService {

	private final String ID;
	
	private final String name;

	private final String description;

	private final Map<String, Execution> executionMap;

	public AbstractExecutionService(String ID, String name, String description) {
		this.ID = ID;
		this.name = name;
		this.description = description;
		executionMap = new HashMap<String, Execution>();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.execution.ExecutionService#getID()
	 */
	@Override
	public String getID() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.execution.ExecutionService#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.execution.ExecutionService#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.execution.ExecutionService#createExecution(uk.org.taverna.scufl2.api.core.Workflow, uk.org.taverna.scufl2.api.profiles.Profile, java.util.Map, net.sf.taverna.t2.reference.ReferenceService)
	 */
	@Override
	public String createExecution(Workflow workflow, Profile profile, Map<String, T2Reference> inputs,
			ReferenceService referenceService) throws InvalidWorkflowException {
		Execution execution = createExecutionImpl(workflow, profile, inputs, referenceService);
		executionMap.put(execution.getID(), execution);
		return execution.getID();
	}
	
	protected abstract Execution createExecutionImpl(Workflow workflow, Profile profile, Map<String, T2Reference> inputs,
			ReferenceService referenceService) throws InvalidWorkflowException;

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.execution.ExecutionService#getWorkflowReport(java.lang.String)
	 */
	@Override
	public WorkflowReport getWorkflowReport(String executionID) throws InvalidExecutionIdException {
		return getExecution(executionID).getWorkflowReport();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.execution.ExecutionService#start(java.lang.String)
	 */
	@Override
	public void start(String executionID) throws InvalidExecutionIdException {
		getExecution(executionID).start();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.execution.ExecutionService#pause(java.lang.String)
	 */
	@Override
	public void pause(String executionID) throws InvalidExecutionIdException {
		getExecution(executionID).pause();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.execution.ExecutionService#resume(java.lang.String)
	 */
	@Override
	public void resume(String executionID) throws InvalidExecutionIdException {
		getExecution(executionID).resume();
	}

	/* (non-Javadoc)
	 * @see uk.org.taverna.platform.execution.ExecutionService#cancel(java.lang.String)
	 */
	@Override
	public void cancel(String executionID) throws InvalidExecutionIdException {
		getExecution(executionID).cancel();
	}

	protected Execution getExecution(String executionID) throws InvalidExecutionIdException {
		Execution execution = executionMap.get(executionID);
		if (execution == null) {
			throw new InvalidExecutionIdException("Execution ID " + executionID + " is not valid");
		}
		return execution;
	}

}
