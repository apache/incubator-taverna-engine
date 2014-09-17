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

import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.EditException;

/**
 * Abstraction of an edit acting on a DataflowOutputPort instance. Handles the
 * check to see that the DataflowOutputPort supplied is really a
 * DataflowOutputPortImpl.
 * 
 * @author David Withers
 * 
 */
public abstract class AbstractDataflowOutputPortEdit extends
		EditSupport<DataflowOutputPort> {
	private DataflowOutputPortImpl dataflowOutputPort;

	protected AbstractDataflowOutputPortEdit(
			DataflowOutputPort dataflowOutputPort) {
		if (dataflowOutputPort == null)
			throw new RuntimeException(
					"Cannot construct a DataflowOutputPort edit with null DataflowOutputPort");
		if (dataflowOutputPort instanceof DataflowOutputPortImpl == false)
			throw new RuntimeException(
					"Edit cannot be applied to a DataflowOutputPort which isn't an instance of DataflowOutputPortImpl");
		this.dataflowOutputPort = (DataflowOutputPortImpl) dataflowOutputPort;
	}

	@Override
	public final DataflowOutputPort applyEdit() throws EditException {
		synchronized (dataflowOutputPort) {
			doEditAction(dataflowOutputPort);
			return this.dataflowOutputPort;
		}
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param dataflowOutputPort
	 *            The DataflowOutputPortImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(
			DataflowOutputPortImpl dataflowOutputPort) throws EditException;

	@Override
	public final DataflowOutputPort getSubject() {
		return dataflowOutputPort;
	}
}
