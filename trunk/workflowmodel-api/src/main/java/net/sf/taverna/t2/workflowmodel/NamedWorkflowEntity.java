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
 * Entities existing directly within a workflow such as Processors, Merge
 * operators and other potential future extensions exist within a naming scheme.
 * The local name of an entity is unique relative to the enclosing workflow.
 * Global names are not defined outside of the context of a given instance of a
 * workflow as the same workflow may be re-used in multiple other workflows,
 * there is therefore no single parent defined for some entities and the
 * approach of traversing the hierarchy to build a fully qualified name cannot
 * be applied. A given instance can be treated this way but this depends on
 * dataflow rather than inherent workflow structure.
 * <p>
 * All named workflow entities support the sticky note annotation type
 * 
 * @author Tom Oinn
 * 
 */
public interface NamedWorkflowEntity {

	/**
	 * Every workflow level entity has a name which is unique within the
	 * workflow in which it exists. This only applies to the immediate parent
	 * workflow, names may be duplicated in child workflows etc.
	 */
	public String getLocalName();

}
