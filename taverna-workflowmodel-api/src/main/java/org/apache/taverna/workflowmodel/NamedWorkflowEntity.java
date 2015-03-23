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

package org.apache.taverna.workflowmodel;

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
 */
public interface NamedWorkflowEntity extends WorkflowItem {
	/**
	 * Every workflow level entity has a name which is unique within the
	 * workflow in which it exists. This only applies to the immediate parent
	 * workflow, names may be duplicated in child workflows etc.
	 */
	String getLocalName();
}
