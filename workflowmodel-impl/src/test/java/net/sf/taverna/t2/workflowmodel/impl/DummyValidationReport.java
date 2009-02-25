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
/**
 * 
 */
package net.sf.taverna.t2.workflowmodel.impl;

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.DataflowValidationReport;
import net.sf.taverna.t2.workflowmodel.TokenProcessingEntity;

public class DummyValidationReport implements DataflowValidationReport {
	private final boolean valid;

	public DummyValidationReport(boolean valid) {
		this.valid = valid;
	}

	public boolean isValid() {
		return valid;
	}

	public List<? extends TokenProcessingEntity> getUnsatisfiedEntities() {
		return null;
	}

	public List<? extends DataflowOutputPort> getUnresolvedOutputs() {
		return null;
	}

	public List<? extends TokenProcessingEntity> getFailedEntities() {
		return null;
	}

	public Map<TokenProcessingEntity, DataflowValidationReport> getInvalidDataflows() {
		return null;
	}
}
