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

import java.util.List;
import java.util.Map;

/**
 * Contains a validation report from a dataflow validation check. Processors are
 * classified as failed, unsatisfied or valid depending on whether they directly
 * fail type validation, cannot be checked due to unsatisfied incoming links or
 * pass respectively.
 * 
 * @author Tom Oinn
 * 
 */
public interface DataflowValidationReport {

	/**
	 * Overal validity - if the workflow is valid it can be run, otherwise there
	 * are problems somewhere and a facade can't be created from it.
	 * 
	 * @return whether the workflow is valid (true) or not (false)
	 */
	public boolean isValid();

	/**
	 * The workflow will be marked as invalid if there are entities with
	 * unlinked input ports or where there are cycles causing the type checking
	 * algorithm to give up. In these cases offending processors or any
	 * ancestors that are affected as a knock on effect will be returned in this
	 * list.
	 * 
	 * @return list of TokenProcessingEntity instances within the Dataflow for
	 *         which it is impossible to determine validity due to missing
	 *         inputs or cyclic dependencies
	 */
	public List<? extends TokenProcessingEntity> getUnsatisfiedEntities();

	/**
	 * The workflow will be marked as invalid if any entity fails to type check.
	 * 
	 * @return list of TokenProcessingEntity instances within the Dataflow which
	 *         caused explicit type check failures
	 */
	public List<? extends TokenProcessingEntity> getFailedEntities();

	/**
	 * The workflow will be marked as invalid if any of the dataflow output
	 * ports can't be typed based on incoming links. This happens if the port
	 * isn't linked (a common enough issue for new users in previous releases of
	 * Taverna) or if the internal port is linked but the entity it links to
	 * isn't validated.
	 * 
	 * @return a list of DataflowOutputPort implementations which are not typed
	 *         correctly. These will have output depth of -1 indicating an
	 *         unknown depth, they may or may not have a granular depth set but
	 *         if the overall depth is -1 this isn't important as the thing
	 *         won't run anyway.
	 */
	public List<? extends DataflowOutputPort> getUnresolvedOutputs();
	
	
	/**
	 * An entity will be marked invalid if it depends on a nested dataflow
	 * which itself is invalid. If this is the case the entity will be
	 * be present both in {@link #getFailedEntities()} and can be used as
	 * a key with this method to get the DataflowValidationReport explaining
	 * how the nested dataflow failed.
	 * 
	 */
	public Map<TokenProcessingEntity, DataflowValidationReport> getInvalidDataflows();

}
