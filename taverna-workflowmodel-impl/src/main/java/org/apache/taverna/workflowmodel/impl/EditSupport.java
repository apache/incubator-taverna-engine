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

package org.apache.taverna.workflowmodel.impl;

import org.apache.taverna.workflowmodel.Edit;
import org.apache.taverna.workflowmodel.EditException;

abstract class EditSupport<T> implements Edit<T> {
	protected boolean applied;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final T doEdit() throws EditException {
		if (applied)
			throw new EditException("Edit has already been applied!");
		T result = applyEdit();
		applied = true;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isApplied() {
		return applied;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void undo() {
		if (!applied)
			throw new RuntimeException(
					"Attempt to undo edit that was never applied");
		applyUndo();
	}

	protected abstract T applyEdit() throws EditException;

	protected void applyUndo() {
		throw new UnsupportedOperationException(
				"undo not supported by this interface in Taverna 3");
	}
}
