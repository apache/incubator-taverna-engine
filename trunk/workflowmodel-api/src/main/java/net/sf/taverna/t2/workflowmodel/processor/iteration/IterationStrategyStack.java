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

import java.util.List;
import java.util.Map;

/**
 * Stack of iteration strategy containers. The stacking behaviour allows for
 * staged implicit iteration where intermediate strategies are used to drill
 * into the collection structure to a certain depth with a final one used to
 * render job objects containing data at the correct depth for the process. This
 * was achieved in Taverna 1 through the combination of nested workflows and
 * 'forcing' processors which could echo and therefore force input types of the
 * workflow to a particular cardinality.
 * 
 * @author Tom Oinn
 * 
 */
public interface IterationStrategyStack {

	/**
	 * The iteration strategy stack consists of an ordered list of iteration
	 * strategies.
	 * 
	 * @return An unmodifiable copy of the list containing the iteration
	 *         strategy objects in order, with the strategy at position 0 in the
	 *         list being the one to which data is fed first.
	 */
	public List<? extends IterationStrategy> getStrategies();

	/**
	 * Calculate the depth of the iteration strategy stack as a whole given a
	 * set of named inputs and their cardinalities. This depth is the length of
	 * the index array which will be added to any output data, so the resultant
	 * output of each port in the owning processor is the depth of that port as
	 * defined by the activity plus this value.
	 * 
	 * @param inputDepths
	 * @return
	 * @throws IterationTypeMismatchException
	 * @throws MissingIterationInputException
	 */
	public int getIterationDepth(Map<String, Integer> inputDepths)
			throws IterationTypeMismatchException,
			MissingIterationInputException;
	

}
