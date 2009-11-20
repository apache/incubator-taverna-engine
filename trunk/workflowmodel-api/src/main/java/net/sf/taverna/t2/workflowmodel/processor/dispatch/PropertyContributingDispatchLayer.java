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
package net.sf.taverna.t2.workflowmodel.processor.dispatch;

/**
 * Used by dispatch layers which can contribute property information to their
 * parent processor instance. This is distinct from dispatch layers which modify
 * the process identifier and therefore create their own nodes in the monitor
 * tree, although there's no reason a layer can't perform both functions. For
 * example, the fault tolerance layers create their own state subtrees for each
 * failure recovery but could also contribute aggregate fault information to the
 * parent processor's property set.
 * 
 * @author Tom Oinn
 * 
 * @param <ConfigType>
 *            configuration type for the dispatch layer
 */
public interface PropertyContributingDispatchLayer<ConfigType> extends
		DispatchLayer<ConfigType> {

	/**
	 * Inject properties for the specified owning process into the parent
	 * dispatch stack. At some point prior to this call being made the
	 * setDispatchStack will have been called, implementations of this method
	 * need to use this DispatchStack reference to push properties in with the
	 * specified key.
	 * <p>
	 * Threading - this thread must not fork, do all the work in this method in
	 * the thread you're given by the caller. This is because implementations
	 * may assume that they can collect properties from the dispatch stack
	 * implementation (which will expose them through a private access method to
	 * prevent arbitrary access to layer properties) once this call has
	 * returned.
	 * <p>
	 * There is no guarantee that the layer will have seen an event with the
	 * specified process, and in fact it's unlikely to in the general case as
	 * any layers above it are free to modify the process identifier of tokens
	 * as they go. Remember that this method is for aggregating properties into
	 * the top level (processor) view so you may need to implement the property
	 * getters such that they check prefixes of identifiers rather than
	 * equality.
	 * 
	 * @param owningProcess
	 */
	void injectPropertiesFor(String owningProcess);

}
