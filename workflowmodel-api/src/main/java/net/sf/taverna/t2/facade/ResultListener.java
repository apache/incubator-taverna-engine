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
package net.sf.taverna.t2.facade;

import net.sf.taverna.t2.invocation.WorkflowDataToken;

/**
 * Implement and use with the WorkflowInstanceFacade to listen for data
 * production events from the underlying workflow instance
 * 
 * @author Tom Oinn
 * 
 */
public interface ResultListener {

	/**
	 * Called when a new result token is produced by the workflow instance.
	 * 
	 * @param token
	 *            the WorkflowDataToken containing the result.
	 * @param portName
	 *            The name of the output port on the workflow from which this
	 *            token is produced, this now folds in the owning process which
	 *            was part of the signature for this method
	 */
	public void resultTokenProduced(WorkflowDataToken token, String portName);

}
