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
package net.sf.taverna.t2.annotation;

/**
 * Possible relationships between entities in a hierarchical context. This is
 * used as a property of the HierarchyTraversal annotation on members which
 * traverse a conceptual object hierarchy such as a parent-child containment
 * relationship. As an example the getProcessors() method in Dataflow is
 * annotated with <code>&amp;HierarchyRole(role=CHILD)</code> to indicate that
 * it accesses child members of the workflow model containment hierarchy.
 * 
 * @author Tom Oinn
 * 
 */
public enum HierarchyRole {

	CHILD,

	PARENT;

}
