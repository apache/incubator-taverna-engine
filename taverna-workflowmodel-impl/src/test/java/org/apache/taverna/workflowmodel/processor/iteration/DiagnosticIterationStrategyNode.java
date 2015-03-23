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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.taverna.invocation.Completion;
import org.apache.taverna.invocation.IterationInternalEvent;
import org.apache.taverna.workflowmodel.processor.activity.Job;

/**
 * Iteration strategy node that logs job and completion events for analysis
 * during debugging.
 * 
 * @author Tom Oinn
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" }) //suppressed to avoid jdk1.5 compilation errors caused by the declaration IterationInternalEvent<? extends IterationInternalEvent<?>> e
public class DiagnosticIterationStrategyNode extends
		AbstractIterationStrategyNode {

	private Map<String, List<IterationInternalEvent>> ownerToJobList;

	public DiagnosticIterationStrategyNode() {
		this.ownerToJobList = new HashMap<String, List<IterationInternalEvent>>();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (String owner : ownerToJobList.keySet()) {
			sb.append(owner + "\n");
			List<IterationInternalEvent> jobs = ownerToJobList.get(owner);
			for (IterationInternalEvent<?> w : jobs) {
				sb.append("  " + w.toString() + "\n");
			}
		}
		return sb.toString();
	}

	public int jobsReceived(String string) {
		if (ownerToJobList.containsKey(string) == false) {
			return 0;
		}
		int number = 0;
		for (IterationInternalEvent w : ownerToJobList.get(string)) {
			if (w instanceof Job) {
				number++;
			}
		}
		return number;
	}

	public boolean containsJob(String owningProcess, int[] jobIndex) {
		List<IterationInternalEvent> jobs = ownerToJobList.get(owningProcess);
		if (jobs == null) {
			return false;
		}
		for (IterationInternalEvent w : jobs) {
			if (w instanceof Job) {
				Job j = (Job)w;
				if (compareArrays(j.getIndex(), jobIndex)
						&& j.getOwningProcess().equals(owningProcess)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean compareArrays(int[] a1, int[] a2) {
		if (a1.length != a2.length) {
			return false;
		}
		for (int i = 0; i < a1.length; i++) {
			if (a1[i] != a2[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized void receiveCompletion(int inputIndex,
			Completion completion) {
		String owningProcess = completion.getOwningProcess();
		List<IterationInternalEvent> jobs = ownerToJobList.get(owningProcess);
		if (jobs == null) {
			jobs = new ArrayList<IterationInternalEvent>();
			ownerToJobList.put(owningProcess, jobs);
		}
		jobs.add(completion);
	}

	@Override
	public synchronized void receiveJob(int inputIndex, Job newJob) {
		List<IterationInternalEvent> jobs = ownerToJobList.get(newJob.getOwningProcess());
		if (jobs == null) {
			jobs = new ArrayList<IterationInternalEvent>();
			ownerToJobList.put(newJob.getOwningProcess(), jobs);
		}
		jobs.add(newJob);
	}

	@Override
	public int getIterationDepth(Map<String, Integer> inputDepths) throws IterationTypeMismatchException {
		// TODO Auto-generated method stub
		return 0;
	}
}
