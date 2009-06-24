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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to getFoo methods to indicate that the returned type is related to
 * the annotated type by some hierarchical relationship, either parent or child.
 * This can then be used by annotation tools to determine the structure of an
 * object under annotation in order to find any child objects without
 * accidentally traversing outside of the bound of the object to be annotated.
 * <p>
 * As annotations are not inherited any annotation tool should traverse up the
 * type structure of an object under annotation to determine the possible
 * child-parent relationships from superclasses and implemented interfaces.
 * <p>
 * There is no guarantee that the return types from annotated members implement
 * Annotated, in these cases traversal should still be followed to cover cases
 * where a grandchild of an object is annotatable even though all children are
 * not.
 * <p>
 * This should only be applied to method with no arguments, if this is not the
 * case an annotation tool is free to not follow such methods (as it has no way
 * to determine what should be applied as arguments)
 * 
 * @author Tom Oinn
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface HierarchyTraversal {

	/**
	 * The role the return type of the annotated method plays in the named
	 * hierarchy relative to the containing type.
	 * 
	 * @return role in hierarchy at corresponding index in the Hierarchies
	 *         property, currently either CHILD or PARENT
	 */
	HierarchyRole[] role();

	/**
	 * It is possible for multiple orthogonal containment hierarchies to exist,
	 * to allow for this the hierarchies are named using this field.
	 * 
	 * @return name of the hierarchy to which this relationship applies
	 */
	String[] hierarchies();

}
