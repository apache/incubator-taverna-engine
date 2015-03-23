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

import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.EditException;

/**
 * Abstraction of an edit acting on a Dataflow instance. Handles the check to
 * see that the Dataflow supplied is really a DataflowImpl.
 * 
 * @author David Withers
 * 
 */
public abstract class AbstractDataflowEdit extends EditSupport<Dataflow> {
	private final DataflowImpl dataflow;

	protected AbstractDataflowEdit(Dataflow dataflow) {
		if (dataflow == null)
			throw new RuntimeException(
					"Cannot construct a dataflow edit with null dataflow");
		if (!(dataflow instanceof DataflowImpl))
			throw new RuntimeException(
					"Edit cannot be applied to a Dataflow which isn't an instance of DataflowImpl");
		this.dataflow = (DataflowImpl) dataflow;
	}

	@Override
	public final Dataflow applyEdit() throws EditException {
		synchronized (dataflow) {
			doEditAction(dataflow);
		}
		return dataflow;
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param dataflow
	 *            The DataflowImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(DataflowImpl dataflow)
			throws EditException;

	@Override
	public final Dataflow getSubject() {
		return dataflow;
	}
}
