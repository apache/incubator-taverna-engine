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
package net.sf.taverna.t2.workflowmodel.processor.dispatch.layers;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.AbstractErrorHandlerLayer;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchLayerErrorReaction;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchLayerJobReaction;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchLayerResultReaction;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import static net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchLayerStateEffect.*;
import static net.sf.taverna.t2.workflowmodel.processor.dispatch.description.DispatchMessageType.*;

/**
 * Failure handling dispatch layer, consumes job events with multiple activities
 * and emits the same job but with only the first activity. On failures the job
 * is resent to the layer below with a new activity list containing the second
 * in the original list and so on. If a failure is received and there are no
 * further activities to use the job fails and the failure is sent back up to
 * the layer above.
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 * 
 * 
 */
@DispatchLayerErrorReaction(emits = { JOB }, relaysUnmodified = true, stateEffects = {
		UPDATE_LOCAL_STATE, REMOVE_LOCAL_STATE })
@DispatchLayerJobReaction(emits = {}, relaysUnmodified = true, stateEffects = { CREATE_LOCAL_STATE })
@DispatchLayerResultReaction(emits = {}, relaysUnmodified = true, stateEffects = { REMOVE_LOCAL_STATE })
public class Failover extends AbstractErrorHandlerLayer<Object> {

	@Override
	protected JobState getStateObject(DispatchJobEvent jobEvent) {
		return new FailoverState(jobEvent);
	}

	/**
	 * Receive a job from the layer above, store it in the state map then relay
	 * it to the layer below with a modified activity list containing only the
	 * activity at index 0
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void receiveJob(DispatchJobEvent jobEvent) {
		addJobToStateList(jobEvent);
		List<Activity<?>> newActivityList = new ArrayList<Activity<?>>();
		newActivityList.add(jobEvent.getActivities().get(0));
		getBelow().receiveJob(
				new DispatchJobEvent(jobEvent.getOwningProcess(), jobEvent
						.getIndex(), jobEvent.getContext(), jobEvent.getData(),
						newActivityList));
	}

	class FailoverState extends JobState {

		int currentActivityIndex = 0;

		public FailoverState(DispatchJobEvent jobEvent) {
			super(jobEvent);
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean handleError() {
			currentActivityIndex++;
			if (currentActivityIndex == jobEvent.getActivities().size()) {
				return false;
			} else {
				List<Activity<?>> newActivityList = new ArrayList<Activity<?>>();
				newActivityList.add(jobEvent.getActivities().get(
						currentActivityIndex));
				getBelow().receiveJob(
						new DispatchJobEvent(jobEvent.getOwningProcess(),
								jobEvent.getIndex(), jobEvent.getContext(),
								jobEvent.getData(), newActivityList));
				return true;
			}
		}
	}

	public void configure(Object config) {
		// Do nothing - there is no configuration to do
	}

	public Object getConfiguration() {
		return null;
	}

}
