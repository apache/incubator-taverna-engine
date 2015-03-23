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

import org.apache.taverna.workflowmodel.DataflowInputPort;
import org.apache.taverna.workflowmodel.EditException;

/**
 * Abstraction of an edit acting on a DataflowInputPort instance. Handles the check to
 * see that the DataflowInputPort supplied is really a DataflowInputPortImpl.
 * 
 * @author David Withers
 *
 */
public abstract class AbstractDataflowInputPortEdit extends EditSupport<DataflowInputPort> {
	private final DataflowInputPortImpl dataflowInputPort;

	protected AbstractDataflowInputPortEdit(DataflowInputPort dataflowInputPort) {
		if (dataflowInputPort == null)
			throw new RuntimeException(
					"Cannot construct a DataflowInputPort edit with null DataflowInputPort");
		if (!(dataflowInputPort instanceof DataflowInputPortImpl))
			throw new RuntimeException(
					"Edit cannot be applied to a DataflowInputPort which isn't an instance of DataflowInputPortImpl");
		this.dataflowInputPort = (DataflowInputPortImpl) dataflowInputPort;
	}

	@Override
	public final DataflowInputPort applyEdit() throws EditException {
		synchronized (dataflowInputPort) {
			doEditAction(dataflowInputPort);
		}
		return dataflowInputPort;
	}

	/**
	 * Do the actual edit here
	 * 
	 * @param dataflowInputPort
	 *            The DataflowInputPortImpl to which the edit applies
	 * @throws EditException
	 */
	protected abstract void doEditAction(DataflowInputPortImpl dataflowInputPort)
			throws EditException;

	@Override
	public final DataflowInputPort getSubject() {
		return dataflowInputPort;
	}
}
