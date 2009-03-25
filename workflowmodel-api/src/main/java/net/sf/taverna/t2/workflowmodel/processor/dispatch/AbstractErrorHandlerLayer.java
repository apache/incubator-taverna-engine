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
package net.sf.taverna.t2.workflowmodel.processor.dispatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchCompletionEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchErrorEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchJobEvent;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchResultEvent;

/**
 * Superclass of error handling dispatch layers (for example retry and
 * failover). Provides generic functionality required by this class of layers.
 * 
 * @author Tom Oinn
 * @author Stian Soiland-Reyes
 * 
 */
public abstract class AbstractErrorHandlerLayer<ConfigurationType> extends
		AbstractDispatchLayer<ConfigurationType> {

	private static Logger logger = Logger
			.getLogger(AbstractErrorHandlerLayer.class);

	/**
	 * Compare two arrays of ints, return true if they are the same length and
	 * if at every index the two integer values are equal
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static boolean identicalIndex(int[] a, int[] b) {
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Map of process name -> list of state models. Note that all access to this
	 * map must be synchronized on the stateMap, and access to the lists inside
	 * it must be synchronized on the list.
	 * 
	 * @see #addJobToStateList(DispatchJobEvent)
	 * @see #removeJob(String, JobState)
	 * @see #getJobsDefault(String)
	 * @see #getJobsCopy(String)
	 */
	private Map<String, List<JobState>> stateMap = new HashMap<String, List<JobState>>();

	protected AbstractErrorHandlerLayer() {
		super();
	}

	/**
	 * Clear cached state for the specified process when notified by the
	 * dispatch stack
	 */
	@Override
	public void finishedWith(String owningProcess) {
		synchronized (stateMap) {
			stateMap.remove(owningProcess);
		}
	}

	/**
	 * If an error occurs we can either handle the error or send it to the layer
	 * above for further processing.
	 */
	@Override
	public void receiveError(DispatchErrorEvent errorEvent) {
		String owningProcess = errorEvent.getOwningProcess();
		List<JobState> activeJobs = getJobsCopy(owningProcess);

		for (JobState rs : activeJobs) {
			if (identicalIndex(rs.jobEvent.getIndex(), errorEvent.getIndex())) {
				boolean handled = rs.handleError();
				if (!handled) {
					removeJob(owningProcess, rs);
					getAbove().receiveError(errorEvent);
					return;
				}
			}
		}
	}

	/**
	 * Receive a job from the layer above, store it for later retries and pass
	 * it down to the next layer
	 */
	@Override
	public void receiveJob(DispatchJobEvent jobEvent) {
		addJobToStateList(jobEvent);
		getBelow().receiveJob(jobEvent);
	}

	/**
	 * If we see a result with an index matching one of those in the current
	 * retry state we can safely forget that state object
	 */
	@Override
	public void receiveResult(DispatchResultEvent j) {
		forget(j.getOwningProcess(), j.getIndex());
		getAbove().receiveResult(j);
	}

	/**
	 * If we see a completion event with an index matching one of those in the
	 * current retry state we can safely forget that state object
	 */
	@Override
	public void receiveResultCompletion(DispatchCompletionEvent c) {
		forget(c.getOwningProcess(), c.getIndex());
		getAbove().receiveResultCompletion(c);
	}

	/**
	 * Remove the specified pending retry job from the cache
	 * 
	 * @param owningProcess
	 *            Owning process identifier as returned by
	 *            {@link DispatchJobEvent#getOwningProcess()}
	 * @param index
	 *            Index of the job as returned by
	 *            {@link DispatchJobEvent#getIndex()}
	 */
	protected void forget(String owningProcess, int[] index) {
		for (JobState jobState : getJobsCopy(owningProcess)) {
			if (identicalIndex(jobState.jobEvent.getIndex(), index)) {
				removeJob(owningProcess, jobState);
				return;
			}
		}
		logger.error("Could not forget " + Arrays.asList(index));
	}

	protected void addJobToStateList(DispatchJobEvent jobEvent) {
		List<JobState> stateList = null;
		stateList = getJobsDefault(jobEvent.getOwningProcess());
		synchronized (stateList) {
			stateList.add(getStateObject(jobEvent));
		}
	}

	/**
	 * Get a copy of the list of {@link JobState}s for the owning process, or an
	 * empty list if the owning process is unknown or have been
	 * {@link #forget(String, int[]) forgotten}.
	 * <p>
	 * This list can safely be iterated over without synchronizing. If you need
	 * to modify the list, either synchronize over the returned list from
	 * {@link #getJobsDefault(String)} or use
	 * {@link #removeJob(String, JobState)}.
	 * 
	 * @param owningProcess
	 *            Owning process identifier as returned by
	 *            {@link DispatchJobEvent#getOwningProcess()}
	 * @return A copy of the list of known JobState {@link JobState}s for the
	 *         owning process,
	 */
	protected List<JobState> getJobsCopy(String owningProcess) {
		List<JobState> activeJobs;
		synchronized (stateMap) {
			activeJobs = stateMap.get(owningProcess);
		}
		if (activeJobs == null) {
			logger.error("Could not find any active jobs for " + owningProcess);
			return Collections.emptyList();
		}
		// Take a copy of the list so we don't modify it while iterating over it
		List<JobState> activeJobsCopy;
		synchronized (activeJobs) {
			activeJobsCopy = new ArrayList<JobState>(activeJobs);
		}
		return activeJobsCopy;
	}

	/**
	 * Get the list of {@link JobState}s for the owning process, creating and
	 * adding it to the state map if necessary.
	 * <p>
	 * Note that all access to the returned list must be synchronized on the
	 * list to avoid threading issues.
	 * <p>
	 * If you are going to iterate over the list, use
	 * {@link #getJobsCopy(String)} instead.
	 * 
	 * @see #getJobsCopy(String)
	 * @param owningProcess
	 *            Owning process identifier as returned by
	 *            {@link DispatchJobEvent#getOwningProcess()}
	 * 
	 * @return List of {@link JobState}s for the owning process
	 */
	protected List<JobState> getJobsDefault(String owningProcess) {
		List<JobState> stateList;
		synchronized (stateMap) {
			stateList = stateMap.get(owningProcess);
			if (stateList == null) {
				stateList = new ArrayList<JobState>();
				stateMap.put(owningProcess, stateList);
			}
		}
		return stateList;
	}

	/**
	 * Generate an appropriate state object from the specified job event. The
	 * state object is a concrete subclass of JobState.
	 * 
	 * @return
	 */
	protected abstract JobState getStateObject(DispatchJobEvent jobEvent);

	protected void removeJob(String owningProcess, JobState jobState) {
		List<JobState> activeJobs;
		synchronized (stateMap) {
			activeJobs = stateMap.get(owningProcess);
		}
		if (activeJobs == null) {
			logger.error("Could not find active jobs for " + owningProcess);
			return;
		}
		synchronized (activeJobs) {
			activeJobs.remove(jobState);
		}
	}

	/**
	 * Abstract superclass of all state models for pending failure handlers.
	 * This object is responsible for handling failure messages if they occur
	 * and represents the current state of the failure handling algorithm on a
	 * per job basis.
	 * 
	 * @author Tom Oinn
	 * 
	 */
	protected abstract class JobState {
		protected DispatchJobEvent jobEvent;

		protected JobState(DispatchJobEvent jobEvent) {
			this.jobEvent = jobEvent;
		}

		/**
		 * Called when the layer below pushes an error up and where the error
		 * index and owning process matches that of this state object. The
		 * implementation must deal with the error, either by handling it and
		 * pushing a new job down the stack or by rejecting it. If this method
		 * returns false the error has not been dealt with and MUST be pushed up
		 * the stack by the active dispatch layer. In this case the layer will
		 * be a subclass of AbstractErrorHandlerLayer and the logic to do this
		 * is already included in the receive methods for results, errors and
		 * completion events.
		 * 
		 * @return true if the error was handled.
		 */
		public abstract boolean handleError();

	}

}
