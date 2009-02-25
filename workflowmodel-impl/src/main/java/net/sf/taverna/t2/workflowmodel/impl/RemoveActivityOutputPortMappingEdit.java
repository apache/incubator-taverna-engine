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

import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

public class RemoveActivityOutputPortMappingEdit extends AbstractActivityEdit {

	private final String processorPortName;
	private String oldValue=null;

	public RemoveActivityOutputPortMappingEdit(Activity<?>activity, String processorPortName) {
		super(activity);
		this.processorPortName = processorPortName;
		
	}
	
	@Override
	protected void doEditAction(AbstractActivity<?> activity)
			throws EditException {
		if (!activity.getOutputPortMapping().containsKey(processorPortName)) throw new EditException("The output port mapping for the processor port name:"+processorPortName+" doesn't exist");
		oldValue=activity.getOutputPortMapping().get(processorPortName);
		activity.getOutputPortMapping().remove(processorPortName);
	}

	@Override
	protected void undoEditAction(AbstractActivity<?> activity) {
		activity.getOutputPortMapping().put(processorPortName, oldValue);
	}

}
