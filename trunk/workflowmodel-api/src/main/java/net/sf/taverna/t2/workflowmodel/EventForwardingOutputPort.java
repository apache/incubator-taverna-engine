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
package net.sf.taverna.t2.workflowmodel;

import java.util.Set;

/**
 * An extension of OutputPort defining a set of target EventReceivingInputPorts
 * to which internally generated events will be relayed. This is the interface
 * used by output ports on a workflow entity with internal logic generating or
 * relaying events.
 * 
 * @author Tom Oinn
 * 
 */
public interface EventForwardingOutputPort extends OutputPort {

	/**
	 * The set of EventHandlingInputPort objects which act as targets for events
	 * produced from this OutputPort
	 * 
	
	public Set<EventHandlingInputPort> getTargets();
*/

	/**
	 * The set of datalinks for which this output port is the source of events
	 */
	public Set<? extends Datalink> getOutgoingLinks();
}
