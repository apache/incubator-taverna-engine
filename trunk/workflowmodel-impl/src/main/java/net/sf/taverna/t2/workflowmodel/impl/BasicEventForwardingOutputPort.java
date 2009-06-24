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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sf.taverna.t2.invocation.WorkflowDataToken;
import net.sf.taverna.t2.workflowmodel.AbstractOutputPort;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.EventForwardingOutputPort;

/**
 * Extension of AbstractOutputPort implementing EventForwardingOutputPort
 * 
 * @author Tom Oinn
 * 
 */
public class BasicEventForwardingOutputPort extends AbstractOutputPort
		implements EventForwardingOutputPort {

	protected Set<DatalinkImpl> outgoingLinks;

	/**
	 * Construct a new abstract output port with event forwarding capability
	 * 
	 * @param portName
	 * @param portDepth
	 * @param granularDepth
	 */
	public BasicEventForwardingOutputPort(String portName, int portDepth,
			int granularDepth) {
		super(portName, portDepth, granularDepth);
		this.outgoingLinks = new HashSet<DatalinkImpl>();
	}

	/**
	 * Implements EventForwardingOutputPort
	 */
	public final Set<? extends Datalink> getOutgoingLinks() {
		return Collections.unmodifiableSet(this.outgoingLinks);
	}

	/**
	 * Forward the specified event to all targets
	 * 
	 * @param e
	 */
	public void sendEvent(WorkflowDataToken e) {
		for (Datalink link : outgoingLinks) {
			link.getSink().receiveEvent(e);
		}
	}

	protected void addOutgoingLink(DatalinkImpl link) {
		if (outgoingLinks.contains(link) == false) {
			outgoingLinks.add(link);
		}
	}

	protected void removeOutgoingLink(Datalink link) {
		outgoingLinks.remove(link);
	}

	protected void setDepth(int depth) {
		this.depth = depth;
	}
	
	protected void setGranularDepth(int granularDepth) {
		this.granularDepth = granularDepth;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

}
