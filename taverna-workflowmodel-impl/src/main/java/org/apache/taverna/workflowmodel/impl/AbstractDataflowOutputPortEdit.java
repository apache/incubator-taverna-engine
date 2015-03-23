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

import org.apache.taverna.workflowmodel.DataflowOutputPort;
import org.apache.taverna.workflowmodel.EditException;

/**
 * Abstraction of an edit acting on a DataflowOutputPort instance. Handles the
 * check to see that the DataflowOutputPort supplied is really a
 * DataflowOutputPortImpl.
 * 
 * @author David Withers
 */
public abstract class AbstractDataflowOutputPortEdit extends
		EditSupport<DataflowOutputPort> {
	private final DataflowOutputPortImpl dataflowOutputPort;

	protected AbstractDataflowOutputPortEdit(
			DataflowOutputPort dataflowOutputPort) {
		if (dataflowOutputPort == null)
			throw new RuntimeException(
					"Cannot construct a DataflowOutputPort edit with null DataflowOutputPort");
		if (!(dataflowOutputPort instanceof DataflowOutputPortImpl))
			throw new RuntimeException(
					"Edit cannot be applied to a DataflowOutputPort which isn't an instance of DataflowOutputPortImpl");
		this.dataflowOutputPort = (DataflowOutputPortImpl) dataflowOutputPort;
	}

	@Override
	public final DataflowOutputPort applyEdit() throws EditException {
		synchronized (dataflowOutputPort) {
			doEditAction(dataflowOutputPort);
		}
		return dataflowOutputPort;
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param dataflowOutputPort
	 *            The DataflowOutputPortImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(
			DataflowOutputPortImpl dataflowOutputPort) throws EditException;

	@Override
	public final DataflowOutputPort getSubject() {
		return dataflowOutputPort;
	}
}
