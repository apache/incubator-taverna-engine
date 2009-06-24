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

import net.sf.taverna.t2.reference.T2Reference;

/**
 * A single data token passed between processors in a workflow. This is distinct
 * from the Job in that it contains a single (unnamed) data reference whereas
 * the Job holds a map of arbitrarily many named data references in a bundle.
 * 
 * @author Tom Oinn
 * 
 */
public class WorkflowDataToken extends Event<WorkflowDataToken> {

	private T2Reference dataRef;
	
	/**
	 * Construct a new data token with the specified owning process, conceptual
	 * index array and data reference
	 * 
	 * @param owningProcess
	 * @param index
	 * @param dataRef
	 */
	public WorkflowDataToken(String owningProcess, int[] index, T2Reference dataRef, InvocationContext context) {
		super(owningProcess, index, context);
		this.dataRef = dataRef;
	}

	@Override
	public WorkflowDataToken popOwningProcess()
			throws ProcessIdentifierException {
		return new WorkflowDataToken(popOwner(), index, dataRef, context);
	}

	@Override
	public WorkflowDataToken pushOwningProcess(String localProcessName)
			throws ProcessIdentifierException {
		return new WorkflowDataToken(pushOwner(localProcessName), index, dataRef, context);
	}

	
	/**
	 * Return the ID of the data this event represents
	 * 
	 * @return
	 */
	public T2Reference getData() {
		return this.dataRef;
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
		sb.append("Token(" + owner + ")[");
		for (int i = 0; i < index.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(index[i] + "");
		}
		sb.append("]{");
		sb.append(dataRef.toString());
		sb.append("}");
		return sb.toString();
	}

	
}
