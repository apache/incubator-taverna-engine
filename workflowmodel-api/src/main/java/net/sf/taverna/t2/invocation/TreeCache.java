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
package net.sf.taverna.t2.invocation;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.processor.activity.Job;

/**
 * Tree cache for jobs waiting to be combined and dispatched down the iteration
 * system
 * 
 * @author Tom Oinn
 * 
 */
public class TreeCache {

	private NamedNode root = null;

	private int indexDepth = -1;

	/**
	 * Show the tree structure, printing each node recursively
	 */
	@Override
	public synchronized String toString() {
		StringBuffer sb = new StringBuffer();
		if (root != null) {
			printNode(root, sb, "");
		}
		else {
			sb.append("No root node defined.");
		}
		return sb.toString();
	}
	
	private synchronized void printNode(NamedNode node, StringBuffer sb, String indent) {
		sb.append(indent+"Node ("+node.contents+")\n");
		String newIndent = indent + "  ";
		for (NamedNode child : node.children) {
			if (child == null) {
				sb.append(newIndent+"null\n");
			}
			else {
				printNode(child, sb, newIndent);
			}
		}
	}
	
	public class NamedNode {

		public Job contents = null;

		public List<NamedNode> children = new ArrayList<NamedNode>();

		public void insertJob(Job j) {
			insertJobAt(j, j.getIndex());
		}

		private void insertJobAt(Job j, int[] position) {
			if (position.length == 0) {
				this.contents = j;
				return;
			}
			int firstIndex = position[0];
			if (firstIndex >= children.size()) {
				// Pad with blank NamedNode objects
				for (int i = children.size(); i <= firstIndex; i++) {
					children.add(null);
				}
			}
			NamedNode child = children.get(firstIndex);
			if (child == null) {
				child = new NamedNode();
				children.set(firstIndex, child);
			}

			int[] newTarget = new int[position.length - 1];
			for (int i = 1; i < position.length; i++) {
				newTarget[i - 1] = position[i];
			}
			child.insertJobAt(j, newTarget);
		}

		public NamedNode childAt(int i) {
			if (i >= children.size()) {
				return null;
			} else {
				return children.get(i);
			}
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
		if (root == null) {
			root = new NamedNode();
		}
		indexDepth = j.getIndex().length;
		root.insertJob(j);
	}

	protected synchronized NamedNode nodeAt(int[] position) {
		if (root == null) {
			return null;
		}
		NamedNode result = root;
		int index = 0;
		while (index < position.length && result != null) {
			result = result.childAt(position[index++]);
		}
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
			if (node != null) {
				if (node.children.size() >= indexArray[indexArray.length - 1]) {
					node.children.set(indexArray[indexArray.length - 1], null);
				}
			}
		}
	}

	/**
	 * Recursively fetch contents of all nodes under the specified index array,
	 * used by the prefix matching iteration strategy
	 */
	public synchronized List<Job> jobsWithPrefix(int[] prefix) {
		List<Job> jobs = new ArrayList<Job>();
		NamedNode prefixNode = nodeAt(prefix);
		if (prefixNode != null) {
			getJobsUnder(prefixNode, jobs);
		}
		return jobs;
	}

	private synchronized void getJobsUnder(NamedNode node, List<Job> jobs) {
		if (node.contents != null) {
			jobs.add(node.contents);
		} else {
			for (NamedNode child : node.children) {
				getJobsUnder(child, jobs);
			}
		}
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
		if (n == null) {
			return null;
		}
		return n.contents;
	}

	/**
	 * Chop the last index off an int[]
	 * 
	 * @param arg
	 * @return
	 */
	private static int[] tail(int[] arg) {
		int result[] = new int[arg.length - 1];
		for (int i = 0; i < arg.length - 1; i++) {
			result[i] = arg[i];
		}
		return result;
	}

}
