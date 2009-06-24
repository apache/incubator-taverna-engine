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
package net.sf.taverna.t2.workflowmodel.processor.iteration;

import java.util.Map;

public interface IterationStrategy {

	/**
	 * The iteration strategy results in a set of job objects with a particular
	 * job index. This method returns the length of that index array when the
	 * specified input types are used. Input types are defined in terms of name
	 * and integer pairs where the name is the name of a NamedInputPortNode in
	 * the iteration strategy and the integer is the depth of the input data
	 * collection (i.e. item depth + index array length for that item which
	 * should be a constant).
	 * 
	 * @param inputDepths
	 *            map of port names to input collection depth
	 * @return the length of the index array which will be generated for each
	 *         resultant job object.
	 */
	public int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException;

	/**
	 * Return a map of port name -> desired cardinality for this iteration
	 * strategy
	 */
	public Map<String, Integer> getDesiredCardinalities();

	public TerminalNode getTerminalNode();

}
