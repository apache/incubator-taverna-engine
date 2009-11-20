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
 * An input port on a Dataflow contains a nested output port within it. This
 * internal output port is used when connecting an edge from the workflow input
 * to a processor or workflow output (which in turn has a nested input port).
 * The workflow ports are therefore effectively pairs of ports with a relay
 * mechanism between the external and internal in the case of the dataflow
 * input.
 * 
 * @author Tom Oinn
 * 
 */
public interface DataflowInputPort extends EventHandlingInputPort, DataflowPort {

	/**
	 * Return the internal output port. Output ports have a granular depth
	 * property denoting the finest grained output token they can possibly
	 * produce, this is used to configure downstream filtering input ports. In
	 * this case the finest depth item is determined by the input to the
	 * workflow port and must be explicitly set.
	 * 
	 * @return the internal output port
	 */
	public EventForwardingOutputPort getInternalOutputPort();
	
	/**
	 * Define the finest grained item that will be sent to this input port. As
	 * all data are relayed through to the internal output port this is used to
	 * denote output port granularity as well as to configure any downstream
	 * connected filtering input ports.
	 */
	public int getGranularInputDepth();
	
	
}
