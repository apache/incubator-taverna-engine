/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
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
package net.sf.taverna.t2.workflowmodel.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.DataflowValidationReport;
import net.sf.taverna.t2.workflowmodel.TokenProcessingEntity;

/**
 * Simple implementation of the DataflowValidationReport interface
 * 
 * @author Tom Oinn
 * 
 */
public class DataflowValidationReportImpl implements DataflowValidationReport {

	private final List<TokenProcessingEntity> failed;
	private final Map<TokenProcessingEntity, DataflowValidationReport> invalidDataflows;
	private final List<DataflowOutputPort> unresolvedOutputs;
	private final List<TokenProcessingEntity> unsatisfied;
	private boolean valid;
	private boolean isWorkflowIncomplete; // whether a workflow is incomplete (contains no processors and no output ports), in which case it also must be invalid

	DataflowValidationReportImpl(boolean isValid, boolean isWorkflowIncomplete,
			List<TokenProcessingEntity> failedProcessors,
			List<TokenProcessingEntity> unsatisfiedProcessors,
			List<DataflowOutputPort> unresolvedOutputs, Map<TokenProcessingEntity, DataflowValidationReport> invalidDataflows) {
		this.valid = isValid;
		this.isWorkflowIncomplete = isWorkflowIncomplete;
		this.invalidDataflows = Collections.unmodifiableMap(invalidDataflows);
		this.failed = Collections.unmodifiableList(failedProcessors);
		this.unsatisfied = Collections.unmodifiableList(unsatisfiedProcessors);
		this.unresolvedOutputs = Collections.unmodifiableList(unresolvedOutputs);
	}

	public List<? extends TokenProcessingEntity> getFailedEntities() {
		return failed;
	}

	public Map<TokenProcessingEntity, DataflowValidationReport> getInvalidDataflows() {
		return invalidDataflows;
	}

	public List<? extends DataflowOutputPort> getUnresolvedOutputs() {
		return unresolvedOutputs;
	}
	
	public List<? extends TokenProcessingEntity> getUnsatisfiedEntities() {
		return unsatisfied;
	}

	public boolean isValid() {
		return valid;
	}

	public boolean isWorkflowIncomplete() {
		return isWorkflowIncomplete;
	}

}
