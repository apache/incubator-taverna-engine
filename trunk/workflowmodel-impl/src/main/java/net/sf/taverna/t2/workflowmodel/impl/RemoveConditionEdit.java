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
import net.sf.taverna.t2.workflowmodel.Processor;

/**
 * Remove the condition serializing execution between the specified control and
 * target processors.
 * 
 * @author Tom Oinn
 * 
 */
public class RemoveConditionEdit extends AbstractBinaryProcessorEdit {

	private ConditionImpl condition = null;

	public RemoveConditionEdit(Processor control, Processor target) {
		super(control, target);

	}

	@Override
	protected void doEditAction(ProcessorImpl control, ProcessorImpl target)
			throws EditException {
		for (ConditionImpl c : control.controlledConditions) {
			if (c.getTarget() == target) {
				this.condition = c;
				break;
			}
		}
		if (this.condition == null) {
			throw new EditException(
					"Can't remove a control link as it doesn't exist");
		}

		control.controlledConditions.remove(condition);
		target.conditions.remove(condition);
	}

	@Override
	protected void undoEditAction(ProcessorImpl control, ProcessorImpl target) {
		control.controlledConditions.add(condition);
		target.conditions.add(condition);
	}

}
