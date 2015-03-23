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

import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.invocation.Completion;
import org.apache.taverna.invocation.TreeCache;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.processor.activity.Job;

/**
 * The dot product matches jobs by index array, when a job is received a job is
 * emited if and only if the index array of the new job is matched exactly by
 * index arrays of one job in each other input index.
 * 
 * @author Tom Oinn
 */
@SuppressWarnings("serial")
public class DotProduct extends CompletionHandlingAbstractIterationStrategyNode {
	Map<String, TreeCache[]> ownerToCache = synchronizedMap(new HashMap<String, TreeCache[]>());

	@Override
	public synchronized void innerReceiveJob(int inputIndex, Job newJob) {
		if (getChildCount() == 1) {
			/*
			 * if there's only one input there's nothing to do here so push the
			 * job through
			 */
			pushJob(newJob);
			return;
		}
		String owningProcess = newJob.getOwningProcess();
		if (!ownerToCache.containsKey(owningProcess)) {
			TreeCache[] caches = new TreeCache[getChildCount()];
			for (int i = 0; i < getChildCount(); i++)
				caches[i] = new TreeCache();
			ownerToCache.put(owningProcess, caches);
		}
		/*
		 * Firstly store the new job in the cache, this isn't optimal but is
		 * safe for now - we can make this more efficient by doing the
		 * comparison first and only storing the job if required
		 */
		TreeCache[] caches = ownerToCache.get(owningProcess);
		caches[inputIndex].insertJob(newJob);
		int[] indexArray = newJob.getIndex();
		boolean foundMatch = true;
		Map<String, T2Reference> newDataMap = new HashMap<>();
		for (TreeCache cache : caches)
			if (cache.containsLocation(indexArray))
				newDataMap.putAll(cache.get(indexArray).getData());
			else
				foundMatch = false;
		if (foundMatch) {
			Job j = new Job(owningProcess, indexArray, newDataMap, newJob
					.getContext());
			/*
			 * Remove all copies of the job with this index from the cache,
			 * we'll never use it again and it pays to be tidy
			 */
			for (TreeCache cache : caches)
				cache.cut(indexArray);
			pushJob(j);
		}
	}

	/**
	 * Delegate to the superclass to propogate completion events if and only if
	 * the completion event is a final one. We can potentially implement finer
	 * grained logic here in the future.
	 */
	@Override
	public void innerReceiveCompletion(int inputIndex,
			Completion completion) {
		/*
		 * Do nothing, let the superclass handle final completion events, ignore
		 * others for now (although in theory we should be able to do better
		 * than this really)
		 */
	}

	@Override
	protected  void cleanUp(String owningProcess) {
		ownerToCache.remove(owningProcess);
	}

	@Override
	public int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException {
		// Check that all input depths are the same
		if (isLeaf())
			// No children!
			throw new IterationTypeMismatchException("Dot product with no children");			
		int depth = getChildAt(0).getIterationDepth(inputDepths);
		for (IterationStrategyNode childNode : getChildren())
			if (childNode.getIterationDepth(inputDepths) != depth)
				throw new IterationTypeMismatchException(
						"Mismatched input types for dot product node");
		return depth;
	}
}
