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
package net.sf.taverna.t2.workflowmodel.processor.iteration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.taverna.t2.invocation.Completion;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.processor.activity.Job;

/**
 * A cross product node combines its inputs in an 'all against all' manner. When
 * a new job is received on index 'n' a set of jobs is emited corresponding to
 * the combination of the new job with all other jobs on input indices other
 * than 'n'.
 * 
 * @author Tom Oinn
 * @author David Withers
 */
public class CrossProduct extends CompletionHandlingAbstractIterationStrategyNode {

	private Map<String, List<Set<Job>>> ownerToCache = Collections.synchronizedMap(new HashMap<String, List<Set<Job>>>());

	/**
	 * Receive a job, emit jobs corresponding to the orthogonal join of the new
	 * job with all jobs in all other input lists.
	 */
	@Override
	public synchronized void innerReceiveJob(int inputIndex, Job newJob) {
		if (getChildCount() == 1) {
			// there's only one input and there's nothing to do here so push the
			// job through
			pushJob(newJob);
			return;
		}
		if (!ownerToCache.containsKey(newJob.getOwningProcess())) {
			List<Set<Job>> perInputCache = new ArrayList<Set<Job>>();
			for (int i = 0; i < getChildCount(); i++) {
				perInputCache.add(new HashSet<Job>());
			}
			ownerToCache.put(newJob.getOwningProcess(), perInputCache);
		}
		// Store the new job
		List<Set<Job>> perInputCache = ownerToCache.get(newJob.getOwningProcess());
		perInputCache.get(inputIndex).add(newJob);
		// Find all combinations of the new job with all permutations of jobs in
		// the other caches. We could make this a lot easier by restricting it
		// to a single pair of inputs, this might be a more sane way to go in
		// the future...
		Set<Job> workingSet = perInputCache.get(0);
		if (inputIndex == 0) {
			workingSet = new HashSet<Job>();
			workingSet.add(newJob);
		}
		for (int i = 1; i < getChildCount(); i++) {
			Set<Job> thisSet = perInputCache.get(i);
			if (i == inputIndex) {
				// This is the cache for the new job, so we rewrite the set to a
				// single element one containing only the newly submitted job
				thisSet = new HashSet<Job>();
				thisSet.add(newJob);
			}
			workingSet = merge(workingSet, thisSet);
		}
		for (Job outputJob : workingSet) {
			pushJob(outputJob);
		}
		if (canClearCache(inputIndex, newJob.getOwningProcess())) {
			// If we've seen completions for all the other indexes we don't need
			// to cache jobs for this index
			perInputCache.get(inputIndex).clear();
		}
	}

	private Set<Job> merge(Set<Job> set1, Set<Job> set2) {
		Set<Job> newSet = new HashSet<Job>();
		for (Job job1 : set1) {
			for (Job job2 : set2) {
				int[] newIndex = new int[job1.getIndex().length + job2.getIndex().length];
				int j = 0;
				for (int i = 0; i < job1.getIndex().length; i++) {
					newIndex[j++] = job1.getIndex()[i];
				}
				for (int i = 0; i < job2.getIndex().length; i++) {
					newIndex[j++] = job2.getIndex()[i];
				}
				Map<String, T2Reference> newDataMap = new HashMap<String, T2Reference>();
				newDataMap.putAll(job1.getData());
				newDataMap.putAll(job2.getData());
				newSet.add(new Job(job1.getOwningProcess(), newIndex, newDataMap, job1.getContext()));
			}
		}
		return newSet;
	}

	@Override
	public void innerReceiveCompletion(int inputIndex, Completion completion) {
		// Do nothing, let the superclass handle final completion events
	}

	@Override
	protected final void cleanUp(String owningProcess) {
		ownerToCache.remove(owningProcess);
	}

	/**
	 * Returns true iff completions have been received for all other inputs.
	 * 
	 * @param inputIndex
	 * @param owningProcess
	 * @return true iff completions have been received for all other inputs
	 */
	private boolean canClearCache(int inputIndex, String owningProcess) {
		boolean[] completionState = getCompletionState(owningProcess).inputComplete;
		for (int i = 0; i < completionState.length; i++) {
			if (i != inputIndex) {
				if (!completionState[i]) {
					return false;
				}
			}
		}
		return true;
	}

	public int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException {
		if (isLeaf()) {
			// No children!
			throw new IterationTypeMismatchException("Cross product with no children");
		}
		int temp = 0;
		for (IterationStrategyNode child : getChildren()) {
			temp += child.getIterationDepth(inputDepths);
		}
		return temp;
	}

}
