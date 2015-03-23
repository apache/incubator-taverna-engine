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
 * The workflow object model exposed by this API is read only. Properties of the
 * model can only be changed through implementations of this interface, this
 * ensures a consistant approach to grouped edits (transactions) and undo / redo
 * support within the UI. It also potentially allows for capture of editing
 * provenance where a workflow is repurposed or created from an aggregate of
 * several others.
 * 
 * @author Tom Oinn
 */
public interface Edit<TargetType> {
	/**
	 * Perform the edit
	 * 
	 * @throws EditException
	 *             if the edit fails. If an edit throws EditException it should
	 *             try to ensure the subject is unaltered. Where this is
	 *             impossible consider breaking edits down into a compound edit.
	 */
	TargetType doEdit() throws EditException;

	/**
	 * Undo the edit, reverting the subject to the state it was in prior to the
	 * edit
	 */
	@Deprecated
	void undo();

	/**
	 * Return the object to which this edit applies
	 * 
	 * @return
	 */
	Object getSubject();

	/**
	 * Has the edit been applied yet?
	 * 
	 * @return true if and only if the edit has been successfully applied to the
	 *         subject
	 */
	boolean isApplied();
}
