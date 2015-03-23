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

package org.apache.taverna.workflowmodel.processor.dispatch.impl;

import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.processor.dispatch.DispatchStack;

/**
 * Abstraction of an edit acting on a DispatchLayer instance. Handles the check to
 * see that the DispatchLayer supplied is really a DispatchLayerImpl.
 * 
 * @author Tom Oinn
 * 
 */
public abstract class AbstractDispatchLayerEdit implements Edit<DispatchStack> {
	private boolean applied = false;
	private DispatchStackImpl stack;

	protected AbstractDispatchLayerEdit(DispatchStack s) {
		if (s == null)
			throw new RuntimeException(
					"Cannot construct a dispatch stack edit with null dispatch stack");
		if (!(s instanceof DispatchStackImpl))
			throw new RuntimeException(
					"Edit cannot be applied to a DispatchStack which isn't "
					+ "an instance of DispatchStackImpl");
		stack = (DispatchStackImpl) s;
	}

	@Override
	public final DispatchStack doEdit() throws EditException {
		if (applied)
			throw new EditException("Edit has already been applied!");
		synchronized (stack) {
			doEditAction(stack);
			applied = true;
			return stack;
		}
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param processor
	 *            The ProcessorImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(DispatchStackImpl stack)
			throws EditException;

	/**
	 * Undo any edit effects here
	 */
	protected void undoEditAction(DispatchStackImpl stack) {
		throw new RuntimeException("undo not supported in the t2 model in T3");
	}

	@Override
	public final DispatchStack getSubject() {
		return stack;
	}

	@Override
	public final boolean isApplied() {
		return this.applied;
	}

	@Override
	public final void undo() {
		if (!applied)
			throw new RuntimeException(
					"Attempt to undo edit that was never applied");
		synchronized (stack) {
			undoEditAction(stack);
			applied = false;
		}
	}

}
