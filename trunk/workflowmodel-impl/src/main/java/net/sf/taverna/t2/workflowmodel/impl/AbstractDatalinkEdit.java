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
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;

/**
 * Abstraction of an edit acting on a Datalink instance. Handles the check to
 * see that the Datalink supplied is really a DatalinkImpl.
 * 
 * @author David Withers
 *
 */
public abstract class AbstractDatalinkEdit implements Edit<Datalink> {

	private boolean applied = false;

	private Datalink datalink;

	protected AbstractDatalinkEdit(Datalink datalink) {
		if (datalink == null) {
			throw new RuntimeException(
					"Cannot construct a datalink edit with null datalink");
		}
		this.datalink = datalink;
	}

	public final Datalink doEdit() throws EditException {
		if (applied) {
			throw new EditException("Edit has already been applied!");
		}
		if (datalink instanceof DatalinkImpl == false) {
			throw new EditException(
					"Edit cannot be applied to a Datalink which isn't an instance of DatalinkImpl");
		}
		DatalinkImpl datalinkImpl = (DatalinkImpl) datalink;
		try {
			synchronized (datalinkImpl) {
				doEditAction(datalinkImpl);
				applied = true;
				return this.datalink;
			}
		} catch (EditException ee) {
			applied = false;
			throw ee;
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

	/**
	 * Undo any edit effects here
	 */
	protected abstract void undoEditAction(DatalinkImpl datalink);

	public final Datalink getSubject() {
		return datalink;
	}

	public final boolean isApplied() {
		return this.applied;
	}

	public final void undo() {
		if (!applied) {
			throw new RuntimeException(
					"Attempt to undo edit that was never applied");
		}
		DatalinkImpl datalinkImpl = (DatalinkImpl) datalink;
		synchronized (datalinkImpl) {
			undoEditAction(datalinkImpl);
			applied = false;
		}

	}
}
