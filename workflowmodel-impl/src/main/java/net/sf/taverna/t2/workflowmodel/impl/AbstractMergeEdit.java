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

import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Merge;

public abstract class AbstractMergeEdit implements Edit<Merge>{
	
	Merge merge;
	boolean applied=false;
	
	public AbstractMergeEdit(Merge merge) {
		if (merge==null) throw new RuntimeException("Cannot construct a merge edit with a null merge");
		this.merge=merge;
	}

	@Override
	public final Merge doEdit() throws EditException {
		if (applied) throw new EditException("Edit has already been applied!");
		if (!(merge instanceof MergeImpl)) throw new EditException("Merge must be an instanceof MergeImpl");
		MergeImpl mergeImpl = (MergeImpl)merge;
		try {
			synchronized (mergeImpl) {
				doEditAction(mergeImpl);
				applied = true;
			}
		} catch (EditException ee) {
			applied = false;
			throw ee;
		}
		
		return this.merge;
	}

	protected abstract void doEditAction(MergeImpl mergeImpl) throws EditException;
	
	@Override
	public final Object getSubject() {
		return merge;
	}

	@Override
	public final boolean isApplied() {
		return applied;
	}

	@Override
	public final void undo() {
		if (!applied) {
			throw new RuntimeException(
					"Attempt to undo edit that was never applied");
		}
		MergeImpl mergeImpl = (MergeImpl) merge;
		synchronized (mergeImpl) {
			throw new UnsupportedOperationException(
					"undo not supported by this interface in Taverna 3");
		}
	}

}
