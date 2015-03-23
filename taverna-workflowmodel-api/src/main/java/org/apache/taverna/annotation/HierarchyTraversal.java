/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.annotation;

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
