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
 * Change the granular depth of the specified DataflowInputPort.
 * 
 * @author David Withers
 * 
 */
public class ChangeDataflowInputPortGranularDepthEdit extends AbstractDataflowInputPortEdit {

	private int newGranularDepth;

	private int oldGranularDepth;

	public ChangeDataflowInputPortGranularDepthEdit(DataflowInputPort dataflowInputPort, int newGranularDepth) {
		super(dataflowInputPort);
		this.newGranularDepth = newGranularDepth;
	}

	@Override
	protected void doEditAction(DataflowInputPortImpl dataflowInputPort) throws EditException {
		oldGranularDepth = dataflowInputPort.getGranularInputDepth();
		dataflowInputPort.setGranularDepth(newGranularDepth);
	}

	@Override
	protected void undoEditAction(DataflowInputPortImpl dataflowInputPort) {
		dataflowInputPort.setGranularDepth(oldGranularDepth);
	}

}
