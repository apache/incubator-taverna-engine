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

/**
 * A filtering input port is one capable of filtering events to only pass
 * through data events at a certain depth. Other events are either ignored (in
 * the case of finer granularity) or converted to completion events (for
 * coarser). Where the filter depth and the port depth are distinct this port
 * type will filter on the filter depth then drill into the data to get down to
 * the port depth. Filter depth must always be equal to or greater than port
 * depth.
 * <p>
 * This is used as the interface for Processor input ports.
 * <p>
 * A condition to use this type is that the stream of events for a given process
 * ID must terminate with a top level (i.e. zero length index array) token. This
 * can be accomplished by use of the crystalizer (as found on the output of a
 * Processor instance) or some other mechanism but is required. Similarly it is
 * assumed that all intermediate collections are emited in the correct sequence,
 * if this is not the case the filtering may not function correctly.
 * 
 * @author Tom Oinn
 * 
 */
public interface FilteringInputPort extends EventHandlingInputPort {

	/**
	 * Set the depth at which to filter events. Events at a lower depth than
	 * this are ignored completely, those at exactly this depth are passed
	 * through intact and those above are converted to completion events.
	 * 
	 * @param filterDepth
	 */
	public int getFilterDepth();

}
