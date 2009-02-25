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
package net.sf.taverna.t2.workflowmodel.processor.dispatch.events;

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.ProcessIdentifierException;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchMessageType;

/**
 * An event within the dispatch stack containing a single job's worth of data
 * along with an ordered list of Activity instances.
 * 
 * @author Tom Oinn
 * 
 */
public class DispatchJobEvent extends AbstractDispatchEvent<DispatchJobEvent> {

	private Map<String, T2Reference> dataMap;

	private List<? extends Activity<?>> activities;

	/**
	 * Create a new job event, specifying a complete set of input data and a
	 * list of activities which could potentially consume this data
	 * 
	 * @param owningProcess
	 * @param index
	 * @param context
	 * @param data
	 * @param activities
	 */
	public DispatchJobEvent(String owningProcess, int[] index,
			InvocationContext context, Map<String, T2Reference> data,
			List<? extends Activity<?>> activities) {
		super(owningProcess, index, context);
		this.dataMap = data;
		this.activities = activities;
	}

	/**
	 * The actual data carried by this dispatch job event object is in the form
	 * of a map, where the keys of the map are Strings identifying the named
	 * input and the values are Strings containing valid data identifiers within
	 * the context of a visible DataManager object (see CloudOne specification
	 * for further information on the DataManager system)
	 * 
	 * @return Map of name to data reference for this Job
	 */
	public Map<String, T2Reference> getData() {
		return this.dataMap;
	}

	/**
	 * Returns a list of activity instances which can be applied to the data
	 * contained by this job event.
	 * 
	 * @return ordered list of Activity instances
	 */
	public List<? extends Activity<?>> getActivities() {
		return this.activities;
	}

	@Override
	public DispatchJobEvent popOwningProcess()
			throws ProcessIdentifierException {
		return new DispatchJobEvent(popOwner(), index, context, dataMap, activities);
	}

	@Override
	public DispatchJobEvent pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException {
		return new DispatchJobEvent(pushOwner(localProcessName), index, context, dataMap, activities);
	}

	/**
	 * DispatchMessageType.JOB
	 */
	@Override
	public DispatchMessageType getMessageType() {
		return DispatchMessageType.JOB;
	}

}
