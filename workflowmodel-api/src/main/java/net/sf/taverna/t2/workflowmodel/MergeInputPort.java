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
package net.sf.taverna.t2.workflowmodel;

import net.sf.taverna.t2.invocation.WorkflowDataToken;

/**
 * Input port on a Merge object
 * 
 * @author Tom Oinn
 * 
 */
public interface MergeInputPort extends EventHandlingInputPort, MergePort {

	/**
	 * Receive an arbitrary workflow event. The index of this port relative to
	 * its parent Merge object is prepended to the event index and the event
	 * forwarded through the Merge output port to any targets.
	 * <p>
	 * If this is a workflow data token and the first such received under a
	 * given owning process ID the implementing method also must also store the
	 * cardinality, i.e. length of index array + depth of token. Subsequent
	 * events are matched to this, if they have unequal cardinality the parent
	 * Merge operation will throw a WorkflowStructureException as the merge
	 * would result in a collection which violated the constraints defined by
	 * the Taverna 2 data model.
	 * 
	 * @param e
	 *            arbitrary workflow event, will be forwarded unchanged other
	 *            than an alteration of the index array by prefixing the index
	 *            of this input port relative to the parent Merge object
	 */
	public void receiveEvent(WorkflowDataToken t);

}
