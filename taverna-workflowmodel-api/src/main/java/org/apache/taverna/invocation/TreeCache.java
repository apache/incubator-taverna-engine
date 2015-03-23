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

package org.apache.taverna.invocation;

import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.workflowmodel.processor.activity.Job;

/**
 * Tree cache for jobs waiting to be combined and dispatched down the iteration
 * system
 * 
 * @author Tom Oinn
 */
public class TreeCache {
	private NamedNode root = null;
	private int indexDepth = -1;

	/**
	 * Show the tree structure, printing each node recursively
	 */
	@Override
	public synchronized String toString() {
		if (root == null)
			return "No root node defined.";
		StringBuilder sb = new StringBuilder();
		printNode(root, sb, "");
		return sb.toString();
	}
	
	private synchronized void printNode(NamedNode node, StringBuilder sb, String indent) {
		sb.append(indent).append("Node (").append(node.contents).append(")\n");
		String newIndent = indent + "  ";
		for (NamedNode child : node.children)
			if (child == null)
				sb.append(newIndent).append("null\n");
			else
				printNode(child, sb, newIndent);
	}
	
	public class NamedNode {
		public Job contents = null;
		public List<NamedNode> children = new ArrayList<>();

		public void insertJob(Job j) {
			insertJobAt(j, j.getIndex());
		}

		private void insertJobAt(Job j, int[] position) {
			if (position.length == 0) {
				this.contents = j;
				return;
			}
			int firstIndex = position[0];
			if (firstIndex >= children.size())
				// Pad with blank NamedNode objects
				for (int i = children.size(); i <= firstIndex; i++)
					children.add(null);
			NamedNode child = children.get(firstIndex);
			if (child == null) {
				child = new NamedNode();
				children.set(firstIndex, child);
			}

			int[] newTarget = new int[position.length - 1];
			for (int i = 1; i < position.length; i++)
				newTarget[i - 1] = position[i];
			child.insertJobAt(j, newTarget);
		}

		public NamedNode childAt(int i) {
			if (i >= children.size())
				return null;
			return children.get(i);
		}
	}

	/**
	 * The length of index arrays of jobs within the TreeCache. This assumes
	 * that all jobs have the same index array length, this is true when the
	 * cache is used by the iteration strategy but may not be in other
	 * scenarios, use with caution!
	 * <p>
	 * If no jobs have been submitted this method returns -1
	 */
	public int getIndexLength() {
		return this.indexDepth;
	}

	/**
	 * Add a job to the cache, the job is inserted at a position corresponding
	 * to its index array property
	 * 
	 * @param j
	 */
	public synchronized void insertJob(Job j) {
		if (root == null)
			root = new NamedNode();
		indexDepth = j.getIndex().length;
		root.insertJob(j);
	}

	protected synchronized NamedNode nodeAt(int[] position) {
		if (root == null)
			return null;
		NamedNode result = root;
		int index = 0;
		while (index < position.length && result != null)
			result = result.childAt(position[index++]);
		return result;
	}

	/**
	 * Chop the cache off at the specified index
	 * 
	 * @param indexArray
	 */
	public synchronized void cut(int[] indexArray) {
		if (indexArray.length > 0) {
			int[] newIndex = tail(indexArray);
			NamedNode node = nodeAt(newIndex);
			if (node != null
					&& node.children.size() >= indexArray[indexArray.length - 1])
				node.children.set(indexArray[indexArray.length - 1], null);
		}
	}

	/**
	 * Recursively fetch contents of all nodes under the specified index array,
	 * used by the prefix matching iteration strategy
	 */
	public synchronized List<Job> jobsWithPrefix(int[] prefix) {
		List<Job> jobs = new ArrayList<>();
		NamedNode prefixNode = nodeAt(prefix);
		if (prefixNode != null)
			getJobsUnder(prefixNode, jobs);
		return jobs;
	}

	private synchronized void getJobsUnder(NamedNode node, List<Job> jobs) {
		if (node.contents != null)
			jobs.add(node.contents);
		else
			for (NamedNode child : node.children)
				getJobsUnder(child, jobs);
	}

	/**
	 * Does the location exist?
	 * 
	 * @param location
	 * @return whether the contents of the location are non null
	 */
	public synchronized boolean containsLocation(int[] location) {
		return (get(location) != null);
	}

	/**
	 * Get the job object at the specified index array
	 * 
	 * @param location
	 * @return Job at the specified location or null if no such job was found
	 */
	public synchronized Job get(int[] location) {
		NamedNode n = nodeAt(location);
		return (n == null ? null : n.contents);
	}

	/**
	 * Chop the last index off an int[]
	 * 
	 * @param arg
	 * @return
	 */
	private static int[] tail(int[] arg) {
		int result[] = new int[arg.length - 1];
		for (int i = 0; i < arg.length - 1; i++)
			result[i] = arg[i];
		return result;
	}
}
