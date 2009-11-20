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

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.annotation.AbstractAnnotatedThing;
import net.sf.taverna.t2.workflowmodel.Condition;

public class ConditionImpl extends AbstractAnnotatedThing<Condition> implements Condition {

	private ProcessorImpl control, target;

	private Map<String, Boolean> stateMap = new HashMap<String, Boolean>();

	protected ConditionImpl(ProcessorImpl control, ProcessorImpl target) {
		this.control = control;
		this.target = target;
	}

	public ProcessorImpl getControl() {
		return this.control;
	}

	public ProcessorImpl getTarget() {
		return this.target;
	}

	public boolean isSatisfied(String owningProcess) {
		if (stateMap.containsKey(owningProcess)) {
			return stateMap.get(owningProcess);
		} else {
			return false;
		}
	}

	protected void satisfy(String owningProcess) {
		stateMap.put(owningProcess, Boolean.TRUE);
		// TODO - poke target processor here
	}

}
