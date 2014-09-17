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
import net.sf.taverna.t2.workflowmodel.EditException;

/**
 * Abstraction of an edit acting on a DataflowInputPort instance. Handles the check to
 * see that the DataflowInputPort supplied is really a DataflowInputPortImpl.
 * 
 * @author David Withers
 *
 */
public abstract class AbstractDataflowInputPortEdit extends EditSupport<DataflowInputPort> {
	private DataflowInputPortImpl dataflowInputPort;

	protected AbstractDataflowInputPortEdit(DataflowInputPort dataflowInputPort) {
		if (dataflowInputPort == null)
			throw new RuntimeException(
					"Cannot construct a DataflowInputPort edit with null DataflowInputPort");
		if (dataflowInputPort instanceof DataflowInputPortImpl == false)
			throw new RuntimeException(
					"Edit cannot be applied to a DataflowInputPort which isn't an instance of DataflowInputPortImpl");
		this.dataflowInputPort = (DataflowInputPortImpl) dataflowInputPort;
	}

	@Override
	public final DataflowInputPort applyEdit() throws EditException {
		synchronized (dataflowInputPort) {
			doEditAction(dataflowInputPort);
			return this.dataflowInputPort;
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

	@Override
	public final DataflowInputPort getSubject() {
		return dataflowInputPort;
	}
}
