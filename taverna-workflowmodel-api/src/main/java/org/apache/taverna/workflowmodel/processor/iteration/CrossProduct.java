/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.workflowmodel.processor.iteration;

import static java.util.Collections.synchronizedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.invocation.Completion;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.processor.activity.Job;

/**
 * A cross product node combines its inputs in an 'all against all' manner. When
 * a new job is received on index 'n' a set of jobs is emited corresponding to
 * the combination of the new job with all other jobs on input indices other
 * than 'n'.
 * 
 * @author Tom Oinn
 * @author David Withers
 */
@SuppressWarnings("serial")
public class CrossProduct extends
		CompletionHandlingAbstractIterationStrategyNode {
	private Map<String, List<Set<Job>>> ownerToCache = synchronizedMap(new HashMap<String, List<Set<Job>>>());

	/**
	 * Receive a job, emit jobs corresponding to the orthogonal join of the new
	 * job with all jobs in all other input lists.
	 */
	@Override
	public synchronized void innerReceiveJob(int inputIndex, Job newJob) {
		if (getChildCount() == 1) {
			/*
			 * there's only one input and there's nothing to do here so push the
			 * job through
			 */
			pushJob(newJob);
			return;
		}
		if (!ownerToCache.containsKey(newJob.getOwningProcess())) {
			List<Set<Job>> perInputCache = new ArrayList<>();
			for (int i = 0; i < getChildCount(); i++)
				perInputCache.add(new HashSet<Job>());
			ownerToCache.put(newJob.getOwningProcess(), perInputCache);
		}
		// Store the new job
		List<Set<Job>> perInputCache = ownerToCache.get(newJob
				.getOwningProcess());
		perInputCache.get(inputIndex).add(newJob);
		/*
		 * Find all combinations of the new job with all permutations of jobs in
		 * the other caches. We could make this a lot easier by restricting it
		 * to a single pair of inputs, this might be a more sane way to go in
		 * the future...
		 */
		Set<Job> workingSet = perInputCache.get(0);
		if (inputIndex == 0) {
			workingSet = new HashSet<>();
			workingSet.add(newJob);
		}
		for (int i = 1; i < getChildCount(); i++) {
			Set<Job> thisSet = perInputCache.get(i);
			if (i == inputIndex) {
				/*
				 * This is the cache for the new job, so we rewrite the set to a
				 * single element one containing only the newly submitted job
				 */
				thisSet = new HashSet<>();
				thisSet.add(newJob);
			}
			workingSet = merge(workingSet, thisSet);
		}
		for (Job outputJob : workingSet)
			pushJob(outputJob);
		if (canClearCache(inputIndex, newJob.getOwningProcess()))
			/*
			 * If we've seen completions for all the other indexes we don't need
			 * to cache jobs for this index
			 */
			perInputCache.get(inputIndex).clear();
	}

	private Set<Job> merge(Set<Job> set1, Set<Job> set2) {
		Set<Job> newSet = new HashSet<>();
		for (Job job1 : set1)
			for (Job job2 : set2) {
				int[] newIndex = new int[job1.getIndex().length
						+ job2.getIndex().length];
				int j = 0;
				for (int i = 0; i < job1.getIndex().length; i++)
					newIndex[j++] = job1.getIndex()[i];
				for (int i = 0; i < job2.getIndex().length; i++)
					newIndex[j++] = job2.getIndex()[i];
				Map<String, T2Reference> newDataMap = new HashMap<>();
				newDataMap.putAll(job1.getData());
				newDataMap.putAll(job2.getData());
				newSet.add(new Job(job1.getOwningProcess(), newIndex,
						newDataMap, job1.getContext()));
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
		for (int i = 0; i < completionState.length; i++)
			if (i != inputIndex && !completionState[i])
				return false;
		return true;
	}

	@Override
	public int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException {
		if (isLeaf())
			// No children!
			throw new IterationTypeMismatchException(
					"Cross product with no children");
		int temp = 0;
		for (IterationStrategyNode child : getChildren())
			temp += child.getIterationDepth(inputDepths);
		return temp;
	}
}
