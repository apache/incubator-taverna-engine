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

import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.EditException;

/**
 * Abstraction of an edit acting on a Datalink instance. Handles the check to
 * see that the Datalink supplied is really a DatalinkImpl.
 * 
 * @author David Withers
 * 
 */
public abstract class AbstractDatalinkEdit extends EditSupport<Datalink> {
	private DatalinkImpl datalink;

	protected AbstractDatalinkEdit(Datalink datalink) {
		if (datalink == null)
			throw new RuntimeException(
					"Cannot construct a datalink edit with null datalink");
		if (datalink instanceof DatalinkImpl == false)
			throw new RuntimeException(
					"Edit cannot be applied to a Datalink which isn't an instance of DatalinkImpl");
		this.datalink = (DatalinkImpl) datalink;
	}

	@Override
	public final Datalink applyEdit() throws EditException {
		synchronized (datalink) {
			doEditAction(datalink);
			return this.datalink;
		}
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param datalink
	 *            The DatalinkImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(DatalinkImpl datalink)
			throws EditException;

	@Override
	public final Datalink getSubject() {
		return datalink;
	}
}
