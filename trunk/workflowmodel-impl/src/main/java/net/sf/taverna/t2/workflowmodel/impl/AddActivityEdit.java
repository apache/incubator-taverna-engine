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

import java.util.List;

import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * Add a new Activity to a Processor, adding the new Activity at the end of the
 * current activity list for that processor.
 * 
 * @author Tom Oinn
 * 
 */
public class AddActivityEdit extends AbstractProcessorEdit {

	private Activity<?> activityToAdd;

	public AddActivityEdit(Processor processor, Activity<?> activity) {
		super(processor);
		this.activityToAdd = activity;
	}

	@Override
	protected void doEditAction(ProcessorImpl processor) throws EditException {
		List<Activity<?>> activities = processor.activityList;
		if (activities.contains(activityToAdd) == false) {
			synchronized (processor) {
				activities.add(activityToAdd);
			}
		} else {
			throw new EditException(
					"Cannot add a duplicate activity to processor");
		}

	}

	@Override
	protected void undoEditAction(ProcessorImpl processor) {
		synchronized (processor) {
			processor.activityList.remove(activityToAdd);
		}
	}

}
