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

package org.apache.taverna.workflowmodel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.taverna.invocation.Completion;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.IterationInternalEvent;
import org.apache.taverna.invocation.TreeCache;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.processor.activity.Job;

/**
 * Receives Job and Completion events and emits Jobs unaltered. Completion
 * events additionally cause registration of lists for each key in the datamap
 * of the jobs at immediate child locations in the index structure. These list
 * identifiers are sent in place of the Completion events.
 * <p>
 * State for a given process ID is purged when a final completion event is
 * received so there is no need for an explicit cache purge operation in the
 * public API (although for termination of partially complete workflows it may
 * be sensible for subclasses to provide one)
 * <p>
 * 
 * @author Tom Oinn
 * @author David Withers
 */
public abstract class AbstractCrystalizer implements Crystalizer {
	private Map<String, CompletionAwareTreeCache> cacheMap = new HashMap<>();

	public abstract Job getEmptyJob(String owningProcess, int[] index,
			InvocationContext context);

	/**
	 * Receive a Job or Completion, Jobs are emitted unaltered and cached,
	 * Completion events trigger registration of a corresponding list - this may
	 * be recursive in nature if the completion event's index implies nested
	 * lists which have not been registered.
	 * <p>
	 * If the baseListDepth property is defined then completion events on nodes
	 * which don't already exist create empty jobs instead and emit those, if
	 * undefined the completion event is emited intact.
	 * 
	 * @param e The event (a {@link Job} or a {@link Completion})
	 */
	@Override
	public void receiveEvent(IterationInternalEvent<?> e) {
		String owningProcess = e.getOwningProcess();
		CompletionAwareTreeCache cache = null;
		synchronized (cacheMap) {
			if (!cacheMap.containsKey(owningProcess)) {
				cache = new CompletionAwareTreeCache(owningProcess, e
						.getContext());
				cacheMap.put(owningProcess, cache);
			} else
				cache = cacheMap.get(owningProcess);
		}
		if (e instanceof Job) {
			// Pass through Job after storing it in the cache
			Job j = (Job) e;
			synchronized (cache) {
				cache.insertJob(new Job("", j.getIndex(), j.getData(), j
						.getContext()));
				jobCreated(j);
				if (j.getIndex().length == 0)
					cacheMap.remove(j.getOwningProcess());
			}
		} else if (e instanceof Completion) {
			Completion c = (Completion) e;
			synchronized (cache) {
				cache.resolveAt(owningProcess, c.getIndex());
				if (c.getIndex().length == 0)
					cacheMap.remove(c.getOwningProcess());
			}
		}
	}

	protected class CompletionAwareTreeCache extends TreeCache {
		private String owningProcess;
		private InvocationContext context;

		public CompletionAwareTreeCache(String owningProcess,
				InvocationContext context) {
			super();
			this.context = context;
			this.owningProcess = owningProcess;
		}

		public void resolveAt(String owningProcess, int[] completionIndex) {
			NamedNode n = nodeAt(completionIndex);
			if (n != null) {
				assignNamesTo(n, completionIndex);
				return;
			}

			/*
			 * We know what the list depth should be, so we can construct
			 * appropriate depth empty lists to fill in the gaps.
			 */

			Job j = getEmptyJob(owningProcess, completionIndex, context);
			insertJob(j);
			jobCreated(j);
		}

		private void assignNamesTo(NamedNode n, int[] index) {
			/* Only act if contents of this node undefined */
			if (n.contents != null)
				return;

			Map<String, List<T2Reference>> listItems = new HashMap<>();
			int pos = 0;
			for (NamedNode child : n.children) {
				/*
				 * If child doesn't have a defined name map yet then define it.
				 */
				Job j;
				if (child == null) {
					/*
					 * happens if we're completing a partially empty collection
					 * structure
					 */
					int[] newIndex = new int[index.length + 1];
					for (int i = 0; i < index.length; i++)
						newIndex[i] = index[i];
					newIndex[index.length] = pos++;
					j = getEmptyJob(owningProcess, newIndex, context);
					jobCreated(j);
				} else if (child.contents == null) {
					int[] newIndex = new int[index.length + 1];
					for (int i = 0; i < index.length; i++)
						newIndex[i] = index[i];
					newIndex[index.length] = pos++;
					assignNamesTo(child, newIndex);
					j = child.contents;
				} else {
					pos++;
					j = child.contents;
				}

				/*
				 * Now pull the names out of the child job map and push them
				 * into lists to be registered
				 */

				for (String outputName : j.getData().keySet()) {
					List<T2Reference> items = listItems.get(outputName);
					if (items == null) {
						items = new ArrayList<>();
						listItems.put(outputName, items);
					}
					items.add(j.getData().get(outputName));
				}
			}
			Map<String, T2Reference> newDataMap = new HashMap<>();
			for (String outputName : listItems.keySet())
				newDataMap.put(
						outputName,
						context.getReferenceService()
								.getListService()
								.registerList(listItems.get(outputName),
										context).getId());
			Job newJob = new Job(owningProcess, index, newDataMap, context);
			n.contents = newJob;

			/* Get rid of the children as we've now named this node */

			n.children.clear();
			jobCreated(n.contents);
		}
	}
}
