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
package uk.org.taverna.platform.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import uk.org.taverna.platform.execution.dataflow.DataflowExecution;
import uk.org.taverna.platform.report.WorkflowReport;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * 
 * 
 * @author David Withers
 */
public class ExecutionService {

	private final String ID;

	private final Map<String, Execution> executionMap;

	public ExecutionService() {
		ID = UUID.randomUUID().toString();
		executionMap = new HashMap<String, Execution>();
	}

	public String getID() {
		return ID;
	}

	public String createExecution(Workflow workflow, Profile profile, Map<String, T2Reference> inputs,
			ReferenceService referenceService) throws InvalidWorkflowException {
		Execution execution = new DataflowExecution(workflow, profile, inputs, referenceService);
		executionMap.put(execution.getID(), execution);
		return execution.getID();
	}

	public WorkflowReport getWorkflowReport(String executionID) {
		return executionMap.get(executionID).getWorkflowReport();
	}

	public void start(String executionID) {
		executionMap.get(executionID).start();
	}

	public void pause(String executionID) {
		executionMap.get(executionID).pause();
	}

	public void resume(String executionID) {
		executionMap.get(executionID).resume();
	}

	public void cancel(String executionID) {
		executionMap.get(executionID).cancel();
	}

}
