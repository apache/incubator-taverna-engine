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
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;
import net.sf.taverna.t2.workflowmodel.EventHandlingInputPort;

/**
 * Connect a datalink to its source and sink.
 * 
 * @author David Withers
 * 
 */
public class ConnectDatalinkEdit extends AbstractDatalinkEdit {

	public ConnectDatalinkEdit(Datalink datalink) {
		super(datalink);
	}

	@Override
	protected void doEditAction(DatalinkImpl datalink) throws EditException {
		EventForwardingOutputPort source = datalink.getSource();
		EventHandlingInputPort sink = datalink.getSink();
		if (source instanceof BasicEventForwardingOutputPort) {
			((BasicEventForwardingOutputPort) source).addOutgoingLink(datalink);
		}
		if (sink instanceof AbstractEventHandlingInputPort) {
			((AbstractEventHandlingInputPort) sink).setIncomingLink(datalink);
		}
	}

	@Override
	protected void undoEditAction(DatalinkImpl datalink) {
		EventForwardingOutputPort source = datalink.getSource();
		EventHandlingInputPort sink = datalink.getSink();
		if (source instanceof BasicEventForwardingOutputPort) {
			((BasicEventForwardingOutputPort) source).removeOutgoingLink(datalink);
		}
		if (sink instanceof AbstractEventHandlingInputPort) {
			((AbstractEventHandlingInputPort) sink).setIncomingLink(null);
		}
	}

}
