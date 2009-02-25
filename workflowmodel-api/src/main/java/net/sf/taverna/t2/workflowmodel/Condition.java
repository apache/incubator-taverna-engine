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
 * Defines the base interface for a condition which must be satisfied before a
 * processor can commence invocation. Conditions are expressed in terms of a
 * relationship between a controlling and a target processor where the target
 * processor may not commence invocation until all conditions for which it is a
 * target are satisfied in the context of a particular owning process
 * identifier.
 * 
 * @author Tom Oinn
 * 
 */
public interface Condition extends Annotated<Condition> {

	/**
	 * @return the Processor constrained by this condition
	 */
	public Processor getControl();

	/**
	 * @return the Processor acting as the controller for this condition
	 */
	public Processor getTarget();

	/**
	 * @param owningProcess
	 *            the context in which the condition is to be evaluated
	 * @return whether the condition is satisfied
	 */
	public boolean isSatisfied(String owningProcess);

}
