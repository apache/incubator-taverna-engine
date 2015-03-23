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
