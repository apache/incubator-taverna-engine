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

/**
 * Contains a (possibly partial) completion event. The completion event is a
 * statement that no further events will occur on this channel with an index
 * prefixed by the completion index. As with Job events completion events have
 * an owning process with the same semantics as that of the Job class
 * <p>
 * The conceptual depth of a completion is the sum of the length of index array
 * for any data tokens the completion shares a stream with and the depth of
 * those tokens. This should be constant for any given token stream.
 * 
 * @author Tom Oinn
 * 
 */
public class Completion extends IterationInternalEvent<Completion> {

	/**
	 * Construct a new optionally partial completion event with the specified
	 * owner and completion index
	 * 
	 * @param owningProcess
	 * @param completionIndex
	 */
	public Completion(String owningProcess, int[] completionIndex,
			InvocationContext context) {
		super(owningProcess, completionIndex, context);
	}

	/**
	 * Construct a new final completion event, equivalent to calling new
	 * Completion(owningProcess, new int[0]);
	 * 
	 * @param owningProcess
	 */
	public Completion(String owningProcess, InvocationContext context) {
		super(owningProcess, new int[0], context);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Cmp(" + owner + ")[");
		for (int i = 0; i < index.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(index[i] + "");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Push the index array onto the owning process name and return the new Job
	 * object. Does not modify this object, the method creates a new Job with
	 * the modified index array and owning process
	 * 
	 * @return
	 */
	@Override
	public Completion pushIndex() {
		return new Completion(getPushedOwningProcess(), new int[] {}, context);
	}

	/**
	 * Pull the index array previous pushed to the owning process name and
	 * prepend it to the current index array
	 */
	@Override
	public Completion popIndex() {
		return new Completion(owner.substring(0, owner.lastIndexOf(':')),
				getPoppedIndex(), context);
	}

	@Override
	public Completion popOwningProcess() throws ProcessIdentifierException {
		return new Completion(popOwner(), index, context);
	}

	@Override
	public Completion pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException {
		return new Completion(pushOwner(localProcessName), index, context);
	}

}
