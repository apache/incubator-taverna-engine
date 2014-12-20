/*******************************************************************************
 * Copyright (C) 2007-2008 The University of Manchester   
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
package net.sf.taverna.t2.workflowmodel.impl;

import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.EditException;

/**
 * Abstraction of an edit acting on a Dataflow instance. Handles the check to
 * see that the Dataflow supplied is really a DataflowImpl.
 * 
 * @author David Withers
 * 
 */
public abstract class AbstractDataflowEdit extends EditSupport<Dataflow> {
	private final DataflowImpl dataflow;

	protected AbstractDataflowEdit(Dataflow dataflow) {
		if (dataflow == null)
			throw new RuntimeException(
					"Cannot construct a dataflow edit with null dataflow");
		if (!(dataflow instanceof DataflowImpl))
			throw new RuntimeException(
					"Edit cannot be applied to a Dataflow which isn't an instance of DataflowImpl");
		this.dataflow = (DataflowImpl) dataflow;
	}

	@Override
	public final Dataflow applyEdit() throws EditException {
		synchronized (dataflow) {
			doEditAction(dataflow);
		}
		return dataflow;
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param dataflow
	 *            The DataflowImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(DataflowImpl dataflow)
			throws EditException;

	@Override
	public final Dataflow getSubject() {
		return dataflow;
	}
}
