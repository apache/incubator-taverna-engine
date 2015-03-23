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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.taverna.invocation.TreeCache;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.processor.activity.Job;

/**
 * Matches jobs where the index array of the job on index 0 is the prefix of the
 * index array of the job on index 1. This node can only ever have exactly two
 * child nodes!
 * 
 * @author Tom Oinn
 */
@SuppressWarnings("serial")
public class PrefixDotProduct extends DotProduct {
	@Override
	protected synchronized final void cleanUp(String owningProcess) {
		ownerToCache.remove(owningProcess);
	}

	@Override
	public void innerReceiveJob(int inputIndex, Job newJob) {
		String owningProcess = newJob.getOwningProcess();
		TreeCache[] caches;
		synchronized (ownerToCache) {
			caches = ownerToCache.get(owningProcess);
			// Create the caches if not already initialized
			if (caches == null) {
				caches = new TreeCache[getChildCount()];
				for (int i = 0; i < getChildCount(); i++)
					caches[i] = new TreeCache();
				ownerToCache.put(owningProcess, caches);
			}
		}

		// Store the job
		caches[inputIndex].insertJob(newJob);

		/*
		 * If this job came in on index 0 we have to find all jobs in the cache
		 * for index 1 which have the index array as a prefix. Fortunately this
		 * is quite easy due to the tree structure of the cache, we can just ask
		 * for all nodes in the cache with that index.
		 */
		if (inputIndex == 0) {
			int[] prefixIndexArray = newJob.getIndex();
			List<Job> matchingJobs;
			synchronized (caches[1]) {
				// Match all jobs and remove them so other calls can't produce
				// duplicates
				matchingJobs = caches[1].jobsWithPrefix(prefixIndexArray);
				caches[1].cut(prefixIndexArray);
			}
			for (Job job : matchingJobs) {
				Map<String, T2Reference> newDataMap = new HashMap<>();
				newDataMap.putAll(newJob.getData());
				newDataMap.putAll(job.getData());
				Job mergedJob = new Job(owningProcess, job.getIndex(),
						newDataMap, newJob.getContext());
				pushJob(mergedJob);
			}
		}

		/*
		 * If the job came in on index 1 we have to find the job on index 0 that
		 * matches the first 'n' indices, where 'n' is determined by the depth
		 * of jobs on the cache for index 0.
		 */
		else if (inputIndex == 1) {
			// Only act if we've received jobs on the cache at index 0
			if (caches[0].getIndexLength() > 0) {
				int[] prefix = new int[caches[0].getIndexLength()];
				for (int i = 0; i < prefix.length; i++)
					prefix[i] = newJob.getIndex()[i];
				Job j = caches[0].get(prefix);
				if (j != null) {
					Map<String, T2Reference> newDataMap = new HashMap<>();
					newDataMap.putAll(j.getData());
					newDataMap.putAll(newJob.getData());
					Job mergedJob = new Job(owningProcess, newJob.getIndex(),
							newDataMap, newJob.getContext());
					pushJob(mergedJob);
				}
			}
		}
	}
}
