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
package net.sf.taverna.t2.workflowmodel.impl;

import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;

/**
 * Abstraction of an edit acting on a DataflowInputPort instance. Handles the check to
 * see that the DataflowInputPort supplied is really a DataflowInputPortImpl.
 * 
 * @author David Withers
 *
 */
public abstract class AbstractDataflowInputPortEdit implements Edit<DataflowInputPort> {

	private boolean applied = false;

	private DataflowInputPort dataflowInputPort;

	protected AbstractDataflowInputPortEdit(DataflowInputPort dataflowInputPort) {
		if (dataflowInputPort == null) {
			throw new RuntimeException(
					"Cannot construct a DataflowInputPort edit with null DataflowInputPort");
		}
		this.dataflowInputPort = dataflowInputPort;
	}

	public final DataflowInputPort doEdit() throws EditException {
		if (applied) {
			throw new EditException("Edit has already been applied!");
		}
		if (dataflowInputPort instanceof DataflowInputPortImpl == false) {
			throw new EditException(
					"Edit cannot be applied to a DataflowInputPort which isn't an instance of DataflowInputPortImpl");
		}
		DataflowInputPortImpl dataflowInputPortImpl = (DataflowInputPortImpl) dataflowInputPort;
		try {
			synchronized (dataflowInputPortImpl) {
				doEditAction(dataflowInputPortImpl);
				applied = true;
				return this.dataflowInputPort;
			}
		} catch (EditException ee) {
			applied = false;
			throw ee;
		}
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param dataflowInputPort
	 *            The DataflowInputPortImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(DataflowInputPortImpl dataflowInputPort)
			throws EditException;

	/**
	 * Undo any edit effects here
	 */
	protected abstract void undoEditAction(DataflowInputPortImpl dataflowInputPort);

	public final DataflowInputPort getSubject() {
		return dataflowInputPort;
	}

	public final boolean isApplied() {
		return this.applied;
	}

	public final void undo() {
		if (!applied) {
			throw new RuntimeException(
					"Attempt to undo edit that was never applied");
		}
		DataflowInputPortImpl dataflowInputPortImpl = (DataflowInputPortImpl) dataflowInputPort;
		synchronized (dataflowInputPortImpl) {
			undoEditAction(dataflowInputPortImpl);
			applied = false;
		}

	}
}
