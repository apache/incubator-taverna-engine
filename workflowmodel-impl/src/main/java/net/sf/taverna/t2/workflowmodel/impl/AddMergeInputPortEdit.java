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

import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Merge;
import net.sf.taverna.t2.workflowmodel.MergeInputPort;

/**
 * Adds a merge input port to a merge.
 * 
 * @author David Withers
 */
public class AddMergeInputPortEdit extends AbstractMergeEdit {

	private MergeInputPort mergeInputPort;
	
	public AddMergeInputPortEdit(Merge merge, MergeInputPort mergeInputPort) {
		super(merge);
		this.mergeInputPort = mergeInputPort;
	}

	@Override
	protected void doEditAction(MergeImpl mergeImpl) throws EditException {
		if (mergeInputPort instanceof MergeInputPortImpl) {
			mergeImpl.addInputPort((MergeInputPortImpl) mergeInputPort);
		}
		else {
			throw new EditException("The MergeInputPort is of the wrong implmentation, it should be of type MergeInputPortImpl");
		}
	}

	@Override
	protected void undoEditAction(MergeImpl mergeImpl) {
		if (mergeInputPort instanceof MergeInputPortImpl) {
			mergeImpl.removeInputPort((MergeInputPortImpl) mergeInputPort);
		}
	}

}
