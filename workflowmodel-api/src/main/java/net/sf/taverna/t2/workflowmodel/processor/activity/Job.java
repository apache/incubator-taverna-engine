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
package net.sf.taverna.t2.workflowmodel.processor.activity;

import java.util.Map;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.IterationInternalEvent;
import net.sf.taverna.t2.invocation.ProcessIdentifierException;
import net.sf.taverna.t2.reference.T2Reference;

/**
 * Contains a (possibly partial) job description. A job is the smallest entity
 * that can be enacted by the invocation layer of the dispatch stack within a
 * processor. Jobs are partial jobs if the set of keys in the data map is not
 * identical to the set of named input ports on the processor within which the
 * job is used. These objects are used internally within the processor to stage
 * data during iteration and within the dispatch stack, they do not appear
 * within the workflow itself.
 * 
 * @author Tom Oinn
 * 
 */
public class Job extends IterationInternalEvent<Job> {

	private Map<String, T2Reference> dataMap;

	/**
	 * Push the index array onto the owning process name and return the new Job
	 * object. Does not modify this object, the method creates a new Job with
	 * the modified index array and owning process
	 * 
	 * @return
	 */
	@Override
	public Job pushIndex() {
		return new Job(getPushedOwningProcess(), new int[] {}, dataMap, context);
	}

	/**
	 * Pull the index array previous pushed to the owning process name and
	 * prepend it to the current index array
	 */
	@Override
	public Job popIndex() {
		return new Job(owner.substring(0, owner.lastIndexOf(':')),
				getPoppedIndex(), dataMap, context);
	}

	/**
	 * The actual data carried by this (partial) Job object is in the form of a
	 * map, where the keys of the map are Strings identifying the named input
	 * and the values are Strings containing valid data identifiers within the
	 * context of a visible DataManager object (see CloudOne specification for
	 * further information on the DataManager system)
	 * 
	 * @return Map of name to data reference for this Job
	 */
	public Map<String, T2Reference> getData() {
		return this.dataMap;
	}

	/**
	 * Create a new Job object with the specified owning process (colon
	 * separated 'list' of process identifiers), index array and data map
	 * 
	 * @param owner
	 * @param index
	 * @param data
	 */
	public Job(String owner, int[] index, Map<String, T2Reference> data,
			InvocationContext context) {
		super(owner, index, context);
		this.dataMap = data;

	}

	/**
	 * Show the owner, index array and data map in textual form for debugging
	 * and any other purpose. Jobs appear in the form :
	 * 
	 * <pre>
	 * Job(Process1)[2,0]{Input2=dataID4,Input1=dataID3}
	 * </pre>
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Job(" + owner + ")[");
		for (int i = 0; i < index.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(index[i] + "");
		}
		sb.append("]{");
		boolean first = true;
		for (String key : dataMap.keySet()) {
			if (!first) {
				sb.append(",");
			}
			sb.append(key + "=" + dataMap.get(key));
			first = false;
		}
		sb.append("}");
		return sb.toString();
	}

	@Override
	public Job popOwningProcess() throws ProcessIdentifierException {
		return new Job(popOwner(), index, dataMap, context);
	}

	@Override
	public Job pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException {
		return new Job(pushOwner(localProcessName), index, dataMap, context);
	}

}
