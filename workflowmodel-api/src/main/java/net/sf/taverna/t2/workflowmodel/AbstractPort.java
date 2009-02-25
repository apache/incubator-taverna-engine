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

import net.sf.taverna.t2.annotation.AbstractAnnotatedThing;

/**
 * Port definition with depth and name
 * 
 * @author Tom Oinn
 *
 */
public abstract class AbstractPort extends AbstractAnnotatedThing<Port> implements Port {

	protected String name;
	protected int depth;
	
	protected AbstractPort(String name, int depth) {
		this.name = name;
		this.depth = depth;
	}
	
	public int getDepth() {
		return this.depth;
	}

	public final String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getName() + " (" + getDepth() + ")";
	}
	
}
