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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.invocation.Completion;
import net.sf.taverna.t2.invocation.IterationInternalEvent;
import net.sf.taverna.t2.workflowmodel.processor.activity.Job;

/**
 * Iteration strategy node that logs job and completion events for analysis
 * during debugging.
 * 
 * @author Tom Oinn
 * 
 */
@SuppressWarnings("unchecked") //suppressed to avoid jdk1.5 compilation errors caused by the declaration IterationInternalEvent<? extends IterationInternalEvent<?>> e
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

	public synchronized void receiveJob(int inputIndex, Job newJob) {
		List<IterationInternalEvent> jobs = ownerToJobList.get(newJob.getOwningProcess());
		if (jobs == null) {
			jobs = new ArrayList<IterationInternalEvent>();
			ownerToJobList.put(newJob.getOwningProcess(), jobs);
		}
		jobs.add(newJob);
	}

	public int getIterationDepth(Map<String, Integer> inputDepths) throws IterationTypeMismatchException {
		// TODO Auto-generated method stub
		return 0;
	}
}
