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
package net.sf.taverna.t2.workflowmodel.impl;

import net.sf.taverna.t2.invocation.Completion;
import net.sf.taverna.t2.invocation.IterationInternalEvent;
import net.sf.taverna.t2.workflowmodel.processor.activity.Job;

/**
 * Recieves Job and Completion events and emits Jobs unaltered. Completion
 * events additionally cause registration of lists for each key in the datamap
 * of the jobs at immediate child locations in the index structure. These list
 * identifiers are sent in place of the Completion events.
 * <p>
 * State for a given process ID is purged when a final completion event is
 * received so there is no need for an explicit cache purge operation in the
 * public API (although for termination of partially complete workflows it may
 * be sensible for subclasses to provide one)
 * <p>
 * 
 * @author Tom Oinn
 */
public interface Crystalizer {

	/**
	 * Receive a Job or Completion, Jobs are emitted unaltered and cached,
	 * Completion events trigger registration of a corresponding list - this may
	 * be recursive in nature if the completion event's index implies nested
	 * lists which have not been registered.
	 */
	public void receiveEvent(IterationInternalEvent<? extends IterationInternalEvent<?>> event);

	/**
	 * This method is called when a new Job has been handled by the
	 * AbstractCrystalizer, either by direct passthrough or by list
	 * registration.
	 * 
	 */
	public void jobCreated(Job outputJob);

	/**
	 * Called whenever a completion not corresponding to a node in the cache is
	 * generated. In many cases this is an indication of an error state, the
	 * processor implementation should ensure that completion events are only
	 * sent to the crystalizer if there has been at least one data event with a
	 * lower depth on the same path.
	 */
	public void completionCreated(Completion completion);

}
