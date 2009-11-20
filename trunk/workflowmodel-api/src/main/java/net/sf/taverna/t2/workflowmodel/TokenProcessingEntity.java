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

import static net.sf.taverna.t2.annotation.HierarchyRole.CHILD;

import java.util.List;

import net.sf.taverna.t2.annotation.HierarchyTraversal;
import net.sf.taverna.t2.workflowmodel.processor.iteration.IterationTypeMismatchException;

/**
 * Superinterface for all classes within the workflow model which consume and
 * emit workflow data tokens.
 * 
 * @author Tom Oinn
 * 
 */
public interface TokenProcessingEntity extends NamedWorkflowEntity {

	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	public List<? extends EventHandlingInputPort> getInputPorts();

	@HierarchyTraversal(hierarchies = { "workflowStructure" }, role = { CHILD })
	public List<? extends EventForwardingOutputPort> getOutputPorts();

	/**
	 * Run a collection level based type check on the token processing entity
	 * 
	 * @return true if the typecheck was successful or false if the check failed
	 *         because there were preconditions missing such as unsatisfied
	 *         input types
	 * @throws IterationTypeMismatchException
	 *             if the typing occurred but didn't match because of an
	 *             iteration mismatch
	 * @throws InvalidDataflowException 
	 * 			 	if the entity depended on a dataflow that was not valid
	 */
	public boolean doTypeCheck() throws IterationTypeMismatchException, InvalidDataflowException;
	
}
