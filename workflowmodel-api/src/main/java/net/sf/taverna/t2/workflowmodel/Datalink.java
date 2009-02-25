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

import net.sf.taverna.t2.annotation.Annotated;

/**
 * A single point to point data link from an instance of
 * EventForwardingOutputPort to an instance of EventHandlingInputPort
 * 
 * @author Tom Oinn
 * 
 */
public interface Datalink extends Annotated<Datalink> {

	/**
	 * Get the sink for events flowing through this link
	 * 
	 * @return input port receiving events
	 */
	public EventHandlingInputPort getSink();

	/**
	 * Get the source for events flowing through this link
	 * 
	 * @return output port generating events
	 */
	public EventForwardingOutputPort getSource();

	/**
	 * Each datalink has a resolved depth, this being the constant sum of index
	 * array length + item depth for all tokens exchanged along this link. Where
	 * no iteration or data streaming is occuring this will evaluate to the
	 * output port depth the link is from (as is always the case with the
	 * internal output ports in dataflow inputs)
	 * 
	 * @return
	 */
	public int getResolvedDepth();

}
